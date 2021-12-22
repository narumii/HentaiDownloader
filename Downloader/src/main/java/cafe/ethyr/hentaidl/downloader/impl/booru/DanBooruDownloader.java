package cafe.ethyr.hentaidl.downloader.impl.booru;

import cafe.ethyr.hentaidl.downloader.composed.BooruDownloader;
import cafe.ethyr.hentaidl.downloader.factory.DownloaderType;
import cafe.ethyr.hentaidl.helper.ExecutorHelper;
import cafe.ethyr.hentaidl.helper.PropertiesHelper;
import cafe.ethyr.hentaidl.helper.SiteHelper;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Optional;

public class DanBooruDownloader extends BooruDownloader {

    public DanBooruDownloader(String path) {
        super(path, DownloaderType.DANBOORU);
        ExecutorHelper.submit(this::login);
    }

    private void login() {
        try {
            SiteHelper.openConnection(
                    String.format("https://danbooru.donmai.us/profile.json?login=%s&api_key=%s", PropertiesHelper.getProperty("danbooru_login"), PropertiesHelper.getProperty("danbooru_api_key"))
            );
        } catch (Exception e) {
            handleException(e);
        }
    }

    @Override
    public Optional<String> getImage(int position, Elements elements) {
        if (elements.size() <= position)
            return Optional.empty();

        Element post = elements.get(position);
        if (post == null || !post.select("file-url").text().contains("http"))
            return Optional.empty();

        return Optional.of(post.select("file-url").text());
    }
}
