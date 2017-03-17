package com.yeahmobi.lab;

import com.yeahmobi.lab.handler.ClientHandler;
import com.yeahmobi.lab.handler.DataFrameDecoder;
import com.yeahmobi.lab.handler.DataFrameEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.compression.Lz4FrameDecoder;
import io.netty.handler.codec.compression.Lz4FrameEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class ClientController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientController.class);

    private final ClientConfig clientConfig;

    private EventLoopGroup eventLoopGroup;
    private Bootstrap bootstrap;

    public ClientController(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
        bootstrap = new Bootstrap();
    }

    public void start() {
        eventLoopGroup = new NioEventLoopGroup();
        final FileManager fileManager = new FileManager(bootstrap, clientConfig);
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_SNDBUF, clientConfig.getSendBufferSize())
                .option(ChannelOption.SO_RCVBUF, clientConfig.getReceiveBufferSize())
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, clientConfig.getConnectTimeout())
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new IdleStateHandler(0, 0, 3000, TimeUnit.MILLISECONDS))
                                .addLast(new Lz4FrameDecoder())
                                .addLast(new Lz4FrameEncoder())
                                .addLast(new DataFrameDecoder())
                                .addLast(new DataFrameEncoder())
                                .addLast(new ClientHandler(fileManager));
                    }
                });

        try {
            fileManager.transfer();
        } catch (InterruptedException e) {
        }
    }

    public void shutdown() {
        if (null != eventLoopGroup) {
            eventLoopGroup.shutdownGracefully();
        }
    }
}
