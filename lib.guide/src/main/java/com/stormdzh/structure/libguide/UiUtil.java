package com.stormdzh.structure.libguide;


import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;


/**
 * * des:测量工具类
 * Created by jaydenxiao
 * on 2016.08.11:59
 */
public final class UiUtil {

    /**
     * 获取屏幕尺寸
     *
     * @param activity Activity
     * @return 屏幕尺寸像素值，下标为0的值为宽，下标为1的值为高
     */
    public static int[] getScreenSize(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return new int[]{metrics.widthPixels, metrics.heightPixels};
    }

    /**
     * 获取状态栏高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resId > 0) {
            result = context.getResources().getDimensionPixelOffset(resId);
        }
        return result;
    }

    public static boolean isShowHomeGuide(Activity activity) {
        if (activity == null) return false;
        ViewGroup mRootView = activity.findViewById(android.R.id.content);
        for (int i = 0; i < mRootView.getChildCount(); i++) {
            View chidView = mRootView.getChildAt(i);
            if (chidView != null) {
                Object otag = chidView.getTag(R.id.tag_guide_home_study);
                if (otag != null && otag instanceof String && TextUtils.equals("isShowing", (String) otag)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        Resources r = context.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpValue, r.getDisplayMetrics());
        return (int) px;
    } /**
     * 获取屏幕宽度
     */
    public static int getScreenW(Context aty) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = aty.getResources().getDisplayMetrics();
        int w = dm.widthPixels;
        // int w = aty.getWindowManager().getDefaultDisplay().getWidth();
        return w;
    }




}
