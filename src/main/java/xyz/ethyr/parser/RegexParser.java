package xyz.ethyr.parser;

import java.util.regex.Pattern;

public class RegexParser {

  public ParsedObject parse(String string) {
    return new ParsedObject(Pattern.compile("(" + String.join("|", string.split(" ")) + ")",
        Pattern.MULTILINE | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE), string);
  }


  public static class ParsedObject {

    private final Pattern pattern;
    private final String string;

    private ParsedObject(Pattern pattern, String string) {
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
