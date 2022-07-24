package dgroomes;

import org.apache.hc.core5.http.impl.bootstrap.HttpServer;
import org.apache.hc.core5.http.impl.bootstrap.ServerBootstrap;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.util.TimeValue;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * A demonstration that exposes Lucene search as an HTTP API. See the README for more information.
 */
public class Runner {
  private static final Logger log = LoggerFactory.getLogger(Runner.class);

  private static final int PORT = 8080;

  public static void main(String[] args) {

    try (Directory indexDir = new ByteBuffersDirectory();
         Analyzer analyzer = new StandardAnalyzer()) {

      indexData(indexDir, analyzer);
      runServerContinuously(indexDir);
    } catch (IOException e) {
      log.error("Unexpected error", e);
      throw new RuntimeException(e);
    }
  }

  /**
   * Search for the given keyword.
   */
  public static List<Document> search(Directory indexDir, String keyword) {
    DirectoryReader reader = null;
    try {
      reader = DirectoryReader.open(indexDir);
    } catch (IOException e) {
      throw new IllegalStateException("Unexpected error opening the Lucene index", e);
    }

    log.info("Searching for time zones using the keyword: '{}'", keyword);
    IndexSearcher searcher = new IndexSearcher(reader);
    StandardQueryParser queryParser = new StandardQueryParser(new StandardAnalyzer());

    List<ScoreDoc> hits;

    try {
      Query query = queryParser.parse(keyword, Indexer.FIELD_TIME_ZONE_DISPLAY_NAME);
      TopDocs results = searcher.search(query, 2000);
      ScoreDoc[] packageNameHits = results.scoreDocs;
      hits = List.of(packageNameHits);
    } catch (QueryNodeException | IOException e) {
      throw new IllegalStateException("Unexpected error while searching", e);
    }

    log.info("Found {} hits", hits.size());

    return hits.stream()
            .map(hit -> {
              try {
                return searcher.doc(hit.doc);
              } catch (IOException e) {
                throw new IllegalStateException("Unexpected error while getting the document from the index", e);
              }
            })
            .toList();
  }

  /**
   * Index the domain data into an in-memory Lucene index.
   */
  private static void indexData(Directory indexDir, Analyzer analyzer) {
    try (var indexWriter = indexWriter(indexDir, analyzer)) {

      List<String> timeZoneDisplayNames = findTimeZones();
      log.info("Indexing {} known time zones.", timeZoneDisplayNames.size());

      var indexer = new Indexer(indexWriter);
      for (var timeZoneDisplayName : timeZoneDisplayNames) {
        indexer.index(timeZoneDisplayName);
      }

      log.info("Indexing done.");
    } catch (Exception e) {
      log.error("Unexpected error while indexing.", e);
      System.exit(1);
    }
  }

  private static List<String> findTimeZones() {
    String[] timeZoneIds = TimeZone.getAvailableIDs();
    return Arrays.stream(timeZoneIds)
            .map(id -> TimeZone.getTimeZone(id).getDisplayName(Locale.US))
            // Filter out "GMT" because the results are not interesting. They just describe offsets not places.
            .filter(displayName -> !displayName.startsWith("GMT"))
            .distinct()
            .sorted()
            .toList();
  }

  /**
   * Run the HTTP server. This runs continuously until the process is stopped with "Ctrl + C".
   */
  private static void runServerContinuously(Directory indexDir) throws IOException {
    var simulatorHttpHandler = new HttpHandler(indexDir);

    ServerBootstrap builder = ServerBootstrap.bootstrap()
            .setListenerPort(PORT)
            .register("*", simulatorHttpHandler);

    try (HttpServer server = builder.create()) {
      server.start();
      Runtime.getRuntime().addShutdownHook(new Thread(() -> server.close(CloseMode.GRACEFUL)));
      log.info("The Lucene search server is serving traffic on port {}", PORT);
      server.awaitTermination(TimeValue.MAX_VALUE);
    } catch (InterruptedException e) {
      log.error("The server was interrupted.", e);
    }
  }

  private static IndexWriter indexWriter(Directory dir, Analyzer analyzer) throws IOException {
    IndexWriterConfig config = new IndexWriterConfig(analyzer);

    // This configuration removes any pre-existing index files.
    config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

    return new IndexWriter(dir, config);
  }
}
