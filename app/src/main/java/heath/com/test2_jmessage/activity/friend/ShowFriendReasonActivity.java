package heath.com.test2_jmessage.activity.friend;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import cn.jpush.im.android.api.ContactManager;
import cn.jpush.im.android.api.callback.GetUserInfoListCallback;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import heath.com.test2_jmessage.R;
import heath.com.test2_jmessage.StatusBar.StatusBarUtil;
import heath.com.test2_jmessage.activity.RegisterAndLoginActivity;
import heath.com.test2_jmessage.activity.TypeActivity;
import heath.com.test2_jmessage.recycleView_item.personMsg;

import static heath.com.test2_jmessage.activity.TypeActivity.myUserId;
import static heath.com.test2_jmessage.activity.friend.FriendAskManage.personAskAdapter;
import static heath.com.test2_jmessage.activity.friend.FriendAskManage.personAskList;

/**
 * Created by ${chenyn} on 16/4/17.
 *
 * @desc :同意或拒绝好友申请
 */
public class ShowFriendReasonActivity extends Activity {
    private static final String TAG = ShowFriendReasonActivity.class.getSimpleName();
    public static final String EXTRA_TYPE = "event_type";
    private Button mAccept_invitation;
    private Button mDeclined_invitation;
    private EditText mEt_reason;
    private int position;
    private String userName,appKey,reason,unique;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initData();
    }

    private void initData() {
        unique=getIntent().getStringExtra("unique");
        position=getIntent().getIntExtra("position",-1);
        userName=getIntent().getStringExtra("username");
        appKey=getIntent().getStringExtra("appkey");
        reason=getIntent().getStringExtra("other");
        final SharedPreferences pref=getSharedPreferences("friends"+myUserId,0);
        String simplemessage=pref.getString("simplemessage"+unique,null);
        Log.d("simplemessage", simplemessage);
        if (simplemessage.equals("已同意")||simplemessage.equals("已拒绝"))
        {
            mAccept_invitation.setEnabled(false);
            mEt_reason.setEnabled(false);
            mDeclined_invitation.setEnabled(false);
            mAccept_invitation.setBackgroundResource(R.drawable.background_addnew_declined);

        }
        final SharedPreferences.Editor editor=getApplicationContext().
                getSharedPreferences("friends"+myUserId,0).edit();
        TextView showFriend_toolbarName=findViewById(R.id.showFriend_toolbarName);
        showFriend_toolbarName.setText("新朋友");
        TextView showFriend_back=findViewById(R.id.showFriend_back);
        showFriend_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), FriendAskManage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }
        });
        TextView showFriend_toolbarAdd=findViewById(R.id.showFriend_toolbarAdd);
        showFriend_toolbarAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(getApplicationContext(), AddFriendActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
            }
        });
        TextView showFriend_person=findViewById(R.id.showFriend_person);
        TextView tiptext2=findViewById(R.id.tiptext2);
        TextView Appkeytext2=findViewById(R.id.Appkeytext2);

        showFriend_person.setText(userName);
        tiptext2.setText(reason);
        Appkeytext2.setText(appKey);
        //同意好友添加邀请
        mAccept_invitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                ContactManager.acceptInvitation(userName, appKey, new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {
                        if (i == 0) {
                            editor.putString("simplemessage"+unique,"已同意");
                            editor.putString("time"+unique,timeA);
                            editor.apply();
                            personAskList.get(position).setSimpleMessage("已同意");
                            personAskList.get(position).setTime(timeA);
                            personAskAdapter.notifyDataSetChanged();
                            ContactManager.getFriendList(new GetUserInfoListCallback() {
                                @Override
                                public void gotResult(int i, String s, List<UserInfo> list) {
                                    if (i == 0) {
                                        String s1,s2;
                                        for (int j=0;j<list.size();j++) {
                                            s1=list.get(j).getUserName()+list.get(j).getAppKey();
                                            s2=userName+appKey;
                                                if (s1.equals(s2)){
                                                    TypeActivity.personList.add(new personMsg(
                                                            list.get(j).getNickname()
                                                            ,list.get(j).getUserID()
                                                            , null,list.get(j).getUserName()
                                                            ,list.get(j).getNotename()
                                                            ,list.get(i).getAppKey()
                                                            ,null
                                                            ,list.get(j).getSignature()
                                                            , timeA
                                                            , list.get(j).getSignature()
                                                            , list.get(j).getGender().toString()
                                                            ,list.get(j).getAddress()
                                                            ,list.get(j).getNoteText()
                                                            ,list.get(j).getBirthday()));
                                                    TypeActivity.adapter.notifyItemChanged(TypeActivity.personList.size()-1);
                                                    TypeActivity.personIcon.add(BitmapFactory.decodeFile(list.get(j).getAvatarFile().getPath()));
                                                    break;
                                                }
                                        }
                                        if (list.size() == 0) {
                                            Toast.makeText(getApplicationContext(), "暂无好友", Toast.LENGTH_SHORT).show();
                                        }
                                        Toast.makeText(getApplicationContext(), "获取成功", Toast.LENGTH_SHORT).show();
                                    } else if (i!=0){
                                        Toast.makeText(getApplicationContext(), "获取失败", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "添加失败", Toast.LENGTH_SHORT).show();
                            Log.i(TAG, "ContactManager.acceptInvitation" + ", responseCode = " + i + " ; LoginDesc = " + s);
                        }
                    }
                });
            }
        });
        //拒绝好友添加邀请
        mDeclined_invitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                String reason = mEt_reason.getText().toString();
                ContactManager.declineInvitation(userName, appKey, reason, new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {
                        if (i == 0) {
                            editor.putString("time"+ unique,timeA);
                            editor.putString("simplemessage"+unique,"已拒绝");
                            editor.apply();
                            personAskList.get(position).setSimpleMessage("已拒绝");
                            personAskList.get(position).setTime(timeA);
                            personAskAdapter.notifyDataSetChanged();
                            Toast.makeText(getApplicationContext(), "拒绝成功", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "拒绝失败", Toast.LENGTH_SHORT).show();
                            Log.i(TAG, "ContactManager.declineInvitation" + ", responseCode = " + i + " ; LoginDesc = " + s);
                        }
                    }
                });
            }
        });
    }
    private void initView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_show_friend_reason);
        zoomInViewSize(StatusBarUtil.getStatusBarHeight(this));
        //StatusBarUtil.setStatusBarColor(this, Color.parseColor("#00C4FF"));
        mAccept_invitation = (Button) findViewById(R.id.accept_invitation);
        mDeclined_invitation = (Button) findViewById(R.id.declined_invitation);
        mEt_reason = (EditText) findViewById(R.id.et_reason);
        LinearLayout linearLayout=findViewById(R.id.showFriend_mainLayout);
        RegisterAndLoginActivity.addLayoutListener(linearLayout,mDeclined_invitation);

    }
    private void zoomInViewSize(int height) {
        View img1 = findViewById(R.id.statusbar);
        ViewGroup.LayoutParams  lp = img1.getLayoutParams();
        lp.height =height;
        img1.setLayoutParams(lp);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
