package cafe.ethyr.hentaidl.downloader.impl.ehentai;

import cafe.ethyr.hentaidl.downloader.DownloadException;
import cafe.ethyr.hentaidl.downloader.factory.DownloaderType;
import cafe.ethyr.hentaidl.helper.PropertiesHelper;
import cafe.ethyr.hentaidl.helper.SiteHelper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.List;
import java.util.stream.Collectors;

public class ExHentaiDownloader extends EHentaiDownloader {

    public ExHentaiDownloader(String path) {
        super(path, DownloaderType.EXHENTAI);
    }

    @Override
    public List<String> gatherViewUrls(Element body) {
        return body.getElementsByClass("gdtm")
                .stream()
                .map(element -> element.select("a").attr("href"))
                .collect(Collectors.toList());
    }

    @Override
    protected String fixUrl(String url) {
        if (!url.startsWith("https://exhentai.org/g/") && !url.startsWith("exhentai.org/g/"))
            throw new DownloadException();

        if (url.contains("p=")) {
            url = url.substring(0, url.indexOf("p=") - 2);
        } else if (url.endsWith("/"))
            url = url.substring(0, url.length() - 1);

        putArgument("url", url);
        return url;
    }

    @Override
    public Document createJsoup(String url) {
        try {
            return Jsoup.connect(url)
                    .cookie("ipb_member_id", PropertiesHelper.getProperty("exhentai.cookie.ipb_member_id")) //exhentai access
                    .cookie("ipb_pass_hash", PropertiesHelper.getProperty("exhentai.cookie.ipb_pass_hash")) //exhentai access
                    .cookie("igneous", PropertiesHelper.getProperty("exhentai.cookie.igneous")) //exhentai access
                    .userAgent(SiteHelper.getUserAgent())
                    .get();

        } catch (Exception e) {
            throw new DownloadException(e);
        }
    }

    @Override
    protected String parseName(Element body) {
        return body.getElementById("gn").getElementsByTag("h1").text();
    }

    @Override
    protected int parsePages(Element body) {
        return Integer.parseInt(body.getElementsContainingOwnText("pages").text().split(" ")[0]);
    }

    @Override
    protected String gatherImageUrl(Element body) {
        try {
            return body.getElementById("i3").getElementById("img").attr("src");
        } catch (Exception e) {
            throw new DownloadException(e);
        }
    }
}
