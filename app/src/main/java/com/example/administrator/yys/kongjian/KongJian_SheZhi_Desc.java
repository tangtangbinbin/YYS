package com.example.administrator.yys.kongjian;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.yys.R;
import com.example.administrator.yys.utils.AppManager;

/**
 * Created by Administrator on 2017/6/30 0030.
 */

public class KongJian_SheZhi_Desc extends Activity implements View.OnClickListener{
    EditText edit;
    TextView text;
    LinearLayout fanhui;
    TextView wancheng;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kongjian_miaoshu_xiugai);
        AppManager.getAppManager().addActivity(this);
        edit = (EditText) findViewById(R.id.kj_shezhi_miaoshu_edit);
        text = (TextView) findViewById(R.id.kj_shezhi_miaoshu_size);
        fanhui = (LinearLayout) findViewById(R.id.kongjian_shezhi_desc_fanhui);
        wancheng = (TextView) findViewById(R.id.kongjian_miaoshu_xiugai_wancheng);

        fanhui.setOnClickListener(this);
        wancheng.setOnClickListener(this);

        Intent intent = getIntent();
        String desc = intent.getStringExtra("descstr");
        edit.setText(desc);
        text.setText(desc.length()+"");
        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String context = edit.getText().toString();
                text.setText(context.length()+"");

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.kongjian_miaoshu_xiugai_wancheng:
                Intent intent = new Intent();
                intent.putExtra("desc",edit.getText().toString());
                KongJian_SheZhi_Desc.this.setResult(3,intent);
                finish();
                break;
            case R.id.kongjian_shezhi_desc_fanhui:
                finish();
                break;

        }
    }
}
