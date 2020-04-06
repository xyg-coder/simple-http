package com.xygcoder.opensource.config;

import com.google.common.annotations.VisibleForTesting;
import com.xygcoder.opensource.annotations.Named;
import com.xygcoder.opensource.constants.ServerConstants;
import com.xygcoder.opensource.exception.SimpleHttpException;
import com.xygcoder.opensource.utils.Helper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This file will read the properties file in Resources folder
 */
@Singleton
public class AppConfig {
  private final static Logger LOG = LoggerFactory.getLogger(AppConfig.class);
  private String resourceFile;
  private Class<?> userClazz;
  private int port;

  /**
   * basic package name. This will be used for router or action scanner
   */
  private String packageName;

  @Inject
  public AppConfig(@Named("userClass") Class<?> clazz,
                   @Named("propertyFile") String resourceFile) {
    this.userClazz = clazz;
    this.resourceFile = resourceFile;
  }

  @VisibleForTesting
  protected void setProperties(Properties prop) throws SimpleHttpException {
    SimpleHttpException propertiesException =
            new SimpleHttpException(SimpleHttpException.ExceptionCode.INITIALIZE_ERROR);
    setOptionalProp(
            propertiesException,
            prop,
            "port",
            ServerConstants.DEFAULT_PORT,
            (String propValue) -> this.port = Integer.parseInt(propValue));
    setOptionalProp(
            propertiesException,
            prop,
            "packageName",
            this.userClazz.getPackage().getName(),
            (String propValue) -> this.packageName = propValue);
    if (propertiesException.getMessage().length() != 0) {
      return;
    }
  }

  public void init() throws SimpleHttpException {
    if (this.userClazz.getPackage() == null) {
      LOG.error(String.format("cannot get %s packageName", this.userClazz.getName()));
      throw new SimpleHttpException(
              SimpleHttpException.ExceptionCode.INITIALIZE_ERROR,
              "cannot get package name");
    }
    Properties prop = new Properties();
    InputStream inputStream = this.userClazz.getClassLoader().getResourceAsStream(resourceFile);
    if (inputStream == null) {
      LOG.error("cannot load properties");
      throw new SimpleHttpException(
              SimpleHttpException.ExceptionCode.INITIALIZE_ERROR,
              "cannot load properties");
    }

    try {
      prop.load(inputStream);
    } catch (IOException e) {
      LOG.error(Helper.traceStackToString(e));
      throw new SimpleHttpException(
              SimpleHttpException.ExceptionCode.INITIALIZE_ERROR,
              String.format("properties loading error: %s", e.getMessage()));
    }

    setProperties(prop);

    LOG.info(String.format("AppConfig init:\n" +
                    "port=%d\t packageName=%s",
            getPort(),
            getPackageName()));
  }

  private interface PropSetter {
    void execute(String prop) throws SimpleHttpException;
  }

  private void setOptionalProp(
          SimpleHttpException exception,
          Properties prop,
          String propName,
          String defaultValue,
          PropSetter propSetter) {
    String propValue = prop.getProperty(propName, defaultValue);
    try {
      propSetter.execute(propValue);
    } catch (SimpleHttpException ex) {
      exception.appendMessage(String.format("read %s from properties file error", propName));
    } catch (NumberFormatException ex) {
      exception.appendMessage(String.format("read %s from properties format error", propName));
    }
  }

  private void setMustProp(
          SimpleHttpException exception,
          Properties prop,
          String propName,
          PropSetter propSetter) {
    String propValue = prop.getProperty(propName);
    try {
      if (propValue == null) {
        throw new SimpleHttpException(
                SimpleHttpException.ExceptionCode.INITIALIZE_ERROR);
      }
      propSetter.execute(propValue);
    } catch (SimpleHttpException ex) {
      exception.appendMessage(String.format("read %s from properties file error", propName));
    } catch (NumberFormatException ex) {
      exception.appendMessage(String.format("read %s from properties format error", propName));
    }
  }

  public int getPort() {
    return port;
  }

  public String getPackageName() {
    return packageName;
  }
}
