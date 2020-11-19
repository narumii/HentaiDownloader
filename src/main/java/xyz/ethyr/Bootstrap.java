package xyz.ethyr;


import java.io.IOException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

public class Bootstrap {

  public static void main(String... args) throws IOException {
    final String xd = Jsoup.connect("https://nekos.life/api/v2/img/lewd").ignoreContentType(true)
        .execute().body();
    System.out.println(xd);
    final JSONObject url = new JSONObject(xd);
    System.out.println(url.get("url"));
  }
}
