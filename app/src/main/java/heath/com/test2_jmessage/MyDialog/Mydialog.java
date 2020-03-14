package heath.com.test2_jmessage.MyDialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
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
import heath.com.test2_jmessage.recycleView_item.Msg;
import heath.com.test2_jmessage.tools.tools;

import static cn.jpush.im.android.api.jmrtc.JMRTCInternalUse.getApplicationContext;

public class Mydialog extends Dialog {
    private PhotoView photoView;
    public static Bitmap bitmap;
    private TextView download, tip;
    private Context context;
    private Msg msg = null;
    private String IsFileUploaded;
    public static ImageContent imageContent;
    public static Message message;
    private SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("data", 0).edit();
    public Mydialog( Context context, int id, Msg msg) {
        super(context, id);
        this.msg = msg;
        this.IsFileUploaded = msg.getIsFileUploaded();
    }

    public Mydialog(Context context, int id) {
        super(context, id);
    }

    public Mydialog(@NonNull Context context, Bitmap bitmap) {
        super(context);
        this.bitmap = bitmap;
        this.context = context;
    }

    @SuppressLint("ClickableViewAccessibility")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.photoview);
        final SharedPreferences pref = getApplicationContext().getSharedPreferences("data", 0);
        TextView close = findViewById(R.id.dialogclose);
        download = findViewById(R.id.download);
        tip = findViewById(R.id.tip);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (msg.getMessage() != null) {
                        tools.getImageContent(msg.getMessage(),photoView,msg);
                    } else {
                        imageContent.downloadOriginImage(message, new DownloadCompletionCallback() {
                            @Override
                            public void onComplete(int responseCode, String responseMessage, File file) {
                                if (responseCode == 0) {

                                    Toast.makeText(getApplicationContext(), "原图下载成功", Toast.LENGTH_SHORT).show();
                                    Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
                                    photoView.setImageBitmap(bitmap);
                                    download.setVisibility(View.GONE);
                                } else {
                                    Toast.makeText(getApplicationContext(), "原图下载失败", Toast.LENGTH_SHORT).show();
                                    Log.i("ShowMessageActivity", "downloadFile" + ", responseCode = " + responseCode + " ; Desc = " + responseMessage);
                                }
                            }
                        });
                }
            }
        });
        close.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        Log.d("ly13172", "onTouch: up");
                        dismiss();
                        break;
                    case MotionEvent.ACTION_DOWN:
                        Log.d("ly13172", "onTouch: down");
                        dismiss();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.d("ly13172", "onTouch: move");
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + event.getAction());
                }
                return true;
            }
        });

        photoView = findViewById(R.id.photo);
        if (msg != null) {
            photoView.setImageBitmap(BitmapFactory.decodeFile(msg.getLocalThumbnailPath()));
        } else {
            if (pref.getString("filepath", null) == null)
                photoView.setImageBitmap(bitmap);
            else {
                photoView.setImageBitmap(BitmapFactory.decodeFile(pref.getString("filepath", null)));
                download.setVisibility(View.GONE);
            }
        }
    }

    protected void onStop() {
        super.onStop();
    }

}
