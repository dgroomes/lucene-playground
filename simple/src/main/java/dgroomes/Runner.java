package dgroomes;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.StoredFields;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * This is a simple demo of Apache Lucene. It indexes and searches over the short stories in this Git repository.
 * <p>
 * Specifically, the program indexes the "short-stories/" directory and then executes a few simple searches.
 */
public class Runner {
  private static final Path SHORT_STORIES_DIR = Path.of("short-stories");
  private static final Path INDEX_DIR = Path.of("index");
  private static final Logger log = LoggerFactory.getLogger(Runner.class);

  public static void main(String[] args) {

    try (var indexDir = FSDirectory.open(INDEX_DIR);
         var analyzer = new StandardAnalyzer();
         var indexWriter = indexWriter(indexDir, analyzer)) {

      indexFilesInDirectory(indexWriter, SHORT_STORIES_DIR);
    } catch (Exception e) {
      log.error("Unexpected error while indexing.", e);
      System.exit(1);
    }

    try (var indexDir = FSDirectory.open(INDEX_DIR);
         var analyzer = new StandardAnalyzer()) {

      var reader = DirectoryReader.open(indexDir);
      var searcher = new IndexSearcher(reader);

      log.info("Let's do a basic search. Searching for 'explorer' ...");
      search(searcher, analyzer, "explorer");

      log.info("Now, let's do a leading wildcard search. Searching for '*fish' ...");
      search(searcher, analyzer, "*fish");

      log.info("Now, let's do an English language-oriented search. Searching for 'entity' (this will yield 0 results!) ...");
      // This will yield no results even though we know the word 'entities' appears in the 'sky.txt' short story. The
      // content was indexed and searched with the Lucene StandardAnalyzer which does not perform stemming. By contrast,
      // the EnglishAnalyzer would stem the words 'entity' and 'entities' to their common root form 'entiti'. It's
      // important to understand the analyzer you're using and how it affects the index and the search.
      search(searcher, analyzer, "entity");

      log.info("Now, let's do a range search. Searching for lines 2 and earlier ...");
      {
        Query query = IntPoint.newRangeQuery(FileAsLinesIndexer.FIELD_LINE_NUMBER, Integer.MIN_VALUE, 2);
        search(searcher, query);
      }
    } catch (Exception e) {
      log.error("Unexpected error while searching.", e);
      System.exit(1);
    }
  }

  private static void search(IndexSearcher searcher, StandardAnalyzer analyzer, String word) throws QueryNodeException, IOException {
    var parser = new StandardQueryParser(analyzer);
    {
      // By default, leading wildcards are not allowed because when used, they cause the search to do a full scan of the
      // term index. This is slow relative to a normal index-driven search. For example, you can't search "*fish" in the
      // hopes of finding matches for "starfish". Fortunately, you can relax this restriction, but you should consider
      // the impact to performance.
      parser.setAllowLeadingWildcard(true);
    }
    Query query = parser.parse(word, FileAsLinesIndexer.FIELD_CONTENTS);

    search(searcher, query);
  }

  /**
   * Execute a search and print the results.
   */
  private static void search(IndexSearcher searcher, Query query) throws IOException {
    StoredFields storedFields;
    try {
      storedFields = searcher.storedFields();
    } catch (IOException e) {
      throw new RuntimeException("Something went wrong during search initialization.", e);
    }

    TopDocs results = searcher.search(query, 10);
    ScoreDoc[] hits = results.scoreDocs;
    log.info("Found {} hits", hits.length);

    for (ScoreDoc hit : hits) {
      Document document = storedFields.document(hit.doc);
      log.info("    Hit: {}", document);
    }

    log.info("");
  }

  private static void indexFilesInDirectory(IndexWriter indexWriter, Path documentsDir) throws IOException {
    log.info("Indexing all 'subject documents' in the directory: {}", documentsDir.toAbsolutePath());
    log.info("Writing the index files to the directory: {}", indexWriter.getDirectory());

    var fileAsLinesIndexer = new FileAsLinesIndexer(indexWriter);
    Files.walkFileTree(documentsDir, new SimpleFileVisitor<>() {
      @Override
      public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
        fileAsLinesIndexer.indexFile(path);
        return FileVisitResult.CONTINUE;
      }
    });
  }

  private static IndexWriter indexWriter(FSDirectory dir, StandardAnalyzer analyzer) throws IOException {
    IndexWriterConfig config = new IndexWriterConfig(analyzer);

    // This configuration removes any pre-existing index files (although this won't work if the encoding changed, like
    // I experienced with the evolution from the Lucene92 to Lucene95 encoding. I had to delete the index by manually.)
    config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

    return new IndexWriter(dir, config);
  }
}
