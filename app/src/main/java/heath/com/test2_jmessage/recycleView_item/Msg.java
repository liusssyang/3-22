package heath.com.test2_jmessage.recycleView_item;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.gson.Gson;

import cn.jpush.im.android.api.model.Message;
import heath.com.test2_jmessage.R;
import heath.com.test2_jmessage.application.MyApplication;
import heath.com.test2_jmessage.tools.App;


public class Msg {
    public static final int TYPE_RECEIVED=0;
    public static final int TYPE_SENT=1;
    private String content;
    private Bitmap icon;
    private int type;
    private int messageId;
    private String userName;
    private String appKey;
    private Message message;
    private String IsFileUploaded;
    private String LocalThumbnailPath;
    private boolean dialogIsOpen=false;
    public Msg (Message message){
        Gson gson = new Gson();
        App app = gson.fromJson(message.getContent().toJson(), App.class);
        if (message.getDirect().toString().equals("receive"))
            type= 0;
        else type=1;
        this.userName=message.getFromUser().getUserName();
        this.appKey=message.getFromUser().getAppKey();
        this.messageId=message.getId();
        this.content=app.getText();
        this.LocalThumbnailPath=app.getLocalThumbnailPath();
        this.IsFileUploaded=app.getIsFileUploaded();
        this.dialogIsOpen=app.getLocalThumbnailPath() != null;
        this.message=message;
    }
    public Msg(String IsFileUploaded,
               String LocalThumbnailPath,
               String userName,
               String appKey,
               String content,
               int type,
               int messageId,
               boolean dialogIsOpen){
        this.LocalThumbnailPath= LocalThumbnailPath;
        this.content=content;
        this.type=type;
        this.IsFileUploaded=IsFileUploaded;
        this.messageId=messageId;
        this.userName=userName;
        this.appKey=appKey;
        this.dialogIsOpen=dialogIsOpen;
    }


    public String getLocalThumbnailPath(){return this.LocalThumbnailPath;}
    public String getIsFileUploaded(){return IsFileUploaded;}
    public Message getMessage(){return message;}
    public boolean dialogIsOpen(){return dialogIsOpen;}
    public String getUserName(){return userName;}
    public String getAppKey(){return appKey; }
    public int getId(){return messageId;}
    public String getContent(){
        return content;
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
