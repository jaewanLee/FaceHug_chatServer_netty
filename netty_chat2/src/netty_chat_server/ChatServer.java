package netty_chat_server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

public class ChatServer {
	
	 static final int PORT = Integer.parseInt(System.getProperty("port", "8992"));

	    public static void main(String[] args) throws Exception {
	    	
//	        SelfSignedCertificate ssc = new SelfSignedCertificate();
//	        SslContext sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey())
//	            .build();

	        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
	        EventLoopGroup workerGroup = new NioEventLoopGroup();
	        try {
	            ServerBootstrap b = new ServerBootstrap();
	            b.group(bossGroup, workerGroup)
	            //nio방식으로 소켓을 열것임,즉 incoming한 대상들을어떻게 처리할것인지에 대한 이야기임.
	             .channel(NioServerSocketChannel.class)
	             //사용되는 핸들러의 종류는 logginghandler임.logginghandler란 
	             .handler(new LoggingHandler(LogLevel.INFO))
	             //새롭게 액세스된 channeld을 처리함
//	             .childHandler(new ChatServerInitializer(sslCtx));
	             .childHandler(new ChatServerInitializer());
	            //chaannelfutrue 이란 무엇을 하는것인고??작업이 완료되면이를 알리는 방법
	            //즉 여기서는 서버측과 포트 bind하는거를 block방식으로 대기하는게 아니라 sync방식으로 냅두다가
	            //다되면 알려주는것
	            //그래서 연결이 다되면 설정해두었던 bootstrap을 비동기방식으로 호출하여 설정에따라 실행시킴
	            ChannelFuture channelFuture=b.bind(PORT).sync();
	            //만일 채널이 닫히게 되면 알려주라는거임
	            //닫희면 finally실행해서 열려있던 소켓 모두 끊어줘야함
	            channelFuture.channel().closeFuture().sync();
	        } finally {
	            bossGroup.shutdownGracefully();
	            workerGroup.shutdownGracefully();
	        }
	    }
}
