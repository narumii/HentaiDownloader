package cafe.ethyr.hentaidl.downloader.factory;

import cafe.ethyr.hentaidl.downloader.impl.booru.*;
import cafe.ethyr.hentaidl.downloader.impl.ehentai.EHentaiDownloader;
import cafe.ethyr.hentaidl.downloader.impl.ehentai.ExHentaiDownloader;
import cafe.ethyr.hentaidl.downloader.impl.gallery.*;
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
            //+
            //"&api_key={api_key}" +
            //"&login={login}"
    ),

    AFT("allthefallen", AllTheFallenDownloader.class, "https://booru.allthefallen.moe/posts.xml?" +
            "page=%s" +
            "&limit=%s" +
            "&tags=%s"
    ),

    HYPNO("hypnohub", HypnoHubDownloader.class, "https://hypnohub.net/index.php?page=dapi&s=post&q=index" +
            "&pid=%s" +
            "&limit=%s" +
            "&tags=%s"
    ),

    BLEACH("bleachbooru", BleachBooruDownloader.class, "https://bleachbooru.org/post.xml?" +
            "page=%s" +
            "&limit=%s" +
            "&tags=%s"
    ),

    LOLI("lolibooru", LoliBooruDownloader.class, "https://lolibooru.moe/post/index.xml?" +
            "page=%s" +
            "&limit=%s" +
            "&tags=%s"
    ),

    TBIB("tbib", TbibDownloader.class, "https://tbib.org/index.php?page=dapi&s=post&q=index" +
            "&pid=%s" +
            "&limit=%s" +
            "&tags=%s"
    ),

    EHENTAI("ehentai", EHentaiDownloader.class, null),
    NEKOSLIFE("nekoslife", NekosLifeDownloader.class, "https://nekos.life/api/v2/img/%s"),
    NHENTAI("nhentai", NHentaiDownloader.class, "https://i.nhentai.net/galleries/%s/%s.%s"),
    OLD_NHENTAI("oldnhentai", OldNHentaiDownloader.class, null),
    NINEHENTAI("ninehentai", NineHentaiDownloader.class, "https://cdn.9hentai.ru/images/%s/%s.jpg"),
    NHENTAICOM("nhentai.com", UnoriginalNHentaiDownloader.class, "https://nhentai.com/api/comics/%s/images?lang=%s&nsfw=true"),
    PURURIN("pururin", PururinDownloader.class, "https://cdn.pururin.to/assets/images/data/%s/%s.jpg"),
    EXHENTAI("exhentai", ExHentaiDownloader.class, null),
    IMHENTAI("imhentai", IMHentaiDownloader.class, null),
    ASMHENTAI("asmhentai", ASMHentaiDownloader.class, null),
    HENTAIFOX("hentaifox", HentaiFoxDownloader.class, null);

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
                .filter(downloader -> downloader.name().toLowerCase(Locale.ROOT).startsWith(name.toLowerCase(Locale.ROOT))
                        || downloader.getName().toLowerCase(Locale.ROOT).startsWith(name.toLowerCase(Locale.ROOT)))
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
