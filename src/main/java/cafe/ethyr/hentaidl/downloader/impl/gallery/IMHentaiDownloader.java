package cafe.ethyr.hentaidl.downloader.impl.gallery;

import cafe.ethyr.hentaidl.downloader.DownloadException;
import cafe.ethyr.hentaidl.downloader.composed.GalleryDownloader;
import cafe.ethyr.hentaidl.downloader.factory.DownloaderType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

public class IMHentaiDownloader extends GalleryDownloader {

    private static final String MAIN_URL = "https://imhentai.xxx/gallery/%s/";
    private static final String VIEW_URL = "https://imhentai.xxx/view/%s/%s/";

    public IMHentaiDownloader(String path) {
        super(path, DownloaderType.IMHENTAI);
    }

    @Override
    protected String fixUrl(String url) {
        if (url.startsWith("https://imhentai.xxx") || url.startsWith("imhentai.xxx"))
            url = url.split("/gallery/")[1].replace("/", "");

        putArgument("id", url);
        return String.format(MAIN_URL, url);
    }

    @Override
    protected String parseName(Element body) {
        return body.getElementsByClass("col-md-7 col-sm-7 col-lg-8 right_details").select("h1").text();
    }

    @Override
    protected int parsePages(Element body) {
        return Integer.parseInt(body.getElementsByClass("pages").select("li").text().split(": ")[1]);
    }

    @Override
    protected String gatherImageUrl(int page) {
        try {
            return Jsoup.connect(String.format(VIEW_URL, getArgument("id"), page)).get().body().getElementById("gimg").attr("data-src");
        } catch (Exception e) {
            throw new DownloadException(e);
        }
    }

    @Override
    protected String gatherImageSource(Element body) {
        return body.getElementsByClass("row gallery_first").first().getElementsByClass("lazy").first().attr("data-src");
    }
}
