package dgroomes;

import io.github.classgraph.ClassInfo;
import io.github.classgraph.MethodInfo;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;

import java.io.IOException;

/**
 * Index Java classes (e.g. name and package) into a Lucene index.
 */
public class JavaClassIndexer {

  public static final String FIELD_CLASS_NAME = "class_name";
  public static final String FIELD_PACKAGE_NAME = "package_name";
  public static final String FIELD_NUMBER_OF_PUBLIC_METHODS = "number_of_public_methods";

  private final IndexWriter indexWriter;

  public JavaClassIndexer(IndexWriter indexWriter) {
    this.indexWriter = indexWriter;
  }

  public void indexClass(ClassInfo classInfo) throws IOException {
    String className = classInfo.getSimpleName();
    String packageName = classInfo.getPackageName();
    int numberOfPublicMethods = classInfo.getMethodInfo().filter(MethodInfo::isPublic).size();

    var doc = new Document();

    doc.add(new TextField(FIELD_CLASS_NAME, className, Field.Store.YES));
    doc.add(new TextField(FIELD_PACKAGE_NAME, packageName, Field.Store.YES));
    doc.add(new LongPoint(FIELD_NUMBER_OF_PUBLIC_METHODS, numberOfPublicMethods));

    indexWriter.addDocument(doc);
  }
}
