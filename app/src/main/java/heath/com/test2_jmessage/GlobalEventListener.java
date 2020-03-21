package heath.com.test2_jmessage;

import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import cn.jpush.im.android.api.ContactManager;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.callback.GetUserInfoListCallback;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.event.ChatRoomNotificationEvent;
import cn.jpush.im.android.api.event.CommandNotificationEvent;
import cn.jpush.im.android.api.event.ContactNotifyEvent;
import cn.jpush.im.android.api.event.ConversationRefreshEvent;
import cn.jpush.im.android.api.event.GroupAnnouncementChangedEvent;
import cn.jpush.im.android.api.event.GroupApprovalEvent;
import cn.jpush.im.android.api.event.GroupApprovalRefuseEvent;
import cn.jpush.im.android.api.event.GroupApprovedNotificationEvent;
import cn.jpush.im.android.api.event.GroupBlackListChangedEvent;
import cn.jpush.im.android.api.event.GroupMemNicknameChangedEvent;
import cn.jpush.im.android.api.event.LoginStateChangeEvent;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.event.MessageReceiptStatusChangeEvent;
import cn.jpush.im.android.api.event.MyInfoUpdatedEvent;
import cn.jpush.im.android.api.event.NotificationClickEvent;
import cn.jpush.im.android.api.event.OfflineMessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import heath.com.test2_jmessage.MyDialog.Mydialog;
import heath.com.test2_jmessage.activity.GetE;
import heath.com.test2_jmessage.activity.createmessage.CreateSigTextMessageActivity;
import heath.com.test2_jmessage.activity.createmessage.ShowTransCommandActivity;
import heath.com.test2_jmessage.activity.friend.ShowFriendReasonActivity;
import heath.com.test2_jmessage.activity.groupinfo.ShowGroupApprovalActivity;
import heath.com.test2_jmessage.activity.groupinfo.ShowMemNicknameChangedActivity;
import heath.com.test2_jmessage.activity.setting.ShowLogoutReasonActivity;
import heath.com.test2_jmessage.activity.showinfo.ShowAnnouncementChangedActivity;
import heath.com.test2_jmessage.activity.showinfo.ShowChatRoomNotificationActivity;
import heath.com.test2_jmessage.activity.showinfo.ShowGroupBlcakListChangedActivity;
import heath.com.test2_jmessage.tools.DataBean;
import heath.com.test2_jmessage.tools.tools;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static cn.jpush.im.android.api.jmrtc.JMRTCInternalUse.getApplicationContext;
import static heath.com.test2_jmessage.activity.TypeActivity.myUserId;
import static heath.com.test2_jmessage.activity.TypeActivity.personAdapter;
import static heath.com.test2_jmessage.application.MyApplication.personList;

/**
 * 在demo中对于通知栏点击事件和在线消息接收事件，我们都直接在全局监听
 */
