package cafe.ethyr.hentaidl.downloader.impl.booru;

import cafe.ethyr.hentaidl.downloader.composed.BooruDownloader;
import cafe.ethyr.hentaidl.downloader.factory.DownloaderType;

public class TbibDownloader extends BooruDownloader {

    public TbibDownloader(String path) {
        super(path, DownloaderType.TBIB);
    }
}
