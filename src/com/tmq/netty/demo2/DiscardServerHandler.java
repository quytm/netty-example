package com.tmq.netty.demo2;

/**
 * Created by tmq on 07/12/2016.
 */


import io.netty.buffer.ByteBuf;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * Handles a server-side channel.
 */
public class DiscardServerHandler extends ChannelInboundHandlerAdapter { // (1)

//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2)
//        // Discard the received data silently.
////        ((ByteBuf) msg).release(); // (3)   --oldCode--
//
//        // We can check server is working by telnet
//
////        // Looking into the Received Data
////        ByteBuf in = (ByteBuf) msg;
////        try {
////            while (in.isReadable()) { // (1)
////                System.out.print((char) in.readByte());
////                System.out.flush();
////            }
////        } finally {
////            ReferenceCountUtil.release(msg); // (2)
////        }
//
//
////        // Echo Server
////        ctx.write(msg);
////        ctx.flush(); // We can instead of write() and flush() function of writeAndFlush() function
//
//    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) { // (1)
        final ByteBuf time = ctx.alloc().buffer(4); // (2)
        time.writeInt((int) (System.currentTimeMillis() / 1000L + 2208988800L));
        System.out.println("handler");

        final ChannelFuture f = ctx.writeAndFlush(time); // (3)
        f.addListener((ChannelFutureListener) future -> {
            assert f == future;
            ctx.close();
        }); // (4)
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}