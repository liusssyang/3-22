package heath.com.test2_jmessage.recycleView_item;

public class personMsg {

    private String name;
    private String sendName;
    private String simpleMessage;
    private String appkey;
    private String time;

    public personMsg(String name,String appkey,String sendName){
        this.name=name;
        this.appkey=appkey;
        this.sendName=sendName;
    }
    public personMsg(String name,String appkey,String sendName,String simpleMessage,String time){
        this.name=name;
        this.appkey=appkey;
        this.sendName=sendName;
        this.simpleMessage=simpleMessage;
        this.time=time;
    }
    public String getName(){
        return name;
    }
    public String getSendName(){return sendName;}
    public String getAppkey(){return appkey;}
    public String getSimpleMessage(){return simpleMessage;}
    public String getTime(){return time;}
    public void setName(String name){this.name=name;}
    public void setSendName(String Sendname){this.sendName=Sendname;}
    public void setAppkey(String Appkey){this.appkey=Appkey;}
    public void setSimpleMessage(String Simplemessage){this.simpleMessage=Simplemessage;}
    public void setTime(String time){this.time=time;}

}
