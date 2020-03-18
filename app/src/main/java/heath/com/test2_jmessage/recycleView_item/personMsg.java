package heath.com.test2_jmessage.recycleView_item;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import heath.com.test2_jmessage.R;
import heath.com.test2_jmessage.application.MyApplication;
import heath.com.test2_jmessage.tools.tools;

public class personMsg {

    private String name;
    private String address;
    private long birthday;
    private String nickname;
    private String gender;
    private String region;
    private String signature;
    private String notename;
    private boolean hasInformation = false;
    private String displayname, other;
    private String simpleMessage;
    private String appkey;
    private String time;
    private long userId, brithday;
    private Bitmap avatar;
    private String username;
    private String unique;
    private int isDistubed;
    private int messageId;
    private String avatarLocalpath;
    private long millisecond;
    private int MsgNumber;
    /********************************************************************************************/
    private long groupId;
    private int noDisturb, maxMemberCount;
    private String groupName, groupOwner,
            groupOwnerAppkey, groupDescription,
            groupType, avatarFilePath;
    public int getNoDisturb(){return noDisturb;}
    public int getMaxMemberCount(){return maxMemberCount;}
    public String getGroupName(){return groupName;}
    public String getGroupOwner(){return groupOwner;}
    public String getGroupOwnerAppkey(){return groupOwnerAppkey;}
    public String getGroupDescription(){return groupDescription;}
    public String getGroupType(){return groupType;}
    public String getAvatarFilePath(){return avatarFilePath;}
    public long getGroupId(){return groupId;}

    public personMsg(String username,Bitmap avatar){
        this.username=username;
        this.avatar=avatar;
    };

    public personMsg(long groupId, String groupName, String groupOwner,
    int noDisturb,String groupOwnerAppkey, int maxMemberCount,
                     String groupDescription,Bitmap avatar,String groupType) {
        this.groupId =groupId;
        this.groupName = groupName;
        this.groupOwner = groupOwner;
        this.noDisturb = noDisturb;
        this.groupOwnerAppkey = groupOwnerAppkey;
        this.maxMemberCount = maxMemberCount;
        this.groupDescription = groupDescription;
        this.avatar = avatar;
        this.groupType =groupType;
    }

    /********************************************************************************************/
    public personMsg(String avatarLocalpath,
                     String username,
                     String simpleMessage,
                     int messageId,
                     String appkey,
                     String nickname,
                     String notename,
                     long millisecond,
                     int MsgNumber) {
        this.MsgNumber = MsgNumber;
        this.avatar = BitmapFactory.decodeFile(avatarLocalpath);
        this.username = username;
        this.simpleMessage = simpleMessage;
        this.avatarLocalpath = avatarLocalpath;
        this.messageId = messageId;
        this.appkey = appkey;
        this.nickname = nickname;
        this.notename = notename;
        this.millisecond = millisecond;
    }

    public int getMsgNumber() {
        return MsgNumber;
    }

    public long getCreateMillisecond() {
        return millisecond;
    }

    public int getMessageId() {
        return messageId;
    }

    public String getAvatarLocalpath() {
        return avatarLocalpath;
    }

    public personMsg(Bitmap avatar, String userName, String other,
                     String appkey, String time, String simpleMessage, String unique) {
        this.avatar = avatar;
        this.username = userName;
        this.other = other;
        this.appkey = appkey;
        this.time = time;
        this.simpleMessage = simpleMessage;
        this.unique = unique;
    }

    public personMsg(int isDistubed, String nickname, long userId, Bitmap avatar, String userName,
                     String noteName, String appkey,
                     boolean hasInformation, String simpleMessage, String time,
                     String signature,
                     String gender, String adress, String other, long birthday,
                     String avatarLocalpath) {
        this.avatarLocalpath = avatarLocalpath;
        this.isDistubed = isDistubed;
        this.username = userName;
        this.signature = signature;
        this.notename = noteName;
        this.appkey = appkey;
        this.hasInformation = hasInformation;
        this.other = other;
        this.simpleMessage = simpleMessage;
        this.time = time;
        this.avatar = avatar;
        this.userId = userId;
        this.gender = gender;
        this.address = adress;
        this.birthday = birthday;
        this.nickname = nickname;
    }

    public void setIsDistubed(int isDistubed) {

        this.isDistubed = isDistubed;
    }

    public boolean getIsDistubed() {
        if (this.isDistubed == 1)
            return true;
        else
            return false;
    }

    public String getUnique() {
        return unique;
    }

    public String getOther() {
        return other;
    }

    public String getAddress() {
        return address;
    }

    public String getUserName() {
        return username;
    }

    public String getNotename() {
        return notename;
    }

    public String getGender() {
        if (gender.equals("female"))
            return "女";
        else if (gender.equals("male"))
            return "男";
        else
            return null;
    }

    public String getSignature() {
        return signature;
    }

    public long getMillisecond() {
        if (birthday < 0)
            return 0;
        else
            return birthday;
    }

    public String getName() {
        if (!TextUtils.isEmpty(notename)) {
            return notename;
        } else {
            if (!TextUtils.isEmpty(nickname)) {
                return nickname;
            } else {
                return username;
            }
        }
    }

    public String getBirthday() {
        return tools.secondToDate(birthday, "yyyy-MM-dd");
    }

    public String getAppkey() {
        return appkey;
    }

    public String getSimpleMessage() {
        return simpleMessage;
    }

    public String getTime() {
        return time;
    }

    public long getUserId() {
        return userId;
    }

    public Bitmap getAvatar() {
        if (this.avatar == null)
            return BitmapFactory.decodeResource(MyApplication.getContext().getResources(), R.drawable.icon_right_default);
        else
            return avatar;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNotename(String notename) {
        this.notename = notename;
    }

    public boolean getHasInformation() {
        return hasInformation;
    }

    public void setHasInformation(boolean hasInformation) {
        this.hasInformation = hasInformation;
    }

    public void setAppkey(String Appkey) {
        this.appkey = Appkey;
    }

    public void setSimpleMessage(String Simplemessage) {
        this.simpleMessage = Simplemessage;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setAvatar(Bitmap bitmap) {
        if (bitmap == null)
            this.avatar = BitmapFactory.decodeResource(MyApplication.getContext().getResources(), R.drawable.icon_right_default);
        else
            this.avatar = bitmap;
    }

    public String getNickname() {
        return this.nickname;
    }

}
