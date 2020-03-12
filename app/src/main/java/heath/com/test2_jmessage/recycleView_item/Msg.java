package heath.com.test2_jmessage.recycleView_item;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.gson.Gson;

import cn.jpush.im.android.api.model.Message;
import heath.com.test2_jmessage.R;
import heath.com.test2_jmessage.application.MyApplication;
import heath.com.test2_jmessage.tools.App;

import static heath.com.test2_jmessage.application.MyApplication.personList;


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
    private Message message;
    private String IsFileUploaded;
    private boolean dialogIsOpen=false;

    public Msg(Bitmap image,String content,int type){
        this.content=content;
        this.type=type;
        this.image=image;
    }
    public Msg(String IsFileUploaded,
               Message message,
               String userName,
               String appKey,
               Bitmap icon,
               Bitmap image,
               String content,
               int type,
               int messageId,
               boolean dialogIsOpen){
        this.content=content;
        this.type=type;
        this.IsFileUploaded=IsFileUploaded;
        this.message=message;
        this.image=image;
        this.icon=icon;
        this.messageId=messageId;
        this.userName=userName;
        this.appKey=appKey;
        this.dialogIsOpen=dialogIsOpen;
    }
    public Msg(Bitmap icon,Bitmap image,String content,int type){
        this.content=content;
        this.type=type;
        this.image=image;
        this.icon=icon;
    }
    public Msg(Message message,int position,int type){
        Gson gson = new Gson();
        App app = gson.fromJson(message.getContent().toJson(), App.class);
        this.message=message;
        this.userName=personList.get(position).getUserName();
        this.appKey=message.getFromUser().getAppKey();
        this.icon=personList.get(position).getAvatar();
        this.messageId=message.getId();
        this.content=app.getText();
        this.type=type;
        this.image=BitmapFactory.decodeFile(app.getLocalThumbnailPath());
        this.IsFileUploaded=app.getIsFileUploaded();
        if (app.getLocalThumbnailPath() != null) {
            dialogIsOpen=true;
        }else dialogIsOpen=false;
    }
    public String getIsFileUploaded(){return IsFileUploaded;}
    public Message getMessage(){return message;}
    public boolean dialogIsOpen(){return dialogIsOpen;}
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
