package com.yeahmobi.lab.handler;

import com.yeahmobi.lab.protocol.DataFrame;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ServerHandler extends SimpleChannelInboundHandler<DataFrame> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DataFrame msg) throws Exception {
        messageReceived(ctx, msg);
    }

    protected void messageReceived(ChannelHandlerContext ctx, DataFrame msg) throws Exception {

    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

    }
}
