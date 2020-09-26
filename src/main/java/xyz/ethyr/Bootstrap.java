package xyz.ethyr;

import xyz.ethyr.conventer.impl.NineHentaiDownloader;

import java.io.File;
import java.util.Scanner;

public class Bootstrap {

    public static void main(final String... args) {
        final NineHentaiDownloader downloader = new NineHentaiDownloader( new File("lol"), new Scanner(System.in));

        downloader.parserArguments();
        downloader.downloadImages();
    }
}
