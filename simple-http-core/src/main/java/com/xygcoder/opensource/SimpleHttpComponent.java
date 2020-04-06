package com.xygcoder.opensource;

import com.xygcoder.opensource.annotations.Named;
import com.xygcoder.opensource.config.AppConfig;
import com.xygcoder.opensource.params.ParamBuilder;
import com.xygcoder.opensource.router.RouterScanner;
import dagger.BindsInstance;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = SimpleHttpModule.class)
public interface SimpleHttpComponent {
  AppConfig appConfig();
  RouterScanner routerScanner();
  ParamBuilder paramBuilder();

  Initializer initializer();

  @Component.Builder
  interface Builder {
    @BindsInstance Builder userClass(@Named("userClass") Class<?> clazz);

    SimpleHttpComponent build();
  }
}
