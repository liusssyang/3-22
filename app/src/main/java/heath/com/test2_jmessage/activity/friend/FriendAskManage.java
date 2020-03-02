package heath.com.test2_jmessage.activity.friend;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

import heath.com.test2_jmessage.R;

public class FriendAskManage extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_managefriends);
    }
}
