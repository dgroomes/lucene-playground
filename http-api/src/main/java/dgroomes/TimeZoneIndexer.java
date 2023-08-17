package dgroomes;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.facet.FacetField;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.taxonomy.TaxonomyWriter;
import org.apache.lucene.index.IndexWriter;

import java.io.IOException;
import java.time.Duration;
import java.util.TimeZone;

/**
 * Index time zone display names. For example "Alaska Standard Time", "Armenia Standard Time", "Chile Time", etc.
 *
 * Note: it would be cool to also index the offset from GMT.
 */
public class TimeZoneIndexer {

  public static final String FIELD_ID = "id";
  public static final String FIELD_OFFSET_DESCRIPTION = "offset";
  public static final String FIELD_OBSERVES_DAYLIGHT_SAVINGS_TIME = "observes_daylight_savings_time";
  public static final String FIELD_TIME_ZONE_DISPLAY_NAME = "time_zone_display_name";

  private final IndexWriter indexWriter;
  private final TaxonomyWriter taxonomyWriter;
  private final FacetsConfig facetsConfig = new FacetsConfig();

  public TimeZoneIndexer(IndexWriter indexWriter, TaxonomyWriter taxonomyWriter) {
    this.indexWriter = indexWriter;
    this.taxonomyWriter = taxonomyWriter;
  }

  public void index(TimeZone timeZone) throws IOException {
    var doc = new Document();
    doc.add(new TextField(FIELD_ID, timeZone.getID(), Field.Store.YES));
    doc.add(new FacetField(FIELD_OFFSET_DESCRIPTION, getOffsetDescription(timeZone)));
    doc.add(new FacetField(FIELD_OBSERVES_DAYLIGHT_SAVINGS_TIME, Boolean.toString(timeZone.observesDaylightTime())));

    // I'm so confused. When you treat a field as a facet, you can't get the field in the result, and you can't even
    // search on the field in the query. So can I just add the field as a regular field? Yeah, it looks like it but that's
    // roundabout and inefficient?
    doc.add(new TextField(FIELD_TIME_ZONE_DISPLAY_NAME, timeZone.getDisplayName(), Field.Store.YES));
    doc.add(new FacetField(FIELD_TIME_ZONE_DISPLAY_NAME, timeZone.getDisplayName()));

    Document build = facetsConfig.build(taxonomyWriter, doc);
    indexWriter.addDocument(build);
  }

  public static String getOffsetDescription(TimeZone timeZone) {
    // If we use the Duration class, we'll get a human-readable string for the offset instead of the raw offset in
    // milliseconds.
      return Duration.ofMillis(timeZone.getRawOffset()).toString();
  }
}
