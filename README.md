# Netty Framework

## I. Các class trong Netty

Source code: https://github.com/quytm/netty-example/tree/master/src/com/tmq/netty/demo2

### ChannelInboundHandlerAdapter:

- Impliment ChannelInboundHandler: là class cung cấp phương thức để handler cho các sự kiện, và có thể override nó.
- ChannelHandlerContext: cung cấp những cách thức khác nhau để giúp bạn triggr các sự kiện IO và các operation.
	+ write(Object): viết các message nhận được từng ít một, và kết thúc bằng cách flush();
	+ flush(): kết thúc việc viết message.
- Các phương thức có thể override:
	+ channelRead(): được gọi khi nhận được message từ client, dữ liệu có kiểu: ByteBuf, ....
	+ channelAlive(): đăng kí khi có kết nối và sẵn sàng truyền dữ liệu
	+ exceptionCaught(): bắt ngoại lệ

### Trong Server
	
- NioEventLoopGroup: là multithread event loop mà nó handler các IO operation.
- EventLoopGroup: impliment cho các transport khác nhau
- ServerBootstrap: là một class helper sử dụng để cài đặt server. Bạn có thể cài server sử dụng Channel trực tiếp. Tuy nhiên trong một số trường hợp có thể không sử dụng trường hợp này.

### Bên Client

- Bootstrap: giống như ServerBootstrap

## II. Example

### II.1. Http

Source code: https://github.com/quytm/netty-example/tree/master/src/com/tmq/netty/http

(tham khảo từ: https://github.com/netty/netty/tree/4.1/example/src/main/java/io/netty/example/http)


#### II.1.1. Hello world

- Tạo class HttpHelloWorldServerInitializer extend ChannelInitializer\<SocketChannel\>
	+ Truyền SslContext vào constructor
	+ initChannel: pipeline.addLast(HttpHelloWorldServerHandler)
- Tạo class HttpHelloWorldServerHandler extend ChannelInboundHandlerAdapter:
	+ channelRead:

```
private static final byte[] CONTENT = { 'H', 'e', 'l', 'l', 'o', ' ', 'W', 'o', 'r', 'l', 'd' };

    private static final AsciiString CONTENT_TYPE = new AsciiString("Content-Type");
    private static final AsciiString CONTENT_LENGTH = new AsciiString("Content-Length");
    private static final AsciiString CONNECTION = new AsciiString("Connection");
    private static final AsciiString KEEP_ALIVE = new AsciiString("keep-alive");



if (msg instanceof HttpRequest) {
            HttpRequest req = (HttpRequest) msg;

            if (HttpUtil.is100ContinueExpected(req)) {
                ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
            }
            boolean keepAlive = HttpUtil.isKeepAlive(req);
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(CONTENT));
            response.headers().set(CONTENT_TYPE, "text/plain");
            response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());

            if (!keepAlive) {
                ctx.write(response).addListener(ChannelFutureListener.CLOSE);
            } else {
                response.headers().set(CONNECTION, KEEP_ALIVE);
                ctx.write(response);
            }
        }
```

#### II.1.2. Upload

##### a. Server

- Các bước đầu tiên tương tự trong example HelloWorl:
	+ Tạo bossGroup, workerGroup -> nhóm trong ServerBootstrap
	+ ServerBootstrap:
		+ channel: NioServerSocketChannel
		+ handler: LoggingHandler
		+ childHandler: class HttpUploadSererInittializer
		+ bind().sync()
- Class HttpUploadServerInitializer extends ChannelInitializer\<SocketChannel\>
	+ Truyền tham số SslContext vào Contructor
	+ InitChannel: pipeline addLast: 
		+ HttpRequestDecoder
		+ HttpResponseEncoder
		+ HttpContentCompressor
		+ HttpUploadServerHandler

- Class HttpUploadServerHandler extends SimpleChannelInboundHandler\<HttpObject\>:
	+ channelRead0(ctx, msg):
		+ Lấy HttpRequest từ msg (cast) -> request
		+ Có thể biết router đang ở đâu thông qua việc lấy uri: request.uri()
	+ Form Server cung cấp:
		+ Là đọan form html (có submit), lưu trong StringBuffer
		+ Cần chuyển đoạn StringBuffer đó sang ByteBuf
		+ Ta sử dụng FullHttpResponse để gửi response cho Client, trong đó có thể tùy biến: version http, response status, data (ByteBuf), header, ....

##### b. Client

- Khởi tạo Uri để GET/POST tới server
- Kiểm tra giao thức, cài đặt cổng (port)
- Header lưu dưới dạng: List\<Entry\<String, String\>\>
- Form GET:
	+ Channel kết nối tới host, port
	+ Sử dụng QueryStringEncoder, thêm các param -> chuyển QueryStringEncoder thành URI
	+ Sử dụng đối tượng HttpRequest request
	+ Set header cho request
	+ Channel write, flush, closeFuture

- Form POST:
	+ Cách làm tương tự Form GET
	+ Gửi data sử dụng đối tượng: HttpPostRequestEncoder: có thể gửi cả attribute và cả file (cần có thêm contenType cho file đó)

- Form POST Multipart
	+ ...

- Class HttpUploadClientInitializer extends ChannelInitialzer\<SocketChannel\>:
	+ Truyền SslContext vào constructor
	+ Pipeline addLast:
		+ HttpClientCodec
		+ HttpContentDecompressor
		+ ChuckedWriteHandler
		+ HttpUploadClientHandler

- Class HttpUploadClientHandler extends SimpleChannelInboundHandler\<HttpObject\>: nhận response từ server:
	+ Cast msg sang response: HttpResponse
	+ Cast msg sang chunk: HttpContent
