package cafe.ethyr.hentaidl.downloader.impl.booru;

import cafe.ethyr.hentaidl.booru.Image;
import cafe.ethyr.hentaidl.downloader.composed.BooruDownloader;
import cafe.ethyr.hentaidl.downloader.factory.DownloaderType;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Optional;

public class LoliBooruDownloader extends BooruDownloader {

    public LoliBooruDownloader(String path) {
        super(path, DownloaderType.LOLI);
    }

    public Optional<Image> getImage(int position, Elements elements) {
        if (elements.size() <= position)
            return Optional.empty();

        Element post = elements.get(position);
        if (post == null)
            return Optional.empty();

        String fileUrl = post.attr("file_url").replace(' ', '+');
        return Optional.of(new Image(fileUrl.substring(fileUrl.lastIndexOf('/')), fileUrl));
    }
}
