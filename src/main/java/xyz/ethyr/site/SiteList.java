package xyz.ethyr.site;

import lombok.Getter;

@Getter
public enum SiteList {

    GELLBOORU("https://gelbooru.com/index.php?page=dapi&s=post&q=index&pid=%page%&limit=%amount%&tags=%tag%&api_key=%apikey%&user_id=%user%"),
    DANBOORU(""),
    YANERE(""),
    LOLISLIFE(""),
    NEKOSLIFE(""),
    KONACHAN(""),
    NINEHENTAI("https://cdn.9hentai.com/images/%id%/"),
    NHENTAI("");

    private final String pattern;

    SiteList(final String pattern) {
        this.pattern = pattern;
    }
}
