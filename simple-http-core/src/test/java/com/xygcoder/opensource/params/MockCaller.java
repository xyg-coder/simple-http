package com.xygcoder.opensource.params;

import java.util.Map;

public class MockCaller {
  public Map<String, String> param;
  public String field1;
  public String field2;
  public MockBean mockBean;

  public void handler1(Map<String, String> param) {
    this.param = param;
  }

  public void handler2(String field1, String field2) {
    this.field1 = field1;
    this.field2 = field2;
  }

  public void handler3(MockBean mockBean) {
    this.mockBean = mockBean;
  }

  public void handler4(MockBean mockBean,
                       String fieldNameComplicated,
                       Map<String, String> param) { }
}
