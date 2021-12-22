package cafe.ethyr.hentaidl.helper.parser;

import java.util.HashMap;
import java.util.Map;

public final class TagsParser {

    private static final Map<String, String> SHORTCUTS = new HashMap<>() {{
        put("s", "safe");
        put("q", "questionable");
        put("e", "explicit");
    }};

    public static String parse(String[] tags, String rating) {
        StringBuilder parsed = new StringBuilder();

        if (check(tags))
            parsed.append(String.join("+", tags));

        if (!(rating == null || rating.isBlank() || rating.isEmpty())) {
            parsed.append("+");

            if (rating.startsWith("-")) {
                parsed.append("-");
                rating = rating.substring(1);
            }

            parsed.append("rating:").append(SHORTCUTS.getOrDefault(rating, rating));
        }

        return parsed.toString();
    }

    private static boolean check(String... array) {
        return (array != null && array.length > 0) && (!array[0].isEmpty() && !array[0].isBlank());
    }

    public static String getFullRatingName(String rating) {
        return SHORTCUTS.getOrDefault(rating, rating);
    }
}
