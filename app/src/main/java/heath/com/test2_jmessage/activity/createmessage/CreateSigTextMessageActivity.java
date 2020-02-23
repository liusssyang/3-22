package heath.com.test2_jmessage.activity.createmessage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.List;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.options.MessageSendingOptions;
import cn.jpush.im.api.BasicCallback;
import heath.com.test2_jmessage.Msg;
import heath.com.test2_jmessage.R;
import heath.com.test2_jmessage.adapter.MsgAdapter;
import heath.com.test2_jmessage.application.IMDebugApplication;

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
public class CreateSigTextMessageActivity extends Activity {

    private String getTextMessage = "\n";
    private EditText mEt_name;
    public static EditText mEt_text;
    private TextView mBt_send;
    public static final String TEXT_MESSAGE = "text_message";
    private EditText mEt_appkey;
    private EditText mEt_customName;
    private EditText mEt_extraKey;
    private EditText mEt_extraValue;
    private EditText mEt_customNotifyTitle;
    private EditText mEt_customNotifyText;
    private EditText mEt_customNotifyAtPrefix;
    private EditText mEt_customMsgCount;
    private CheckBox mCb_showNotification;
    private CheckBox mCb_retainOfflineMsg;
    private CheckBox mCb_enableCustomNotify;
    private CheckBox mCb_enableReadReceipt;
    private ProgressDialog mProgressDialog;
    private IntentFilter intentFilter;
    private Localreceiver localRceiver;
    private LocalBroadcastManager localBroadcastManager;
    private List<Msg> msgList=new ArrayList<>();
    private RecyclerView msgRecyclerView;
    private MsgAdapter adapter;
    private TextView menu;
    private BottomSheetBehavior behavior;
    //private Button button,button2;
    private RelativeLayout re;
    private CoordinatorLayout coordinatorLayout;
    SharedPreferences.Editor editor2;
    SharedPreferences pref;
    String history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_single_text_message);
        editor2=getApplicationContext().getSharedPreferences("history",0).edit();
        pref=getApplicationContext().getSharedPreferences("history",0);
        history=pref.getString("historyRecord","");
        String []his=history.split("%%");
        mEt_name = (EditText) findViewById(R.id.et_name);
        mEt_text = (EditText) findViewById(R.id.et_text);
        mBt_send = (TextView) findViewById(R.id.bt_send);
        mEt_appkey = (EditText) findViewById(R.id.et_appkey);
        mEt_customName = (EditText) findViewById(R.id.et_custom_name);
        mEt_extraKey = (EditText) findViewById(R.id.et_extra_key);
        mEt_extraValue = (EditText) findViewById(R.id.et_extra_value);
        mEt_customNotifyTitle = (EditText) findViewById(R.id.et_custom_notifyTitle);
        mEt_customNotifyText = (EditText) findViewById(R.id.et_custom_notifyText);
        mEt_customNotifyAtPrefix = (EditText) findViewById(R.id.et_custom_notifyAtPrefix);
        mEt_customMsgCount = (EditText) findViewById(R.id.et_custom_msg_count);
        mCb_showNotification = (CheckBox) findViewById(R.id.cb_showNotification);
        mCb_retainOfflineMsg = (CheckBox) findViewById(R.id.cb_retainOffline);
        mCb_enableCustomNotify = (CheckBox) findViewById(R.id.cb_enableCustomNotify);
        mCb_enableReadReceipt = (CheckBox) findViewById(R.id.cb_needReadReceipt);
        menu=(TextView) findViewById(R.id.menu);
        //findViewById(R.id.et_custom_notifyAtPrefix).setVisibility(View.GONE);
        msgRecyclerView=findViewById(R.id.msg_recycler_view);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager);
        adapter=new MsgAdapter(msgList);
        msgRecyclerView.setAdapter(adapter);
        for (int i=0;i<his.length;i++){
            getTextMessage+=i+"\n"+his[i]+"\n\n";
        }
        Log.d("kkk",getTextMessage);
        for (int i=his.length-1;i>=0;i--){
            if (his[i].contains("text_left")&&!his[i].equals("text_left")){
                msgList.add(0,new Msg(null,his[i].replace("text_left",""),Msg.TYPE_RECEIVED));
            }
            if (his[i].contains("image_left")&&!his[i].equals("image_left")){
                Bitmap bitmap= BitmapFactory.decodeFile(his[i].replace("image_left",""));
                msgList.add(0,new Msg(bitmap,null,Msg.TYPE_RECEIVED));
            }
            if (his[i].contains("text_right")&&!his[i].equals("text_right")){
                msgList.add(0,new Msg(null,his[i].replace("text_right",""),Msg.TYPE_SENT));
            }
        }
        msgRecyclerView.scrollToPosition(msgList.size()-1);
        RelativeLayout linearLayout=findViewById(R.id.ll_content_bottom_sheet);
        behavior = BottomSheetBehavior.from(linearLayout);
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        coordinatorLayout=findViewById(R.id.co);
        re=findViewById(R.id.l1);

