package com.holiday.tangram.cell;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.holiday.tangram.R;
import com.holiday.tangram.view.NetImageView;
import com.tmall.wireless.tangram.structure.BaseCell;
import com.tmall.wireless.tangram.structure.view.ITangramViewLifeCycle;

/**
 * 自定义cell
 */
public class ImageTextView extends LinearLayout implements ITangramViewLifeCycle {
    private NetImageView mImgIcon;
    private TextView mTvTitle;

    public ImageTextView(Context context) {
        this(context, null);
    }

    public ImageTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        inflate(getContext(), R.layout.cell_image_text, this);
        mImgIcon = findViewById(R.id.img_icon);
        mTvTitle = findViewById(R.id.tv_title);
    }

    @Override
    public void cellInited(BaseCell cell) {
    }

    @Override
    public void postBindView(BaseCell cell) {
        mImgIcon.load(cell.optStringParam("imgUrl"));
        mTvTitle.setText(cell.optStringParam("title"));
    }

    @Override
    public void postUnBindView(BaseCell cell) {
    }
}
