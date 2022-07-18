package dgroomes;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
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
 * IN PROGRESS: I'm in the process of ejecting from the Lucene demo. I want one main thing: Don't write files (this makes for an easier demo)
 * <p>
 * This is a simple demo of Apache Lucene. It indexes and searches over the short stories in this Git repository.
 * <p>
 * Specifically, the program indexes the "short-stories/" directory and then prompts the user for a query.
 */
public class Runner {

  private static final Path SHORT_STORIES_DIR = Path.of("short-stories");
  private static final Path INDEX_DIR = Path.of("index");
  private static final Logger log = LoggerFactory.getLogger(Runner.class);


  public static void main(String[] args) {
    try (var indexDir = FSDirectory.open(INDEX_DIR);
         var indexWriter = indexWriter(indexDir)) {

      indexFilesInDirectory(indexWriter, SHORT_STORIES_DIR);
    } catch (Exception e) {
      log.error("Unexpected error.", e);
      System.exit(1);
    }

    // todo: Search the documents. (SearchFiles.main(new String[]{});)
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

  /**
   * TODO can we write the index to memory instead of a file? I mostly care just to have less cleanup for the demo, but
   * also this is a feature I'm looking for.
   */
  private static IndexWriter indexWriter(FSDirectory dir) throws IOException {
    Analyzer analyzer = new StandardAnalyzer();
    IndexWriterConfig config = new IndexWriterConfig(analyzer);
    return new IndexWriter(dir, config);
  }
}
