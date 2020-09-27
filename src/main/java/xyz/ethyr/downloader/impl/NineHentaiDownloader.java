package xyz.ethyr.downloader.impl;

import lombok.Getter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import xyz.ethyr.downloader.Downloader;
import xyz.ethyr.util.ExecutorUtil;
import xyz.ethyr.util.FileUtil;

import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

@Getter
public class NineHentaiDownloader extends Downloader {

    private static final String MAIN_URL = "https://9hentai.com/g/%s";
    private static final String DOWNLOAD_URL = "https://cdn.9hentai.com/images/%s/%s.jpg";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11";

    private final String doujinshiId;

    public NineHentaiDownloader(final File dir, final Scanner scanner) {
        super(dir);
        System.out.print("Doujinshi ID: ");
        this.doujinshiId = String.valueOf(scanner.nextInt());
    }

    @Override
    public void downloadImages() {
        ExecutorUtil.submit(() -> {
            try {
                final Document element = Jsoup.connect(String.format(MAIN_URL, this.doujinshiId)).get();
                final String doujinshiName = element.body().getElementById("info").getElementsByTag("h1").text();
                final int doujinshiPages = Integer.parseInt(element.body().getElementsContainingOwnText("pages").text().split(" ")[0]);
                System.out.println("Doujinshi Name: " + doujinshiName);
                System.out.println("Doujinshi Pages: " + doujinshiPages);

                final File file = new File(this.dir, doujinshiName.replace("|", "_"));
                FileUtil.deleteAndCreateDirectory(file);
                for (int i = 1; i != doujinshiPages + 1; i++) {
                    if (i != doujinshiPages + 1)
                        System.out.print((i != doujinshiPages ? "Downloading (" + i + ") / (" + (i * 100 / doujinshiPages) + "%)\r" : "Downloaded."));

                    final URLConnection connection = new URL(String.format(DOWNLOAD_URL, this.doujinshiId, i)).openConnection();
                    connection.setRequestProperty("User-Agent", USER_AGENT);
                    connection.connect();
                    Files.copy(connection.getInputStream(), Paths.get(file.getPath(), i + ".jpg"));
                }
            } catch (final Exception e) {
                e.printStackTrace();
            }
        });
    }
}
