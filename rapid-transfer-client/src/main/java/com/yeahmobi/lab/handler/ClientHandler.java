package com.yeahmobi.lab.handler;

import com.yeahmobi.lab.FileManager;
import com.yeahmobi.lab.protocol.DataFrame;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientHandler extends SimpleChannelInboundHandler<DataFrame> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientHandler.class);

    private final FileManager fileManager;

    public ClientHandler(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DataFrame msg) throws Exception {
        messageReceived(ctx, msg);
    }

    protected void messageReceived(ChannelHandlerContext ctx, DataFrame msg) throws Exception {
        fileManager.onMessageReceived(ctx, msg);
    }

    @Override
    public void userEventTriggered(final ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            ctx.close().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    LOGGER.info("Connection closed. {} --> {}",
                            ctx.channel().localAddress().toString(),
                            ctx.channel().remoteAddress().toString());
                }
            });
        }
    }
}
