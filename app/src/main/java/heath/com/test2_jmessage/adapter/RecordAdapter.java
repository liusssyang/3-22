package heath.com.test2_jmessage.adapter;

import android.content.Intent;
import android.text.Html;
import android.util.Log;
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
import heath.com.test2_jmessage.tools.tools;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder>{
    private List<personMsg> personMsgList;
    static class ViewHolder extends RecyclerView.ViewHolder{
        View v;
        TextView leftMsg,simpleMessage,time,time2;
        CircleImageView friendsIcon;

        public ViewHolder(View view){
            super(view);
            v=view;
            leftMsg=(TextView) view.findViewById(R.id.person);
            simpleMessage=view.findViewById(R.id.simple_message);
            time=view.findViewById(R.id.time);
            time2=view.findViewById(R.id.message_time);
            time2.setVisibility(View.GONE);
            friendsIcon=view.findViewById(R.id.person_icon);

        }
    }
    public  RecordAdapter(List<personMsg> msgList){
        personMsgList=msgList;
    }
    public  ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        View view= LayoutInflater.from(parent.getContext()).
                inflate(R.layout.person_item,parent,false);
        final ViewHolder holder=new ViewHolder(view);

        return  holder;
    }

    public void onBindViewHolder(final ViewHolder holder, final int position){
        personMsg msg=personMsgList.get(position);
        holder.leftMsg.setText(msg.getName()+"("+msg.getUserName()+")");
        holder.simpleMessage.setText(Html.fromHtml(msg.getSimpleMessage()));
        holder.time.setVisibility(View.VISIBLE);
        holder.time.setText(tools.secondToDate(msg.getCreateMillisecond(),"MM/dd HH:mm"));
        holder.friendsIcon.setImageBitmap(msg.getAvatar());
        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //int position=holder.getAdapterPosition();
                Intent intent = new Intent(MyApplication.getContext(), CreateSigTextMessageActivity.class);
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                int position2= tools.getPosition(personMsgList.get(position).getUserName(),
                        personMsgList.get(position).getAppkey());
                intent.putExtra("position", position2);
                intent.putExtra("showAllFromDatabases",true);
                intent.putExtra("msgNumber",personMsgList.get(position).getMsgNumber());
                Log.d("position", position+"onClick: "+personMsgList.get(position).getMsgNumber());
                MyApplication.getContext().startActivity(intent);
            }
        });
    }
    public  int getItemCount(){
        return personMsgList.size();
    }

}
