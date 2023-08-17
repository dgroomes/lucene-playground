package dgroomes;

import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.HttpRequestHandler;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.net.URIBuilder;
import org.apache.lucene.facet.FacetResult;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Collectors;

/**
 * This handles incoming HTTP requests that represent searches.
 */
class HttpHandler implements HttpRequestHandler {

  private final TimeZoneSearchSystem timeZoneSearchSystem;

  public HttpHandler(TimeZoneSearchSystem timeZoneSearchSystem) {
    this.timeZoneSearchSystem = timeZoneSearchSystem;
  }

  @Override
  public void handle(final ClassicHttpRequest request, final ClassicHttpResponse response, final HttpContext context) {
    Optional<String> keywordOpt = parseKeyword(request);
    if (keywordOpt.isEmpty()) {
      response.setCode(400);
      response.setEntity(new StringEntity("The 'keyword' query parameter is required. Please supply it."));
      return;
    }

    var keyword = keywordOpt.get();
    TimeZoneSearchSystem.SearchResult result = timeZoneSearchSystem.search(keyword);

    String msg;
    if (result.hits().isEmpty()) {
      msg = "No search results found for keyword '%s'".formatted(keyword);
    } else {
      var facetsSerialized = result.facetResults().stream()
              .map(FacetResult::toString)
              .collect(Collectors.joining("\n", "", ""));

      var hitsSerialized = result.hits().stream()
              .map(doc -> {
                String id = doc.get(TimeZoneIndexer.FIELD_ID);
                TimeZone timeZone = TimeZone.getTimeZone(id);
                return toString(timeZone);
              })
              .collect(Collectors.joining("\n", "", ""));

      msg = """
              Search found %d hits for keyword '%s'.
              
              Facet results:
              %s
              
              Hits:
              %s
              """.formatted(result.hits().size(), keyword, facetsSerialized.indent(4), hitsSerialized.indent(4));
    }

    var responseBody = new StringEntity(msg);
    response.setEntity(responseBody);
  }

  /**
   * Parse out the "keyword" query parameter if it exists. If it does not exist, an empty {@link Optional} is returned.
   */
  private Optional<String> parseKeyword(ClassicHttpRequest request) {
    URI uri;
    try {
      uri = request.getUri();
    } catch (URISyntaxException e) {
      throw new IllegalStateException("Unexpected error while parsing the HTTP request URI", e);
    }

    var params = parseQueryParams(uri);
    if (params.containsKey("keyword")) {
      return Optional.of(params.get("keyword"));
    } else {
      return Optional.empty();
    }
  }

  /**
   * Parse query parameters into a map.
   * <p>
   * This also normalizes the query parameter names by lower-casing them so you can predictably call "get" on the map.
   *
   * @return a map of the query parameter/value pairs, keyed by query parameter name.
   */
  private Map<String, String> parseQueryParams(URI uri) {
    return new URIBuilder(uri)
            .getQueryParams()
            .stream()
            .collect(Collectors.toMap(nameValuePair -> nameValuePair.getName().toLowerCase(), NameValuePair::getValue));
  }

  /**
   * This is used to format the TimeZone object search result in a way that reflects the fields we've showcased: ID,
   * offset and "observes daylight savings time".
   */
  public static String toString(TimeZone timeZone) {
    return "%s (%s) offset=%s observesDST=%s".formatted(timeZone.getDisplayName(),
            timeZone.getID(),
            TimeZoneIndexer.getOffsetDescription(timeZone),
            timeZone.observesDaylightTime());
  }
}