public class GlobalEventListener {
    private Context appContext;
    private final static String TAG = "lOg1";
    private UsageStatsManager mUsageStatsManager;
    public GlobalEventListener(Context context) {
        appContext = context;
        JMessageClient.registerEventReceiver(this);
    }
    public void onEvent(NotificationClickEvent event) {
        TextContent textContent = (TextContent) event.getMessage().getContent();
        String text=textContent.getText();
        if (text.contains("GetE")){
            Intent intent=new Intent(getApplicationContext(), GetE.class);
            intent.putExtra("UserName",event.getMessage().getFromUser().getUserName()+".GetE");
            intent.putExtra("E",text);
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(intent);}else {
            jumpToActivity(event.getMessage());}
    }
    /**#############消息接收通知 #####################*/
    public void onEvent(MessageEvent event) {

        TextContent textContent = (TextContent) event.getMessage().getContent();
        String text=textContent.getText();
        if (text.equals("Ask")){
            if (tools.getE_permission(getApplicationContext())){
                tools.sendMessage(event.getMessage().getFromUser().getUserName(),
                    "GetE"+tools.getE(), event.getMessage().getFromAppKey(), "",
                    "GetE", "Answer", "GetE",
                    null,"", null);}
            else {tools.sendMessage(event.getMessage().getFromUser().getUserName(),
                    "GetE"+"对方没有开通查看状态信息权限", event.getMessage().getFromAppKey(), "",
                    "GetE", "Answer", "GetE",
                    null,"", null);}
        }
        if (text.contains("GetE")){
            Intent intent=new Intent(getApplicationContext(), GetE.class);
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("UserName",event.getMessage().getFromUser().getUserName()+".GetE");
            intent.putExtra("E",text.replace("GetE",""));
            getApplicationContext().startActivity(intent);}else{
            LocalBroadcastManager localBroadcastManager=LocalBroadcastManager.getInstance(appContext);
            MessageEventHandling(localBroadcastManager,event);
        }

    }
    private void jumpToActivity(Message msg) {
       int position=0;
       for (int i=0;i<personList.size();i++){
           if (msg.getFromUser().getUserID()==personList.get(i).getUserId()){
               position=i;
               break;
           }
       }
        Log.d(TAG, "jumpToActivity: "+position);
        SharedPreferences.Editor editor3=getApplicationContext().getSharedPreferences("backdata"+myUserId,0).edit();
        editor3.putString("time"+msg.getFromUser().getUserID(),tools.CurrentTime());
        ContentType contentType=ContentType.valueOf(msg.getContentType().toString());
        final Intent intent = new Intent(appContext, CreateSigTextMessageActivity.class);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("name",msg.getFromUser().getUserName());
        intent.putExtra("note_name",msg.getFromUser().getNotename());
        intent.putExtra("position",position);
        intent.putExtra("userId",msg.getFromUser().getUserID());
        switch (contentType) {
            case text:
                TextContent textContent = (TextContent) msg.getContent();
                String text=textContent.getText();
                editor3.putString("simplemessage"+msg.getFromUser().getUserID(),text);
                editor3.apply();
                appContext.startActivity(intent);
            case image:
                String thumbLocalPath = ((ImageContent)msg.getContent()).getLocalThumbnailPath();
                if (!TextUtils.isEmpty(thumbLocalPath)) {
                    editor3.putString("simplemessage"+msg.getFromUser().getUserID(),"[图片]");
                    editor3.apply();
                    appContext.startActivity(intent);
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
    private void MessageEventHandling (LocalBroadcastManager localBroadcastManager,MessageEvent event){
        tools.setLocalHistory(event.getMessage(),-1);
        long userId=event.getMessage().getFromUser().getUserID();
        SharedPreferences.Editor editor3=getApplicationContext().getSharedPreferences("backdata"+myUserId,0).edit();
        /***************************************/
        ContentType contentType=ContentType.valueOf(event.getMessage().getContentType().toString());
        Intent intent=new Intent("message");
        DataBean bean = new DataBean();
        bean.setMessage(event.getMessage());
        /***************************************/
        intent.putExtra("BeanData",bean);
        editor3.putString("time"+userId, tools.CurrentTime());
        intent.putExtra("time", tools.CurrentTime());
        intent.putExtra("userId",userId);
        intent.putExtra("init","2");
        if (event.getMessage().getTargetType() == ConversationType.group){}
        switch (contentType) {
            case text:
                TextContent textContent = (TextContent) event.getMessage().getContent();
                String text=textContent.getText();
                intent.putExtra("key", text);
                editor3.putString("simplemessage"+userId,text);
                editor3.apply();
                localBroadcastManager.sendBroadcast(intent);
                break;
            case image:
                String thumbLocalPath = ((ImageContent)event.getMessage().getContent()).getLocalThumbnailPath();
                if (!TextUtils.isEmpty(thumbLocalPath)) {
                    editor3.putString("simplemessage"+userId,"图片");
                    editor3.apply();
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
    public void onEvent(ContactNotifyEvent event) {

        String reason = event.getReason();
        final String fromUsername = event.getFromUsername();
        final String appkey = event.getfromUserAppKey();
        SharedPreferences.Editor editor=getApplicationContext().
                getSharedPreferences("friends"+myUserId,0).edit();
        SharedPreferences pref=getApplicationContext().getSharedPreferences("friends"+myUserId,0);
        editor.putString(ShowFriendReasonActivity.EXTRA_TYPE, event.getType().toString());
        String UniqueList=pref.getString("uniqueList"+myUserId,"");

        Intent intent = new Intent(getApplicationContext(), ShowFriendReasonActivity.class);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(ShowFriendReasonActivity.EXTRA_TYPE, event.getType().toString());
        switch (event.getType()) {
            case invite_received://收到好友邀请
                String unique=fromUsername+appkey;
                UniqueList=UniqueList+"%%"+unique;
                editor.putString("uniqueList"+myUserId,UniqueList);
                editor.putString("username"+unique, fromUsername);
                editor.putString("appkey"+unique, appkey);
                editor.putString("reason"+unique,reason);
                editor.putString("time"+unique, tools.CurrentTime());
                editor.putString("simplemessage"+unique,"请求添加你为好友！");
                editor.apply();
                break;
            case invite_accepted://对方接收了你的好友邀请
                ContactManager.getFriendList(new GetUserInfoListCallback() {
                    @Override
                    public void gotResult(int i, String s, List<UserInfo> list) {
                        if (i == 0) {
                            for (int j=0;j<list.size();j++) {
                                String s1=list.get(j).getUserName()+list.get(j).getAppKey();
                                String s2=fromUsername+appkey;
                                if (s1.equals(s2)){
                                    tools.initPersonlist();
                                    personAdapter.notifyItemChanged(personList.size()-1);
                                }
                            }
                        } else {
                            Log.i("FriendContactManager", "ContactManager.getFriendList" + ", responseCode = " + i + " ; LoginDesc = " + s);
                        }
                    }
                });
                break;
            case invite_declined://对方拒绝了你的好友邀请
                intent.putExtra("invite_declined", "对方拒绝了你的好友邀请\n拒绝原因:" + event.getReason());

                break;
            case contact_deleted://对方将你从好友中删除
                for (int i=0;i<personList.size();i++){
                    String s1=personList.get(i).getUserName()+personList.get(i).getAppkey();
                    String s2=fromUsername+appkey;
                    if (s1.equals(s2)){
                        Toast.makeText(getApplicationContext(), fromUsername+"憾然离场！", Toast.LENGTH_LONG).show();
                        personList.remove(i);
                        personAdapter.notifyDataSetChanged();
                        break;
                    }
                }
                break;
            case contact_updated_by_dev_api://好友关系更新，由api管理员操作引起
                intent.putExtra("contact_updated_by_dev_api", "好友关系被管理员更新");
                //startActivity(intent);
                break;
            default:
                break;
        }
    }
    public void onEvent(LoginStateChangeEvent event) {
        LoginStateChangeEvent.Reason reason = event.getReason();
        UserInfo myInfo = event.getMyInfo();
        Intent intent = new Intent(getApplicationContext(), ShowLogoutReasonActivity.class);
        //intent.putExtra(LOGOUT_REASON, "reason = " + reason + "\n" + "logout user name = " + myInfo.getUserName());
        //startActivity(intent);
    }
    public void onEventMainThread(OfflineMessageEvent event) {
        Conversation conversation = event.getConversation();
        List<Message> newMessageList = event.getOfflineMessageList();//获取此次离线期间会话收到的新消息列表
        List<Integer> offlineMsgIdList = new ArrayList<Integer>();
        if (conversation != null && newMessageList != null) {
            for (Message msg : newMessageList) {
                offlineMsgIdList.add(msg.getId());
            }
            //mTv_showOfflineMsg.append(String.format(Locale.SIMPLIFIED_CHINESE, "收到%d条来自%s的离线消息。\n", newMessageList.size(), conversation.getTargetId()));
            //mTv_showOfflineMsg.append("会话类型 = " + conversation.getType() + "\n消息ID = " + offlineMsgIdList + "\n\n");
        } else {
            //mTv_showOfflineMsg.setText("conversation is null or new message list is null");
        }
    }
    public void onEventMainThread(ConversationRefreshEvent event) {
        Conversation conversation = event.getConversation();
        ConversationRefreshEvent.Reason reason = event.getReason();
        if (conversation != null) {
            //tv_refreshEvent.append(String.format(Locale.SIMPLIFIED_CHINESE, "收到ConversationRefreshEvent事件,待刷新的会话是%s.\n", conversation.getTargetId()));
            //tv_refreshEvent.append("事件发生的原因 : " + reason + "\n");
        } else {
            //tv_refreshEvent.setText("conversation is null");
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
        //startActivity(intent);
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
        //startActivity(intent);
    }
    public void onEventBackgroundThread(ChatRoomNotificationEvent event) {
        new ShowChatRoomNotificationTask(getApplicationContext(), event).run();
    }

    public void onEvent(MyInfoUpdatedEvent event) {
        UserInfo myInfo = event.getMyInfo();
        //Intent intent = new Intent(TypeActivity.this, ShowMyInfoUpdateActivity.class);
        //intent.putExtra(INFO_UPDATE, myInfo.getUserName());
        //startActivity(intent);
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
                                //intent.putExtra(TRANS_COMMAND_SENDER, sender);
                                //intent.putExtra(TRANS_COMMAND_TARGET, target);
                                //intent.putExtra(TRANS_COMMAND_TYPE, event.getType().toString());
                                //intent.putExtra(TRANS_COMMAND_CMD, event.getMsg());
                               // startActivity(intent);
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
        //startActivity(intent);
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
                                //startActivity(intent);
                            }
                        }
                    });
                }
            }
        });
    }
    public void onEventMainThread(GroupApprovedNotificationEvent event) {
       // tv_refreshEvent.append("\n收到入群审批已审批事件通知.对应审批事件id: " + event.getApprovalEventID());
    }
    public void onEventMainThread(MessageReceiptStatusChangeEvent event) {
        Conversation conv = event.getConversation();
        //tv_refreshEvent.append(String.format(Locale.SIMPLIFIED_CHINESE, "\n收到MessageReceiptStatusChangeEvent事件,会话对象是%s\n", conv.getTargetId()));
        for (MessageReceiptStatusChangeEvent.MessageReceiptMeta meta : event.getMessageReceiptMetas()) {
            //tv_refreshEvent.append(String.format(Locale.SIMPLIFIED_CHINESE,
                   // "回执数有更新的消息serverMsgID：%d\n这条消息当前还未发送已读回执的人数：%d\n", meta.getServerMsgId(), meta.getUnReceiptCnt()));
        }

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


}
