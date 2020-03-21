package heath.com.test2_jmessage.activity.groupinfo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetGroupInfoCallback;
import cn.jpush.im.android.api.model.GroupBasicInfo;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.api.BasicCallback;
import de.hdodenhof.circleimageview.CircleImageView;
import heath.com.test2_jmessage.R;
import heath.com.test2_jmessage.StatusBar.StatusBarUtil;

import static heath.com.test2_jmessage.activity.groupinfo.GroupInfoActivity.gMemberList;

/**
 * Created by ${chenyn} on 16/3/30.
 *
 * @desc :修改群组信息
 */
public class UpdateGroupInfoActivity extends Activity {

    private static int RESULT_LOAD_IMAGE = 1;

    private EditText mEt_groupName;
    private Button mBt_updateGroupName;
    private ProgressDialog mProgressDialog = null;
    private long mGroupID;
    private EditText mEt_groupDesc;
    private EditText mEt_groupAvatarPath;
    private TextView mTv_updateInfo;
    private Button mBt_updateGroupDesc;
    private String mAvatarPath;
    private RadioGroup mRg_type;
    private GroupBasicInfo.Type groupType;
    private int position;
    private CircleImageView icon_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initData();
    }

    private void initData() {
        mEt_groupAvatarPath.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, RESULT_LOAD_IMAGE);
                }
            }
        });

        mBt_updateGroupName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String name = mEt_groupName.getText().toString();

                mProgressDialog = ProgressDialog.show(UpdateGroupInfoActivity.this, "提示：", "正在加载中。。。");
                mProgressDialog.setCanceledOnTouchOutside(true);
                mGroupID = gMemberList.get(position).getGroupId();
                JMessageClient.getGroupInfo(mGroupID, new GetGroupInfoCallback() {
                    @Override
                    public void gotResult(int responseCode, String responseMessage, GroupInfo groupInfo) {
                        if (null != groupInfo) {
                            groupInfo.updateName(name, new BasicCallback() {
                                @Override
                                public void gotResult(int responseCode, String responseMessage) {
                                    if (responseCode == 0) {
                                        mTv_updateInfo.setText("修改群组名成功" + "\n");
                                        mProgressDialog.dismiss();
                                    } else {
                                        mTv_updateInfo.setText("修改群组名失败" + "responseCode = " + responseCode + " ; Desc = " + responseMessage + "\n");
                                        mProgressDialog.dismiss();
                                    }
                                }
                            });
                        } else {
                            mTv_updateInfo.setText("修改群组名失败" + "responseCode = " + responseCode + " ; Desc = " + responseMessage + "\n");
                            mProgressDialog.dismiss();
                        }
                    }
                });

            }
        });

        mBt_updateGroupDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgressDialog = ProgressDialog.show(UpdateGroupInfoActivity.this, "提示：", "正在加载中。。。");
                mProgressDialog.setCanceledOnTouchOutside(true);
                mGroupID = gMemberList.get(position).getGroupId();
                final String desc = mEt_groupDesc.getText().toString();
                JMessageClient.getGroupInfo(mGroupID, new GetGroupInfoCallback() {
                    @Override
                    public void gotResult(int responseCode, String responseMessage, GroupInfo groupInfo) {
                        if (null != groupInfo) {
                            groupInfo.updateDescription(desc, new BasicCallback() {
                                @Override
                                public void gotResult(int responseCode, String responseMessage) {
                                    if (responseCode == 0) {
                                        mProgressDialog.dismiss();
                                        mTv_updateInfo.setText("修改群描述成功" + "\n");
                                    } else {
                                        mProgressDialog.dismiss();
                                        mTv_updateInfo.setText("修改群描述失败" + "responseCode = " + responseCode + " ; Desc = " + responseMessage);
                                        Log.i("UpdateGroupInfoActivity", "JMessageClient.updateGroupDescription " + ", responseCode = " + responseCode + " ; Desc = " + responseMessage);
                                    }
                                }
                            });
                        } else {
                            mProgressDialog.dismiss();
                            mTv_updateInfo.setText("修改群描述失败" + "responseCode = " + responseCode + " ; Desc = " + responseMessage);
                        }
                    }
                });

            }
        });

        findViewById(R.id.bt_update_group_avatar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog = ProgressDialog.show(UpdateGroupInfoActivity.this, "提示：", "正在加载中。。。");
                mProgressDialog.setCanceledOnTouchOutside(true);
                mGroupID = gMemberList.get(position).getGroupId();
                JMessageClient.getGroupInfo(mGroupID, new GetGroupInfoCallback() {
                    @Override
                    public void gotResult(int responseCode, String responseMessage, GroupInfo groupInfo) {
                        if (null != groupInfo) {
                            groupInfo.updateAvatar(new File(mAvatarPath), null, new BasicCallback() {
                                @Override
                                public void gotResult(int responseCode, String responseMessage) {
                                    if (responseCode == 0) {
                                        mProgressDialog.dismiss();
                                        mTv_updateInfo.setText("修改群头像成功" + "\n");
                                    } else {
                                        mProgressDialog.dismiss();
                                        mTv_updateInfo.setText("修改群头像失败" + "responseCode = " + responseCode + " ; Desc = " + responseMessage);
                                        Log.i("UpdateGroupInfoActivity", "JMessageClient.updateGroupAvatar " + ", responseCode = " + responseCode + " ; Desc = " + responseMessage);
                                    }
                                }
                            });
                        } else {
                            mProgressDialog.dismiss();
                            mTv_updateInfo.setText("修改群头像失败" + "responseCode = " + responseCode + " ; Desc = " + responseMessage);
                        }
                    }
                });

            }
        });

        findViewById(R.id.bt_change_group_type).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressDialog = ProgressDialog.show(UpdateGroupInfoActivity.this, "提示：", "正在加载中。。。");
                mProgressDialog.setCanceledOnTouchOutside(true);
                mGroupID = gMemberList.get(position).getGroupId();
                JMessageClient.getGroupInfo(mGroupID, new GetGroupInfoCallback() {
                    @Override
                    public void gotResult(int responseCode, String responseMessage, GroupInfo groupInfo) {
                        if (responseCode == 0) {
                            groupInfo.changeGroupType(groupType, new BasicCallback() {
                                @Override
                                public void gotResult(int responseCode, String responseMessage) {
                                    mProgressDialog.dismiss();
                                    if (responseCode == 0) {
                                        Toast.makeText(getApplicationContext(), "修改群组类型成功", Toast.LENGTH_SHORT).show();
                                    } else {
                                        mTv_updateInfo.setText("修改群类型失败" + "responseCode = " + responseCode + " ; Desc = " + responseMessage);
                                    }
                                }
                            });
                        } else {
                            mProgressDialog.dismiss();
                            mTv_updateInfo.setText("修改群类型失败" + "responseCode = " + responseCode + " ; Desc = " + responseMessage);
                        }
                    }
                });

            }
        });

        mRg_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkId) {
                if (checkId == R.id.rb_private) {
                    groupType = GroupBasicInfo.Type.private_group;
                } else if (checkId == R.id.rb_public) {
                    groupType = GroupBasicInfo.Type.public_group;
                }
            }
        });
        icon_add = findViewById(R.id.icon_add);
        icon_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RESULT_LOAD_IMAGE);
            }
        });
        Button bt_confirm = findViewById(R.id.bt_confirm);
        bt_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initing();
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

    private void initView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_update_group_info);
        zoomInViewSize(StatusBarUtil.getStatusBarHeight(this));
        mEt_groupName = (EditText) findViewById(R.id.et_group_name);
        mBt_updateGroupName = (Button) findViewById(R.id.bt_update_group_name);
        mEt_groupDesc = (EditText) findViewById(R.id.et_group_desc);
        mTv_updateInfo = (TextView) findViewById(R.id.tv_update_info);
        mBt_updateGroupDesc = (Button) findViewById(R.id.bt_update_group_desc);
        mEt_groupAvatarPath = (EditText) findViewById(R.id.et_group_avatar_path);
        mRg_type = (RadioGroup) findViewById(R.id.rg_type);
        position = getIntent().getIntExtra("position", -1);
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
                icon_add.setImageBitmap(BitmapFactory.decodeFile(mAvatarPath));
                cursor.close();
            }
        }
    }

    void zoomInViewSize(int height) {
        View img1 = findViewById(R.id.statusbar);
        ViewGroup.LayoutParams lp = img1.getLayoutParams();
        lp.height = height;
        img1.setLayoutParams(lp);
    }

    private void initing() {
        mGroupID = gMemberList.get(position).getGroupId();
        mTv_updateInfo.setText(null);
        if (!TextUtils.isEmpty(mEt_groupName.getText().toString())) {
            JMessageClient.getGroupInfo(mGroupID, new GetGroupInfoCallback() {
                @Override
                public void gotResult(int responseCode, String responseMessage, GroupInfo groupInfo) {
                    if (null != groupInfo) {
                        groupInfo.updateName(mEt_groupName.getText().toString(), new BasicCallback() {
                            @Override
                            public void gotResult(int responseCode, String responseMessage) {
                                if (responseCode == 0) {
                                    mTv_updateInfo.append("修改群组名成功" + "\n");
                                } else {
                                    mTv_updateInfo.append("修改群组名失败" + "responseCode = " + responseCode + " ; Desc = " + responseMessage + "\n");
                                }
                            }
                        });
                    } else {
                        mTv_updateInfo.append("修改群组名失败" + "responseCode = " + responseCode + " ; Desc = " + responseMessage + "\n");
                        mProgressDialog.dismiss();
                    }
                }
            });
        }
        if (!TextUtils.isEmpty(mEt_groupDesc.getText().toString())) {
            JMessageClient.getGroupInfo(mGroupID, new GetGroupInfoCallback() {
                @Override
                public void gotResult(int responseCode, String responseMessage, GroupInfo groupInfo) {
                    if (null != groupInfo) {
                        groupInfo.updateDescription(mEt_groupDesc.getText().toString(), new BasicCallback() {
                            @Override
                            public void gotResult(int responseCode, String responseMessage) {
                                if (responseCode == 0) {
                                    mTv_updateInfo.append("修改群描述成功" + "\n");
                                } else {

                                    mTv_updateInfo.append("修改群描述失败" + "responseCode = " + responseCode + " ; Desc = " + responseMessage);
                                    Log.i("UpdateGroupInfoActivity", "JMessageClient.updateGroupDescription " + ", responseCode = " + responseCode + " ; Desc = " + responseMessage);
                                }
                            }
                        });
                    } else {

                        mTv_updateInfo.append("修改群描述失败" + "responseCode = " + responseCode + " ; Desc = " + responseMessage);
                    }
                }
            });
        }
        if (!TextUtils.isEmpty(mAvatarPath)) {
            JMessageClient.getGroupInfo(mGroupID, new GetGroupInfoCallback() {
                @Override
                public void gotResult(int responseCode, String responseMessage, GroupInfo groupInfo) {
                    if (null != groupInfo) {
                        groupInfo.updateAvatar(new File(mAvatarPath), null, new BasicCallback() {
                            @Override
                            public void gotResult(int responseCode, String responseMessage) {
                                if (responseCode == 0) {
                                    mTv_updateInfo.append("修改群头像成功" + "\n");
                                } else {

                                    mTv_updateInfo.append("修改群头像失败" + "responseCode = " + responseCode + " ; Desc = " + responseMessage);
                                    Log.i("UpdateGroupInfoActivity", "JMessageClient.updateGroupAvatar " + ", responseCode = " + responseCode + " ; Desc = " + responseMessage);
                                }
                            }
                        });
                    } else {

                        mTv_updateInfo.append("修改群头像失败" + "responseCode = " + responseCode + " ; Desc = " + responseMessage);
                    }
                }
            });
        }
        if (groupType != null) {
            JMessageClient.getGroupInfo(mGroupID, new GetGroupInfoCallback() {
                @Override
                public void gotResult(int responseCode, String responseMessage, GroupInfo groupInfo) {
                    if (responseCode == 0) {
                        groupInfo.changeGroupType(groupType, new BasicCallback() {
                            @Override
                            public void gotResult(int responseCode, String responseMessage) {

                                if (responseCode == 0) {
                                    Toast.makeText(getApplicationContext(), "修改群组类型成功", Toast.LENGTH_SHORT).show();
                                } else {
                                    mTv_updateInfo.append("修改群类型失败" + "responseCode = " + responseCode + " ; Desc = " + responseMessage);
                                }
                            }
                        });
                    } else {

                        mTv_updateInfo.append("修改群类型失败" + "responseCode = " + responseCode + " ; Desc = " + responseMessage);
                    }
                }
            });
        }


    }
}
