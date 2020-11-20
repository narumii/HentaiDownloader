package xyz.ethyr.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.internal.StringUtil;
import xyz.ethyr.booru.Site;

public final class SiteUtil {

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

  public static List<Site> createSites(Map<Integer, Integer> pages, String url, String[] tags) {
    List<Site> urls = new ArrayList<>();
    pages.forEach((page, images) -> {
      if (images > 0) {
        urls.add(new Site(String.format(url, images, page, StringUtil.join(
            Arrays.asList(tags), "+")), page, images));
      }
    });
    return urls;
  }

  public static JSONObject toJson(String url) throws IOException {
    return new JSONObject(
        Jsoup.connect(url).userAgent(USER_AGENT).ignoreContentType(true).execute().body());
  }
}
