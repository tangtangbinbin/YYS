package com.example.administrator.yys.account;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;

import com.example.administrator.yys.R;
import com.example.administrator.yys.utils.AppManager;

/**
 * Created by Administrator on 2017/8/16 0016.
 */

public class YongHuXieYi extends Activity {
    LinearLayout fanhui;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        setContentView(R.layout.yonghuxieyi);
        fanhui = findViewById(R.id.yonghuxieyi_fanhui);
        fanhui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
