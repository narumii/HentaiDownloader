package cafe.ethyr.hentaidl.downloader.factory;

import cafe.ethyr.hentaidl.downloader.Downloader;

import java.util.Optional;

public final class DownloaderFactory {

    public static Optional<Downloader> fetch(String name, String dir) {
        return DownloaderType.getByName(name).map(type -> fetch0(type, dir));
    }

    private static Downloader fetch0(DownloaderType type, String dir) {
        try {
            return (Downloader) type.getConstructor().invoke(dir);
        } catch (Throwable throwable) {
            return null;
        }
    }
}
