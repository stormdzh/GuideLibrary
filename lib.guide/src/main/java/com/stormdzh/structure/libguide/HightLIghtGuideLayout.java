package com.stormdzh.structure.libguide;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stormdzh.structure.libguide.bean.RectangleBean;

/**
 * Created by a111 on 2018/5/24.
 * 引导
 */

public class HightLIghtGuideLayout extends FrameLayout implements CopyHighLightGuideView.OnDrawCompleteListener, CopyHighLightGuideView.OnDismissListener {

    //高亮类型：矩形、圆形、椭圆形
    public static final int VIEWSTYLE_RECT = 0;
    public static final int VIEWSTYLE_CIRCLE = 1;
    public static final int VIEWSTYLE_OVAL = 2;


    public static final int HAND_RIGHT = 0; //手在右边
    public static final int HAND_LEFT = 1; //手在左边
    public int mHandLocation = 0;//0:right 1:left

    public static final int STYLE_LIGHT = 0; // 按钮高亮
    public static final int STYLE_PIC = 1; // 显示图标
    public static final int STYLE_MAP = 2; // 地图
    public int mStyleType = 0;//0:白色高亮 1:不高亮，显示图标 2：地图
    private View mRootView;//activity的contentview,是FrameLayout
    private Activity activity;
    private CopyHighLightGuideView mGuideView;
    private RelativeLayout mLLHandLayout;
    private ImageView mHandImageView;
    private TextView mNoticeView;
    private RectangleBean rectangleBean;
    private boolean hasShowOndraw = false;
    private AnimatorSet handAnimSet; // 手的动画

    public HightLIghtGuideLayout(@NonNull Activity context) {
        super(context);
        init(context);
    }

    public HightLIghtGuideLayout(@NonNull Context context) {
        super(context);
        if (context instanceof Activity)
            init((Activity) context);
    }

    private void init(Activity context) {
        this.activity = context;
        mRootView = ((Activity) getContext()).findViewById(android.R.id.content);
        mGuideView = new CopyHighLightGuideView(context);
        mGuideView.setOnDrawCompleteListener(this);
        mGuideView.setOnDismissListener(this);
        addView(mGuideView);

        //初始化提示布局
        mLLHandLayout = new RelativeLayout(context);
        LayoutParams llparams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        mLLHandLayout.setLayoutParams(llparams);

        //初始化动画小手
        mHandImageView = new ImageView(context);
        mHandImageView.setId(R.id.guide_layout_hand);
        LayoutParams params = new LayoutParams(UiUtil.dip2px(context, 80), UiUtil.dip2px(context, 80));
        mHandImageView.setLayoutParams(params);
        mHandImageView.setImageResource(R.drawable.click_this_guide);
//        mHandImageView.setImageResource(R.drawable.anim_guide_hand_point_left);
//        AnimationDrawable loadingDrawable = (AnimationDrawable) mHandImageView.getDrawable();
//        loadingDrawable.start();
        mLLHandLayout.addView(mHandImageView);

        //初始化手下文案
        mNoticeView = new TextView(context);
        RelativeLayout.LayoutParams handParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        handParams.addRule(RelativeLayout.BELOW, R.id.guide_layout_hand);
        mNoticeView.setLayoutParams(handParams);
        mLLHandLayout.addView(mNoticeView);
        mNoticeView.setLines(2);
        mNoticeView.setTextSize(14);
        mNoticeView.setTextColor(Color.WHITE);

//      addView(mLLHandLayout);

    }

    public static HightLIghtGuideLayout builder(Activity activity) {

        return new HightLIghtGuideLayout(activity);
    }

    /**
     * 设置需要高亮的View和提示的图片
     *
     * @param targetView 高亮的控件
     */
    public HightLIghtGuideLayout addHighLightGuidView(View targetView) {
        mGuideView.addHighLightGuidView(targetView);
        return this;
    }

    /**
     * 设置需要高亮的View和提示的图片
     */
    public HightLIghtGuideLayout addHighLightGuidView(Bitmap bitmap, RectangleBean rectangleBean) {
        mGuideView.addHighLightGuidView(bitmap, rectangleBean);
        this.rectangleBean = rectangleBean;
        return this;
    }

    /**
     * 设置高亮显示类型
     *
     * @param style 高亮的类型
     */
    public HightLIghtGuideLayout setHighLightStyle(int style) {
        mGuideView.setHighLightStyle(style);
        return this;
    }

    public HightLIghtGuideLayout setHandLocation(int location) {
        this.mHandLocation = location;
        return this;
    }

    public HightLIghtGuideLayout setHandText(String text) {
        if (TextUtils.isEmpty(text)) {
            mNoticeView.setVisibility(View.GONE);
        } else {
            mNoticeView.setVisibility(View.VISIBLE);
            this.mNoticeView.setText(text);
        }
        return this;
    }

    public HightLIghtGuideLayout setStyleType(int type) {
        this.mStyleType = type;
        this.mGuideView.setStyleType(type);
        return this;
    }

