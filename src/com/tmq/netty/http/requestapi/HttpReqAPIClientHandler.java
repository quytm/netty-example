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
//        if (msg instanceof HttpResponse) {
//            HttpResponse response = (HttpResponse) msg;
//
//            System.err.println("STATUS: " + response.status());
//            System.err.println("VERSION: " + response.protocolVersion());
//
//            if (!response.headers().isEmpty()) {
//                for (CharSequence name : response.headers().names()) {
//                    for (CharSequence value : response.headers().getAll(name)) {
//                        System.err.println("HEADER: " + name + " = " + value);
//                    }
//                }
//            }
//
//            if (response.status().code() == 200 && HttpUtil.isTransferEncodingChunked(response)) {
//                readingChunks = true;
//                System.err.println("CHUNKED CONTENT {");
//            } else {
//                System.err.println("CONTENT {");
//            }
//        }
//        if (msg instanceof HttpContent) {
//            HttpContent chunk = (HttpContent) msg;
//            System.err.println(chunk.content().toString(CharsetUtil.UTF_8));
//
//            if (chunk instanceof LastHttpContent) {
//                if (readingChunks) {
//                    System.err.println("} END OF CHUNKED CONTENT");
//                } else {
//                    System.err.println("} END OF CONTENT");
//                }
//                readingChunks = false;
//            } else {
//                System.err.println(chunk.content().toString(CharsetUtil.UTF_8));
//            }
//        }

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
