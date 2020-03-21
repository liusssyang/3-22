package heath.com.test2_jmessage.activity.groupinfo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetGroupInfoCallback;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.api.BasicCallback;
import heath.com.test2_jmessage.R;
import heath.com.test2_jmessage.StatusBar.StatusBarUtil;

import static heath.com.test2_jmessage.activity.groupinfo.GroupInfoActivity.gMemberList;

public class ChangeGroupAdminActivity extends Activity {

    private TextView mEtGroupID;
    private TextView mEtUsername;
    private TextView mEtAppKey;
    private Button mBtChangeGroupAdmin;
    private TextView mTvResult;
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
        setContentView(R.layout.activity_change_group_admin);
        position = getIntent().getIntExtra("position", -1);
        zoomInViewSize(StatusBarUtil.getStatusBarHeight(this));
        mEtGroupID = findViewById(R.id.et_group_id);
        mEtUsername = findViewById(R.id.et_username);
        mEtAppKey =  findViewById(R.id.et_appKey);
        mEtGroupID.setText(gMemberList.get(position).getGroupId()+"");
        mEtUsername.setText(gMemberList.get(position).getUserName());
        mEtAppKey.setText(gMemberList.get(position).getAppkey());
        mBtChangeGroupAdmin = (Button) findViewById(R.id.bt_change_group_admin);
        mTvResult = (TextView) findViewById(R.id.tv_result);
    }

    private void initData() {
        mBtChangeGroupAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    long gid = gMemberList.get(position).getGroupId();
                    final String username = gMemberList.get(position).getUserName();
                    final String appkey = gMemberList.get(position).getAppkey();
                    JMessageClient.getGroupInfo(gid, new GetGroupInfoCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage, GroupInfo groupInfo) {
                            if (responseCode == 0) {
                                groupInfo.changeGroupAdmin(username, appkey, new BasicCallback() {
                                    @Override
                                    public void gotResult(int responseCode, String responseMessage) {
                                        if (responseCode == 0) {
                                            Toast.makeText(ChangeGroupAdminActivity.this, "移交群主成功", Toast.LENGTH_SHORT).show();
                                        } else {
                                            mTvResult.append("responseCode: " + responseCode + ".responseMessage: " + responseMessage);
                                            Toast.makeText(ChangeGroupAdminActivity.this, "移交群主失败", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                mTvResult.append("responseCode: " +  responseCode + ".responseMessage: " + responseMessage);
                                Toast.makeText(ChangeGroupAdminActivity.this, "移交群主失败", Toast.LENGTH_SHORT).show();
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
    }
    private void zoomInViewSize(int height) {
        View img1 = findViewById(R.id.statusbar);
        ViewGroup.LayoutParams lp = img1.getLayoutParams();
        lp.height = height;
        img1.setLayoutParams(lp);
    }
}
