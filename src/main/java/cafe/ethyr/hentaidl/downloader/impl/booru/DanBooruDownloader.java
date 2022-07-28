package cafe.ethyr.hentaidl.downloader.impl.booru;

import cafe.ethyr.hentaidl.booru.Image;
import cafe.ethyr.hentaidl.downloader.composed.BooruDownloader;
import cafe.ethyr.hentaidl.downloader.factory.DownloaderType;
import cafe.ethyr.hentaidl.helper.PropertiesHelper;
import cafe.ethyr.hentaidl.helper.SiteHelper;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.HttpURLConnection;
import java.util.Optional;

public class DanBooruDownloader extends BooruDownloader {

    public DanBooruDownloader(String path) {
        super(path, DownloaderType.DANBOORU);
        login();
    }

    private void login() {
        try {
            HttpURLConnection connection = (HttpURLConnection) SiteHelper.openConnection(
                    String.format("https://danbooru.donmai.us/profile.json?login=%s&api_key=%s", PropertiesHelper.getProperty("danbooru.login"), PropertiesHelper.getProperty("danbooru.api_key"))
            );
            System.out.println(String.format("DanBooru login response: %s(%s)\n", connection.getResponseMessage(), connection.getResponseCode()));
            if (connection.getResponseCode() != 200)
                done();
        } catch (Exception e) {
            handleException(e);
        }
    }

    /*
    @Override
    public List<Site> createSites() {
        return SiteHelper.createSites(getArgument("amount"), 100, fixUrl(downloaderType.getApi()), getArgument("encoded_search"));
    }

    @Override
    public String fixUrl(String string) {
        return string
                .replace("{api_key}", PropertiesHelper.getProperty("danbooru_api_key"))
                .replace("{login}", PropertiesHelper.getProperty("danbooru_login"));
    }
    */

    @Override
    public Optional<Image> getImage(int position, Elements elements) {
        if (elements.size() <= position)
            return Optional.empty();

        Element post = elements.get(position);
        if (post == null || !post.select("file-url").text().contains("http"))
            return Optional.empty();

        String fileUrl = post.select("file-url").text();
        return Optional.of(new Image(fileUrl.substring(fileUrl.lastIndexOf('/')), fileUrl));
    }
}
