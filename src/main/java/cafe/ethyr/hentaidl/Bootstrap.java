package cafe.ethyr.hentaidl;

import cafe.ethyr.hentaidl.downloader.Downloader;
import cafe.ethyr.hentaidl.downloader.factory.DownloaderFactory;
import cafe.ethyr.hentaidl.downloader.factory.DownloaderType;
import cafe.ethyr.hentaidl.helper.PropertiesHelper;

import java.util.Arrays;
import java.util.Optional;
import java.util.Scanner;

public class Bootstrap {

    public static void main(String... args) {
        Optional<Downloader> downloaderOptional = Optional.empty();
        Scanner scanner = new Scanner(System.in);
        System.out.println("HentaiDownloader created by narumi ( https://github.com/narumii )");
        System.out.println("Configuration: " + PropertiesHelper.getPath());
        System.out.println("Supported sites: " + Arrays.toString(DownloaderType.values()) + "\n");

        do {
            if (downloaderOptional.isPresent() && !downloaderOptional.get().isDone()) {
                continue;
            }

            try {
                System.out.println("\n------------------------------------");

                System.out.println("Site name: \r");
                String name = scanner.next();

                scanner.nextLine();  //JAVA IS THE BEST, NO DOUBT

                System.out.println("\nImages dir: \r");
                String dir = scanner.nextLine();

                System.out.println();

                downloaderOptional = DownloaderFactory.fetch(name, dir);
                downloaderOptional.ifPresentOrElse(downloader -> {
                    downloader.readInput(scanner);
                    downloader.downloadImages();
                }, () -> {
                    System.out.printf("No downloader by name %s was found", name);
                    scanner.reset();
                });
            } catch (Exception e) {
                scanner.reset();
            }
        } while (true);
    }
}
