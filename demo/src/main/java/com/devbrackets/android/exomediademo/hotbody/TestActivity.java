package com.devbrackets.android.exomediademo.hotbody;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.devbrackets.android.exomediademo.R;

public class TestActivity extends AppCompatActivity implements OnPreparedListener, OrientationManager.OrientationChangeListener {

    private final String commonVideo = "http://source.hotbody.cn/TwcO7c85-5VEK-2pk4-X3kZ-Rayz5bB0nVdd.mp4";
    // 宽度 / 高度 > 16 : 9
    private final String widthVideo = "http://source.hotbody.cn/hS6GTHgM-cEfJ-mhNT-nsQC-QNogKBuPtipD.mp4";
    // 宽度 / 高度 < 16 : 9
    private final String heightVideo = "http://source.hotbody.cn/SHFAgTuF-zvLx-kBlX-oOXu-1suTVltJ34yg.mp4";

    private VideoView videoView;
    private int mOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    private OrientationManager orientationManager;
    private TestControllers mTestControllers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupVideoView();

        orientationManager = OrientationManager.getInstance(this);
        orientationManager.setOrientationChangedListener(this);
        orientationManager.enable();
    }

    private void setupVideoView() {
        // Make sure to use the correct VideoView import
        initVideoView();

        videoView.setOnPreparedListener(this);

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
}
