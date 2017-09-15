package netty_chat_client;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;

public class ChatClientHandler extends SimpleChannelInboundHandler<String>{
	
	ChatClient chatClient;
	static final int RECONNECT_DELAY = Integer.parseInt(System.getProperty("reconnectDelay", "2"));
	public ChatClientHandler(ChatClient chatClient) {
		// TODO Auto-generated constructor stub
		this.chatClient=chatClient;
	}
	@Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.err.println(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        System.out.println("exception cautgh");
        ctx.close();
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	// TODO Auto-generated method stub
    	final EventLoop eventLoop = ctx.channel().eventLoop();  
        eventLoop.schedule(new Runnable() {  
          @Override  
          public void run() {  
            chatClient.createBootstrap(new Bootstrap(), eventLoop);  
          }  
        }, RECONNECT_DELAY, TimeUnit.SECONDS); 
    	super.channelInactive(ctx);
    	System.out.println("channelInactive");
    }
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
    	// TODO Auto-generated method stub
    	super.channelUnregistered(ctx);
    	System.out.println("channelUnregistered");
    }

}
