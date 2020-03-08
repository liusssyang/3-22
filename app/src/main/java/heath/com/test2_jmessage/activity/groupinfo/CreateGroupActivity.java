package heath.com.test2_jmessage.activity.groupinfo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.CreateGroupCallback;
import de.hdodenhof.circleimageview.CircleImageView;
import heath.com.test2_jmessage.R;
import heath.com.test2_jmessage.StatusBar.StatusBarUtil;

import static heath.com.test2_jmessage.activity.RegisterAndLoginActivity.addLayoutListener;

/**
 * Created by ${chenyn} on 16/3/29.
 *
 * @desc :创建群组
 */
public class CreateGroupActivity extends Activity {

    private static int RESULT_LOAD_IMAGE = 1;
    private Button mBt_create;
    private EditText mEt_groupDesc;
    private EditText mEt_groupName;
    private EditText mEt_groupAvatar;
    private ProgressDialog mProgressDialog;
    private String mAvatarPath;
    private CreateGroupCallback callback;
    private RadioGroup mRg_groupType;
    private RadioButton mRb_privateGroup;
    private RadioButton mRb_publicGroup;
    private int flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initData();
    }

    private void initData() {

        callback = new CreateGroupCallback() {
            @Override
            public void gotResult(int responseCode, String responseMsg, long groupId) {
                mProgressDialog.dismiss();
                if (responseCode == 0) {
                    Toast.makeText(getApplicationContext(), "创建成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "创建失败", Toast.LENGTH_SHORT).show();
                }
            }
        };
        mEt_groupName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(mEt_groupName.getText())){
                    mBt_create.setBackgroundResource(R.drawable.background_addnew_declined);
                    mBt_create.setEnabled(false);
                }else{
                    mBt_create.setBackgroundResource(R.drawable.background_addnew_accept);
                    mBt_create.setEnabled(true);
                }
            }
        });
        mBt_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog = ProgressDialog.show(CreateGroupActivity.this, "提示：", "正在加载中。。。");
                mProgressDialog.setCanceledOnTouchOutside(true);
                String name = mEt_groupName.getText().toString();
                String desc = mEt_groupDesc.getText().toString();
                flag = mRb_privateGroup.isChecked() ? 1 : 2;
                File avatarFile = TextUtils.isEmpty(mAvatarPath) ? null : new File(mAvatarPath);
                //创建群组
                if (null == avatarFile) {
                    if (flag == 1) {
                        JMessageClient.createGroup(name, desc, callback);
                    } else {
                        JMessageClient.createPublicGroup(name, desc, callback);
                    }
                } else {
                    if (flag == 1) {
                        JMessageClient.createGroup(name, desc, avatarFile, null, callback);
                    } else {
                        JMessageClient.createPublicGroup(name, desc, avatarFile, null, callback);
                    }
                }
            }
        });
        TextView service_station=findViewById(R.id.service_station);
        service_station.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "你还真把这当回事了？", Toast.LENGTH_SHORT).show();

            }
        });
        TextView manage_back=findViewById(R.id.manage_back);
        manage_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        CircleImageView icon_add=findViewById(R.id.icon_add);
        icon_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RESULT_LOAD_IMAGE);
            }
        });
        mEt_groupAvatar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, RESULT_LOAD_IMAGE);
                }
            }
        });
        mEt_groupAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RESULT_LOAD_IMAGE);
            }
        });

    }

    private void initView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_create_group);
        zoomInViewSize(StatusBarUtil.getStatusBarHeight(this));
        mEt_groupName = (EditText) findViewById(R.id.et_group_name);
        mEt_groupDesc = (EditText) findViewById(R.id.et_group_desc);
        mEt_groupAvatar = (EditText) findViewById(R.id.et_create_group_avatar);
        mRg_groupType = (RadioGroup) findViewById(R.id.rg_group_type);
        mRb_privateGroup = (RadioButton) findViewById(R.id.rb_private_group);
        mRb_publicGroup = (RadioButton) findViewById(R.id.rb_public_group);
        mBt_create = (Button) findViewById(R.id.bt_create_group);
        LinearLayout activity_create_group_totalLayout=findViewById(R.id.activity_create_group_totalLayout);
        if (TextUtils.isEmpty(mEt_groupName.getText())){
            mBt_create.setBackgroundResource(R.drawable.background_addnew_declined);
            mBt_create.setEnabled(false);
        }else{
            mBt_create.setBackgroundResource(R.drawable.background_addnew_accept);
            mBt_create.setEnabled(true);
        }
        addLayoutListener(activity_create_group_totalLayout,mBt_create);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            if (null != cursor) {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                mAvatarPath = cursor.getString(columnIndex);
                mEt_groupAvatar.setText("群头像路径：" + mAvatarPath);
                cursor.close();
            }
        }
    }
    private void zoomInViewSize(int height) {
        View img1 = findViewById(R.id.statusbar);
        ViewGroup.LayoutParams  lp = img1.getLayoutParams();
        lp.height =height;
        img1.setLayoutParams(lp);
    }
}
