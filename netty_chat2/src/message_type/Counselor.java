package message_type;

/**
 * Created by jaewan on 2017-05-16.
 */

public class Counselor {
    int counsleor_no;
    String counselor_id;
    String name;
    String phone;
    String udpate_date;
    String introcude;
    String detail_produce;
    String domain;
    String img;
    public Counselor(){

    }

    public void setUdpate_date(String udpate_date) {
        this.udpate_date = udpate_date;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setCounselor_id(String counselor_id) {
        this.counselor_id = counselor_id;
    }

    public void setCounsleor_no(int counsleor_no) {
        this.counsleor_no = counsleor_no;
    }

    public void setDetail_produce(String detail_produce) {
        this.detail_produce = detail_produce;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setIntrocude(String introcude) {
        this.introcude = introcude;
    }

    public String getUdpate_date() {
        return udpate_date;
    }

    public String getPhone() {
        return phone;
    }

    public String getName() {
        return name;
    }

    public String getCounselor_id() {
        return counselor_id;
    }

    public int getCounsleor_no() {
        return counsleor_no;
    }

    public String getDetail_produce() {
        return detail_produce;
    }

    public String getDomain() {
        return domain;
    }

    public String getImg() {
        return img;
    }

    public String getIntrocude() {
        return introcude;
    }
}
