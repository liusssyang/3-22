package heath.com.test2_jmessage.activity.createmessage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.android.api.options.MessageSendingOptions;
import cn.jpush.im.api.BasicCallback;
import heath.com.test2_jmessage.R;
import heath.com.test2_jmessage.StatusBar.StatusBarUtils;
import heath.com.test2_jmessage.activity.groupinfo.GroupInfoActivity;
import heath.com.test2_jmessage.adapter.MsgAdapter;
import heath.com.test2_jmessage.recycleView_item.Msg;
import heath.com.test2_jmessage.tools.DataBean;
import heath.com.test2_jmessage.tools.PushToast;
import heath.com.test2_jmessage.tools.tools;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static heath.com.test2_jmessage.application.MyApplication.groupList;

/**
 * Created by ${chenyn} on 16/3/29.
 * <p>
 * 创建消息有两种方式：
 * <p>
 * ============
 * 其一：
 * 通过JMessageClient中提供的接口快捷的创建一条消息
 * <p>
 * Message msg = JMessageClient.createXXXMessage();
 * ============
 * 其二：
 * 通过创建message content -> 创建message.的标准方式创建一条消息
 * <p>
 * TextContent textContent = new TextContent("hello jmessage."); //这里以文字消息为例
 * Message msg = conversation.createSendMessage(textContent);
 * ============
 * <p>
 * 两种方式的区别在于：
 * 1.使用快捷方式上层可以无需关注会话实例，以及message content中各种字段，使用上比较便捷。
 * 2.标准方式在创建消息时可以方便指定message content中的各种字段，如extra、customFromName。适用于对消息的内容有
 * 较高的定制需求的开发者.
 * <p>
 * 这里创建消息步骤以及消息发送控制等参数的设置均可沿用到其他类型的消息发送中去，如图片、语音、文件等。这里仅仅是以发送文字消息为例做一个演示。
 */
public class CreateGroupTextMsgActivity extends Activity {
    private static final String TAG = "ly13174CGroup";

