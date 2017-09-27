package com.example.administrator.yys.wode;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;

import com.example.administrator.yys.R;

/**
 * Created by Administrator on 2017/9/25 0025.
 */

public class WoDe_FenXiang extends Activity {
    LinearLayout fanhui;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wode_shezhi_fenxiang);
        fanhui = findViewById(R.id.wode_shezhi_fenxiang_fanhui);
        fanhui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
