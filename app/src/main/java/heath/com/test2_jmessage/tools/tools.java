package heath.com.test2_jmessage.tools;

import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.gson.Gson;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import cn.jpush.im.android.api.ContactManager;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.DownloadCompletionCallback;
import cn.jpush.im.android.api.callback.GetGroupIDListCallback;
import cn.jpush.im.android.api.callback.GetGroupInfoCallback;
import cn.jpush.im.android.api.callback.GetGroupMembersCallback;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.callback.GetUserInfoListCallback;
import cn.jpush.im.android.api.callback.IntegerCallback;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.android.api.options.MessageSendingOptions;
import cn.jpush.im.api.BasicCallback;
import heath.com.test2_jmessage.activity.createmessage.CreateGroupTextMsgActivity;
import heath.com.test2_jmessage.application.MyApplication;
import heath.com.test2_jmessage.recycleView_item.Msg;
import heath.com.test2_jmessage.recycleView_item.personMsg;

import static android.content.Context.USAGE_STATS_SERVICE;
import static cn.jpush.im.android.api.jmrtc.JMRTCInternalUse.getApplicationContext;
import static heath.com.test2_jmessage.activity.TypeActivity.groupAdapter;
import static heath.com.test2_jmessage.activity.TypeActivity.personAdapter;
import static heath.com.test2_jmessage.application.MyApplication.groupList;
import static heath.com.test2_jmessage.application.MyApplication.groupListIsAvaluable;
import static heath.com.test2_jmessage.application.MyApplication.list;
import static heath.com.test2_jmessage.application.MyApplication.list2;
import static heath.com.test2_jmessage.application.MyApplication.personList;

public class tools {
    public static void getGroupMembers(long groupId){
        JMessageClient.getGroupMembers(groupId, new GetGroupMembersCallback() {
            @Override
            public void gotResult(int i, String s, List<UserInfo> list) {
                if (i == 0) {
                    CreateGroupTextMsgActivity.GroupMemberList =list;
                }
            }
        });
    }

    /**
     * Get the no-disturb mode for yourself here,
     * but this method can only be assigned to global variables in MyApplication.class.
     */
    public static void getNoDisturbToMyself() {
        JMessageClient.getNoDisturbGlobal(new IntegerCallback() {
            @Override
            public void gotResult(int i, String s, Integer integer) {
                if (i == 0) {
                    MyApplication.getNoDisturbToMyselfResult = integer;
                    Log.i("SettingMainActivity", "JMessageClient.getNoDisturbGlobal" + ", responseCode = " + i + " ; desc = " + s);
                } else {
                        Log.i("SettingMainActivity", "JMessageClient.getNoDisturbGlobal" + ", responseCode = " + i + " ; desc = " + s);
                }
            }
        });
    }

