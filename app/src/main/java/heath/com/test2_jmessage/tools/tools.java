package heath.com.test2_jmessage.tools;

import android.text.TextUtils;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.options.MessageSendingOptions;
import cn.jpush.im.api.BasicCallback;
import heath.com.test2_jmessage.application.IMDebugApplication;

public class tools {
    /**获取当前时间*/
    public static String CurrentTime(){
        Calendar cal;
        final String year, month, day, hour, minute, second, timeA;
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
        return timeA;
    }
    /**发送信息时备用*/
    public static  void sendMessage(String name, String text, String appkey, String customFromName,
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
    /**日期格式转换*/
    public static String secondToDate(long second,String patten) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(second);//转换为毫秒
        Date date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat(patten);
        String dateString = format.format(date);
        return dateString;
    }
    /**返回日时分秒*/
    public static String secondToTime(long second) {
        second=second*1000;
        long days = second / 86400;//转换天数
        second = second % 86400;//剩余秒数
        long hours = second / 3600;//转换小时数
        second = second % 3600;//剩余秒数
        long minutes = second / 60;//转换分钟
        second = second % 60;//剩余秒数
        if (0 < days){
            return days + "天，"+hours+"小时，"+minutes+"分，"+second+"秒";
        }else {
            return hours+"小时，"+minutes+"分，"+second+"秒";
        }
    }
    /**由毫秒数转换年数*/
    public static String Age(long second) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        int year = calendar.get(Calendar.YEAR);
        calendar.setTimeInMillis(second);//转换为毫秒
        Date date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy");
        int year2 =Integer.parseInt(format.format(date));
        year2=year-year2;
        return year2+"";
    }
}
