package com.holiday.tangram.demo3;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.holiday.tangram.R;
import com.holiday.tangram.databinding.ActivityShoppingHomeV3Binding;


public class ShoppingHomeActV3 extends AppCompatActivity {
    ActivityShoppingHomeV3Binding mBinding;
    MyTangramEngine mEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_shopping_home_v3);

        //构建引擎，传入必需的参数
        mEngine = MyTangramEngine.build(this, "shopping_home_v3", mBinding.rvList);
        //初始化其他参数，多了再抽成builder
        mEngine.init(true);
        //加载
        mEngine.load();
    }

    public void changeTemplate(View view) {
        mBinding.rvList.scrollToPosition(0);
        mEngine.setUseRemoteTemplate(!mEngine.isUseRemoteTemplate());
        mEngine.load();
    }
}
