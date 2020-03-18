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
import heath.com.test2_jmessage.activity.createmessage.CreateSigTextMessageActivity;
import heath.com.test2_jmessage.application.MyApplication;
import heath.com.test2_jmessage.recycleView_item.personMsg;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.ViewHolder>{
    private List<personMsg> personMsgList;
    static class ViewHolder extends RecyclerView.ViewHolder{
        View v;
        TextView leftMsg,simpleMessage,time;
        CircleImageView friendsIcon;

        public ViewHolder(View view){
            super(view);
            v=view;
            leftMsg=(TextView) view.findViewById(R.id.person);
            simpleMessage=view.findViewById(R.id.simple_message);
            time=view.findViewById(R.id.message_time);
            friendsIcon=view.findViewById(R.id.person_icon);
        }
    }
    public PersonAdapter(List<personMsg> msgList){
        personMsgList=msgList;
    }
    public  ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        View view= LayoutInflater.from(parent.getContext()).
                inflate(R.layout.person_item,parent,false);
        final ViewHolder holder=new ViewHolder(view);
        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=holder.getAdapterPosition();
                Intent intent = new Intent(MyApplication.getContext(), CreateSigTextMessageActivity.class);
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("showAllFromDatabases",false);
                intent.putExtra("position",position);
                MyApplication.getContext().startActivity(intent);

            }
        });
        return  holder;
    }

    public void onBindViewHolder(ViewHolder holder,int position){
        personMsg msg=personMsgList.get(position);
        holder.leftMsg.setText(msg.getName()+"("+msg.getUserName()+")");
        holder.simpleMessage.setText(msg.getSimpleMessage());
        holder.time.setText(msg.getTime());
        holder.friendsIcon.setImageBitmap(msg.getAvatar());
    }
    public  int getItemCount(){
        return personMsgList.size();
    }

}
