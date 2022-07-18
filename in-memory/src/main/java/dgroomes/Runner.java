package dgroomes;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
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
 * This is a simple demo of Apache Lucene that showcases an in-memory usecase using {@link ByteBuffersDirectory}.
 */
public class Runner {
  private static final Logger log = LoggerFactory.getLogger(Runner.class);

  public static void main(String[] args) {

    try (var indexDir = new ByteBuffersDirectory();
         Analyzer analyzer = new StandardAnalyzer()) {

      try (var indexWriter = indexWriter(indexDir, analyzer)) {
        indexAllJavaClasses(indexWriter);
      } catch (Exception e) {
        log.error("Unexpected error while indexing.", e);
        System.exit(1);
      }

      search(indexDir, new StandardAnalyzer(), "Parser");
      search(indexDir, new StandardAnalyzer(), "ClassGraph");
      search(indexDir, new StandardAnalyzer(), "nonapi.io.github.classgraph.types");
    } catch (IOException | ParseException exception) {
      log.error("Unexpected error", exception);
    }
  }

  private static void search(Directory indexDir, Analyzer analyzer, String keyword) throws IOException, ParseException {
    var reader = DirectoryReader.open(indexDir);
    log.info("Let's search for Java class using the keyword: '{}'", keyword);
    IndexSearcher searcher = new IndexSearcher(reader);

    List<ScoreDoc> hits = new ArrayList<>();

    {
      QueryParser parser = new QueryParser(JavaClassIndexer.FIELD_CLASS_NAME, analyzer);
      Query query = parser.parse(keyword);
      TopDocs results = searcher.search(query, 2000);
      ScoreDoc[] classNameHits = results.scoreDocs;
      hits.addAll(List.of(classNameHits));
    }

    {
      QueryParser parser = new QueryParser(JavaClassIndexer.FIELD_PACKAGE_NAME, analyzer);
      Query query = parser.parse(keyword);
      TopDocs results = searcher.search(query, 2000);
      ScoreDoc[] packageNameHits = results.scoreDocs;
      hits.addAll(List.of(packageNameHits));
    }

    log.info("Found {} hits", hits.size());

    for (ScoreDoc hit : hits) {
      Document document = searcher.doc(hit.doc);
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
