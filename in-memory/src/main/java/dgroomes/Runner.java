package dgroomes;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
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
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a simple demo of Apache Lucene that showcases an in-memory use case using {@link ByteBuffersDirectory}.
 */
public class Runner {
  private static final Logger log = LoggerFactory.getLogger(Runner.class);

  public static void main(String[] args) {

    try (Directory indexDir = new ByteBuffersDirectory();
         Analyzer analyzer = new StandardAnalyzer()) {

      index(indexDir, analyzer);
      search(indexDir);
    } catch (IOException e) {
      log.error("Unexpected error", e);
      throw new RuntimeException(e);
    }
  }

  private static void search(Directory indexDir) {
    try {
      search(indexDir, new StandardAnalyzer(), "Parser");
      search(indexDir, new StandardAnalyzer(), "ClassGraph");
      search(indexDir, new StandardAnalyzer(), "nonapi.io.github.classgraph.types");
    } catch (IOException | QueryNodeException e) {
      log.error("Unexpected error while searching", e);
      System.exit(1);
    }
  }

  private static void index(Directory indexDir, Analyzer analyzer) {
    try (var indexWriter = indexWriter(indexDir, analyzer)) {
      indexAllJavaClasses(indexWriter);
    } catch (Exception e) {
      log.error("Unexpected error while indexing.", e);
      System.exit(1);
    }
  }

  private static void search(Directory indexDir, Analyzer analyzer, String keyword) throws IOException, QueryNodeException {
    var reader = DirectoryReader.open(indexDir);
    log.info("Let's search for Java classes using the keyword: '{}'", keyword);
    IndexSearcher searcher = new IndexSearcher(reader);
    StoredFields storedFields;
    try {
      storedFields = searcher.storedFields();
    } catch (IOException e) {
      throw new RuntimeException("Something went wrong during search initialization.", e);
    }
    StandardQueryParser queryParser = new StandardQueryParser(analyzer);

    List<ScoreDoc> hits = new ArrayList<>();

    {
      Query query = queryParser.parse(keyword, JavaClassIndexer.FIELD_CLASS_NAME);
      TopDocs results = searcher.search(query, 2000);
      ScoreDoc[] classNameHits = results.scoreDocs;
      hits.addAll(List.of(classNameHits));
    }

    {
      Query query = queryParser.parse(keyword, JavaClassIndexer.FIELD_PACKAGE_NAME);
      TopDocs results = searcher.search(query, 2000);
      ScoreDoc[] packageNameHits = results.scoreDocs;
      hits.addAll(List.of(packageNameHits));
    }

    log.info("Found {} hits", hits.size());

    for (ScoreDoc hit : hits) {
      Document document = storedFields.document(hit.doc);
      log.info("\tHit: {}", document);
    }

    log.info("");
  }

  private static void indexAllJavaClasses(IndexWriter indexWriter) throws IOException {
    log.info("Indexing all Java classes on the classpath");

    ClassGraph classGraph = new ClassGraph().enableClassInfo().enableMethodInfo();

    List<ClassInfo> classInfos;
    try (var scanResult = classGraph.scan()) {
      classInfos = scanResult.getAllClasses()
              .stream()
              .toList();
    }

    log.info("Found {} classes. Indexing them...\n", classInfos.size());

    var indexer = new JavaClassIndexer(indexWriter);
    for (var classInfo : classInfos) {
      indexer.indexClass(classInfo);
    }

    log.info("Indexing done.");
  }

  private static IndexWriter indexWriter(Directory dir, Analyzer analyzer) throws IOException {
    IndexWriterConfig config = new IndexWriterConfig(analyzer);

    // This configuration removes any pre-existing index files.
    config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

    return new IndexWriter(dir, config);
  }
}
