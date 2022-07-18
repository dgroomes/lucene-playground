package dgroomes;

import org.apache.lucene.demo.IndexFiles;
import org.apache.lucene.demo.SearchFiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

/**
 * This is a simple demo of Apache Lucene. It indexes and searches over the source code of this Git repository.
 *
 * Specifically, the program indexes the "src/" directory and then prompts the user for a query. There are only a couple
 * of files in the "src/" directory, so it makes for a small demo.
 */
public class Runner {

  private final static String SRC_DIR = Path.of("src").toAbsolutePath().toString();
  private final static String INDEX_DIR = Path.of("index").toAbsolutePath().toString();
  private static final Logger log = LoggerFactory.getLogger(Runner.class);

  public static void main(String[] args) throws Exception {
    // Note: it's not worth trying to catch an exception because the Lucene demo code just logs to standard error
    // and shuts down the process if something goes wrong.
    IndexFiles.main(new String[]{"-index", INDEX_DIR, "-docs", SRC_DIR});

    SearchFiles.main(new String[]{});
  }
}