// 得到参数
        final CoordinatorLayout.LayoutParams layoutParams = new CoordinatorLayout.LayoutParams(re.getLayoutParams());
        mEt_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.setBackgroundResource(R.drawable.menu);
                menu.setText(" ");
                coordinatorLayout.scrollTo(0,0);
            }
        });
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideInput();
                if (menu.getText().toString().equals(" ")){
                    menu.setBackgroundResource(R.drawable.close);
                    menu.setText("  ");
                    coordinatorLayout.scrollTo(0,500);
                    //behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                else {
                    menu.setBackgroundResource(R.drawable.menu);
                    menu.setText(" ");
                    coordinatorLayout.scrollTo(0,0);
                    //behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
            }
        });
        /*behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState)
            {
                //这里是bottomSheet状态的改变
                if (newState==BottomSheetBehavior.STATE_DRAGGING)
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset)
            {
                //这里是拖拽中的回调，根据slideOffset可以做一些动画
                Log.d("ssss", "onSlide: "+slideOffset);
            }
        });*/
        mCb_enableCustomNotify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mEt_customNotifyTitle.setEnabled(true);
                    mEt_customNotifyAtPrefix.setEnabled(true);
                    mEt_customNotifyText.setEnabled(true);
                } else {
                    mEt_customNotifyTitle.setEnabled(false);
                    mEt_customNotifyAtPrefix.setEnabled(false);
                    mEt_customNotifyText.setEnabled(false);
                }
            }
        });

        mBt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mEt_name.getText().toString();
                String text = mEt_text.getText().toString();
                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(text)) {
                    String appkey = mEt_appkey.getText().toString();
                    String customFromName = mEt_customName.getText().toString();
                    String extraKey = mEt_extraKey.getText().toString();
                    String extraValue = mEt_extraValue.getText().toString();
                    final Msg msg=new Msg(null,text,Msg.TYPE_SENT);
                    boolean retainOfflineMsg = mCb_retainOfflineMsg.isChecked();
                    boolean showNotification = mCb_showNotification.isChecked();
                    boolean enableCustomNotify = mCb_enableCustomNotify.isChecked();
                    boolean needReadReceipt = mCb_enableReadReceipt.isChecked();

                    //通过username和appkey拿到会话对象，通过指定appkey可以创建一个和跨应用用户的会话对象，从而实现跨应用的消息发送
                    Conversation mConversation = JMessageClient.getSingleConversation(name, appkey);
                    if (mConversation == null) {
                        mConversation = Conversation.createSingleConversation(name, appkey);
                    }

                    //构造message content对象
                    TextContent textContent = new TextContent(text);
                    //设置自定义的extra参数
                    textContent.setStringExtra(extraKey, extraValue);

                    //创建message实体，设置消息发送回调。
                    final Message message = mConversation.createSendMessage(textContent, customFromName);
                    message.setOnSendCompleteCallback(new BasicCallback() {
                        @Override
                        public void gotResult(int i, String s) {
                            mProgressDialog.dismiss();
                            if (i == 0) {
                                //Log.i(TAG, "JMessageClient.createSingleTextMessage" + ", responseCode = " + i + " ; LoginDesc = " + s);
                                msgList.add(msg);
                                adapter.notifyItemInserted(msgList.size()-1);
                                msgRecyclerView.scrollToPosition(msgList.size()-1);
                                history=history+mEt_text.getText().toString()+"text_right%%";
                                editor2.putString("historyRecord",history);
                                editor2.apply();
                                mEt_text.setText("");
                                Toast.makeText(getApplicationContext(), "发送成功", Toast.LENGTH_SHORT).show();
                            } else {
                                //Log.i(TAG, "JMessageClient.createSingleTextMessage" + ", responseCode = " + i + " ; LoginDesc = " + s);
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
                        options.setNotificationTitle(mEt_customNotifyTitle.getText().toString());//自定义对方收到消息时通知栏展示的title
                        options.setNotificationAtPrefix(mEt_customNotifyAtPrefix.getText().toString());//自定义对方收到消息时通知栏展示的@信息的前缀
                        options.setNotificationText(mEt_customNotifyText.getText().toString());//自定义对方收到消息时通知栏展示的text
                    }
                    if (!TextUtils.isEmpty(mEt_customMsgCount.getText())) {
                        try {
                            options.setMsgCount(Integer.valueOf(mEt_customMsgCount.getText().toString()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    mProgressDialog = MsgProgressDialog.show(CreateSigTextMessageActivity.this, message);

                    //发送消息
                    JMessageClient.sendMessage(message, options);
                } else {
                    Toast.makeText(getApplicationContext(), "必填字段不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });
        intentFilter=new IntentFilter();
        intentFilter.addAction("message");
        localRceiver =new Localreceiver(msgList,msgRecyclerView,adapter);
        localBroadcastManager=LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(localRceiver,intentFilter);
    }
    protected void onStop() {
        super.onStop();

    }

    public void sendMessage(String name, String text, String appkey, String customFromName,
                            String extraKey, String extraValue, String NotificationTitle,
                            String NotificationAtPrefix, String NotificationText, String MsgCount){
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(text)) {
            //通过username和appkey拿到会话对象，通过指定appkey可以创建一个和跨应用用户的会话对象，从而实现跨应用的消息发送
            Conversation mConversation = JMessageClient.getSingleConversation(name, appkey);
            if (mConversation == null) {
                mConversation = Conversation.createSingleConversation(name, appkey);
            }

            //构造message content对象
            TextContent textContent = new TextContent(text);
            //设置自定义的extra参数
            textContent.setStringExtra(extraKey, extraValue);
            //创建message实体，设置消息发送回调。
            final Message message = mConversation.createSendMessage(textContent, customFromName);
            message.setOnSendCompleteCallback(new BasicCallback() {
                @Override
                public void gotResult(int i, String s) {
                    if (i == 0) {
                        Toast.makeText(IMDebugApplication.getContext(), "发送成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(IMDebugApplication.getContext(), "发送失败", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            //设置消息发送时的一些控制参数
            MessageSendingOptions options = new MessageSendingOptions();
            options.setNeedReadReceipt(true);//是否需要对方用户发送消息已读回执
            options.setRetainOffline(true);//是否当对方用户不在线时让后台服务区保存这条消息的离线消息
            options.setShowNotification(true);//是否让对方展示sdk默认的通知栏通知
            options.setCustomNotificationEnabled(true);//是否需要自定义对方收到这条消息时sdk默认展示的通知栏中的文字
            if (true) {
                options.setNotificationTitle(NotificationTitle);//自定义对方收到消息时通知栏展示的title
                options.setNotificationAtPrefix(NotificationAtPrefix);//自定义对方收到消息时通知栏展示的@信息的前缀
                options.setNotificationText(NotificationText);//自定义对方收到消息时通知栏展示的text
            }
            if (MsgCount!=null) {
                try {
                    options.setMsgCount(Integer.valueOf(MsgCount));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            //发送消息
            JMessageClient.sendMessage(message, options);
        }
        else {
            Toast.makeText(IMDebugApplication.getContext(), "必填字段不能为空", Toast.LENGTH_SHORT).show();
        }
    }
    public void onDestroy(){
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(localRceiver);
    }
    class  Localreceiver extends BroadcastReceiver{
        private List<Msg> msgList;
        private RecyclerView msgRecyclerView;
        private MsgAdapter adapter;

        public Localreceiver(List<Msg> msgList,RecyclerView msgRecyclerView,MsgAdapter adapter){
            this.msgList=msgList;
            this.msgRecyclerView=msgRecyclerView;
            this.adapter=adapter;
        }
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra("key")!=null) {
                Msg msg = new Msg(null, intent.getStringExtra("key"), Msg.TYPE_RECEIVED);
                msgList.add(msg);
                adapter.notifyItemInserted(msgList.size() - 1);
                msgRecyclerView.scrollToPosition(msgList.size() - 1);
            }
            if (intent.getStringExtra("SysMessage")!=null){
                Msg msg2=new Msg(null,intent.getStringExtra("SysMessage"),Msg.TYPE_SENT);
                msgList.add(msg2);
                adapter.notifyItemInserted(msgList.size()-1);
                msgRecyclerView.scrollToPosition(msgList.size()-1);
                sendMessage(mEt_name.getText().toString(),intent.getStringExtra("SysMessage"),null,null,null,null,null,null,null,null);
            }
            if (intent.getByteArrayExtra("image")!=null){
                byte[] res=intent.getByteArrayExtra("image");
                Bitmap bitmap=getPicFromBytes(res,null);
                Msg msg2=new Msg(bitmap,null,Msg.TYPE_RECEIVED);
                msgList.add(msg2);
                adapter.notifyItemInserted(msgList.size()-1);
                msgRecyclerView.scrollToPosition(msgList.size()-1);
            }

        }

    }
    public  Bitmap getPicFromBytes(byte[] bytes, BitmapFactory.Options opts) {

        if (bytes != null)
            if (opts != null)
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length,  opts);
            else
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return null;

    }
    protected void hideInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View v = getWindow().peekDecorView();
        if (null != v) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

}
