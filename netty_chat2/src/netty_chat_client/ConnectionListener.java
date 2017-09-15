package netty_chat_client;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class ConnectionListener implements ChannelFutureListener{

	private ChatClient chatclien;
	static final int RECONNECT_DELAY = Integer.parseInt(System.getProperty("reconnectDelay", "2"));
	
	public ConnectionListener(ChatClient client) {
		this.chatclien=client;
		// TODO Auto-generated constructor stub
	}
	@Override
	public void operationComplete(ChannelFuture future) throws Exception {
		// TODO Auto-generated method stub
		if (!future.isSuccess()) {  
		       System.out.println("Reconnect");  
		       final EventLoopGroup loop = future.channel().eventLoop(); 
		       loop.schedule(new Runnable() {  
		         @Override  
		         public void run() {  
		        	 chatclien.createBootstrap(new Bootstrap(), loop);
		         }  
		       }, RECONNECT_DELAY, TimeUnit.SECONDS);  
		     }
		else{
			System.out.println("Connected");
		}
	
	}

}
