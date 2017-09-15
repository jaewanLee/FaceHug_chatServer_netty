package message_type;

/**
 * Created by jaewan on 2017-05-16.
 */

public class User {

    private int user_no;
    private String user_id;
    private String name;
    private String phone;
    private String udpate_date;
    private String udpate_img_date;
    private String img;
    private String type;
    private String etc;
    public User(){
    }

    public int getUser_no() {
        return user_no;
    }

    public String getEtc() {
        return etc;
    }

    public String getImg() {
        return img;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getType() {
        return type;
    }

    public String getUdpate_date() {
        return udpate_date;
    }

    public String getUdpate_img_date() {
        return udpate_img_date;
    }

    public String getUser_id() {
        return user_id;
    }
    public void setEtc(String etc) {
        this.etc = etc;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUdpate_date(String udpate_date) {
        this.udpate_date = udpate_date;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setUser_no(int user_no) {
        this.user_no = user_no;
    }

    public void setUdpate_img_date(String udpate_img_date) {
        this.udpate_img_date = udpate_img_date;
    }
}