    /**
     * Set the no-disturb mode to yourself here.
     */
    public static void setNoDisturbToMyself(int code) {
        JMessageClient.setNoDisturbGlobal(code, new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                if (i == 0) {
                    PushToast.getInstance().createToast("提示", "设置成功", null, true);
                    Log.i("SettingMainActivity", "JMessageClient.setNoDisturbGlobal" + ", responseCode = " + i + " ; desc = " + s);
                } else {
                    PushToast.getInstance().createToast("提示", "设置失败", null, false);
                    Log.i("SettingMainActivity", "JMessageClient.setNoDisturbGlobal" + ", responseCode = " + i + " ; desc = " + s);

                }
            }
        });
    }

    /**
     * History is shown here.
     * You can query the historical notes of group by enter the groupId,
     * or query the personal historical notes by enter the position
     * that confirm the userName.
     */
    public static boolean shownHistoryFromCloud(String userName, List<Msg> msgList, String groupId) {
        if (MyApplication.personListIsAvaluable) {
            Gson gson = new Gson();
            Conversation conversation = getConversation(userName, groupId);
            if (conversation != null) {
                msgList.clear();
                List<Message> list;
                if (!TextUtils.isEmpty(userName))
                    list = getConversation(userName, null).getAllMessage();
                else
                    list = getConversation(null, groupId).getAllMessage();
                for (int j = 0; j < list.size(); j++) {
                    Message message = conversation.getMessage(list.get(j).getId());
                    Log.d("HISTORY_RECORD", list.get(j).getContent().toJson());
                    Log.d("HISTORY_RECORD", list.get(j).getFromUser().getUserName() + "(" + list.get(j).getFromName() + ")");
                    Log.d("HISTORY_RECORD", list.get(j).getId() + "|" + list.get(j).getDirect());
                    Log.d("HISTORY_RECORD", list.get(j).getCreateTime() + "");
                    App app = gson.fromJson(list.get(j).getContent().toJson(), App.class);
                    Log.d("HISTORY_RECORD", "解析" + app.getPromptText());
                    Log.d("HISTORY_RECORD", "解析" + app.getText());
                    Log.d("HISTORY_RECORD", "解析" + app.getIsFileUploaded());
                    Log.d("HISTORY_RECORD", "解析" + app.getExtras());
                    Log.d("HISTORY_RECORD", "解析" + app.getHeight());
                    Log.d("HISTORY_RECORD", "解析" + app.getWidth());
                    Log.d("HISTORY_RECORD", "解析" + app.getLocalThumbnailPath());
                    Log.d("HISTORY_RECORD", "_______________________________\n");
                    if (app.getText() != null) {
                        if (list.get(j).getDirect().toString().equals("receive"))
                            msgList.add(new Msg(message));
                        else
                            msgList.add(new Msg(message));
                    }
                    if (app.getLocalThumbnailPath() != null) {
                        msgList.add(new Msg(message));
                    }
                }
                return true;
            } else {
                return false;
            }
        } else {
            PushToast.getInstance().createToast("提示", "数据获取失败", null, false);
            return false;
        }
    }

    /**
     * The history is read from the database here.
     * The number represents the limited number of messages read.
     */
    public static void shownHistoryFromDataBases(List<Msg> msgList, int limit,long userId) {
        List<LocalHistory> localHistories;
        if (limit < 0) {
            localHistories = DataSupport.
                    where("userid=?",userId+"").find(LocalHistory.class);

        } else {
            localHistories = DataSupport.where("userid=?",userId+"").
                    limit(limit).offset(tools.getDataBasesNumber() - limit)
                    .find(LocalHistory.class);
        }
        for (LocalHistory localHistory : localHistories) {

            msgList.add(new Msg(
                    localHistory.getIsFileUploaded(),
                    localHistory.getLocalThumbnailPath(),
                    localHistory.getUserName(),
                    localHistory.getAppKey(),
                    localHistory.getContent(),
                    localHistory.getType(),
                    localHistory.getMessageId(),
                    localHistory.getDialogIsOpen() == 1
            ));

        }
    }

    /**
     * Here is the number of database data.
     */
    public static int getDataBasesNumber() {
        List<LocalHistory> localHistories = DataSupport.
                where().find(LocalHistory.class);
        return localHistories.size();
    }

    /**
     * Record chat content to databases here.
     */
    public static void setLocalHistory(Message message, int position) {
        if (position >= 0) {
            LocalHistory localHistory = new LocalHistory(message, position);
            localHistory.setMyUserId(JMessageClient.getMyInfo().getUserID());
            localHistory.setMsgNumber(getDataBasesNumber());
            localHistory.save();
        } else {
            LocalHistory localHistory = new LocalHistory(message);
            localHistory.setMyUserId(JMessageClient.getMyInfo().getUserID());
            localHistory.setMsgNumber(getDataBasesNumber());
            localHistory.save();
        }
    }

    /**
     * Read the history from the databases here.
     * However, this process can be very slow when the databases content is too large.
     * Don't change the contents of the method easily.
     */
    public static void getLocalHistoryFromCloud(int position, String groupId) {
        if (MyApplication.personListIsAvaluable) {
            List<LocalHistory> localHistories = DataSupport.findAll(LocalHistory.class);
            Conversation conversation = getConversation(personList.get(position).getUserName(), groupId);
            if (conversation != null) {
                List<Message> list;
                list = getConversation(personList.get(position).getUserName(), "").getAllMessage();
                for (int j = 0; j < list.size(); j++) {
                    boolean judgement = true;
                    for (int i = 0; i < localHistories.size(); i++) {
                        if (localHistories.get(i).getId() == list.get(j).getId()) {
                            judgement = false;
                            break;
                        }
                    }
                    if (judgement) {
                        Message message = conversation.getMessage(list.get(j).getId());
                        LocalHistory localHistory = new LocalHistory(message);
                        localHistory.setMyUserId(JMessageClient.getMyInfo().getUserID());
                        localHistory.setMsgNumber(getDataBasesNumber());
                        localHistory.save();
                    }
                }
            }
        }
    }

    /**
     * Set the no-disturb mode to others here.
     */
    public static void setNoDisturb(final boolean judgement, final Switch no_disturb, final int code, String userName, String appKey) {

        JMessageClient.getUserInfo(userName, appKey, new GetUserInfoCallback() {
            @Override
            public void gotResult(int i, String s, UserInfo userInfo) {
                if (i == 0) {
                    Log.i("GetUserInfoActivity", "JMessageClient.getUserInfo" + ", responseCode = " + i + " ; desc = " + s);
                    userInfo.setNoDisturb(code, new BasicCallback() {
                        @Override
                        public void gotResult(int i, String s) {
                            if (i == 0) {
                                Log.i("GetUserInfoActivity", "userInfo.setNoDisturb" + ", responseCode = " + i + " ; desc = " + s);
                                PushToast.getInstance().createToast("提示", "设置成功", null, true);
                            } else {
                                Log.i("GetUserInfoActivity", "userInfo.setNoDisturb" + ", responseCode = " + i + " ; desc = " + s);
                                PushToast.getInstance().createToast("提示", "设置失败", null, false);
                                no_disturb.setChecked(judgement);
                            }
                        }
                    });
                } else {
                    no_disturb.setChecked(judgement);
                    PushToast.getInstance().createToast("提示", "设置失败", null, false);
                    Log.i("GetUserInfoActivity", "JMessageClient.getUserInfo" + ", responseCode = " + i + " ; desc = " + s);

                }
            }
        });
    }

    /**
     * Download the picture here, but the photoView belongs to MyDialog.
     */
    public static void getImageContent(final TextView download, final Message message, final PhotoView photoView, final Msg msg) {
        ImageContent imageContent = (ImageContent) message.getContent();
        imageContent.downloadOriginImage(message, new DownloadCompletionCallback() {
            @Override
            public void onComplete(int responseCode, String responseMessage, File file) {
                if (responseCode == 0) {
                    LocalHistory localHistory = new LocalHistory();
                    localHistory.setLocalDownload(file.getPath());
                    localHistory.updateAll("messageid=?", msg.getId() + "");
                    photoView.setImageBitmap(BitmapFactory.decodeFile(file.getPath()));
                    download.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "原图下载成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "原图下载失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Retract the message here, but id of the message can only be your own.
     */
    public static void retractMessage(String userName, String appkey, int msgId) {
        Log.d("retractMessage", userName + msgId);
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
        getNoDisturbToMyself();
        ContactManager.getFriendList(new GetUserInfoListCallback() {
            @Override
            public void gotResult(int i, String s, List<UserInfo> list) {
                if (i == 0) {
                    if (list.size() != 0) {
                        MyApplication.personListIsAvaluable = true;
                        MyApplication.list = list;
                    } else {
                        MyApplication.personListIsAvaluable = true;
                        Log.d("getUserInfoList", "获取成功");
                    }
                } else {
                    MyApplication.personListIsAvaluable = false;
                }
            }
        });
    }
    /**
     * 获取群列表
     */
    public static void getGroupIdList(){

        JMessageClient.getGroupIDList(new GetGroupIDListCallback() {
            @Override
            public void gotResult(int i, String s, List<Long> list) {
                if (i == 0) {
                    list2.clear();
                    PushToast.getInstance().createToast("提示", "获取群列表成功", null, true);
                    for(int j=0;j<list.size();j++){
                        Log.d("13174",list.get(j).toString());
                        JMessageClient.getGroupInfo(list.get(j), new GetGroupInfoCallback() {
                            @Override
                            public void gotResult(int i, String s, GroupInfo groupInfo) {
                                if (i == 0) {
                                   list2.add(groupInfo);
                                    groupListIsAvaluable=true;
                                    Log.d("13174",list2.size()+"");
                                }
                            }
                        });
                    }

                } else {
                    groupListIsAvaluable=false;
                }
            }
        });
    }
    public static void initPersonlist() {
        personList.clear();
        personAdapter.notifyDataSetChanged();
        for (int j = 0; j < list.size(); j++) {
            Bitmap bitmap;
            if (TextUtils.isEmpty(list.get(j).getAvatar())){
                bitmap=null;
            }else
                bitmap=BitmapFactory.decodeFile(list.get(j).getAvatarFile().getPath());
            personList.add(
                    new personMsg(
                            list.get(j).getNoDisturb(),
                            list.get(j).getNickname()
                            , list.get(j).getUserID()
                            , bitmap
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
                            , list.get(j).getBirthday()
                            , list.get(j).getAvatarFile().getPath()
                    ));
            personAdapter.notifyDataSetChanged();
        }
    }
    public static void initGrouplist(){
        groupList.clear();
        for (int j=0;j<list2.size();j++){
            Bitmap bitmap;
            if (TextUtils.isEmpty(list2.get(j).getAvatar())){
                bitmap=null;
            }else
                bitmap=BitmapFactory.decodeFile(list2.get(j).getAvatarFile().getPath());
            groupList.add(new personMsg(list2.get(j).getGroupID(),
                    list2.get(j).getGroupName(),
                    list2.get(j).getGroupOwner(),
                    list2.get(j).getNoDisturb(),
                    list2.get(j).getOwnerAppkey(),
                    list2.get(j).getMaxMemberCount(),
                    list2.get(j).getGroupDescription(),
                    bitmap
                    ,list2.get(j).getGroupType().toString()));
            groupAdapter.notifyDataSetChanged();
        }

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
    @SuppressLint("SimpleDateFormat")
    public static String secondToDate(long millisecond, String patten) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millisecond);
        Date date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat(patten);
        String dateString = format.format(date);
        return dateString;
    }

    /**
     * 返回日时分秒
     */
    public static String secondToTime(long millisecond) {
        millisecond = millisecond ;
        long days = millisecond / 86400;//转换天数
        millisecond = millisecond % 86400;//剩余秒数
        long hours = millisecond / 3600;//转换小时数
        millisecond = millisecond % 3600;//剩余秒数
        long minutes = millisecond / 60;//转换分钟
        millisecond = millisecond % 60;//剩余秒数
        if (0 < days) {
            return days + "天，" + hours + "小时，" + minutes + "分，" + millisecond + "秒";
        } else {
            return hours + "小时，" + minutes + "分，" + millisecond + "秒";
        }
    }

    /**
     * 由毫秒数转换年数
     */
    public static String Age(long millisecond) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        int year = calendar.get(Calendar.YEAR);
        calendar.setTimeInMillis(millisecond);//转换为毫秒
        Date date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy");
        int year2 = Integer.parseInt(format.format(date));
        year2 = year - year2;
        return year2 + "";
    }

    public static int getPosition(String userName, String appKey) {
        int position = 0;
        for (int i = 0; i < personList.size(); i++) {
            String t = personList.get(i).getUserName() + personList.get(i).getAppkey();
            String s = userName + appKey;
            if (t.equals(s)) {
                position = i;
                break;
            }
        }
        return position;
    }
/************获取状态权限和状态信息*****************************/
    public static  boolean getE_permission(Context context){
        AppOpsManager appOpt = (AppOpsManager) context
                .getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOpt.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),context.getPackageName());
        return  mode == AppOpsManager.MODE_ALLOWED;
    }
    public static String getE(){
        String s1 = null;
        Calendar beginCal = Calendar.getInstance();
        beginCal.add(Calendar.HOUR_OF_DAY, -1);
        Calendar endCal = Calendar.getInstance();
        UsageStatsManager manager=(UsageStatsManager)getApplicationContext().getSystemService(USAGE_STATS_SERVICE);
        List<UsageStats> stats=manager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,beginCal.getTimeInMillis(),endCal.getTimeInMillis());
        StringBuilder sb=new StringBuilder();
        for(UsageStats us:stats){
            try {
                PackageManager pm = getApplicationContext().getPackageManager();
                ApplicationInfo applicationInfo = pm.getApplicationInfo(us.getPackageName(), PackageManager.GET_META_DATA);
                if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                    @SuppressLint("SimpleDateFormat")
                    SimpleDateFormat format = new SimpleDateFormat("MM/dd HH:mm:ss");
                    String t = format.format(new Date(us.getLastTimeUsed()));

                    if (us.getTotalTimeInForeground() != 0) {
                        if (us.getTotalTimeInForeground() / 1000 / 60 >=180) {
                            sb.append("AN:   ").append(pm.getApplicationLabel(applicationInfo)).append("\n").append("LT:   ").append(t).append("\n").append("RT:   ").append(us.getTotalTimeInForeground() / 1000 / 60).append("min").append(us.getTotalTimeInForeground() / 1000 % 60).append("s     ⚠\n").append("\n");
                        } else {
                            sb.append("AN:   ").append(pm.getApplicationLabel(applicationInfo)).append("\n").append("LT:   ").append(t).append("\n").append("RT:   ").append(us.getTotalTimeInForeground() / 1000 / 60).append("min").append(us.getTotalTimeInForeground() / 1000 % 60).append("s\n\n");
                        }
                        s1=sb.toString();
                    }

                }
            }catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

        }
        return s1;
    }
}
