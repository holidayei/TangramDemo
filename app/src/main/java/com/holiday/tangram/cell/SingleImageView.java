package com.holiday.tangram.cell;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.holiday.tangram.util.ImgUrlParseUtil;
import com.holiday.tangram.view.NetImageView;
import com.tmall.wireless.tangram.structure.BaseCell;
import com.tmall.wireless.tangram.structure.view.ITangramViewLifeCycle;

/**
 * 自定义cell：单图
 */
public class SingleImageView extends NetImageView implements ITangramViewLifeCycle {
    public SingleImageView(Context context) {
        this(context, null);
    }

    public SingleImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SingleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setScaleType(ScaleType.CENTER_CROP);
    }

    @Override
    public void cellInited(BaseCell cell) {
        setOnClickListener(cell);
    }

    @Override
    public void postBindView(BaseCell cell) {
        String imgUrl = cell.optStringParam("imgUrl");
        int[] wh = ImgUrlParseUtil.parse(imgUrl);
        //设置宽高
        if (null == getLayoutParams() || wh[0] != getLayoutParams().width || wh[1] != getLayoutParams().width)
            setLayoutParams(new ViewGroup.LayoutParams(wh[0], wh[1]));
        load(imgUrl);
    }

    @Override
    public void postUnBindView(BaseCell cell) {

    }
}
