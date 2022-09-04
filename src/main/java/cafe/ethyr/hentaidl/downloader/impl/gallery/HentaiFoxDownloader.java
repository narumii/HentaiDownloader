package cafe.ethyr.hentaidl.downloader.impl.gallery;

import cafe.ethyr.hentaidl.downloader.composed.GalleryDownloader;
import cafe.ethyr.hentaidl.downloader.factory.DownloaderType;
import org.jsoup.nodes.Element;

public class HentaiFoxDownloader extends GalleryDownloader {

    private static final String GALLERY_URL = "https://hentaifox.com/gallery/%s/";

    public HentaiFoxDownloader(String path) {
        super(path, DownloaderType.HENTAIFOX);
    }

    @Override
    protected String fixUrl(String url) {
        if (url.contains("hentaifox.com/gallery"))
            url = url.split("/gallery/")[1].replace("/", "");
        else if (url.contains("hentaifox.com/g"))
            url = url.split("/g/")[1].replace("/", "");

        putArgument("id", url);
        return String.format(GALLERY_URL, url);
    }

    @Override
    protected String parseName(Element body) {
        return body.getElementsByClass("gallery_right").first().getElementsByClass("info").select("h1").text();
    }

    @Override
    protected int parsePages(Element body) {
        return Integer.parseInt(body.getElementsByClass("i_text pages").first().text().split(": ")[1]);
    }

    @Override
    protected String composeDownloadUrl(String url) {
        return url.substring(0, url.lastIndexOf('/')) + "/%s" + url.substring(url.lastIndexOf('.'));
    }

    @Override
    protected String gatherImageSource(Element body) {
        return body.getElementsByClass("gallery_left").first().getElementsByClass("cover").select("img").attr("src");
    }
}
