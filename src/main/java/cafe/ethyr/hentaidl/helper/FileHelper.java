package cafe.ethyr.hentaidl.helper;

import java.io.File;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadLocalRandom;
import java.util.jar.JarFile;

public final class FileHelper {

    private static final ForkJoinPool FORK_JOIN_POOL = new ForkJoinPool(1);

    private static final Path USER_HOME = Path.of(System.getProperty("user.home"));
    private static final Path DOWNLOADER_HOME = Path.of(USER_HOME.toString(), ".hentaidownloader");
    private static final Path DOWNLOADER_TEMP = Path.of(DOWNLOADER_HOME.toString(), "temp");

    static {
        if (!DOWNLOADER_HOME.toFile().exists())
            DOWNLOADER_HOME.toFile().mkdirs();

        //Fix my bad sorry
        if (DOWNLOADER_TEMP.toFile().exists())
            deleteDirectory(DOWNLOADER_TEMP.toFile());

        //if (!DOWNLOADER_TEMP.toFile().exists())
        //    DOWNLOADER_TEMP.toFile().mkdirs();
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

    //Dude WTF
    public static void saveImage(Path path, URLConnection connection) {
        if (connection == null) {
            return;
        }

        boolean flag = false;
        Path oldPath = path;

        if (Files.exists(path)) {
            path = Path.of(path.toString().replaceFirst("\\.", System.currentTimeMillis() % 1000000 + "" + ThreadLocalRandom.current().nextInt() + "."));
            flag = true;
        }

        try (InputStream stream = connection.getInputStream()) {
            Files.copy(stream, path);

            boolean finalFlag = flag;
            Path finalPath = path;
            FORK_JOIN_POOL.execute(() -> {
                try {
                    if (finalFlag && isSameFile(finalPath, oldPath)) {
                        Files.delete(finalPath);
                    }
                } catch (Exception e) {
                }
            });
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

    public static boolean isSameFile(Path first, Path second) {
        return (first.toFile().length() == second.toFile().length()) || checkSum(first.toString()).equals(second.toString());
    }

    public static String checkSum(String file) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            try (DigestInputStream inputStream = new DigestInputStream(Files.newInputStream(Paths.get(file)), messageDigest)) {
                byte[] buffer = new byte[4096];
                while (inputStream.read(buffer) != -1) {
                }

                return new BigInteger(1, messageDigest.digest()).toString(16);
            }
        } catch (Exception e) {
            throw new RuntimeException("Can't create file checksum", e);
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
