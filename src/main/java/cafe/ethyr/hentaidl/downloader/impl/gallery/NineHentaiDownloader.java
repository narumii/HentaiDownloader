package cafe.ethyr.hentaidl.downloader.impl.gallery;

import cafe.ethyr.hentaidl.downloader.DownloadException;
import cafe.ethyr.hentaidl.downloader.composed.GalleryDownloader;
import cafe.ethyr.hentaidl.downloader.factory.DownloaderType;
import org.jsoup.nodes.Element;

public class NineHentaiDownloader extends GalleryDownloader {

    private static final String MAIN_URL = "https://9hentai.com/g/%s/";

    public NineHentaiDownloader(String path) {
        super(path, DownloaderType.NINEHENTAI);
    }

    @Override
    protected String fixUrl(String url) {
        if (url.contains("9hentai"))
            url = url.split("/g/")[1].replace("/", "");

        putArgument("id", url);
        return String.format(MAIN_URL, url);
    }

    @Override
    protected String parseName(Element body) {
        return body.getElementById("info").getElementsByTag("h1").text();
    }

    @Override
    protected int parsePages(Element body) {
        return Integer.parseInt(body.getElementsContainingOwnText("pages").text().split(" ")[0]);
    }

    @Override
    protected String gatherImageUrl(int page) {
        try {
            return String.format(downloaderType.getApi(), getArgument("id"), page);
        } catch (Exception e) {
            throw new DownloadException(e);
        }
    }
}
