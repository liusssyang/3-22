package heath.com.test2_jmessage.recycleView_item;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import heath.com.test2_jmessage.R;
import heath.com.test2_jmessage.application.IMDebugApplication;


public class Msg {
    public static final int TYPE_RECEIVED=0;
    public static final int TYPE_SENT=1;
    private String content;
    private Bitmap image;
    private Bitmap icon;
    private int type;
    public Msg(Bitmap image,String content,int type){
        this.content=content;
        this.type=type;
        this.image=image;
    }
    public Msg(Bitmap icon,Bitmap image,String content,int type){
        this.content=content;
        this.type=type;
        this.image=image;
        this.icon=icon;
    }
    public String getContent(){
        return content;
    }
    public Bitmap getImageContent(){
        return image;
    }
    public Bitmap getIconContent(){
        if (this.icon==null)
            return BitmapFactory.decodeResource(IMDebugApplication.getContext().getResources(), R.drawable.icon_right_default);
        else
            return this.icon;
    }
    public int getType(){
        return type;
    }
    public void setIcon(Bitmap icon){
        if (icon==null)
            this.icon= BitmapFactory.decodeResource(IMDebugApplication.getContext().getResources(), R.drawable.icon_right_default);
        else
            this.icon=icon;
    }
}
