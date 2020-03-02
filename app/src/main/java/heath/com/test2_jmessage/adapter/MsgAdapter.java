package heath.com.test2_jmessage.adapter;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import heath.com.test2_jmessage.MyDialog.Mydialog;
import heath.com.test2_jmessage.R;
import heath.com.test2_jmessage.activity.TypeActivity;
import heath.com.test2_jmessage.recycleView_item.Msg;

//import heath.com.test2_jmessage.LocalReceiver.Localreceiver;


public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder>{
    private List<Msg> mMsgList;
    static class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout leftLayout;
        LinearLayout rightLayout;
        TextView leftMsg;
        TextView rightMsg;
        ImageView leftImg;
        ImageView rightImg;
        CircleImageView lefticon;
        CircleImageView righticon;
        public ViewHolder(View view){
            super(view);
            View itemview=view;
            leftLayout=(LinearLayout)view.findViewById(R.id.left_layout);
            rightLayout=(LinearLayout)view.findViewById(R.id.right_layout);
            leftMsg=view.findViewById(R.id.left_msg);
            rightMsg=view.findViewById(R.id.right_msg);
            leftImg=view.findViewById(R.id.left_img);
            rightImg=view.findViewById(R.id.right_img);
            lefticon=view.findViewById(R.id.iconleft);
            righticon=view.findViewById(R.id.iconright);
        }
    }
    public  MsgAdapter(List<Msg> msgList){
        mMsgList=msgList;
    }
    public  ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){

        View view= LayoutInflater.from(parent.getContext()).
                inflate(R.layout.msg_item,parent,false);
        final ViewHolder holder=new ViewHolder(view);
        holder.leftImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Mydialog mydialog=new Mydialog(v.getContext(),R.style.MyDialogStyle);
                mydialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                Window dialogWindow = mydialog.getWindow();
                dialogWindow.setGravity(Gravity.BOTTOM);
                dialogWindow.getDecorView().setPadding(0, 0, 0, 0);
                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialogWindow.setAttributes(lp);
                mydialog.show();
            }
        });

        return  new ViewHolder(view);
    }

    public void onBindViewHolder(ViewHolder holder,int position){
        Msg msg=mMsgList.get(position);
        holder.lefticon.setImageBitmap(msg.getIconContent());
        holder.righticon.setImageBitmap(TypeActivity.myIcon);
        if (msg.getType() == Msg.TYPE_RECEIVED) {
            if (msg.getImageContent()!=null||msg.getContent()==null)
                holder.leftLayout.setBackgroundColor(Color.parseColor("#00000000"));
            else
                holder.leftLayout.setBackgroundResource(R.drawable.left);
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.lefticon.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.righticon.setVisibility(View.GONE);
            holder.leftMsg.setText(msg.getContent());
            holder.leftImg.setImageBitmap(msg.getImageContent());
        }else if (msg.getType() == Msg.TYPE_SENT) {
            if (msg.getImageContent()!=null||msg.getContent()==null)
                holder.leftLayout.setBackgroundColor(Color.parseColor("#00000000"));
            else
                holder.leftLayout.setBackgroundResource(R.drawable.right);
            holder.leftLayout.setVisibility(View.GONE);
            holder.lefticon.setVisibility(View.GONE);
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.righticon.setVisibility(View.VISIBLE);
            holder.rightMsg.setText(msg.getContent());
            holder.rightImg.setImageBitmap(msg.getImageContent());
        }
    }
    public  int getItemCount(){
        return mMsgList.size();
    }
}
