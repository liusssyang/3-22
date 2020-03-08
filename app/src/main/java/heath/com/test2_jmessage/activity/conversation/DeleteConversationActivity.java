package heath.com.test2_jmessage.activity.conversation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import heath.com.test2_jmessage.R;
import heath.com.test2_jmessage.StatusBar.StatusBarUtil;
import heath.com.test2_jmessage.activity.friend.ManageFriendActivity;

import static heath.com.test2_jmessage.activity.TypeActivity.personList;

public class DeleteConversationActivity extends Activity implements View.OnClickListener {

    private EditText mEt_username;
    private TextView mTv_info;
    private EditText mEt_group_id;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position=getIntent().getIntExtra("position",-1);
        initView();
    }

    private void initView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_delete_conversation);
        zoomInViewSize(StatusBarUtil.getStatusBarHeight(this));
        Log.d("deskij", position+"");
        mEt_username = (EditText) findViewById(R.id.et_username);
        mTv_info = (TextView) findViewById(R.id.tv_info);
        mEt_group_id = (EditText) findViewById(R.id.et_group_id);
        Button bt_deleteMessage = (Button) findViewById(R.id.bt_delete_message);
        Button bt_singleDelete = (Button) findViewById(R.id.bt_single_delete);
        Button bt_groupDelete = (Button) findViewById(R.id.bt_group_delete);
        Button bt_getConversation = (Button) findViewById(R.id.bt_get_conversation);
        Button bt_getMessage = (Button) findViewById(R.id.bt_get_message);

        bt_deleteMessage.setOnClickListener(this);
        bt_singleDelete.setOnClickListener(this);
        bt_groupDelete.setOnClickListener(this);
        bt_getConversation.setOnClickListener(this);
        bt_getMessage.setOnClickListener(this);
        findViewById(R.id.bt_get_latest_message).setOnClickListener(this);
        TextView manage_back=findViewById(R.id.manage_back);
        manage_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), ManageFriendActivity.class);
                intent.putExtra("position",position);
                getApplicationContext().startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
            }
        });
    }

    @Override
    public void onClick(View v) {
        String targetName =personList.get(position).getName();

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
                getAllMessage(getConversation(targetName, targetGidString));
                List<Message> list=new ArrayList<>();
                list=getConversation(targetName, targetGidString).getAllMessage();
                for (int j=0;j<list.size();j++) {
                    Log.d("HISTORY_RECORD", list.get(j).getFromUser().getUserName()+"("+list.get(j).getFromName()+")");
                    Log.d("HISTORY_RECORD", list.get(j).getId()+"|"+list.get(j).getDirect());
                    Log.d("HISTORY_RECORD", list.get(j).getContent().toJson().intern());
                    Log.d("HISTORY_RECORD", list.get(j).getCreateTime()+"");
                    //Log.d("HISTORY_RECORD", list.get(j).toJson());
                    Log.d("HISTORY_RECORD", "_______________________________\n");
                    mTv_info.append(list.get(j).getFromUser().getUserName()+"("+list.get(j).getFromName()+")\n");
                    mTv_info.append(list.get(j).getId()+"|"+list.get(j).getDirect()+"\n");
                    mTv_info.append(list.get(j).getContent().toJson().intern()+"\n");
                    mTv_info.append(list.get(j).getCreateTime()+""+"\n");
                    mTv_info.append("_______________________________\n"+"\n");
                }

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

    private Conversation getConversation(String targetName, String targetGidString) {
        Conversation conversation = null;
        if (!TextUtils.isEmpty(targetName) && TextUtils.isEmpty(targetGidString)) {
            conversation = JMessageClient.getSingleConversation(targetName);
        } else if (TextUtils.isEmpty(targetName) && !TextUtils.isEmpty(targetGidString)) {
            long groupId = Long.parseLong(targetGidString);
            conversation = JMessageClient.getGroupConversation(groupId);
        } else {
            Toast.makeText(getApplicationContext(), "输入相关参数有误", Toast.LENGTH_SHORT).show();
        }
        return conversation;
    }

    private void getAllMessage(Conversation conversation) {
        if (conversation == null) {
            Toast.makeText(getApplicationContext(), "会话为空", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Message> allMessage = conversation.getAllMessage();
        if (allMessage != null) {
            mTv_info.setText("");
            StringBuilder sb = new StringBuilder();
            for (Message msg : allMessage) {
                /*sb.append("消息ID = " + msg.getId());
                sb.append(msg.getContent());
                sb.append("~~~消息类型 = " + msg.getContentType());
                sb.append("\n");*/
            }
            mTv_info.append("getAllMessage = " + "\n" + sb.toString());
        } else {
            Toast.makeText(DeleteConversationActivity.this, "未能获取到消息", Toast.LENGTH_SHORT).show();
        }
    }
    private void zoomInViewSize(int height) {
        View img1 = findViewById(R.id.statusbar);
        ViewGroup.LayoutParams  lp = img1.getLayoutParams();
        lp.height =height;
        img1.setLayoutParams(lp);
    }

}
