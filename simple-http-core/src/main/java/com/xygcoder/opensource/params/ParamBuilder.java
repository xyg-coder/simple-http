package com.xygcoder.opensource.params;

import com.xygcoder.opensource.context.SimpleHttpContext;
import com.xygcoder.opensource.exception.SimpleHttpException;
import com.xygcoder.opensource.router.RouterInformation;
import com.xygcoder.opensource.router.RouterScanner;
import com.xygcoder.opensource.utils.Helper;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class ParamBuilder {
  private final static Logger LOG = LoggerFactory.getLogger(ParamBuilder.class);
  /**
   * {
   *   className: {
   *     methodName: fieldName(lowercase)
   *   }
   * }
   */
  private Map<String, Map<Method, String>> classToMethodToFiledName;
  private RouterScanner routerScanner;

  @Inject
  public ParamBuilder(RouterScanner routerScanner) {
    this.routerScanner = routerScanner;
  }

  public Object[] buildParams(Method method, Map<String, String> paramMap)
          throws Exception {
    Parameter[] params = method.getParameters();
    Object[] result = new Object[params.length];
    for (int i = 0; i < params.length; ++i) {
      Class<?> clazz = params[i].getType();
      if (clazz.isAssignableFrom(String.class)) {
        result[i] = paramMap.getOrDefault(params[i].getName(), "");
      } else if (clazz.isAssignableFrom(SimpleHttpContext.class)) {
        result[i] = SimpleHttpContext.getContext();
      } else if (clazz.isAssignableFrom(Map.class)) {
        result[i] = buildParam(paramMap);
      } else {
        result[i] = buildParam(params[i].getType(), paramMap);
      }
    }
    return result;
  }

  private Object buildParam(Class<?> clazz, Map<String, String> paramMap)
          throws Exception {
    Object instance = clazz.newInstance();
    Map<Method, String> methodToFiledName =
            classToMethodToFiledName.get(clazz.getName());
    for (Map.Entry<Method, String> entry : methodToFiledName.entrySet()) {
      String fieldName = entry.getValue();
      if (paramMap.containsKey(fieldName)) {
        String param = paramMap.get(fieldName);
        entry.getKey().invoke(instance, param);
      }
    }
    return instance;
  }

  private Map<String, String> buildParam(Map<String, String> paramMap) {
    return paramMap;
  }

  /**
   * construct classToMethodToFieldName and check the validation of functions
   * @throws SimpleHttpException
   */
  public void init() {
    classToMethodToFiledName = new HashMap<>();
    Map<String, Map<HttpMethod, RouterInformation>> routerMap
            = routerScanner.getRouterMap();
    for (Map<HttpMethod, RouterInformation> methodMap : routerMap.values()) {
      for (RouterInformation routerInformation : methodMap.values()) {
        Method handlerMethod = routerInformation.getHandlerMethod();
        Class<?>[] params = handlerMethod.getParameterTypes();
        for (Class<?> clazz : params) {
          if (clazz.isAssignableFrom(String.class)
                  || clazz.isAssignableFrom(Map.class)
                  || clazz.isAssignableFrom(SimpleHttpContext.class)) {
            continue;
          }
          for (Method method : clazz.getMethods()) {
            String methodName = method.getName();
            if (!methodName.startsWith("set") ||
                    methodName.length() <= 3) {
              continue;
            }
            if (method.getParameters().length != 1 ||
                    !method.getParameters()[0].getType().isAssignableFrom(String.class)) {
              continue;
            }
            String fieldName = Helper.formalizeName(methodName.substring(3));
            Map<Method, String> methodToField =
                    classToMethodToFiledName.getOrDefault(clazz.getName(), new HashMap<>());
            methodToField.put(method, fieldName);
            classToMethodToFiledName.put(clazz.getName(), methodToField);
          }
        }
      }
    }
  }

  public Map<String, String> buildParamMap(QueryStringDecoder queryStringDecoder) {
    Map<String, String> paramMap = new HashMap<>();
    Map<String, List<String>> params = queryStringDecoder.parameters();
    for (Map.Entry<String, List<String>> param : params.entrySet()) {
      if (!param.getValue().isEmpty()) {
        paramMap.put(param.getKey(), param.getValue().get(0));
      }
    }
    return paramMap;
  }
}
