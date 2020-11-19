package xyz.ethyr.parser;

import java.util.regex.Pattern;

public class RegexParser {

  public ParserObject parse(String string) {
    StringBuilder stringBuilder = new StringBuilder("(");
    for (String tag : string.split(" ")) {
      stringBuilder.append(tag).append("|");
    }
    stringBuilder.replace(stringBuilder.length() - 1, stringBuilder.length(), ")");
    return new ParserObject(Pattern.compile(stringBuilder.toString(), Pattern.MULTILINE | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE), string);
  }


  public class ParserObject {
    private final Pattern pattern;
    private final String string;

    private ParserObject(Pattern pattern, String string) {
      this.pattern = pattern;
      this.string = string;
    }

    public Pattern getPattern() {
      return pattern;
    }

    public String getString() {
      return string;
    }
  }
}
