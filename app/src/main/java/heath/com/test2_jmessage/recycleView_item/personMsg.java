package heath.com.test2_jmessage.recycleView_item;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import heath.com.test2_jmessage.R;
import heath.com.test2_jmessage.application.IMDebugApplication;

public class personMsg {

    private String name;
    private String sendName;
    private String simpleMessage;
    private String appkey;
    private String time;
    private Bitmap bitmap;

    public personMsg(String name,String appkey,String sendName){
        this.name=name;
        this.appkey=appkey;
        this.sendName=sendName;
    }
    public personMsg(Bitmap icon,String name,String appkey,String sendName,String simpleMessage,String time){
        this.name=name;
        this.appkey=appkey;
        this.sendName=sendName;
        this.simpleMessage=simpleMessage;
        this.time=time;
        this.bitmap=icon;
    }
    public String getName(){
        return name;
    }
    public String getSendName(){return sendName;}
    public String getAppkey(){return appkey;}
    public String getSimpleMessage(){return simpleMessage;}
    public String getTime(){return time;}
    public Bitmap getBitmap(){
        if (this.bitmap==null)
            return BitmapFactory.decodeResource(IMDebugApplication.getContext().getResources(), R.drawable.icon_right_default);
        else
            return  bitmap;
    }
    public void setName(String name){this.name=name;}
    public void setSendName(String Sendname){this.sendName=Sendname;}
    public void setAppkey(String Appkey){this.appkey=Appkey;}
    public void setSimpleMessage(String Simplemessage){this.simpleMessage=Simplemessage;}
    public void setTime(String time){this.time=time;}
    public void setBitmap(Bitmap bitmap){
        if (bitmap==null)
            this.bitmap=BitmapFactory.decodeResource(IMDebugApplication.getContext().getResources(), R.drawable.icon_right_default);
        else
            this.bitmap=bitmap;
    }

}
