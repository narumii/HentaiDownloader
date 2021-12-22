package cafe.ethyr.hentaidl.helper;

import java.io.File;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.jar.JarFile;

public final class FileHelper {

    private static final Path USER_HOME = Path.of(System.getProperty("user.home"));
    private static final Path DOWNLOADER_HOME = Path.of(USER_HOME.toString(), ".hentaidownloader");
    private static final Path DOWNLOADER_TEMP = Path.of(DOWNLOADER_HOME.toString(), "temp", String.valueOf(System.currentTimeMillis()));

    static {
        if (!DOWNLOADER_HOME.toFile().exists())
            DOWNLOADER_HOME.toFile().mkdirs();

        if (!DOWNLOADER_TEMP.toFile().exists())
            DOWNLOADER_TEMP.toFile().mkdirs();
    }

    public static File createFile(File dir, String name) {
        return new File(dir, fixPath(name));
    }

    public static void deleteAndCreateDirectory(File file) {
        if (file.exists()) {
            deleteDirectory(file);
        }

        file.mkdirs();
    }

    public static Path computePath(File dir, String file, String extension) {
        return Path.of(dir.getAbsolutePath(), file + "." + extension);
    }

    public static void saveImage(Path path, URLConnection connection) {
        if (connection == null) {
            return;
        }

        try (InputStream stream = connection.getInputStream()) {
            Files.copy(stream, path);
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

    public static void deleteDirectory(File file) {
        for (File parentFile : file.listFiles()) {
            if (parentFile.isDirectory()) {
                deleteDirectory(parentFile);
            } else {
                parentFile.delete();
            }
        }
        file.delete();
    }

    public static byte[] loadFileFromProgram(String name) {
        try (JarFile jarFile = new JarFile(FileHelper.class.getProtectionDomain().getCodeSource().getLocation().getFile())) {
            return jarFile.getInputStream(jarFile.getJarEntry(name)).readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String generateDirName(String[] tags, String rating) {
        StringBuilder dirName = new StringBuilder();

        if (check(tags))
            dirName.append(Arrays.toString(tags));

        if (!(rating == null || rating.isBlank() || rating.isEmpty()))
            dirName.append(" _ ").append("(").append(rating).append(")");

        if (dirName.length() <= 0)
            dirName.append("_");

        return dirName.toString();
    }

    private static boolean check(String... array) {
        return (array != null && array.length > 0) && (!array[0].isEmpty() && !array[0].isBlank());
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
