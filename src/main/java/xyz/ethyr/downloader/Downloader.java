package xyz.ethyr.downloader;

import java.io.File;
import java.util.Scanner;
import lombok.Getter;
import xyz.ethyr.util.FileUtil;

@Getter
public abstract class Downloader {

    private final File dir;
    private final Scanner scanner;

    public Downloader(File dir, Scanner scanner) {
        this.dir = dir;
        this.scanner = scanner;
        FileUtil.deleteAndCreateDirectory(dir);
    }

    public abstract void downloadImages();
}
