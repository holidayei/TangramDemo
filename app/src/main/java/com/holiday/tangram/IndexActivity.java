package com.holiday.tangram;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.holiday.tangram.demo2.GoodsDetailAct;
import com.holiday.tangram.demo2.ShoppingHomeAct;

public class IndexActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
    }

    public void toMain(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }

    public void toShoppingHome(View view) {
        startActivity(new Intent(this, ShoppingHomeAct.class));
    }

    public void toGoodsDetail(View view) {
        startActivity(new Intent(this, GoodsDetailAct.class));
    }
}
