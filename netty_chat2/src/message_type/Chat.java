package message_type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Chat {
	int type;
	ArrayList<String> to;
	String from;
	String contex;
	String roomId;
	public Chat(){
		this.type=-1;
		this.to=new ArrayList<String>();
		this.from="";
		this.contex="";
		this.roomId="";
	}
	public Chat(int type,ArrayList<String> to,String from,String contex){
		this.type=type;
		this.to=to;
		this.from=from;
		this.contex=contex;
	}
	public int getType(){
		return this.type;
	}
	public ArrayList<String> getTo(){
		//to를 정렬해줌
		Collections.sort(to, new Comparator<String>(){
		      public int compare(String obj1, String obj2)
		      {     return obj1.compareToIgnoreCase(obj2);
		     
		      }
		});
		return this.to;
	}
	public String getFrom(){
		return this.from;
	}
	public String getContext(){
		return this.contex;
	}
//	public String getRoomId(){
//		String roomId=to.get(0);
//		for(int i=0;i<to.size();i++){
//			roomId+=","+to.get(i);
//		}
//		return roomId;
//	}
	public String getRoomId(){
		return this.roomId;
	}
	public void setType(int type){
		this.type=type;
	}
	public void setTo(String user_id){
		this.to.add(user_id);
	}
	public void setFrom(String from){
		this.from=from;
	}
	public void setContext(String context){
		this.contex=context;
	}
	public void setRoomId(String roomId){
		this.roomId=roomId;
	}
	public void removeTo(){
		this.to=new ArrayList<String>();
	}

}
