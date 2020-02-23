package heath.com.test2_jmessage;

import android.graphics.Bitmap;


public class Msg {
    public static final int TYPE_RECEIVED=0;
    public static final int TYPE_SENT=1;
    private String content;
    private Bitmap image;
    private int type;
    public Msg(Bitmap image,String content,int type){
        this.content=content;
        this.type=type;
        this.image=image;
    }
    public String getContent(){
        return content;
    }
    public Bitmap getImageContent(){
        return image;
    }
    public int getType(){
        return type;
    }
}
