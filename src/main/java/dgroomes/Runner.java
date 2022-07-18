package dgroomes;

import org.apache.lucene.demo.IndexFiles;
import org.apache.lucene.demo.SearchFiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

/**
 * This is a simple demo of Apache Lucene. It indexes and searches over the short stories in this Git repository.
 *
 * Specifically, the program indexes the "short-stories/" directory and then prompts the user for a query.
 */
public class Runner {

  private static final String SHORT_STORIES_DIR = Path.of("short-stories").toAbsolutePath().toString();
  private static final String INDEX_DIR = Path.of("index").toAbsolutePath().toString();

  private static final Logger log = LoggerFactory.getLogger(Runner.class);

  public static void main(String[] args) throws Exception {
    // Note: it's not worth trying to catch an exception because the Lucene demo code just logs to standard error
    // and shuts down the process if something goes wrong.
    IndexFiles.main(new String[]{"-index", INDEX_DIR, "-docs", SHORT_STORIES_DIR});

    SearchFiles.main(new String[]{});
  }
}
