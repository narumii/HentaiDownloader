package xyz.ethyr.downloader.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import xyz.ethyr.downloader.Downloader;

import java.io.File;
import java.util.Scanner;

public class GelBooruDownloader extends Downloader {

    private static final String URL  = "https://gelbooru.com/index.php?page=dapi&s=post&q=index&pid=" + "%page%" + "&limit=" + "amount" + "&tags=" + "%tags%" + "&api_key=16d7195f94cd43f7680c2310ec6788f05a6a6e06fbaad1f0e6c55fd284c57f5a&user_id=629393";
    private final List<String> tags = new ArrayList<>();
    private final List<String> blockedTags = new ArrayList<>();
    private final List<String> ratings;
    private final int amount;

    public GelBooruDownloader(final File dir, final Scanner scanner) {
        super(dir);

        System.out.print("Image tags: ");

        for (String s : scanner.nextLine().split(" ")) {
            if (s.startsWith("-"))
                blockedTags.add(s.replace("_", ""));
            else
                tags.add(s);
        }
        ratings = Arrays.asList(scanner.nextLine().split(" "));
        amount = scanner.nextInt();

    }

    @Override
    public void downloadImages() {

    }

    private String getImage() {
        return null;
    }
}
