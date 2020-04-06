package com.xygcoder.opensource;

import com.xygcoder.opensource.constants.ServerConstants;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleHttpServer {
  private final static Logger LOG = LoggerFactory.getLogger(SimpleHttpServer.class);
  private SimpleHttpComponent component;

  public void init(Class<?> userClass) throws Exception {
    LOG.info("\n=======================================\n" +
            "Initialization begins\n" +
            "=======================================");
    component = DaggerSimpleHttpComponent
            .builder()
            .userClass(userClass)
            .build();
    component.appConfig().init();
    component.routerScanner().init();
    component.paramBuilder().init();
    LOG.info("\n=======================================\n" +
            "Initialization ends\n" +
            "=======================================");
  }

  public void run(Class<?> userClass) throws Exception {
    LOG.info(String.format("\n%s", ServerConstants.LOGO));
    init(userClass);
    EventLoopGroup bossGroup = new NioEventLoopGroup();
    EventLoopGroup workerGroup = new NioEventLoopGroup();
    try {
      ServerBootstrap b = new ServerBootstrap();
      b.group(bossGroup, workerGroup)
              .channel(NioServerSocketChannel.class)
              .localAddress(component.appConfig().getPort())
              .childHandler(component.initializer());
      ChannelFuture f = b.bind(component.appConfig().getPort()).sync();
      if (f.isSuccess()) {
        LOG.info("start netty success");
      }
      f.channel().closeFuture().sync();
    } finally {
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
    }
  }

  public static void main(String[] args) throws Exception {
    new SimpleHttpServer().run(SimpleHttpServer.class);
  }
}
