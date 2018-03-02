package com.hero.gsyvideoplayerdemo.gsyjikplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hero.gsyvideoplayerdemo.R;
import com.hero.gsyvideoplayerdemo.ViewsizeUtils;
import com.orhanobut.logger.Logger;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.model.VideoOptionModel;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by shuyu on 2016/12/7.
 * 注意
 * 这个播放器的demo配置切换到全屏播放器
 * 这只是单纯的作为全屏播放显示，如果需要做大小屏幕切换，请记得在这里耶设置上视频全屏的需要的自定义配置
 */

/**
 * demo PreViewGSYVideoPlayer   进度条小窗口预览（测试）
 * 怎么说呢。你可以看看源码，这个功能目前来说还不是很好，还在选择方案中，目前还没有什么特别体验好的方案，以前还有一个旧的方案，是CustomGSYVideoPlayer，不过目前没有维护，后面看看再寻找下上面实现逻辑比较好
 */


public class SampleVideo extends StandardGSYVideoPlayer implements MyHorizontalScrollView.ScrollViewListener {
    private static final String TAG = "SampleVideo";
    private TextView mMoreScale;

    private TextView mSwitchSize;

    private TextView mChangeRotate;

    private TextView mChangeTransform;

    private List<SwitchVideoModel> mUrlList = new ArrayList<>();

    //记住切换数据源类型
    private int mType = 0;

    private int mTransformSize = 0;

    //数据源
    private int mSourcePosition = 0;

    private String mTypeText = "标准";

    private LinearLayout mLinSeekpreview;
    private MyHorizontalScrollView mHsvSeekpreview;
    private int mSeekLegth = 0;
    private long mDurationVideo;

