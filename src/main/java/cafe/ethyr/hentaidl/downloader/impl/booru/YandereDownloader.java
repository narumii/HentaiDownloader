package cafe.ethyr.hentaidl.downloader.impl.booru;

import cafe.ethyr.hentaidl.downloader.composed.BooruDownloader;
import cafe.ethyr.hentaidl.downloader.factory.DownloaderType;

public class YandereDownloader extends BooruDownloader {

    public YandereDownloader(String path) {
        super(path, DownloaderType.YANDERE);
    }
}
