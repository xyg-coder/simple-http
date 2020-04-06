package com.xygcoder.opensource.config;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.util.Properties;

public class TestAppConfig {
  @Test
  public void testCorrectResourceFile() throws Exception {
    Properties mockProp = mock(Properties.class);
    when(mockProp.getProperty("port", "8080")).thenReturn("8080");
    when(mockProp.getProperty("packageName", AppConfig.class.getPackage().getName()))
            .thenReturn("package-for-test");
    AppConfig appConfig = new AppConfig(AppConfig.class, "application.properties");
    appConfig.setProperties(mockProp);
    assertEquals(8080, appConfig.getPort());
    assertEquals("package-for-test", appConfig.getPackageName());
  }

  @Test
  public void testOptionalProp() throws Exception {
    Properties mockProp = mock(Properties.class);
    when(mockProp.getProperty("port", "8080")).thenReturn("9090");
    when(mockProp.getProperty("packageName", AppConfig.class.getPackage().getName()))
            .thenReturn(AppConfig.class.getPackage().getName());
    AppConfig appConfig = new AppConfig(AppConfig.class, "application.properties");
    appConfig.setProperties(mockProp);
    assertEquals(9090, appConfig.getPort());
    assertEquals(AppConfig.class.getPackage().getName(), appConfig.getPackageName());
  }
}
