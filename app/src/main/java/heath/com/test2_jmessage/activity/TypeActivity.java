package heath.com.test2_jmessage.activity;

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

import java.util.List;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.model.DeviceInfo;
import cn.jpush.im.android.api.model.UserInfo;
import de.hdodenhof.circleimageview.CircleImageView;
import heath.com.test2_jmessage.MyDialog.TypeDialog;
import heath.com.test2_jmessage.R;
import heath.com.test2_jmessage.StatusBar.StatusBarUtil;
import heath.com.test2_jmessage.activity.chatroom.ChatRoomActivity;
import heath.com.test2_jmessage.activity.conversation.ConversationActivity;
import heath.com.test2_jmessage.activity.createmessage.CreateMessageActivity;
import heath.com.test2_jmessage.activity.friend.AddFriendActivity;
import heath.com.test2_jmessage.activity.friend.FriendAskManage;
import heath.com.test2_jmessage.activity.friend.FriendContactManager;
import heath.com.test2_jmessage.activity.groupinfo.ApplyJoinGroupActivity;
import heath.com.test2_jmessage.activity.groupinfo.GroupInfoActivity;
import heath.com.test2_jmessage.activity.jmrtc.JMRTCActivity;
import heath.com.test2_jmessage.activity.setting.SettingMainActivity;
import heath.com.test2_jmessage.activity.setting.UpdateUserAvatar;
import heath.com.test2_jmessage.activity.setting.UpdateUserInfoActivity;
import heath.com.test2_jmessage.adapter.personAdapter;
import heath.com.test2_jmessage.application.MyApplication;
import heath.com.test2_jmessage.recycleView_item.personMsg;
import heath.com.test2_jmessage.tools.PushToast;
import heath.com.test2_jmessage.tools.tools;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static heath.com.test2_jmessage.application.MyApplication.list;
import static heath.com.test2_jmessage.application.MyApplication.personList;

/**
 * netstat -ano|findstr "5037"
 */
