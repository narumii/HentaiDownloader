package xyz.ethyr;

import java.io.File;
import java.util.Scanner;
import xyz.ethyr.downloader.impl.GelBooruDownloader;
import xyz.ethyr.downloader.impl.NineHentaiDownloader;
import xyz.ethyr.downloader.impl.Rule34Downloader;

public class Bootstrap {


  public static void main(final String... args) {
    //inal NineHentaiDownloader downloader = new NineHentaiDownloader(new File("lol"), new Scanner(System.in));

    final Rule34Downloader downloader = new Rule34Downloader(new File("lol"),
       new Scanner(System.in));
    downloader.downloadImages();
  }
}
