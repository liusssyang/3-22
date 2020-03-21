package heath.com.test2_jmessage.activity.groupinfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;
import de.hdodenhof.circleimageview.CircleImageView;
import heath.com.test2_jmessage.R;
import heath.com.test2_jmessage.StatusBar.StatusBarUtil;
import heath.com.test2_jmessage.tools.tools;

import static heath.com.test2_jmessage.activity.groupinfo.GroupInfoActivity.gMemberAdapter;
import static heath.com.test2_jmessage.activity.groupinfo.GroupInfoActivity.gMemberList;


public class groupMemManage extends Activity {
    private int position;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_group_mem_manage);
        position = getIntent().getIntExtra("position", -1);
        zoomInViewSize(StatusBarUtil.getStatusBarHeight(this));
        TextView mem_delete= findViewById(R.id.mem_delete);
        final List<String> mNames=new ArrayList<>();
        mNames.add(gMemberList.get(position).getUserName());
        mem_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JMessageClient.removeGroupMembers(gMemberList.get(position).getGroupId(),
                        gMemberList.get(position).getAppkey(), mNames, new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {
                        if (i == 0) {
                            tools.getGroupMembers(gMemberList.get(position).getGroupId());
                            gMemberList.remove(position);
                            gMemberAdapter.notifyDataSetChanged();
                            Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), s+"\n删除失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        TextView manage_back = findViewById(R.id.manage_back);
        manage_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
            }
        });
        TextView nickname=findViewById(R.id.nickname);
        nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), GroupMemNicknameActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("position",position);
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
            }
        });
        final CircleImageView manage_person_icon = findViewById(R.id.manage_person_icon);
        final TextView manage_Name = findViewById(R.id.manage_Name);
        final TextView manage_simple_message = findViewById(R.id.manage_simple_message);
        RelativeLayout personal_information = findViewById(R.id.personal_information);
        if (position >= 0) {
            if (!TextUtils.isEmpty(gMemberList.get(position).getNickname())) {
                manage_Name.setText(gMemberList.get(position).getNickname());
                manage_Name.append("(" + gMemberList.get(position).getUserName() + ")");
            } else
                manage_Name.append(gMemberList.get(position).getUserName());
            manage_simple_message.setText(tools.Age(gMemberList.get(position).getMillisecond()) + "岁");
            if (!TextUtils.isEmpty(gMemberList.get(position).getGender())) {
                manage_simple_message.append("|" + gMemberList.get(position).getGender());
            }
        }
        manage_person_icon.setImageBitmap(gMemberList.get(position).getAvatar());
        final TextView state = findViewById(R.id.state);
        final LinearLayout detail_information = findViewById(R.id.detail_information);
        final TextView UserId = findViewById(R.id.userid);
        TextView note = findViewById(R.id.note);
        TextView adress = findViewById(R.id.adress);
        TextView AppKey = findViewById(R.id.appkey);
        TextView other = findViewById(R.id.other);
        TextView signature = findViewById(R.id.signature);
        TextView birthday = findViewById(R.id.birthday);
        TextView username = findViewById(R.id.username);

        TextView no_speak=findViewById(R.id.no_speak);
        no_speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SetGroupMemSilenceActivity.class);
                intent.putExtra("position", position);
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
            }
        });
        TextView keeper=findViewById(R.id.keeper);
        keeper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GroupKeeperActivity.class);
                intent.putExtra("position", position);
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
            }
        });
        TextView change_group_admin=findViewById(R.id.change_group_admin);
        change_group_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChangeGroupAdminActivity.class);
                intent.putExtra("position", position);
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
            }
        });
        username.setText(gMemberList.get(position).getUserName());
        birthday.setText(gMemberList.get(position).getBirthday());
        signature.setText(gMemberList.get(position).getSignature());
        adress.setText(gMemberList.get(position).getAddress());
        other.setText(gMemberList.get(position).getOther());
        UserId.setText(gMemberList.get(position).getUserId() + "");
        note.setText(gMemberList.get(position).getName());
        AppKey.setText(gMemberList.get(position).getAppkey());
        detail_information.setVisibility(View.GONE);
        personal_information.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state.getText().toString().equals(" ")) {
                    state.setText("  ");
                    detail_information.setVisibility(View.GONE);
                } else {
                    state.setText(" ");
                    detail_information.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    private void zoomInViewSize(int height) {
        View img1 = findViewById(R.id.statusbar);
        ViewGroup.LayoutParams lp = img1.getLayoutParams();
        lp.height = height;
        img1.setLayoutParams(lp);
    }
}
