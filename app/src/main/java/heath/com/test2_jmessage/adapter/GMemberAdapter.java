package heath.com.test2_jmessage.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import heath.com.test2_jmessage.R;
import heath.com.test2_jmessage.recycleView_item.personMsg;


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
        personMsg msg=personMsgList.get(position);
        holder.name.setText(msg.getName());
        holder.gMavatar.setImageBitmap(msg.getAvatar());
        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
    public  int getItemCount(){
        return personMsgList.size();
    }

}
