package cafe.ethyr.hentaidl.downloader.impl.gallery;

import cafe.ethyr.hentaidl.downloader.composed.GalleryDownloader;
import cafe.ethyr.hentaidl.downloader.factory.DownloaderType;
import cafe.ethyr.hentaidl.executor.ExecutorHelper;
import cafe.ethyr.hentaidl.helper.FileHelper;
import cafe.ethyr.hentaidl.helper.SiteHelper;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Element;

import java.nio.file.Path;

public class UnoriginalNHentaiDownloader extends GalleryDownloader {

    public UnoriginalNHentaiDownloader(String path) {
        super(path, DownloaderType.NHENTAICOM);
    }

    @Override
    public void downloadImages() {
        //Idk know why jsoup, urlconnection, httpclient returning 403
        //Thats why we need fucking ~4mb shit inside downloader
        try (Response response = SiteHelper.CLIENT.newCall(new Request.Builder()
                .addHeader("User-Agent", SiteHelper.getUserAgent())
                .url(this.<String>getArgument("url"))
                .get()
                .build()).execute()) {

            JSONObject json = new JSONObject(response.body().string());

            String name = json.getJSONObject("comic").getString("title");
            int pageAmount = json.getJSONObject("comic").getInt("pages");
            JSONArray pages = json.getJSONArray("images");

            Path path = Path.of(getArgument("path"), FileHelper.fixPath(name));
            FileHelper.deleteAndCreateDirectory(path);
            completionMessage(String.format("Downloaded %s\r", name));

            System.out.println("Name: " + name);
            System.out.println("Pages: " + pageAmount);

            jobs(pages.length());
            for (int i = 0; i < pages.length(); i++) {
                final JSONObject object = pages.getJSONObject(i);
                ExecutorHelper.submit(() -> {
                    try {
                        int page = object.getInt("page");
                        System.out.printf("Downloading (%s) | Page: %s/%s (%s)\r",
                                name, page, pageAmount, calculatePercent(page, pageAmount));

                        String url = object.getString("source_url");
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

    @Override
    protected String fixUrl(String url) {
        String language = "all";
        String comic;

        if (url.contains("nhentai.com")) {
            int languageStart = url.indexOf('/', 10) + 1;

            language = url.substring(languageStart, url.indexOf('/', languageStart + 1));
            comic = url.substring(url.lastIndexOf('/') + 1);
        } else {
            comic = url;
        }

        putArgument("language", language);
        putArgument("id", comic);
        return String.format(downloaderType.getApi(), comic, language);
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
