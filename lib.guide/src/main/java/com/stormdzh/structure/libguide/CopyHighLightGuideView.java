package com.stormdzh.structure.libguide;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.stormdzh.structure.libguide.bean.CircleBean;
import com.stormdzh.structure.libguide.bean.RectangleBean;


/**
 * * des:“应用新功能”的用户指引view
 * Created by jaydenxiao
 * on 2016.08.11:59
 */
public class CopyHighLightGuideView extends View {

    //画笔类型，圆滑、默认
    public static final int MASKBLURSTYLE_SOLID = 0;
    public static final int MASKBLURSTYLE_NORMAL = 1;
    public int mStyleType = 0;//0:白色高亮 1:不高亮，显示图标,2：地图

    private Bitmap fgBitmap;// 前景
    private Canvas mCanvas;// 绘制蒙版层的画布
    private Paint mPaint;// 绘制蒙版层画笔
    private int screenW, screenH;// 屏幕宽高
    //    private static final int margin = 40;
    @SuppressWarnings("all")
    private int radius;//圆半径
    private OnDismissListener onDismissListener;//关闭监听
    private Activity activity;
//    private Bitmap myKnowBtn; // 我知道了bitmap

    /*******************可配置属性*****************************/
    private int highLightStyle = HightLIghtGuideLayout.VIEWSTYLE_RECT;//高亮类型默认圆形
    public int maskblurstyle = MASKBLURSTYLE_SOLID;//画笔类型默认
    //    private ArrayList<View> targetViews;//高亮目标view
    private View mTargetView;
    private int maskColor = 0x99000000;// 蒙版层颜色
    private int borderWitdh = 10;
    private int highLisghtPadding = 0;// 高亮控件padding
    private Rect rtLocation;
    private CircleBean lightCircle;
    private RectangleBean rectangleBean;

    private Bitmap mapBitmap;
    private RectangleBean mapRectangleBean;
    private OnMapStudyClickListener mapListener;
    private int mapPosition;


    public CopyHighLightGuideView(Activity activity) {
        super(activity);
        this.activity = activity;
        // 计算参数
        cal(activity);
        // 初始化对象
        init();
    }

    public CopyHighLightGuideView(Context context) {
        super(context);
        if (context instanceof Activity) {
            this.activity = (Activity) context;
            // 计算参数
            cal(context);
            // 初始化对象
            init();
        }
    }

    public static CopyHighLightGuideView builder(Activity activity) {
        return new CopyHighLightGuideView(activity);
    }

    /**
     * 计算参数
     *
     * @param context 上下文环境引用
     */
    private void cal(Context context) {
        // 获取屏幕尺寸数组
        int[] screenSize = UiUtil.getScreenSize((Activity) context);
        // 获取屏幕宽高
        screenW = screenSize[0];
        screenH = screenSize[1];
    }

    /**
     * 初始化对象
     */
    private void init() {
        // 实例化画笔并开启其抗锯齿和抗抖动
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        // 设置画笔透明度为0是关键！
        mPaint.setARGB(0, 255, 0, 0);
        // 设置混合模式为DST_IN
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        BlurMaskFilter.Blur blurStyle = null;
        switch (maskblurstyle) {
            case MASKBLURSTYLE_SOLID:
                blurStyle = BlurMaskFilter.Blur.SOLID;
                break;
            case MASKBLURSTYLE_NORMAL:
                blurStyle = BlurMaskFilter.Blur.NORMAL;
                break;
        }
        mPaint.setMaskFilter(new BlurMaskFilter(15, blurStyle));
        // 生成前景图Bitmap
        fgBitmap = Bitmap.createBitmap(screenW, screenH, Bitmap.Config.ARGB_4444);
        // 将其注入画布
        mCanvas = new Canvas(fgBitmap);
        // 绘制前景画布颜色
        mCanvas.drawColor(maskColor);
        // 实例化箭头图片
//        myKnowBtn = BitmapFactory.decodeResource(getResources(), R.drawable.my_know);
    }

