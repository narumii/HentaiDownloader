package cafe.ethyr.hentaidl.downloader.impl.gallery;

import cafe.ethyr.hentaidl.downloader.DownloadException;
import cafe.ethyr.hentaidl.downloader.composed.GalleryDownloader;
import cafe.ethyr.hentaidl.downloader.factory.DownloaderType;
import cafe.ethyr.hentaidl.helper.SiteHelper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

public class NHentaiDownloader extends GalleryDownloader {

    private static final String MAIN_URL = "https://nhentai.net/g/%s/";
    private static final String VIEW_URL = "https://nhentai.net/g/%s/%s/";

    public NHentaiDownloader(String path) {
        super(path, DownloaderType.NHENTAI);
    }

    @Override
    protected String fixUrl(String url) {
        if (url.startsWith("https://nhentai") || url.startsWith("nhentai"))
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
        return Integer.parseInt(body.getElementsByClass("name").last().ownText());
    }

    @Override
    protected String gatherImageUrl(int page) {
        try {
            return Jsoup.connect(String.format(VIEW_URL, getArgument("id"), page)).userAgent(SiteHelper.getUserAgent()).get().body().getElementById("image-container").select("img").attr("src");
        } catch (Exception e) {
            throw new DownloadException(e);
        }
    }

    @Override
    protected String composeDownloadUrl(String url) {
        return super.composeDownloadUrl(url).replace("t.nhentai", "i.nhentai");
    }
}
