package com.holiday.tangram.util;

import com.holiday.tangram.MyApp;
import com.holiday.tangram.bean.ArticleBean;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;


public class Api {

    //获取玩安卓文章列表
    public static void get(String api, ArticleCallback callback) {
        OkHttpUtils.get().url(api).build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        ArticleBean articleBean = MyApp.gson.fromJson(response, ArticleBean.class);
                        QrLog.e("玩安卓文章列表：页码 = " + articleBean.getData().getCurPage() +
                                "，数量 = " + articleBean.getData().getDatas().size());
                        callback.onSuccess(articleBean.getData());
                    }
                });
    }

    public static void get(String api, StringCallback callback) {
        OkHttpUtils.get().url(api).build()
                .execute(callback);
    }

    public interface ArticleCallback {
        void onSuccess(ArticleBean.DataBean data);

        void onFail();
    }
}
