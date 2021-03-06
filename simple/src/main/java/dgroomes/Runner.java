package dgroomes;

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

      search(indexDir, analyzer, "explorer");
      search(indexDir, analyzer, "fish");
      search(indexDir, analyzer, "entity");
    } catch (Exception e) {
      log.error("Unexpected error while searching.", e);
      System.exit(1);
    }
  }

  private static void search(FSDirectory indexDir, StandardAnalyzer analyzer, String word) throws IOException, QueryNodeException {
    var reader = DirectoryReader.open(indexDir);
    log.info("Let's search for the text content: '{}'", word);
    IndexSearcher searcher = new IndexSearcher(reader);
    var parser = new StandardQueryParser(analyzer);
    {
      // By default, leading wildcards are not allowed because when used, they cause the search to do a full scan of the
      // term index. This is slow relative to a normal index-driven search. For example, you can't search "*fish" in the
      // hopes if finding matches for "starfish". Fortunately, you can relax this restriction, but you should consider
      // the impact to performance.
      parser.setAllowLeadingWildcard(true);
    }
    Query query = parser.parse(word, FileAsLinesIndexer.FIELD_CONTENTS);

    TopDocs results = searcher.search(query, 10);
    ScoreDoc[] hits = results.scoreDocs;
    log.info("Found {} hits", hits.length);

    for (ScoreDoc hit : hits) {
      Document document = searcher.doc(hit.doc);
      log.info("\tHit: {}", document);
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

    // This configuration removes any pre-existing index files.
    config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

    return new IndexWriter(dir, config);
  }
}
