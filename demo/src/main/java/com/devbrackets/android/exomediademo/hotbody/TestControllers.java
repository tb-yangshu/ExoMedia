package com.devbrackets.android.exomediademo.hotbody;

import android.content.Context;
import android.support.annotation.IntRange;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;

import com.devbrackets.android.exomedia.ui.animation.BottomViewHideShowAnimation;
import com.devbrackets.android.exomedia.ui.animation.TopViewHideShowAnimation;
import com.devbrackets.android.exomedia.ui.widget.VideoControls;
import com.devbrackets.android.exomedia.util.TimeFormatUtil;
import com.devbrackets.android.exomediademo.R;

/**
 * TestControllers
 * Created by tianbin on 2017/10/10.
 */
public class TestControllers extends VideoControls {

    private SeekBar mSeekBar;
    private boolean userInteracting;
    private View mBtnFullscreen;
    private View mBtnReplay;

    public TestControllers(Context context) {
        this(context, null);
    }

    public TestControllers(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TestControllers(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setHideDelay(5000);
    }

    @Override
    public void setPosition(@IntRange(from = 0) long position) {
        currentTimeTextView.setText(TimeFormatUtil.formatMs(position));
        mSeekBar.setProgress((int) position);
    }

    @Override
    public void setDuration(@IntRange(from = 0) long duration) {
        if (duration != mSeekBar.getMax()) {
            endTimeTextView.setText(TimeFormatUtil.formatMs(duration));
            mSeekBar.setMax((int) duration);
        }
    }

    @Override
    public void updateProgress(@IntRange(from = 0) long position, @IntRange(from = 0) long duration, @IntRange(from = 0, to = 100) int bufferPercent) {
        if (!userInteracting) {
            mSeekBar.setSecondaryProgress((int) (mSeekBar.getMax() * ((float) bufferPercent / 100)));
            mSeekBar.setProgress((int) position);
            currentTimeTextView.setText(TimeFormatUtil.formatMs(position));
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.layout_controllers;
    }

    @Override
    public void hideDelayed(long delay) {
        hideDelay = delay;

        if (delay < 0 || !canViewHide || isLoading) {
            return;
        }

        //If the user is interacting with controls we don't want to start the delayed hide yet
        if (!userInteracting) {
            visibilityHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    animateVisibility(false);
                }
            }, delay);
        }
    }

    @Override
    protected void animateVisibility(boolean toVisible) {
        if (isVisible == toVisible) {
            return;
        }

        if (!hideEmptyTextContainer || !isTextContainerEmpty()) {
            textContainer.startAnimation(new TopViewHideShowAnimation(textContainer, toVisible, CONTROL_VISIBILITY_ANIMATION_LENGTH));
        }

        if (!isLoading) {
            controlsContainer.startAnimation(new BottomViewHideShowAnimation(controlsContainer, toVisible, CONTROL_VISIBILITY_ANIMATION_LENGTH));
        }

        isVisible = toVisible;
        onVisibilityChanged();
    }

    @Override
    protected void updateTextContainerVisibility() {
    }

    @Override
    public void showLoading(boolean initialLoad) {
        if (isLoading) {
            return;
        }

        isLoading = true;
        loadingProgressBar.setVisibility(View.VISIBLE);

        if (initialLoad) {
            controlsContainer.setVisibility(View.GONE);
        } else {
            playPauseButton.setEnabled(false);
        }

        show();
    }

    @Override
    public void finishLoading() {
        if (!isLoading) {
            return;
        }

        isLoading = false;
        loadingProgressBar.setVisibility(View.GONE);
        controlsContainer.setVisibility(View.VISIBLE);

        playPauseButton.setEnabled(true);

        updatePlaybackState(videoView != null && videoView.isPlaying());
    }

    @Override
    protected void updateButtonDrawables() {
        playDrawable = getResources().getDrawable(R.drawable.ic_play_normal);
        pauseDrawable = getResources().getDrawable(R.drawable.ic_pause_normal);
        playPauseButton.setImageDrawable(playDrawable);
    }

    @Override
    protected void retrieveViews() {
        super.retrieveViews();
        mSeekBar = findViewById(R.id.exomedia_controls_video_seek);
        mBtnFullscreen = findViewById(R.id.exomedia_controls_fullscreen_btn);
        mBtnReplay = findViewById(R.id.btn_replay);
    }

    public void setFullscreenListener(OnClickListener listener) {
        mBtnFullscreen.setOnClickListener(listener);
    }

    public void updateFullscreenVisibility(int visibility) {
        mBtnFullscreen.setVisibility(visibility);
    }

    public void setReplayClickListener(OnClickListener listener) {
        mBtnReplay.setOnClickListener(listener);
    }

    @Override
    protected void registerListeners() {
        super.registerListeners();
        mSeekBar.setOnSeekBarChangeListener(new SeekBarChanged());
    }

    public void showCompleteView() {
        controlsContainer.setVisibility(GONE);
        mBtnReplay.setVisibility(VISIBLE);
    }

    // 视频播放完后不再弹出controller
    public void updatePlaybackStateAndHideController(boolean isPlaying) {
        updatePlayPauseImage(isPlaying);
        progressPollRepeater.start();

        animateVisibility(false);
    }

    /**
     * Listens to the seek bar change events and correctly handles the changes
     */
    protected class SeekBarChanged implements SeekBar.OnSeekBarChangeListener {
        private long seekToTime;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!fromUser) {
                return;
            }

            seekToTime = progress;
            if (currentTimeTextView != null) {
                currentTimeTextView.setText(TimeFormatUtil.formatMs(seekToTime));
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            userInteracting = true;
            if (seekListener == null || !seekListener.onSeekStarted()) {
                internalListener.onSeekStarted();
            }
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            userInteracting = false;
            if (seekListener == null || !seekListener.onSeekEnded(seekToTime)) {
                internalListener.onSeekEnded(seekToTime);
            }
        }
    }
}
