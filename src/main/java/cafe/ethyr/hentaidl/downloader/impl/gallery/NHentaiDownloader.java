package cafe.ethyr.hentaidl.downloader.impl.gallery;

import cafe.ethyr.hentaidl.downloader.DownloadException;
import cafe.ethyr.hentaidl.downloader.composed.GalleryDownloader;
import cafe.ethyr.hentaidl.downloader.factory.DownloaderType;
import cafe.ethyr.hentaidl.helper.ExecutorHelper;
import cafe.ethyr.hentaidl.helper.FileHelper;
import cafe.ethyr.hentaidl.helper.PropertiesHelper;
import cafe.ethyr.hentaidl.helper.SiteHelper;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

public class NHentaiDownloader extends GalleryDownloader {

    private static final String MAIN_URL = "https://nhentai.net/g/%s/";
    private static final String VIEW_URL = "https://nhentai.net/g/%s/%s/";

    public NHentaiDownloader(String path) {
        super(path, DownloaderType.NHENTAI);
    }

    @Override
    public void downloadImages() {
        try {
            Element body = Jsoup.connect(getArgument("url"))
                    .cookie("cf_chl_2", PropertiesHelper.getProperty("nhentai.cookie.cf_chl_2"))
                    .cookie("cf_chl_prog", PropertiesHelper.getProperty("nhentai.cookie.cf_chl_prog"))
                    .cookie("cf_clearance", PropertiesHelper.getProperty("nhentai.cookie.cf_clearance"))
                    .cookie("csrftoken", PropertiesHelper.getProperty("nhentai.cookie.csrftoken"))
                    .userAgent(PropertiesHelper.getProperty("nhentai.user_agent"))
                    .get();

            String name = parseName(body);
            int pages = parsePages(body);
            String downloadUrl = composeDownloadUrl(gatherImageSource(body));

            Path path = Path.of(getArgument("path"), FileHelper.fixPath(name));
            FileHelper.deleteAndCreateDirectory(path.toFile());

            System.out.println("Name: " + name);
            System.out.println("Pages: " + pages);

            AtomicInteger index = new AtomicInteger();
            AtomicInteger fileNameIndex = new AtomicInteger();

            jobs(pages);
            for (int unused = 1; unused != pages + 1; unused++) {
                ExecutorHelper.submit(() -> {
                    try {
                        int page = index.incrementAndGet();
                        System.out.printf("Downloading (%s) | Page: %s/%s (%s)\r",
                                name, page, pages, calculatePercent(page, pages));

                        int fileName = fileNameIndex.getAndIncrement();
                        String url = String.format(downloadUrl, page);
                        FileHelper.saveImage(FileHelper.computePath(path.toFile(), String.valueOf(fileName), SiteHelper.getExtension(url)),
                                SiteHelper.openConnection(url));

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
        return super.composeDownloadUrl(url).replaceAll("t[0-9]\\.", "i.");
    }
}
