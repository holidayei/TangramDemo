package com.holiday.tangram.util;

import android.widget.Toast;

import com.holiday.tangram.MyApp;

public class QrToast {
    public static void show(String s) {
        Toast.makeText(MyApp.app, s, Toast.LENGTH_SHORT).show();
    }
}
