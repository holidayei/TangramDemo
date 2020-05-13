package com.holiday.tangram.support;

import android.view.View;

import com.holiday.tangram.util.QrToast;
import com.tmall.wireless.tangram.structure.BaseCell;
import com.tmall.wireless.tangram.support.SimpleClickSupport;

public class MyClickSupport extends SimpleClickSupport {
    public MyClickSupport() {
        setOptimizedMode(true);
    }

    @Override
    public void defaultClick(View targetView, BaseCell cell, int eventType) {
        super.defaultClick(targetView, cell, eventType);
        QrToast.show(cell.stringType);
    }
}
