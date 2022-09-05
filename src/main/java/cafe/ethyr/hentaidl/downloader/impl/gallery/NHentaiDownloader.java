package cafe.ethyr.hentaidl.downloader.impl.gallery;

import cafe.ethyr.hentaidl.downloader.composed.GalleryDownloader;
import cafe.ethyr.hentaidl.downloader.factory.DownloaderType;
import cafe.ethyr.hentaidl.executor.ExecutorHelper;
import cafe.ethyr.hentaidl.helper.FileHelper;
import cafe.ethyr.hentaidl.helper.PropertiesHelper;
import cafe.ethyr.hentaidl.helper.SiteHelper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

public class NHentaiDownloader extends GalleryDownloader {

    /**
     * @link https://github.com/sinkaroid/jandapress/blob/2b5d6badd4ea03c8ec1d39ab873772b22c5d5187/src/utils/options.ts#L10
     */
    private static final String NUMERIC_IP = "138.2.77.198:3002";
    private static final String NORMAL_IP = "nhentai.net";

    private static final boolean DIRECT = Boolean.parseBoolean(PropertiesHelper.getProperty("nhentai.direct"));

    private static final String SCHEME = DIRECT ? "http" : "https";
    private static final String BASE = DIRECT ? NUMERIC_IP : NORMAL_IP;

    private static final String GALLERY_URL = "%s://%s/api/gallery/%s";

    public NHentaiDownloader(String path) {
        super(path, DownloaderType.NHENTAI);
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
            JSONObject json = new JSONObject(body.text());

            String name = json.getJSONObject("title").getString("pretty");
            String mediaId = json.getString("media_id");
            JSONArray pages = json.getJSONObject("images").getJSONArray("pages");

            int pageAmount = pages.length();

            Path path = Path.of(getArgument("path"), FileHelper.fixPath(name));
            FileHelper.deleteAndCreateDirectory(path);
            completionMessage(String.format("Downloaded %s\r", name));

            System.out.println("Name: " + name);
            System.out.println("Pages: " + pageAmount);

            AtomicInteger index = new AtomicInteger();

            jobs(pageAmount);

            final String type;
            switch (pages.getJSONObject(0).getString("t")) {
                case "p":
                    type = "png";
                    break;
                case "g":
                    type = "gif";
                    break;
                case "w":
                    type = "webp";
                    break;
                default:
                    type = "jpg";
            }

            for (int unused = 1; unused != pageAmount + 1; unused++) {
                ExecutorHelper.submit(() -> {
                    try {
                        int page = index.incrementAndGet();
                        System.out.printf("Downloading (%s) | Page: %s/%s (%s)\r",
                                name, page, pageAmount, calculatePercent(page, pageAmount));


                        String url = String.format(downloaderType.getApi(), mediaId, page, type);
                        FileHelper.saveImage(
                                path.resolve(page + "." + type),
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
        throw new UnsupportedOperationException();
    }

    @Override
    protected int parsePages(Element body) {
        throw new UnsupportedOperationException();
    }
}
