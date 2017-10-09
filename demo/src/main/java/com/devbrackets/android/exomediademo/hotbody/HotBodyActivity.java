package com.devbrackets.android.exomediademo.hotbody;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.ViewGroup;

import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.devbrackets.android.exomediademo.R;

public class HotBodyActivity extends AppCompatActivity implements OnPreparedListener {

    private final String commonVideo = "http://source.hotbody.cn/TwcO7c85-5VEK-2pk4-X3kZ-Rayz5bB0nVdd.mp4";
    // 宽度 / 高度 > 16 : 9
    private final String widthVideo = "http://source.hotbody.cn/hS6GTHgM-cEfJ-mhNT-nsQC-QNogKBuPtipD.mp4";
    // 宽度 / 高度 < 16 : 9
    private final String heightVideo = "http://source.hotbody.cn/SHFAgTuF-zvLx-kBlX-oOXu-1suTVltJ34yg.mp4";

    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupVideoView();
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

        final ViewGroup.LayoutParams layoutParams = videoView.getLayoutParams();
        // 竖屏
        if (newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            layoutParams.height = getHeightPx();

            NavBarUtils.showNavigationBar(this, getWindow().getDecorView());
        }
        // 横屏
        else {
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;

            NavBarUtils.hideNavigationBar(this, getWindow().getDecorView());
        }
        videoView.setLayoutParams(layoutParams);
    }
}
