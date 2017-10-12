package com.devbrackets.android.exomediademo.hotbody;

import android.content.Context;
import android.content.res.Configuration;
import android.view.OrientationEventListener;

import java.lang.ref.WeakReference;

/**
 * OrientationManager
 * Created by tianbin on 2017/7/5.
 */
public class OrientationManager extends OrientationEventListener {

    private int previousAngle;
    private int previousOrientation;
    private WeakReference<Context> mContextRef;
    private OrientationChangeListener orientationChangeListener;

    public OrientationManager(Context context) {
        super(context);

        mContextRef = new WeakReference<>(context);
    }

    @Override
    public void onOrientationChanged(int orientation) {
        if (orientation == -1)
            return;
        if (previousOrientation == 0) {
            previousOrientation = mContextRef.get().getResources().getConfiguration().orientation;
            if (orientationChangeListener != null) {
                orientationChangeListener.onOrientationChanged(previousOrientation);
            }
        }
        if (previousOrientation == Configuration.ORIENTATION_LANDSCAPE &&
                ((previousAngle > 10 && orientation <= 10) ||
                        (previousAngle < 350 && previousAngle > 270 && orientation >= 350))) {
            if (orientationChangeListener != null) {
                orientationChangeListener.onOrientationChanged(Configuration.ORIENTATION_PORTRAIT);
            }
            previousOrientation = Configuration.ORIENTATION_PORTRAIT;
        }

        if (previousOrientation == Configuration.ORIENTATION_PORTRAIT &&
                ((previousAngle < 90 && orientation >= 90 && orientation < 270) ||
                        (previousAngle > 280 && orientation <= 280 && orientation > 180))) {
            if (orientationChangeListener != null) {
                orientationChangeListener.onOrientationChanged(Configuration.ORIENTATION_LANDSCAPE);
            }
            previousOrientation = Configuration.ORIENTATION_LANDSCAPE;
        }
        previousAngle = orientation;
    }

    public void setOrientationChangedListener(OrientationChangeListener l) {
        this.orientationChangeListener = l;
    }

    public interface OrientationChangeListener {
        void onOrientationChanged(int newOrientation);
    }

    public void release() {
        mContextRef.clear();
    }
}
