package heath.com.test2_jmessage.activity.setting;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import cn.jpush.im.android.api.JMessageClient;
import heath.com.test2_jmessage.R;
import heath.com.test2_jmessage.StatusBar.StatusBarUtil;
import heath.com.test2_jmessage.application.MyApplication;
import heath.com.test2_jmessage.tools.tools;

/**
 * Created by ${chenyn} on 16/3/23.
 *
 * @desc : 设置用户信息
 */
public class SettingMainActivity extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private int notificationFlag = JMessageClient.getNotificationFlag();
    private EditText mEt_setNoDisturbGlobal;
    private TextView mTv_showNoDisturbGlobal;
    private Switch cb_sound;
    private Switch cb_vibrate;
    private Switch cb_led;
    private int result=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        /*******initView2设置通知栏********/
        initView2();
    }

    private void initView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_about_setting);
        zoomInViewSize(StatusBarUtil.getStatusBarHeight(this));
        final LocalBroadcastManager localBroadcastManager=LocalBroadcastManager.getInstance(this);

        Button bt_getMyInfo = (Button) findViewById(R.id.bt_get_my_info);

        Button bt_isCurrentUserPasswordValid = (Button) findViewById(R.id.bt_iscurrent_user_password_valid);

        Button bt_getBlacklist = (Button) findViewById(R.id.bt_get_black_list);
        Button bt_addUsersToBlacklist = (Button) findViewById(R.id.bt_add_or_remove_users_to_blacklist);

        Button bt_updateUserExtras = (Button) findViewById(R.id.bt_update_user_extras);
        Button bt_getNoDisturbList = (Button) findViewById(R.id.bt_get_no_disturb_list);

        bt_getMyInfo.setOnClickListener(this);
          bt_isCurrentUserPasswordValid.setOnClickListener(this);
           bt_getBlacklist.setOnClickListener(this);
        bt_addUsersToBlacklist.setOnClickListener(this);


        bt_getNoDisturbList.setOnClickListener(this);
          TextView manage_noteFriends = findViewById(R.id.manage_noteFriends);
        manage_noteFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), UpdateUserInfoActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
            }
        });
        TextView manage_HistoryFriends = findViewById(R.id.manage_HistoryFriends);
        manage_HistoryFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(SettingMainActivity.this, UpdateUserExtras.class);
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
            }
        });
        TextView manage_message_notify = findViewById(R.id.manage_message_notify);
        manage_message_notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(SettingMainActivity.this, UpdatePassword.class);
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
            }
        });

        TextView manage_back=findViewById(R.id.manage_back);
        manage_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
            }
        });
        Switch no_disturb = findViewById(R.id.no_disturb);
        if (MyApplication.getNoDisturbToMyselfResult ==1)
            no_disturb.setChecked(true);
        else
            no_disturb.setChecked(false);
        no_disturb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    MyApplication.getNoDisturbToMyselfResult=1;
                    tools.setNoDisturbToMyself(1);
                } else {
                    MyApplication.getNoDisturbToMyselfResult=0;
                    tools.setNoDisturbToMyself(0);
                }
            }
        });
    }

    private void initView2() {
        Switch cb_enable = findViewById(R.id.cb_notify_enable);
        cb_sound = findViewById(R.id.cb_notify_sound);
        cb_vibrate = findViewById(R.id.cb_notify_vibrate);
        cb_led = findViewById(R.id.cb_notify_led);

        //初始化几个cb的选中状态，需要在下面setOnCheckedChangeListener之前进行。
        boolean isDisable = 0 != (notificationFlag & JMessageClient.FLAG_NOTIFY_DISABLE);
        cb_enable.setChecked(!isDisable);
        cb_sound.setEnabled(!isDisable);
        cb_vibrate.setEnabled(!isDisable);
        cb_led.setEnabled(!isDisable);

        boolean isSoundEnable = 0 != (notificationFlag & JMessageClient.FLAG_NOTIFY_WITH_SOUND);
        boolean isVibrateEnable = 0 != (notificationFlag & JMessageClient.FLAG_NOTIFY_WITH_VIBRATE);
        boolean isLedEnable = 0 != (notificationFlag & JMessageClient.FLAG_NOTIFY_WITH_LED);
        cb_sound.setChecked(isSoundEnable);
        cb_vibrate.setChecked(isVibrateEnable);
        cb_led.setChecked(isLedEnable);

        cb_enable.setOnCheckedChangeListener(this);
        cb_sound.setOnCheckedChangeListener(this);
        cb_vibrate.setOnCheckedChangeListener(this);
        cb_led.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        ProgressDialog mProgressDialog;
        switch (v.getId()) {
/**#################    获取当前用户信息    #################*/
            case R.id.bt_get_my_info:
                intent.setClass(SettingMainActivity.this, InfoActivity.class);
                startActivity(intent);
                break;
/**#################    用户登出    #################*/

/**#################    判断输入的字符串是否匹配当前密码    #################*/
            case R.id.bt_iscurrent_user_password_valid:
                intent.setClass(SettingMainActivity.this, AssertEqualsActivity.class);
                startActivity(intent);
                break;
/**#################    获取指定用户信息    #################*/

/**#################    更新密码    #################*/

/**#################    更新用户信息    #################*/

/**#################    获取当前用户的黑名单列表    #################*/
            case R.id.bt_get_black_list:
                intent.setClass(SettingMainActivity.this, GetBlackListActivity.class);
                startActivity(intent);
                break;
/**#################    加入或移除黑名单    #################*/
            case R.id.bt_add_or_remove_users_to_blacklist:
                intent.setClass(SettingMainActivity.this, AddRemoveBlackListActivity.class);
                startActivity(intent);
                break;
/**#################    设置通知类型    #################*/

/**#################    获取当前用户设置的免打扰名单    #################*/
            case R.id.bt_get_no_disturb_list:
                intent.setClass(SettingMainActivity.this, NoDisturbListActivity.class);
                startActivity(intent);
                break;
/**#################    全局免打扰设置    #################*/

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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cb_notify_enable:
                if (isChecked) {
                    notificationFlag ^= JMessageClient.FLAG_NOTIFY_DISABLE;
                } else {
                    cb_sound.setChecked(false);
                    cb_vibrate.setChecked(false);
                    cb_led.setChecked(false);
                    notificationFlag |= JMessageClient.FLAG_NOTIFY_DISABLE;
                }
                cb_sound.setEnabled(isChecked);
                cb_vibrate.setEnabled(isChecked);
                cb_led.setEnabled(isChecked);
                JMessageClient.setNotificationFlag(notificationFlag);
                break;
            case R.id.cb_notify_sound:
                if (isChecked) {
                    notificationFlag |= JMessageClient.FLAG_NOTIFY_WITH_SOUND;
                } else {
                    notificationFlag ^= JMessageClient.FLAG_NOTIFY_WITH_SOUND;
                }
                JMessageClient.setNotificationFlag(notificationFlag);
                break;
            case R.id.cb_notify_vibrate:
                if (isChecked) {
                    notificationFlag |= JMessageClient.FLAG_NOTIFY_WITH_VIBRATE;
                } else {
                    notificationFlag ^= JMessageClient.FLAG_NOTIFY_WITH_VIBRATE;
                }
                JMessageClient.setNotificationFlag(notificationFlag);
                break;
            case R.id.cb_notify_led:
                if (isChecked) {
                    notificationFlag |= JMessageClient.FLAG_NOTIFY_WITH_LED;
                } else {
                    notificationFlag ^= JMessageClient.FLAG_NOTIFY_WITH_LED;
                }
                JMessageClient.setNotificationFlag(notificationFlag);
                break;
        }
    }
}
