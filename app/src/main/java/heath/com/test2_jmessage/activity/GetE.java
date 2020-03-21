package heath.com.test2_jmessage.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import heath.com.test2_jmessage.R;

public class GetE extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.get_e);
        TextView headusername=findViewById(R.id.manage_sigText_toolbarName);
        headusername.setText(getIntent().getStringExtra("UserName"));
        TextView tv_show_group_keeper=findViewById(R.id.tv_show_group_keeper);
        tv_show_group_keeper.setText(getIntent().getStringExtra("E"));
        TextView manage_back=findViewById(R.id.manage_back);
        manage_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
