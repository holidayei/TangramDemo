package com.holiday.tangram.demo2;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.holiday.tangram.MyApp;
import com.holiday.tangram.bean.ArticleBean;
import com.holiday.tangram.cell.GoodsItemView;
import com.holiday.tangram.cell.ImageTextView;
import com.holiday.tangram.cell.SingleImageView;
import com.holiday.tangram.cell.SingleTextView;
import com.holiday.tangram.support.MyClickSupport;
import com.holiday.tangram.util.Api;
import com.holiday.tangram.util.DataUtil;
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
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.Call;

/**
 * 具备Tangram能力的Activity，如果业务没有太多定制逻辑，可以直接继承使用，需返回RecyclerView和业务域mBizDomain
 */
// TODO: 2020-05-17 banner不能设置margin和padding，会引起抖动
// TODO: 2020-05-16 card.notifyDataChange没法局部刷新
public abstract class TangramActivity extends AppCompatActivity {
    //本地模板
    private static final String LOCAL_TEMPLATE = "local_%s.json";
    //远程模板地址，%s为业务域
    private String mNetTemplateUrl = "http://rest.apizza.net/mock/3f233eed2d9be716a5f48fccb9c719f2/tangram/template/net_%s.json";
    /**
     * 注：实际业务中，并非写死各类模板地址，而是根据业务域和策略来决定使用本地还是远程模板，模板的version是多少等等
     */
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBizDomain = createBizDomain();
        mRecyclerView = createRecyclerView();
        //fix:首次进来滑动导致item乱移
//        mRecyclerView.setItemAnimator(null);
        if (TextUtils.isEmpty(mBizDomain) || null == mRecyclerView) {
            //每个页面必须指明业务域，提供RecyclerView来渲染模板
            return;
        }
        //初始化下拉刷新和加载更多
        initRefreshLayoutIfNeed();
        initLoadingView();

        //创建builder来配置参数
        TangramBuilder.InnerBuilder builder = TangramBuilder.newInnerBuilder(this);
        //注册自己的cell
        builder.registerCell(ImageTextView.class.getSimpleName(), ImageTextView.class);
        builder.registerCell(SingleImageView.class.getSimpleName(), SingleImageView.class);
        builder.registerCell(GoodsItemView.class.getSimpleName(), GoodsItemView.class);
        builder.registerCell(SingleTextView.class.getSimpleName(), SingleTextView.class);

        //创建引擎
        mEngine = builder.build();
        //绑定RecyclerView
        mEngine.bindView(mRecyclerView);

        //support
        mEngine.addSimpleClickSupport(new MyClickSupport());

        //加载模板
        loadTemplate();
    }

    //加载模板，可以按需写一个策略，比如日常使用本地模板，大促使用远程下发的模板
    protected void loadTemplate() {
        mProgressBar.setVisibility(View.VISIBLE);
        mListDataPage = 0;
        if (useRemoteTemplate()) {
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
            String json = FileUtil.getAssertsFile(this, String.format(LOCAL_TEMPLATE, mBizDomain));
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

        Api.get(String.format(api, mListDataPage), new Api.ArticleCallback() {
            @Override
            public void onSuccess(ArticleBean.DataBean data) {
                parseListData(data.getDatas(), card);
                mListDataPage++;
            }

            @Override
            public void onFail() {
                QrLog.e("瀑布流数据加载失败");
            }
        });
    }

    //解析瀑布流数据
    private void parseListData(List<ArticleBean.DataBean.Article> list, @NonNull Card card) {
        JSONArray cells = new JSONArray();
        try {
            for (int i = 0; i < list.size(); i++) {
                JSONObject obj = new JSONObject(MyApp.gson.toJson(list.get(i)));
                obj.put("type", card.optStringParam("itemType"));
                //由于使用了玩安卓的数据结构，这里手动添加一些参数用于演示
                obj.put("imgUrl", DataUtil.getImgByIdx(i + mListDataPage * list.size()));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mEngine.destroy();
    }

    private void initLoadingView() {
        mProgressBar = new ProgressBar(this);
        mProgressBar.setVisibility(View.GONE);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        addContentView(mProgressBar, params);
    }

    //初始化下拉刷新和加载更多
    private void initRefreshLayoutIfNeed() {
        if (needRefreshAndLoadMore() && mRecyclerView.getParent() instanceof SmartRefreshLayout) {
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

    abstract protected String createBizDomain();

    abstract protected RecyclerView createRecyclerView();

    //是否开启下拉刷新和加载更多
    protected boolean needRefreshAndLoadMore() {
        return false;
    }

    //是否使用远程模板，交给业务方根据自己的策略设置
    protected boolean useRemoteTemplate() {
        return false;
    }
}
