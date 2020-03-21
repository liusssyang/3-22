package heath.com.test2_jmessage.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dk.view.patheffect.PathTextView;
import com.google.gson.Gson;

import org.litepal.crud.DataSupport;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.RequestCallback;
import cn.jpush.im.android.api.model.DeviceInfo;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import de.hdodenhof.circleimageview.CircleImageView;
import heath.com.test2_jmessage.R;
import heath.com.test2_jmessage.StatusBar.StatusBarUtil;
import heath.com.test2_jmessage.activity.setting.RegisterActivity;
import heath.com.test2_jmessage.tools.LoginInformation;
import heath.com.test2_jmessage.tools.PushToast;
import heath.com.test2_jmessage.tools.tools;
import heath.com.test2_jmessage.utils.AndroidUtils;

import static heath.com.test2_jmessage.activity.TypeActivity.TAG;


/**
 * Created by ${chenyn} on 16/3/23.
 *
 * @desc : 注册和登陆界面
 */
public class RegisterAndLoginActivity extends Activity {
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PWD = "pwd";
    public static final int REQ_CODE_FOR_REGISTER = 1;
    public EditText mEd_userName;
    public EditText mEd_password;
    private Button mBt_login;
    private Button mBt_login_with_infos;
    LocalBroadcastManager localBroadcastManager;
    private Button mBt_gotoRegister;
    private ProgressDialog mProgressDialog = null;
    private RadioGroup mRgType;
    private boolean isTestVisibility = false;
    private TextView pwdIsVisible;
    private LocalRceiver localRceiver;
    private AutoCompleteTextView actv_main_at;
    private MultiAutoCompleteTextView mactv_main_m;
    private Spinner s_main_spinner;
    private ArrayAdapter<String> adapter;
    private static final String[] REQUIRED_PERMISSIONS =
            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!AndroidUtils.checkPermission(this, REQUIRED_PERMISSIONS)) {
            try {
                AndroidUtils.requestPermission(this, REQUIRED_PERMISSIONS);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        initView();
        initData();
        mEd_userName.setHint("Account");
        mEd_password.setHint("Password");
        mEd_userName.setText("");
    }

    @Override
    @TargetApi(23)
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (Build.VERSION.SDK_INT >= 23 && grantResults[0] == PackageManager.PERMISSION_DENIED && !shouldShowRequestPermissionRationale(permissions[0])) {
            Toast.makeText(getApplicationContext(), "请在设置中打开存储权限", Toast.LENGTH_SHORT).show();
        }
        initView();
        initData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE_FOR_REGISTER && RESULT_OK == resultCode && data != null) {
            mEd_userName.setText(data.getStringExtra(KEY_USERNAME));
            mEd_password.setText(data.getStringExtra(KEY_PWD));
        }

    }

    /**
     * #################    应用入口,登陆或者是注册    #################
     */
    private void initData() {
        /**=================     获取个人信息不是null，说明已经登陆，无需再次登陆，则直接进入type界面    =================*/
        UserInfo myInfo = JMessageClient.getMyInfo();
        final Intent intent = new Intent(RegisterAndLoginActivity.this, TypeActivity.class);
        if (myInfo != null) {
            startActivity(intent);
            finish();
        }
        /**=================     调用注册接口    =================*/
        mBt_gotoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), RegisterActivity.class);
                startActivityForResult(intent, REQ_CODE_FOR_REGISTER);
            }
        });
        mEd_userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                mEd_password.setText("");
            }});
        mBt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tools.getUserInfoList();
                tools.getGroupIdList();
                mProgressDialog = ProgressDialog.show(RegisterAndLoginActivity.this, "提示：", "正在加载中。。。");
                mProgressDialog.setCanceledOnTouchOutside(true);
                final String userName = mEd_userName.getText().toString();
                final String password = mEd_password.getText().toString();
                /**=================     调用SDk登陆接口    =================*/
     /***/           JMessageClient.login(userName, password, new BasicCallback() {
                    @Override
                    public void gotResult(int responseCode, String LoginDesc) {
                        if (responseCode == 0) {

                            mProgressDialog.dismiss();
                            PushToast.getInstance().createToast("提示","登陆成功",null,true);
                            Log.d(TAG, "gotResult: button_login");
                            final Intent intent = new Intent(RegisterAndLoginActivity.this, TypeActivity.class);
                            startActivity(intent);
                            DataSupport.deleteAll(LoginInformation.class,"account=?",userName);
                            LoginInformation loginInformation=new LoginInformation(userName,password);
                            loginInformation.save();
                            finish();

                        } else {
                            mProgressDialog.dismiss();
                            PushToast.getInstance().createToast("提示","登陆失败",null,false);
                            Log.i("MainActivity", "JMessageClient.login" + ", responseCode = " + responseCode + " ; LoginDesc = " + LoginDesc);
                        }
                    }
                });
            }
        });

        mBt_login_with_infos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog = ProgressDialog.show(RegisterAndLoginActivity.this, "提示：", "正在加载中。。。");
                mProgressDialog.setCanceledOnTouchOutside(true);
                String userName = mEd_userName.getText().toString();
                String password = mEd_password.getText().toString();
                /**=================     调用SDk登陆接口    =================*/
                JMessageClient.login(userName, password, new RequestCallback<List<DeviceInfo>>() {
                    @Override
                    public void gotResult(int responseCode, String responseMessage, List<DeviceInfo> result) {
                        if (responseCode == 0) {
                            mProgressDialog.dismiss();
                            PushToast.getInstance().createToast("提示","登陆成功",null,true);
                            Log.i("MainActivity", "JMessageClient.login" + ", responseCode = " + responseCode + " ; LoginDesc = " + responseMessage);
                            Intent intent = new Intent(getApplicationContext(), TypeActivity.class);
                            Gson gson = new Gson();
                            intent.putExtra("deviceInfos", gson.toJson(result));
                            startActivity(intent);
                            finish();
                        } else {
                            mProgressDialog.dismiss();
                            PushToast.getInstance().createToast("提示","登陆失败",null,false);
                            Log.i("MainActivity", "JMessageClient.login" + ", responseCode = " + responseCode + " ; LoginDesc = " + responseMessage);
                        }
                    }
                });
            }
        });

        //供jmessage sdk测试使用，开发者无需关心。
        mRgType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_public:
                        swapEnvironment(RegisterAndLoginActivity.this.getApplicationContext(), false, false);
                        break;
                    case R.id.rb_test:
                        swapEnvironment(RegisterAndLoginActivity.this.getApplicationContext(), true, false);
                        break;
                    case R.id.rb_qa:
                        swapEnvironment(RegisterAndLoginActivity.this.getApplicationContext(), false, true);
                        break;
                }
            }
        });
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("spinner_message");
        localRceiver = new LocalRceiver();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(localRceiver, intentFilter);
    }

    private void initView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_login);
        PushToast.getInstance().init(this);
        StatusBarUtil.setStatusBarColor(this, Color.parseColor("#3F51B5"));
        PathTextView mPathTextView = findViewById(R.id.path);
        mPathTextView.init("welcome");
        mPathTextView.setPaintType(PathTextView.Type.SINGLE);
        mPathTextView.setTextColor(Color.parseColor("#3F51B5"));
        //mPathTextView.setTextSize(size);
        mPathTextView.setTextWeight(5);
        mPathTextView.setDuration(4000);
        mPathTextView.setShadow(10, 10, 10, Color.parseColor("#00C4FF"));
        mEd_userName = (EditText) findViewById(R.id.ed_login_username);
        mEd_password = (EditText) findViewById(R.id.ed_login_password);
        mBt_login = (Button) findViewById(R.id.bt_login);
        mBt_login_with_infos = (Button) findViewById(R.id.bt_login_with_infos);
        mBt_gotoRegister = (Button) findViewById(R.id.bt_goto_regester);

        pwdIsVisible=findViewById(R.id.eye);
        pwdIsVisible.setText(" ");
        pwdIsVisible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pwdIsVisible.getText().toString().equals(" ")) {
                    mEd_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    mEd_password.setSelection(mEd_password.getText().toString().length());
                    pwdIsVisible.setText("");
                    pwdIsVisible.setBackgroundResource(R.drawable.eye_close);
                }else{
                    pwdIsVisible.setText(" ");
                    mEd_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    mEd_password.setSelection(mEd_password.getText().toString().length());
                    pwdIsVisible.setBackgroundResource(R.drawable.eye_open);
                }
            }
        });
        //addLayoutListener(re,mBt_login);
        mRgType = (RadioGroup) findViewById(R.id.rg_environment);;
        if (!isTestVisibility) {
            mRgType.setVisibility(View.GONE);
        } else {
            //供jmessage sdk测试使用，开发者无需关心。
            Boolean isTestEvn = invokeIsTestEvn();
            Boolean isQAEvn = invokeIsQAEvn();
            mRgType.check(R.id.rb_public);
            if (isTestEvn) {
                mRgType.check(R.id.rb_test);
            } else if (isQAEvn) {
                mRgType.check(R.id.rb_qa);
            }
        }
        adapter = new ArrayAdapter<String>(this, R.layout.login_item, R.id.account){
            @Override
            public View getDropDownView(final int position, View convertView,
                                        ViewGroup parent) {
                View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.login_item, null);
                CircleImageView circleImageView=view.findViewById(R.id.avatar);
                circleImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("131767", "onClick: "+position);
                    }
                });
                TextView textView=view.findViewById(R.id.account);
                textView.setText(adapter.getItem(position));
                return view;
            }

        };
        adapter.setDropDownViewResource( R.layout.login_item);
        s_main_spinner = (Spinner) findViewById(R.id.s_main_spinner);
        ImageView imageView=findViewById(R.id.spinner_but);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s_main_spinner.performClick();
            }
        });
        //给控件设置适配器
        s_main_spinner.setAdapter(adapter);
        List<LoginInformation> loginInformations=DataSupport.findAll(LoginInformation.class);
        if (loginInformations.size()==0){
            imageView.setEnabled(false);
        }
        for (LoginInformation loginInformation:loginInformations){
            adapter.add(loginInformation.getAccount());
        }
        s_main_spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                mEd_userName.setText(adapter.getItem(arg2));
                List<LoginInformation> loginInformations1=DataSupport.select("password")
                        .where("account=?",adapter.getItem(arg2)).find(LoginInformation.class);
                for (LoginInformation loginInformation:loginInformations1){
                    mEd_password.setText(loginInformation.getPassword());
                }
            }
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
    }

    public static String getAppKey(Context context) {
        Bundle metaData = null;
        String appkey = null;
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            } else {
                return null;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
        if (null != metaData) {
            appkey = metaData.getString("JPUSH_APPKEY");
            if (TextUtils.isEmpty(appkey)) {
                return null;
            } else if (appkey.length() != 24) {
                return null;
            }
            appkey = appkey.toLowerCase(Locale.getDefault());
        }
        return appkey;
    }

    public static Boolean invokeIsTestEvn() {
        try {
            Class cls = Class.forName("cn.jpush.im.android.api.EnvironmentManager");
            Method method = cls.getDeclaredMethod("isTestEnvironment");
            Object result = method.invoke(null);
            return (Boolean) result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static Boolean invokeIsQAEvn() {
        try {
            Class cls = Class.forName("cn.jpush.im.android.api.EnvironmentManager");
            Method method = cls.getDeclaredMethod("isQAEnvironment");
            Object result = method.invoke(null);
            return (Boolean) result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void swapEnvironment(Context context, boolean isTest, boolean isQA) {
        try {
            Class cls = Class.forName("cn.jpush.im.android.api.EnvironmentManager");
            Method method = cls.getDeclaredMethod("swapPublicEnvironment", Context.class);
            if (isTest) {
                method = cls.getDeclaredMethod("swapTestEnvironment", Context.class);
            } else if (isQA) {
                method = cls.getDeclaredMethod("swapQAEnvironment", Context.class);
            }
            method.invoke(null, context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void addLayoutListener(final View main,final View scroll){
        main.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Rect rect=new Rect();
                        main.getWindowVisibleDisplayFrame(rect);
                        int mainInvisibleHeight=main.getRootView().getHeight()-rect.bottom;
                        if (mainInvisibleHeight>100){
                            int[] location=new int[2];
                            scroll.getLocationInWindow(location);
                            int srollHeight=
                                    (location[1]+scroll.getHeight())-rect.bottom;
                            main.scrollTo(0,srollHeight);
                        }else{
                            main.scrollTo(0,0);
                        }

                    }
                }
        );
    }
    class LocalRceiver extends BroadcastReceiver {
        public LocalRceiver() { }
        public void onReceive(Context context, Intent intent) {
            mEd_userName.setText(intent.getStringExtra("account"));

        }
    }
    public void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(localRceiver);
    }
}
