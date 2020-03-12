package heath.com.test2_jmessage.application;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import heath.com.test2_jmessage.GlobalEventListener;
import heath.com.test2_jmessage.recycleView_item.personMsg;
import heath.com.test2_jmessage.tools.tools;

/**
 * Created by ${chenyn} on 16/3/22.
 *
 * @desc :
 */
public class MyApplication extends Application {
    private  static Context context;
    private final String TAG="MyApplication";
    public static boolean isAvaluable=false;
    public static List<UserInfo> list;
    public static StringBuilder sb= new StringBuilder();
    public static List<personMsg> personList=new ArrayList<>();
    public static int getNoDisturbToMyselfResult=0;

    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
        JMessageClient.setDebugMode(true);
        JMessageClient.init(getApplicationContext(), true);
        /**注册全局事件监听类*/
        JMessageClient.registerEventReceiver(new GlobalEventListener(getApplicationContext()));
        /**##################################*/
        tools.getUserInfoList();
        tools.getNoDisturbToMyself();
        Log.i("MyApplication", isAvaluable+"");

    }
    @Override
    public void onTerminate() {
        // 程序终止的时候执行
        Log.i("MyApplication", isAvaluable+"");
        personList.clear();
        super.onTerminate();
    }
    @Override
    public void onLowMemory() {
        // 低内存的时候执行

        Log.d(TAG, "onLowMemory");
        super.onLowMemory();
    }
    @Override
    public void onTrimMemory(int level) {
        // 程序在内存清理的时候执行（回收内存）
        // HOME键退出应用程序、长按MENU键，打开Recent TASK都会执行
        Log.i("MyApplication", isAvaluable+"");
        super.onTrimMemory(level);
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(TAG, "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
    }
    public static Context getContext(){
        return  context;
    }
}
