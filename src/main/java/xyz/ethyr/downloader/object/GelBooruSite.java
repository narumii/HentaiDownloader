package xyz.ethyr.downloader.object;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public class GelBooruSite {

  private final String url;
  private final int page;
  private final int amount;

}
