package cafe.ethyr.hentaidl.helper;

import cafe.ethyr.hentaidl.booru.Site;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SiteHelper {

    private static final String USER_AGENT = PropertiesHelper.getProperty("user_agent");

    public static int getPages(int value, int max) {
        return value <= max ? 0 : (value / max) + (value % max != 0 ? 1 : 0);
    }

    public static List<Site> createSites(int value, int maxPerPage, String url, String tags) {
        List<Site> urls = new ArrayList<>();

        int remain = value % maxPerPage;
        int pages = value <= maxPerPage ? 0 : (value / maxPerPage) + (remain != 0 ? 1 : 0);

        if (pages == 0) {
            return Collections.singletonList(new Site(String.format(url, 0, value, tags), 0, value));
        } else {
            for (int i = 0; i < pages; i++) {
                urls.add(new Site(String.format(url, i, maxPerPage, tags), i, maxPerPage));
            }

            if (remain != 0)
                urls.add(new Site(String.format(url, pages + 1, maxPerPage, tags), pages + 1, maxPerPage));

            return urls;
        }
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
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static String getExtension(String url) {
        return url.split("\\.")[url.split("\\.").length - 1];
    }

    public static String getUserAgent() {
        return USER_AGENT;
    }
}