public class TypeActivity extends Activity implements View.OnClickListener {
    public static final String TAG = "ly13172";
    public static final String LOGOUT_REASON = "logout_reason";
    private TextView mTv_showOfflineMsg;
    private TextView tv_refreshEvent;
    private TextView tv_deviceInfo,head_state;
    private TextView headusername, headappkey, headvision, signature;
    public static final String INFO_UPDATE = "info_update";
    public static final String TRANS_COMMAND_SENDER = "trans_command_sender";
    public static final String TRANS_COMMAND_TARGET = "trans_command_target";
    public static final String TRANS_COMMAND_TYPE = "trans_command_type";
    public static final String TRANS_COMMAND_CMD = "trans_command_cmd";
    public static personAdapter adapter;
    private DrawerLayout drawerLayout;
    private Localreceiver localRceiver;
    private LocalBroadcastManager localBroadcastManager;
    private CircleImageView circleImageView;
    public static long myUserId, backUserId;
    public static Bitmap myIcon;
    private final Handler handler = new Handler();
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        /*循环体做初始化list的保证*/
        handler.postDelayed(task, 1);
    }

    private void initView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_type);
        PushToast.getInstance().init(this);
        zoomInViewSize(StatusBarUtil.getStatusBarHeight(this));
        backUserId = getIntent().getLongExtra("userId", 0);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.sigText_drawerLayout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View navHeaderView = navigationView.inflateHeaderView(R.layout.headlayout);
        headusername = navHeaderView.findViewById(R.id.head_username);
        circleImageView = navHeaderView.findViewById(R.id.picture);
        headappkey = navHeaderView.findViewById(R.id.head_appkey);
        headvision = navHeaderView.findViewById(R.id.head_vision);
        signature = navHeaderView.findViewById(R.id.signature);
        head_state=navHeaderView.findViewById(R.id.head_state);
        head_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(getApplicationContext(), SettingMainActivity.class);
                startActivityForResult(intent, 0);
            }
        });
        swipeRefreshLayout = findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#d7a101"), Color.parseColor("#54c745"), Color.parseColor("#f16161"), Color.BLUE, Color.YELLOW);


        final TextView index = findViewById(R.id.index);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                drawerLayout.closeDrawers();
                Intent intent = new Intent();
                if (item.toString().equals("退出")) {
                    UserInfo myInfo = JMessageClient.getMyInfo();
                    if (myInfo != null) {
                        JMessageClient.logout();
                        list.clear();
                        personList.clear();
                        PushToast.getInstance().createToast("提示", "已登出", null, true);
                        intent.setClass(getApplicationContext(), RegisterAndLoginActivity.class);
                        setResult(8);
                        startActivity(intent);
                        finish();
                    } else {
                        PushToast.getInstance().createToast("提示", "登出失败", null, false);
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
                if (item.toString().equals("会话")) {
                    intent.setClass(getApplicationContext(), ConversationActivity.class);
                    startActivity(intent);
                }
                if (item.toString().equals("更多")) {
                    intent = new Intent(getApplicationContext(), FriendContactManager.class);
                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(intent);
                }
                if (item.toString().equals("历史纪录")) {
                    SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("history" + myUserId, 0).edit();
                    editor.clear();
                    editor.apply();
                    Toast.makeText(getApplicationContext(), "cleared", Toast.LENGTH_LONG).show();
                }
                return true;
            }
        });
        Resources resource = getBaseContext().getResources();
        ColorStateList csl = resource.getColorStateList(R.color.selector);
        navigationView.setItemTextColor(csl);
        navigationView.setItemIconTintList(csl);
        navigationView.getMenu().getItem(0).setChecked(true);
        RelativeLayout re = navHeaderView.findViewById(R.id.head);
        re.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyApplication.getContext(), UpdateUserInfoActivity.class);
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                MyApplication.getContext().startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
            }
        });
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyApplication.getContext(), UpdateUserAvatar.class);
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                MyApplication.getContext().startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
            }
        });
        final LinearLayout index2_linear = findViewById(R.id.index2_linear);
        final LinearLayout other_linear = findViewById(R.id.other_linear);
        findViewById(R.id.bt_about_setting).setOnClickListener(this);
        findViewById(R.id.bt_create_message).setOnClickListener(this);
        findViewById(R.id.bt_group_info).setOnClickListener(this);
        findViewById(R.id.bt_conversation).setOnClickListener(this);
        findViewById(R.id.bt_friend).setOnClickListener(this);
        findViewById(R.id.bt_chatroom).setOnClickListener(this);
        findViewById(R.id.bt_jmrtc).setOnClickListener(this);
        TextView menu = findViewById(R.id.type_menu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        mTv_showOfflineMsg = findViewById(R.id.tv_showOfflineMsg);
        tv_refreshEvent = findViewById(R.id.tv_refreshEvent);
        tv_deviceInfo = findViewById(R.id.tv_deviceInfo);
        RecyclerView recyclerView = findViewById(R.id.type_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new personAdapter(personList);
        recyclerView.setAdapter(adapter);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("message");
        localRceiver = new Localreceiver(personList, recyclerView, adapter);
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(localRceiver, intentFilter);
        TextView addNew = findViewById(R.id.addNew);
        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TypeDialog mydialog = new TypeDialog(v.getContext());
                mydialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                Window dialogWindow = mydialog.getWindow();
                assert dialogWindow != null;
                dialogWindow.setGravity(Gravity.TOP | Gravity.END);
                dialogWindow.getDecorView().setPadding(0, 0, 0, 0);
                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                lp.y = 100;
                lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialogWindow.setAttributes(lp);
                dialogWindow.setWindowAnimations(R.style.dialogWindowAnim);
                mydialog.show();
            }
        });
        TextView newFriends = findViewById(R.id.newFriends);
        newFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
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
        TextView cancel = findViewById(R.id.cancel);
        final LinearLayout find_visibility = findViewById(R.id.find_visibility);
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
        final TextView find_single = findViewById(R.id.find_single);
        final TextView find_group = findViewById(R.id.find_group);
        final EditText index2 = findViewById(R.id.index2);
        LinearLayout find_single_line = findViewById(R.id.find_single_line);
        LinearLayout find_group_line = findViewById(R.id.find_group_line);
        find_group_line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ApplyJoinGroupActivity.class);
                intent.putExtra("groupId", index2.getText().toString());
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
            }
        });
        find_single_line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddFriendActivity.class);
                intent.putExtra("username", index2.getText().toString());
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);

            }
        });
        index2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                find_visibility.setVisibility(View.VISIBLE);
                find_group.setText(s.toString());
                find_single.setText(s.toString());
            }
        });
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                tools.getUserInfoList();
                if (MyApplication.isAvaluable) {
                    personList.clear();
                    /*通知广播接收器该刷新personList了**/
                    Intent intent = new Intent("message");
                    intent.putExtra("init", "1");
                    localBroadcastManager.sendBroadcast(intent);
                    PushToast.getInstance().createToast("提示", "刷新成功", null, true);
                } else {
                    PushToast.getInstance().createToast("提示", "刷新失败", null, false);
                }
                swipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MyApplication.isAvaluable) {
            SharedPreferences pref2 = getBaseContext().getSharedPreferences("backdata" + myUserId, 0);
            for (int i = 0; i < personList.size(); i++) {
                String simpleMessage = pref2.getString("simplemessage" + personList.get(i).getUserId(), "");
                String time = pref2.getString("time" + personList.get(i).getUserId(), "");
                if (!"".equals(simpleMessage)) {
                    personList.get(i).setSimpleMessage(simpleMessage);
                    adapter.notifyDataSetChanged();
                }
                if (!"".equals(time)) {
                    personList.get(i).setTime(time);
                    adapter.notifyDataSetChanged();
                }
            }
        }
        Intent intent = getIntent();
        Gson gson = new Gson();
        List<DeviceInfo> deviceInfos = gson.fromJson(intent.getStringExtra("deviceInfos"), new TypeToken<List<DeviceInfo>>() {
        }.getType());
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

    }

    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        UserInfo info = JMessageClient.getMyInfo();
        if (null != info) {
            myUserId = info.getUserID();
            String detail = "当前账号：" + info.getUserName() + "\nAppkey：\n" + info.getAppKey();
            headappkey.setText(detail);
            String name = TextUtils.isEmpty(info.getNickname()) ? info.getUserName() : info.getNickname();
            headusername.setText(name);
            signature.setText(info.getSignature());
            String vision = "Vision：" + JMessageClient.getSdkVersionString();
            headvision.setText(vision);
            info.getAvatarBitmap(new GetAvatarBitmapCallback() {
                @Override
                public void gotResult(int i, String s, Bitmap bitmap) {
                    Log.d("Hresult", i + "\n" + s);
                    if (s.equals("Success")) {
                        myIcon = bitmap;
                        circleImageView.setImageBitmap(myIcon);
                    } else
                        myIcon = BitmapFactory.decodeResource(getResources(), R.drawable.icon_left_default);
                }
            });
            Log.d(TAG, "onStart:当前免打扰 "+MyApplication.getNoDisturbToMyselfResult);
            if (MyApplication.getNoDisturbToMyselfResult==1){
                String state="当前免打扰";
                head_state.setVisibility(View.VISIBLE);
                head_state.setText(state);
            }else head_state.setVisibility(View.GONE);

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
        Log.d(TAG, "onNewIntent: here is " + personList.size());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 8) {
            mTv_showOfflineMsg.setText("");
            tv_refreshEvent.setText("");
        }
    }

    private void zoomInViewSize(int height) {
        View img1 = findViewById(R.id.statusbar);
        ViewGroup.LayoutParams lp = img1.getLayoutParams();
        lp.height = height;
        img1.setLayoutParams(lp);
    }

    class Localreceiver extends BroadcastReceiver {
        private List<personMsg> msgList;
        private RecyclerView msgRecyclerView;
        private personAdapter adapter;

        public Localreceiver(List<personMsg> msgList, RecyclerView msgRecyclerView, personAdapter adapter) {
            this.msgList = msgList;
            this.msgRecyclerView = msgRecyclerView;
            this.adapter = adapter;
        }

        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra("init").equals("1")) {
                tools.initPersonlist();
                adapter.notifyDataSetChanged();
                Log.d(TAG, "onReceive: ready" + "|" + personList.size());
                SharedPreferences pref2 = getBaseContext().getSharedPreferences("backdata" + myUserId, 0);
                for (int i = 0; i < personList.size(); i++) {
                    String simpleMessage = pref2.getString("simplemessage" + personList.get(i).getUserId(), "");
                    String time = pref2.getString("time" + personList.get(i).getUserId(), "");
                    if (!simpleMessage.equals("")) {
                        personList.get(i).setSimpleMessage(simpleMessage);
                        adapter.notifyDataSetChanged();
                    }
                    if (!"".equals(time)) {
                        personList.get(i).setTime(time);
                        adapter.notifyDataSetChanged();
                    }
                }
            } else
                for (int i = 0; i < personList.size(); i++) {
                    if (personList.get(i).getUserId() == intent.getLongExtra("userId", 0)) {
                        if (intent.getStringExtra("key") != null) {
                            msgList.get(i).setSimpleMessage(intent.getStringExtra("key"));
                            msgList.get(i).setTime(intent.getStringExtra("time"));
                        }
                        if (intent.getStringExtra("SysMessage") != null) {
                            msgList.get(i).setSimpleMessage("[System]");
                        }
                        if (intent.getByteArrayExtra("image") != null) {
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
            if (MyApplication.isAvaluable) {
                Intent intent = new Intent("message");
                intent.putExtra("init", "1");
                localBroadcastManager.sendBroadcast(intent);
            } else {
                /*在这个循环中如果MyApplication中UserInfo—list初始化失败
                  则由本活动循环代为初始化，代偿值仍保存于MyApplication中*/
                tools.getUserInfoList();
                Log.d("ly13172", "running");
                handler.postDelayed(this, 1000);
            }
        }
    };

    public void hideInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View v = getWindow().peekDecorView();
        if (null != v) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
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
}
