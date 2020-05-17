package com.holiday.tangram.cell;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

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
        load(cell.optStringParam("imgUrl"));
    }

    @Override
    public void postUnBindView(BaseCell cell) {

    }
}
