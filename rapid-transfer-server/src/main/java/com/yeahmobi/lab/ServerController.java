package com.yeahmobi.lab;

import com.yeahmobi.lab.handler.DataFrameDecoder;
import com.yeahmobi.lab.handler.DataFrameEncoder;
import com.yeahmobi.lab.handler.ServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.compression.Lz4FrameDecoder;
import io.netty.handler.codec.compression.Lz4FrameEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class ServerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerController.class);

    private final ServerConfig serverConfig;
    private EventLoopGroup parentGroup;
    private EventLoopGroup childGroup;

    public ServerController(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
        int numberOfCores = Runtime.getRuntime().availableProcessors();
        parentGroup = new NioEventLoopGroup(1);
        childGroup = new NioEventLoopGroup(numberOfCores);
    }

    public void start() {
        ServerBootstrap bootstrap = new ServerBootstrap();

        try {
            bootstrap.group(parentGroup, childGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, serverConfig.getBacklog())
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new IdleStateHandler(0, 0, 3000, TimeUnit.MILLISECONDS))
                                    .addLast(new Lz4FrameEncoder())
                                    .addLast(new Lz4FrameDecoder())
                                    .addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4))
                                    .addLast(new DataFrameDecoder())
                                    .addLast(new DataFrameEncoder())
                                    .addLast(new ServerHandler());
                        }
                    });

            ChannelFuture future = bootstrap.bind(serverConfig.getPort()).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted", e);
        } finally {
            if (null != parentGroup) {
                parentGroup.shutdownGracefully();
            }

            if (null != childGroup) {
                childGroup.shutdownGracefully();
            }
        }


    }

    public void shutdown() {
        if (null != parentGroup) {
            parentGroup.shutdownGracefully();
        }

        if (null != childGroup) {
            childGroup.shutdownGracefully();
        }
    }
}
