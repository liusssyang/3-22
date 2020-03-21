package heath.com.test2_jmessage.activity.groupinfo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cn.jpush.im.android.ErrorCode;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetGroupInfoCallback;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.GroupMemberInfo;
import cn.jpush.im.api.BasicCallback;
import heath.com.test2_jmessage.R;
import heath.com.test2_jmessage.StatusBar.StatusBarUtil;

import static heath.com.test2_jmessage.activity.groupinfo.GroupInfoActivity.gMemberList;

public class GroupMemNicknameActivity extends Activity implements View.OnClickListener{
    private EditText mEtGroupId;
    private TextView mEtUsername;
    private TextView mEtAppKey;
    private EditText mEtNickname;
    private TextView mTvShowResult;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_group_mem_nickname);
        zoomInViewSize(StatusBarUtil.getStatusBarHeight(this));
        mEtGroupId = (EditText) findViewById(R.id.et_gid);
        mEtUsername = findViewById(R.id.et_username);
        mEtAppKey = findViewById(R.id.et_appKey);
        mEtNickname = (EditText) findViewById(R.id.et_nickname);
        mTvShowResult = (TextView) findViewById(R.id.tv_show_group_member_nickname);
        position=getIntent().getIntExtra("position",-1);
    }

    private void initData() {
        findViewById(R.id.bt_get_nickname).setOnClickListener(this);
        findViewById(R.id.bt_set_nickname).setOnClickListener(this);
        TextView manage_back = findViewById(R.id.manage_back);
        manage_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
            }
        });
        mEtAppKey.setText(gMemberList.get(position).getAppkey());
        mEtUsername.setText(gMemberList.get(position).getUserName());
    }

    @Override
    public void onClick(View view) {

        long gid = gMemberList.get(position).getGroupId();
        String username =gMemberList.get(position).getUserName();
        String appkey = gMemberList.get(position).getAppkey();

        switch (view.getId()) {
            case R.id.bt_get_nickname:
                getNickname(gid, username, appkey);
                break;
            case R.id.bt_set_nickname:
                String nickname = mEtNickname.getText().toString();
                setNickname(gid, username, appkey, nickname);
        }
    }

    private void getNickname(long gid, String username, String appkey) {
        operateNickname(gid, username, appkey, true, null);
    }

    private void setNickname(long gid, String username, String appkey, String nickname) {
        operateNickname(gid, username, appkey, false, nickname);
    }

    private void operateNickname(long gid, final String username, final String appkey, final boolean isGet, final String nickname) {
        JMessageClient.getGroupInfo(gid, new GetGroupInfoCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage, GroupInfo groupInfo) {
                if (responseCode == ErrorCode.NO_ERROR) {
                    if (isGet) {
                        GroupMemberInfo memberInfo = groupInfo.getGroupMember(username, appkey);
                        if (memberInfo != null) {
                            mTvShowResult.setText("nickname:" + memberInfo.getNickName());
                        } else {
                            mTvShowResult.setText("群成员未找到");
                        }
                    } else {
                        groupInfo.setMemNickname(username, appkey, nickname, new BasicCallback() {
                            @Override
                            public void gotResult(int responseCode, String responseMessage) {
                                if (responseCode == ErrorCode.NO_ERROR) {
                                    Toast.makeText(getApplicationContext(), "设置群昵称成功", Toast.LENGTH_SHORT).show();
                                } else {
                                    mTvShowResult.setText("设置群昵称失败\ncode:" + responseCode + "\nmsg:" + responseMessage);
                                }
                            }
                        });
                    }
                } else {
                    mTvShowResult.setText("获取群信息失败");
                }
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
