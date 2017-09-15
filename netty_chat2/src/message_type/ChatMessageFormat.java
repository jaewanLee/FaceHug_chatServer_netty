package message_type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by jaewan on 2017-05-09.
 */

public class ChatMessageFormat {
    ArrayList<String> to_user;
    String from_user;
    String context;
    String fromType;

    public ChatMessageFormat(){

    }
    public ChatMessageFormat(ArrayList<String> to_user,String from_user,String context){
        this.to_user=to_user;
        this.from_user=from_user;
        this.context=context;
    }

    public String getFromType() {
        return fromType;
    }

    public String getContext() {
        return context;
    }

    public String getFrom_user() {
        return from_user;
    }

    public ArrayList<String> getTo_user() {
        return to_user;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public void setTo_user(ArrayList<String> to_user) {
        this.to_user = to_user;
    }

    public void setFrom_user(String from_user) {
        this.from_user = from_user;
    }

    public void setFromType(String fromType) {
        this.fromType = fromType;
    }

    public String getRoomId(){
        String roomId="roomId_";
        Collections.sort(to_user, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareToIgnoreCase(o2);
            }
        });
        for(int i=0;i<to_user.size();i++){
            roomId+=to_user.get(i)+",";
        }
        return  roomId;
    }
}
