package heath.com.test2_jmessage.MyDialog;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;

import androidx.annotation.NonNull;
import cn.jpush.im.android.api.callback.DownloadCompletionCallback;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.model.Message;
import heath.com.test2_jmessage.R;

import static cn.jpush.im.android.api.jmrtc.JMRTCInternalUse.getApplicationContext;

public class Mydialog extends Dialog {
    private PhotoView photoView;
    public static Bitmap bitmap;
    private TextView close,download,tip;
    private Context context;
    public static ImageContent imageContent;
    public static Message message;
    SharedPreferences.Editor editor=getApplicationContext().getSharedPreferences("data",0).edit();
    public Mydialog(Context context){
        super(context);
    }
    public Mydialog(@NonNull Context context,Bitmap bitmap) {
        super(context);
        this.bitmap=bitmap;
        this.context=context;
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.photoview);
        SharedPreferences pref=getApplicationContext().getSharedPreferences("data",0);
        close=findViewById(R.id.dialogclose);
        download=findViewById(R.id.download);
        tip=findViewById(R.id.tip);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageContent.downloadOriginImage(message, new DownloadCompletionCallback() {
                    @Override
                    public void onComplete(int responseCode, String responseMessage, File file) {

                        if (responseCode == 0) {
                            editor.putString("filepath",file.getPath());
                            editor.apply();
                            //mTv_showText.append("原图文件下载成功，路径:" + file.getPath() + "\n");
                            Toast.makeText(getApplicationContext(), "原图下载成功", Toast.LENGTH_SHORT).show();
                            Bitmap bitmap= BitmapFactory.decodeFile(file.getPath());
                            photoView.setImageBitmap(bitmap);
                            download.setVisibility(View.GONE);

                        } else {
                            Toast.makeText(getApplicationContext(), "原图下载失败", Toast.LENGTH_SHORT).show();
                            Log.i("ShowMessageActivity", "downloadFile" + ", responseCode = " + responseCode + " ; Desc = " + responseMessage);
                        }
                    }
                });
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        photoView=findViewById(R.id.photo);
        if (pref.getString("filepath",null)==null)
            photoView.setImageBitmap(bitmap);
        else{
            photoView.setImageBitmap(BitmapFactory.decodeFile(pref.getString("filepath",null)));
            download.setVisibility(View.GONE);

        }
    }
    protected void onStop(){
        super.onStop();
    }

}
