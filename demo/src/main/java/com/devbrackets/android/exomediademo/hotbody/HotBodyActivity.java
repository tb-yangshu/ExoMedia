package com.devbrackets.android.exomediademo.hotbody;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.ViewGroup;

import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.devbrackets.android.exomediademo.R;

public class HotBodyActivity extends AppCompatActivity implements OnPreparedListener {

    private final String commonVideo = "http://source.hotbody.cn/M2VzGQWd-dbaF-nosP-73In-t9LvHpKv5Xqv.mp4";
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
        videoView.setVideoURI(Uri.parse(heightVideo));
    }

    private void initVideoView() {
        videoView = findViewById(R.id.video_view);

        updateVideoViewSize();
    }

    private void updateVideoViewSize() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int heightPx = (int) (displayMetrics.widthPixels * 9 * 1.0f / 16);
        ViewGroup.LayoutParams layoutParams = videoView.getLayoutParams();
        layoutParams.height = heightPx;
        videoView.setLayoutParams(layoutParams);
    }

    @Override
    public void onPrepared() {
        //Starts the video playback as soon as it is ready
        videoView.start();
    }
}
