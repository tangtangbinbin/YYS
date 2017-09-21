package com.example.administrator.yys.huodong;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.yys.R;

/**
 * Created by Administrator on 2017/9/12 0012.
 */

public class HuoDong_qinglvzheng_Info extends Activity {
    LinearLayout fanhui;
    TextView lingqu;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.huodong_info);
        fanhui = findViewById(R.id.huodong_fanhui);
        lingqu = findViewById(R.id.huodong_lingqu);
        fanhui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        lingqu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(HuoDong_qinglvzheng_Info.this,HuoDong_qinglvzheng_Make.class);
                startActivity(intent);
            }
        });

    }
}
