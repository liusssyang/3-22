package heath.com.test2_jmessage.activity.friend;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.jpush.im.android.api.ContactManager;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.callback.GetUserInfoListCallback;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import de.hdodenhof.circleimageview.CircleImageView;
import heath.com.test2_jmessage.R;
import heath.com.test2_jmessage.StatusBar.StatusBarUtil;
import heath.com.test2_jmessage.activity.TypeActivity;
import heath.com.test2_jmessage.activity.conversation.DeleteConversationActivity;
import heath.com.test2_jmessage.activity.createmessage.CreateSigTextMessageActivity;
import heath.com.test2_jmessage.application.MyApplication;
import heath.com.test2_jmessage.tools.PushToast;
import heath.com.test2_jmessage.tools.tools;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static heath.com.test2_jmessage.activity.TypeActivity.adapter;
import static heath.com.test2_jmessage.application.MyApplication.personList;


public class ManageFriendActivity extends Activity {
    private int position;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_managefriends);
        position = getIntent().getIntExtra("position", -1);
        userId = personList.get(position).getUserId();
        zoomInViewSize(StatusBarUtil.getStatusBarHeight(this));
        TextView manage_back = findViewById(R.id.manage_back);
        manage_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyApplication.getContext(), CreateSigTextMessageActivity.class);
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("name", personList.get(position).getUserName());
                intent.putExtra("note_name", personList.get(position).getName());
                intent.putExtra("position", position);
                intent.putExtra("userId", personList.get(position).getUserId());
                MyApplication.getContext().startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
            }
        });
        final CircleImageView manage_person_icon = findViewById(R.id.manage_person_icon);
        final TextView manage_Name = findViewById(R.id.manage_Name);
        final TextView manage_simple_message = findViewById(R.id.manage_simple_message);
        RelativeLayout personal_information = findViewById(R.id.personal_information);
        TextView manage_noteFriends = findViewById(R.id.manage_noteFriends);
        TextView manage_HistoryFriends = findViewById(R.id.manage_HistoryFriends);
        manage_HistoryFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DeleteConversationActivity.class);
                intent.putExtra("position", position);
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
            }
        });
        manage_noteFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NoteFriend.class);
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                if (position >= 0) {
                    intent.putExtra("position", position);
                }
                if (userId != 0)
                    intent.putExtra("userId", userId);
                getApplicationContext().startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
            }
        });
        if (position >= 0) {
            if (!TextUtils.isEmpty(personList.get(position).getNickname())) {
                manage_Name.setText(personList.get(position).getNickname());
                manage_Name.append("(" + personList.get(position).getUserName() + ")");
            } else
                manage_Name.append(personList.get(position).getUserName());
            manage_simple_message.setText(tools.Age(personList.get(position).getMillisecond()) + "岁");
            if (!TextUtils.isEmpty(personList.get(position).getGender())) {
                manage_simple_message.append("|" + personList.get(position).getGender());
            }
        }
        if (personList.get(position).getAvatar() == null) {
            ContactManager.getFriendList(
                    new GetUserInfoListCallback() {
                        @Override
                        public void gotResult(int i, String s, List<UserInfo> list) {
                            if (list.size() == 0) {
                                Toast.makeText(getApplicationContext(), "没有好友", Toast.LENGTH_SHORT).show();
                            }
                            for (int j = 0; j < list.size(); j++) {
                                if (list.get(j).getUserID() == userId) {
                                    list.get(j).getAvatarBitmap(new GetAvatarBitmapCallback() {
                                        @Override
                                        public void gotResult(int i, String s, Bitmap bitmap) {
                                            if (i == 0) {
                                                manage_person_icon.setImageBitmap(bitmap);
                                                personList.get(position).setAvatar(bitmap);
                                                personList.get(position).setAvatar(bitmap);
                                                adapter.notifyDataSetChanged();
                                            }
                                        }
                                    });
                                }
                                break;
                            }
                        }
                    });
        } else
            manage_person_icon.setImageBitmap(personList.get(position).getAvatar());
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
        TextView manage_friend_delete = findViewById(R.id.manage_friend_delete);
        manage_friend_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactManager.getFriendList(
                        new GetUserInfoListCallback() {
                            @Override
                            public void gotResult(int i, String s, List<UserInfo> list) {
                                if (i == 0) {
                                    if (list.size() == 0) {
                                        Toast.makeText(getApplicationContext(), "没有好友", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    for (int j = 0; j < list.size(); j++) {
                                        if (list.get(j).getUserID() == userId) {
                                            final UserInfo info = list.get(j);
                                            info.removeFromFriendList(new BasicCallback() {
                                                @Override
                                                public void gotResult(int i, String s) {
                                                    if (i == 0) {
                                                        personList.remove(position);
                                                        adapter.notifyDataSetChanged();
                                                        Intent intent = new Intent(MyApplication.getContext(), TypeActivity.class);
                                                        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                                                        MyApplication.getContext().startActivity(intent);
                                                        Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(getApplicationContext(), "删除失败", Toast.LENGTH_SHORT).show();
                                                        Log.i("FriendContactManager", "UserInfo.removeFromFriendList" + ", responseCode = " + i + " ; LoginDesc = " + s);
                                                    }

                                                }
                                            });
                                            break;
                                        }
                                    }
                                } else
                                    Toast.makeText(getApplicationContext(), "获取失败", Toast.LENGTH_SHORT).show();
                            }

                        }
                );
            }
        });
        note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NoteFriend.class);
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                if (position >= 0) {
                    intent.putExtra("position", position);
                }
                if (userId != 0)
                    intent.putExtra("userId", userId);
                getApplicationContext().startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
            }

        });
        username.setText(personList.get(position).getUserName());
        birthday.setText(personList.get(position).getBirthday());
        signature.setText(personList.get(position).getSignature());
        adress.setText(personList.get(position).getAddress());
        other.setText(personList.get(position).getOther());
        UserId.setText(personList.get(position).getUserId() + "");
        note.setText(personList.get(position).getName());
        AppKey.setText(personList.get(position).getAppkey());
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
        final Switch no_disturb = findViewById(R.id.no_disturb);
        if (personList.get(position).getIsDistubed()) {
            no_disturb.setChecked(true);
        } else {
            no_disturb.setChecked(false);
        }
        no_disturb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    personList.get(position).setIsDistubed(1);
                    tools.setNoDisturb(no_disturb.isChecked(),no_disturb,1, personList.get(position).getUserName(), personList.get(position).getAppkey());
                } else {
                    personList.get(position).setIsDistubed(0);
                    tools.setNoDisturb(no_disturb.isChecked(),no_disturb,0, personList.get(position).getUserName(), personList.get(position).getAppkey());
                }
            }
        });
        TextView manage_message_delete = findViewById(R.id.manage_message_delete);
        manage_message_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (JMessageClient.deleteSingleConversation(personList.get(position).getUserName())) {
                    adapter.notifyDataSetChanged();
                    PushToast.getInstance().createToast("提示", "删除成功", null, true);
                } else PushToast.getInstance().createToast("提示", "操作失败", null, false);

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
