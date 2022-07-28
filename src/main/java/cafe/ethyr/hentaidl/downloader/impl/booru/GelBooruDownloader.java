package cafe.ethyr.hentaidl.downloader.impl.booru;

import cafe.ethyr.hentaidl.booru.Image;
import cafe.ethyr.hentaidl.downloader.composed.BooruDownloader;
import cafe.ethyr.hentaidl.downloader.factory.DownloaderType;
import cafe.ethyr.hentaidl.helper.PropertiesHelper;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Optional;

public class GelBooruDownloader extends BooruDownloader {

    public GelBooruDownloader(String path) {
        super(path, DownloaderType.GELBOORU);
    }

    @Override
    public String fixUrl(String string) {
        return string
                .replace("{api_key}", PropertiesHelper.getProperty("gelbooru.api_key"))
                .replace("{user_id}", PropertiesHelper.getProperty("gelbooru.user_id"));
    }

    @Override
    public Optional<Image> getImage(int position, Elements elements) {
        if (elements.size() <= position)
            return Optional.empty();

        Element post = elements.get(position);
        if (post == null || !post.select("file_url").text().contains("http"))
            return Optional.empty();

        String fileUrl = post.select("file_url").text();
        return Optional.of(new Image(fileUrl.substring(fileUrl.lastIndexOf('/')), fileUrl));
    }
}
