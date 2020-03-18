package heath.com.test2_jmessage.activity.conversation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import heath.com.test2_jmessage.MyDialog.ConversationDialog;
import heath.com.test2_jmessage.R;
import heath.com.test2_jmessage.StatusBar.StatusBarUtil;
import heath.com.test2_jmessage.StatusBar.StatusBarUtils;
import heath.com.test2_jmessage.adapter.MsgAdapter;
import heath.com.test2_jmessage.recycleView_item.Msg;
import heath.com.test2_jmessage.tools.tools;

import static heath.com.test2_jmessage.application.MyApplication.personList;
import static heath.com.test2_jmessage.tools.tools.getConversation;

public class DeleteConversationActivity extends Activity implements View.OnClickListener {

    private TextView mTv_info;
    private EditText mEt_group_id;
    private int position;
    private TextView none;
    private MsgAdapter adapter;
    private List<Msg> msgList = new ArrayList<>();
    private RecyclerView msgRecyclerView;
    private int X = 0, Y = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getIntent().getIntExtra("position", -1);
        initView();
        initData();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initData() {
       if (tools.shownHistoryFromCloud(personList.get(position).getUserName(), msgList, null)){
           none.setVisibility(View.GONE);
        }else none.setVisibility(View.VISIBLE);
    }

    private void initView() {
        setContentView(R.layout.activity_delete_conversation);
        zoomInViewSize(StatusBarUtil.getStatusBarHeight(this));
        StatusBarUtils.with(this).setDrawable(getResources().getDrawable(R.drawable.toolbar_ground)).init();
        none = findViewById(R.id.none);
        mTv_info = findViewById(R.id.tv_info);
        mEt_group_id = findViewById(R.id.et_group_id);
        msgRecyclerView = findViewById(R.id.msg_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager);
        adapter = new MsgAdapter(msgList);
        msgRecyclerView.setAdapter(adapter);
        msgList.clear();
        Button bt_deleteMessage = findViewById(R.id.bt_delete_message);
        Button bt_singleDelete = findViewById(R.id.bt_single_delete);
        Button bt_groupDelete = findViewById(R.id.bt_group_delete);
        Button bt_getConversation = findViewById(R.id.bt_get_conversation);
        Button bt_getMessage = findViewById(R.id.bt_get_message);
        TextView manage_sigText_toolbarName = findViewById(R.id.manage_sigText_toolbarName);
        manage_sigText_toolbarName.setText(personList.get(position).getName());
        bt_deleteMessage.setOnClickListener(this);
        bt_singleDelete.setOnClickListener(this);
        bt_groupDelete.setOnClickListener(this);
        bt_getConversation.setOnClickListener(this);
        bt_getMessage.setOnClickListener(this);
        findViewById(R.id.bt_get_latest_message).setOnClickListener(this);
        TextView manage_back = findViewById(R.id.manage_back);
        manage_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
            }
        });
        TextView manage_message = findViewById(R.id.manage_message);
        manage_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = personList.get(position).getUserName();
                ConversationDialog mydialog = new ConversationDialog(v.getContext(), userName, none, msgList, adapter);
                mydialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                Window dialogWindow = mydialog.getWindow();
                assert dialogWindow != null;
                dialogWindow.setGravity(Gravity.TOP | Gravity.END);
                dialogWindow.getDecorView().setPadding(0, 0, 0, 0);
                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                lp.y = tools.getActionBarSize();
                lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialogWindow.setAttributes(lp);
                dialogWindow.setWindowAnimations(R.style.dialogWindowAnim);
                mydialog.show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        String targetName = personList.get(position).getUserName();
        String targetGidString = mEt_group_id.getText().toString();
        Conversation conversation;
        switch (v.getId()) {
            case R.id.bt_delete_message:
                mTv_info.setText("");
                conversation = getConversation(targetName, targetGidString);
                if (conversation == null) {
                    Toast.makeText(getApplicationContext(), "会话为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(getApplicationContext(), "删除结果：" + conversation.deleteAllMessage(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.bt_single_delete:
                mTv_info.setText("");
                if (TextUtils.isEmpty(targetName)) {
                    Toast.makeText(getApplicationContext(), "请输入userName", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(getApplicationContext(), "删除单聊会话结果：" + JMessageClient.deleteSingleConversation(targetName), Toast.LENGTH_SHORT).show();
                break;
            case R.id.bt_group_delete:
                mTv_info.setText("");
                if (TextUtils.isEmpty(targetGidString)) {
                    Toast.makeText(getApplicationContext(), "请输入群组id", Toast.LENGTH_SHORT).show();
                    return;
                }
                long gid = Long.parseLong(targetGidString);
                Toast.makeText(getApplicationContext(), "删除群聊会话结果：" + JMessageClient.deleteGroupConversation(gid), Toast.LENGTH_SHORT).show();
                break;
            case R.id.bt_get_conversation:
                conversation = getConversation(targetName, targetGidString);
                if (conversation != null) {
                    mTv_info.setText("");
                    mTv_info.append("getType = " + conversation.getType() + "\ngetId = " + conversation.getTargetId());
                } else {
                    mTv_info.setText("");
                    mTv_info.append("会话为null");
                }
                break;
            case R.id.bt_get_message:

                break;
            case R.id.bt_get_latest_message:
                conversation = getConversation(targetName, targetGidString);
                if (null != conversation) {
                    Message latestMessage = conversation.getLatestMessage();
                    if (latestMessage != null) {
                        mTv_info.setText(latestMessage.toString());
                    } else {
                        mTv_info.setText("");
                        mTv_info.append("latestMessage为null");
                    }
                }
                break;
            default:
                break;
        }
    }

    private void zoomInViewSize(int height) {
        View img1 = findViewById(R.id.statusbar);
        ViewGroup.LayoutParams lp = img1.getLayoutParams();
        lp.height = height;
        img1.setLayoutParams(lp);
    }


}
