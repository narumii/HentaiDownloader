package cafe.ethyr.hentaidl.downloader.impl.other;

import cafe.ethyr.hentaidl.downloader.DownloadException;
import cafe.ethyr.hentaidl.downloader.composed.GalleryDownloader;
import cafe.ethyr.hentaidl.downloader.factory.DownloaderType;
import cafe.ethyr.hentaidl.helper.ExecutorHelper;
import cafe.ethyr.hentaidl.helper.FileHelper;
import cafe.ethyr.hentaidl.helper.SiteHelper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class AsianSisterDownloader extends GalleryDownloader {

    private static final String SEARCH_URL = "https://asiansister.com/search.php?q=%s";
    private static final String TAG_URL = "https://asiansister.com/tag.php?tag=%s";

    public AsianSisterDownloader(String path) {
        super(path, DownloaderType.ASIANSISTER);
    }

    @Override
    public void readInput(Scanner scanner) {
        System.out.print("Search/Tag/Link: ");

        String url;
        switch (scanner.nextLine().toLowerCase(Locale.ROOT)) {
            case "search": {
                System.out.print("Query: ");
                url = String.format(SEARCH_URL, scanner.nextLine().replace(" ", "_"));
                break;
            }
            case "tag": {
                System.out.print("Tag: ");
                url = String.format(TAG_URL, scanner.nextLine().replace(" ", "_"));
                break;
            }
            case "link": {
                System.out.print("Link: ");
                url = scanner.nextLine();
                break;
            }
            default:
                throw new DownloadException();
        }

        if (url == null)
            throw new DownloadException();

        putArgument("url", url);
        System.out.println();
    }

    @Override
    public void downloadImages() {
        try {
            Element body = Jsoup.connect(getArgument("url")).get().body();

            String name = parseName(body);
            int pages = parsePages(body);
            Path path = Path.of(getArgument("path"), name);

            List<String> links = createLinks(getArgument("url"), pages, body);

            System.out.println("Query/Tag: " + name);
            System.out.println("Pages: " + pages);
            System.out.println("Galleries: " + links.size());
            System.out.println();

            AtomicInteger index = new AtomicInteger();
            for (int i = 0; i < links.size(); i++) {
                ExecutorHelper.slaveSubmit(() -> downloadGallery(path, links.get(index.getAndIncrement()), index.get(), links.size()));
            }

            completeJob(index, links.size());
            complete(String.format("Downloaded %s\r", this.<String>getArgument("url")), links.size() + 1);
        } catch (Exception e) {
            handleException(e);
        }
    }

    private void downloadGallery(Path path, String url, int gallery, int galleries) {
        try {
            Element body = Jsoup.connect(url).get().body();
            Path dir = Path.of(FileHelper.fixPath(path.toString()), FileHelper.fixPath(parseName(body).replace('/', '_').replace('\\', '_')));
            Elements elements = body.getElementsByClass("lazyload showMiniImage");

            FileHelper.deleteAndCreateDirectory(dir.toFile());
            AtomicInteger index = new AtomicInteger();
            for (int j = 0; j < elements.size(); j++) {
                ExecutorHelper.submit(() -> {
                    int i = index.getAndIncrement();
                    System.out.printf("Downloading (%s) | Image: %s/%s (%s) | Gallery: %s/%s\r", url,
                            i + 1, elements.size(), calculatePercent(i + 1, elements.size()), gallery, galleries);

                    String dataUrl = elements.get(i).attr("dataurl");
                    FileHelper.saveImage(Path.of(dir.toString(), FileHelper.fixPath(dataUrl.substring(dataUrl.lastIndexOf('/') + 1))),
                            SiteHelper.openConnection("https://asiansister.com/" + dataUrl.replace("imageimages/", "images/")));
                });
            }

            completeJob(index, elements.size() - 1);
        } catch (Exception e) {
            handleException(e);
        }
    }

    private List<String> createLinks(String url, int pages, Element body) {
        try {
            if (pages != 0) {
                List<String> links = new ArrayList<>();
                for (int i = 1; i <= pages; i++) {
                    links.addAll(createLinks0(url + "&page=" + i, Jsoup.connect(url + "&page=" + i).get().body()));
                }
                return links;
            } else {
                return createLinks0(url, body);
            }
        } catch (Exception e) {
            throw new DownloadException(e);
        }
    }

    private List<String> createLinks0(String url, Element body) {
        if (!url.contains("view_")) {
            return body.getElementsByAttribute("href").stream()
                    .map(gallery -> gallery.attr("abs:href"))
                    .filter(link -> link.contains("view_"))
                    .collect(Collectors.toList());
        }

        return Collections.singletonList(url);
    }

    @Override
    protected String fixUrl(String url) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected String parseName(Element body) {
        String text = body.tagName("center").getElementsByTag("h1").first().text();
        return text.contains(": ") ? text.split(": ")[1] : text;
    }

    @Override
    protected int parsePages(Element body) {
        try {
            return Integer.parseInt(body.getElementsByClass("btn page").last().tagName("b").text());
        } catch (Exception e) {
            return 0;
        }
    }
}
