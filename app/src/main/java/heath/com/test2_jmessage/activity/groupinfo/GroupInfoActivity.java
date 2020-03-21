package heath.com.test2_jmessage.activity.groupinfo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetGroupIDListCallback;
import cn.jpush.im.api.BasicCallback;
import heath.com.test2_jmessage.R;
import heath.com.test2_jmessage.StatusBar.StatusBarUtil;
import heath.com.test2_jmessage.activity.TypeActivity;
import heath.com.test2_jmessage.adapter.GMemberAdapter;
import heath.com.test2_jmessage.recycleView_item.personMsg;
import heath.com.test2_jmessage.tools.PushToast;

import static heath.com.test2_jmessage.activity.TypeActivity.groupAdapter;
import static heath.com.test2_jmessage.activity.createmessage.CreateGroupTextMsgActivity.GroupMemberList;
import static heath.com.test2_jmessage.application.MyApplication.groupList;

/**
 * Created by ${chenyn} on 16/3/29.
 *
 * @desc :群组相关
 */
public class GroupInfoActivity extends Activity implements View.OnClickListener {

    private Button mBt_createGroup;
    private Button mBt_dissolveGroup;
    private Button mBt_getGroupInfo;
    private Button mBt_getPublicGroupInfos;
    private Button mBt_applyJoinGroup;
    private Button mBt_addGroupMembers;
    private Button mBt_setGroupMemSilence;
    private Button mBt_updateGroupNameAndDesc;
    private Button mBt_exitGroup;
    private TextView mTv_getList;
    private Button mBt_getGroupIDList;
    private ProgressDialog mProgressDialog = null;
    private Button mBt_getGroupMembers;
    private Button mBt_blockedGroupMsg;//message_notify
    public static List<personMsg> gMemberList = new ArrayList<>();
    public static GMemberAdapter gMemberAdapter;
    private int position=-1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initData() {

        mBt_createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreateGroupActivity.class);
                startActivity(intent);
            }
        });

        mBt_dissolveGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DissolveGroupActivity.class);
                startActivity(intent);
            }
        });

        mBt_getPublicGroupInfos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GetPublicGroupInfosActivity.class);
                startActivity(intent);
            }
        });

        mBt_applyJoinGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ApplyJoinGroupActivity.class);
                startActivity(intent);
            }
        });

        mBt_getGroupInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GetGroupInfoActivity.class);
                startActivity(intent);
            }
        });

        mBt_addGroupMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddRemoveGroupMemberActivity.class);
                startActivity(intent);
            }
        });

        mBt_setGroupMemSilence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SetGroupMemSilenceActivity.class);
                startActivity(intent);
            }
        });

        mBt_updateGroupNameAndDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UpdateGroupInfoActivity.class);
                startActivity(intent);
            }
        });

        mBt_exitGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ExitGroupActivity.class);
                startActivity(intent);
            }
        });

        mBt_getGroupIDList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog = ProgressDialog.show(GroupInfoActivity.this, "提示：", "正在加载中。。。");
                mProgressDialog.setCanceledOnTouchOutside(false);

                JMessageClient.getGroupIDList(new GetGroupIDListCallback() {
                    @Override
                    public void gotResult(int i, String s, List<Long> list) {
                        if (i == 0) {
                            mProgressDialog.dismiss();
                           mTv_getList.setText("");
                            mTv_getList.append(list.toString());
                        } else {
                            mProgressDialog.dismiss();
                            Log.i("GroupInfoActivity", "JMessageClient.getGroupIDList " + ", responseCode = " + i + " ; Desc = " + s);
                            Toast.makeText(getApplicationContext(), "获取失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        mBt_getGroupMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GetLocalGroupMembersActivity.class);
                startActivity(intent);
            }
        });

        mBt_blockedGroupMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BlockedGroupMsgActivity.class);
                startActivity(intent);
            }
        });

    }

    private void initView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_group_info);
        zoomInViewSize(StatusBarUtil.getStatusBarHeight(this));
        position=getIntent().getIntExtra("position",position);

        mBt_createGroup = (Button) findViewById(R.id.bt_create_group);
        mBt_dissolveGroup = (Button) findViewById(R.id.bt_dissolve_group);
        mBt_getPublicGroupInfos = (Button) findViewById(R.id.bt_get_public_group_infos);
        mBt_applyJoinGroup = (Button) findViewById(R.id.bt_apply_join_group);
        mBt_getGroupInfo = (Button) findViewById(R.id.bt_get_group_info);
        mBt_addGroupMembers = (Button) findViewById(R.id.bt_add_group_members);
        mBt_setGroupMemSilence = (Button) findViewById(R.id.bt_group_mem_silence);
        mBt_updateGroupNameAndDesc = (Button) findViewById(R.id.bt_update_group_name_and_desc);
        mBt_exitGroup = (Button) findViewById(R.id.bt_exit_group);
        mTv_getList = (TextView) findViewById(R.id.tv_get_list);
        mBt_getGroupIDList = (Button) findViewById(R.id.bt_get_group_id_list);
        mBt_getGroupMembers = (Button) findViewById(R.id.bt_get_group_members);
        mBt_blockedGroupMsg = (Button) findViewById(R.id.bt_blockedGroupMsg);
        findViewById(R.id.bt_group_approval_batch).setOnClickListener(this);
        findViewById(R.id.bt_group_member_nickname).setOnClickListener(this);
        findViewById(R.id.bt_group_announcement).setOnClickListener(this);
        findViewById(R.id.bt_group_black_list).setOnClickListener(this);

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this);
        layoutManager2.setOrientation(RecyclerView.HORIZONTAL);
        RecyclerView recyclerView2 = findViewById(R.id.type_recyclerview);
        recyclerView2.setLayoutManager(layoutManager2);
        gMemberAdapter = new GMemberAdapter(gMemberList);
        recyclerView2.setAdapter(gMemberAdapter);
        TextView member_number=findViewById(R.id.member_number);
        member_number.setText(GroupMemberList.size()+"人>");
        TextView textView=findViewById(R.id.manage_back);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
            }
        });

        TextView message_notify=findViewById(R.id.message_notify);
        message_notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), GroupAnnouncementActivity.class);
                intent.putExtra("position", position);
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
            }
        });
        TextView groupid=findViewById(R.id.groupid);
        TextView avatar=findViewById(R.id.avatar);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), UpdateGroupInfoActivity.class);
                intent.putExtra("position", position);
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
            }
        });
        TextView exit_group=findViewById(R.id.exit_group);
        exit_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog = ProgressDialog.show(v.getContext(), "提示：", "正在退出。。。");
                JMessageClient.exitGroup(groupList.get(position).getGroupId(), new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {
                        if (i == 0) {
                            mProgressDialog.dismiss();
                            groupList.remove(position);
                            groupAdapter.notifyDataSetChanged();
                            Intent intent=new Intent(getApplicationContext(), TypeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                            JMessageClient.deleteGroupConversation(groupList.get(position).getGroupId());
                            PushToast.getInstance().createToast("提示", "退出成功", null, true);
                            finish();
                        } else {

                            PushToast.getInstance().createToast("提示", "退出失败", null, false);
                            Log.i("ExitGroupActivity", "JMessageClient.exitGroup " + ", responseCode = " + i + " ; Desc = " + s);
                        }
                    }
                });

            }
        });
        groupid.setText(groupList.get(position).getGroupId()+"");
        gMemberList.clear();
        gMemberAdapter.notifyDataSetChanged();
        gMemberList.add(new personMsg("添加成员",
                BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.add_icon)
       ,groupList.get(position).getGroupId()));
        for (int j=0;j<GroupMemberList.size();j++){
            Bitmap bitmap;
            if (TextUtils.isEmpty(GroupMemberList.get(j).getAvatar())){
                bitmap=null;
            }else
                bitmap= BitmapFactory.decodeFile(GroupMemberList.get(j).getAvatarFile().getPath());
            gMemberList.add(new personMsg(
                    GroupMemberList.get(j).getNoDisturb(),
                    GroupMemberList.get(j).getNickname()
                    , GroupMemberList.get(j).getUserID()
                    , bitmap
                    , GroupMemberList.get(j).getUserName()
                    , GroupMemberList.get(j).getNotename()
                    , GroupMemberList.get(j).getAppKey()
                    , true
                    , GroupMemberList.get(j).getSignature()
                    , ""
                    , GroupMemberList.get(j).getSignature()
                    , GroupMemberList.get(j).getGender().toString()
                    , GroupMemberList.get(j).getAddress()
                    , GroupMemberList.get(j).getNoteText()
                    , GroupMemberList.get(j).getBirthday()
                    , GroupMemberList.get(j).getAvatarFile().getPath()
                    ,groupList.get(position).getGroupId()));
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.bt_group_approval_batch:
                intent.setClass(getApplicationContext(), HandleGroupApprovalInBatchActivity.class);
                break;
            case R.id.bt_group_member_nickname:
                intent.setClass(getApplicationContext(), GroupMemNicknameActivity.class);
                break;
            case R.id.bt_group_announcement:
                intent.setClass(getApplicationContext(), GroupAnnouncementActivity.class);
                break;
            case R.id.bt_group_black_list:
                intent.setClass(getApplicationContext(), GroupBlackListActivity.class);
                break;
        }
        startActivity(intent);
    }
    private void zoomInViewSize(int height) {
        View img1 = findViewById(R.id.statusbar);
        ViewGroup.LayoutParams  lp = img1.getLayoutParams();
        lp.height =height;
        img1.setLayoutParams(lp);
    }
    protected void onResume() {
        super.onResume();
    }

}
