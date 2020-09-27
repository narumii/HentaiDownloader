package xyz.ethyr;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class Bootstrap {


    public static void main(final String... args) {
        final Map<Integer, Integer> imagePages = new HashMap<>();
        final int amount = 353;
        final int pages = amount / 1000;
        IntStream.range(0, pages + 1).forEach(page -> {
            if (pages == page)
                imagePages.put(page, amount - ((page) * 1000));
            else
                imagePages.put(page, 1000);
        });
        imagePages.forEach((page, images) -> System.out.println(page + " -> " + images));


        /*
        final NineHentaiDownloader downloader = new NineHentaiDownloader( new File("lol"), new Scanner(System.in));

        downloader.downloadImages();*/
    }
}
