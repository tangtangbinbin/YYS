package com.example.administrator.yys.wode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.yys.R;
import com.example.administrator.yys.utils.AppManager;

/**
 * Created by Administrator on 2017/7/26 0026.
 */

public class WoDe_sheZhi_ZhangHao extends Activity implements View.OnClickListener{
    LinearLayout fanhui,bangding,xiugai;
    TextView zhanghao,phonenumber;
    String loginname;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wode_shezhi_zhanghao);
        fanhui = findViewById(R.id.wode_shezhi_zhanghao_fanhui);
        AppManager.getAppManager().addActivity(this);
        bangding = findViewById(R.id.wd_shezhi_zhanghao_bangding_lin);
        xiugai = findViewById(R.id.wd_shezhi_update_lin);
        phonenumber = findViewById(R.id.wd_shezhi_number);
        String mobile = getSharedPreferences("user",MODE_PRIVATE).getString("mobile","");
        if (mobile.length()==11){
            phonenumber.setText("已绑定（"+mobile+")");
        }
        zhanghao = findViewById(R.id.wode_shezhi_zhanghao);
        loginname = getSharedPreferences("user",MODE_PRIVATE).getString("login_name","");
        zhanghao.setText(loginname);
        fanhui.setOnClickListener(this);
        bangding.setOnClickListener(this);
        xiugai.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.wode_shezhi_zhanghao_fanhui:
                finish();
                break;
            case R.id.wd_shezhi_zhanghao_bangding_lin:
                Intent intent = new Intent();
                intent.setClass(WoDe_sheZhi_ZhangHao.this,WoDe_SheZhi_ZhangHao_BandDing.class);
                startActivity(intent);
                break;
            case R.id.wd_shezhi_update_lin:
                Intent intent1 = new Intent();
                intent1.setClass(WoDe_sheZhi_ZhangHao.this,WoDe_SheZhi_ZhangHao_XiuGai.class);
                startActivity(intent1);
                break;
        }
    }
}
