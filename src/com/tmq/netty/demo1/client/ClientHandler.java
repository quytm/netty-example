package com.tmq.netty.demo1.client;

import com.tmq.netty.demo1.common.LoopBackTimeStamp;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by tmq on 03/12/2016.
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("ClientHandler: write and flush");
        LoopBackTimeStamp loopBackTimeStamp = (LoopBackTimeStamp) msg;

        ctx.writeAndFlush(loopBackTimeStamp);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
