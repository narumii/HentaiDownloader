package cafe.ethyr.hentaidl.downloader.impl.other;

import cafe.ethyr.hentaidl.downloader.DownloadException;
import cafe.ethyr.hentaidl.downloader.Downloader;
import cafe.ethyr.hentaidl.downloader.factory.DownloaderType;
import cafe.ethyr.hentaidl.helper.FileHelper;
import cafe.ethyr.hentaidl.helper.PropertiesHelper;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.tika.Tika;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

/*
    I don't know why they created site in this way whdfuiwahfiwadiopeuciorwuecgiofw4ueorf
    And i thing this downloader can be done a lot better but my experience with selenium is 0 xd
 */
public class HitomiDownloader extends Downloader {

    private static boolean setupDriver;

    public HitomiDownloader(String path) {
        super(path, DownloaderType.HITOMI);

        System.err.println("! This downloader is bugged because of way that hitomi is created. In my opinion there is a lot of better sites.");
        System.err.println();

        if (!Path.of(PropertiesHelper.getProperties().getProperty("chromium_browser_path")).toFile().exists())
            throw new DownloadException();

        if (!setupDriver) {
            WebDriverManager.chromedriver().setup();
            //Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF); //kil mi pls (how do i can disable this fucking ugly logs? ioejfowiajfioa)
            setupDriver = true;
        }
    }

    @Override
    public void readInput(Scanner scanner) {
        System.out.print("Link: ");
        fixUrl(scanner.nextLine());

        System.out.println();
    }

    @Override
    public void downloadImages() {
        ChromeDriver driver = null;
        try {
            Element element = Jsoup.connect(getArgument("url")).get().body();
            Elements galleryInfo = element.getElementsByClass("gallery");
            String name = galleryInfo.select("h1").text();

            driver = createDriver();
            driver.manage().window().maximize();
            driver.manage().window().fullscreen();
            driver.get(galleryInfo.select("h1").select("a").attr("abs:href"));

            int images = getImages(driver);
            if (images == 0)
                throw new DownloadException();

            System.out.println();
            System.out.println("Name: " + name);
            System.out.println("Pages: " + images);

            Path path = Path.of(getArgument("path"), name);
            FileHelper.deleteAndCreateDirectory(path.toFile());

            tryFetch(driver, name, images); //basically loading images to disk cache xd

            AtomicInteger index = new AtomicInteger();
            for (Path filePath : FileHelper.searchForFiles(FileHelper.getDownloaderTemp())) { //Ye disk cache
                Files.copy(filePath, Path.of(path.toString() + "/" + index.incrementAndGet() + "." + new Tika().detect(filePath).split("/")[1]));
            }

            System.out.printf("Downloaded %s\r", name);
        } catch (Exception e) {
            handleException(e);
        } finally {
            if (driver != null) {
                driver.close();
                driver.quit();
            }

            FileHelper.deleteDirectory(FileHelper.getDownloaderTemp().toFile());
        }

        done();
        System.out.println();
    }

    private void tryFetch(ChromeDriver webDriver, String name, int images) {
        String readerLink = webDriver.getCurrentUrl().substring(0, webDriver.getCurrentUrl().lastIndexOf('#') + 1);

        for (int i = 0; i < images + 1; i++) {
            webDriver.get(readerLink + (i + 1));
            new WebDriverWait(webDriver, Duration.ofSeconds(10))
                    .until(driver -> driver.findElement(By.className("lillie")).isDisplayed());

            String link = null;
            try { //maybe some "if" replacement?
                link = webDriver.findElement(By.tagName("picture")).findElement(By.tagName("source")).getAttribute("srcset");
            } catch (Exception e) {
                link = webDriver.findElement(By.className("lillie")).getAttribute("src");
            } finally {
                if (link != null) {
                    webDriver.get(link);
                    new WebDriverWait(webDriver, Duration.ofSeconds(10))
                            .until(driver -> driver.findElement(By.tagName("img")).isDisplayed());

                    webDriver.navigate().back();
                    System.out.printf("Downloading (%s) | Image: %s/%s (%s)\r", name, i, images, calculatePercent(i, images));
                }
            }
        }
    }

    private int getImages(ChromeDriver driver) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(webDriver -> webDriver.findElement(By.className("container")).isDisplayed());

            return driver.findElement(By.className("input-medium")).findElements(By.tagName("option")).size();
        } catch (Exception e) {
            throw new DownloadException();
        }
    }

    private void fixUrl(String url) {
        if (!url.startsWith("https://hitomi.la") && !url.startsWith("hitomi.la"))
            throw new DownloadException();

        putArgument("url", url);
    }

    private ChromeDriver createDriver() {
        ChromeOptions options = new ChromeOptions();
        options.setBinary(PropertiesHelper.getProperty("chromium_browser_path"));
        options.setHeadless(true);
        //options.setLogLevel(ChromeDriverLogLevel.OFF);
        options.addArguments(
                "--disk-cache=true",
                "--disable-dev-shm-usage",
                "--disable-crash-reporter",
                "--disk-cache-dir=" + FileHelper.getDownloaderTemp().toString());

        return new ChromeDriver(options);
    }

}
