package com.xygcoder.opensource.dispatcher;

import com.xygcoder.opensource.context.SimpleHttpContext;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DispatcherCleaner implements ChannelFutureListener {
  private final static Logger LOG =
          LoggerFactory.getLogger(DispatcherCleaner.class);
  public void operationComplete(ChannelFuture channelFuture) throws Exception {
    SimpleHttpContext.removeContext();
    ChannelFutureListener.CLOSE.operationComplete(channelFuture);
    LOG.info("operation complete");
  }
}
