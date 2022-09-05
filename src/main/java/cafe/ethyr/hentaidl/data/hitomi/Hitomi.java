package cafe.ethyr.hentaidl.data.hitomi;

import cafe.ethyr.hentaidl.helper.SiteHelper;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

//TODO: Make better script parsing
public class Hitomi {

    public static final String DOMAIN = "https://ltn.hitomi.la";
    public static final String GG_URL = DOMAIN + "/gg.js";
    public static final String GALLERY_INFO_URL = DOMAIN + "/galleries/%s.js";
    private static final Pattern URL_PATTERN = Pattern.compile("/[0-9a-f]{61}([0-9a-f]{2})([0-9a-f])");
    private final List<HitomiFile> files = new ArrayList<>();
    private final int[] keys;
    private final Set<Integer> switchBlock;

    private final String id;
    private final String title;
    private final int pages;

    private final JSONObject galleryInfo;
    private final String ggPath;

    public Hitomi(String id) throws IOException {
        try (
                Response galleryInfoResponse = SiteHelper.CLIENT.newCall(new Request.Builder().url(String.format(GALLERY_INFO_URL, id))
                        .addHeader("User-Agent", SiteHelper.getUserAgent())
                        .addHeader("Content-Type", "application/javascript; charset=UTF-8")
                        .get().build()).execute();

                Response ggScriptResponse = SiteHelper.CLIENT.newCall(new Request.Builder().url(GG_URL)
                        .addHeader("User-Agent", SiteHelper.getUserAgent())
                        .addHeader("Content-Type", "application/javascript; charset=UTF-8")
                        .get().build()).execute()
        ) {

            String galleryInfo = galleryInfoResponse.body().string();
            String ggScript = ggScriptResponse.body().string();

            this.galleryInfo = new JSONObject(galleryInfo.replace("var galleryinfo = ", ""));
            this.ggPath = getPathFromScript(ggScript);
            this.switchBlock = getSwitchBlockFromScript(ggScript);
            this.keys = getVarsFromScript(ggScript);

            this.id = this.galleryInfo.getString("id");
            this.title = this.galleryInfo.getString("title");

            JSONArray files = this.galleryInfo.getJSONArray("files");
            this.pages = files.length();
            for (int i = 0; i < files.length(); i++) {
                this.files.add(new HitomiFile(files.getJSONObject(i)));
            }
        } catch (Exception e) {
            throw new RemoteException("Can't request data from hitomi", e);
        }
    }

    public static String pathFromHash(String hash) {
        return Integer.toString(Integer.parseInt(hash.replaceAll("^.*(..)(.)$", "$2$1"), 16), 10);
    }

    public String subdomainFromUrl(String url, String base) {
        String retVal = "b";
        if (base != null)
            retVal = base;

        Optional<MatchResult> matchResult = URL_PATTERN.matcher(url).results().findFirst();
        if (matchResult.isEmpty() || matchResult.get().groupCount() <= 1)
            return "a";

        MatchResult result = matchResult.get();
        try {
            retVal = (char) (97 + switchTest(Integer.parseInt(result.group(2) + result.group(1), 16))) + retVal;
        } catch (Exception ignored) {
        }

        return retVal;
    }

    public String urlFromUrl(String url, String base) {
        return url.replaceAll("//..?\\.hitomi\\.la/", "//" + subdomainFromUrl(url, base) + ".hitomi.la/");
    }

    public String fullPathFromHash(String hash) {
        return ggPath + pathFromHash(hash) + "/" + hash;
    }

    public String realFullPathFromHash(String hash) {
        return hash.replaceAll("^.*(..)(.)$", "$2/$1/" + hash);
    }

    public String urlFromHash(String ignored, HitomiFile image, String dir, String ext) {
        if (ext == null)
            ext = dir;

        if (ext == null)
            ext = image.ext;

        if (dir == null)
            dir = "images";

        return "https://a.hitomi.la/" + dir + "/" + fullPathFromHash(image.hash) + '.' + ext;
    }

    public String urlFromUrlFromHash(String galleryId, HitomiFile image, String dir, String ext, String base) {
        if ("tn".equals(base))
            return urlFromUrl("https://a.hitomi.la/" + dir + "/" + realFullPathFromHash(image.hash) + "." + ext, base);

        return urlFromUrl(urlFromHash(galleryId, image, dir, ext), base);
    }

