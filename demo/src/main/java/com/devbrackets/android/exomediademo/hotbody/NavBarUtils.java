package com.devbrackets.android.exomediademo.hotbody;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;

/**
 * 底部功能栏工具类
 * Created by Spencer on 9/2/16.
 */
public final class NavBarUtils {

    private NavBarUtils() {
        //no instance
    }

    /**
     * 隐藏底部功能栏
     *
     * @param rootView
     */
    public static void hideNavBar(View rootView) {
        if (rootView == null) {
            return;
        }

        int mSystemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        if (Build.VERSION.SDK_INT >= 16) {
            mSystemUiVisibility |=
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_FULLSCREEN;
        }
        if (Build.VERSION.SDK_INT >= 19) {
            mSystemUiVisibility |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }
        rootView.setSystemUiVisibility(mSystemUiVisibility);
    }

    public static void showNavigationBar(Activity activity, View rootView) {
        final WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
        attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activity.getWindow().setAttributes(attrs);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        toggleNavigationBar(rootView);
    }

    public static void hideNavigationBar(Activity activity, View rootView) {
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        toggleNavigationBar(rootView);
    }

    private static void toggleNavigationBar(View rootView) {
        int uiOptions = rootView.getSystemUiVisibility();
        int newUiOptions = uiOptions;

        if (Build.VERSION.SDK_INT >= 14) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }

        if (Build.VERSION.SDK_INT >= 16) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }

        if (Build.VERSION.SDK_INT >= 19) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        rootView.setSystemUiVisibility(newUiOptions);
    }

}
