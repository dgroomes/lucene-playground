package dgroomes;

import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.io.HttpRequestHandler;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.lucene.document.Document;

import java.io.IOException;
import java.util.List;

/**
 * This handles incoming HTTP requests that represent searches.
 */
class HttpHandler implements HttpRequestHandler {

  private final TimeZoneSearchSystem timeZoneSearchSystem;

  public HttpHandler(TimeZoneSearchSystem timeZoneSearchSystem) {
    this.timeZoneSearchSystem = timeZoneSearchSystem;
  }

  @Override
  public void handle(final ClassicHttpRequest request, final ClassicHttpResponse response, final HttpContext context) throws HttpException, IOException {
    String keyword = parseKeyword();
    List<Document> results = timeZoneSearchSystem.search(keyword);
    var msg = "Found %d results for the search.";
    var responseBody = new StringEntity(msg.formatted(results.size()));
    response.setEntity(responseBody);
  }

  private String parseKeyword() {
    // todo parse the search from the request
    //   String body = EntityUtils.toString(request.getEntity());
    return "Central*";
  }
}
