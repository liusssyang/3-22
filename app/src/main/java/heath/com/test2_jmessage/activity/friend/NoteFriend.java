package heath.com.test2_jmessage.activity.friend;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.jpush.im.android.api.ContactManager;
import cn.jpush.im.android.api.callback.GetUserInfoListCallback;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import heath.com.test2_jmessage.R;
import heath.com.test2_jmessage.StatusBar.StatusBarUtil;
import heath.com.test2_jmessage.tools.PushToast;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static heath.com.test2_jmessage.activity.TypeActivity.adapter;
import static heath.com.test2_jmessage.application.MyApplication.personList;


public class NoteFriend extends Activity {
    private  int position;
    private long userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_note_friends);
        PushToast.getInstance().init(this);
        position=getIntent().getIntExtra("position",-1);
        userId=getIntent().getLongExtra("userId",0);
        zoomInViewSize(StatusBarUtil.getStatusBarHeight(this));
        TextView manage_back=findViewById(R.id.manage_back);
        manage_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),ManageFriendActivity.class);
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("position",position);
                intent.putExtra("userId",userId);
                getApplicationContext().startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
            }
        });
        final EditText note_name=findViewById(R.id.note_name);
        if (position>=0){
            note_name.setText(personList.get(position).getName());
            note_name.setSelection(personList.get(position).getName().length());
        }
        final EditText note_text=findViewById(R.id.note_text);
        Button note_complete=findViewById(R.id.note_complete);
        note_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String noteName=note_name.getText().toString();
                final String noteText=note_text.getText().toString();
                ContactManager.getFriendList(
                        new GetUserInfoListCallback() {
                            @Override
                            public void gotResult(int i, String s, List<UserInfo> list) {
                                if (i==0){
                                if (list.size() == 0) {
                                    Toast.makeText(getApplicationContext(), "没有好友不能更新", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                for (int j=0;j<list.size();j++) {
                                    if (list.get(j).getUserID()==userId){
                                        final UserInfo info=list.get(j);
                                        if (TextUtils.isEmpty(noteName) && TextUtils.isEmpty(noteText)) {

                                            PushToast.getInstance().createToast("提示","请输入相关参数",null,false);
                                        }
                                        if (!TextUtils.isEmpty(noteName)) {
                                            info.updateNoteName(noteName, new BasicCallback() {
                                                @Override
                                                public void gotResult(int i, String s) {
                                                    if (i == 0) {
                                                        PushToast.getInstance().createToast("提示","备注更新成功",null,true);
                                                        personList.get(position).setNotename(noteName);
                                                        adapter.notifyDataSetChanged();
                                                        Intent intent=new Intent(getApplicationContext(),ManageFriendActivity.class);
                                                        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                                                        intent.putExtra("position",position);
                                                        intent.putExtra("userId",userId);
                                                        getApplicationContext().startActivity(intent);
                                                        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                                                    } else {
                                                        PushToast.getInstance().createToast("提示","备注更新失败",null,false);
                                                    }
                                                }
                                            });
                                        }
                                        if (!TextUtils.isEmpty(noteText)) {
                                            info.updateNoteText(noteText, new BasicCallback() {
                                                @Override
                                                public void gotResult(int i, String s) {
                                                    if (i == 0) {
                                                        Intent intent=new Intent(getApplicationContext(),ManageFriendActivity.class);
                                                        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                                                        getApplicationContext().startActivity(intent);
                                                        Toast.makeText(getApplicationContext(), "更新 note text 成功", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(getApplicationContext(), "更新失败", Toast.LENGTH_SHORT).show();
                                                        Log.i("FriendContactManager", "UserInfo.updateNoteText" + ", responseCode = " + i + " ; Desc = " + s);
                                                    }
                                                }
                                            });
                                        }
                                        break;
                                    }
                                }}
                                else
                                    Toast.makeText(getApplicationContext(), "获取失败", Toast.LENGTH_SHORT).show();
                            }

                        }
                );
            }
        });
    }
    private void zoomInViewSize(int height) {
        View img1 = findViewById(R.id.statusbar);
        ViewGroup.LayoutParams  lp = img1.getLayoutParams();
        lp.height =height;
        img1.setLayoutParams(lp);
    }
}
