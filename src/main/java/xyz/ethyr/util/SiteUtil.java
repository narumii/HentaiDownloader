package xyz.ethyr.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import xyz.ethyr.booru.Image;
import xyz.ethyr.booru.Site;
import xyz.ethyr.util.RegexParser.RegexInfo;

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
        urls.add(
            new Site(String.format(url, images, page, StringUtil.join(tags, "+")), page, images));
      }
    });
    return urls;
  }

  public static JSONObject toJson(String url) throws IOException {
    return new JSONObject(
        Jsoup.connect(url).userAgent(USER_AGENT).ignoreContentType(true).execute().body());
  }

  public static URLConnection openConnection(String url) {
    HttpURLConnection connection = null;
    try {
      connection = (HttpURLConnection) new URL(url).openConnection();
      connection.setRequestProperty("User-Agent", USER_AGENT);
      return connection;
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
    return null;
  }

  public static String getExtension(String url) {
    return url.split("\\.")[url.split("\\.").length - 1];
  }

  public static Optional<Image> getImage(String fileName, int position, Elements elements,
      RegexInfo ratings, RegexInfo blacklistedTags) {
    try {
      Element post = elements.get(position);
      String tags = post.attr("tags");
      String rating = post.attr("rating");
      if ((ratings == null || ratings.getPattern().matcher(rating).matches()) && !(
          !blacklistedTags.getString().isEmpty() && blacklistedTags.getPattern().matcher(tags)
              .find())) {
        return Optional.of(new Image(post.attr("file_url"),
            fileName + "_" + FileUtil.generateRandomString(10)));
      }
    } catch (Exception e) {
    }
    return Optional.empty();
  }
}
