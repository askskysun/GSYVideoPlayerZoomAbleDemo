package com.hero.gsyvideoplayerdemo.gsyjikplayer;

import android.annotation.TargetApi;
import android.content.pm.ActivityInfo;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewCompat;
import android.transition.Transition;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hero.gsyvideoplayerdemo.BaseActivity;
import com.hero.gsyvideoplayerdemo.CustomFrameLayout;
import com.hero.gsyvideoplayerdemo.DispatchTouchEventListener;
import com.hero.gsyvideoplayerdemo.OnTransitionListener;
import com.hero.gsyvideoplayerdemo.OrientationUtils;
import com.hero.gsyvideoplayerdemo.R;
import com.hero.gsyvideoplayerdemo.SwitchVideoModel;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements DispatchTouchEventListener {
    public final static String IMG_TRANSITION = "IMG_TRANSITION";
    public final static String TRANSITION = "TRANSITION";

    @BindView(R.id.video_player)
    SampleVideo videoPlayer;
    @BindView(R.id.activity_play)
    RelativeLayout activity_play;

    @BindView(R.id.frm)
    CustomFrameLayout frm;

    OrientationUtils orientationUtils;

    private boolean isTransition;

    private Transition transition;
    TouchListener mTouchListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        isTransition = getIntent().getBooleanExtra(TRANSITION, false);

        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(true)  // (Optional) Whether to show thread info or not. Default true
                .methodCount(5)         // (Optional) How many method line to show. Default 2        //  方法栈打印的个数，默认是2
                .methodOffset(0)        // (Optional) Skips some method invokes in stack trace. Default 5     //  设置调用堆栈的函数偏移值，0的话则从打印该Log的函数开始输出堆栈信息，默认是0
//        .logStrategy(customLog) // (Optional) Changes the log strategy to print out. Default LogCat
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
        frm.setDispatchTouchEventListener(this);
        mTouchListener = new TouchListener();
        init();
    }

    private void init() {
        String url = "https://res.exexm.com/cw_145225549855002";

        //String url = "http://7xse1z.com1.z0.glb.clouddn.com/1491813192";
        //需要路径的
        //videoPlayer.setUp(url, true, new File(FileUtils.getPath()), "");

        //借用了jjdxm_ijkplayer的URL
//        String source1 = "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4";
//        String source1 = "http://1253492636.vod2.myqcloud.com/2e5fc148vodgzp1253492636/677f7ef57447398154657427328/cuM4k64ZGGQA.mp4";
//        String source1 = Environment.getExternalStorageDirectory() + "/record_20180207171537-1.mp4";
        String source1 = Environment.getExternalStorageDirectory() + "/test.mp4";
        String name = "普通";
        SwitchVideoModel switchVideoModel = new SwitchVideoModel(name, source1);

//        String source2 = "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f30.mp4";
//        String name2 = "清晰";
//        SwitchVideoModel switchVideoModel2 = new SwitchVideoModel(name2, source2);

        List<SwitchVideoModel> list = new ArrayList<>();
        list.add(switchVideoModel);
//        list.add(switchVideoModel2);
        videoPlayer.setUp(list, true, "病例视频");

        //增加封面
        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        //todo   加载封面
        SwitchVideoModel switchVideoModel1 = list.get(0);
        Glide.with(this)
                .setDefaultRequestOptions(
                        new RequestOptions()
                                .frame(500)
                                .dontAnimate()
                                .centerCrop())
                .load(switchVideoModel1.getUrl())
                .into(imageView);


        videoPlayer.setThumbImageView(imageView);
        //增加title
        videoPlayer.getTitleTextView().setVisibility(View.GONE);
        //videoPlayer.setShowPauseCover(false);
        //videoPlayer.setSpeed(2f);
        //设置返回键
        videoPlayer.getBackButton().setVisibility(View.VISIBLE);
        //设置旋转
        orientationUtils = new OrientationUtils(this, videoPlayer);
        //设置全屏按键功能,这是使用的是选择屏幕，而不是全屏
        videoPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orientationUtils.resolveByClick();
            }
        });

        //videoPlayer.setBottomProgressBarDrawable(getResources().getDrawable(R.drawable.video_new_progress));
        //videoPlayer.setDialogVolumeProgressBar(getResources().getDrawable(R.drawable.video_new_volume_progress_bg));
        //videoPlayer.setDialogProgressBar(getResources().getDrawable(R.drawable.video_new_progress));
        //videoPlayer.setBottomShowProgressBarDrawable(getResources().getDrawable(R.drawable.video_new_seekbar_progress),
        //getResources().getDrawable(R.drawable.video_new_seekbar_thumb));
        //videoPlayer.setDialogProgressColor(getResources().getColor(R.color.colorAccent), -11);

        //是否可以滑动调整
        videoPlayer.setIsTouchWiget(false);

        //设置返回按键功能
        videoPlayer.getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        /**
         *  设置播放速度
         */
        //   public void setSpeed(float speed)

        /**
         * 播放速度
         *
         * @param speed      速度
         * @param soundTouch 是否对6.0下开启变速不变调
         */
