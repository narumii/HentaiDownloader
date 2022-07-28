package cafe.ethyr.hentaidl.downloader.composed;

import cafe.ethyr.hentaidl.booru.Image;
import cafe.ethyr.hentaidl.booru.Site;
import cafe.ethyr.hentaidl.downloader.Downloader;
import cafe.ethyr.hentaidl.downloader.factory.DownloaderType;
import cafe.ethyr.hentaidl.helper.ExecutorHelper;
import cafe.ethyr.hentaidl.helper.FileHelper;
import cafe.ethyr.hentaidl.helper.SiteHelper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class BooruDownloader extends Downloader {

    public BooruDownloader(String path, DownloaderType downloaderType) {
        super(path, downloaderType);
    }

    @Override
    public void readInput(Scanner scanner) {
        System.out.println("Search: \r");
        putArgument("search", scanner.nextLine().trim());
        putArgument("encoded_search", URLEncoder.encode(getArgument("search"), StandardCharsets.UTF_8));

        System.out.println("\nAmount: \r");
        putArgument("amount", scanner.nextInt());

        jobs(getArgument("amount"));
        completionMessage(String.format("Downloaded %s images with \"%s\" tags\r", getArgument("amount"), getArgument("search")));

        System.out.println();
    }

    @Override
    public void downloadImages() {
        try {
            List<Site> sites = createSites();

            Path path = Path.of(this.<String>getArgument("path"));
            if (Files.notExists(path))
                path.toFile().mkdirs();

            AtomicInteger siteIndex = new AtomicInteger();
            for (Site ignored : sites) {
                ExecutorHelper.slaveSubmit(() -> {
                    try {
                        AtomicInteger imageIndex = new AtomicInteger();
                        Site site = sites.get(siteIndex.getAndIncrement());
                        Elements body = Jsoup.connect(site.getUrl()).userAgent(SiteHelper.getUserAgent()).get().getElementsByTag("post");
                        for (int unused = 0; unused < site.getAmount(); unused++) {
                            ExecutorHelper.submit(() -> {
                                int position = imageIndex.getAndIncrement();

                                System.out.printf("Downloading (%s) | Page: %s/%s, Image: %s/%s (%s)\r", this.<String>getArgument("search"), site.getPage() + 1, sites.size(), position + 1, site.getAmount(), calculatePercent(position + 1, site.getAmount()));
                                getImage(position, body).ifPresent(image -> FileHelper.saveImage(
                                        Path.of(path.toString(), image.getImage()), //lolibooru BEST
                                        SiteHelper.openConnection(image.getFileUrl())
                                ));
                                completeJob();
                            });
                        }
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

    public List<Site> createSites() {
        return SiteHelper.createSites(getArgument("amount"), 1000, fixUrl(downloaderType.getApi()), getArgument("encoded_search"));
    }

    public String fixUrl(String string) {
        return string;
    }

    public Optional<Image> getImage(int position, Elements elements) {
        if (elements.size() <= position)
            return Optional.empty();

        Element post = elements.get(position);
        if (post == null)
            return Optional.empty();

        String fileUrl = post.attr("file_url");
        return Optional.of(new Image(fileUrl.substring(fileUrl.lastIndexOf('/')), fileUrl));
    }
}
