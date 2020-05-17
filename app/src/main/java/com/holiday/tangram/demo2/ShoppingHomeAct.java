package com.holiday.tangram.demo2;

import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.holiday.tangram.R;
import com.holiday.tangram.databinding.ActivityShoppingHomeBinding;

/**
 * 模板和数据分离，目前选了两个场景进行实践：
 * 1. 商城首页：业务域shopping_home，本地模板local_shopping_home，远程模板net_shopping_home
 * 2. 商品详情：业务域goods_detail，本地模板local_goods_detail，远程模板net_goods_detail
 */
public class ShoppingHomeAct extends TangramActivity {
    ActivityShoppingHomeBinding mBinding;
    boolean mUseRemoteTemplate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_shopping_home);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected String createBizDomain() {
        return "shopping_home";//业务域：商城首页
    }

    @Override
    protected RecyclerView createRecyclerView() {
        return mBinding.rvList;
    }

    @Override
    protected boolean needRefreshAndLoadMore() {
        return true;
    }

    @Override
    protected boolean useRemoteTemplate() {
        return mUseRemoteTemplate;
    }

    public void changeTemplate(View view) {
        mBinding.rvList.scrollToPosition(0);
        mUseRemoteTemplate = !mUseRemoteTemplate;
        loadTemplate();
    }
}
