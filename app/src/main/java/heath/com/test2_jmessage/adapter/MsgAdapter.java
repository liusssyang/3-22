package heath.com.test2_jmessage.adapter;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import heath.com.test2_jmessage.LocalReceiver.Localreceiver;
import heath.com.test2_jmessage.Msg;
import heath.com.test2_jmessage.MyDialog.Mydialog;
import heath.com.test2_jmessage.R;


public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder>{
    private List<Msg> mMsgList;
    private Localreceiver localRceiver;
    private LocalBroadcastManager localBroadcastManager;
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
                Mydialog mydialog=new Mydialog(v.getContext());
                Window window=mydialog.getWindow();
                window.requestFeature(Window.FEATURE_NO_TITLE);
                mydialog.show();
            }
        });

        return  new ViewHolder(view);
    }

    public void onBindViewHolder(ViewHolder holder,int position){
        Msg msg=mMsgList.get(position);

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
    Bitmap drawableToBitmap(Drawable drawable) {

        Bitmap bitmap = Bitmap
                .createBitmap(
                        drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(),
                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        //canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
