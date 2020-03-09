package heath.com.test2_jmessage.tools;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import heath.com.test2_jmessage.R;

public class PushToast {
    private Activity mActivity;
    private static PushToast mInstance;
    private Toast mToast;
    private final int SHOW = 1;
    private final int HIDE = 0;
    private Object mTN;
    private Method mShow;
    private Method mHide;
    private Field mViewFeild;
    private long durationTime = 5*1000;

    public static PushToast getInstance() {
        if (mInstance == null) {
            mInstance = new PushToast();
        }
        return mInstance;
    }

    public void init(Context context) {
        mActivity = (Activity)context;
    }

    public void createToast(String title, String content, Map<String,String> params,boolean judgment) {
        if (mActivity == null) {
            return;
        }
        LayoutInflater inflater = mActivity.getLayoutInflater();//调用Activity的getLayoutInflater()
       //LayoutInflater inflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_push_toast, null); //加載layout下的布局
        LinearLayout llPushContent = (LinearLayout) view.findViewById(R.id.ll_push_content);
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
        TextView tvContent = (TextView) view.findViewById(R.id.tv_content);
        View wrong=view.findViewById(R.id.wrong);
        View completion=view.findViewById(R.id.completion);
        if (judgment)
        {
            wrong.setVisibility(View.GONE);
            completion.setVisibility(View.VISIBLE);
        }else {
            wrong.setVisibility(View.VISIBLE);
            completion.setVisibility(View.GONE);
        }
        tvTitle.setText(title);
        tvContent.setText(content);
        mToast = new Toast(mActivity);
        mToast.setView(view);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.TOP, 0, 0);
        reflectEnableClick();
        reflectToast();
        llPushContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        if(mShow != null && mHide != null){
            handler.sendEmptyMessage(SHOW);
        }else{
            mToast.show();
        }
    }

    private void reflectEnableClick() {
        try {
            Object mTN;
            mTN = getField(mToast, "mTN");
            if (mTN != null) {
                Object mParams = getField(mTN, "mParams");
                if (mParams != null
                        && mParams instanceof WindowManager.LayoutParams) {
                    WindowManager.LayoutParams params = (WindowManager.LayoutParams) mParams;
                    //显示与隐藏动画
                   params.windowAnimations = R.style.dialogWindowAnim;
                    //Toast可点击
                    params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                            | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                    //设置viewgroup宽高
                    params.width = WindowManager.LayoutParams.MATCH_PARENT; //设置Toast宽度为屏幕宽度
                    params.height = WindowManager.LayoutParams.WRAP_CONTENT; //设置高度

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 反射字段
     *
     * @param object    要反射的对象
     * @param fieldName 要反射的字段名称
     */
    private static Object getField(Object object, String fieldName)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = object.getClass().getDeclaredField(fieldName);
        if (field != null) {
            field.setAccessible(true);
            return field.get(object);
        }
        return null;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW:
                    handler.sendEmptyMessageDelayed(HIDE, durationTime);
                    show();
                    break;
                case HIDE:
                    hide();
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + msg.what);
            }
        }
    };

    public void reflectToast() {
        Field field = null;
        try {
            field = mToast.getClass().getDeclaredField("mTN");
            field.setAccessible(true);
            mTN = field.get(mToast);
            mShow = mTN.getClass().getDeclaredMethod("show");
            mHide = mTN.getClass().getDeclaredMethod("hide");
            mViewFeild = mTN.getClass().getDeclaredField("mNextView");
            mViewFeild.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e1) {
            e1.printStackTrace();
        }
    }

    public void show() {
        try {
            //android4.0以上就要以下处理
            if (Build.VERSION.SDK_INT > 14) {
                Field mNextViewField = mTN.getClass().getDeclaredField("mNextView");
                mNextViewField.setAccessible(true);
                LayoutInflater inflate = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v = mToast.getView();
                mNextViewField.set(mTN, v);
                Method method = mTN.getClass().getDeclaredMethod("show", null);
                method.invoke(mTN, null);
            }
            mShow.invoke(mTN, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hide() {
        try {
            mHide.invoke(mTN, null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }catch (NullPointerException ex){
            ex.printStackTrace();
        }
    }
}