    @SuppressWarnings("all")
    @Override
    protected void onDraw(Canvas canvas) {
        // 绘制前景
        canvas.drawBitmap(fgBitmap, 0, 0, null);
        //有高亮控件
        if (mTargetView != null) {
            //高亮控件宽高
            int vWidth = mTargetView.getWidth();
            int vHeight = mTargetView.getHeight();
            //获取获取高亮控件坐标
            int left = 0;
            int top = 0;
            int right = 0;
            int bottom = 0;
            if (mStyleType == HightLIghtGuideLayout.STYLE_LIGHT || mStyleType == HightLIghtGuideLayout.STYLE_PIC) {
                try {
                    rtLocation = ViewUtils.getLocationInView(((ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT)).getChildAt(0), mTargetView);
                    left = rtLocation.left;
                    top = rtLocation.top;
                    right = rtLocation.right;
                    bottom = rtLocation.bottom;
                    Log.d("statusheightssleft", left + "");
                    Log.d("statusheightsstop", top + "");
                    Log.d("statusheightbottom", right + "");
                    Log.d("statusheightsbottom", bottom + "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (mStyleType == HightLIghtGuideLayout.STYLE_LIGHT) {
                //绘制高亮形状
                switch (highLightStyle) {
                    case HightLIghtGuideLayout.VIEWSTYLE_OVAL:
                        RectF rectf = new RectF(left - highLisghtPadding, top - highLisghtPadding, right + highLisghtPadding, bottom + highLisghtPadding);
                        mCanvas.drawOval(rectf, mPaint);
                        break;
                    case HightLIghtGuideLayout.VIEWSTYLE_RECT:
                        RectF rect = new RectF(left - borderWitdh - highLisghtPadding, top - borderWitdh - highLisghtPadding, right + borderWitdh + highLisghtPadding, bottom + borderWitdh + highLisghtPadding);
                        mCanvas.drawRoundRect(rect, 20, 20, mPaint);
                        break;
                    case HightLIghtGuideLayout.VIEWSTYLE_CIRCLE:
                    default:
                        radius = vWidth > vHeight ? vWidth / 2 + highLisghtPadding / 2 : vHeight / 2 + highLisghtPadding / 2;
                        if (radius < 50) {
                            radius = 100;
                        }
                        if (rtLocation != null) {
                            lightCircle = new CircleBean();
                            lightCircle.cx = left + vWidth / 2;
                            lightCircle.cy = top + vHeight / 2;
                            lightCircle.r = radius;
                            mCanvas.drawCircle(left + vWidth / 2, top + vHeight / 2, radius, mPaint);
                        }
                        break;

                }
            } else if (mStyleType == HightLIghtGuideLayout.STYLE_PIC) { //直接在遮罩上绘制图标
                if (rtLocation != null) {
                    rectangleBean = new RectangleBean();
                    rectangleBean.top = rtLocation.top;
                    rectangleBean.right = rtLocation.right;
                    rectangleBean.left = rtLocation.left;
                    rectangleBean.bottom = rtLocation.bottom;

                    Bitmap bitmap = loadBitmapFromView(mTargetView);
                    mCanvas.drawBitmap(bitmap, rtLocation.left, rtLocation.top, null);
                }
            }

        } else {
            if (mStyleType == HightLIghtGuideLayout.STYLE_MAP) { //绘制地图
                if (mapRectangleBean != null && mapBitmap != null) {
                    mCanvas.drawBitmap(mapBitmap, mapRectangleBean.left, mapRectangleBean.top, null);
                }
            }
        }

        // 在底部绘制知识了按钮
//        if (showKnowBtn) {
//            canvas.drawBitmap(myKnowBtn, (screenW - myKnowBtn.getWidth()) / 2, (screenH - DeviceUtil.dip2px(getContext(), 100) - myKnowBtn.getHeight()), null);
//
//        }

        //绘制结束之后，回调给父控件绘制手
        if (mListener != null)
            mListener.onDrawComplete();
    }


    private Bitmap loadBitmapFromView(View v) {
        int w = v.getWidth();
        int h = v.getHeight();
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);

        c.drawColor(Color.TRANSPARENT);
        //如果不设置canvas画布为白色，则生成透明
//        v.layout(0, 0, w, h);
        v.draw(c);

        return bmp;
    }

    @SuppressWarnings("all")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP://

                int x = (int) event.getX();
                int y = (int) event.getY();
                if (mStyleType == HightLIghtGuideLayout.STYLE_LIGHT) {  //点击高亮区域
                    if (inCircle(new Point(x, y), lightCircle)) {
                        mTargetView.performClick();
                        if (this.onDismissListener != null) {
                            onDismissListener.onDismiss();
                        }
                        return super.onTouchEvent(event);
                    }
                } else if (mStyleType == HightLIghtGuideLayout.STYLE_PIC) { //点击控件图片
                    if (inRectangle(new Point(x, y), rectangleBean)) {
                        mTargetView.performClick();
                        if (this.onDismissListener != null) {
                            onDismissListener.onDismiss();
                        }
                        return super.onTouchEvent(event);
                    }
                } else if (mStyleType == HightLIghtGuideLayout.STYLE_MAP) { //点击地图控件
                    if (inRectangle2(new Point(x, y), mapRectangleBean)) {
                        mapListener.onMapStudyClick(mapPosition);
                        if (this.onDismissListener != null) {
                            onDismissListener.onDismiss();
                        }
                        return super.onTouchEvent(event);
                    }
                }
        }
        return true;
    }


    //判断点在圆内
    @SuppressWarnings("all")
    private boolean inCircle(Point point, CircleBean circle) {
        int x = circle.cx;
        int y = circle.cy;
        int r = circle.r;
        int px = point.x;
        int py = point.y;

        if (!((x - px) * (x - px) + (y - py) * (y - py) > r * r)) {
            return true;        //当前点在圆内
        } else {
            return false;       //当前点在圆外
        }
    }

    //判断点在矩行内
    @SuppressWarnings("all")
    private boolean inRectangle(Point point, RectangleBean rectangleBean) {

        int top = rectangleBean.top;
        int left = rectangleBean.left;
        int right = rectangleBean.right;
        int bottom = rectangleBean.bottom;
        int px = point.x;
        int py = point.y;

        if (px >= left && px <= right && py >= top && py <= bottom) {
            return true;        //当前点在内
        } else {
            return false;       //当前点在外
        }
    }

    //判断点在矩形内
    @SuppressWarnings("all")
    private boolean inRectangle2(Point point, RectangleBean rectangleBean) {

        int top = rectangleBean.top;
        int left = rectangleBean.left;
        int right = rectangleBean.right;
        int bottom = rectangleBean.bottom;
        int px = point.x;
        int py = point.y;

        if (px >= left && px <= right + left && py >= top && py <= bottom + top) {
            return true;        //当前点在内
        } else {
            return false;       //当前点在外
        }
    }

    public interface OnDismissListener {
        void onDismiss();
    }


    /**
     * 绘制前景画布颜色
     *
     * @param bgColor 背景颜色
     */
    @SuppressWarnings("unused")
    public void setMaskColor(int bgColor) {
        try {
            this.maskColor = ContextCompat.getColor(getContext(), bgColor);
            // 重新绘制前景画布
            mCanvas.drawColor(maskColor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置高亮显示类型
     *
     * @param style 高亮的类型
     */
    public void setHighLightStyle(int style) {
        this.highLightStyle = style;
    }

    /**
     * 设置需要高亮的View和提示的图片
     *
     * @param targetView targetView
     */
    public void addHighLightGuidView(View targetView) {
        this.mTargetView = targetView;
    }

    /**
     * 设置需要高亮的View和提示的图片
     */
    public void addHighLightGuidView(Bitmap bitmap, RectangleBean rectangleBean) {
        this.mapRectangleBean = rectangleBean;
        this.mapBitmap = bitmap;
    }


    /**
     * 设置额外的边框宽度
     *
     * @param borderWidth borderWidth
     */
    @SuppressWarnings("unused")
    public void setBorderWidth(int borderWidth) {
        this.borderWitdh = borderWidth;
    }

    /**
     * 设置状态栏高度 默认是减去了一个状态栏高度 如果主题设置android:windowTranslucentStatus=true
     * 需要设置状态栏高度为0
     *
     * @param highLisghtPadding highLisghtPadding
     */
    @SuppressWarnings("unused")
    public void setHighLisghtPadding(int highLisghtPadding) {
        this.highLisghtPadding = highLisghtPadding;
    }

    /**
     * 设置关闭监听
     *
     * @param listener 关闭引导的监听事件
     */
    public void setOnDismissListener(OnDismissListener listener) {
        this.onDismissListener = listener;
    }

    /**
     * 清空画布
     */
    @SuppressWarnings("unused")
    public void clearBg() {
        if (mCanvas != null) {
            Paint paint = new Paint();
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            mCanvas.drawPaint(paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        }
        // 将其注入画布
        mCanvas = new Canvas(fgBitmap);
        // 绘制前景画布
        mCanvas.drawColor(maskColor);
    }


    public void setStyleType(int type) {
        this.mStyleType = type;
    }

    public void setMapStudyClick(OnMapStudyClickListener mapListener, int position) {
        this.mapListener = mapListener;
        this.mapPosition = position;
    }

    /**
     * 后去控件的位置
     *
     * @return 图标的位置
     */
    public Rect getLightViewLocation() {
        return rtLocation;
    }

    private OnDrawCompleteListener mListener;

    public void setOnDrawCompleteListener(OnDrawCompleteListener listener) {
        this.mListener = listener;
    }

    public interface OnDrawCompleteListener {
        void onDrawComplete();
    }

}
