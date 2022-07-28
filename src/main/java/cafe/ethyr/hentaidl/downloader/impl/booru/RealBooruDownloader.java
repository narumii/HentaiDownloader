package cafe.ethyr.hentaidl.downloader.impl.booru;

import cafe.ethyr.hentaidl.downloader.composed.BooruDownloader;
import cafe.ethyr.hentaidl.downloader.factory.DownloaderType;

public class RealBooruDownloader extends BooruDownloader {

    public RealBooruDownloader(String path) {
        super(path, DownloaderType.REALBOORU);
    }
}
