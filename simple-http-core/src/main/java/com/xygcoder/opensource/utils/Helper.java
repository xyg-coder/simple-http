package com.xygcoder.opensource.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Helper {
  public static String traceStackToString(Exception ex) {
    StringWriter errors = new StringWriter();
    ex.printStackTrace(new PrintWriter(errors));
    return errors.toString();
  }

  /**
   * remove the prefix or suffix "/" in url
   * @param url
   * @return formalized url
   */
  public static String formalizeUrl(String url) {
    if (url == null || url.isEmpty()) {
      return url;
    }
    int start = 0;
    int end = url.length();
    if (url.charAt(0) == '/') {
      ++start;
    }
    if (url.charAt(end - 1) == '/') {
      --end;
    }
    return start >= end ? "" : url.substring(start, end);
  }

  public static String concatUrl(String path1, String path2) {
    if (path2.isEmpty()) {
      return path1;
    } else if (path1.isEmpty()) {
      return path2;
    } else {
      return String.format("%s/%s", formalizeUrl(path1), formalizeUrl(path2));
    }
  }

  /**
   * If first character is uppercase, turn it into lowercase
   */
  public static String formalizeName(String name) {
    if (!Character.isUpperCase(name.charAt(0))) {
      return name;
    }
    StringBuilder sb = new StringBuilder();
    sb.append(Character.toLowerCase(name.charAt(0)))
            .append(name.substring(1));
    return sb.toString();
  }
}
