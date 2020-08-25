package com.founq.sdk.klotski;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

/**
 * @author
 * @Title DensityUtils
 * @Package
 * @Description DensityUtils是一个像素与dp转换的工具
 * @date
 */
public class DensityUtils {
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     *
     * @param context
     * @param dpValue dp值
     * @return 返回像素值
     */
    public static int dipTopx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f * (dpValue >= 0 ? 1 : -1));
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     *
     * @param context
     * @param pxValue 像素值
     * @return 返回dp值
     */
    public static int pxTodip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int pxTosp(Context context, float pxValue, float fontScale) {
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int spTopx(float spValue, float fontScale) {
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 获得屏幕尺寸
     *
     * @param context
     * @return
     */
    public static int[] getScreenSize(Context context) {
        int[] screenSize = new int[2];
        int measuredWidth = 0;
        int measuredheight = 0;
        Point size = new Point();
        WindowManager w = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            w.getDefaultDisplay().getSize(size);
            measuredWidth = size.x;
            measuredheight = size.y;
        } else {
            Display d = w.getDefaultDisplay();
            measuredWidth = d.getWidth();
            measuredheight = d.getHeight();
        }
        screenSize[0] = measuredWidth;
        screenSize[1] = measuredheight;

        return screenSize;
    }

    public static final int DPI_NORMAL = 0x00;
    public static final int DPI_LARGE = 0x01;
    public static final int DPI_XLARGE = 0x02;
    public static final int DPI_XXLARGE = 0x03;

    /**
     * 获得屏幕尺寸类型
     *
     * @param activity
     * @return 0-small 1-normal 2-large 3-xlarge
     */
    public static int getScreenDensityDPI(Activity activity) {
        DisplayMetrics metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        float density = metric.density;  // 屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = metric.densityDpi;  // 屏幕密度DPI（120 / 160 / 240）

        int level;
        if (densityDpi <= 120) {
            level = DPI_NORMAL;
        } else if (densityDpi <= 160) {
            level = DPI_LARGE;
        } else if (densityDpi <= 240) {
            level = DPI_XLARGE;
        } else {
            level = DPI_XXLARGE;
        }

        return level;
    }

    /**
     * 获取状态栏高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        // 这里只需要获取屏幕高度
        int screenHeight = getScreenSize(context)[1];

        switch (screenHeight) {
            case 240:
                statusBarHeight = 20;
                break;
            case 480:
                statusBarHeight = 25;
                break;
            case 800:
                statusBarHeight = 38;
                break;
            default:
                statusBarHeight = 25;
                break;
        }
        return statusBarHeight;
    }

    /**
     * 屏幕适配
     *
     * @param context
     * @return 适配后的宽高
     */
    public static int[] getAdaptScreenSize(Context context) {
        int[] screenSize = getScreenSize(context);

        if (screenSize[0] <= screenSize[1]) {    //竖屏
            screenSize[0] *= 0.8;
        } else {
            screenSize[0] *= 0.6;
        }
        return screenSize;
    }

    /**
     * 计算图片缩放值
     *
     * @param width    原本的宽度
     * @param reqWidth 希望小于的宽度
     * @return
     */
    public static float calculateInSampleSize(int width, int reqWidth) {
        float inSampleSize = 1f;

        if (width > reqWidth) {
            float widthRatio = (float) width / (float) reqWidth;
            inSampleSize = widthRatio;
        }
        return inSampleSize;
    }

    // 计算出该TextView中文字的长度(像素)
    public static float getTextViewLength(Context context, String text) {
        Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(DensityUtils.spTopx(15, context.getResources().getDisplayMetrics().scaledDensity));
        // Measure the width of the text string.
        return mTextPaint.measureText(text);
    }

}
