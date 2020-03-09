package heath.com.test2_jmessage.activity.friend;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import heath.com.test2_jmessage.R;
import heath.com.test2_jmessage.StatusBar.StatusBarUtil;
import heath.com.test2_jmessage.activity.TypeActivity;
import heath.com.test2_jmessage.adapter.AskAdapter;
import heath.com.test2_jmessage.recycleView_item.personMsg;

import static heath.com.test2_jmessage.activity.TypeActivity.myUserId;


public class FriendAskManage extends Activity {
    private TextView none;
    public static AskAdapter personAskAdapter;
    public static List<personMsg> personAskList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_friendask);
        zoomInViewSize(StatusBarUtil.getStatusBarHeight(this));
        none=findViewById(R.id.none);
        RecyclerView recyclerView=findViewById(R.id.type_recyclerview);
        personAskAdapter=new AskAdapter(personAskList);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(personAskAdapter);
        TextView manage_back=findViewById(R.id.manage_back);
        manage_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*SharedPreferences.Editor editor=getApplicationContext().
                        getSharedPreferences("friends"+myUserId,0).edit();
                editor.clear();
                editor.apply();
                personAskList.clear();
                personAskAdapter.notifyDataSetChanged();*/
                Intent intent=new Intent(getApplicationContext(), TypeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
            }
        });
        final SharedPreferences pref=getSharedPreferences("friends"+myUserId,0);
        String uniqueList= Objects.requireNonNull(pref.getString("uniqueList" + myUserId, " ")).replaceFirst("%%","");
        String [] unique=testA(uniqueList.split("%%"));
        String userName,appKey,reason,time,simplemessage;
        personAskList.clear();
            for (int i=0;i<unique.length;i++){
                userName=pref.getString("username"+ unique[i],null);
                appKey=pref.getString("appkey"+unique[i],null);
                reason=pref.getString("reason"+unique[i],null);
                time=pref.getString("time"+unique[i],null);
                simplemessage=pref.getString("simplemessage"+unique[i],null);
                if (userName!=null)
                    personAskList.add(new personMsg(null,userName,reason,appKey,time,simplemessage,unique[i]));
            }
            if (personAskList.size()==0){
                none.setVisibility(View.VISIBLE);
            }
    }
    protected void onResume() {
        super.onResume();

    }
    /**去除数组重复**/
    private String[] testA(String [] arrStr) {
        List<String> list = new ArrayList<String>();
        for (int i=0; i<arrStr.length; i++) {
            if(!list.contains(arrStr[i])) {
                list.add(arrStr[i]);
            }
        }
        String[] array=new String[list.size()];
        for(int i = 0; i < list.size();i++){
            array[i] = list.get(i);
        }
        return array;
    }
    protected void onDestroy() {
        Log.d("uniQue", "onDestroy: ");
        none.setVisibility(View.GONE);
        super.onDestroy();
    }
    private void zoomInViewSize(int height) {
        View img1 = findViewById(R.id.statusbar);
        ViewGroup.LayoutParams  lp = img1.getLayoutParams();
        lp.height =height;
        img1.setLayoutParams(lp);
    }
}
