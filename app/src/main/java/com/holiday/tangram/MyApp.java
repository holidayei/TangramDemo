package com.holiday.tangram;

import android.app.Application;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.holiday.tangram.view.NetImageView;
import com.tmall.wireless.tangram.TangramBuilder;
import com.tmall.wireless.tangram.util.IInnerImageSetter;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.https.HttpsUtils;

import okhttp3.OkHttpClient;

public class MyApp extends Application {
    public static MyApp app;
    public static Gson gson = new Gson();

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;

        TangramBuilder.init(this, new IInnerImageSetter() {
            @Override
            public <IMAGE extends ImageView> void doLoadImageUrl(@NonNull IMAGE view, @Nullable String url) {
                Glide.with(view.getContext()).load(url).
                        error(R.mipmap.ic_launcher).
                        into(view);
            }
        }, NetImageView.class);

        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .build();
        OkHttpUtils.initClient(okHttpClient);
    }
}
