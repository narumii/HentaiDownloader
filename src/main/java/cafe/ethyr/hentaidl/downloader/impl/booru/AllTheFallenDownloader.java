package cafe.ethyr.hentaidl.downloader.impl.booru;

import cafe.ethyr.hentaidl.data.booru.Image;
import cafe.ethyr.hentaidl.downloader.composed.BooruDownloader;
import cafe.ethyr.hentaidl.downloader.factory.DownloaderType;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Optional;

public class AllTheFallenDownloader extends BooruDownloader {

    public AllTheFallenDownloader(String path) {
        super(path, DownloaderType.AFT);
    }

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
