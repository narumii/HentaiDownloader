package cafe.ethyr.hentaidl.downloader.impl.booru;

import cafe.ethyr.hentaidl.downloader.composed.BooruDownloader;
import cafe.ethyr.hentaidl.downloader.factory.DownloaderType;

public class Rule34Downloader extends BooruDownloader {

    public Rule34Downloader(String path) {
        super(path, DownloaderType.RULE34);
    }
}
