package com.devbrackets.android.exomediademo.hotbody;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.devbrackets.android.exomedia.listener.OnCompletionListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomediademo.R;

public class TestActivity extends AppCompatActivity implements OnPreparedListener, OrientationManager.OrientationChangeListener, OnCompletionListener {

    private final String commonVideo = "http://source.hotbody.cn/TwcO7c85-5VEK-2pk4-X3kZ-Rayz5bB0nVdd.mp4";
    // 宽度 / 高度 > 16 : 9
    private final String widthVideo = "http://source.hotbody.cn/hS6GTHgM-cEfJ-mhNT-nsQC-QNogKBuPtipD.mp4";
    // 宽度 / 高度 < 16 : 9
    private final String heightVideo = "http://source.hotbody.cn/SHFAgTuF-zvLx-kBlX-oOXu-1suTVltJ34yg.mp4";

    private TestVideoView videoView;
    private View mBtnPlayAnotherVideo;
    private int mOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    private OrientationManager orientationManager;
    private TestControllers mTestControllers;
    private long mCurrentPosition;
    private FrameLayout mVideoViewContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupVideoView();

        orientationManager = new OrientationManager(getApplicationContext());
        orientationManager.setOrientationChangedListener(this);
        orientationManager.enable();

        mBtnPlayAnotherVideo = findViewById(R.id.btn_play_another_video);
        mBtnPlayAnotherVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTestControllers.hideReplay();
                videoView.playAnotherVideo(commonVideo);
            }
        });

        findViewById(R.id.btn_start_new_video_page).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), TestActivity.class);
                startActivity(intent);
            }
        });

        mVideoViewContainer = findViewById(R.id.fl_video_view);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        videoView = new TestVideoView(this);
        videoView.setBackgroundResource(android.R.color.black);
        mVideoViewContainer.addView(videoView);
        updateVideoViewSize();

        videoView.setControls(mTestControllers);
        videoView.setVideoURI(Uri.parse(widthVideo));
        videoView.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared() {
                videoView.seekTo(mCurrentPosition);
                // 不需要调用 stop 视频不会自动开始
                //videoView.stopPlayback();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        mCurrentPosition = videoView.getCurrentPosition();

        videoView.removeView(mTestControllers);
        videoView.release();

        mVideoViewContainer.removeView(videoView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (orientationManager != null) {
            orientationManager.setOrientationChangedListener(null);
            orientationManager.release();
        }

        if (videoView != null) {
            videoView.release();
            mVideoViewContainer.removeView(videoView);
        }
    }

    private void setupVideoView() {
        // Make sure to use the correct VideoView import
        initVideoView();

        videoView.setOnPreparedListener(this);
        videoView.setOnCompletionListener(this);
        //For now we just picked an arbitrary item to play
        videoView.setVideoURI(Uri.parse(widthVideo));
    }

    private void initVideoView() {
        videoView = findViewById(R.id.video_view);

        mTestControllers = new TestControllers(this);
        mTestControllers.setFullscreenListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                view.setVisibility(View.GONE);
            }
        });
        mTestControllers.setReplayClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setVisibility(View.GONE);
                videoView.replay();
            }
        });
        videoView.setControls(mTestControllers);
        updateVideoViewSize();
    }

    private void updateVideoViewSize() {
        int heightPx = getHeightPx();
        ViewGroup.LayoutParams layoutParams = videoView.getLayoutParams();
        layoutParams.height = heightPx;
        videoView.setLayoutParams(layoutParams);
    }

    private int getHeightPx() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return (int) (displayMetrics.widthPixels * 9 * 1.0f / 16);
    }

    @Override
    public void onPrepared() {
        //Starts the video playback as soon as it is ready
        videoView.start();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mOrientation = newConfig.orientation;

        final ViewGroup.LayoutParams layoutParams = videoView.getLayoutParams();
        // 竖屏
        if (newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            layoutParams.height = getHeightPx();

            NavBarUtils.showNavigationBar(this, getWindow().getDecorView());
            updateFullscreenVisibility(View.VISIBLE);
        }
        // 横屏
        else {
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;

            NavBarUtils.hideNavigationBar(this, getWindow().getDecorView());
            updateFullscreenVisibility(View.INVISIBLE);
        }
        videoView.setLayoutParams(layoutParams);
    }

    @Override
    public void onBackPressed() {
        if (mOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            finish();
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            updateFullscreenVisibility(View.VISIBLE);
        }
    }

    private void updateFullscreenVisibility(int visibility) {
        mTestControllers.updateFullscreenVisibility(visibility);
    }

    @Override
    public void onOrientationChanged(int newOrientation) {
        if (android.provider.Settings.System.getInt(getContentResolver(),
                Settings.System.ACCELEROMETER_ROTATION, 0) == 1) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
    }

    @Override
    public void onCompletion() {
        mTestControllers.showCompleteView();
        videoView.unregisterTouchListener();

        mBtnPlayAnotherVideo.setVisibility(View.VISIBLE);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_scroll_close_enter, R.anim.activity_scroll_close_exit);
    }
}