    private EditText mEt_text;
    private TextView mBt_send;
    private ProgressDialog mProgressDialog;
    private static int position=-1;
    private List<Msg> msgList = new ArrayList<>();
    private RecyclerView msgRecyclerView;
    private MsgAdapter adapter;
    public  static List<UserInfo> GroupMemberList =new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_create_group_text_message);
        position=getIntent().getIntExtra("position",position);
        PushToast.getInstance().init(this);
        StatusBarUtils.with(this).setDrawable(getResources().getDrawable(R.drawable.toolbar_ground)).init();
        tools.getGroupMembers(groupList.get(position).getGroupId());
        msgRecyclerView = findViewById(R.id.msg_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager);
        adapter = new MsgAdapter(msgList);
        msgRecyclerView.setAdapter(adapter);
        msgRecyclerView.scrollToPosition(msgList.size() - 1);

        mEt_text = (EditText) findViewById(R.id.et_text);
        mBt_send =  findViewById(R.id.bt_send);
        TextView back = findViewById(R.id.back);
        TextView toolbarName = findViewById(R.id.sigText_toolbarName);
        toolbarName.setText(groupList.get(position).getGroupName());
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        TextView sigText_toolbarMenu = findViewById(R.id.sigText_toolbarMenu);
        sigText_toolbarMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), GroupInfoActivity.class);
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("position", position);
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
            }
        });
        mBt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = mEt_text.getText().toString();
                if (-1 != position ) {
                    String customFromName = null;
                    String extraKey = null;
                    String extraValue = null;
                    String atUsername = null;
                    boolean retainOfflineMsg = true;
                    boolean showNotification = true;
                    boolean enableCustomNotify = true;
                    boolean needReadReceipt = true;

                    //通过groupid拿到会话对象
                    Conversation mConversation = JMessageClient.getGroupConversation(groupList.get(position).getGroupId());
                    if (mConversation == null) {
                        mConversation = Conversation.createGroupConversation(groupList.get(position).getGroupId());
                    }
                    //构造message content对象
                    TextContent textContent = new TextContent(text);
                    //设置自定义的extra参数
                    textContent.setStringExtra(extraKey, extraValue);


                    final Message message;
                    //创建message实体
                    if (!TextUtils.isEmpty(atUsername) && atUsername.equals("all")) {
                        //创建一条@全体群成员的消息
                        message = mConversation.createSendMessageAtAllMember(textContent, customFromName);
                    } else if (!TextUtils.isEmpty(atUsername) && !atUsername.equals("all")) {
                        //创建一条@某些群成员的消息
                        List<UserInfo> atList = new ArrayList<UserInfo>();//被@用户的用户信息list
                        GroupInfo groupInfo = (GroupInfo) mConversation.getTargetInfo();
                        //sdk支持同时@多个用户，demo为了简便，只实现@一个用户的逻辑，@多个用户同理。
                        UserInfo info = groupInfo.getGroupMemberInfo(atUsername, null);
                        if (null != info) {
                            atList.add(info);//默认at当前应用下的用户，如需跨应用，则填写对方应用的appkey.
                            message = mConversation.createSendMessage(textContent, atList, customFromName);
                        } else {
                            Toast.makeText(getApplicationContext(), "被@的用户不在群组中。", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else {
                        //创建一条没有@任何群成员的消息
                        message = mConversation.createSendMessage(textContent, customFromName);
                    }

                    //设置消息发送回调。
                    message.setOnSendCompleteCallback(new BasicCallback() {
                        @Override
                        public void gotResult(int i, String s) {
                            mProgressDialog.dismiss();
                            if (i == 0) {
                                final Msg msg = new Msg(message);
                                //tools.setLocalHistory(message, position);
                                msgList.add(msg);
                                adapter.notifyItemInserted(msgList.size() - 1);
                                msgRecyclerView.scrollToPosition(msgList.size() - 1);
                                Log.i(TAG, "JMessageClient.createSingleTextMessage" + ", responseCode = " + i + " ; LoginDesc = " + s);
                                Toast.makeText(getApplicationContext(), "发送成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.i(TAG, "JMessageClient.createSingleTextMessage" + ", responseCode = " + i + " ; LoginDesc = " + s);
                                Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    //设置消息发送时的一些控制参数
                    MessageSendingOptions options = new MessageSendingOptions();
                    options.setNeedReadReceipt(needReadReceipt);//是否需要对方用户发送消息已读回执
                    options.setRetainOffline(retainOfflineMsg);//是否当对方用户不在线时让后台服务区保存这条消息的离线消息
                    options.setShowNotification(showNotification);//是否让对方展示sdk默认的通知栏通知
                    options.setCustomNotificationEnabled(enableCustomNotify);//是否需要自定义对方收到这条消息时sdk默认展示的通知栏中的文字
                    if (enableCustomNotify) {
                        options.setNotificationTitle(null);//自定义对方收到消息时通知栏展示的title
                        options.setNotificationAtPrefix(null);//自定义对方收到消息时通知栏展示的@信息的前缀
                        options.setNotificationText(null);//自定义对方收到消息时通知栏展示的text
                    }

                    mProgressDialog = MsgProgressDialog.show(CreateGroupTextMsgActivity.this, message);

                    //发送消息
                    JMessageClient.sendMessage(message, options);
                } else {
                    Toast.makeText(getApplicationContext(), "必填字段不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("message");
        LocalRceiver localRceiver= new LocalRceiver(position, msgList, msgRecyclerView, adapter);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(localRceiver, intentFilter);
        final SwipeRefreshLayout swipeLayout = findViewById(R.id.swipeLayout);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (tools.shownHistoryFromCloud(null, msgList, groupList.get(position).getGroupId()+"")) {
                    PushToast.getInstance().createToast("提示", "刷新成功", null, true);
                    adapter.notifyDataSetChanged();
                    msgRecyclerView.scrollToPosition(msgList.size() - 1);
                    //tools.getLocalHistoryFromCloud(position,null);
                }
                swipeLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });

    }
    class LocalRceiver extends BroadcastReceiver {
        private List<Msg> msgList;
        private RecyclerView msgRecyclerView;
        private MsgAdapter adapter;
        private int position;

        public LocalRceiver(int position, List<Msg> msgList, RecyclerView msgRecyclerView, MsgAdapter adapter) {
            this.msgList = msgList;
            this.msgRecyclerView = msgRecyclerView;
            this.adapter = adapter;
            this.position = position;
        }

        public void onReceive(Context context, Intent intent) {
            Serializable se = intent.getSerializableExtra("BeanData");
            DataBean db = (DataBean) se;
            Message message = db.getMessage();
            if (message.getTargetType() == ConversationType.group){
                GroupInfo groupInfo = (GroupInfo) message.getTargetInfo();
                if(groupInfo.getGroupID()==groupList.get(position).getGroupId()){
                    msgList.add(new Msg(message));
                    adapter.notifyItemInserted(msgList.size() - 1);
                    msgRecyclerView.scrollToPosition(msgList.size() - 1);
                }
            }
        }}
}
