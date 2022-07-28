package cafe.ethyr.hentaidl.downloader.impl.booru;

import cafe.ethyr.hentaidl.downloader.composed.BooruDownloader;
import cafe.ethyr.hentaidl.downloader.factory.DownloaderType;

public class HypnoHubDownloader extends BooruDownloader {

    public HypnoHubDownloader(String path) {
        super(path, DownloaderType.HYPNO);
    }
}
