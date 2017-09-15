package netty_chat_server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.google.gson.Gson;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;
import message_type.Chat;
import message_type.ChatMessageFormat;
import message_type.Message_format;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class ChatServerHandler extends SimpleChannelInboundHandler<String> {

	static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	static final HashMap<String, ChannelHandlerContext> idToChannel = new HashMap<>();
	static final HashMap<ChannelHandlerContext, String> channeltoId=new HashMap<>();

	static final int ENROOL = 1;
	static final int MESSAGE=2;
	static final int PUSH=3;
	static final int FRIEND=4; 
	static final int VIDEOCHAT=5;
	
	JedisPoolConfig jedisPoolConfig;
	JedisPool pool;
	Jedis jedis;

	// ctx는 파이프라인과 핸들러관계정보를 지니고있음
	// 처음 등록될때 실행됨
	@Override
	public void channelActive(final ChannelHandlerContext ctx) {

//		try {
//			ctx.writeAndFlush(
//					"Welcome to " + InetAddress.getLocalHost().getHostName() + " secure chat service!\n");
//				} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		

		// 내가 가지고잇는 채널리스트 등록시켜줌
		channels.add(ctx.channel());
	}

	// 읽을게 들어올경우 이를 방안에 들어온 전부에게 뿌려줌
	@Override
	public void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		// Send the received message to all channels but the current one.

		// 받은 메세지를 getChat Class로 변경시킴
		System.out.println("받 	은 메세지 : " + msg);
		Gson gson = new Gson();
		Message_format message_format=gson.fromJson(msg, Message_format.class);

		// 해당 메세지의 타입이 어떤것인지 확인하기
		switch (message_format.getType()) {
		case ENROOL:
			// 보낸사람의 소켓정보가 hash에 저장되어 있는지 확인하고 저장되지 않앗다면 저장하기
			if (!idToChannel.containsKey(message_format.getFrom_user())) {
				idToChannel.put(message_format.getFrom_user(), ctx);
				channeltoId.put(ctx, message_format.getFrom_user());
				System.out.println("사용자가 등록되었습니다"+message_format.getFrom_user());
				message_format.setContext("서버에 정상적으로 등록되었습니다");
				ctx.writeAndFlush(gson.toJson(message_format)+"\r\n");
				// ctx.channel().writeAndFlush("서버에 정상적으로 등록되었습니다");
			} else {
				System.out.println(message_format.getTo_user()+"는 이미 등록되어있는 사용자입니다");
				ChannelHandlerContext duplicated_ctx=idToChannel.get(message_format.getFrom_user());
				channeltoId.remove(duplicated_ctx);
				duplicated_ctx.close();
				idToChannel.put(message_format.getFrom_user(), ctx);
				channeltoId.put(ctx, message_format.getFrom_user());
				message_format.setContext("이미 등록되어있는 사용자입니다");
				ctx.writeAndFlush(gson.toJson(message_format)+"\r\n");
				// ctx.channel().writeAndFlush("이미 등록되어있는 사용자입니다");
			}
//			String network_update_date=message_format.getTime();
			System.out.println("메세지 동기화를 시작합니다");
			jedisPoolConfig = new JedisPoolConfig();
	    	pool = new JedisPool(new JedisPoolConfig(), "localhost");
	    	jedis = pool.getResource();
	    	while(true){
	    		String value=jedis.lpop(message_format.getFrom_user());
	    		if(value==null){
	    			System.out.println("동기화 할 메시지가 없습니다");
	    			break;
	    		}
	    		Message_format message_format_old=gson.fromJson(value, Message_format.class);
    			System.out.println("old 메세지 시간:"+message_format_old.getTime()+"현재 등록되는 시간:"+message_format.getTime());
    			System.out.println(String.valueOf(message_format_old.getTime().compareTo(message_format.getTime())));
	    		if(message_format_old.getTime().compareTo(message_format.getTime())>0){
	    			System.out.println("메세지 동기화 : "+value);
	    			ctx.writeAndFlush(value+"\r\n");
	    			continue;
	    		}
	    		else{
	    			System.out.println("이미 받은 메세지는 삭제");
	    			continue;
	    		}
	    	}
	    	if( jedis != null ){
	    		jedis.close();
	    	}
			//redis에 받는사람과 날짜로 저장,실제 메세지 내용은 string으로 구현되어있어서 그거 그대로 넘겨주기
			//서버측에서 가져와야될꺼 있는지 확인하기
			
			break;
		case MESSAGE:
			ChatMessageFormat chatMessageFormat=gson.fromJson(message_format.getContext(), ChatMessageFormat.class);
			ArrayList<String> to_user=chatMessageFormat.getTo_user();
			System.out.println("user list"+idToChannel.keySet());
			
			for(int i=0;i<to_user.size();i++){
				//현재 사용자가 인터넷 접속 중일경우
				if(idToChannel.containsKey(to_user.get(i))){
					ChannelHandlerContext channelHandlerContext=idToChannel.get(to_user.get(i));
					if(channelHandlerContext!=null){
						System.out.println("message to "+to_user.get(i)+" : "+message_format.getContext()+"\r\n");
						channelHandlerContext.writeAndFlush(msg+"\r\n");
						jedisPoolConfig = new JedisPoolConfig();
				    	pool = new JedisPool(new JedisPoolConfig(), "localhost");
				    	jedis = pool.getResource();	
				    	jedis.rpush(to_user.get(i), msg);
				    	System.out.println("redis에 메세지 내용 저장");	
					}
					else{
						System.out.println(to_user.get(i)+"님이 접속중이지 않습니다");
						//redis에 추가
											
					}
				}
				//현재 사용자가 인터넷 접속이 안되어있을 경우
				else{
					System.out.println(to_user.get(i)+"님이 접속중이지 않습니다");
					//redis에 추가
					jedisPoolConfig = new JedisPoolConfig();
			    	pool = new JedisPool(new JedisPoolConfig(), "localhost");
			    	jedis = pool.getResource();	
			    	jedis.rpush(to_user.get(i), msg);
			    	System.out.println("redis에 메세지 내용 저장");
			    	
					
				}
			}
			break;
		case VIDEOCHAT:
			String target_user=message_format.getTo_user();
			ChannelHandlerContext channelHandlerContext=idToChannel.get(target_user);
			if(channelHandlerContext!=null){
				System.out.println("message to"+target_user+":"+msg);
				channelHandlerContext.writeAndFlush(msg+"\r\n");	
			}
			else{
				System.out.println(target_user+"님이 접속중이지 않습니다");				
			}
			break;
		}
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		if(ctx!=null){
			String user_id=channeltoId.get(ctx);
			channeltoId.remove(ctx);
			if(user_id!=null){
				idToChannel.remove(user_id);
				System.out.println(ctx.channel().remoteAddress() +"  ,"+user_id+"에서 접속이 종료되었습니다");
			}
		}
		super.channelUnregistered(ctx);
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		System.out.println(ctx.channel().remoteAddress()+"에서 channelInactive합니다");
		super.channelInactive(ctx);
	}

	@Override
	public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		System.out.println(ctx.channel().remoteAddress() + "에서 쓰기 요청을 보냈습니다.");
		super.channelWritabilityChanged(ctx);
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		super.channelRegistered(ctx);
		System.out.println(ctx.channel().remoteAddress() + "에서 접속하였습니다");
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		System.out.println(ctx.channel().remoteAddress() + "에서 오류가 발생하였습니다");
		cause.printStackTrace();
		ctx.close();
	}

}