//        public void setSpeed(float speed, boolean soundTouch)
        /**
         * 播放中生效的播放数据
         *
         * @param speed
         * @param soundTouch
         */
//        public void setSpeedPlaying(float speed, boolean soundTouch)

        /**
         * 长时间失去音频焦点，暂停播放器
         *
         * @param releaseWhenLossAudio 默认true，false的时候只会暂停
         */
        videoPlayer.setReleaseWhenLossAudio(false);
        //过渡动画
        initTransition();
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoPlayer.onVideoPause();
    }

    @Override
    public void initUI() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        videoPlayer.onVideoResume();
        videoPlayer.setPreView4();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (orientationUtils != null)
            orientationUtils.releaseListener();
    }

    @Override
    public void onBackPressed() {
        //先返回正常状态
        if (orientationUtils.getScreenType() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            videoPlayer.getFullscreenButton().performClick();
            return;
        }
        //释放所有
//        videoPlayer.setStandardVideoAllCallBack(null);
//        GSYVideoPlayer.releaseAllVideos();
        if (isTransition && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            super.onBackPressed();
        } else {
            setHandlerPostDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                    overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                }
            } , 500);
        }
    }


    private void initTransition() {
        if (isTransition && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition();
            ViewCompat.setTransitionName(videoPlayer, IMG_TRANSITION);
            addTransitionListener();
            startPostponedEnterTransition();
        } else {
            videoPlayer.startPlayLogic();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private boolean addTransitionListener() {
        transition = getWindow().getSharedElementEnterTransition();
        if (transition != null) {
            transition.addListener(new OnTransitionListener() {
                @Override
                public void onTransitionEnd(Transition transition) {
                    super.onTransitionEnd(transition);
                    videoPlayer.startPlayLogic();
                    transition.removeListener(this);
                }
            });
            return true;
        }
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void dispatchTouch(MotionEvent ev) {
        mTouchListener.onTouch(videoPlayer, ev);
    }

    /**
     * 一旦触碰  会回到原始位置   修改为  currentMatrix.set(matrix);  不用获取
     * currentMatrix.set(imageView.getImageMatrix());
     */
    private float[] finalTransformation = new float[9];

    private final class TouchListener implements View.OnTouchListener {

        /**
         * 记录是拖拉照片模式还是放大缩小照片模式
         */
        private int mode = 0;// 初始状态
        /**
         * 拖拉照片模式
         */
        private static final int MODE_DRAG = 1;
        /**
         * 放大缩小照片模式
         */
        private static final int MODE_ZOOM = 2;

        /**
         * 用于记录开始时候的坐标位置
         */
        private PointF startPoint = new PointF();
        /**
         * 用于记录拖拉图片移动的坐标位置
         */
        private Matrix matrix = new Matrix();
        /**
         * 用于记录图片要进行拖拉时候的坐标位置
         */
        private Matrix currentMatrix = new Matrix();

        /**
         * 两个手指的开始距离
         */
        private float startDis;
        /**
         * 两个手指的中间点
         */
        private PointF midPoint;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            /** 通过与运算保留最后八位 MotionEvent.ACTION_MASK = 255 */
            try {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    // 手指压下屏幕
                    case MotionEvent.ACTION_DOWN:
                        mode = MODE_DRAG;
                        // 记录ImageView当前的移动位置
                        currentMatrix.set(matrix);
                        startPoint.set(event.getX(), event.getY());
                        break;
                    // 手指在屏幕上移动，改事件会被不断触发
                    case MotionEvent.ACTION_MOVE:
                        // 拖拉图片
                        if (mode == MODE_DRAG) {
                            float dx = event.getX() - startPoint.x; // 得到x轴的移动距离
                            float dy = event.getY() - startPoint.y; // 得到x轴的移动距离
                            // 在没有移动之前的位置上进行移动
                            matrix.set(currentMatrix);
                            matrix.postTranslate(dx, dy);
                        }
                        // 放大缩小图片
                        else if (mode == MODE_ZOOM) {
                            float endDis = distance(event);// 结束距离
                            if (endDis > 10f) { // 两个手指并拢在一起的时候像素大于10
                                float scale = endDis / startDis;// 得到缩放倍数
                                matrix.set(currentMatrix);
                                Logger.d(midPoint);
                                matrix.postScale(scale, scale, midPoint.x, midPoint.y);
                                //                            matrix.postScale(scale, scale,event.getX(),event.getY());
                            }
                        }
                        break;
                    // 手指离开屏幕
                    case MotionEvent.ACTION_UP:
                        // 当触点离开屏幕，但是屏幕上还有触点(手指)
                    case MotionEvent.ACTION_POINTER_UP:
                        mode = 0;
                        break;
                    // 当屏幕上已经有触点(手指)，再有一个触点压下屏幕
                    case MotionEvent.ACTION_POINTER_DOWN:
                        mode = MODE_ZOOM;
                        /** 计算两个手指间的距离 */
                        startDis = distance(event);
                        /** 计算两个手指间的中间点 */
                        if (startDis > 10f) { // 两个手指并拢在一起的时候像素大于10
                            midPoint = mid(event);
                            //                        Logger.d(midPoint);
                            //记录当前ImageView的缩放倍数
                            currentMatrix.set(matrix);
                        }
                        break;
                }
                /** 限定缩放不小于原始大小 */
                matrix.getValues(finalTransformation);
                //缩放值
                float mscale_x = finalTransformation[Matrix.MSCALE_X];
                float mscale_y = finalTransformation[Matrix.MSCALE_Y];

                if (mscale_x < 1) {
                    finalTransformation[Matrix.MSCALE_X] = 1;
                    mscale_x = 1;
                    matrix.setValues(finalTransformation);
                }
                if (mscale_y < 1) {
                    mscale_y = 1;
                    finalTransformation[Matrix.MSCALE_Y] = 1;
                    matrix.setValues(finalTransformation);
                }
                /** 限定移动不超过边界 */
                //平移值
                float mtrans_x = finalTransformation[Matrix.MTRANS_X];
                float mtrans_y = finalTransformation[Matrix.MTRANS_Y];
                //左
                if (mtrans_x > 0) {
                    finalTransformation[Matrix.MTRANS_X] = 0;
                    matrix.setValues(finalTransformation);
                }
                //上
                if (mtrans_y > 0) {
                    finalTransformation[Matrix.MTRANS_Y] = 0;
                    matrix.setValues(finalTransformation);
                }
                //右
                float width = videoPlayer.getRenderProxy().getWidth();
                float v1 = width - width * mscale_x;
                if (mtrans_x < v1) {
                    finalTransformation[Matrix.MTRANS_X] = v1;
                    matrix.setValues(finalTransformation);
                }
                //下
                float height = videoPlayer.getRenderProxy().getHeight();
                float v2 = height - height * mscale_y;
                if (mtrans_y < v2) {
                    finalTransformation[Matrix.MTRANS_Y] = v2;
                    matrix.setValues(finalTransformation);
                }
//            Logger.t("finalTransformation").i(Arrays.toString(finalTransformation));
                videoPlayer.setMoreScale(matrix);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        /**
         * 计算两个手指间的距离
         */
        private float distance(MotionEvent event) {
            float dx = event.getX(1) - event.getX(0);
            float dy = event.getY(1) - event.getY(0);
            /** 使用勾股定理返回两点之间的距离 */
            return (float) Math.sqrt(dx * dx + dy * dy);
        }

        /**
         * 计算两个手指间的中间点
         */
        private PointF mid(MotionEvent event) {
            /**
             * 此处event 是父控件的  因此要计算比例   其次要 减去 初始位置     未完全解决
             */
            float midX = 0;
            float midY = 0;
            try {
//            Logger.t("midgetX").d(event.getX(0) + "         " + event.getX(1));
//            Logger.t("midgetY").d(event.getY(0) + "         " + event.getY(1));
                //计算比例    横竖屏切换  不可写死比例
//                double ratioX = (double) videoPlayer.getWidth() / (double) DensityUtils.getScreenW(MainActivity.this);
//                double ratioY = (double) videoPlayer.getHeight() / (double) DensityUtils.getScreenH(MainActivity.this);
//            Logger.t("getScreenH").d(DensityUtils.getScreenH(MainActivity.this) + "         " + videoPlayer.getHeight());
//            Logger.t("ratioX").d(ratioX + "         " + ratioY);
                //减去 初始位置
                int[] location = new int[2];
                videoPlayer.getLocationOnScreen(location); //获取在整个屏幕内的绝对坐标，含statusBar
//            Logger.t("midgetX").d(event.getX(0) + "         " + event.getX(1));
//            Logger.t("getLocationOnScreen").d(Arrays.toString(location));
//                midX = (float) ((event.getX(1) + event.getX(0) - location[0] * 2) * ratioX / 2);
//                midY = (float) ((event.getY(1) + event.getY(0) - location[1] * 2) * ratioY / 2);
                midX = (float) ((event.getX(1) + event.getX(0) - location[0] * 2) / 2);
                midY = (float) ((event.getY(1) + event.getY(0) - location[1] * 2) / 2);
//            Logger.t("getY").d(midX + "         " + midY);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new PointF(midX, midY);
        }

    }
}
