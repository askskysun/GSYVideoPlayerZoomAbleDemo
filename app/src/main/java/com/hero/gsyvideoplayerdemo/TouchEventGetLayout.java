package com.hero.gsyvideoplayerdemo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 *   吧dispatchTouchEvent 事件监听暴露出来   用户获取不到 view的touch事件  从父类拦截
 */
public class TouchEventGetLayout extends FrameLayout {

     DispatchTouchEventListener dispatchTouchEventListener;

    public TouchEventGetLayout(@NonNull Context context) {
        super(context);
    }

    public TouchEventGetLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setDispatchTouchEventListener(DispatchTouchEventListener dispatchTouchEventListener) {
        this.dispatchTouchEventListener = dispatchTouchEventListener;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        dispatchTouchEventListener.dispatchTouch(ev);
        return super.dispatchTouchEvent(ev);
    }
}
