package xyz.ethyr.downloader;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.Optional;
import xyz.ethyr.downloader.impl.DanBooruDownloader;
import xyz.ethyr.downloader.impl.EHentaiDownloader;
import xyz.ethyr.downloader.impl.GelBooruDownloader;
import xyz.ethyr.downloader.impl.KonachanDownloader;
import xyz.ethyr.downloader.impl.NHentaiDownloader;
import xyz.ethyr.downloader.impl.NekosLifeDownloader;
import xyz.ethyr.downloader.impl.NineHentaiDownloader;
import xyz.ethyr.downloader.impl.Rule34Downloader;
import xyz.ethyr.downloader.impl.SafeBooruDownloader;
import xyz.ethyr.downloader.impl.YandereDownloader;

//jak ktos chce dodac downloadera to chyba robi forka?
public enum DownloaderType {

  DANBOORU("danbooru", DanBooruDownloader.class),
  EHENTAI("ehentai", EHentaiDownloader.class),
  GELBOORU("gelbooru", GelBooruDownloader.class),
  KONACHAN("konachan", KonachanDownloader.class),
  NEKOSLIFE("nekoslife", NekosLifeDownloader.class),
  NHENTAI("nhentai", NHentaiDownloader.class),
  NINEHENTAI("ninehentai", NineHentaiDownloader.class),
  RULE34("rule34", Rule34Downloader.class),
  SAFEBOORU("safebooru", SafeBooruDownloader.class),
  YANDERE("yandere", YandereDownloader.class);

  private final String name;
  private MethodHandle constructor;

  DownloaderType(String name, Class<?> clazz) {
    this.name = name;
    try {
      this.constructor = MethodHandles.publicLookup()
          .unreflectConstructor(clazz.getDeclaredConstructors()[0]);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  public static Optional<DownloaderType> getByName(String name) {
    return Arrays.stream(values())
        .filter(downloader -> downloader.getName().equalsIgnoreCase(name))
        .findFirst();
  }

  public String getName() {
    return name;
  }

  public MethodHandle getConstructor() {
    return constructor;
  }
}
