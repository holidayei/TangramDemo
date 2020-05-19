package com.holiday.tangram.cell;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.holiday.tangram.R;
import com.holiday.tangram.util.ImgUrlParseUtil;
import com.holiday.tangram.util.ScreenUtil;
import com.holiday.tangram.view.NetImageView;
import com.tmall.wireless.tangram.dataparser.concrete.Style;
import com.tmall.wireless.tangram.structure.BaseCell;
import com.tmall.wireless.tangram.structure.view.ITangramViewLifeCycle;

/**
 * 自定义cell：商品样式
 */
public class GoodsItemViewV3 extends LinearLayout implements ITangramViewLifeCycle {
    private NetImageView mImgIcon;
    private TextView mTvTitle;

    public GoodsItemViewV3(Context context) {
        this(context, null);
    }

    public GoodsItemViewV3(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GoodsItemViewV3(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        inflate(getContext(), R.layout.cell_goods_item_v3, this);
        mImgIcon = findViewById(R.id.img_icon);
        mTvTitle = findViewById(R.id.tv_title);
    }

    @Override
    public void cellInited(BaseCell cell) {
        setOnClickListener(cell);
    }

    @Override
    public void postBindView(BaseCell cell) {
        mTvTitle.setText(cell.optStringParam("title"));

        String itemBgColor = cell.parent.optStringParam("itemBgColor");
        setBackgroundColor(Color.parseColor(TextUtils.isEmpty(itemBgColor) ? "#333333" : itemBgColor));

        String textColor = cell.parent.optStringParam("textColor");
        mTvTitle.setTextColor(Color.parseColor(TextUtils.isEmpty(textColor) ? "#333333" : textColor));

        String imgUrl = cell.optStringParam("imgUrl");
        int[] wh = ImgUrlParseUtil.parse(imgUrl);
        Style style = cell.parent.style;
        // TODO: 2020-05-19  应该从style取配置来计算item宽度，不过StaggeredStyle没有暴露出来，只能用jsonObject取，先写死吧
        int w = (ScreenUtil.screenW() - ScreenUtil.dip2px(9 + 9 + 4)) / 2;
        int h = wh[1] * w / wh[0];
        //设置宽高
//        if (null == mImgIcon.getLayoutParams()
//                || w != mImgIcon.getLayoutParams().width || h != mImgIcon.getLayoutParams().width) {
        mImgIcon.setLayoutParams(new LinearLayout.LayoutParams(w, h));
//            QrLog.e("w = " + w + " , h = " + h + " , " + cell.optStringParam("title"));
//        }

        mImgIcon.load(imgUrl, ScreenUtil.dip2px(5f));

        //tangram没法局部刷新，每次加载更多引起页面内所有item的rebind
//        QrLog.e("postBindView = " + cell.optStringParam("title"));
    }

    @Override
    public void postUnBindView(BaseCell cell) {
    }
}
