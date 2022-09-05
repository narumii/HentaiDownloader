package cafe.ethyr.hentaidl.downloader.impl.gallery;

import cafe.ethyr.hentaidl.downloader.DownloadException;
import cafe.ethyr.hentaidl.downloader.composed.GalleryDownloader;
import cafe.ethyr.hentaidl.downloader.factory.DownloaderType;
import cafe.ethyr.hentaidl.executor.ExecutorHelper;
import cafe.ethyr.hentaidl.helper.FileHelper;
import cafe.ethyr.hentaidl.helper.PropertiesHelper;
import cafe.ethyr.hentaidl.helper.SiteHelper;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

public class OldNHentaiDownloader extends GalleryDownloader {

    /**
     * @link https://github.com/sinkaroid/jandapress/blob/2b5d6badd4ea03c8ec1d39ab873772b22c5d5187/src/utils/options.ts#L10
     */
    private static final String NUMERIC_IP = "138.2.77.198:3002";
    private static final String NORMAL_IP = "nhentai.net";

    private static final boolean DIRECT = Boolean.parseBoolean(PropertiesHelper.getProperty("nhentai.direct"));

    private static final String SCHEME = DIRECT ? "http" : "https";
    private static final String BASE = DIRECT ? NUMERIC_IP : NORMAL_IP;

    private static final String GALLERY_URL = "%s://%s/g/%s";
    private static final String VIEW_URL = "%s://%s/g/%s/%s/";

    public OldNHentaiDownloader(String path) {
        super(path, DownloaderType.OLD_NHENTAI);
    }

    @Override
    public void downloadImages() {
        try {
            Connection connection = Jsoup.connect(getArgument("url")).ignoreContentType(true);
            if (!DIRECT) {
                connection.cookie("cf_clearance", PropertiesHelper.getProperty("nhentai.cookie.cf_clearance"));
                connection.userAgent(PropertiesHelper.getProperty("nhentai.user_agent"));
            }

            Element body = connection.get();

            String name = parseName(body);
            int pages = parsePages(body);
            String downloadUrl = composeDownloadUrl(gatherImageSource(body));

            Path path = Path.of(getArgument("path"), FileHelper.fixPath(name));
            FileHelper.deleteAndCreateDirectory(path);
            completionMessage(String.format("Downloaded %s\r", name));

            System.out.println("Name: " + name);
            System.out.println("Pages: " + pages);

            AtomicInteger index = new AtomicInteger();
            jobs(pages);
            for (int unused = 1; unused != pages + 1; unused++) {
                ExecutorHelper.submit(() -> {
                    try {
                        int page = index.incrementAndGet();
                        System.out.printf("Downloading (%s) | Page: %s/%s (%s)\r",
                                name, page, pages, calculatePercent(page, pages));

                        String url = String.format(downloadUrl, page);
                        FileHelper.saveImage(
                                path.resolve(page + "." + SiteHelper.getExtension(url)),
                                SiteHelper.openConnection(url)
                        );
                        completeJob();
                    } catch (Exception e) {
                        completeJob();
                        handleException(e);
                    }
                });
            }
        } catch (HttpStatusException e) {
            System.err.println("Invalid NHentai(CloudFlare) token. You need to copy your (nhentai) cookies and user agent to HentaiDownloader properties");
            done();
        } catch (Exception e) {
            handleException(e);
            done();
        }
    }

    @Override
    protected String fixUrl(String url) {
        if (url.contains("nhentai"))
            url = url.split("/g/")[1].replace("/", "");

        putArgument("id", url);
        return String.format(GALLERY_URL, SCHEME, BASE, url);
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
            return Jsoup.connect(String.format(VIEW_URL, SCHEME, BASE, getArgument("id"), page)).userAgent(SiteHelper.getUserAgent()).get().body().getElementById("image-container").select("img").attr("src");
        } catch (Exception e) {
            throw new DownloadException(e);
        }
    }

    @Override
    protected String composeDownloadUrl(String url) {
        return super.composeDownloadUrl(url).replaceAll("t[0-9]\\.", "i.");
    }
}
