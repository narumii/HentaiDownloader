package cafe.ethyr.hentaidl.helper;

import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class PropertiesHelper {

    private static final Properties PROPERTIES = new Properties();
    private static final String PROPERTIES_FILE = "hentaidownloader.properties";
    private static final Path PROPERTIES_PATH = Path.of(FileHelper.getDownloaderHome().toString(), PROPERTIES_FILE);

    static {
        try {
            if (!PROPERTIES_PATH.toFile().exists()) {
                Files.write(PROPERTIES_PATH,
                        FileHelper.loadFileFromProgram(PROPERTIES_FILE)
                );
            }

            PROPERTIES.load(new FileReader(PROPERTIES_PATH.toFile()));
        } catch (Exception e) {
            System.err.println("! Can't read properties: " + e);
        }
    }

    public static String getProperty(String key) {
        return PROPERTIES.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        return PROPERTIES.getProperty(key, defaultValue);
    }

    public static int size() {
        return PROPERTIES.size();
    }

    public static boolean isEmpty() {
        return PROPERTIES.isEmpty();
    }

    public static boolean contains(Object value) {
        return PROPERTIES.contains(value);
    }

    public static boolean containsValue(Object value) {
        return PROPERTIES.containsValue(value);
    }

    public static boolean containsKey(Object key) {
        return PROPERTIES.containsKey(key);
    }

    public static Object get(Object key) {
        return PROPERTIES.get(key);
    }

    public static <T> T getAs(Object key) {
        return (T) PROPERTIES.get(key);
    }

    public static Object put(Object key, Object value) {
        return PROPERTIES.put(key, value);
    }

    public static void putAll(Map<?, ?> t) {
        PROPERTIES.putAll(t);
    }

    public static Object getOrDefault(Object key, Object defaultValue) {
        return PROPERTIES.getOrDefault(key, defaultValue);
    }

    public static Object putIfAbsent(Object key, Object value) {
        return PROPERTIES.putIfAbsent(key, value);
    }

    public static boolean remove(Object key, Object value) {
        return PROPERTIES.remove(key, value);
    }

    public static boolean replace(Object key, Object oldValue, Object newValue) {
        return PROPERTIES.replace(key, oldValue, newValue);
    }

    public static Object replace(Object key, Object value) {
        return PROPERTIES.replace(key, value);
    }

    public static Object computeIfAbsent(Object key, Function<? super Object, ?> mappingFunction) {
        return PROPERTIES.computeIfAbsent(key, mappingFunction);
    }

    public static Object computeIfPresent(Object key, BiFunction<? super Object, ? super Object, ?> remappingFunction) {
        return PROPERTIES.computeIfPresent(key, remappingFunction);
    }

    public static Object compute(Object key, BiFunction<? super Object, ? super Object, ?> remappingFunction) {
        return PROPERTIES.compute(key, remappingFunction);
    }

    public static Object merge(Object key, Object value, BiFunction<? super Object, ? super Object, ?> remappingFunction) {
        return PROPERTIES.merge(key, value, remappingFunction);
    }

    public static Properties getProperties() {
        return PROPERTIES;
    }

    public static String getPath() {
        return PROPERTIES_PATH.toString();
    }
}
