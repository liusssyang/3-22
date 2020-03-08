package heath.com.test2_jmessage.MyDialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;

import androidx.annotation.NonNull;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.model.Message;
import heath.com.test2_jmessage.R;
import heath.com.test2_jmessage.activity.friend.AddFriendActivity;
import heath.com.test2_jmessage.activity.groupinfo.ApplyJoinGroupActivity;
import heath.com.test2_jmessage.activity.groupinfo.CreateGroupActivity;

import static cn.jpush.im.android.api.jmrtc.JMRTCInternalUse.getApplicationContext;

public class TypeDialog extends Dialog {
    private PhotoView photoView;
    public static Bitmap bitmap;
    private TextView download,tip;
    private Context context;
    public static ImageContent imageContent;
    public static Message message;
    SharedPreferences.Editor editor=getApplicationContext().getSharedPreferences("data",0).edit();
    public TypeDialog(Context context){
        super(context);
    }
    public TypeDialog(Context context,int id){
        super(context,id);
    }
    public TypeDialog(@NonNull Context context, Bitmap bitmap) {
        super(context);
        this.bitmap=bitmap;
        this.context=context;
    }
    @SuppressLint("ClickableViewAccessibility")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.alertdialog_popwindow);
        LinearLayout build_group=findViewById(R.id.build_group);
        build_group.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        Log.d("ly13172", "onTouch: up");
                       // dismiss();
                        break;
                    case MotionEvent.ACTION_DOWN:
                        Intent intent=new Intent(getApplicationContext(), CreateGroupActivity.class);
                        getApplicationContext().startActivity(intent);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //Log.d("ly13172", "onTouch: move");
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + event.getAction());
                }
                return false;
            }
        });
        LinearLayout find_single_line=findViewById(R.id.find_single_line);
        find_single_line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), AddFriendActivity.class);
                getApplicationContext().startActivity(intent);
            }
        });
        LinearLayout find_group_line=findViewById(R.id.find_group_line);
        find_group_line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), ApplyJoinGroupActivity.class);
                getApplicationContext().startActivity(intent);
            }
        });

    }
    protected void onStop(){
        super.onStop();
    }

}
