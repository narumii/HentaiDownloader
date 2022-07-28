package cafe.ethyr.hentaidl.downloader.impl.other;

import cafe.ethyr.hentaidl.downloader.Downloader;
import cafe.ethyr.hentaidl.downloader.factory.DownloaderType;
import cafe.ethyr.hentaidl.helper.ExecutorHelper;
import cafe.ethyr.hentaidl.helper.FileHelper;
import cafe.ethyr.hentaidl.helper.SiteHelper;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class NekosLifeDownloader extends Downloader {

    private static final String[] ENDPOINTS = new String[]{"femdom", "tickle", "classic", "ngif",
            "erofeet", "meow", "erok", "poke", "les", "v3", "hololewd", "nekoapi_v3.1", "lewdk", "keta",
            "feetg", "nsfw_neko_gif", "eroyuri", "kiss", "8ball", "kuni", "tits", "pussy_jpg", "cum_jpg",
            "pussy", "lewdkemo", "lizard", "slap", "lewd", "cum", "cuddle", "spank", "smallboobs",
            "goose", "Random_hentai_gif", "avatar", "fox_girl", "nsfw_avatar", "hug", "gecg", "boobs",
            "pat", "feet", "smug", "kemonomimi", "solog", "holo", "wallpaper", "bj", "woof", "yuri",
            "trap", "anal", "baka", "blowjob", "holoero", "feed", "neko", "gasm", "hentai", "futanari",
            "ero", "solo", "waifu", "pwankg", "eron", "erokemo"};

    public NekosLifeDownloader(String path) {
        super(path, DownloaderType.NEKOSLIFE);

        System.out.println("Available tags: " + String.join(", ", ENDPOINTS));
        System.out.println();
    }

    @Override
    public void readInput(Scanner scanner) {
        System.out.println("Tag: ");

        String tag;
        do {
            tag = scanner.nextLine().replace(" ", "_");
        } while (!checkTag(tag));

        System.out.println("\nAmount: ");

        putArgument("amount", scanner.nextInt());
        putArgument("tag", tag);
        putArgument("url", String.format(downloaderType.getApi(), tag));

        completionMessage(String.format("Downloaded %s %s images\r", this.<Integer>getArgument("amount"), this.<String>getArgument("tag")));
        System.out.println();
    }

    @Override
    public void downloadImages() {
        Path path = Path.of(this.<String>getArgument("path"));
        if (Files.notExists(path))
            path.toFile().mkdirs();

        int amount = getArgument("amount");
        AtomicInteger index = new AtomicInteger();

        jobs(amount);
        for (int unsued = 0; unsued < amount; unsued++) {
            ExecutorHelper.submit(() -> {
                try {
                    int i = index.getAndIncrement();
                    System.out.printf("Downloading (%s) | Image: %s/%s (%s)\r", getArgument("tag"), i + 1, amount, calculatePercent(i + 1, amount));

                    JSONObject response = SiteHelper.toJson(getArgument("url"));
                    if (response.has("url")) {
                        System.out.print("Malformed response\r");
                        String fileUrl = response.getString("url");
                        FileHelper.saveImage(FileHelper.computePath(path.toFile(), String.valueOf(i) + ThreadLocalRandom.current().nextInt(), SiteHelper.getExtension(fileUrl)), SiteHelper.openConnection(fileUrl));
                    }
                    completeJob();
                } catch (Exception e) {
                    completeJob();
                    handleException(e);
                }
            });
        }
    }

    private boolean checkTag(String string) {
        if (string == null)
            return false;

        for (String endpoint : ENDPOINTS) {
            if (string.equalsIgnoreCase(endpoint)) {
                return true;
            }
        }
        return false;
    }
}
