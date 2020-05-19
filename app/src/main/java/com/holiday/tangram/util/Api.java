package com.holiday.tangram.util;

import com.holiday.tangram.MyApp;
import com.holiday.tangram.bean.ArticleBean;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Response;


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

    //支持泛型使用
    public abstract static class BaseCallback<T> extends Callback<T> {
        private Type mType;

        public BaseCallback() {
            mType = getSuperclassTypeParameter(getClass());
        }

        private Type getSuperclassTypeParameter(Class<?> subclass) {
            Type superclass = subclass.getGenericSuperclass();
            if (superclass instanceof Class) {
                throw new RuntimeException("Missing type parameter.");
            }
            ParameterizedType parameterized = (ParameterizedType) superclass;
            return parameterized.getActualTypeArguments()[0];
        }

        @Override
        public T parseNetworkResponse(Response response, int id) throws Exception {
            return MyApp.gson.fromJson(response.body().string(), mType);
        }

        @Override
        public void onError(Call call, Exception e, int id) {

        }
    }
}
