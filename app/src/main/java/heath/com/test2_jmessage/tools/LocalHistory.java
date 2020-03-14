package heath.com.test2_jmessage.tools;

import com.google.gson.Gson;

import org.litepal.crud.DataSupport;

import cn.jpush.im.android.api.model.Message;

public class LocalHistory extends DataSupport {
    private int id;

    private String LocalDownload;
    private String content;
    private String LocalThumbnailPath;
    private int type;
    private int messageId;
    private String userName;
    private String appKey;
    private String IsFileUploaded;
    private int dialogIsOpen=0;
    private long userId;
    private long myUserId;
    public  int getMessageId(){return messageId;}
    public int getDialogIsOpen(){return this.dialogIsOpen;}
    public void setDialogIsOpen(int dialogIsOpen){this.dialogIsOpen=dialogIsOpen;}
    public void setLocalDownload(String localDownload){this.LocalDownload=localDownload;}
    public String getLocalDownload(){return this.LocalDownload;}
    public LocalHistory(){}
    public LocalHistory(int type,
                     String userName,
                     String appKey,
                     int messageId,
                     String content,
                     long userId,
                     String localThumbnailPath,
                     String IsFileUploaded,
                     int dialogIsOpen){
        this.type=type;this.content=content;this.userName=userName;this.appKey=appKey;
        this.messageId=messageId;this.userId=userId;this.LocalThumbnailPath=localThumbnailPath;
        this.IsFileUploaded=IsFileUploaded;;this.dialogIsOpen=dialogIsOpen;
    }
    public LocalHistory (Message message){
        Gson gson = new Gson();
        App app = gson.fromJson(message.getContent().toJson(), App.class);
        if (message.getDirect().toString().equals("receive"))
            type= 0;
        else type=1;
        this.userName=message.getFromUser().getUserName();
        this.appKey=message.getFromUser().getAppKey();
        this.messageId=message.getId();
        this.content=app.getText();
        this.userId=message.getFromUser().getUserID();
        this.LocalThumbnailPath=app.getLocalThumbnailPath();
        this.IsFileUploaded= app.getIsFileUploaded();
        if (app.getLocalThumbnailPath() != null) {
            dialogIsOpen=1;
        }else dialogIsOpen=0;
    }
    public String getLocalThumbnailPath(){return LocalThumbnailPath;}
    public long getMyUserId(){return myUserId;}
    public void setMyUserId(long myUserId){this.myUserId=myUserId;}
    public long getUserId(){return userId;}
    public String getIsFileUploaded(){return IsFileUploaded;}
    public int dialogIsOpen(){return dialogIsOpen;}
    public String getUserName(){return userName;}
    public String getAppKey(){return appKey; }
    public int getId(){return messageId;}
    public String getContent(){
        return content;
    }
    public int getType(){
        return type;
    }
    public void setId(int id){this.id=id;}

}
