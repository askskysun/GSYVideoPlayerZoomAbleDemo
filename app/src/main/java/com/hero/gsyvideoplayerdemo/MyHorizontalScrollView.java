package com.hero.gsyvideoplayerdemo;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

public class MyHorizontalScrollView extends HorizontalScrollView {

    private static final String TAG = "MyHorizontalScrollView";

    private Handler mHandler;

    public void setmScrollViewListener(ScrollViewListener mScrollViewListener) {
        this.mScrollViewListener = mScrollViewListener;
    }

    private ScrollViewListener mScrollViewListener;

    /**
     * 记录当前滚动的距离
     */
    private int currentX = -9999999;
    public interface ScrollViewListener {
        void onScrollChanged(int scrollSeek);
    }

    public MyHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHandler = new Handler();
    }

    /**
     * 滚动监听runnable
     */
    private Runnable scrollRunnable = new Runnable() {
        @Override
        public void run() {
            if (getScrollX() == currentX) {
                //滚动停止,取消监听线程
                mHandler.removeCallbacks(this);
                return;
            }
            currentX = getScrollX();
            if (mScrollViewListener != null) {
                mScrollViewListener.onScrollChanged(currentX);
            }
            //滚动监听间隔:milliseconds
            mHandler.postDelayed(this, 50);
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                mHandler.post(scrollRunnable);
                break;
            case MotionEvent.ACTION_UP:
                mHandler.removeCallbacks(scrollRunnable);
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 阻尼：1000为将惯性滚动速度缩小1000倍，近似drag操作。
     */
    @Override
    public void fling(int velocity) {
        super.fling(velocity / 10000);
    }

}