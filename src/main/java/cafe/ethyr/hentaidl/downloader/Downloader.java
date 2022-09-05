package cafe.ethyr.hentaidl.downloader;

import cafe.ethyr.hentaidl.downloader.factory.DownloaderType;
import cafe.ethyr.hentaidl.helper.PropertiesHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Downloader {

    private static final boolean debug = Boolean.parseBoolean(PropertiesHelper.getProperty("debug"));
    protected final DownloaderType downloaderType;
    private final Map<String, Object> arguments = new HashMap<>();

    private final AtomicInteger completedJobs = new AtomicInteger();
    private String completionMessage;
    private int expectedJobs;
    private boolean isDone;

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

    public void completionMessage(String completionMessage) {
        this.completionMessage = completionMessage;
    }

    public void jobs(int expectedJobs) {
        this.expectedJobs = expectedJobs;
    }

    protected void done() {
        this.isDone = true;
        this.arguments.clear();
        System.out.print(completionMessage);
    }

    public boolean isDone() {
        return isDone /*|| (expectedJobs == completedJobs)*/;
    }

    public void completeJob() {
        if (expectedJobs == completedJobs.incrementAndGet()) {
            done();
        }
    }

    public DownloaderType type() {
        return downloaderType;
    }
}
