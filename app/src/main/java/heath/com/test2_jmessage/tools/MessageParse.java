package heath.com.test2_jmessage.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.gson.Gson;

import cn.jpush.im.android.api.model.Message;
import heath.com.test2_jmessage.recycleView_item.Msg;

import static heath.com.test2_jmessage.application.MyApplication.personList;

public class MessageParse {
    private Message message;
    private String userNme;
    private String appKey;
    private String IsFileUploaded;
    private Bitmap icon;
    private Bitmap image;
    private String content;
    int type;
    int messageId;
    boolean dialogIsOpen;
    public MessageParse(Message message,int position){
        Gson gson = new Gson();
        App app = gson.fromJson(message.getContent().toJson(), App.class);
        this.message=message;
        this.userNme=message.getFromUser().getUserName();
        this.appKey=message.getFromUser().getAppKey();
        this.icon=personList.get(position).getAvatar();
        this.messageId=message.getId();
        this.content=app.getText();
        this.type=Msg.TYPE_RECEIVED;
        this.image=BitmapFactory.decodeFile(app.getLocalThumbnailPath());
        this.IsFileUploaded=app.getIsFileUploaded();
        if (app.getLocalThumbnailPath() != null) {
            dialogIsOpen=true;
        }else dialogIsOpen=false;

    }
}
