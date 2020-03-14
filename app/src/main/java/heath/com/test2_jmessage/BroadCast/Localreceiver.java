package heath.com.test2_jmessage.BroadCast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.io.Serializable;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import cn.jpush.im.android.api.model.Message;
import heath.com.test2_jmessage.adapter.MsgAdapter;
import heath.com.test2_jmessage.recycleView_item.Msg;
import heath.com.test2_jmessage.tools.DataBean;
import heath.com.test2_jmessage.tools.tools;

import static heath.com.test2_jmessage.application.MyApplication.personList;

public class Localreceiver extends BroadcastReceiver {
    private List<Msg> msgList;
    private RecyclerView msgRecyclerView;
    private MsgAdapter adapter;
    private int position;

    public Localreceiver(int position,List<Msg> msgList, RecyclerView msgRecyclerView, MsgAdapter adapter) {
        this.msgList = msgList;
        this.msgRecyclerView = msgRecyclerView;
        this.adapter = adapter;
        this.position=position;
    }

    public void onReceive(Context context, Intent intent) {
        Serializable se = intent.getSerializableExtra("BeanData");
        if (se instanceof DataBean && intent.getLongExtra("userId", 0) ==personList.get(position).getUserId()) {
            DataBean db = (DataBean) se;
            Message message = db.getMessage();
            msgList.add(new Msg(message));
            adapter.notifyItemInserted(msgList.size() - 1);
            msgRecyclerView.scrollToPosition(msgList.size() - 1);

        }
    }

}
