package xyz.ethyr.util;

import java.util.regex.Pattern;

public final class RegexParser {

  public static RegexInfo parse(String string) {
    return new RegexInfo(Pattern.compile("(" + String.join("|", string.split(" ")) + ")",
        Pattern.MULTILINE | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE), string);
  }

  public static class RegexInfo {

    private final Pattern pattern;
    private final String string;

    private RegexInfo(Pattern pattern, String string) {
      this.pattern = pattern;
      this.string = string;
    }

    public Pattern getPattern() {
      return pattern;
    }

    public String getString() {
      return string;
    }

    public String getString(String prefix) {
      return string.isEmpty() ? "" : prefix + string;
    }
  }
}
