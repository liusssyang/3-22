package heath.com.test2_jmessage.activity.groupinfo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetGroupInfoCallback;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import heath.com.test2_jmessage.R;
import heath.com.test2_jmessage.StatusBar.StatusBarUtil;

import static heath.com.test2_jmessage.activity.groupinfo.GroupInfoActivity.gMemberList;

public class GroupKeeperActivity extends Activity implements View.OnClickListener{
    private EditText mEtGroupId;
    private TextView mEtUsername;
    private TextView mEtAppKey;
    private Button mBtAddGroupKeeper;
    private Button mBtRemoveGroupKeeper;
    private Button mBtGetGroupKeeper;
    private TextView mTvGroupKeeper;
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
        setContentView(R.layout.activity_group_keeper);
        position = getIntent().getIntExtra("position", -1);
        zoomInViewSize(StatusBarUtil.getStatusBarHeight(this));
        mEtGroupId = (EditText) findViewById(R.id.et_gid);
        mEtUsername =  findViewById(R.id.et_keeper_username);
        mEtAppKey =  findViewById(R.id.et_keeper_appKey);

        mEtAppKey.setText(gMemberList.get(position).getAppkey());
        mEtUsername.setText(gMemberList.get(position).getUserName());

        mBtAddGroupKeeper = (Button) findViewById(R.id.bt_add_group_keeper);
        mBtRemoveGroupKeeper = (Button) findViewById(R.id.bt_remove_group_keeper);
        mBtGetGroupKeeper = (Button) findViewById(R.id.bt_get_group_keeper);
        mTvGroupKeeper = (TextView) findViewById(R.id.tv_show_group_keeper);
    }

    private void initData() {
        mBtAddGroupKeeper.setOnClickListener(this);
        mBtRemoveGroupKeeper.setOnClickListener(this);
        mBtGetGroupKeeper.setOnClickListener(this);
        TextView manage_back = findViewById(R.id.manage_back);
        manage_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
            }
        });
    }

    @Override
    public void onClick(final View v) {
        long gid = gMemberList.get(position).getGroupId();
        JMessageClient.getGroupInfo(gid, new GetGroupInfoCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage, GroupInfo groupInfo) {
                if (0 == responseCode) {
                    switch (v.getId()) {
                        case R.id.bt_add_group_keeper:
                            setGroupKeeper(groupInfo, true);
                            break;
                        case R.id.bt_remove_group_keeper:
                            setGroupKeeper(groupInfo, false);
                            break;
                        case R.id.bt_get_group_keeper:
                            List<UserInfo> userInfos = groupInfo.getGroupKeepers();
                            String result = "这里只展示username:";
                            for (UserInfo userInfo : userInfos) {
                                if (userInfo != null) {
                                    result += "\n" + userInfo.getUserName();
                                }
                            }
                            mTvGroupKeeper.setText(result);
                            break;
                        default:
                            break;
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "找不到群信息", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setGroupKeeper(GroupInfo groupInfo, boolean isAdd) {
            String username =gMemberList.get(position).getUserName();
            String appKey = gMemberList.get(position).getAppkey();
        UserInfo userInfo = groupInfo.getGroupMemberInfo(username, appKey);
            if (userInfo != null) {
                List<UserInfo> userInfos = new ArrayList<UserInfo>();
                userInfos.add(userInfo);
                if (isAdd) {
                    groupInfo.addGroupKeeper(userInfos, new BasicCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage) {
                            if (responseCode == 0) {
                                Toast.makeText(getApplicationContext(), "添加管理员成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "添加管理员失败", Toast.LENGTH_SHORT).show();
                                mTvGroupKeeper.setText("responseCode:" + responseCode + "\nresponseMessage:" + responseMessage);
                            }
                        }
                    });
                } else {
                    groupInfo.removeGroupKeeper(userInfos, new BasicCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage) {
                            if (responseCode == 0) {
                                Toast.makeText(getApplicationContext(), "取消管理员成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "取消管理员失败", Toast.LENGTH_SHORT).show();
                                mTvGroupKeeper.setText("responseCode:" + responseCode + "\nresponseMessage:" + responseMessage);
                            }
                        }
                    });
                }
            } else {
                mTvGroupKeeper.setText("can not find group member info with given username and appKey");
            }

    }
    void zoomInViewSize(int height) {
        View img1 = findViewById(R.id.statusbar);
        ViewGroup.LayoutParams lp = img1.getLayoutParams();
        lp.height = height;
        img1.setLayoutParams(lp);
    }
    protected void onStop() {
        super.onStop();
        Log.d("13172", gMemberList.get(position).getGroupId()+"ID");
    }
}
