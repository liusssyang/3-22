package heath.com.test2_jmessage.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import heath.com.test2_jmessage.R;
import heath.com.test2_jmessage.activity.groupinfo.AddRemoveGroupMemberActivity;
import heath.com.test2_jmessage.activity.groupinfo.groupMemManage;
import heath.com.test2_jmessage.recycleView_item.personMsg;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static cn.jpush.im.android.api.jmrtc.JMRTCInternalUse.getApplicationContext;


public class GMemberAdapter extends RecyclerView.Adapter<GMemberAdapter.ViewHolder>{
    private List<personMsg> personMsgList;
    static class ViewHolder extends RecyclerView.ViewHolder{
        View v;
        TextView name;
        CircleImageView gMavatar;

        public ViewHolder(View view){
            super(view);
            v=view;
            gMavatar=v.findViewById(R.id.member_avatar);
            name=v.findViewById(R.id.name);
        }
    }
    public  GMemberAdapter(List<personMsg> msgList){
        personMsgList=msgList;
    }
    public  ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        View view= LayoutInflater.from(parent.getContext()).
                inflate(R.layout.group_member,parent,false);
        final ViewHolder holder=new ViewHolder(view);

        return  holder;
    }

    public void onBindViewHolder(final ViewHolder holder, final int position){
        final personMsg msg=personMsgList.get(position);
        holder.name.setText(msg.getName());
        holder.gMavatar.setImageBitmap(msg.getAvatar());
        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (msg.getUserName().equals("添加成员")){
                    Intent intent = new Intent(getApplicationContext(),  AddRemoveGroupMemberActivity.class);
                    intent.putExtra("groupId",msg.getGroupId());
                    getApplicationContext().startActivity(intent);
                }else{
                    Intent intent = new Intent(getApplicationContext(), groupMemManage.class);
                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("position",position);
                    getApplicationContext().startActivity(intent);
                }

            }
        });
    }
    public  int getItemCount(){
        return personMsgList.size();
    }

}
