package xyz.ethyr.downloader;

import lombok.Getter;

import java.io.File;
import java.util.Scanner;

@Getter
public abstract class Downloader {

    private final File dir;
    private final Scanner scanner;

    public Downloader(File dir, Scanner scanner) {
        this.dir = dir;
        this.scanner = scanner;
        if (!dir.exists())
            dir.mkdir();
    }

    public abstract void downloadImages();
}
