package cafe.ethyr.hentaidl.downloader;

import cafe.ethyr.hentaidl.downloader.factory.DownloaderType;
import cafe.ethyr.hentaidl.helper.PropertiesHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Downloader {

    private static final boolean debug = Boolean.parseBoolean(PropertiesHelper.getProperty("debug"));

    protected final DownloaderType downloaderType;
    private final Map<String, Object> arguments = new HashMap<>();

    private final AtomicInteger jobsDone = new AtomicInteger();
    private final AtomicBoolean jobDone = new AtomicBoolean();

    public Downloader(DownloaderType downloaderType) {
        this("images", downloaderType);
    }

    public Downloader(String path, DownloaderType downloaderType) {
        this.downloaderType = downloaderType;
        putArgument("path", path);
    }

    public abstract void readInput(Scanner scanner);

    public abstract void downloadImages();

    protected void handleException(Throwable throwable) {
        System.err.println();
        System.err.println("! Error occurred during downloading: " + throwable);
        if (debug)
            throwable.printStackTrace();

        System.err.println();
        done();
    }

    protected final <T> T getArgument(String argument) {
        return (T) arguments.get(argument);
    }

    protected final void putArgument(String argumentName, Object argument) {
        arguments.put(argumentName, argument);
    }

    protected String calculatePercent(int amount, int max) {
        return ((amount * 100) / max) + "%";
    }

    protected void done() {
        this.jobDone.set(true);
        this.arguments.clear();
    }

    public boolean isDone() {
        return jobDone.get();
    }

    public void completeJob(AtomicInteger value, int expectedValue) {
        try {
            while (true) {
                if (value.get() >= expectedValue) {
                    jobsDone.incrementAndGet();
                    break;
                }

                TimeUnit.MILLISECONDS.sleep(100); //anti cpu burn
            }
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void complete(String message, int expectedJobsDone) {
        try {
            while (true) {
                TimeUnit.MILLISECONDS.sleep(100); //anti cpu burn
                if (jobsDone.get() >= expectedJobsDone && !isDone()) {
                    System.out.print(message);
                    done();
                    break;
                }
            }
        } catch (Exception e) {
            handleException(e);
        }
    }
}
