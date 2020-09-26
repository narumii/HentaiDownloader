package xyz.ethyr.conventer.impl;

import lombok.Getter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import xyz.ethyr.conventer.Downloader;

import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
public class NineHentaiDownloader extends Downloader {

    private final String URL = "https://9hentai.com/g/" + "%id%";
    private final String DOWNLOAD = "https://cdn.9hentai.com/images/" + "%id%" + "/" + "%index%" + ".jpg";

    private String doujinshiName;
    private String doujinshiId;
    private int pages;

    public NineHentaiDownloader(final File dir, final Scanner scanner) {
        super(dir, scanner);

        if (dir.exists())
            dir.delete();

        dir.mkdir();
    }

    public void parserArguments() {
        System.out.print("Doujinshi ID: ");
        this.doujinshiId = String.valueOf(getScanner().nextInt());
    }

    public void downloadImages() {
        final ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.submit(() -> {
            try {
                final Document element = Jsoup.connect(URL.replace("%id%", this.doujinshiId)).get();
                System.out.println("Doujinshi Name: " + setFolderName(element.body().getElementById("info").getElementsByTag("h1").text()));
                System.out.println("Doujinshi Pages: " + setPages(Integer.parseInt(element.body().getElementsContainingOwnText("pages").text().split(" ")[0])));

                final File file = new File(this.getDir().getPath() + File.separator + doujinshiName.replace("|", "_"));
                if (file.exists()) {
                    for (final File listFile : file.listFiles())
                        listFile.delete();

                    file.delete();
                }

                file.mkdirs();

                for (int i = 1; i != pages + 1; i++) {
                    if (i != pages + 1)
                        System.out.print( ( i != pages ? "Downloading (" + i + ") / (" + (i * 100 / pages) + "%)\r" : "Downloaded.") );

                    final URLConnection connection = new URL(DOWNLOAD.replace("%id%", this.doujinshiId).replace("%index%", String.valueOf(i))).openConnection();
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                    connection.connect();
                    Files.copy(connection.getInputStream(), Paths.get(file.getPath() + File.separator + i + ".jpg"));
                }
            } catch (final Exception e) {
                e.printStackTrace();
            }

            System.exit(-1);
        });
    }

    public String setFolderName(final String doujinshiName) {
        this.doujinshiName = doujinshiName;
        return doujinshiName;
    }

    public int setPages(final int pages) {
        this.pages = pages;
        return pages;
    }
}
