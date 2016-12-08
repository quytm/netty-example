package com.tmq.netty.demo1.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Created by tmq on 03/12/2016.
 */
public class TimeStampDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        final int messageLength = Long.SIZE / Byte.SIZE * 2;
        if (byteBuf.readableBytes() < messageLength) {
            return;
        }

        byte[] ba = new byte[messageLength];
        byteBuf.readBytes(ba, 0, messageLength);  // block until read 16 bytes from sockets
        LoopBackTimeStamp loopBackTimeStamp = new LoopBackTimeStamp();
        loopBackTimeStamp.fromByteArray(ba);
        list.add(loopBackTimeStamp);
    }
}
