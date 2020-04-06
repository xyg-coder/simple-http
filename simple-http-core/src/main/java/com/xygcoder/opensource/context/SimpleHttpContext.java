package com.xygcoder.opensource.context;

import com.xygcoder.opensource.actions.SimpleHttpRequest;
import com.xygcoder.opensource.actions.SimpleHttpResponse;
import io.netty.util.concurrent.FastThreadLocal;

/**
 * This context can persist along the LocalThread (one request)
 */
public class SimpleHttpContext {
  private static final FastThreadLocal<SimpleHttpContext> localContext =
          new FastThreadLocal<>();
  private SimpleHttpRequest request;

  private SimpleHttpResponse response;

  private SimpleHttpContext() { }

  public void setRequest(SimpleHttpRequest request) {
    this.request = request;
  }

  public void setResponse(SimpleHttpResponse response) {
    this.response = response;
  }

  /**
   * @return return new context if not exist
   */
  public static SimpleHttpContext getContext() {
    SimpleHttpContext context = localContext.get();
    if (context == null) {
      context = new SimpleHttpContext();
      setContext(context);
    }
    return context;
  }

  public static SimpleHttpRequest getRequest() {
    return getContext().request;
  }

  public static SimpleHttpResponse getResponse() {
    return getContext().response;
  }

  public static void setContext(SimpleHttpContext context) {
    localContext.set(context);
  }

  public static void removeContext() {
    localContext.remove();
  }
}
