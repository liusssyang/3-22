package heath.com.test2_jmessage.activity.groupinfo;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.jpush.im.android.ErrorCode;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetGroupInfoCallback;
import cn.jpush.im.android.api.callback.PublishAnnouncementCallback;
import cn.jpush.im.android.api.callback.RequestCallback;
import cn.jpush.im.android.api.model.GroupAnnouncement;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.api.BasicCallback;
import heath.com.test2_jmessage.R;
import heath.com.test2_jmessage.StatusBar.StatusBarUtil;
import heath.com.test2_jmessage.tools.PushToast;

import static heath.com.test2_jmessage.activity.groupinfo.GroupInfoActivity.gMemberList;

public class GroupAnnouncementActivity extends Activity implements View.OnClickListener {
    private TextView mEtGroupId;
    private EditText mEtText;
    private EditText mEtAnnounceID;
    private TextView mTvResult;
    private int position;
    private boolean isSending=false;
    private Switch setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initData() {
        findViewById(R.id.bt_publish_group_announcement).setOnClickListener(this);
        findViewById(R.id.bt_del_group_announcement).setOnClickListener(this);
        findViewById(R.id.bt_set_top).setOnClickListener(this);
        findViewById(R.id.bt_cancel_top).setOnClickListener(this);
        findViewById(R.id.bt_get_announcements).setOnClickListener(this);
    }

