package heath.com.test2_jmessage.LocalReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import heath.com.test2_jmessage.recycleView_item.Msg;
import heath.com.test2_jmessage.adapter.MsgAdapter;

public class Localreceiver extends BroadcastReceiver {
    private List<Msg> msgList;
    private RecyclerView msgRecyclerView;
    private MsgAdapter adapter;
    public Localreceiver(List<Msg> msgList, RecyclerView msgRecyclerView, MsgAdapter adapter){
        this.msgList=msgList;
        this.msgRecyclerView=msgRecyclerView;
        this.adapter=adapter;
    }
    public void onReceive(Context context, Intent intent) {

    }

}
