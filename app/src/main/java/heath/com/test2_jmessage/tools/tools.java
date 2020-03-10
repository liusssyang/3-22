package heath.com.test2_jmessage.tools;

import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import cn.jpush.im.android.api.ContactManager;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoListCallback;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.android.api.options.MessageSendingOptions;
import cn.jpush.im.api.BasicCallback;
import heath.com.test2_jmessage.application.MyApplication;
import heath.com.test2_jmessage.recycleView_item.personMsg;

import static cn.jpush.im.android.api.jmrtc.JMRTCInternalUse.getApplicationContext;
import static heath.com.test2_jmessage.activity.TypeActivity.adapter;
import static heath.com.test2_jmessage.application.MyApplication.personList;
import static heath.com.test2_jmessage.application.MyApplication.sb;

public class tools {
    /**
     * 撤回单聊消息
     */
    public static void retractMessage(String userName, String appkey, int msgId) {
        Conversation conv;
        if (!TextUtils.isEmpty(userName)) {
            conv = JMessageClient.getSingleConversation(userName, appkey);
        } else {
            Toast.makeText(getApplicationContext(), "请填写会话相关属性username/gid", Toast.LENGTH_SHORT).show();
            return;
        }
        if (null == conv) {
            Toast.makeText(getApplicationContext(), "此会话不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            Message msg = conv.getMessage(msgId);
            conv.retractMessage(msg, new BasicCallback() {
                @Override
                public void gotResult(int responseCode, String responseMessage) {
                    if (0 == responseCode) {
                        Toast.makeText(getApplicationContext(), "消息撤回成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "消息撤回失败，code = " + responseCode + " \n msg = " + responseMessage, Toast.LENGTH_LONG).show();
                    }
                }
            });
        } catch (NumberFormatException e) {
            Toast.makeText(getApplicationContext(), "请输入正确的msg id", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    public static int getActionBarSize() {
        TypedArray actionbarSizeTypedArray = getApplicationContext().obtainStyledAttributes(new int[]{android.R.attr.actionBarSize});
        float h = actionbarSizeTypedArray.getDimension(0, 0);
        return (int) h;
    }

    /**
     * 漫游记录对话管理
     */
    public static Conversation getConversation(String targetName, String targetGidString) {
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

    /**
     * 获取好友列表
     */
    public static void getUserInfoList() {
        ContactManager.getFriendList(new GetUserInfoListCallback() {
            @Override
            public void gotResult(int i, String s, List<UserInfo> list) {
                if (i == 0) {
                    if (list.size() != 0) {
                        MyApplication.list = list;
                        MyApplication.isAvaluable = true;
                    } else {
                        MyApplication.isAvaluable = true;
                        Log.d("getUserInfoList", "获取成功");
                    }
                } else {
                    MyApplication.isAvaluable = false;
                    Log.d("getUserInfoList", "获取失败");
                    Log.i("getUserInfoList", "ContactManager.getFriendList" + ", responseCode = " + i + " ; LoginDesc = " + s);
                }
            }
        });
    }

    public static void getUserInfoStringBuilder() {
        ContactManager.getFriendList(new GetUserInfoListCallback() {
            @Override
            public void gotResult(int i, String s, List<UserInfo> list) {
                if (i == 0) {
                    for (UserInfo info : list) {
                        sb.append(info);
                    }
                    if (sb.length() == 0) {
                        sb.append("null");
                    }
                    Log.d("getUserInfoList", "获取成功");
                } else {
                    Log.d("getUserInfoList", "获取失败");
                    Log.i("getUserInfoList", "ContactManager.getFriendList" + ", responseCode = " + i + " ; LoginDesc = " + s);
                }
            }
        });

    }

    public static void refreshPersonList() {
        ContactManager.getFriendList(new GetUserInfoListCallback() {
            @Override
            public void gotResult(int i, String s, List<UserInfo> list) {
                if (i == 0) {
                    MyApplication.isAvaluable = true;
                    for (int j = 0; j < list.size(); j++) {
                        personList.add(
                                new personMsg(
                                        list.get(j).getNickname()
                                        , list.get(j).getUserID()
                                        , BitmapFactory.decodeFile(list.get(j).getAvatarFile().getPath())
                                        , list.get(j).getUserName()
                                        , list.get(j).getNotename()
                                        , list.get(j).getAppKey()
                                        , true
                                        , list.get(j).getSignature()
                                        , ""
                                        , list.get(j).getSignature()
                                        , list.get(j).getGender().toString()
                                        , list.get(j).getAddress()
                                        , list.get(j).getNoteText()
                                        , list.get(j).getBirthday()));
                        adapter.notifyDataSetChanged();
                    }
                    if (list.size() == 0) {
                        personList.add(
                                new personMsg(null, 0, null, null,
                                        null, null, true,
                                        null, null, null,
                                        null, null, null, 0));
                    }
                } else if (i != 0) {
                    MyApplication.isAvaluable = false;
                    PushToast.getInstance().createToast("提示", "获取好友失败", null, false);
                    Log.i("FriendContactManager", "ContactManager.getFriendList" + ", responseCode = " + i + " ; LoginDesc = " + s);
                }
            }
        });
    }

    /**
     * 获取当前时间
     */
    public static String CurrentTime() {
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

    /**
     * 发送信息时备用
     */
    public static void sendMessage(String name, String text, String appkey, String customFromName,
                                   String extraKey, String extraValue, String NotificationTitle,
                                   String NotificationAtPrefix, String NotificationText, String MsgCount) {
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
                        Toast.makeText(MyApplication.getContext(), "发送成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MyApplication.getContext(), "发送失败", Toast.LENGTH_SHORT).show();
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
            if (MsgCount != null) {
                try {
                    options.setMsgCount(Integer.valueOf(MsgCount));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //发送消息
            JMessageClient.sendMessage(message, options);
        } else {
            Toast.makeText(MyApplication.getContext(), "必填字段不能为空", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 日期格式转换
     */
    public static String secondToDate(long second, String patten) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(second);//转换为毫秒
        Date date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat(patten);
        String dateString = format.format(date);
        return dateString;
    }

    /**
     * 返回日时分秒
     */
    public static String secondToTime(long second) {
        second = second * 1000;
        long days = second / 86400;//转换天数
        second = second % 86400;//剩余秒数
        long hours = second / 3600;//转换小时数
        second = second % 3600;//剩余秒数
        long minutes = second / 60;//转换分钟
        second = second % 60;//剩余秒数
        if (0 < days) {
            return days + "天，" + hours + "小时，" + minutes + "分，" + second + "秒";
        } else {
            return hours + "小时，" + minutes + "分，" + second + "秒";
        }
    }

    /**
     * 由毫秒数转换年数
     */
    public static String Age(long second) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        int year = calendar.get(Calendar.YEAR);
        calendar.setTimeInMillis(second);//转换为毫秒
        Date date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy");
        int year2 = Integer.parseInt(format.format(date));
        year2 = year - year2;
        return year2 + "";
    }
}
