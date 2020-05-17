package com.holiday.tangram.util;

import com.holiday.tangram.MyApp;

public class DensityUtil {

    public static int dip2px(float dpValue) {
        float scale = MyApp.app.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5F * (float) (dpValue >= 0.0F ? 1 : -1));
    }

    public static int dimenDip2px(int dimenId) {
        float value = MyApp.app.getResources().getDimension(dimenId);
        return dip2px(value / MyApp.app.getResources().getDisplayMetrics().density);
    }

    public static int px2dip(float pxValue) {
        float scale = MyApp.app.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5F * (float) (pxValue >= 0.0F ? 1 : -1));
    }

    public static int px2sp(float pxValue) {
        float scale = MyApp.app.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / scale + 0.5F * (float) (pxValue >= 0.0F ? 1 : -1));
    }

    public static int sp2px(float spValue) {
        float scale = MyApp.app.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * scale + 0.5F * (float) (spValue >= 0.0F ? 1 : -1));
    }
}




