package cafe.ethyr.hentaidl.downloader.composed;

import cafe.ethyr.hentaidl.downloader.Downloader;
import cafe.ethyr.hentaidl.downloader.factory.DownloaderType;
import cafe.ethyr.hentaidl.executor.ExecutorHelper;
import cafe.ethyr.hentaidl.helper.FileHelper;
import cafe.ethyr.hentaidl.helper.SiteHelper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.nio.file.Path;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class GalleryDownloader extends Downloader {

    public GalleryDownloader(String path, DownloaderType downloaderType) {
        super(path, downloaderType);
    }

    @Override
    public void readInput(Scanner scanner) {
        System.out.println("Link/Id: ");
        putArgument("url", fixUrl(scanner.nextLine()));
        System.out.println();
    }

    @Override
    public void downloadImages() {
        try {
            Element body = Jsoup.connect(getArgument("url")).userAgent(SiteHelper.getUserAgent()).get();

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
        } catch (Exception e) {
            handleException(e);
            done();
        }
    }

    //==============================================

    protected abstract String fixUrl(String url);

    protected abstract String parseName(Element body);

    protected abstract int parsePages(Element body);

    protected String gatherImageSource(Element body) {
        return body.getElementsByTag("head").first().getElementsByAttributeValue("property", "og:image").last().attr("content");
    }

    protected String composeDownloadUrl(String url) {
        return url.replace("/cover", "/%s");
    }

    protected String gatherImageUrl(int page) {
        throw new UnsupportedOperationException();
    }

    protected String gatherImageUrl(Element body) {
        throw new UnsupportedOperationException();
    }
}
