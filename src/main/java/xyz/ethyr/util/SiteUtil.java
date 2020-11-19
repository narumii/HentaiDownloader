package xyz.ethyr.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;
import org.json.JSONObject;
import org.jsoup.Jsoup;

public class SiteUtil {

  private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11";

  public static Map<Integer, Integer> parsePages(int value, int numberPerPage) {
    Map<Integer, Integer> map = new HashMap<>();
    int pages = value / numberPerPage;
    IntStream.range(0, pages + 1).forEach(page -> {
      if (pages == page) {
        map.put(page, value - ((page) * numberPerPage));
      } else {
        map.put(page, numberPerPage);
      }
    });
    return map;
  }

  public static JSONObject toJson(final String url) throws IOException {
    return new JSONObject(
        Jsoup.connect(url).userAgent(USER_AGENT).ignoreContentType(true).execute().body());
  }
}
