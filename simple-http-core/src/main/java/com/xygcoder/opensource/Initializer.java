package com.xygcoder.opensource;

import com.xygcoder.opensource.dispatcher.HttpDispatcher;
import com.xygcoder.opensource.params.ParamBuilder;
import com.xygcoder.opensource.router.RouterScanner;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Initializer extends ChannelInitializer<Channel> {
  private RouterScanner routerScanner;
  private ParamBuilder paramBuilder;

  @Inject
  public Initializer(RouterScanner routerScanner,
                     ParamBuilder paramBuilder) {
    this.routerScanner = routerScanner;
    this.paramBuilder = paramBuilder;
  }

  protected void initChannel(Channel channel) throws Exception {
    channel.pipeline()
            .addLast(new HttpRequestDecoder())
            .addLast(new HttpResponseEncoder())
            .addLast(new HttpDispatcher(routerScanner, paramBuilder))
            .addLast("logging", new LoggingHandler(LogLevel.INFO));
  }
}
