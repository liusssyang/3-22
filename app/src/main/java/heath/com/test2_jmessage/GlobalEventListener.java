package heath.com.test2_jmessage;

import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.event.NotificationClickEvent;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import heath.com.test2_jmessage.MyDialog.Mydialog;
import heath.com.test2_jmessage.activity.createmessage.CreateSigTextMessageActivity;
import heath.com.test2_jmessage.activity.createmessage.ShowMessageActivity;
import heath.com.test2_jmessage.application.IMDebugApplication;

import static android.content.Context.USAGE_STATS_SERVICE;
import static cn.jpush.im.android.api.jmrtc.JMRTCInternalUse.getApplicationContext;
import static heath.com.test2_jmessage.activity.TypeActivity.myUserId;
import static heath.com.test2_jmessage.activity.TypeActivity.personList;

/**
 * 在demo中对于通知栏点击事件和在线消息接收事件，我们都直接在全局监听
 */
public class GlobalEventListener {
    private Context appContext;
    private final static String TAG = "log1";
    private UsageStatsManager mUsageStatsManager;
    public GlobalEventListener(Context context) {
        appContext = context;
        JMessageClient.registerEventReceiver(this);
    }
    public void onEvent(NotificationClickEvent event) {
        jumpToActivity(event.getMessage());
    }
    public void onEvent(MessageEvent event) {
        LocalBroadcastManager localBroadcastManager=LocalBroadcastManager.getInstance(appContext);
        dealing(localBroadcastManager,event);
    }
    private void jumpToActivity(Message msg) {
        UserInfo fromUser = msg.getFromUser();
        SharedPreferences.Editor editor3=getApplicationContext().getSharedPreferences("backdata"+myUserId,0).edit();
        long userId=msg.getFromUser().getUserID();
        Calendar cal;
        String year, month, day, hour, minute, second, timeA;
        String total = "";
        cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        year = String.valueOf(cal.get(Calendar.YEAR));
        month = String.valueOf(cal.get(Calendar.MONTH) + 1);
        day = String.valueOf(cal.get(Calendar.DATE));
        if (cal.get(Calendar.AM_PM) == 0)
            hour = String.valueOf(cal.get(Calendar.HOUR));
        else
            hour = String.valueOf(cal.get(Calendar.HOUR) + 12);
        minute = String.valueOf(cal.get(Calendar.MINUTE));
        second = String.valueOf(cal.get(Calendar.SECOND));
        timeA = month + "/" + day + "  " + hour + ":" + minute;
        editor3.putString("time"+userId,timeA);
        ContentType contentType=ContentType.valueOf(msg.getContentType().toString());
        switch (contentType) {
            case text:
                TextContent textContent = (TextContent) msg.getContent();
                String text=textContent.getText();
                editor3.putString("simplemessage"+userId,text);
                editor3.apply();
            case image:
                String thumbLocalPath = ((ImageContent)msg.getContent()).getLocalThumbnailPath();
                if (!TextUtils.isEmpty(thumbLocalPath)) {
                    editor3.putString("simplemessage"+userId,"[图片]");
                    editor3.apply();
                }
                break;

            case voice:

                break;

            case location:
                break;
            case file:

                break;
            case custom:
                break;
            case eventNotification:
                break;
            case prompt:
                break;
            case video:

                break;
        }
        final Intent notificationIntent = new Intent(appContext, CreateSigTextMessageActivity.class);
        for (int i=0;i<personList.size();i++){
            if (personList.get(i).getUserId()==fromUser.getUserID()){
                notificationIntent.putExtra("position",i);
            }
        }

        if (msg.getTargetType() == ConversationType.group) {
            GroupInfo groupInfo = (GroupInfo) msg.getTargetInfo();
            notificationIntent.putExtra(ShowMessageActivity.EXTRA_IS_GROUP, true);
            notificationIntent.putExtra(ShowMessageActivity.EXTRA_GROUPID, groupInfo.getGroupID());
        } else {
            notificationIntent.putExtra(ShowMessageActivity.EXTRA_IS_GROUP, false);
        }
        notificationIntent.putExtra("name", fromUser.getUserName());
        notificationIntent.putExtra("userId",fromUser.getUserID());
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
    private void dealing(LocalBroadcastManager localBroadcastManager,MessageEvent event){
        long userId=event.getMessage().getFromUser().getUserID();
        SharedPreferences.Editor editor2=getApplicationContext().getSharedPreferences("history"+myUserId,0).edit();
        SharedPreferences pref=getApplicationContext().getSharedPreferences("history"+myUserId,0);
        SharedPreferences.Editor editor3=getApplicationContext().getSharedPreferences("backdata"+myUserId,0).edit();
        String history=pref.getString("historyRecord"+userId," ");
        ContentType contentType=ContentType.valueOf(event.getMessage().getContentType().toString());
        Intent intent=new Intent("message");
        Calendar cal;
        String year, month, day, hour, minute, second, timeA;
        String total = "";
        cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        year = String.valueOf(cal.get(Calendar.YEAR));
        month = String.valueOf(cal.get(Calendar.MONTH) + 1);
        day = String.valueOf(cal.get(Calendar.DATE));
        if (cal.get(Calendar.AM_PM) == 0)
            hour = String.valueOf(cal.get(Calendar.HOUR));
        else
            hour = String.valueOf(cal.get(Calendar.HOUR) + 12);
        minute = String.valueOf(cal.get(Calendar.MINUTE));
        second = String.valueOf(cal.get(Calendar.SECOND));
        timeA = month + "/" + day + "  " + hour + ":" + minute;
        editor3.putString("time"+userId,timeA);
        intent.putExtra("time",timeA);
        intent.putExtra("userId",userId);
        intent.putExtra("init","2");
        switch (contentType) {
            case text:
                TextContent textContent = (TextContent) event.getMessage().getContent();
                String text=textContent.getText();
                intent.putExtra("key", text);
                history=history+text+"text_left%%";
                editor2.putString("historyRecord"+userId,history);
                editor2.apply();
                editor3.putString("simplemessage"+userId,text);
                editor3.apply();
                if (text.equals("Ask"))
                    intent.putExtra("SysMessage",SysMessage());
                else
                    Log.d(TAG,"wrong");
                localBroadcastManager.sendBroadcast(intent);
                break;
            case image:
                Mydialog.imageContent=(ImageContent)event.getMessage().getContent();
                Mydialog.message=event.getMessage();
                SharedPreferences.Editor editor=getApplicationContext().getSharedPreferences("data",0).edit();
                String thumbLocalPath = ((ImageContent)event.getMessage().getContent()).getLocalThumbnailPath();
                editor.putString("filepath",null);
                editor.apply();
                if (!TextUtils.isEmpty(thumbLocalPath)) {
                    history=history+thumbLocalPath+"image_left%%";
                    editor2.putString("historyRecord"+userId,history);
                    editor3.putString("simplemessage"+userId,"图片");
                    editor3.apply();
                    editor2.apply();
                    Bitmap bitmap = BitmapFactory.decodeFile(thumbLocalPath);
                    Mydialog.bitmap=bitmap;
                    ByteArrayOutputStream output = new ByteArrayOutputStream();//初始化一个流对象
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);//把bitmap100%高质量压缩 到 output对象里
                    byte[] result = output.toByteArray();//转换成功了  result就是一个bit的资源数组
                    intent.putExtra("image", result);
                    localBroadcastManager.sendBroadcast(intent);
                }
                break;

            case voice:

                break;

            case location:
                break;
            case file:

                break;
            case custom:
                break;
            case eventNotification:
                break;
            case prompt:
                break;
            case video:

                break;
        }
    }

}
