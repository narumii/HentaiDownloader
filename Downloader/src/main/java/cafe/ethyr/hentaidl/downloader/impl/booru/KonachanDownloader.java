package cafe.ethyr.hentaidl.downloader.impl.booru;

import cafe.ethyr.hentaidl.downloader.composed.BooruDownloader;
import cafe.ethyr.hentaidl.downloader.factory.DownloaderType;

public class KonachanDownloader extends BooruDownloader {

    public KonachanDownloader(String path) {
        super(path, DownloaderType.KONACHAN);
    }
}
