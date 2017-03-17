package com.yeahmobi.lab.handler;

import com.yeahmobi.lab.protocol.DataFrame;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class DataFrameDecoder extends SimpleChannelInboundHandler<ByteBuf> {

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        messageReceived(ctx, msg);
    }

    protected void messageReceived(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        int totalLength = msg.readInt();
        int headerLength = msg.readInt();
        long id = msg.readLong();
        ByteBuf mapByteBuf = msg.readSlice(headerLength - 8);
        ByteBuf body = msg.readRetainedSlice(totalLength - headerLength);
        DataFrame dataFrame = new DataFrame();
        dataFrame.setId(id);
        dataFrame.setBody(body);
        dataFrame.setMap(decodeMap(mapByteBuf));

        ctx.fireChannelRead(dataFrame);
    }

    private Map<String, String> decodeMap(ByteBuf byteBuf) {
        int count = byteBuf.readInt();
        Map<String, String> map = new HashMap<>(count, 1.0F);
        for (int i = 0; i < count; i++) {
            int kvLength = byteBuf.readInt();
            int keyLength = byteBuf.readInt();

            ByteBuf key = byteBuf.readBytes(keyLength);
            ByteBuf value = byteBuf.readBytes(kvLength - keyLength);
            map.put(key.toString(UTF_8), value.toString(UTF_8));
        }

        return map;
    }
}
