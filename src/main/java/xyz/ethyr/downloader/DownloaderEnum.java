package xyz.ethyr.downloader;

import java.util.Arrays;
import java.util.Optional;
import xyz.ethyr.downloader.impl.EHentaiDownloader;
import xyz.ethyr.downloader.impl.GelBooruDownloader;
import xyz.ethyr.downloader.impl.KonachanDownloader;
import xyz.ethyr.downloader.impl.NHentaiDownloader;
import xyz.ethyr.downloader.impl.NekosLifeDownloader;
import xyz.ethyr.downloader.impl.NineHentaiDownloader;
import xyz.ethyr.downloader.impl.Rule34Downloader;
import xyz.ethyr.downloader.impl.SafeBooruDownloader;
import xyz.ethyr.downloader.impl.YandereDownloader;

public enum DownloaderEnum {

  EHENTAI("ehentai", EHentaiDownloader.class),
  GELBOORU("gelbooru", GelBooruDownloader.class),
  KONACHAN("konachan", KonachanDownloader.class),
  NEKOSLIFE("nekoslife", NekosLifeDownloader.class),
  NHENTAI("nhentai", NHentaiDownloader.class),
  NINEHENTAI("9hentai", NineHentaiDownloader.class),
  RULE34("rule34", Rule34Downloader.class),
  SAFEBOORU("safebooru", SafeBooruDownloader.class),
  YANDERE("yandere", YandereDownloader.class);


  private final String name;
  private final Class<?> clazz;

  DownloaderEnum(String name, Class<?> clazz) {
    this.name = name;
    this.clazz = clazz;
  }

  public static Optional<DownloaderEnum> getByName(String name) {
    return Arrays.stream(values())
        .filter(downloader -> downloader.getName().equalsIgnoreCase(name))
        .findFirst();
  }

  public String getName() {
    return name;
  }

  public Class<?> getClazz() {
    return clazz;
  }
}
