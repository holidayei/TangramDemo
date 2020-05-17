package com.holiday.tangram.cell;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.tmall.wireless.tangram.structure.BaseCell;
import com.tmall.wireless.tangram.structure.view.ITangramViewLifeCycle;

/**
 * 自定义cell：纯文本
 */
public class SingleTextView extends AppCompatTextView implements ITangramViewLifeCycle {
    public SingleTextView(Context context) {
        this(context, null);
    }

    public SingleTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SingleTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setGravity(Gravity.CENTER);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    @Override
    public void cellInited(BaseCell cell) {
    }

    @Override
    public void postBindView(BaseCell cell) {
        setText(cell.optStringParam("title"));

        String textColor = cell.parent.optStringParam("textColor");
        setTextColor(Color.parseColor(TextUtils.isEmpty(textColor) ? "#333333" : textColor));
    }

    @Override
    public void postUnBindView(BaseCell cell) {

    }
}
