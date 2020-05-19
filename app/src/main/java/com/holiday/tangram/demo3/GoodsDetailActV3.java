package com.holiday.tangram.demo3;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.holiday.tangram.R;
import com.holiday.tangram.databinding.ActivityGoodsDetailBinding;


public class GoodsDetailActV3 extends AppCompatActivity {
    ActivityGoodsDetailBinding mBinding;
    MyTangramEngine mEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_goods_detail);
        //构建引擎，传入必需的参数
        mEngine = MyTangramEngine.build(this, "goods_detail_v3", mBinding.rvList);
        //初始化其他参数，多了再抽成builder
        mEngine.init(true);
        //加载
        mEngine.load();
    }

}
