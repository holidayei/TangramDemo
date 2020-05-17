package com.holiday.tangram.demo2;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.holiday.tangram.R;
import com.holiday.tangram.databinding.ActivityGoodsDetailBinding;

/**
 * 模板和数据分离，目前选了两个场景进行实践：
 * 1. 商城首页：业务域shopping_home，本地模板local_shopping_home，远程模板net_shopping_home
 * 2. 商品详情：业务域goods_detail，本地模板local_goods_detail，远程模板net_goods_detail
 */
public class GoodsDetailAct extends TangramActivity {
    ActivityGoodsDetailBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_goods_detail);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected String createBizDomain() {
        return "goods_detail";//业务域：商品详情
    }

    @Override
    protected RecyclerView createRecyclerView() {
        return mBinding.rvList;
    }
}