    private void initView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_group_announcement);
        zoomInViewSize(StatusBarUtil.getStatusBarHeight(this));
        position = getIntent().getIntExtra("position", position);
        PushToast.getInstance().init(this);
        mEtGroupId = findViewById(R.id.et_gid);
        mEtGroupId.setText(gMemberList.get(position).getGroupId()+"");
        mEtText = (EditText) findViewById(R.id.et_text);
        mEtAnnounceID = (EditText) findViewById(R.id.et_announce_id);
        mTvResult = (TextView) findViewById(R.id.tv_show_result);
        TextView textView = findViewById(R.id.manage_back);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
            }
        });
        Switch send_message=findViewById(R.id.send_message);
        send_message.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {isSending=true;}else isSending=false;
            }
        });
        setting = findViewById(R.id.no_disturb);

        setting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    JMessageClient.getGroupInfo(gMemberList.get(position).getGroupId(), new GetGroupInfoCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage, GroupInfo groupInfo) {
                            if (ErrorCode.NO_ERROR == responseCode) {
                                setTopAnnouncement(groupInfo, true);
                                 } else {
                                 PushToast.getInstance().createToast("提示", "设置失败", null, false);
                            }
                        }
                    });
                } else {
                    JMessageClient.getGroupInfo(gMemberList.get(position).getGroupId(), new GetGroupInfoCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage, GroupInfo groupInfo) {
                            if (ErrorCode.NO_ERROR == responseCode) {
                                setTopAnnouncement(groupInfo, false);
                                  } else {
                                PushToast.getInstance().createToast("提示", "设置失败", null, false);
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onClick(final View view) {
        try {
            long gid = gMemberList.get(position).getGroupId();
            JMessageClient.getGroupInfo(gid, new GetGroupInfoCallback() {
                @Override
                public void gotResult(int responseCode, String responseMessage, GroupInfo groupInfo) {
                    if (ErrorCode.NO_ERROR == responseCode) {
                        switch (view.getId()) {
                            case R.id.bt_publish_group_announcement:
                                publishGroupAnnouncement(groupInfo);
                                break;
                            case R.id.bt_del_group_announcement:
                                delGroupAnnouncement(groupInfo);
                                break;
                            case R.id.bt_set_top:
                                setTopAnnouncement(groupInfo, true);
                                break;
                            case R.id.bt_cancel_top:
                                setTopAnnouncement(groupInfo, false);
                                break;
                            case R.id.bt_get_announcements:
                                getGroupAnnouncements(groupInfo);
                                break;
                            default:
                                break;
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "找不到群信息", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (NumberFormatException e) {
            Toast.makeText(getApplicationContext(), "请输入合法群组ID", Toast.LENGTH_SHORT).show();
        }
    }

    private void publishGroupAnnouncement(GroupInfo groupInfo) {
        if (!TextUtils.isEmpty(mEtText.getText())) {
            String text = mEtText.getText().toString();
            groupInfo.publishGroupAnnouncement(text, isSending, new PublishAnnouncementCallback() {
                @Override
                public void gotResult(int responseCode, String responseMessage, GroupAnnouncement announcement, Message message) {
                    if (ErrorCode.NO_ERROR == responseCode) {
                        PushToast.getInstance().createToast("提示", "发布公告成功", null, true);

                    } else {
                        PushToast.getInstance().createToast("提示", "发布公告失败", null, false);
                         mTvResult.setText("responseCode:" + responseCode + "\nresponseMessage:" + responseMessage);
                    }
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "请输入公告内容", Toast.LENGTH_SHORT).show();
        }
    }

    private void delGroupAnnouncement(GroupInfo groupInfo) {
        try {
            int announceID = Integer.valueOf(mEtAnnounceID.getText().toString());
            groupInfo.delGroupAnnouncement(announceID, new BasicCallback() {
                @Override
                public void gotResult(int responseCode, String responseMessage) {
                    if (ErrorCode.NO_ERROR == responseCode) {
                        Toast.makeText(getApplicationContext(), "删除公告成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "删除公告失败", Toast.LENGTH_SHORT).show();
                        mTvResult.setText("responseCode:" + responseCode + "\nresponseMessage:" + responseMessage);
                    }
                }
            });
        } catch (NumberFormatException e) {
            Toast.makeText(getApplicationContext(), "请输入合法公告ID", Toast.LENGTH_SHORT).show();
        }
    }

    private void setTopAnnouncement(GroupInfo groupInfo, final boolean isTop) {
        try {
            int announceID = Integer.valueOf(mEtAnnounceID.getText().toString());
            groupInfo.setTopAnnouncement(announceID, isTop, new BasicCallback() {
                @Override
                public void gotResult(int responseCode, String responseMessage) {
                    StringBuilder result = new StringBuilder();
                    result.append(isTop ? "置顶" : "取消置顶");
                    if (ErrorCode.NO_ERROR == responseCode) {
                        result.append("成功");
                        setting.setChecked(true);
                        PushToast.getInstance().createToast("提示", result.toString(), null, true);
                    } else {
                        result.append("失败");
                        setting.setChecked(false);
                        PushToast.getInstance().createToast("提示", result.toString(), null, false);
                        mTvResult.setText("responseCode:" + responseCode + "\nresponseMessage:" + responseMessage);
                    }
                }
            });
        } catch (NumberFormatException e) {
            setting.setChecked(false);
            PushToast.getInstance().createToast("提示", "请输入合法公告ID", null, false);
        }
    }

    private void getGroupAnnouncements(GroupInfo groupInfo) {
        groupInfo.getAnnouncementsByOrder(new RequestCallback<List<GroupAnnouncement>>() {
            @Override
            public void gotResult(int responseCode, String responseMessage, List<GroupAnnouncement> announcements) {
                if (ErrorCode.NO_ERROR == responseCode) {
                    StringBuilder result = new StringBuilder();
                    for (GroupAnnouncement announcement : announcements) {
                        result.append("公告ID:" + announcement.getAnnounceID() + "\n");
                        result.append("公告内容:" + announcement.getText() + "\n");
                        result.append("公告创建时间:" + announcement.getCtime() + "\n");
                        result.append("公告是否置顶:" + announcement.isTop() + "\n");
                        result.append("公告置顶时间:" + announcement.getTopTime() + "\n");
                        result.append("公告发布者(username):" + announcement.getPublisher().getUserName() + "\n\n");
                    }
                    Toast.makeText(getApplicationContext(), "获取公告成功", Toast.LENGTH_SHORT).show();
                    mTvResult.setText(result.toString());
                } else {
                    Toast.makeText(getApplicationContext(), "获取公告失败", Toast.LENGTH_SHORT).show();
                    mTvResult.setText("responseCode:" + responseCode + "\nresponseMessage:" + responseMessage);
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
