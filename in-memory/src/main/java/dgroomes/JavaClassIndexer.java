package dgroomes;

import io.github.classgraph.ClassInfo;
import io.github.classgraph.MethodInfo;
import io.github.classgraph.ModuleInfo;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;

import java.io.IOException;

/**
 * Index Java classes (e.g. name, package and module) into a Lucene index.
 */
public class JavaClassIndexer {

  public static final String FIELD_CLASS_NAME = "class_name";
  public static final String FIELD_PACKAGE_NAME = "package_name";
  public static final String FIELD_MODULE_NAME = "module_name";

  private final IndexWriter indexWriter;

  public JavaClassIndexer(IndexWriter indexWriter) {
    this.indexWriter = indexWriter;
  }

  public void indexClass(ClassInfo classInfo) throws IOException {
    String className = classInfo.getSimpleName();
    String packageName = classInfo.getPackageName();
    String moduleName;
    {
      ModuleInfo moduleInfo = classInfo.getModuleInfo();
      if (moduleInfo == null) {
        // Classes that are not part of a modularized library/component will be in the "unnamed" module (I think?).
        moduleName = "unnamed";
      } else {
        moduleName = moduleInfo.getName();
      }
    }

    var doc = new Document();

    doc.add(new TextField(FIELD_CLASS_NAME, className, Field.Store.YES));
    doc.add(new TextField(FIELD_PACKAGE_NAME, packageName, Field.Store.YES));
    doc.add(new TextField(FIELD_MODULE_NAME, moduleName, Field.Store.YES));

    indexWriter.addDocument(doc);
  }
}
