package com.tmq.netty.http.requestapi;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

/**
 * Created by tmq on 09/12/2016.
 */

public class HttpReqAPIClientHandler extends SimpleChannelInboundHandler<HttpObject> {

    private StringBuffer responseString = new StringBuffer();

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) {
        if (msg instanceof HttpContent) {
            HttpContent chunk = (HttpContent) msg;
            responseString.append(chunk.content().toString(CharsetUtil.UTF_8));

            if (chunk instanceof LastHttpContent) {
                System.out.println("Response: ---------------------------");
                System.out.println(responseString);
                System.out.println("-------------------------------------\n\n");
                responseString = new StringBuffer();
            }
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.channel().close();
    }
}
