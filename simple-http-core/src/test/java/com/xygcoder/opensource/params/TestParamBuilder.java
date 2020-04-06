package com.xygcoder.opensource.params;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.xygcoder.opensource.router.RouterInformation;
import com.xygcoder.opensource.router.RouterScanner;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class TestParamBuilder {
  private RouterScanner routerScanner;
  private ParamBuilder paramBuilder;

  @Before
  public void setUp() throws Exception {
    Map<String, Map<HttpMethod, RouterInformation>> informationMap = new HashMap<>();
    MockCaller caller = new MockCaller();
    informationMap.put("test/handler1",
            ImmutableMap.of(HttpMethod.GET,
                    new RouterInformation(MockCaller.class,
                            MockCaller.class.getMethod("handler1", Map.class),
                            caller)));
    informationMap.put("test/handler2",
            ImmutableMap.of(HttpMethod.GET,
                    new RouterInformation(MockCaller.class,
                            MockCaller.class.getMethod("handler2",
                                    String.class, String.class),
                            caller)));
    informationMap.put("test/handler3",
            ImmutableMap.of(HttpMethod.GET,
                    new RouterInformation(MockCaller.class,
                            MockCaller.class.getMethod("handler3", MockBean.class),
                            caller)));
    routerScanner = mock(RouterScanner.class);
    when(routerScanner.getRouterMap()).thenReturn(informationMap);
    paramBuilder = new ParamBuilder(routerScanner);
    paramBuilder.init();
  }

  @Test
  public void testGetInstances() throws Exception {
    Map<String, String> parameters = ImmutableMap.of(
            "field1", "value1",
            "field2", "2",
            "field3", "value3",
            "fieldNameComplicated", "complicated"
    );
    Object[] params =
            paramBuilder.buildParams(
                    MockCaller.class.getMethod("handler1", Map.class),
                    parameters);
    Assert.assertEquals(params.length, 1);
    Assert.assertEquals(params[0], parameters);

    params = paramBuilder.buildParams(
            MockCaller.class.getMethod("handler2",
                    String.class, String.class), parameters);
    Assert.assertEquals(params.length, 2);
    Assert.assertEquals(params[0], "value1");
    Assert.assertEquals(params[1], "2");

    params = paramBuilder.buildParams(
            MockCaller.class.getMethod("handler3", MockBean.class),
            parameters);
    Assert.assertEquals(params.length, 1);
    MockBean bean = (MockBean)params[0];
    Assert.assertEquals(bean.field1, "value1");
    Assert.assertEquals(bean.field2, 2);
    Assert.assertEquals(bean.field3, "not called");
    Assert.assertEquals(bean.fieldNameComplicated, "complicated");

    params = paramBuilder.buildParams(
            MockCaller.class.getMethod("handler4",
                    MockBean.class, String.class, Map.class), parameters);
    Assert.assertEquals(params.length, 3);
    bean = (MockBean) params[0];
    Assert.assertEquals(bean.field1, "value1");
    Assert.assertEquals(bean.field2, 2);
    Assert.assertEquals(bean.field3, "not called");
    Assert.assertEquals(bean.fieldNameComplicated, "complicated");

    Assert.assertEquals(params[1], "complicated");
    Assert.assertEquals(params[2], parameters);
  }

  @Test
  public void testBuildParamMap() throws Exception {
    QueryStringDecoder decoder = mock(QueryStringDecoder.class);
    when(decoder.parameters()).thenReturn(
            ImmutableMap.of("field1", ImmutableList.of("value1"),
                    "field2", ImmutableList.of("value2")));
    Map<String, String> correctResult = ImmutableMap.of("field1", "value1",
            "field2", "value2");
    Assert.assertEquals(correctResult, paramBuilder.buildParamMap(decoder));
  }

  @Test
  public void testBuildParamFromUrl() throws Exception {
    String url = "/test/a?field1=value1&field2=value2";
    Map<String, String> result =
            paramBuilder.buildParamMap(new QueryStringDecoder(url));
    Map<String, String> correctResult = ImmutableMap.of("field1", "value1",
            "field2", "value2");
    Assert.assertEquals(correctResult, result);

  }
}
