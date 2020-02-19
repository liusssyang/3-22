package heath.com.test2_jmessage;

import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.event.NotificationClickEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import heath.com.test2_jmessage.activity.createmessage.CreateSigTextMessageActivity;
import heath.com.test2_jmessage.activity.createmessage.ShowMessageActivity;
import heath.com.test2_jmessage.application.IMDebugApplication;

import static android.content.Context.USAGE_STATS_SERVICE;
import static cn.jpush.im.android.api.jmrtc.JMRTCInternalUse.getApplicationContext;
import static heath.com.test2_jmessage.activity.TypeActivity.TAG;
import static heath.com.test2_jmessage.activity.createmessage.CreateSigTextMessageActivity.mEt_text;

/**
 * 在demo中对于通知栏点击事件和在线消息接收事件，我们都直接在全局监听
 */
public class GlobalEventListener {
    private Context appContext;
    private final static String TAG = "log1";
    private UsageStatsManager mUsageStatsManager;

    private LocalBroadcastManager localBroadcastManager;
    public GlobalEventListener(Context context) {
        appContext = context;
        JMessageClient.registerEventReceiver(this);
    }

    public void onEvent(NotificationClickEvent event) {

    }

    public void onEvent(MessageEvent event) {
        TextContent textContent = (TextContent) event.getMessage().getContent();
        String text=textContent.getText();
        localBroadcastManager=LocalBroadcastManager.getInstance(appContext);
        Intent intent=new Intent("message");
        intent.putExtra("key", text);
        if (text.equals("Ask"))
            intent.putExtra("SysMessage",SysMessage());
        else
            Log.d(TAG,"wrong");
        localBroadcastManager.sendBroadcast(intent);

    }

    private void jumpToActivity(Message msg) {
        UserInfo fromUser = msg.getFromUser();
        final Intent notificationIntent = new Intent(appContext, CreateSigTextMessageActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (msg.getTargetType() == ConversationType.group) {
            GroupInfo groupInfo = (GroupInfo) msg.getTargetInfo();
            notificationIntent.putExtra(ShowMessageActivity.EXTRA_IS_GROUP, true);
            notificationIntent.putExtra(ShowMessageActivity.EXTRA_GROUPID, groupInfo.getGroupID());
        } else {
            notificationIntent.putExtra(ShowMessageActivity.EXTRA_IS_GROUP, false);
        }

        notificationIntent.putExtra(ShowMessageActivity.EXTRA_FROM_USERNAME, fromUser.getUserName());
        notificationIntent.putExtra(ShowMessageActivity.EXTRA_FROM_APPKEY, fromUser.getAppKey());
        notificationIntent.putExtra(ShowMessageActivity.EXTRA_MSG_TYPE, msg.getContentType().toString());
        notificationIntent.putExtra(ShowMessageActivity.EXTRA_MSGID, msg.getId());
        appContext.startActivity(notificationIntent);

    }
    private String SysMessage(){
        permission();
        String s1="";
        mUsageStatsManager = (UsageStatsManager) IMDebugApplication.getContext().getSystemService(USAGE_STATS_SERVICE);
        Calendar beginCal = Calendar.getInstance();
        beginCal.add(Calendar.HOUR_OF_DAY, -1);
        Calendar endCal = Calendar.getInstance();
        UsageStatsManager manager=(UsageStatsManager)getApplicationContext().getSystemService(USAGE_STATS_SERVICE);
        List<UsageStats> stats=manager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,beginCal.getTimeInMillis(),endCal.getTimeInMillis());
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH)+1;
        int date = c.get(Calendar.DATE);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);

        StringBuilder sb=new StringBuilder();
        for(UsageStats us:stats){
            try {
                PackageManager pm=getApplicationContext().getPackageManager();
                ApplicationInfo applicationInfo=pm.getApplicationInfo(us.getPackageName(),PackageManager.GET_META_DATA);
                if((applicationInfo.flags&applicationInfo.FLAG_SYSTEM)<=0){
                    SimpleDateFormat format = new SimpleDateFormat("MM/dd HH:mm:ss");
                    String t=format.format(new Date(us.getLastTimeUsed()));
                    if (us.getTotalTimeInForeground()!=0&&!pm.getApplicationLabel(applicationInfo).toString().equals("test3")){
                        sb.append("应用名称:   "+pm.getApplicationLabel(applicationInfo)+"\n"+"最近使用:   "+t+"\n"+"前台运行:   "+us.getTotalTimeInForeground()/1000/60+"min"+us.getTotalTimeInForeground()/1000%60+"s"+"\n\n");
                        s1=sb.toString();
                        s1=s1+"统计时间："+year+"/"+month+"/"+date+"   "+hour+":"+minute+":"+second;
                        Log.d(TAG,sb.toString());}
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return s1;
    }
    public void permission(){
        AppOpsManager appOpt = (AppOpsManager) IMDebugApplication.getContext()
                .getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOpt.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), IMDebugApplication.getContext().getPackageName());
        boolean isGranted=mode == AppOpsManager.MODE_ALLOWED;
        if(!isGranted)
            IMDebugApplication.getContext().startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));

    }

}
