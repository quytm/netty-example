package com.tmq.netty.demo1.client;

import com.tmq.netty.demo1.common.TimeStampDecoder;
import com.tmq.netty.demo1.common.TimeStampEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Created by tmq on 03/12/2016.
 */
public class NettyClient {
    public static void main(String[] args) {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(workerGroup);
        b.channel(NioSocketChannel.class);

        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new TimeStampEncoder(),new TimeStampDecoder(),new ClientHandler());
                System.out.println("Handler client");
            }
        });

//        String serverIp = "192.168.203.156";
        String serverIp = "localhost";
        b.connect(serverIp, 19000);

        System.out.println("Start Client");

    }
}
