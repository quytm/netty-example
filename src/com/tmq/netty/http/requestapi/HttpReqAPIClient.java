package com.tmq.netty.http.requestapi;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.HttpPostRequestEncoder;
import io.netty.handler.ssl.SslContext;

import java.net.URI;

/**
 * Created by tmq on 09/12/2016.
 *
 * Vi du ve Request Server thong qua API co san
 *
 * Host: http://api-v2.uetf.me
 * Port: 80
 *
 * API:
 *      - Login:
 *          + url: http://api-v2.uetf.me/login
 *          + params:
 *              email: String
 *              password: String
 *          + Account co san:
 *              email: khoiln@vnu.edu.vn
 *              password: 123456
 *
 *      - Logout:
 *          + url: http://api-v2.uetf.me/logout
 *          + headers:
 *              authorization: String (la authorization nhan duoc sau khi Login thanh cong)
 */
public class HttpReqAPIClient {
    private static final String BASE_URL = "http://api-v2.uetf.me";
    private static final String LOGIN = "/login";

    public static void main(String[] args) throws Exception {
        String urlLogin = BASE_URL + LOGIN;

        URI uriLogin = new URI(urlLogin);

        System.out.println("uri: Scheme: " + uriLogin.getScheme());
        System.out.println("uri: Host: " + uriLogin.getHost());
        System.out.println("uri: Port: " + uriLogin.getPort());

        // Vi la http request nen cho ssl = null, neu https thi phai khoi tao cho no
        final SslContext sslCtx = null;

        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class).handler(new HttpReqAPIClientInitializer(sslCtx));

            // http request: port = 80
            // https request: port = 443
            Channel channel = b.connect(uriLogin.getHost(), 80).sync().channel();

//            // Co the su dung QueryStringEncoder de them param cho uri
//            QueryStringEncoder encoder = new QueryStringEncoder(urlLogin);
//            URI uriGet = new URI(encoder.toString());

            HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, uriLogin.toASCIIString());

            // header: (Khong hieu, de mac dinh theo Example cua Netty)----------------------------------------------------------
            HttpHeaders headers = request.headers();
            headers.set(HttpHeaderNames.HOST, uriLogin.getHost());
            headers.set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
            headers.set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP + "," + HttpHeaderValues.DEFLATE);

            headers.set(HttpHeaderNames.ACCEPT_CHARSET, "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
            headers.set(HttpHeaderNames.ACCEPT_LANGUAGE, "fr");
            headers.set(HttpHeaderNames.REFERER, uriLogin.toString());
            headers.set(HttpHeaderNames.USER_AGENT, "Netty Simple Http Client side");
            headers.set(HttpHeaderNames.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");

            //connection will not close but needed
            // headers.set("Connection","keep-alive");
            // headers.set("Keep-Alive","300");

            headers.set(
                    HttpHeaderNames.COOKIE, io.netty.handler.codec.http.cookie.ClientCookieEncoder.STRICT.encode(
                            new io.netty.handler.codec.http.cookie.DefaultCookie("my-cookie", "foo"),
                            new io.netty.handler.codec.http.cookie.DefaultCookie("another-cookie", "bar"))
            );

            // --------------------------- Set body --------------------------------------------------------------------
            // false => khong su dung multipart
            HttpPostRequestEncoder bodyRequestEncoder = new HttpPostRequestEncoder(request, false);

            // add them attribute cho request
            bodyRequestEncoder.addBodyAttribute("email", "khoiln@vnu.edu.vn");
            bodyRequestEncoder.addBodyAttribute("password", "123456");

            // finalize request
            request = bodyRequestEncoder.finalizeRequest();
            // ---------------------------------------------------------------------------------------------------------

            // send request
            channel.write(request);

            // Copy (chunk????)
            if (bodyRequestEncoder.isChunked()) { // test if request was chunked and if so, finish the write
                // either do it through ChunkedWriteHandler
                channel.write(bodyRequestEncoder);
            }
            channel.flush();

            channel.closeFuture().sync();

        } finally {
            group.shutdownGracefully();
        }
    }
}
