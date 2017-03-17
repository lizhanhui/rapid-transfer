package com.yeahmobi.lab.handler;

import com.yeahmobi.lab.protocol.DataFrame;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.ReferenceCountUtil;

import java.util.Map;

public class DataFrameEncoder extends MessageToByteEncoder<DataFrame> {

    @Override
    protected void encode(ChannelHandlerContext ctx, DataFrame msg, ByteBuf out) throws Exception {
        out.writeInt(0); // to be overridden
        out.writeInt(0); // to be overridden

        out.writeLong(msg.getId());
        encode(out, msg.getMap());

        out.setInt(4, out.writerIndex() - 4 - 4);

        if (null != msg.getBody()) {
            out.writeBytes(msg.getBody());
            ReferenceCountUtil.release(msg.getBody());
        }

        out.setInt(0, out.writerIndex() - 4 - 4);
    }

    private void encode(ByteBuf byteBuf, Map<String, String> map) {
        if (null == map || map.isEmpty()) {
            return;
        }

        byteBuf.writeInt(map.size());
        for (Map.Entry<String, String> next : map.entrySet()) {
            if (null == next.getKey() || null == next.getValue()) {
                continue;
            }
            byteBuf.writeInt(next.getKey().length() + next.getValue().length())
                    .writeInt(next.getKey().length());
            ByteBufUtil.writeUtf8(byteBuf, next.getKey());
            ByteBufUtil.writeUtf8(byteBuf, next.getValue());
        }
    }
}
