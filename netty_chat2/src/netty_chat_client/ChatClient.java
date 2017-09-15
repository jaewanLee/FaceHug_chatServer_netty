package netty_chat_client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.net.ssl.SSLException;

import com.google.gson.Gson;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import message_type.Chat;
import message_type.ChatMessageFormat;
import message_type.Message_format;

public class ChatClient {
//	 static final String HOST = System.getProperty("host", "115.71.233.22");
	static final String HOST = System.getProperty("host", "127.0.0.1");
	static final int PORT = Integer.parseInt(System.getProperty("port", "8992"));

	static final int ENROOL = 1;
	static final int FIND_PEOPLE = 2;
	static final int CHOOSE_FRIEND = 3;
	static final int SEND = 4;
	static final int MAKE_ROOM = 5;
	static final int ENTER_ROOM = 6;
	static final int SEND_ROOM = 7;
	static final int FINISH = 8;
	
	static final int RECONNECT_DELAY = Integer.parseInt(System.getProperty("reconnectDelay", "5"));
	private EventLoopGroup loop = new NioEventLoopGroup();  
	
	public Bootstrap createBootstrap(Bootstrap bootstrap, EventLoopGroup eventLoop) {  
	     if(bootstrap!=null){
	    	// netty소켓의 설정해주기
				try {
					
					bootstrap.group(eventLoop)
							// 소켓 방식
							.channel(NioSocketChannel.class)
							// 요청이 들어오면 가장 처음 어떻게 할것인지 정의함
//							.handler(new ChatClientInitializer(sslCtx));
							.handler(new ChatClientInitializer(this));

					// Start the connection attempt.
					ChannelFuture channelFuture=bootstrap.connect(HOST, PORT);
					channelFuture.addListener(new ConnectionListener(this));
					Channel ch = channelFuture.sync().channel();
					
					

					// Read commands from the stdin.
					ChannelFuture lastWriteFuture = null;

					BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
					System.out.println("사용자의 아이디를 입력하주세요");
					System.out.println("========================================================");
					String user_id = in.readLine();
					System.out.println("your id:" + user_id);
					Chat sendChat = new Chat();
					sendChat.setFrom(user_id);
					Gson gson = new Gson();
					int checker = 0;


					Message_format message_format=new Message_format();
					ChatMessageFormat chatMessageFormat=new ChatMessageFormat();
					for (;;) {
						System.out.println("1.등록\n2.접속한 사람 확인하기\n3.보낼 사람 선택\n4.푸쉬 보내기\n5.방만들기\n6.방들어가기\n7.방에 메세지 보내기\n8.끝내기");
						String line = in.readLine();
						if (line == null) {
							System.out.println("not null");
							break;
						}
						// 등록하기
						if ("1".equals(line)) {
							
							message_format.setType(ENROOL);
							message_format.setFrom_user(user_id);
							lastWriteFuture=ch.writeAndFlush(gson.toJson(message_format)+"\r\n");
							
							checker = 1;
						}
						// 접속한 사람 확인하기
						else if ("2".equals(line)) {
							if (checker < 1) {
								System.out.println("서버에 등록을 해주세요");
							} else {
								System.out.println("추가할 사용자의 아이디를 입력하세요");
								String addFriend = in.readLine();
								message_format.setType(FIND_PEOPLE);
								ArrayList<String> to_user=new ArrayList<String>();
								to_user.add(user_id);
								to_user.add(addFriend);
								chatMessageFormat.setFrom_user(user_id);
								chatMessageFormat.setContext("hhh");
								chatMessageFormat.setTo_user(to_user);
								
								message_format.setContext(gson.toJson(chatMessageFormat));

								lastWriteFuture = ch.writeAndFlush(gson.toJson(message_format) + "\r\n");
								checker = 2;
							}
						}
						// 보낼 사람 선택하기
						else if ("3".equals(line)) {
							System.out.println("추가할 사용자의 아이디를 입력하세요");
							String addFriend = in.readLine();
							sendChat.setTo(addFriend);
							checker = 3;
						}
						// 메세지 보내기
						else if ("4".equals(line)) {
							if (checker < 3) {
								System.out.println("보낼사람을 선택하세요");
							} else {
								System.out.println("메세지를 입력하세요");
								String message = in.readLine();
								sendChat.setContext(message);
								sendChat.setType(SEND);
								lastWriteFuture = ch.writeAndFlush(gson.toJson(sendChat) + "\r\n");
								sendChat.removeTo();
								sendChat.setTo(user_id);
							}
						}
						// 방만들기
						else if ("5".equals(line)) {
							sendChat.setType(MAKE_ROOM);
							System.out.println("방이름을 입력하세요");
							String roomId=in.readLine();
							sendChat.setRoomId(roomId);
							lastWriteFuture = ch.writeAndFlush(gson.toJson(sendChat) + "\r\n");
						}
						// 방 들어가기
						else if ("6".equals(line)) {
							System.out.println("접속할 방이름을 입력하세요");
							String roomId=in.readLine();
							sendChat.setRoomId(roomId);
						}
						// 방에 메시지 보내기
						else if ("7".equals(line)) {
							sendChat.setType(SEND_ROOM);
							System.out.println("메시지를 입력하세요");
							String message=in.readLine();
							sendChat.setContext(message);
							lastWriteFuture = ch.writeAndFlush(gson.toJson(sendChat) + "\r\n");
							sendChat.removeTo();
							sendChat.setTo(user_id);

						}
						// 끝내기
						else if ("8".equals(line)) {
							sendChat.setType(FINISH);
							lastWriteFuture = ch.writeAndFlush(gson.toJson(sendChat) + "\r\n");
							ch.closeFuture().sync();
							break;
						}
						else {
						}

					}
					// Wait until all messages are flushed before closing the channel.
					if (lastWriteFuture != null) {
						lastWriteFuture.sync();
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
	     }
		return bootstrap;		     
	   }  

	public static void main(String[] args) throws Exception {
		// Configure SSL.
//		final SslContext sslCtx = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE)
//				.build();
		new ChatClient().run();
	}
	public void run(){
		createBootstrap(new Bootstrap(), loop);
	}
}
