package message_type;

import java.util.Date;

/**
 * Created by jaewan on 2017-05-09.
 */

public class Message_format {
    private int type;
    private String context;
    private String from_user;
    private String to_user;
    private String time;
    public Message_format(){
    }

    public void setContext(String context) {
        this.context = context;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setFrom_user(String from_user) {
        this.from_user = from_user;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setTo_user(String to_user) {
        this.to_user = to_user;
    }

    public int getType() {
        return type;
    }

    public String getFrom_user() {
        return from_user;
    }

    public String getContext() {
        return context;
    }

    public String getTime() {
        return time;
    }

    public String getTo_user() {
        return to_user;
    }
}
