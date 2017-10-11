package com.devbrackets.android.exomediademo.hotbody;

import android.content.Context;
import android.util.AttributeSet;

import com.devbrackets.android.exomedia.ui.widget.VideoView;

/**
 * VideoView
 * Created by tianbin on 2017/10/11.
 */
public class TestVideoView extends VideoView {

    public TestVideoView(Context context) {
        super(context);
    }

    public TestVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TestVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void stopPlayback(boolean clearSurface) {
        audioFocusHelper.abandonFocus();
        videoViewImpl.stopPlayback(clearSurface);
        setKeepScreenOn(false);

        if (videoControls != null && videoControls instanceof TestControllers) {
            ((TestControllers) videoControls).updatePlaybackState(false, false);
        }
    }

    public void unregisterTouchListener() {
        setOnTouchListener(null);
    }

    public void replay() {
        if (videoControls != null) {
            videoControls.updatePlaybackState(false);
        }

        setVideoURI(videoUri);

        TouchListener listener = new TouchListener(getContext());
        setOnTouchListener(listener);
    }
}
