package cafe.ethyr.hentaidl.downloader.impl.gallery;

import cafe.ethyr.hentaidl.downloader.composed.GalleryDownloader;
import cafe.ethyr.hentaidl.downloader.factory.DownloaderType;
import org.jsoup.nodes.Element;

public class ASMHentaiDownloader extends GalleryDownloader {

    private static final String GALLERY_URL = "https://asmhentai.com/g/%s/";

    public ASMHentaiDownloader(String path) {
        super(path, DownloaderType.ASMHENTAI);
    }

    @Override
    protected String fixUrl(String url) {
        if (url.contains("asmhentai.com/g"))
            url = url.split("/g/")[1].replace("/", "");

        putArgument("id", url);
        return String.format(GALLERY_URL, url);
    }

    @Override
    protected String parseName(Element body) {
        return body.getElementsByClass("info").select("h1").text();
    }

    @Override
    protected int parsePages(Element body) {
        return Integer.parseInt(body.getElementsByClass("info").first().getElementsByClass("pages").first().select("h3").text().split(": ")[1]);
    }

    @Override
    protected String composeDownloadUrl(String url) {
        return url.substring(0, url.lastIndexOf('/')) + "/%s" + url.substring(url.lastIndexOf('.'));
    }

    @Override
    protected String gatherImageSource(Element body) {
        return body.getElementsByClass("gallery").first().getElementsByClass("preview_thumb").select("img").attr("data-src");
    }
}
