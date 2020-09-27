package xyz.ethyr;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class Bootstrap {


    public static void main(final String... args) {
        final Map<Integer, Integer> xd = new HashMap<>();

        AtomicInteger ai = new AtomicInteger();
        int i = 2452 / 1000;
        IntStream.range(0, i + 1).forEach(x -> {
            if (i == x) {
                xd.put(x, 2452 - (ai.get() * 1000));
            }else {
                ai.set(x + 1);
                xd.put(x, 1000);
            }
        });

        xd.forEach((c, b) -> {
            System.out.println(c + " " + b);
        });
        /*
        final NineHentaiDownloader downloader = new NineHentaiDownloader( new File("lol"), new Scanner(System.in));

        downloader.downloadImages();*/
    }
}
