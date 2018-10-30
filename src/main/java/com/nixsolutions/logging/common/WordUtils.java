package com.nixsolutions.logging.common;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.text.WordUtils.capitalizeFully;
import static org.apache.commons.lang3.text.WordUtils.uncapitalize;
import java.text.BreakIterator;
import org.apache.commons.lang3.StringUtils;

public class WordUtils
{
  /**
   * Truncate string on closest  word boundary.
   * <p>
   * <pre>
   *   WordUtils.truncateWithWordBoundary(null, *) = ""
   *   WordUtils.truncateWithWordBoundary(*, 0) = ""
   *   WordUtils.truncateWithWordBoundary(*, -1) = ""
   *   WordUtils.truncateWithWordBoundary("abc", 5) = "abc"
   *   WordUtils.truncateWithWordBoundary("abc dfe", 5) = "abc"
   *   WordUtils.truncateWithWordBoundary("abc,:;dfc", 5) = "abc
   * </pre>
   *
   * @param string    - the String to be truncated, may be null
   * @param maxLength - max length of truncated string
   * @return same string if string length less then max length or truncated string
   */
  public static String truncateWithWordBoundary(String string, int maxLength)
  {
    if (StringUtils.isBlank(string) || maxLength <= 0)
    {
      return EMPTY;
    }

    if (string.length() < maxLength)
    {
      return string;
    }

    BreakIterator breakIterator = BreakIterator.getWordInstance();
    breakIterator.setText(string);

    int currentWordStart = breakIterator.preceding(maxLength);

    return string.substring(0, breakIterator.following(currentWordStart - 2));
  }

  /**
   * Converts {@code sourceString} to a camelCase string using {@code delimiters}
   * Examples:
   * <ul>
   * <li>toCamelCase("Load renewal"," ") - loadRenewal</li>
   * <li>toCamelCase("Load renewal! Now"," !") - loadRenewalNow</li>
   * <li>toCamelCase("Load ReNEwaL"," ") - loadRenewal</li>
   * <li>toCamelCase("Loadrenewal","") - loadrenewal </li>
   * </ul>
   *
   * @param sourceString
   * @param delimiters   - delimiters in a single string
   */
  public static String toCamelCase(String sourceString, String delimiters)
  {
    String result = capitalizeFully(sourceString, delimiters.toCharArray());
    for (String delimiter : delimiters.split(EMPTY))
    {
      result = result.replaceAll(delimiter, EMPTY);
    }
    return uncapitalize(result);
  }
}
