package com.xygcoder.opensource.params;

public class MockBean {
  public String field1;
  public int field2;
  public String field3 = "not called";
  public String fieldNameComplicated;
  public void setField1(String field1) {
    this.field1 = field1;
  }

  public void setField2(String field2) {
    this.field2 = Integer.parseInt(field2);
  }

  public void setField3(int notCalled) {
    field3 = "called by mistake";
  }

  public void setFieldNameComplicated(String complicated) {
    this.fieldNameComplicated = complicated;
  }
}
