package cafe.ethyr.hentaidl.downloader.impl.gallery;

import cafe.ethyr.hentaidl.data.hitomi.Hitomi;
import cafe.ethyr.hentaidl.downloader.composed.GalleryDownloader;
import cafe.ethyr.hentaidl.downloader.factory.DownloaderType;
import cafe.ethyr.hentaidl.executor.ExecutorHelper;
import cafe.ethyr.hentaidl.helper.FileHelper;
import cafe.ethyr.hentaidl.helper.PropertiesHelper;
import cafe.ethyr.hentaidl.helper.SiteHelper;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.nodes.Element;

import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

//TODO: AVIF/WEBP Converter
//TODO: Animated?
public class HitomiDownloader extends GalleryDownloader {

    private static final String USELESS = "https://hitomi.la/reader/%s.html#1";

    static {
        ExecutorHelper.getJoinPool().init(Boolean.parseBoolean(PropertiesHelper.getProperty("hitomi.safe.rate.limit")) ? 3 : 4, Duration.ofSeconds(1));
    }

    public HitomiDownloader(String path) {
        super(path, DownloaderType.HITOMI);
    }

    @Override
    public void downloadImages() {
        try {
            Hitomi hitomi = new Hitomi(getArgument("id"));

            String name = hitomi.getTitle();
            int pageAmount = hitomi.getFiles().size();

            Path path = Path.of(getArgument("path"), FileHelper.fixPath(name));
            FileHelper.deleteAndCreateDirectory(path);
            completionMessage(String.format("Downloaded %s\r", name));

            System.out.println("Name: " + name);
            System.out.println("Pages: " + pageAmount);

            AtomicInteger index = new AtomicInteger();

            jobs(pageAmount);

            for (final Hitomi.HitomiFile file : hitomi.getFiles()) {
                ExecutorHelper.limitSubmit(() -> {
                    try {
                        int page = index.getAndIncrement();
                        System.out.printf("Downloading (%s) | Page: %s/%s (%s)\r",
                                name, page, pageAmount, calculatePercent(page, pageAmount));

                        String url = file.getUrl(hitomi);
                        Request request = new Request.Builder()
                                .url(HttpUrl.get(url))
                                .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8")
                                .header("referer", "https://hitomi.la")
                                .header("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36")
                                .build();


                        try (Response response = SiteHelper.CLIENT.newCall(request).execute()) {
                            if (!response.isSuccessful())
                                throw new RuntimeException(String.format("%s (status=%s) | %s", url, response.code(), file));

                            FileHelper.saveImage(path.resolve(file.getName() + "." + file.getFixedExt()), response.body().byteStream());
                            completeJob();
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

    @Override
    protected String fixUrl(String url) {
        if (url.contains("hitomi.la"))
            url = url.substring(url.lastIndexOf('-') + 1, url.lastIndexOf('.'));

        putArgument("id", url);
        return String.format(USELESS, url);
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