    public int switchTest(int number) {
        return switchBlock.contains(number) ? keys[1] : keys[0];
    }

    public int[] getVarsFromScript(String script) {
        int[] ints = new int[2];

        script.lines().forEach(line -> {
            if (line.startsWith("var o ="))
                ints[0] = Integer.parseInt(line.substring(line.lastIndexOf(' ') + 1, line.lastIndexOf(';')));
            else if (line.startsWith("o = "))
                ints[1] = Integer.parseInt(line.substring(line.indexOf(' ', 3) + 1, line.indexOf(';')));
        });

        return ints;
    }

    public Set<Integer> getSwitchBlockFromScript(String script) {
        return script.lines()
                .filter(line -> line.startsWith("case"))
                .map(line -> line.substring(0, line.length() - 1).replace("case ", ""))
                .map(Integer::parseInt)
                .collect(Collectors.toSet());
    }

    public String getPathFromScript(String script) {
        return script.lines()
                .filter(line -> line.contains("b:"))
                .filter(line -> line.contains("/'"))
                .findAny()
                .map(line -> line.substring(line.indexOf('\'') + 1, line.lastIndexOf('\'')))
                .orElseThrow();
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public List<HitomiFile> getFiles() {
        return files;
    }

    public int getPages() {
        return pages;
    }

    public JSONObject getGalleryInfo() {
        return galleryInfo;
    }

    @Override
    public String toString() {
        return "Hitomi{" + "id='" + id + '\'' + ", title='" + title + '\'' + ", pages=" + pages + ", ggPath='" + ggPath + '\'' + ", keys=" + Arrays.toString(keys) + ", switchBlock=" + switchBlock + ", files=" + files + '}';
    }

    public static class HitomiFile {
        private final String name;
        private final String ext;
        private final String fullName;
        private final String hash;
        private final boolean hasAvif;
        private final boolean hasWebp;

        public HitomiFile(JSONObject jsonObject) {
            this(
                    jsonObject.getString("name"),
                    jsonObject.getString("hash"),
                    jsonObject.getInt("hasavif") == 1,
                    jsonObject.getInt("haswebp") == 1
            );
        }

        public HitomiFile(String fullName, String hash, boolean hasAvif, boolean hasWebp) {
            this.name = fullName.substring(0, fullName.indexOf('.'));
            this.ext = fullName.substring(fullName.indexOf('.') + 1);
            this.fullName = fullName;
            this.hash = hash;
            this.hasAvif = hasAvif;
            this.hasWebp = hasWebp;
        }

        public HitomiFile(String name, String ext, String fullName, String hash, boolean hasAvif, boolean hasWebp) {
            this.name = name;
            this.ext = ext;
            this.fullName = fullName;
            this.hash = hash;
            this.hasAvif = hasAvif;
            this.hasWebp = hasWebp;
        }

        public String getUrl(Hitomi hitomi) {
            if (hasAvif) {
                return hitomi.urlFromUrlFromHash(hitomi.getId(), this, "avif", null, "a");
            } else if (hasWebp) {
                return hitomi.urlFromUrlFromHash(hitomi.getId(), this, "webp", null, "a");
            } else {
                return hitomi.urlFromUrlFromHash(hitomi.getId(), this, null, null, "a");
            }
        }

        public String getFixedExt() {
            if (hasAvif) {
                return "avif";
            } else if (hasWebp) {
                return "webp";
            } else {
                return ext;
            }
        }

        public String getName() {
            return name;
        }

        public String getExt() {
            return ext;
        }

        public String getFullName() {
            return fullName;
        }

        public String getHash() {
            return hash;
        }

        public boolean hasAvif() {
            return hasAvif;
        }

        public boolean hasWebp() {
            return hasWebp;
        }

        @Override
        public String toString() {
            return "HitomiFile{" + "name='" + name + '\'' + ", ext='" + ext + '\'' + ", fullName='" + fullName + '\'' + ", hash='" + hash + '\'' + ", hasAvif=" + hasAvif + ", hasWebp=" + hasWebp + '}';
        }
    }
}
