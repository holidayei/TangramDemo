package com.holiday.tangram.demo3;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.holiday.tangram.MyApp;
import com.holiday.tangram.bean.GoodsBean;
import com.holiday.tangram.bean.ResultBean;
import com.holiday.tangram.cell.GoodsItemView;
import com.holiday.tangram.cell.GoodsItemViewV3;
import com.holiday.tangram.cell.ImageTextView;
import com.holiday.tangram.cell.SingleImageView;
import com.holiday.tangram.cell.SingleTextView;
import com.holiday.tangram.support.MyClickSupport;
import com.holiday.tangram.util.Api;
import com.holiday.tangram.util.FileUtil;
import com.holiday.tangram.util.JsonUtil;
import com.holiday.tangram.util.QrLog;
import com.holiday.tangram.util.QrToast;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tmall.wireless.tangram.TangramBuilder;
import com.tmall.wireless.tangram.TangramEngine;
import com.tmall.wireless.tangram.dataparser.concrete.Card;
import com.tmall.wireless.tangram.structure.BaseCell;
import com.tmall.wireless.tangram.structure.card.StaggeredCard;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.Call;

/**
 * 简单封装，改成非继承使用
 */
public class MyTangramEngine implements DefaultLifecycleObserver {
    /**
     * 注：实际业务中，并非写死各类模板地址，而是根据业务域和策略来决定使用本地还是远程模板，模板的version是多少等等
     */
    //本地模板
    private static final String LOCAL_TEMPLATE = "local_%s.json";
    //远程模板地址，%s为业务域
    private String mNetTemplateUrl = "http://rest.apizza.net/mock/3f233eed2d9be716a5f48fccb9c719f2/tangram/template/net_%s.json";
    private RecyclerView mRecyclerView;
    private SmartRefreshLayout mRefreshLayout;
    private ProgressBar mProgressBar;
    private TangramEngine mEngine;
    //业务域
    private String mBizDomain;
    //模板对象
    private JSONObject mTemplate;
    //瀑布流数据页码
    private int mListDataPage = 0;
    private Context mContext;
    private boolean mNeedRefreshAndLoadMore;
    private boolean mUseRemoteTemplate;

    private MyTangramEngine(Context context, String bizDomain, RecyclerView recyclerView) {
        mBizDomain = bizDomain;
        mRecyclerView = recyclerView;
        mContext = context;
    }

    public static MyTangramEngine build(Context context, String bizDomain, RecyclerView recyclerView) {
        return new MyTangramEngine(context, bizDomain, recyclerView);
    }

    public void init(boolean needRefreshAndLoadMore) {
        if (TextUtils.isEmpty(mBizDomain) || null == mRecyclerView || null == mContext) {
            //每个页面必须指明业务域，提供RecyclerView来渲染模板
            return;
        }
        mNeedRefreshAndLoadMore = needRefreshAndLoadMore;
        //订阅宿主act的生命周期
        if (mContext instanceof AppCompatActivity) {
            ((AppCompatActivity) mContext).getLifecycle().addObserver(this);
        }
        //初始化下拉刷新和加载更多
        initRefreshLayoutIfNeed();
        initLoadingView();

        //创建builder来配置参数
        TangramBuilder.InnerBuilder builder = TangramBuilder.newInnerBuilder(mContext);
        //注册自己的cell
        builder.registerCell(ImageTextView.class.getSimpleName(), ImageTextView.class);
        builder.registerCell(SingleImageView.class.getSimpleName(), SingleImageView.class);
        builder.registerCell(GoodsItemView.class.getSimpleName(), GoodsItemView.class);
        builder.registerCell(GoodsItemViewV3.class.getSimpleName(), GoodsItemViewV3.class);
        builder.registerCell(SingleTextView.class.getSimpleName(), SingleTextView.class);

        //创建引擎
        mEngine = builder.build();
        //绑定RecyclerView
        mEngine.bindView(mRecyclerView);

        //support
        mEngine.addSimpleClickSupport(new MyClickSupport());
    }


    public void load() {
        //加载模板
        loadTemplate();
    }

