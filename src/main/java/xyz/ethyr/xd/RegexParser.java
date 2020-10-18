package xyz.ethyr.xd;

import java.util.regex.Pattern;
import lombok.Getter;

public class RegexParser {

  public ParserObject parse(final String string) {
    final StringBuilder stringBuilder = new StringBuilder("(");
    for (final String tag : string.split(" ")) {
      stringBuilder.append(tag).append("|");
    }
    stringBuilder.replace(stringBuilder.length() - 1, stringBuilder.length(), ")");
    return new ParserObject(Pattern.compile(stringBuilder.toString(), Pattern.MULTILINE | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE), string);
  }

  @Getter
  public class ParserObject {
    private final Pattern pattern;
    private String string;

    private ParserObject(Pattern pattern, String string) {
      this.pattern = pattern;
      this.string = string;
    }
  }
}
