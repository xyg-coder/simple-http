package com.xygcoder.opensource.actions;

import java.util.Map;

public class SimpleHttpRequest {
  private String url;
  private Map<String, String> paramMap;

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public Map<String, String> getParamMap() {
    return paramMap;
  }

  public void setParamMap(Map<String, String> paramMap) {
    this.paramMap = paramMap;
  }
}
