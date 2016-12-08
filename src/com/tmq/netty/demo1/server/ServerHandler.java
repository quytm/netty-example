package com.tmq.netty.demo1.server;

import com.tmq.netty.demo1.common.LoopBackTimeStamp;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * Created by tmq on 03/12/2016.
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        LoopBackTimeStamp loopBackTimeStamp = (LoopBackTimeStamp) msg;

        loopBackTimeStamp.setRecvTimeStamp(System.nanoTime());

        System.out.println("Loop delay in: " + 1.0 * loopBackTimeStamp.timeLapseInNanoSecond() / 1000000L );
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.ALL_IDLE) { // idle for no read and write
                ctx.writeAndFlush(new LoopBackTimeStamp());
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}
