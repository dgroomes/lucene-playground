package dgroomes;

import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Given a file, treat each line of the file as a Lucene document and index the documents.
 * <p>
 * The shape of the index is:
 * <p>
 * - A {@link String} field named "file_name"
 * - A {@link IntPoint} field name "line_number"
 * - A {@link TextField} field named "contents"
 */
public class FileAsLinesIndexer {

  public static final String FIELD_FILE_NAME = "file_name";
  public static final String FIELD_LINE_NUMBER = "line_number";
  public static final String FIELD_CONTENTS = "contents";
  private static final Logger log = LoggerFactory.getLogger(FileAsLinesIndexer.class);

  private final IndexWriter indexWriter;

  public FileAsLinesIndexer(IndexWriter indexWriter) {
    this.indexWriter = indexWriter;
  }

  public void indexFile(Path path) {
    var fileName = path.getFileName().toString();

    // Note: it would be nice to use streams to do this work, but I need both the line text and the line
    // number. I think it's possible to make my own spliterator or whatever but that detracts from the demo.
    try (var fileReader = new FileReader(path.toFile()); var bufferedReader = new BufferedReader(fileReader)) {

      int lineNumber = 1; // Use 1-indexed because that's how line numbers are usually represented.

      String textContent;
      while ((textContent = bufferedReader.readLine()) != null) {
        var doc = toDocument(textContent, fileName, lineNumber);
        indexWriter.addDocument(doc);
        lineNumber++;
      }
    } catch (IOException e) {
      log.error("Unexpected error while ");
    }
  }

  /**
   * Incorporate a "subject text" (I would like a better word for this. This the text that we are indexing. This is the
   * actual data, so to speak. There's the word "corpus" but that's for the whole collection of texts).
   * <p>
   * In this demo, we're focusing on individual lines of text, instead of whole files. So, a line of text gets treated
   * as a whole Lucene {@link Document}.
   *
   * @param text       - The "subject" text.
   * @param fileName   - The name of the file.
   * @param lineNumber - The line number in the file that the text comes from.
   * @return a Lucene {@link Document}. It still needs to be indexed.
   */
  private Document toDocument(String text, String fileName, int lineNumber) {
    var doc = new Document();

    // The name of the file is in-scope for searching. So, include it in the document.
    Field pathField = new StringField(FIELD_FILE_NAME, fileName, Field.Store.YES);
    doc.add(pathField);

    // The line number is in-scope for searching.
    doc.add(new IntPoint(FIELD_LINE_NUMBER, lineNumber));

    // todo: when you provide a Reader, then the text is tokenized and indexed. But what if I want to store the string.
    //  I just use String, but does that mean the string is not tokenized and indexed?
    doc.add(new TextField(FIELD_CONTENTS, text, Field.Store.YES));

    return doc;
  }
}
