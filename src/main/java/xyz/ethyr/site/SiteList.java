package xyz.ethyr.site;

import lombok.Getter;

@Getter
public enum SiteList {

    GELLBOORU(""),
    DANBOORU(""),
    YANERE(""),
    LOLISLIFE(""),
    NEKOSLIFE(""),
    KONACHAN(""),
    NINEHENTAI(""),
    NHENTAI("");

    final String parrten;

    SiteList(final String pattern) {
        this.parrten = pattern;
    }
}
