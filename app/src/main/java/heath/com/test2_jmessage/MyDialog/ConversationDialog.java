package heath.com.test2_jmessage.MyDialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import cn.jpush.im.android.api.JMessageClient;
import heath.com.test2_jmessage.R;
import heath.com.test2_jmessage.activity.friend.AddFriendActivity;
import heath.com.test2_jmessage.activity.groupinfo.ApplyJoinGroupActivity;
import heath.com.test2_jmessage.adapter.MsgAdapter;
import heath.com.test2_jmessage.recycleView_item.Msg;
import heath.com.test2_jmessage.tools.PushToast;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static cn.jpush.im.android.api.jmrtc.JMRTCInternalUse.getApplicationContext;

public class ConversationDialog extends Dialog {

    //public static Message message;
    private TextView none;
    private MsgAdapter adapter;
    private List<Msg> msgList;
    private RecyclerView msgRecyclerView;
    private String userName;
    SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("data", 0).edit();

    public ConversationDialog(Context context, String userName, TextView none, List<Msg> msgList, MsgAdapter adapter) {
        super(context);
        this.userName = userName;
        this.none = none;
        this.msgRecyclerView = msgRecyclerView;
        this.msgList = msgList;
        this.adapter = adapter;
    }
    public ConversationDialog(Context context) {
        super(context);
    }
    public ConversationDialog(Context context, int id) {
        super(context, id);
    }

    @SuppressLint("ClickableViewAccessibility")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.conversation_dialog);
        LinearLayout build_group = findViewById(R.id.build_group);
        build_group.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        Log.d("ly13172", "onTouch: up");
                        // dismiss();
                        break;
                    case MotionEvent.ACTION_DOWN:
                        if (JMessageClient.deleteSingleConversation(userName)) {
                            dismiss();
                            none.setVisibility(View.VISIBLE);
                            msgList.clear();
                            adapter.notifyDataSetChanged();
                            PushToast.getInstance().createToast("提示", "删除成功", null, true);
                        } else PushToast.getInstance().createToast("提示", "操作失败", null, false);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + event.getAction());
                }
                return false;
            }
        });
        LinearLayout find_single_line = findViewById(R.id.find_single_line);
        find_single_line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddFriendActivity.class);
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }
        });
        LinearLayout find_group_line = findViewById(R.id.find_group_line);
        find_group_line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ApplyJoinGroupActivity.class);
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }
        });

    }

    protected void onStop() {
        super.onStop();
    }
}
