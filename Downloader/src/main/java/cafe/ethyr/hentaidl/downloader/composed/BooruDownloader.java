package cafe.ethyr.hentaidl.downloader.composed;

import cafe.ethyr.hentaidl.booru.Site;
import cafe.ethyr.hentaidl.downloader.Downloader;
import cafe.ethyr.hentaidl.downloader.factory.DownloaderType;
import cafe.ethyr.hentaidl.helper.ExecutorHelper;
import cafe.ethyr.hentaidl.helper.FileHelper;
import cafe.ethyr.hentaidl.helper.SiteHelper;
import cafe.ethyr.hentaidl.helper.parser.TagsParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

//GelBooru, SafeBooru, Rule34, KonaChan, Yande.re, DanBooru, RealBooru
public abstract class BooruDownloader extends Downloader {

    public BooruDownloader(String path, DownloaderType downloaderType) {
        super(path, downloaderType);
    }

    @Override
    public void readInput(Scanner scanner) {
        System.out.print("Tags: ");
        putArgument("raw_tags", scanner.nextLine().replace(" ", "_").split(","));

        System.out.print("Rating: ");
        putArgument("rating", scanner.nextLine());

        System.out.print("Amount: ");
        putArgument("amount", scanner.nextInt());

        putArgument("tags", TagsParser.parse(getArgument("raw_tags"), getArgument("rating")));
        System.out.println();
    }

    @Override
    public void downloadImages() {
        List<Site> sites = createSites();

        Path path = Path.of(getArgument("path"), FileHelper.generateDirName(getArgument("raw_tags"), getArgument("rating")));
        FileHelper.deleteAndCreateDirectory(path.toFile());

        String tags = this.<String>getArgument("tags").replace("+", " ");
        AtomicInteger siteIndex = new AtomicInteger();
        AtomicInteger fileNameIndex = new AtomicInteger();

        for (Site ignored : sites) {
            ExecutorHelper.slaveSubmit(() -> {
                try {
                    AtomicInteger imageIndex = new AtomicInteger();
                    Site site = sites.get(siteIndex.getAndIncrement());
                    Elements body = Jsoup.connect(site.getUrl()).get().getElementsByTag("post");

                    for (int unused = 0; unused < site.getAmount(); unused++) {
                        ExecutorHelper.submit(() -> {
                            int position = imageIndex.getAndIncrement();

                            System.out.printf("Downloading (%s) | Page: %s/%s, Image: %s/%s (%s)\r", tags, site.getPage() + 1, sites.size(), position + 1, site.getAmount(), calculatePercent(position + 1, site.getAmount()));
                            getImage(position, body).ifPresent(image -> FileHelper.saveImage(
                                    FileHelper.computePath(path.toFile(), String.valueOf(fileNameIndex.getAndIncrement()), SiteHelper.getExtension(image)),
                                    SiteHelper.openConnection(image)
                            ));
                        });
                    }
                    completeJob(imageIndex, site.getAmount());
                } catch (Exception e) {
                    handleException(e);
                }
            });
        }

        completeJob(siteIndex, siteIndex.get());
        complete(String.format("Downloaded %s images with %s tags\r", getArgument("amount"), tags), sites.size() * 2);
    }

    public List<Site> createSites() {
        return SiteHelper.createSites(getArgument("amount"), 1000, fixUrl(downloaderType.getApi()), getArgument("tags"));
    }

    public String fixUrl(String string) {
        return string;
    }

    public Optional<String> getImage(int position, Elements elements) {
        if (elements.size() <= position)
            return Optional.empty();

        Element post = elements.get(position);
        if (post == null)
            return Optional.empty();

        return Optional.of(post.attr("file_url"));
    }
}
