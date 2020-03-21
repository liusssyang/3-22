package heath.com.test2_jmessage.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import de.hdodenhof.circleimageview.CircleImageView;
import heath.com.test2_jmessage.R;
import heath.com.test2_jmessage.recycleView_item.personMsg;

public class LoginAdapter extends BaseAdapter {
    private List<personMsg> mList;
    private Context mContext;
    private LocalBroadcastManager localBroadcastManager;


    public LoginAdapter(Context pContext, List<personMsg> pList) {
        this.mContext = pContext;
        this.mList = pList;
        localBroadcastManager=LocalBroadcastManager.getInstance(pContext);
        Log.d("13132", "LoginAdapter: ");
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    /**
     * 下面是重要代码
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final personMsg msg=mList.get(position);

        LayoutInflater _LayoutInflater=LayoutInflater.from(mContext);
        convertView=_LayoutInflater.inflate(R.layout.login_item, null);
        if(convertView!=null){
            TextView textView=(TextView)convertView.findViewById(R.id.account);
            textView.setText(msg.getUserName());
            CircleImageView circleImageView=convertView.findViewById(R.id.avatar);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent("spinner_message");
                intent.putExtra("account",msg.getUserName());
                localBroadcastManager.sendBroadcast(intent);
            }
        });
        convertView.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("13179",position+"");
            }
        });
        return convertView;
    }
    public View getDropDownView(int position, View convertView, ViewGroup parent){
        View view = LayoutInflater.from(mContext).inflate(R.layout.login_item, null);

        return view;
    }
}

