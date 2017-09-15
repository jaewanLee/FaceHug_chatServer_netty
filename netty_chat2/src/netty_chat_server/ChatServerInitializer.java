package netty_chat_server;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class ChatServerInitializer extends ChannelInitializer<SocketChannel>{
//	 private final SslContext sslCtx;

//	    public ChatServerInitializer(SslContext sslCtx) {
//	        this.sslCtx = sslCtx;
//	    }
	    public ChatServerInitializer() {
	     
	    }
	    //맨처음 소켓이 연결되면 진행하는거
	    @Override
	    public void initChannel(SocketChannel ch) throws Exception {
	        ChannelPipeline pipeline = ch.pipeline();

	        // Add SSL handler first to encrypt and decrypt everything.
	        // In this example, we use a bogus certificate in the server side
	        // and accept any invalid certificates in the client side.
	        // You will need something more complicated to identify both
	        // and server in the real world.
	        //파이프라인은 이벤트의 핸들러들을 가지고있는 리스트같은거
//	        pipeline.addLast(sslCtx.newHandler(ch.alloc()));
	        //ssl을 위해서 코덱들 설정해주기
	        // On top of the SSL handler, add the text line codec.
	        pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
	        pipeline.addLast(new StringDecoder());
	        pipeline.addLast(new StringEncoder());

	        // and then business logic.
	        //실제 처리에 필요한 핸들러
	        pipeline.addLast(new ChatServerHandler());
	    }
	    public class MyHandler extends ChannelDuplexHandler {
	        @Override
	        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
	            if (evt instanceof IdleStateEvent) {
	                IdleStateEvent e = (IdleStateEvent) evt;
	                if (e.state() == IdleState.READER_IDLE) {
	                	System.out.println("idle reader_idle");
	                    ctx.close();
	                } else if (e.state() == IdleState.WRITER_IDLE) {
//	                    ctx.writeAndFlush(new PingMessage());
	                	System.out.println("idle writer_idle");
	                }
	            }
	        }
	    }
}
