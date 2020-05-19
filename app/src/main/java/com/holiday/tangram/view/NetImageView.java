package com.holiday.tangram.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.holiday.tangram.R;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * 加载网络图片
 */
public class NetImageView extends AppCompatImageView {
    private static final String TAG = "NetImageView";

    public NetImageView(Context context) {
        super(context);
    }

    public NetImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NetImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @BindingAdapter({"url"})
    public static void load(ImageView view, String url) {
        Log.e(TAG, "load url = " + url);
        Glide.with(view.getContext()).load(url).
                error(R.mipmap.ic_launcher).
                into(view);
    }

    public void load(String url) {
        Log.e(TAG, "load url = " + url);
        Glide.with(getContext()).load(url).
                error(R.mipmap.ic_launcher).
                into(this);
    }

    public void load(String url, int corner) {
        Log.e(TAG, "load url = " + url);
        RoundedCornersTransformation transformation = new RoundedCornersTransformation
                (corner, 0, RoundedCornersTransformation.CornerType.TOP);
        Glide.with(getContext()).load(url).
                error(R.mipmap.ic_launcher).
                apply(RequestOptions.bitmapTransform(transformation)).
                into(this);
    }
}
