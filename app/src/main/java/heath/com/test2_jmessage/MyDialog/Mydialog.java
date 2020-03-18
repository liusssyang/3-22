package heath.com.test2_jmessage.MyDialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;

import org.litepal.crud.DataSupport;

import java.util.List;

import androidx.annotation.NonNull;
import cn.jpush.im.android.api.model.Message;
import heath.com.test2_jmessage.R;
import heath.com.test2_jmessage.recycleView_item.Msg;
import heath.com.test2_jmessage.tools.LocalHistory;
import heath.com.test2_jmessage.tools.tools;

import static cn.jpush.im.android.api.jmrtc.JMRTCInternalUse.getApplicationContext;

public class Mydialog extends Dialog {
    private PhotoView photoView;
    public static Bitmap bitmap;
    private TextView download, tip;
    private Context context;
    private Msg msg = null;
    private String IsFileUploaded;

    private Message message;

    public Mydialog(Context context, int id, Msg msg) {
        super(context, id);
        this.msg = msg;
        this.IsFileUploaded = msg.getIsFileUploaded();
        this.message=msg.getMessage();
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

                if (TextUtils.isEmpty(getPicPath(msg))) {
                    tools.getImageContent(download,msg.getMessage(), photoView, msg);
                }
                if (msg.getMessage()==null){
                    Log.d("ly13171", "message is null");
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
        if (!TextUtils.isEmpty(getPicPath(msg))) {
            photoView.setImageBitmap(BitmapFactory.decodeFile(getPicPath(msg)));
            download.setVisibility(View.GONE);
        } else {
            photoView.setImageBitmap(BitmapFactory.decodeFile(msg.getLocalThumbnailPath()));
            download.setVisibility(View.VISIBLE);
        }
        if (msg.getMessage()==null){
            download.setVisibility(View.GONE);
        }
    }

    protected void onStop() {
        super.onStop();
    }

    private String getPicPath(Msg msg) {
        String s = null;
        List<LocalHistory> localHistories = DataSupport.
                where("messageid=?", msg.getId() + "").find(LocalHistory.class);
        for (LocalHistory localHistory : localHistories) {
            s = localHistory.getLocalDownload();
            Log.d("13171", localHistory.getId()+"{}"+msg.getId()+localHistory.getLocalDownload());
        }

        return s;
    }

}
