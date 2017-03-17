package com.yeahmobi.lab;

import com.yeahmobi.lab.protocol.DataFrame;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.TimeUnit;

public class FileManager {

    private final ClientConfig clientConfig;

    private final Bootstrap bootstrap;

    public FileManager(final Bootstrap bootstrap, ClientConfig clientConfig) {
        this.bootstrap = bootstrap;
        this.clientConfig = clientConfig;
    }

    private Channel getAndCreateChannel() throws InterruptedException {
        ChannelFuture channelFuture = bootstrap.connect(clientConfig.getIp(), clientConfig.getPort());
        channelFuture.await(clientConfig.getConnectTimeout(), TimeUnit.MILLISECONDS);
        return channelFuture.channel();
    }

    public void transfer() throws InterruptedException {
        Channel channel = getAndCreateChannel();

    }

    public void onMessageReceived(ChannelHandlerContext ctx, DataFrame msg) {

    }
}
