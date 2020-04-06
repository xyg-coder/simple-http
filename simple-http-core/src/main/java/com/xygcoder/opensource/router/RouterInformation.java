package com.xygcoder.opensource.router;

import java.lang.reflect.Method;

public class RouterInformation {
  private Class<?> clazz;
  private Method handlerMethod;
  private Object bean;

  public RouterInformation(Class<?> clazz, Method handlerMethod, Object bean) {
    this.clazz = clazz;
    this.handlerMethod = handlerMethod;
    this.bean = bean;
  }

  public Class<?> getClazz() {
    return clazz;
  }

  public Method getHandlerMethod() {
    return handlerMethod;
  }

  public Object getBean() {
    return bean;
  }
}
