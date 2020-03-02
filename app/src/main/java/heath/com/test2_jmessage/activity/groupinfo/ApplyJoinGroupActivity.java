package heath.com.test2_jmessage.activity.groupinfo;

import android.app.Activity;
import android.app.ProgressDialog;
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

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;
import heath.com.test2_jmessage.R;
import heath.com.test2_jmessage.StatusBar.StatusBarUtil;

public class ApplyJoinGroupActivity extends Activity {
    private static final String TAG = "ApplyJoinGroupActivity";

    private EditText mEt_groupID;
    private EditText mEt_apply_reason;
    private Button mBt_applyJoinGroup;
    private ProgressDialog mProgressDialog;
    private String reason;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initData() {
        mEt_groupID.setText(getIntent().getStringExtra("groupId"));
        mEt_groupID.setSelection(getIntent().getStringExtra("groupId").length());
        mBt_applyJoinGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mEt_groupID.getText())) {
                    Toast.makeText(getApplicationContext(), "请输入gid", Toast.LENGTH_SHORT).show();
                    return;
                }
                mProgressDialog = ProgressDialog.show(ApplyJoinGroupActivity.this, "提示：", "正在加载中。。。");
                if (!TextUtils.isEmpty(mEt_apply_reason.getText())) {
                    reason = mEt_apply_reason.getText().toString();
                }
                
                JMessageClient.applyJoinGroup(Long.parseLong(mEt_groupID.getText().toString()), reason, new BasicCallback() {
                    @Override
                    public void gotResult(int responseCode, String responseMessage) {
                        mProgressDialog.dismiss();
                        if (responseCode == 0) {
                            Toast.makeText(ApplyJoinGroupActivity.this, "申请成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(TAG, "apply failed. code :" + responseCode + " msg : " + responseMessage);
                            Toast.makeText(ApplyJoinGroupActivity.this, "申请失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void initView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_apply_join_group);
        zoomInViewSize(StatusBarUtil.getStatusBarHeight(this));
        mEt_groupID = (EditText) findViewById(R.id.et_group_id);
        mEt_apply_reason = (EditText) findViewById(R.id.et_apply_reason);
        mBt_applyJoinGroup = (Button) findViewById(R.id.bt_apply_join_group);
        TextView manage_back=findViewById(R.id.manage_back);
        manage_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Log.d(TAG, "onClick: ");
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
