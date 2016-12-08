package com.tmq.netty.demo1.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by tmq on 03/12/2016.
 */
public class TimeStampEncoder extends MessageToByteEncoder<LoopBackTimeStamp> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, LoopBackTimeStamp loopBackTimeStamp, ByteBuf byteBuf) throws Exception {
        byteBuf.writeBytes(loopBackTimeStamp.toByteArray());
    }
}
