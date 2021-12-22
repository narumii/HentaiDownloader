package cafe.ethyr.hentaidl.downloader.impl.booru;

import cafe.ethyr.hentaidl.downloader.composed.BooruDownloader;
import cafe.ethyr.hentaidl.downloader.factory.DownloaderType;
import cafe.ethyr.hentaidl.helper.PropertiesHelper;

public class GelBooruDownloader extends BooruDownloader {

    public GelBooruDownloader(String path) {
        super(path, DownloaderType.GELBOORU);
    }

    @Override
    public String fixUrl(String string) {
        return string
                .replace("{api_key}", PropertiesHelper.getProperty("gelbooru_api_key"))
                .replace("{user_id}", PropertiesHelper.getProperty("gelbooru_user_id"));
    }
}
