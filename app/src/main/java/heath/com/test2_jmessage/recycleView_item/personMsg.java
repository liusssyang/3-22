package heath.com.test2_jmessage.recycleView_item;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import cn.jpush.im.android.api.model.UserInfo;
import heath.com.test2_jmessage.R;
import heath.com.test2_jmessage.application.IMDebugApplication;

public class personMsg {

    private String name;
    private String address;
    private long birthday;
    private String nickname;
    private String gender;
    private String region;
    private String signature;
    private String notename;
    private String sendName,displayname,other;
    private String simpleMessage;
    private String appkey;
    private String time;
    private long userId,brithday;
    private Bitmap bitmap;

    public void saveFriend(String simpleMessage,String time){

    }

    public personMsg(List<UserInfo> list,int j){
        /*this.address=list.get(j).getAddress();
        this.birthday=list.get(j).getBirthday()+"";
        this.displayname=list.get(j).getDisplayName();
        this.notename=list.get(j).getNotename();
        this.gender= String.valueOf(list.get(j).getGender());
        this.region=list.get(j).getRegion();
        this.signature=list.get(j).getSignature();
        this.nickname=list.get(j).getNickname();
        this.simpleMessage=list.get(j).getExtra("simpleMessage");
        this.time=list.get(j).getExtra("time");*/
        this.name=list.get(j).getUserName();
        this.appkey=list.get(j).getAppKey();

        this.bitmap=BitmapFactory.decodeFile(list.get(j).getBigAvatarFile().getPath());
        this.userId=list.get(j).getUserID();
    }
    public personMsg(long userID, Bitmap icon, String userName, String notename, String appKey, String o, String name, String appkey, String sendName, UserInfo.Gender gender){
        this.name=name;
        this.appkey=appkey;
        this.sendName=sendName;
    }
    public personMsg(long userId,Bitmap icon,String userName,String noteName,String appkey,
                     String sendName,String simpleMessage,String time,String signature,
                     String gender,String adress,String other,long birthday){
        this.name=userName;
        this.signature=signature;
        this.notename=noteName;
        this.appkey=appkey;
        this.sendName=sendName;
        this.other=other;
        this.simpleMessage=simpleMessage;
        this.time=time;
        this.bitmap=icon;
        this.userId=userId;
        this.gender=gender;
        this.address=adress;
        this.birthday=birthday;
    }
    public String getOther(){return  other;}
    public String getAddress(){return address;}
    public String getUserName(){
        return name;
    }
    public String getGender(){
        if (gender.equals("unknown"))
            return null;
        else
            return gender;}
    public String getSignature(){
        return  signature;
    }
    public String getName(){
        if (notename.equals("")){
            return name;}
        else{
            Log.d("notename", "getName: "+notename);
            return notename;
        }
    }
    public String getBirthday(){
        Calendar cal;
        int year;
        cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        Date d = new Date(this.birthday);

        year = cal.get(Calendar.YEAR);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        year=year-Integer.parseInt(new SimpleDateFormat("yyyy").format(d));
        //String s=new java.util.Date(this.birthday).toString();
        return sdf.format(d)+"  "+year+"岁";}
    public String getSendName(){return sendName;}
    public String getAppkey(){return appkey;}
    public String getSimpleMessage(){return simpleMessage;}
    public String getTime(){return time;}
    public long getUserId(){return userId;}
    public Bitmap getBitmap(){
        if (this.bitmap==null)
            return BitmapFactory.decodeResource(IMDebugApplication.getContext().getResources(), R.drawable.icon_right_default);
        else
            return  bitmap;
    }
    public void setName(String name){this.name=name;}
    public void setNotename(String notename){this.notename=notename;}
    public void setSendName(String Sendname){this.sendName=Sendname;}
    public void setAppkey(String Appkey){this.appkey=Appkey;}
    public void setSimpleMessage(String Simplemessage){this.simpleMessage=Simplemessage;}
    public void setTime(String time){this.time=time;}
    public void setBitmap(Bitmap bitmap){
        if (bitmap==null)
            this.bitmap=BitmapFactory.decodeResource(IMDebugApplication.getContext().getResources(), R.drawable.icon_right_default);
        else
            this.bitmap=bitmap;
    }
    public void setUserId(long userId){
        this.userId=userId;
    }

}
