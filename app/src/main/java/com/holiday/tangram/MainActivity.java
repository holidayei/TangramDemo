package com.holiday.tangram;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.holiday.tangram.cell.ImageTextView;
import com.holiday.tangram.cell.SingleImageView;
import com.holiday.tangram.databinding.ActivityMainBinding;
import com.holiday.tangram.support.MyClickSupport;
import com.holiday.tangram.util.FileUtil;
import com.tmall.wireless.tangram.TangramBuilder;
import com.tmall.wireless.tangram.TangramEngine;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * 使用文档：https://github.com/alibaba/Tangram-Android/blob/master/docs/Tutorial-ch.md
 * 参数文档：http://tangram.pingguohe.net/docs/layout-support/linearscroll-layout
 */
public class MainActivity extends AppCompatActivity {

    ActivityMainBinding mBinding;
    TangramEngine mEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        //创建builder来配置参数
        TangramBuilder.InnerBuilder builder = TangramBuilder.newInnerBuilder(this);
        //注册自己的cell
        builder.registerCell(ImageTextView.class.getSimpleName(), ImageTextView.class);
        builder.registerCell(SingleImageView.class.getSimpleName(), SingleImageView.class);

        //创建引擎
        mEngine = builder.build();
        //绑定RecyclerView
        mEngine.bindView(mBinding.rvList);

        //support
        mEngine.addSimpleClickSupport(new MyClickSupport());

        mBinding.rvList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //在 scroll 事件中触发 engine 的 onScroll，内部会触发需要异步加载的卡片去提前加载数据
                mEngine.onScrolled();
            }
        });

        //设置数据，触发渲染
        String file = FileUtil.getAssertsFile(this, "main.json");
        try {
            mEngine.setData(new JSONArray(file));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mEngine.destroy();
    }
}
