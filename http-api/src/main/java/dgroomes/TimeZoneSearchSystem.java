package dgroomes;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.facet.FacetResult;
import org.apache.lucene.facet.Facets;
import org.apache.lucene.facet.FacetsCollector;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.taxonomy.FastTaxonomyFacetCounts;
import org.apache.lucene.facet.taxonomy.TaxonomyReader;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyReader;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyWriter;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * This class encapsulates a "search system".
 * <p>
 * It exposes a search API via a public method, and it encapsulates the internals of the data-under-search (the Lucene index).
 * The data-under-search is toy data. It's a set of time zones names.
 */
public class TimeZoneSearchSystem {

  private static final Logger log = LoggerFactory.getLogger(TimeZoneSearchSystem.class);

  private final Directory indexDir;
  private final Directory taxonomyDir;
  private final Analyzer analyzer;
  private final FacetsConfig facetsConfig = new FacetsConfig();

  public TimeZoneSearchSystem(Directory indexDir, Directory taxonomyDir, Analyzer analyzer) {
    this.indexDir = indexDir;
    this.taxonomyDir = taxonomyDir;
    this.analyzer = analyzer;
  }

  /**
   * Initialize the search system. This will execute the indexing process and the method returns when indexing is
   * complete.
   * <p>
   * It would be nice to encapsulate the {@link Directory} and {@link Analyzer} instances as implementation details
   * inside this initialization method, but I would prefer to have control over the lifecycle of these objects by
   * having the calling code inject them, and the calling code also close the objects using a try-with-resources block.
   * It's a trade-off.
   */
  public static TimeZoneSearchSystem init(Directory indexDir, Analyzer analyzer, Directory taxonomyDir) {
    TimeZoneSearchSystem timeZoneSearchSystem = new TimeZoneSearchSystem(indexDir, taxonomyDir, analyzer);
    timeZoneSearchSystem.indexData();
    return timeZoneSearchSystem;
  }

  public record SearchResults(List<Document> hits, FacetResult facetResult) {}

  /**
   * Search for the given keyword.
   * <p>
   * IN PROGRESS This will do both a regular search and a facet search. Actually, I'm confused about this because the
   * Lucene API makes you do two separate searches? Wouldn't you want to just "do a search" and get both the regular
   * results (the "hits") AND the facet results? That way, it's one search and then the user has the option to narrow
   * down the results using the facets if they're satisfied with the top hits.
   */
  public SearchResults search(String keyword) {
    IndexReader indexReader;
    TaxonomyReader taxonomyReader;

    try {
      indexReader = DirectoryReader.open(indexDir);
      taxonomyReader = new DirectoryTaxonomyReader(taxonomyDir);
    } catch (IOException e) {
      throw new IllegalStateException("Unexpected error opening the Lucene index", e);
    }

    log.info("Searching for time zones using the keyword: '{}'", keyword);
    IndexSearcher searcher = new IndexSearcher(indexReader);
    StoredFields storedFields;
    try {
      storedFields = searcher.storedFields();
    } catch (IOException e) {
      throw new RuntimeException("Something went wrong during search initialization.", e);
    }
    StandardQueryParser queryParser = new StandardQueryParser(new StandardAnalyzer());
    FacetsCollector facetsCollector = new FacetsCollector();

    List<ScoreDoc> hits;
    FacetResult facetResult;

    try {

      Query queryOnId = queryParser.parse(keyword, TimeZoneIndexer.FIELD_ID);
      Query queryOnDisplayName = queryParser.parse(keyword, TimeZoneIndexer.FIELD_TIME_ZONE_DISPLAY_NAME);

      // Note: A Lucene boolean query uses the word "SHOULD" to mean "at least one of the 'should' terms must
      // match". Here, we want the search keyword to match either the HTML body, the title (or of course both).
      Query query = new BooleanQuery.Builder()
              .add(queryOnId, BooleanClause.Occur.SHOULD)
              .add(queryOnDisplayName, BooleanClause.Occur.SHOULD)
              .build();

      TopDocs results = FacetsCollector.search(searcher, query, Integer.MAX_VALUE, facetsCollector);
      ScoreDoc[] packageNameHits = results.scoreDocs;
      hits = List.of(packageNameHits);

      Facets facets = new FastTaxonomyFacetCounts(taxonomyReader, facetsConfig, facetsCollector);
      facetResult = facets.getTopChildren(Integer.MAX_VALUE, TimeZoneIndexer.FIELD_OBSERVES_DAYLIGHT_SAVINGS_TIME);
    } catch (QueryNodeException | IOException e) {
      throw new IllegalStateException("Unexpected error while searching", e);
    }

    log.info("Found {} hits. Found facet result: {}", hits.size(), facetResult);

    List<Document> results = hits.stream()
            .map(hit -> {
              try {
                return storedFields.document(hit.doc);
              } catch (IOException e) {
                throw new IllegalStateException("Unexpected error while getting the document from the index", e);
              }
            })
            .toList();

    try {
      indexReader.close();
      taxonomyReader.close();
    } catch (IOException e) {
      throw new RuntimeException("Failed to close the reader", e);
    }
    return new SearchResults(results, facetResult);
  }

  /**
   * Index the domain data into an in-memory Lucene index.
   */
  private void indexData() {
    // The "CREATE" open mode removes any pre-existing index files.
    IndexWriterConfig.OpenMode openMode = IndexWriterConfig.OpenMode.CREATE;

    IndexWriterConfig config = new IndexWriterConfig(analyzer);
    config.setOpenMode(openMode);

      try (var indexWriter = new IndexWriter(indexDir, config);
           var taxonomyWriter = new DirectoryTaxonomyWriter(taxonomyDir, openMode)) {

        List<TimeZone> timeZones = findTimeZones();
        log.info("Indexing {} known time zones.", timeZones.size());

        var indexer = new TimeZoneIndexer(indexWriter, taxonomyWriter);
        for (var timeZone : timeZones) {
          indexer.index(timeZone);
        }

        log.info("Indexing done.");
    } catch (Exception e) {
      log.error("Unexpected error while indexing.", e);
      System.exit(1);
    }
  }

  private static List<TimeZone> findTimeZones() {
    String[] timeZoneIds = TimeZone.getAvailableIDs();
    return Arrays.stream(timeZoneIds)
            .map(TimeZone::getTimeZone)
            // Filter out "GMT" because the results are not interesting. They just describe offsets not places.
            .filter(timeZone -> !timeZone.getDisplayName(Locale.US).startsWith("GMT"))
            .sorted(Comparator.comparing(TimeZone::getRawOffset).thenComparing(TimeZone::getID))
            .toList();
  }
}