    /**
     * 1.5.0开始加入，如果需要不同布局区分功能，需要重载
     */
    public SampleVideo(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public SampleVideo(Context context) {
        super(context);
    }

    public SampleVideo(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        initView();
    }

    @Override
    protected void changeUiToCompleteShow() {
        super.changeUiToCompleteShow();
    }

    /**
     * 设置   进度条是否显示
     * @param isVisibity
     */
    private void setHsvSeekpreviewVisibity(boolean isVisibity) {
        if (isVisibity) {
            if (mHsvSeekpreview.getVisibility() != VISIBLE) {
                mHsvSeekpreview.setVisibility(VISIBLE);
                mHsvSeekpreview.smoothScrollTo(0,0);
            }
        }else {
            if (mHsvSeekpreview.getVisibility() != GONE) {
                mHsvSeekpreview.setVisibility(GONE);
            }
        }
    }

    private void initView() {
        mMoreScale = (TextView) findViewById(R.id.moreScale);
        mSwitchSize = (TextView) findViewById(R.id.switchSize);
        mChangeRotate = (TextView) findViewById(R.id.change_rotate);
        mChangeTransform = (TextView) findViewById(R.id.change_transform);
        mLinSeekpreview = (LinearLayout) findViewById(R.id.lin_seekpreview);
        mHsvSeekpreview = (MyHorizontalScrollView) findViewById(R.id.hsv_seekpreview);
        mHsvSeekpreview.setmScrollViewListener(this);

        //切换清晰度
        mMoreScale.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mHadPlay) {
                    return;
                }
                if (mType == 0) {
                    mType = 1;
                } else if (mType == 1) {
                    mType = 2;
                } else if (mType == 2) {
                    mType = 3;
                } else if (mType == 3) {
                    mType = 4;
                } else if (mType == 4) {
                    mType = 0;
                }
                resolveTypeUI();
            }
        });

        //切换视频清晰度
        mSwitchSize.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showSwitchDialog();
            }
        });

        //旋转播放角度
        mChangeRotate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mHadPlay) {
                    return;
                }
                if ((mTextureView.getRotation() - mRotate) == 270) {
                    mTextureView.setRotation(mRotate);
                    mTextureView.requestLayout();
                } else {
                    mTextureView.setRotation(mTextureView.getRotation() + 90);
                    mTextureView.requestLayout();
                }
            }
        });

        //镜像旋转
        mChangeTransform.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mHadPlay) {
                    return;
                }
                if (mTransformSize == 0) {
                    mTransformSize = 1;
                } else if (mTransformSize == 1) {
                    mTransformSize = 2;
                } else if (mTransformSize == 2) {
                    mTransformSize = 0;
                }
                resolveTransform();
            }
        });
    }

    /**
     * 需要在尺寸发生变化的时候重新处理
     */
    @Override
    public void onSurfaceSizeChanged(Surface surface, int width, int height) {
        super.onSurfaceSizeChanged(surface, width, height);
        resolveTransform();
    }

    /**
     * 处理镜像旋转
     * 注意，暂停时
     */
    protected void resolveTransform() {
        switch (mTransformSize) {
            case 1: {
                Matrix transform = new Matrix();
                transform.setScale(-1, 1, mTextureView.getWidth() / 2, 0);
                mTextureView.setTransform(transform);
                mChangeTransform.setText("左右镜像");
                mTextureView.invalidate();
            }
            break;
            case 2: {
                Matrix transform = new Matrix();
                transform.setScale(1, -1, 0, mTextureView.getHeight() / 2);
                mTextureView.setTransform(transform);
                mChangeTransform.setText("上下镜像");
                mTextureView.invalidate();
            }
            break;
            case 0: {
                Matrix transform = new Matrix();
                transform.setScale(1, 1, mTextureView.getWidth() / 2, 0);
                mTextureView.setTransform(transform);
                mChangeTransform.setText("旋转镜像");
                mTextureView.invalidate();
            }
            break;
        }
    }

    public void setMoreScale(Matrix transform) {
        mTextureView.setTransform(transform);
        mTextureView.invalidate();
    }

    /**
     * 设置播放URL
     *
     * @param url           播放url
     * @param cacheWithPlay 是否边播边缓存
     * @param title         title
     * @return
     */
    public boolean setUp(List<SwitchVideoModel> url, boolean cacheWithPlay, String title) {
        mUrlList = url;
        return setUp(url.get(mSourcePosition).getUrl(), cacheWithPlay, title);
    }

    private int mCurrentTime;

    private boolean isNetVideo(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }

        if (url.startsWith("http")) {
            return true;
        }

        if (url.startsWith("rtmp") || url.startsWith("rtsp")) {
            setLowDelay();
            return true;
        }
        return false;
    }

    /**
     *  设置直播超低延迟
     */
    private void setLowDelay() {
        VideoOptionModel videoOptionModel =
                new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "probesize", 10240);
        List<VideoOptionModel> list = new ArrayList<>();
        list.add(videoOptionModel);
        //第三个参数设置延迟  越低延迟越低   原来100   单位ms
        videoOptionModel =
                new VideoOptionModel(1, "analyzemaxduration", 5);
        list.add(videoOptionModel);
        GSYVideoManager.instance().setOptionModelList(list);
    }

    public void setPreView4() {
        String urlVideo = mUrlList.get(mSourcePosition).getUrl();
        try {
            //第一次打开   getGSYVideoManager().getMediaPlayer()会为空  定时获取也不行
            mDurationVideo = getGSYVideoManager().getMediaPlayer().getDuration();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (mDurationVideo == 0) {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                if (isNetVideo(urlVideo)) {
                    retriever.setDataSource(urlVideo, new HashMap<String, String>());
                } else {
                    retriever.setDataSource(urlVideo);
                }
                // 取得视频的长度(单位为毫秒)
                mDurationVideo = Long.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Logger.t(TAG).v("--duration------------" + mDurationVideo);
        Logger.t(TAG).v("--mUrlList.get(mSourcePosition).getUrl()------------" + urlVideo);
        try {
            for (int i = 0; i < 21; i++) {
                Logger.t(TAG).v("--currentTime------------" + mCurrentTime);
                ImageView imageView = new ImageView(mContext);
                ViewsizeUtils.viewSizeLin(mContext, imageView, 120 * 3, 72 * 3);
                Glide.with(getContext().getApplicationContext())
                        .setDefaultRequestOptions(
                                new RequestOptions()
                                        .frame(1000 * mCurrentTime)
                                        .override(120, 72)
                                        .dontAnimate()
                                        .centerCrop())
                        .load(urlVideo)
                        .into(imageView);
                mLinSeekpreview.addView(imageView);
                mCurrentTime += mDurationVideo / 20;
            }

            ViewTreeObserver viewTreeObserver = mLinSeekpreview.getViewTreeObserver();
            viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    //观察者  此方法会不断打印  要获取到值就停止赋值
                    int width = mLinSeekpreview.getWidth();
                    if (mSeekLegth == 0) {
                        mSeekLegth = width;
                        Logger.t(TAG).v("mLinSeekpreview--width------------" + mSeekLegth);
                    }
                    return true;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPreView1() throws Exception {
        mLinSeekpreview.removeAllViews();

        Logger.t(TAG).v("--mUrlList.get(mSourcePosition).getUrl()------------" + mUrlList.get(mSourcePosition).getUrl());
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
//后面这个是传请求Headers，如果有需要可以添加
        mmr.setDataSource(mUrlList.get(mSourcePosition).getUrl(), new HashMap());

        int currentTime = 0;

        mDurationVideo = getGSYVideoManager().getMediaPlayer().getDuration();
        Logger.t(TAG).v("--duration------------" + mDurationVideo);

        for (int i = 0; i < 21; i++) {
            currentTime += mDurationVideo / 20;
            Logger.t(TAG).v("--currentTime------------" + currentTime);

            Bitmap bitmap = mmr.
                    getFrameAtTime(currentTime * 1000, MediaMetadataRetriever.OPTION_CLOSEST);
            ImageView imageView = new ImageView(mContext);
            ViewsizeUtils.viewSizeLin(mContext, imageView, 120 * 3, 72 * 3);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 60, baos);
            byte[] bytes = baos.toByteArray();

            Glide.with(mContext)
                    .load(bytes)
                    .into(imageView);
//            imageView.setImageBitmap(bitmap);     不行
            mLinSeekpreview.addView(imageView);
            bitmap.recycle();
        }
    }

    /**
     * 设置播放URL
     *
     * @param url           播放url
     * @param cacheWithPlay 是否边播边缓存
     * @param cachePath     缓存路径，如果是M3U8或者HLS，请设置为false
     * @param title         title
     * @return
     */
    public boolean setUp(List<SwitchVideoModel> url, boolean cacheWithPlay, File cachePath, String title) {
        mUrlList = url;
        return setUp(url.get(mSourcePosition).getUrl(), cacheWithPlay, cachePath, title);
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_gsy_sample_video;
    }

    /**
     * 全屏时将对应处理参数逻辑赋给全屏播放器
     *
     * @param context
     * @param actionBar
     * @param statusBar
     * @return
     */
    @Override
    public GSYBaseVideoPlayer startWindowFullscreen(Context context, boolean actionBar, boolean statusBar) {
        SampleVideo sampleVideo = (SampleVideo) super.startWindowFullscreen(context, actionBar, statusBar);
        sampleVideo.mSourcePosition = mSourcePosition;
        sampleVideo.mType = mType;
        sampleVideo.mTransformSize = mTransformSize;
        sampleVideo.mUrlList = mUrlList;
        sampleVideo.mTypeText = mTypeText;
        //sampleVideo.resolveTransform();
        sampleVideo.resolveTypeUI();
        //sampleVideo.resolveRotateUI();
        //这个播放器的demo配置切换到全屏播放器
        //这只是单纯的作为全屏播放显示，如果需要做大小屏幕切换，请记得在这里耶设置上视频全屏的需要的自定义配置
        //比如已旋转角度之类的等等
        //可参考super中的实现
        return sampleVideo;
    }

    /**
     * 推出全屏时将对应处理参数逻辑返回给非播放器
     *
     * @param oldF
     * @param vp
     * @param gsyVideoPlayer
     */
    @Override
    protected void resolveNormalVideoShow(View oldF, ViewGroup vp, GSYVideoPlayer gsyVideoPlayer) {
        super.resolveNormalVideoShow(oldF, vp, gsyVideoPlayer);
        if (gsyVideoPlayer != null) {
            SampleVideo sampleVideo = (SampleVideo) gsyVideoPlayer;
            mSourcePosition = sampleVideo.mSourcePosition;
            mType = sampleVideo.mType;
            mTransformSize = sampleVideo.mTransformSize;
            mTypeText = sampleVideo.mTypeText;
            setUp(mUrlList, mCache, mCachePath, mTitle);
            resolveTypeUI();
        }
    }

    @Override
    public void startAfterPrepared() {
        super.startAfterPrepared();
        Logger.t(TAG).v("---startAfterPrepared-----------");
        setHsvSeekpreviewVisibity(true);
    }

    @Override
    public void onCompletion() {
        super.onCompletion();
        Logger.t(TAG).v("---onCompletion-----------");
        setHsvSeekpreviewVisibity(false);
    }

    //播放出错
    @Override
    public void onError(int what, int extra) {
        super.onError(what, extra);
        Logger.t(TAG).v("---onError-----------");
        setHsvSeekpreviewVisibity(false);
    }

    @Override
    public void onAutoCompletion() {
        super.onAutoCompletion();
        Logger.t(TAG).v("---onAutoCompletion-----------");
        setHsvSeekpreviewVisibity(false);
    }

    /**
     * 处理显示逻辑
     */
    @Override
    public void onSurfaceAvailable(Surface surface) {
        super.onSurfaceAvailable(surface);
        resolveRotateUI();
        resolveTransform();
    }

    /**
     * 旋转逻辑
     */
    private void resolveRotateUI() {
        if (!mHadPlay) {
            return;
        }
        mTextureView.setRotation(mRotate);
        mTextureView.requestLayout();
    }

    /**
     * 显示比例
     * 注意，GSYVideoType.setShowType是全局静态生效，除非重启APP。
     */
    private void resolveTypeUI() {
        if (!mHadPlay) {
            return;
        }
        if (mType == 1) {
            mMoreScale.setText("16:9");
            GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_16_9);
        } else if (mType == 2) {
            mMoreScale.setText("4:3");
            GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_4_3);
        } else if (mType == 3) {
            mMoreScale.setText("全屏");
            GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_FULL);
        } else if (mType == 4) {
            mMoreScale.setText("拉伸全屏");
            GSYVideoType.setShowType(GSYVideoType.SCREEN_MATCH_FULL);
        } else if (mType == 0) {
            mMoreScale.setText("默认比例");
            GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_DEFAULT);
        }
        changeTextureViewShowType();
        if (mTextureView != null)
            mTextureView.requestLayout();
        mSwitchSize.setText(mTypeText);
    }

    /**
     * 弹出切换清晰度
     */
    private void showSwitchDialog() {
        if (!mHadPlay) {
            return;
        }
        SwitchVideoTypeDialog switchVideoTypeDialog = new SwitchVideoTypeDialog(getContext());
        switchVideoTypeDialog.initList(mUrlList, new SwitchVideoTypeDialog.OnListItemClickListener() {
            @Override
            public void onItemClick(int position) {
                final String name = mUrlList.get(position).getName();
                if (mSourcePosition != position) {
                    if ((mCurrentState == GSYVideoPlayer.CURRENT_STATE_PLAYING
                            || mCurrentState == GSYVideoPlayer.CURRENT_STATE_PAUSE)
                            && GSYVideoManager.instance().getMediaPlayer() != null) {
                        final String url = mUrlList.get(position).getUrl();
                        onVideoPause();
                        final long currentPosition = mCurrentPosition;
                        GSYVideoManager.instance().releaseMediaPlayer();
                        cancelProgressTimer();
                        hideAllWidget();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setUp(url, mCache, mCachePath, mTitle);
                                setSeekOnStart(currentPosition);
                                startPlayLogic();
                                cancelProgressTimer();
                                hideAllWidget();
                            }
                        }, 500);
                        mTypeText = name;
                        mSwitchSize.setText(name);
                        mSourcePosition = position;
                    }
                } else {
                    Toast.makeText(getContext(), "已经是 " + name, Toast.LENGTH_LONG).show();
                }
            }
        });
        switchVideoTypeDialog.show();
    }

    @Override
    public void onScrollChanged(int scrollSeek) {
        Logger.t("onScrollChanged").v("--onScrollChanged-----scrollSeek-------" + scrollSeek);
        Logger.t("onScrollChanged").v("onScrollChanged--mDurationVideo------------" + mDurationVideo);
        Logger.t("onScrollChanged").v("onScrollChanged--mSeekLegth------------" + mSeekLegth);
        Logger.t("onScrollChanged").v("--onScrollChanged-----(long) scrollSeek / (long) mSeekLegth-------" + (double) scrollSeek / (double) mSeekLegth);
        Logger.t("onScrollChanged").v("onScrollChanged--seek------------" + (double) scrollSeek / (double) mSeekLegth * mDurationVideo);
        seekTo((long) ((double) scrollSeek / (double) mSeekLegth * mDurationVideo));
    }
}
