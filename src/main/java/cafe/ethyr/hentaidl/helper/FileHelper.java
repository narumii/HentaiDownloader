package cafe.ethyr.hentaidl.helper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.jar.JarFile;

public final class FileHelper {

    private static final Path USER_HOME = Path.of(System.getProperty("user.home"));
    private static final Path DOWNLOADER_HOME = USER_HOME.resolve(".hentaidownloader");
    private static final Path DOWNLOADER_TEMP = DOWNLOADER_HOME.resolve("temp");

    static {
        try {
            if (Files.notExists(DOWNLOADER_HOME))
                Files.createDirectories(DOWNLOADER_HOME);

            if (Files.exists(DOWNLOADER_TEMP))
                deleteDirectory(DOWNLOADER_TEMP);
        } catch (Exception e) {
            throw new RuntimeException("Can't create downloader directories");
        }
    }

    public static void deleteAndCreateDirectory(Path path) throws IOException {
        if (Files.exists(path)) {
            deleteDirectory(path);
        }

        Files.createDirectories(path);
    }

    public static void deleteDirectory(Path dir) {
        try {
            Files.walkFileTree(dir, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (Exception e) {
            throw new RuntimeException("Can't delete directory", e);
        }
    }

    public static void saveImage(Path path, URLConnection connection) {
        if (connection == null || Files.exists(path)) {
            return;
        }

        try (InputStream stream = connection.getInputStream()) {
            Files.copy(stream, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveImage(Path path, InputStream inputStream) throws IOException {
        if (inputStream == null || Files.exists(path)) {
            return;
        }

        try (inputStream) {
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String fixPath(String string) {
        return string
                .replace("[", "")
                .replace("]", "")
                .replace("<", "")
                .replace(">", "")
                .replace(":", "")
                .replace("\"", "")
                .replace("|", "")
                .replace("?", "")
                .replace("*", "");
    }

    public static byte[] loadFileFromProgram(String name) {
        try (JarFile jarFile = new JarFile(FileHelper.class.getProtectionDomain().getCodeSource().getLocation().getFile())) {
            return jarFile.getInputStream(jarFile.getJarEntry(name)).readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Path getUserHome() {
        return USER_HOME;
    }

    public static Path getDownloaderHome() {
        return DOWNLOADER_HOME;
    }

    public static Path getDownloaderTemp() {
        return DOWNLOADER_TEMP;
    }
}
