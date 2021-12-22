package cafe.ethyr.hentaidl.downloader.factory;

import cafe.ethyr.hentaidl.downloader.impl.booru.*;
import cafe.ethyr.hentaidl.downloader.impl.ehentai.EHentaiDownloader;
import cafe.ethyr.hentaidl.downloader.impl.ehentai.ExHentaiDownloader;
import cafe.ethyr.hentaidl.downloader.impl.gallery.IMHentaiDownloader;
import cafe.ethyr.hentaidl.downloader.impl.gallery.NHentaiDownloader;
import cafe.ethyr.hentaidl.downloader.impl.gallery.NineHentaiDownloader;
import cafe.ethyr.hentaidl.downloader.impl.other.AsianSisterDownloader;
import cafe.ethyr.hentaidl.downloader.impl.other.HitomiDownloader;
import cafe.ethyr.hentaidl.downloader.impl.other.NekosLifeDownloader;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

public enum DownloaderType {

    //page, limit, tags

    REALBOORU("realbooru", RealBooruDownloader.class, "https://realbooru.com/index.php?page=dapi&s=post&q=index" +
            "&pid=%s" +
            "&limit=%s" +
            "&tags=%s"
    ),

    GELBOORU("gelbooru", GelBooruDownloader.class, "https://gelbooru.com/index.php?page=dapi&s=post&q=index" +
            "&pid=%s" +
            "&limit=%s" +
            "&tags=%s" +
            "&api_key={api_key}" +
            "&user_id={user_id}"
    ),

    KONACHAN("konachan", KonachanDownloader.class, "https://konachan.com/post.xml?" +
            "page=%s" +
            "&limit=%s" +
            "&tags=%s"
    ),

    RULE34("rule34", Rule34Downloader.class, "https://rule34.xxx/index.php?page=dapi&s=post&q=index" +
            "&pid=%s" +
            "&limit=%s" +
            "&tags=%s"
    ),

    SAFEBOORU("safebooru", SafeBooruDownloader.class, "https://safebooru.org/index.php?page=dapi&s=post&q=index" +
            "&pid=%s" +
            "&limit=%s" +
            "&tags=%s"
    ),

    YANDERE("yandere", YandereDownloader.class, "https://yande.re/post.xml?" +
            "page=%s" +
            "&limit=%s" +
            "&tags=%s"
    ),

    DANBOORU("danbooru", DanBooruDownloader.class, "https://danbooru.donmai.us/posts.xml?" +
            "page=%s" +
            "&limit=%s" +
            "&tags=%s"
    ),

    EHENTAI("ehentai", EHentaiDownloader.class, null),
    NEKOSLIFE("nekoslife", NekosLifeDownloader.class, "https://nekos.life/api/v2/img/%s"),
    NHENTAI("nhentai", NHentaiDownloader.class, null),
    NINEHENTAI("ninehentai", NineHentaiDownloader.class, "https://cdn.9hentai.ru/images/%s/%s.jpg"),
    EXHENTAI("exhentai", ExHentaiDownloader.class, null),
    IMHENTAI("imhentai", IMHentaiDownloader.class, null),
    ASIANSISTER("asiansister", AsianSisterDownloader.class, null),
    HITOMI("hitomi", HitomiDownloader.class, null);

    private final String name;
    private final String api;
    private MethodHandle constructor;


    DownloaderType(String name, Class<?> clazz, String api) {
        this.name = name;
        this.api = api;

        try {
            this.constructor = MethodHandles.publicLookup()
                    .unreflectConstructor(clazz.getDeclaredConstructors()[0]);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static Optional<DownloaderType> getByName(String name) {
        return Arrays.stream(values())
                .filter(downloader -> downloader.getName().toLowerCase(Locale.ROOT).startsWith(name.toLowerCase(Locale.ROOT)))
                .findFirst();
    }

    public String getName() {
        return name;
    }

    public MethodHandle getConstructor() {
        return constructor;
    }

    public String getApi() {
        return api;
    }
}
