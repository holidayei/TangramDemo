package com.holiday.tangram.util;

import android.text.TextUtils;

/**
 * 图片地址解析工具
 */
public class ImgUrlParseUtil {
    //最大像素
    private static final int MAX_PX = 750;

    //根据图片地址，解析出宽高，https://XXX_640x959.jpg
    public static int[] parse(String url) {
        int[] wh = new int[2];
        if (TextUtils.isEmpty(url)) return wh;
        int idxUnderline = url.lastIndexOf('_');
        int idxDot = url.lastIndexOf('.');
        int idx = url.lastIndexOf('x');
        if (idxUnderline > 0 && idxDot > 0 && idx > 0) {
            try {
                wh[0] = Integer.parseInt(url.substring(idxUnderline + 1, idx));
                wh[1] = Integer.parseInt(url.substring(idx + 1, idxDot));
            } catch (Exception e) {
            }
            int max = Math.max(wh[0], wh[1]);
            if (max > MAX_PX) {
                //进行缩小
                float ratio = max * 1f / MAX_PX;
                wh[0] = (int) (wh[0] / ratio);
                wh[1] = (int) (wh[1] / ratio);
            }
        } else {
            QrLog.e("图片地址没有宽高信息 = " + url);
        }
//        QrLog.e("图片解析 w = " + wh[0] + " , h = " + wh[1]);
        return wh;
    }
}