    //加载模板，可以按需写一个策略，比如日常使用本地模板，大促使用远程下发的模板
    private void loadTemplate() {
        mProgressBar.setVisibility(View.VISIBLE);
        mListDataPage = 0;
        if (mUseRemoteTemplate) {
            Api.get(String.format(mNetTemplateUrl, mBizDomain), new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    QrLog.e("远程模板加载失败");
                }

                @Override
                public void onResponse(String json, int id) {
                    if (!TextUtils.isEmpty(json)) {
                        try {
                            mTemplate = new JSONObject(json);
                            QrToast.show("使用远程模板 = " + JsonUtil.getString(mTemplate, "templateName"));
                            loadMakeupData();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } else {
            String json = FileUtil.getAssertsFile(mContext, String.format(LOCAL_TEMPLATE, mBizDomain));
            if (!TextUtils.isEmpty(json)) {
                try {
                    mTemplate = new JSONObject(json);
                    QrToast.show("使用本地模板 = " + JsonUtil.getString(mTemplate, "templateName"));
                    loadMakeupData();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //加载聚合数据
    private void loadMakeupData() {
        Api.get(JsonUtil.getString(mTemplate, "requestMakeup"), new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                QrLog.e("聚合数据加载失败");
            }

            @Override
            public void onResponse(String response, int id) {
                QrLog.e("聚合数据 = " + response);
                try {
                    mergeMakeupDataToTemplate(new JSONObject(response).getJSONObject("data"), mTemplate.getJSONArray("template"));
                    mEngine.setData(mTemplate.getJSONArray("template"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                loadListData(false);
            }
        });
    }

    //把聚合数据合并进模板对象的template字段
    private void mergeMakeupDataToTemplate(JSONObject data, JSONArray template) throws JSONException {
        //遍历每一个卡片（布局），把数据填充进字段items
        for (int i = 0; i < template.length(); i++) {
            JSONObject card = template.getJSONObject(i);
            //如果card有load字段，并且字段值是makeup:开头，表示card的数据源为聚合数据
            if (card.has("load") && card.getString("load").startsWith("makeup:")) {
                String load = card.getString("load");
                JSONArray cells = data.getJSONArray(load.substring(load.indexOf(":") + 1));
                //把模板配置的itemType即具体视图cell写进数据源
                for (int cellIdx = 0; cellIdx < cells.length(); cellIdx++) {
                    cells.getJSONObject(cellIdx).put("type", card.getString("itemType"));
                }
                card.put("items", cells);
            }
        }
    }

    //加载瀑布流数据
    private void loadListData(boolean loadMore) {
        String api = JsonUtil.getString(mTemplate, "requestList");
        if (TextUtils.isEmpty(api)) {
            finishLoad();
            return;
        }
        List<Card> groups = mEngine.getGroupBasicAdapter().getGroups();
        Card card = groups.get(groups.size() - 1);
        //如果最后一个Card不是瀑布流container-waterfall，跳过
        if (!(card instanceof StaggeredCard)) return;

        if (!loadMore) card.removeAllCells();

        OkHttpUtils.get().url(String.format(api, mListDataPage))
                .build()
                .execute(new Api.BaseCallback<ResultBean<List<GoodsBean>>>() {
                    @Override
                    public void onResponse(ResultBean<List<GoodsBean>> response, int id) {
                        List<GoodsBean> data = response.getData();
                        QrLog.e("商品列表 = " + data.size() + " , 页码 = " + mListDataPage);
                        parseListData(data, card);
                    }
                });
    }

    //解析瀑布流数据
    private void parseListData(List<GoodsBean> list, @NonNull Card card) {
        if (list.isEmpty()) {
            QrToast.show("没有更多数据了哦");
            finishLoad();
            return;
        }
        mListDataPage++;
        JSONArray cells = new JSONArray();
        try {
            for (int i = 0; i < list.size(); i++) {
                JSONObject obj = new JSONObject(MyApp.gson.toJson(list.get(i)));
                obj.put("type", card.optStringParam("itemType"));
                cells.put(obj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        List<BaseCell> cs = mEngine.parseComponent(cells);
        card.addCells(cs);
        card.notifyDataChange();
//        Range<Integer> cardRange = mEngine.getGroupBasicAdapter().getCardRange(card);
//        mEngine.getGroupBasicAdapter().notifyItemRangeInserted(cardRange.getLower(), list.size());
        finishLoad();
    }

    private void finishLoad() {
        if (null != mRefreshLayout) {
            RefreshState state = mRefreshLayout.getState();
            if (state.isFooter && state.isOpening) {
                mRefreshLayout.finishLoadMore();
            } else if (state.isHeader && state.isOpening) {
                mRefreshLayout.finishRefresh();
            }
        }
        mProgressBar.setVisibility(View.GONE);
    }

    //初始化下拉刷新和加载更多
    private void initRefreshLayoutIfNeed() {
        if (mNeedRefreshAndLoadMore && mRecyclerView.getParent() instanceof SmartRefreshLayout) {
            mRefreshLayout = (SmartRefreshLayout) mRecyclerView.getParent();
            mRefreshLayout.setEnableLoadMore(true);
            mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
                @Override
                public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                    mListDataPage = 0;
                    loadMakeupData();
                }
            });
            mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                    loadListData(true);
                }
            });
        }
    }

    private void initLoadingView() {
        if (mContext instanceof Activity) {
            mProgressBar = new ProgressBar(mContext);
            mProgressBar.setVisibility(View.GONE);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            ((Activity) mContext).addContentView(mProgressBar, params);
        }
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        QrLog.e("mEngine.destroy()");
        mEngine.destroy();
    }

    public void setUseRemoteTemplate(boolean useRemoteTemplate) {
        mUseRemoteTemplate = useRemoteTemplate;
    }

    public boolean isUseRemoteTemplate() {
        return mUseRemoteTemplate;
    }
}
