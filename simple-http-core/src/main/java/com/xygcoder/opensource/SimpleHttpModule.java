package com.xygcoder.opensource;

import com.xygcoder.opensource.annotations.Named;
import dagger.Module;
import dagger.Provides;

@Module
public class SimpleHttpModule {
  @Provides
  @Named("propertyFile")
  public static String providePropertyFileString() {
    return "application.properties";
  }
}
