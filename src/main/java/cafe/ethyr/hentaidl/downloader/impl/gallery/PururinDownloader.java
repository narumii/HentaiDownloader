package cafe.ethyr.hentaidl.downloader.impl.gallery;

import cafe.ethyr.hentaidl.downloader.DownloadException;
import cafe.ethyr.hentaidl.downloader.composed.GalleryDownloader;
import cafe.ethyr.hentaidl.downloader.factory.DownloaderType;
import cafe.ethyr.hentaidl.helper.ExecutorHelper;
import cafe.ethyr.hentaidl.helper.FileHelper;
import cafe.ethyr.hentaidl.helper.SiteHelper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

public class PururinDownloader extends GalleryDownloader {

    private static final String GALLERY_URL = "https://pururin.to/gallery/%s";

    public PururinDownloader(String path) {
        super(path, DownloaderType.PURURIN);
    }

    @Override
    public void downloadImages() {
        try {
            Element body = Jsoup.connect(getArgument("url")).userAgent(SiteHelper.getUserAgent()).get();
            String id = getArgument("id");

            String name = parseName(body);
            int pages = parsePages(body);

            Path path = Path.of(getArgument("path"), FileHelper.fixPath(name));
            FileHelper.deleteAndCreateDirectory(path.toFile());
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

                        String url = String.format(downloaderType.getApi(), id, page);
                        FileHelper.saveImage(FileHelper.computePath(path.toFile(), String.valueOf(page), SiteHelper.getExtension(url)),
                                SiteHelper.openConnection(url));

                        completeJob();
                    } catch (Exception e) {
                        completeJob();
                        handleException(e);
                    }
                });
            }
        } catch (Exception e) {
            handleException(e);
            done();
        }
    }

    @Override
    protected String fixUrl(String url) {
        if (url.contains("pururin.to/gallery"))
            url = url.split("/gallery/")[1];

        putArgument("id", url.split("/")[0]);
        return String.format(GALLERY_URL, url);
    }

    @Override
    protected String parseName(Element body) {
        String name = body.getElementsByClass("title").select("h1").text();
        name = name.contains("/") ? name.substring(0, name.indexOf("/")) : name;
        if (name.endsWith(" "))
            name = name.substring(0, name.length() - 1);

        return name;
    }

    @Override
    protected int parsePages(Element body) {
        return Integer.parseInt(body.getElementsByClass("table table-gallery-info").first().select("tr").last().parent().getElementsContainingOwnText("(").text().split(" ")[0]);
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
