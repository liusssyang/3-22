package heath.com.test2_jmessage.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.callback.GetUserInfoListCallback;
import cn.jpush.im.android.api.event.ChatRoomNotificationEvent;
import cn.jpush.im.android.api.event.CommandNotificationEvent;
import cn.jpush.im.android.api.event.ConversationRefreshEvent;
import cn.jpush.im.android.api.event.GroupAnnouncementChangedEvent;
import cn.jpush.im.android.api.event.GroupApprovalEvent;
import cn.jpush.im.android.api.event.GroupApprovalRefuseEvent;
import cn.jpush.im.android.api.event.GroupApprovedNotificationEvent;
import cn.jpush.im.android.api.event.GroupBlackListChangedEvent;
import cn.jpush.im.android.api.event.GroupMemNicknameChangedEvent;
import cn.jpush.im.android.api.event.LoginStateChangeEvent;
import cn.jpush.im.android.api.event.MessageReceiptStatusChangeEvent;
import cn.jpush.im.android.api.event.MyInfoUpdatedEvent;
import cn.jpush.im.android.api.event.OfflineMessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.DeviceInfo;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import de.hdodenhof.circleimageview.CircleImageView;
import heath.com.test2_jmessage.MyDialog.TypeDialog;
import heath.com.test2_jmessage.R;
import heath.com.test2_jmessage.StatusBar.StatusBarUtil;
import heath.com.test2_jmessage.activity.chatroom.ChatRoomActivity;
import heath.com.test2_jmessage.activity.conversation.ConversationActivity;
import heath.com.test2_jmessage.activity.conversation.GetConversationInfoActivity;
import heath.com.test2_jmessage.activity.createmessage.CreateMessageActivity;
import heath.com.test2_jmessage.activity.createmessage.ShowTransCommandActivity;
import heath.com.test2_jmessage.activity.friend.AddFriendActivity;
import heath.com.test2_jmessage.activity.friend.FriendAskManage;
import heath.com.test2_jmessage.activity.friend.FriendContactManager;
import heath.com.test2_jmessage.activity.groupinfo.ApplyJoinGroupActivity;
import heath.com.test2_jmessage.activity.groupinfo.GroupInfoActivity;
import heath.com.test2_jmessage.activity.groupinfo.ShowGroupApprovalActivity;
import heath.com.test2_jmessage.activity.groupinfo.ShowMemNicknameChangedActivity;
import heath.com.test2_jmessage.activity.jmrtc.JMRTCActivity;
import heath.com.test2_jmessage.activity.setting.SettingMainActivity;
import heath.com.test2_jmessage.activity.setting.ShowLogoutReasonActivity;
import heath.com.test2_jmessage.activity.setting.UpdateUserAvatar;
import heath.com.test2_jmessage.activity.setting.UpdateUserInfoActivity;
import heath.com.test2_jmessage.activity.showinfo.ShowAnnouncementChangedActivity;
import heath.com.test2_jmessage.activity.showinfo.ShowChatRoomNotificationActivity;
import heath.com.test2_jmessage.activity.showinfo.ShowGroupBlcakListChangedActivity;
import heath.com.test2_jmessage.activity.showinfo.ShowMyInfoUpdateActivity;
import heath.com.test2_jmessage.adapter.personAdapter;
import heath.com.test2_jmessage.application.IMDebugApplication;
import heath.com.test2_jmessage.recycleView_item.personMsg;
import heath.com.test2_jmessage.tools.PushToast;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class TypeActivity extends Activity implements View.OnClickListener {
    public static final String TAG = "ly13172";
    public static final String LOGOUT_REASON = "logout_reason";
    private TextView mTv_showOfflineMsg;
    private TextView tv_refreshEvent;
    private TextView tv_deviceInfo,newFriends;
    private TextView addNew;
    private TextView menu,headusername,headappkey,headvision,signature;
    public static final String INFO_UPDATE = "info_update";
    public static final String TRANS_COMMAND_SENDER = "trans_command_sender";
    public static final String TRANS_COMMAND_TARGET = "trans_command_target";
    public static final String TRANS_COMMAND_TYPE = "trans_command_type";
    public static final String TRANS_COMMAND_CMD = "trans_command_cmd";
    private RecyclerView recyclerView;
    public static personAdapter adapter;
    public static List<Bitmap> personIcon=new ArrayList<>();
    public static List<personMsg> personList=new ArrayList<>();
    private DrawerLayout drawerLayout;
    private IntentFilter intentFilter;
    private Localreceiver localRceiver;
    private LocalBroadcastManager localBroadcastManager;
    private CircleImageView circleImageView;
    public static long myUserId,backUserId;
    public static Bitmap myIcon;
    private final Handler handler = new Handler();
    public boolean isReady;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JMessageClient.registerEventReceiver(this);
        initView();
        Log.d(TAG, "TypeActivity_onCreate ");
        isReady=false;
        handler.postDelayed(task, 1);

    }
    private void initView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_type);
        PushToast.getInstance().init(this);
        zoomInViewSize(StatusBarUtil.getStatusBarHeight(this));
        backUserId=getIntent().getLongExtra("userId",0);
        final Toolbar toolbar=findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        setActionBar(toolbar);
        ActionBar actionBar=getActionBar();
        drawerLayout=findViewById(R.id.sigText_drawerLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View navHeaderView = navigationView.inflateHeaderView(R.layout.headlayout);
        headusername=navHeaderView.findViewById(R.id.head_username);
        circleImageView=navHeaderView.findViewById(R.id.picture);
        headappkey=navHeaderView.findViewById(R.id.head_appkey);
        headvision=navHeaderView.findViewById(R.id.head_vision);
        signature=navHeaderView.findViewById(R.id.signature);
        final TextView index=findViewById(R.id.index);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                drawerLayout.closeDrawers();
                Intent intent = new Intent();
                if (item.toString().equals("退出")) {
                    UserInfo myInfo = JMessageClient.getMyInfo();
                    if (myInfo != null) {
                        JMessageClient.logout();
                        personList.clear();
                        PushToast.getInstance().createToast("提示","已登出",null,true);
                        intent.setClass(getApplicationContext(), RegisterAndLoginActivity.class);
                        setResult(8);
                        startActivity(intent);
                        finish();
                    } else {
                        PushToast.getInstance().createToast("提示","登出失败",null,false);
                    }
                }
                if (item.toString().equals("设置")) {
                    intent.setClass(getApplicationContext(), SettingMainActivity.class);
                    startActivityForResult(intent, 0);
                }
                if (item.toString().equals("群组")) {
                    intent.setClass(getApplicationContext(), GroupInfoActivity.class);
                     startActivity(intent);
                }
                if (item.toString().equals("聊天室相关")) {

                }
                if (item.toString().equals("会话")) {
                    intent.setClass(getApplicationContext(), ConversationActivity.class);
                    startActivity(intent);                }
                if (item.toString().equals("添加文件")) {
                    intent=new Intent(getApplicationContext(), GetConversationInfoActivity.class);
                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(intent);
                }
                if (item.toString().equals("更多")) {
                    intent=new Intent(getApplicationContext(),FriendContactManager.class);
                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                   getApplicationContext().startActivity(intent);
                }
                if (item.toString().equals("历史纪录")) {
                    SharedPreferences.Editor editor=getApplicationContext().getSharedPreferences("history"+myUserId,0).edit();
                    editor.clear();
                    editor.apply();
                    Toast.makeText(getApplicationContext(),"cleared",Toast.LENGTH_LONG).show();
                }
                    return true;
            }
        });
        Resources resource=getBaseContext().getResources();
        ColorStateList csl=resource.getColorStateList(R.color.selector);
        navigationView.setItemTextColor(csl);
        navigationView.setItemIconTintList(csl);
        navigationView.getMenu().getItem(0).setChecked(true);
        RelativeLayout re=navHeaderView.findViewById(R.id.head);
        re.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent=new Intent(IMDebugApplication.getContext(), UpdateUserInfoActivity.class);
               intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
               IMDebugApplication.getContext().startActivity(intent);
               overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
            }
        });
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(IMDebugApplication.getContext(), UpdateUserAvatar.class);
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                IMDebugApplication.getContext().startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
            }
        });
        final LinearLayout index2_linear=findViewById(R.id.index2_linear);
        final LinearLayout other_linear=findViewById(R.id.other_linear);
        findViewById(R.id.bt_about_setting).setOnClickListener(this);
        findViewById(R.id.bt_create_message).setOnClickListener(this);
        findViewById(R.id.bt_group_info).setOnClickListener(this);
        findViewById(R.id.bt_conversation).setOnClickListener(this);
        findViewById(R.id.bt_friend).setOnClickListener(this);
        findViewById(R.id.bt_chatroom).setOnClickListener(this);
        findViewById(R.id.bt_jmrtc).setOnClickListener(this);
        menu=findViewById(R.id.type_menu);
        menu.setOnClickListener(new View.OnClickListener() {@Override public void onClick(View v) { drawerLayout.openDrawer(GravityCompat.START); }});
        mTv_showOfflineMsg = (TextView) findViewById(R.id.tv_showOfflineMsg);
        tv_refreshEvent = (TextView) findViewById(R.id.tv_refreshEvent);
        tv_deviceInfo = (TextView) findViewById(R.id.tv_deviceInfo);
        recyclerView=findViewById(R.id.type_recyclerview);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter=new personAdapter(personList);
        recyclerView.setAdapter(adapter);
        intentFilter=new IntentFilter();
        intentFilter.addAction("message");
        localRceiver =new Localreceiver(personList,recyclerView,adapter);
        localBroadcastManager= LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(localRceiver,intentFilter);
        addNew=findViewById(R.id.addNew);
        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TypeDialog mydialog=new TypeDialog(v.getContext());
                mydialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                Window dialogWindow = mydialog.getWindow();
                dialogWindow.setGravity(Gravity.TOP|Gravity.RIGHT);
                dialogWindow.getDecorView().setPadding(0, 0, 0, 0);
                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                lp.y=100;
                lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialogWindow.setAttributes(lp);
                dialogWindow.setWindowAnimations(R.style.dialogWindowAnim);
                mydialog.show();
            }
        });
        newFriends=findViewById(R.id.newFriends);
        newFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(getApplicationContext(), FriendAskManage.class);
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
            }
        });
        index.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                other_linear.setVisibility(View.GONE);
                index.setVisibility(View.GONE);
                toolbar.setVisibility(View.GONE);
                index2_linear.setVisibility(View.VISIBLE);
            }
        });
        TextView cancel=findViewById(R.id.cancel);
        final LinearLayout find_visibility=findViewById(R.id.find_visibility);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideInput();
                other_linear.setVisibility(View.VISIBLE);
                index.setVisibility(View.VISIBLE);
                toolbar.setVisibility(View.VISIBLE);
                index2_linear.setVisibility(View.GONE);
                find_visibility.setVisibility(View.GONE);
            }
        });
        final TextView find_single=findViewById(R.id.find_single);
        final TextView find_group=findViewById(R.id.find_group);
        final EditText index2=findViewById(R.id.index2);
        LinearLayout find_single_line=findViewById(R.id.find_single_line);
        LinearLayout find_group_line=findViewById(R.id.find_group_line);
        find_group_line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ApplyJoinGroupActivity.class);
                intent.putExtra("groupId",index2.getText().toString());
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
            }
        });
        find_single_line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddFriendActivity.class);
                intent.putExtra("username",index2.getText().toString());
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);

            }
        });
        index2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                find_visibility.setVisibility(View.VISIBLE);
                find_group.setText(s.toString());
                find_single.setText(s.toString());
            }
        });
        adapter.notifyDataSetChanged();
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        if (isReady){
            SharedPreferences pref2=getBaseContext().getSharedPreferences("backdata"+myUserId,0);
            for (int i=0;i<personList.size();i++) {
                String simpleMessage=pref2.getString("simplemessage" + personList.get(i).getUserId(), "");
                String time=pref2.getString("time" +personList.get(i).getUserId(), "");
                if (!simpleMessage.equals("")) {
                    personList.get(i).setSimpleMessage(simpleMessage);
                    adapter.notifyDataSetChanged();
                }
                if (!time.equals("")){
                    personList.get(i).setTime(time);
                    adapter.notifyDataSetChanged();
                }
            }
        }
        Intent intent = getIntent();
        Gson gson = new Gson();
        List<DeviceInfo> deviceInfos = gson.fromJson(intent.getStringExtra("deviceInfos"), new TypeToken<List<DeviceInfo>>() {}.getType());
        if (deviceInfos != null) {
            for (DeviceInfo deviceInfo : deviceInfos) {
                tv_deviceInfo.append("设备登陆记录:\n");
                tv_deviceInfo.append("设备ID: " + deviceInfo.getDeviceID() + " 平台：" + deviceInfo.getPlatformType()
                        + " 上次登陆时间:" + deviceInfo.getLastLoginTime() + "登陆状态:" + deviceInfo.isLogin() + "在线状态:" + deviceInfo.getOnlineStatus()
                        + " flag:" + deviceInfo.getFlag());
            }
        }
    }
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: ");
    }
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        UserInfo info = JMessageClient.getMyInfo();
        if (null != info) {
            myUserId=info.getUserID();
            String detail="当前账号："+info.getUserName()+"\nAppkey：\n"+info.getAppKey();
            headappkey.setText(detail);
            String name=TextUtils.isEmpty(info.getNickname())?info.getUserName():info.getNickname();
            headusername.setText(name);
            signature.setText(info.getSignature());
            headvision.setText("Vision："+JMessageClient.getSdkVersionString());
            info.getAvatarBitmap(new GetAvatarBitmapCallback() {
                @Override
                public void gotResult(int i, String s, Bitmap bitmap) {
                    Log.d("Hresult", i+"\n"+s);
                    if (s.equals("Success")){
                        myIcon=bitmap;
                        circleImageView.setImageBitmap(myIcon);
                    }else myIcon= BitmapFactory.decodeResource(getResources(),R.drawable.icon_left_default);
                }
            });
        }

    }
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        JMessageClient.unRegisterEventReceiver(this);
        localBroadcastManager.unregisterReceiver(localRceiver);
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        Log.d(TAG, "onNewIntent: here is "+personList.size());
        /*for (int j=0;j<personList.size();j++) {
            personIcon.add(BitmapFactory.decodeFile(list.get(j).getAvatarFile().getPath()));
            personList.add(
                    new personMsg(
                            list.get(j).getNickname()
                            ,list.get(j).getUserID()
                            ,BitmapFactory.decodeFile(list.get(j).getAvatarFile().getPath())
                            ,list.get(j).getUserName()
                            ,list.get(j).getNotename()
                            ,list.get(j).getAppKey()
                            ,null
                            ,list.get(j).getSignature()
                            ,""
                            ,list.get(j).getSignature()
                            ,list.get(j).getGender().toString()
                            ,list.get(j).getAddress()
                            ,list.get(j).getNoteText()
                            ,list.get(j).getBirthday()));
            adapter.notifyItemChanged(personList.size()-1);
        }*/
    }
    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.bt_about_setting:
                intent.setClass(getApplicationContext(), SettingMainActivity.class);
                startActivityForResult(intent, 0);
                break;
            case R.id.bt_create_message:
                intent.setClass(getApplicationContext(), CreateMessageActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_group_info:
                intent.setClass(getApplicationContext(), GroupInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_conversation:
                intent.setClass(getApplicationContext(), ConversationActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_friend:
                intent.setClass(getApplicationContext(), FriendContactManager.class);
                startActivity(intent);
                break;
            case R.id.bt_chatroom:
                intent.setClass(getApplicationContext(), ChatRoomActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_jmrtc:
                intent.setClass(getApplicationContext(), JMRTCActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 8) {
            mTv_showOfflineMsg.setText("");
            tv_refreshEvent.setText("");
        }
    }

    public void onEvent(LoginStateChangeEvent event) {
        LoginStateChangeEvent.Reason reason = event.getReason();
        UserInfo myInfo = event.getMyInfo();
        Intent intent = new Intent(getApplicationContext(), ShowLogoutReasonActivity.class);
        intent.putExtra(LOGOUT_REASON, "reason = " + reason + "\n" + "logout user name = " + myInfo.getUserName());
        startActivity(intent);
    }
    public void onEventMainThread(OfflineMessageEvent event) {
        Conversation conversation = event.getConversation();
        List<Message> newMessageList = event.getOfflineMessageList();//获取此次离线期间会话收到的新消息列表
        List<Integer> offlineMsgIdList = new ArrayList<Integer>();
        if (conversation != null && newMessageList != null) {
            for (Message msg : newMessageList) {
                offlineMsgIdList.add(msg.getId());
            }
            mTv_showOfflineMsg.append(String.format(Locale.SIMPLIFIED_CHINESE, "收到%d条来自%s的离线消息。\n", newMessageList.size(), conversation.getTargetId()));
            mTv_showOfflineMsg.append("会话类型 = " + conversation.getType() + "\n消息ID = " + offlineMsgIdList + "\n\n");
        } else {
            mTv_showOfflineMsg.setText("conversation is null or new message list is null");
        }
    }
    public void onEventMainThread(ConversationRefreshEvent event) {
        Conversation conversation = event.getConversation();
        ConversationRefreshEvent.Reason reason = event.getReason();
        if (conversation != null) {
            tv_refreshEvent.append(String.format(Locale.SIMPLIFIED_CHINESE, "收到ConversationRefreshEvent事件,待刷新的会话是%s.\n", conversation.getTargetId()));
            tv_refreshEvent.append("事件发生的原因 : " + reason + "\n");
        } else {
            tv_refreshEvent.setText("conversation is null");
        }
    }
    public void onEvent(GroupMemNicknameChangedEvent event) {
        new ShowMemChangeTask(getApplicationContext()).execute(event);
    }
    public void onEvent(GroupAnnouncementChangedEvent event) {
        StringBuilder builder = new StringBuilder();
        builder.append("群组ID:").append(event.getGroupID()).append("\n\n");
        for (GroupAnnouncementChangedEvent.ChangeEntity entity : event.getChangeEntities()) {
            builder.append("类型:").append(entity.getType().toString()).append("\n");
            builder.append("发起者(username):").append(entity.getFromUserInfo().getUserName()).append("\n");
            builder.append("内容:").append(entity.getAnnouncement().toJson()).append("\n");
            builder.append("时间:").append(entity.getCtime()).append("\n\n");
        }
        Intent intent = new Intent(getApplicationContext(), ShowAnnouncementChangedActivity.class);
        intent.putExtra(ShowAnnouncementChangedActivity.SHOW_ANNOUNCEMENT_CHANGED, builder.toString());
        startActivity(intent);
    }
    public void onEvent(GroupBlackListChangedEvent event) {
        StringBuilder builder = new StringBuilder();
        builder.append("群组ID:").append(event.getGroupID()).append("\n\n");
        for (GroupBlackListChangedEvent.ChangeEntity entity : event.getChangeEntities()) {
            builder.append("类型:").append(entity.getType().toString()).append("\n");
            builder.append("操作者(username):").append(entity.getOperator().getUserName()).append("\n");
            builder.append("被操作的用户(username):\n");
            for (UserInfo userInfo : entity.getUserInfos()) {
                builder.append(userInfo.getUserName()).append(" ");
            }
            builder.append("\n");
            builder.append("时间:").append(entity.getCtime()).append("\n\n");
        }
        Intent intent = new Intent(getApplicationContext(), ShowGroupBlcakListChangedActivity.class);
        intent.putExtra(ShowGroupBlcakListChangedActivity.SHOW_GROUP_BLACK_LIST_CHANGED, builder.toString());
        startActivity(intent);
    }
    public void onEventBackgroundThread(ChatRoomNotificationEvent event) {
        new ShowChatRoomNotificationTask(getApplicationContext(), event).run();
    }
    private static class ShowChatRoomNotificationTask {
        private WeakReference<Context> contextWeakReference;
        ChatRoomNotificationEvent event;
        ShowChatRoomNotificationTask(Context context, ChatRoomNotificationEvent event) {
            this.contextWeakReference = new WeakReference<Context>(context);
            this.event = event;
        }
        void run() {
            final StringBuilder builder = new StringBuilder();
            builder.append("事件id:").append(event.getEventID()).append("\n");
            builder.append("聊天室id:").append(event.getRoomID()).append("\n");
            builder.append("事件类型:").append(event.getType()).append("\n");
            final CountDownLatch countDownLatch = new CountDownLatch(2);
            event.getOperator(new GetUserInfoCallback() {
                @Override
                public void gotResult(int responseCode, String responseMessage, UserInfo info) {
                    builder.append("事件发起者:");
                    if (0 == responseCode) {
                        if (info == null) {
                            builder.append("api操作");
                        } else {
                            builder.append(info.getUserName());
                        }
                    } else {
                        builder.append("获取失败");
                    }
                    countDownLatch.countDown();
                }
            });
            event.getTargetUserInfoList(new GetUserInfoListCallback() {
                @Override
                public void gotResult(int responseCode, String responseMessage, List<UserInfo> userInfoList) {
                    builder.append("目标用户:\n");
                    if (0 == responseCode) {
                        for (UserInfo userInfo : userInfoList) {
                            builder.append(userInfo.getUserName()).append("\n");
                        }
                    } else {
                        builder.append("获取失败");
                    }
                    countDownLatch.countDown();
                }
            });
            try {
                countDownLatch.await(30, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            builder.append("事件发生时间:" + event.getCtime());
            Context context = contextWeakReference.get();
            if (context != null) {
                Intent intent = new Intent(context.getApplicationContext(), ShowChatRoomNotificationActivity.class);
                intent.putExtra(ShowChatRoomNotificationActivity.SHOW_CHAT_ROOM_NOTIFICATION, builder.toString());
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        }
    }
    public void onEvent(MyInfoUpdatedEvent event) {
        UserInfo myInfo = event.getMyInfo();
        Intent intent = new Intent(TypeActivity.this, ShowMyInfoUpdateActivity.class);
        intent.putExtra(INFO_UPDATE, myInfo.getUserName());
        startActivity(intent);
    }
    public void onEvent(final CommandNotificationEvent event) {
        event.getSenderUserInfo(new GetUserInfoCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage, UserInfo info) {
                if (0 == responseCode) {
                    final String sender = info.getUserName();
                    event.getTargetInfo(new CommandNotificationEvent.GetTargetInfoCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage, Object targetInfo, CommandNotificationEvent.Type type) {
                            if (0 == responseCode) {
                                String target = "";
                                if (type == CommandNotificationEvent.Type.single) {
                                    target = ((UserInfo) targetInfo).getUserName();
                                } else if (type == CommandNotificationEvent.Type.group) {
                                    target += ((GroupInfo) targetInfo).getGroupID();
                                }
                                Intent intent = new Intent(getApplicationContext(), ShowTransCommandActivity.class);
                                intent.putExtra(TRANS_COMMAND_SENDER, sender);
                                intent.putExtra(TRANS_COMMAND_TARGET, target);
                                intent.putExtra(TRANS_COMMAND_TYPE, event.getType().toString());
                                intent.putExtra(TRANS_COMMAND_CMD, event.getMsg());
                                startActivity(intent);
                            } else {
                                Log.w(TAG, "CommandNotificationEvent getSenderUserInfo failed. " + "code = " +
                                        responseCode + " desc = " + responseMessage);
                            }
                        }
                    });
                } else {
                    Log.w(TAG, "CommandNotificationEvent getTargetInfo failed. " + "code = " +
                            responseCode + " desc = " + responseMessage);
                }
            }
        });
    }
    public void onEvent(GroupApprovalEvent event) {
        Intent intent = new Intent(getApplicationContext(), ShowGroupApprovalActivity.class);
        Gson gson = new Gson();
        intent.putExtra("GroupApprovalEvent", gson.toJson(event));
        intent.putExtra(ShowGroupApprovalActivity.EXTRA_EVENT_TYPE, ShowGroupApprovalActivity.TYPE_APPROVAL);
        startActivity(intent);
    }
    public void onEvent(final GroupApprovalRefuseEvent event) {
        final Intent intent = new Intent(getApplicationContext(), ShowGroupApprovalActivity.class);
        event.getFromUserInfo(new GetUserInfoCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage, UserInfo info) {
                if (0 == responseCode) {
                    final String fromUsername = info.getUserName();
                    final String fromAppKey = info.getAppKey();
                    event.getToUserInfoList(new GetUserInfoListCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage, List<UserInfo> userInfoList) {
                            if (0 == responseCode) {
                                String toUsername = userInfoList.get(0).getUserName();
                                String toAppKey = userInfoList.get(0).getAppKey();
                                intent.putExtra("notification", "入群审批拒绝通知" + "\n群组gid:" + event.getGid()
                                        + "\n群主username:" + fromUsername
                                        + "\n群主appKey:" + fromAppKey
                                        + "\n被拒绝者username:" + toUsername
                                        + "\n被拒绝者appKey:" + toAppKey
                                        + "\n拒绝理由:" + event.getReason());
                                intent.putExtra(ShowGroupApprovalActivity.EXTRA_EVENT_TYPE, ShowGroupApprovalActivity.TYPE_APPROVAL_REFUSE);
                                startActivity(intent);
                            }
                        }
                    });
                }
            }
        });
    }
    public void onEventMainThread(GroupApprovedNotificationEvent event) {
        tv_refreshEvent.append("\n收到入群审批已审批事件通知.对应审批事件id: " + event.getApprovalEventID());
    }
    public void onEventMainThread(MessageReceiptStatusChangeEvent event) {
        Conversation conv = event.getConversation();
        tv_refreshEvent.append(String.format(Locale.SIMPLIFIED_CHINESE, "\n收到MessageReceiptStatusChangeEvent事件,会话对象是%s\n", conv.getTargetId()));
        for (MessageReceiptStatusChangeEvent.MessageReceiptMeta meta : event.getMessageReceiptMetas()) {
            tv_refreshEvent.append(String.format(Locale.SIMPLIFIED_CHINESE,
                    "回执数有更新的消息serverMsgID：%d\n这条消息当前还未发送已读回执的人数：%d\n", meta.getServerMsgId(), meta.getUnReceiptCnt()));
        }

    }
    private static class ShowMemChangeTask extends AsyncTask<GroupMemNicknameChangedEvent, Integer, String> {
        private WeakReference<Context> contextWeakReference;

        ShowMemChangeTask(Context context) {
            this.contextWeakReference = new WeakReference<Context>(context);
        }
        @Override
        protected String doInBackground(GroupMemNicknameChangedEvent... groupMemNicknameChangedEvents) {
            final StringBuilder builder = new StringBuilder();
            if (groupMemNicknameChangedEvents != null && groupMemNicknameChangedEvents.length > 0) {
                GroupMemNicknameChangedEvent event = groupMemNicknameChangedEvents[0];
                builder.append("群组id:" + event.getGroupID() + "\n");
                final CountDownLatch countDownLatch = new CountDownLatch(event.getChangeEntities().size());
                for (final GroupMemNicknameChangedEvent.ChangeEntity changeEntity : event.getChangeEntities()) {
                    changeEntity.getFromUserInfo(new GetUserInfoCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage, UserInfo info) {
                            String fromUsername = info != null ? info.getUserName() : "";
                            builder.append("修改昵称者：" + fromUsername + "\n");
                            changeEntity.getToUserInfoList(new GetUserInfoListCallback() {
                                @Override
                                public void gotResult(int responseCode, String responseMessage, List<UserInfo> userInfoList) {
                                    List<String> useNames = new ArrayList<>();
                                    if (userInfoList != null) {
                                        for (UserInfo userInfo : userInfoList) {
                                            useNames.add(userInfo.getUserName());
                                        }
                                    }
                                    builder.append("被修改昵称者: " + useNames + "\n");
                                    builder.append("修改后的昵称：" + changeEntity.getNickname() + "\n");
                                    builder.append("修改时间：" + changeEntity.getCtime() + "\n\n");
                                    countDownLatch.countDown();
                                }
                            });
                        }
                    });
                }
                try {
                    countDownLatch.await(30, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return builder.toString();
        }
        @Override
        protected void onPostExecute(String s) {
            Context context = contextWeakReference.get();
            if (context != null) {
                Intent intent = new Intent();
                intent.setClass(context, ShowMemNicknameChangedActivity.class);
                intent.putExtra(ShowMemNicknameChangedActivity.SHOW_MEM_NICKNAME_CHANGED, s);
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        }
    }
    private void zoomInViewSize(int height) {
        View img1 = findViewById(R.id.statusbar);
        ViewGroup.LayoutParams  lp = img1.getLayoutParams();
        lp.height =height;
        img1.setLayoutParams(lp);
    }
    class  Localreceiver extends BroadcastReceiver {
        private List<personMsg> msgList;
        private RecyclerView msgRecyclerView;
        private personAdapter adapter;

        public Localreceiver(List<personMsg> msgList,RecyclerView msgRecyclerView,personAdapter adapter){
            this.msgList=msgList;
            this.msgRecyclerView=msgRecyclerView;
            this.adapter=adapter;
        }
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra("init").equals("1")){
                Log.d(TAG, "onReceive: ready"+"|"+personList.size());
                SharedPreferences pref2=getBaseContext().getSharedPreferences("backdata"+myUserId,0);
                for (int i=0;i<personList.size();i++) {
                    String simpleMessage=pref2.getString("simplemessage" + personList.get(i).getUserId(), "");
                    String time=pref2.getString("time" +personList.get(i).getUserId(), "");
                    if (!simpleMessage.equals("")) {
                        personList.get(i).setSimpleMessage(simpleMessage);
                        adapter.notifyDataSetChanged();
                    }
                    if (!time.equals("")){
                        personList.get(i).setTime(time);
                        adapter.notifyDataSetChanged();
                    }
                }
            }else
            for (int i=0;i<personList.size();i++){
                if (personList.get(i).getUserId()==intent.getLongExtra("userId",0)){
                if (intent.getStringExtra("key")!=null) {
                    msgList.get(i).setSimpleMessage(intent.getStringExtra("key"));
                    msgList.get(i).setTime(intent.getStringExtra("time"));
                }
                if (intent.getStringExtra("SysMessage")!=null){
                msgList.get(i).setSimpleMessage("[System]");
                }
                if (intent.getByteArrayExtra("image")!=null){
                msgList.get(i).setSimpleMessage("[图片]");
            }
                adapter.notifyDataSetChanged();
                }

            }
        }
    }
    private final Runnable task = new Runnable() {
        @Override
        public void run() {
            if ( personList.size()!=0){
                Intent intent=new Intent("message");
                intent.putExtra("init","1");
                localBroadcastManager.sendBroadcast(intent);
                for (int i=0;i<personList.size();i++){
                    Log.d("personList.size", i+"run: "+personList.get(i).getUserName());
                }
            isReady=true;
            }
            else{
                Log.d("personList.size", "running");
                handler.postDelayed(this,1);
                isReady=false;
            }
        }
    };
    protected void hideInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View v = getWindow().peekDecorView();
        if (null != v) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
}
