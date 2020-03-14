package heath.com.test2_jmessage.activity.setting;

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

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;
import heath.com.test2_jmessage.R;
import heath.com.test2_jmessage.StatusBar.StatusBarUtil;

/**
 * Created by ${chenyn} on 16/3/25.
 *
 * @desc :更新当前用户密码
 */
public class UpdatePassword extends Activity {

    private Button   mBt_updatePassword;
    private EditText mEt_updateOldPassword;
    private EditText mEt_updateNewPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initData();
    }
/**=================     调用SDK 更新密码    =================*/
    private void initData() {
        mBt_updatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String oldPassword = mEt_updateOldPassword.getText().toString();
                final String newPassword = mEt_updateNewPassword.getText().toString();
                JMessageClient.updateUserPassword(oldPassword, newPassword, new BasicCallback() {
                    @Override
                    public void gotResult(int responseCode, String updadePasswordDesc) {
                        if (responseCode == 0) {
                            Toast.makeText(getApplicationContext(), "更新成功", Toast.LENGTH_SHORT).show();
                            Log.i("UpdatePassword", "JMessageClient.updateUserPassword" + ", responseCode = " + responseCode + " ; desc = " + updadePasswordDesc);

                        } else {
                            Log.i("UpdatePassword", "JMessageClient.updateUserPassword" + ", responseCode = " + responseCode + " ; desc = " + updadePasswordDesc);
                            Toast.makeText(getApplicationContext(), "更新失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void initView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_updatepassword);
        zoomInViewSize(StatusBarUtil.getStatusBarHeight(this));
        mBt_updatePassword = (Button) findViewById(R.id.bt_update_password);
        mEt_updateOldPassword = (EditText) findViewById(R.id.et_update_old_password);
        mEt_updateNewPassword = (EditText) findViewById(R.id.et_update_new_password);
        TextView manage_back=findViewById(R.id.manage_back);
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
