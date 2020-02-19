package heath.com.test2_jmessage.application;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import cn.jpush.im.android.api.JMessageClient;
import heath.com.test2_jmessage.GlobalEventListener;

/**
 * Created by ${chenyn} on 16/3/22.
 *
 * @desc :
 */
public class IMDebugApplication extends Application {
    private  static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
        Log.i("IMDebugApplication", "init");
        JMessageClient.setDebugMode(true);
        JMessageClient.init(getApplicationContext(), true);
        //注册全局事件监听类
        JMessageClient.registerEventReceiver(new GlobalEventListener(getApplicationContext()));
    }
    public static Context getContext(){
        return  context;
    }
}
