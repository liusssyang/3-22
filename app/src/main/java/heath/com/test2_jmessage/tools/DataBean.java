package heath.com.test2_jmessage.tools;

import java.io.Serializable;

import cn.jpush.im.android.api.model.Message;

public class DataBean implements Serializable {

    private String name;

    private String sex;
    private Message message;
    public void setMessage(Message message){
        this.message=message;
    }
    public Message getMessage(){
        return this.message;
    }

    public String getName() {

        return name;

    }

    public void setName(String name) {

        this.name = name;

    }

    public String getSex() {

        return sex;

    }

    public void setSex(String sex) {

        this.sex = sex;

    }

}
