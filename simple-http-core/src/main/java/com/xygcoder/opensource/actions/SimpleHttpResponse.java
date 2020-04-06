package com.xygcoder.opensource.actions;

import com.xygcoder.opensource.constants.ServerConstants;

public class SimpleHttpResponse {
  private String contentType;
  private String content;

  public void setText(String text) {
    contentType = ServerConstants.ContentType.TEXT;
    content = text;
  }

  public void setHtml(String html) {
    contentType = ServerConstants.ContentType.HTML;
    content = html;
  }

  public void setJson(String json) {
    contentType = ServerConstants.ContentType.JSON;
    content = json;
  }

  public String getContentType() {
    return contentType;
  }

  public String getContent() {
    return content;
  }
}
