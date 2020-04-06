package com.xygcoder.opensource.router;

import com.xygcoder.opensource.annotations.SimpleHttpGet;
import com.xygcoder.opensource.annotations.SimpleHttpRouter;
import com.xygcoder.opensource.config.AppConfig;
import com.xygcoder.opensource.exception.SimpleHttpException;
import com.xygcoder.opensource.utils.Helper;
import io.netty.handler.codec.http.HttpMethod;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Singleton
public class RouterScanner {
  private final static Logger LOG = LoggerFactory.getLogger(RouterScanner.class);
  /**
   * map from router path to RouterInformation
   */
  private Map<String, Map<HttpMethod, RouterInformation>> routerInformationMap;
  private AppConfig appConfig;

  @Inject
  public RouterScanner(AppConfig appConfig) {
    this.appConfig = appConfig;
  }

  /**
   * SimpleHttpRouter.value cannot be empty
   * @throws SimpleHttpException
   */
  public void init() throws SimpleHttpException {
    routerInformationMap = new HashMap<>();

    Reflections reflections = new Reflections(appConfig.getPackageName());
    Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(SimpleHttpRouter.class);
    Set<String> routerPathSet = new HashSet<>();
    try {
      for (Class<?> clazz : annotated) {
        Annotation annotation = clazz.getAnnotation(SimpleHttpRouter.class);
        String routerPath = "";
        if (annotation instanceof SimpleHttpRouter) {
          SimpleHttpRouter simpleHttpRouter = (SimpleHttpRouter) annotation;
          routerPath = Helper.formalizeUrl(simpleHttpRouter.value());
        }
        if (routerPath.isEmpty()) {
          throw new SimpleHttpException(
                  SimpleHttpException.ExceptionCode.INITIALIZE_ERROR,
                  "Empty Router path");
        }
        if (routerPathSet.contains(routerPath)) {
          throw new SimpleHttpException(
                  SimpleHttpException.ExceptionCode.INITIALIZE_ERROR,
                  "Duplicate router path");
        }
        routerPathSet.add(routerPath);
        Object bean = clazz.newInstance();
        for (Method method : clazz.getMethods()) {
          Annotation methodAnnotation = method.getAnnotation(SimpleHttpGet.class);
          if (methodAnnotation instanceof SimpleHttpGet) {
            String subPath = ((SimpleHttpGet) methodAnnotation).value();
            String path = Helper.concatUrl(routerPath, subPath);

            if (routerInformationMap.containsKey(path)) {
              throw new SimpleHttpException(
                      SimpleHttpException.ExceptionCode.INITIALIZE_ERROR,
                      String.format("Duplicate handler method for path(%s)", path));
            }

            Map<HttpMethod, RouterInformation> map =
                    routerInformationMap.getOrDefault(path, new HashMap<>());
            map.put(HttpMethod.GET, new RouterInformation(clazz, method, bean));
            routerInformationMap.put(path, map);
          }
        }
      }
    } catch (InstantiationException | IllegalAccessException e) {
      LOG.error(String.format("bean initialzation error: %s", e.getMessage()));
      throw new SimpleHttpException(
              SimpleHttpException.ExceptionCode.INITIALIZE_ERROR,
              "Bean initialization error");
    }
  }

  public Map<String, Map<HttpMethod, RouterInformation>> getRouterMap() {
    return routerInformationMap;
  }

  // TODO(xgui): get RouterInformation corresponding to the url, if no match, throw exception
  public RouterInformation getRouterInformation(String path, HttpMethod method)
          throws SimpleHttpException {
    if (!routerInformationMap.containsKey(path)
            || !routerInformationMap.get(path).containsKey(method)) {
      throw new SimpleHttpException(
              SimpleHttpException.ExceptionCode.NO_HANDLER_FOUND,
              String.format("cannot find handler for path(%s) and method(%s)",
                      path, method.name()));
    }
    return routerInformationMap.get(path).get(method);
  }
}
