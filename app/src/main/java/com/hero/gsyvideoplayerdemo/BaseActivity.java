package com.hero.gsyvideoplayerdemo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Field;


public abstract class BaseActivity extends FragmentActivity implements HandlerUtils.OnReceiveMessageListener {
    public TextView textView;
    protected BaseActivity self;
    public String TAG = "";
    private HandlerUtils.HandlerHolder handlerHolder;

    /**
     * 初始化ui
     */
    public abstract void initUI();

    /**
     * 初始化Controller
     */
    public void initController() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //需要在4.4版本以上执行
        if (android.os.Build.VERSION.SDK_INT > 18) {
            Window window = getWindow();
//设置StatusBar为透明显示,需要在setContentView之前完成操作
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        //handler
        handlerHolder = new HandlerUtils.HandlerHolder(this);
        TAG = this.getClass().getSimpleName();
        self = this;
    }
    public void setHandlerPostDelayed(Runnable r, long delayMillis) {
        if (handlerHolder != null) {
            handlerHolder.postDelayed(r , delayMillis);
        }
    }
        // 通过反射机制获取手机状态栏高度
    private int getStatusBarHeight() {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }

    public void setStatusBarAfter(String colorString) {
        // 创建TextView用于叠加StatusBar的颜色块
        textView = new TextView(this);
        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, getStatusBarHeight());
        textView.setBackgroundColor(Color.parseColor(colorString));
        textView.setLayoutParams(lParams);
        // 获得根视图并把TextView加进去。
        ViewGroup view = (ViewGroup) getWindow().getDecorView();
        view.addView(textView);
    }

    /**
     * 打开新的Activity
     **/
    public void changeActivity(Class<?> cls, Bundle bundle) {
        Intent addAccountIntent = new Intent();
        //addAccountIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        if (bundle != null) {
            addAccountIntent.putExtras(bundle);
        }
        addAccountIntent.setClass(getBaseContext(), cls);
        startActivity(addAccountIntent);
    }

    /**
     * 通过动画打开新的Activty
     **/
    public void changeActivityByAnimation(Class<?> cls, int enterAnim, int exitAnim, Bundle bundle) {
        changeActivity(cls, bundle);
        overridePendingTransition(enterAnim, exitAnim);
    }

    /**
     * 打开一个有返回值的Activity
     **/
    public void changActivityForResult(Class<?> cls, int resule_code, Bundle bundle) {
        Intent addAccountIntent = new Intent();
        addAccountIntent.setFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);

        if (bundle != null)
            addAccountIntent.putExtras(bundle);

        addAccountIntent.setClass(getBaseContext(), cls);
        startActivityForResult(addAccountIntent, resule_code);
    }

    /**
     * 通过动画打开有返回值的Activity
     **/
    public void changActivityForResultByAnimation(Class<?> cls, int enterAnim, int exitAnim, int resule_code, Bundle bundle) {
        Intent addAccountIntent = new Intent();
        addAccountIntent.setFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);

        if (bundle != null)
            addAccountIntent.putExtras(bundle);

        addAccountIntent.setClass(getBaseContext(), cls);
        startActivityForResult(addAccountIntent, resule_code);

        overridePendingTransition(enterAnim, exitAnim);
    }

    /**
     * 打开新的Activity后结束本身
     **/
    public void changeActivityAndFinish(Class<?> cls, Bundle bundle) {
        changeActivity(cls, bundle);
        finish();
    }

    /**
     * 通过动画打开新的Activity后结束本身
     **/
    public void changeActivityByAnimationAndFinish(Class<?> cls, int enterAnim, int exitAnim, Bundle bundle) {
        changeActivityByAnimation(cls, enterAnim, exitAnim, bundle);
        finish();
    }

    @Override
    public void handlerMessage(Message msg) {

    }
}
