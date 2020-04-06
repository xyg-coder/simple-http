package com.xygcoder.opensource.dispatcher;

import com.xygcoder.opensource.actions.SimpleHttpRequest;
import com.xygcoder.opensource.actions.SimpleHttpResponse;
import com.xygcoder.opensource.context.SimpleHttpContext;
import com.xygcoder.opensource.params.ParamBuilder;
import com.xygcoder.opensource.router.RouterInformation;
import com.xygcoder.opensource.router.RouterScanner;
import com.xygcoder.opensource.utils.Helper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;

public class HttpDispatcher extends SimpleChannelInboundHandler<DefaultHttpRequest> {
  private RouterScanner routerScanner;
  private ParamBuilder paramBuilder;

  public HttpDispatcher(RouterScanner routerScanner,
                        ParamBuilder paramBuilder) {
    super();
    this.routerScanner = routerScanner;
    this.paramBuilder = paramBuilder;
  }

  private final static Logger LOG =
          LoggerFactory.getLogger(HttpDispatcher.class);

  protected void channelRead0(
          ChannelHandlerContext ctx,
          DefaultHttpRequest httpRequest) throws Exception {
    SimpleHttpContext context = SimpleHttpContext.getContext();
    context.setResponse(new SimpleHttpResponse());
    defaultHandler();
    QueryStringDecoder queryStringDecoder =
            new QueryStringDecoder(URLDecoder.decode(httpRequest.uri(), "utf-8"));
    String path = Helper.formalizeUrl(queryStringDecoder.path());
    SimpleHttpRequest request = new SimpleHttpRequest();
    request.setUrl(httpRequest.uri());
    try {
      RouterInformation routerInformation =
              routerScanner.getRouterInformation(path, httpRequest.method());
      Method handlerMethod = routerInformation.getHandlerMethod();
      Map<String, String> paramMap = paramBuilder.buildParamMap(queryStringDecoder);
      request.setParamMap(paramMap);
      context.setRequest(request);
      Object[] objects = paramBuilder.buildParams(handlerMethod, paramMap);
      handlerMethod.invoke(routerInformation.getBean(), objects);
    } catch (Exception e) {
      LOG.error("Handler exception", e);
    } finally {
      DefaultHttpResponse response = getResponse();
      ctx.writeAndFlush(response).addListeners(new DispatcherCleaner());
    }
  }


  private DefaultFullHttpResponse getResponse() {
    SimpleHttpResponse response = SimpleHttpContext.getResponse();
    String content = response.getContent();
    ByteBuf buf = Unpooled.wrappedBuffer(content.getBytes(StandardCharsets.UTF_8));
    DefaultFullHttpResponse result = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
            HttpResponseStatus.OK, buf);

    /* set header */
    HttpHeaders headers = result.headers();
    headers.setInt(HttpHeaderNames.CONTENT_LENGTH, result.content().readableBytes());
    headers.set(CONTENT_TYPE, response.getContentType());

    return result;
  }

  /**
   * Default handler
   */
  private void defaultHandler() {
    SimpleHttpResponse response = SimpleHttpContext.getResponse();
    response.setHtml("<center> Hello sir, this page is missing <br/><br/>" +
            "Power by <a href='https://github.com/xyg-coder/simple-http'>@Simple-http</a> </center>");
  }
}