    @SuppressWarnings("all")
    public HightLIghtGuideLayout setMapStudyClick(OnMapStudyClickListener mapListener, int position) {
        mGuideView.setMapStudyClick(mapListener, position);
        return this;
    }

    /**
     * 显示
     */
    public void show() {
        if (mRootView != null) {
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams
                    (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            this.setTag(R.id.tag_guide_home_study, "isShowing");
            ((ViewGroup) mRootView).addView(this, ((ViewGroup) mRootView).getChildCount(), lp);
        }
    }

    @SuppressWarnings("all")
    private void startThisAnim(ImageView ivStudyThis, boolean isPointRight) {
        if (ivStudyThis == null) {
            return;
        }
        if (handAnimSet != null) {
            handAnimSet.cancel();
        }
        int handAnimLeft = UiUtil.dip2px(getContext(), 10);
        handAnimSet = handAnim(isPointRight, ivStudyThis, -handAnimLeft, -handAnimLeft);
        handAnimSet.start();
    }

    public static AnimatorSet handAnim(boolean isPointRight, ImageView ivStudyThis, int handAnimLeft, int handAnimTop) {

        ObjectAnimator animator1;
        ObjectAnimator animator2;
        if (isPointRight) {
            animator1 = ObjectAnimator.ofFloat(ivStudyThis, "translationX", 0, handAnimLeft, 0);
            animator2 = ObjectAnimator.ofFloat(ivStudyThis, "translationY", 0, -handAnimTop, 0);
        } else {
            animator1 = ObjectAnimator.ofFloat(ivStudyThis, "translationX", 0, -handAnimLeft, 0);
            animator2 = ObjectAnimator.ofFloat(ivStudyThis, "translationY", 0, -handAnimTop, 0);
        }

        AnimatorSet handAnimSet = new AnimatorSet();
        handAnimSet.setDuration(1500);
        animator1.setRepeatCount(ValueAnimator.INFINITE);
        animator2.setRepeatCount(ValueAnimator.INFINITE);
        handAnimSet.playTogether(animator1, animator2);
        return handAnimSet;
    }


    @Override
    public void onDrawComplete() {
        if (hasShowOndraw) return;
        hasShowOndraw = true;
        Log.e("test", "onDrawComplete====");
        if (mGuideView == null) return;
        if (mLLHandLayout.getParent() == null)
            addView(mLLHandLayout);
        startThisAnim(mHandImageView, false);
        if (mStyleType == STYLE_LIGHT) {
            Rect rtLocation = mGuideView.getLightViewLocation();
            if (rtLocation == null) return;
            int dx = (rtLocation.right - rtLocation.left) / 2;
            LayoutParams layoutParams = (LayoutParams) mLLHandLayout.getLayoutParams();
            if (mHandLocation == HAND_RIGHT) {
                layoutParams.topMargin = rtLocation.top + dx;
                layoutParams.leftMargin = rtLocation.left + dx;
            } else if (mHandLocation == HAND_LEFT) {
                layoutParams.topMargin = rtLocation.top + dx;
                layoutParams.rightMargin = UiUtil.getScreenW(activity) - rtLocation.right + dx;

                RelativeLayout.LayoutParams textParams = (RelativeLayout.LayoutParams) mNoticeView.getLayoutParams();
                textParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                mNoticeView.setLayoutParams(textParams);
                mHandImageView.setImageResource(R.drawable.click_guide_point_right);
                startThisAnim(mHandImageView, true);
//                mHandImageView.setImageResource(R.drawable.anim_guide_hand_point_right);
//                AnimationDrawable loadingDrawable = (AnimationDrawable) mHandImageView.getDrawable();
//                loadingDrawable.stop();
//                loadingDrawable.start();
                RelativeLayout.LayoutParams handParams = (RelativeLayout.LayoutParams) mHandImageView.getLayoutParams();
                handParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                mHandImageView.setLayoutParams(handParams);
            }
            mLLHandLayout.setLayoutParams(layoutParams);
        } else if (mStyleType == STYLE_PIC) {
            Rect rtLocation = mGuideView.getLightViewLocation();
            if (rtLocation == null) return;
            int dx = (rtLocation.right - rtLocation.left) / 4;
            LayoutParams layoutParams = (LayoutParams) mLLHandLayout.getLayoutParams();
            layoutParams.topMargin = rtLocation.top;
            layoutParams.leftMargin = rtLocation.right - dx;
            mLLHandLayout.setLayoutParams(layoutParams);

        } else if (mStyleType == STYLE_MAP) {
            LayoutParams layoutParams = (LayoutParams) mLLHandLayout.getLayoutParams();
            layoutParams.topMargin = rectangleBean.top + rectangleBean.right / 2;
            layoutParams.leftMargin = rectangleBean.left + rectangleBean.bottom / 2;
            mLLHandLayout.setLayoutParams(layoutParams);
        }
    }

    @Override
    public void onDismiss() {
        if (mLLHandLayout != null)
            mLLHandLayout.removeAllViews();
        ((ViewGroup) mRootView).removeView(this);
    }
}
