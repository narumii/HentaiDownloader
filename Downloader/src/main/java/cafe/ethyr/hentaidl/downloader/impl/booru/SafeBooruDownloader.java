package cafe.ethyr.hentaidl.downloader.impl.booru;

import cafe.ethyr.hentaidl.downloader.composed.BooruDownloader;
import cafe.ethyr.hentaidl.downloader.factory.DownloaderType;

public class SafeBooruDownloader extends BooruDownloader {

    public SafeBooruDownloader(String path) {
        super(path, DownloaderType.SAFEBOORU);
    }
}
