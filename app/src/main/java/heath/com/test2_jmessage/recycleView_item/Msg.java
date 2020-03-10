package heath.com.test2_jmessage.recycleView_item;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import heath.com.test2_jmessage.R;
import heath.com.test2_jmessage.application.MyApplication;


public class Msg {
    public static final int TYPE_RECEIVED=0;
    public static final int TYPE_SENT=1;
    private String content;
    private Bitmap image;
    private Bitmap icon;
    private int type;
    private int messageId;
    private String userName;
    private String appKey;

    public Msg(Bitmap image,String content,int type){
        this.content=content;
        this.type=type;
        this.image=image;
    }
    public Msg(String userName,String appKey,Bitmap icon,Bitmap image,String content,int type,int messageId){
        this.content=content;
        this.type=type;
        this.image=image;
        this.icon=icon;
        this.messageId=messageId;
        this.userName=userName;
        this.appKey=appKey;
    }
    public Msg(Bitmap icon,Bitmap image,String content,int type){
        this.content=content;
        this.type=type;
        this.image=image;
        this.icon=icon;
    }
    public String getUserName(){return userName;}
    public String getAppKey(){return appKey; }
    public int getId(){return messageId;}
    public String getContent(){
        return content;
    }
    public Bitmap getImageContent(){
        return image;
    }
    public Bitmap getIconContent(){
        if (this.icon==null)
            return BitmapFactory.decodeResource(MyApplication.getContext().getResources(), R.drawable.icon_right_default);
        else
            return this.icon;
    }
    public int getType(){
        return type;
    }
    public void setIcon(Bitmap icon){
        if (icon==null)
            this.icon= BitmapFactory.decodeResource(MyApplication.getContext().getResources(), R.drawable.icon_right_default);
        else
            this.icon=icon;
    }
}
