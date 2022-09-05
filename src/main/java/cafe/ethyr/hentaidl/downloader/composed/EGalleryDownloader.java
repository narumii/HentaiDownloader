package cafe.ethyr.hentaidl.downloader.composed;

import cafe.ethyr.hentaidl.downloader.factory.DownloaderType;
import cafe.ethyr.hentaidl.executor.ExecutorHelper;
import cafe.ethyr.hentaidl.helper.FileHelper;
import cafe.ethyr.hentaidl.helper.SiteHelper;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class EGalleryDownloader extends GalleryDownloader {

    public EGalleryDownloader(String path, DownloaderType downloaderType) {
        super(path, downloaderType);
    }

    @Override
    public void readInput(Scanner scanner) {
        System.out.println("Link: ");
        putArgument("url", fixUrl(scanner.nextLine()));

        System.err.println("Image resolution for now can't be higher than HD");
        System.out.println();
    }

    @Override
    public void downloadImages() {
        try {
            Element body = createJsoup(getArgument("url")).body();

            String name = parseName(body);
            int images = parsePages(body);
            int pages = SiteHelper.getPages(images, 40) == 0 ? 1 : SiteHelper.getPages(images, 40);

            Path path = Path.of(getArgument("path"), FileHelper.fixPath(name));
            FileHelper.deleteAndCreateDirectory(path);
            completionMessage(String.format("Downloaded %s\r", name));

            System.out.println("Name: " + name);
            System.out.println("Images: " + images);
            System.out.println("Pages: " + pages);

            AtomicInteger index = new AtomicInteger();
            AtomicInteger fileNameIndex = new AtomicInteger();

            jobs(images);
            for (int ignored = 0; ignored < pages; ignored++) {
                ExecutorHelper.slaveSubmit(() -> {
                    int sitePage = index.getAndIncrement();
                    Element page = sitePage == 0 ? body : createJsoup(getArgument("url") + "/?p=" + sitePage);
                    List<String> viewUrls = gatherViewUrls(page);

                    AtomicInteger image = new AtomicInteger();
                    for (int unused = 0; unused < viewUrls.size(); unused++) {
                        ExecutorHelper.submit(() -> {
                            try {
                                int entry = image.getAndIncrement();
                                int fileIndex = fileNameIndex.getAndIncrement();
                                String url = gatherImageUrl(createJsoup(viewUrls.get(entry)));

                                System.out.printf("Downloading (%s) | Page: %s/%s, Image: %s/%s (%s)\r",
                                        name, sitePage + 1, pages, fileIndex + 1, images, calculatePercent(fileIndex + 1, images));

                                FileHelper.saveImage(
                                        path.resolve(fileIndex + "." + SiteHelper.getExtension(url)),
                                        SiteHelper.openConnection(url)
                                );
                                completeJob();
                            } catch (Exception e) {
                                completeJob();
                                handleException(e);
                            }
                        });
                    }
                });
            }
        } catch (Exception e) {
            handleException(e);
            done();
        }
    }

    public abstract Document createJsoup(String url);

    public abstract List<String> gatherViewUrls(Element body);
}
