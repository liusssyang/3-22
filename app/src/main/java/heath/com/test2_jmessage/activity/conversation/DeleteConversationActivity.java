package heath.com.test2_jmessage.activity.conversation;

import android.app.Activity;
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

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import heath.com.test2_jmessage.R;
import heath.com.test2_jmessage.StatusBar.StatusBarUtil;
import heath.com.test2_jmessage.adapter.MsgAdapter;
import heath.com.test2_jmessage.recycleView_item.Msg;
import heath.com.test2_jmessage.tools.App;
import heath.com.test2_jmessage.tools.tools;
import static heath.com.test2_jmessage.activity.TypeActivity.personList;

public class DeleteConversationActivity extends Activity implements View.OnClickListener {

    private TextView mTv_info;
    private EditText mEt_group_id;
    private int position;
    public MsgAdapter adapter;
    private List<Msg> msgList=new ArrayList<>();
    private RecyclerView msgRecyclerView;

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
        mTv_info = (TextView) findViewById(R.id.tv_info);
        mEt_group_id = (EditText) findViewById(R.id.et_group_id);
        msgRecyclerView=findViewById(R.id.msg_recycler_view);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager);
        adapter=new MsgAdapter(msgList);
        msgRecyclerView.setAdapter(adapter);
        msgList.clear();
        Button bt_deleteMessage = (Button) findViewById(R.id.bt_delete_message);
        Button bt_singleDelete = (Button) findViewById(R.id.bt_single_delete);
        Button bt_groupDelete = (Button) findViewById(R.id.bt_group_delete);
        Button bt_getConversation = (Button) findViewById(R.id.bt_get_conversation);
        Button bt_getMessage = (Button) findViewById(R.id.bt_get_message);
        TextView manage_sigText_toolbarName=findViewById(R.id.manage_sigText_toolbarName);
        manage_sigText_toolbarName.setText("与"+personList.get(position).getName()+"的聊天记录");
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
                finish();
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
            }
        });
    }

    @Override
    public void onClick(View v) {
        Gson gson=new Gson();
        String targetName =personList.get(position).getUserName();
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
                List<Message> list=new ArrayList<>();
                Log.d("NaMe", "onClick: "+targetName);
                list=getConversation(targetName, targetGidString).getAllMessage();
                if (list!=null)
                    for (int j=0;j<list.size();j++) {
                        Log.d("HISTORY_RECORD", list.get(j).getFromUser().getUserName()+"("+list.get(j).getFromName()+")");
                        Log.d("HISTORY_RECORD", list.get(j).getId()+"|"+list.get(j).getDirect());
                        Log.d("HISTORY_RECORD", list.get(j).getCreateTime()+"");
                        App app=gson.fromJson(list.get(j).getContent().toJson(), App.class);
                        Log.d("HISTORY_RECORD", "解析"+app.getText());
                        Log.d("HISTORY_RECORD", "解析"+app.getIsFileUploaded());
                        Log.d("HISTORY_RECORD", "解析"+app.getExtras());
                        Log.d("HISTORY_RECORD", "解析"+app.getHeight());
                        Log.d("HISTORY_RECORD", "解析"+app.getWidth());
                        Log.d("HISTORY_RECORD", "解析"+app.getLocalThumbnailPath());
                        Log.d("HISTORY_RECORD", "_______________________________\n");
                        mTv_info.append(list.get(j).getFromUser().getUserName()+"("+list.get(j).getFromName()+")\n");
                    mTv_info.append(list.get(j).getId()+"|"+list.get(j).getDirect()+"\n");
                    if (app.getText()!=null){
                        if (list.get(j).getDirect().toString().equals("receive"))
                            msgList.add(new Msg(null,null,app.getText(),Msg.TYPE_RECEIVED));
                        else
                            msgList.add(new Msg(null,null,app.getText(),Msg.TYPE_SENT));
                        adapter.notifyDataSetChanged();
                        mTv_info.append(app.getText()+"\n");
                    }
                    if (app.getLocalThumbnailPath()!=null){
                        mTv_info.append(app.getIsFileUploaded()+"\n");
                        mTv_info.append(app.getLocalThumbnailPath()+"\n");
                    }
                    mTv_info.append(tools.secondToDate(list.get(j).getCreateTime(),"hh:mm:ss")+""+"\n");
                    mTv_info.append("_______________________________\n"+"\n");
                }
                else
                    mTv_info.append("empty");
                msgRecyclerView.scrollToPosition(msgList.size()-1);
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
