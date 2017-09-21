package com.example.administrator.yys.kongjian;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.yys.R;
import com.example.administrator.yys.utils.AppManager;

/**
 * Created by Administrator on 2017/7/18 0018.
 */

public class KongJian_InPut extends Activity {
    LinearLayout lin;
    TextView queding;
    EditText hudong_input;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hudong_input);
        lin = findViewById(R.id.input_lin_hui);
        queding = findViewById(R.id.input_queding);
        Intent intent = getIntent();
        AppManager.getAppManager().addActivity(this);
        String content = intent.getStringExtra("content");
        hudong_input = findViewById(R.id.hudong_input);
        hudong_input.setText(content);

        //点击屏幕关闭软键盘
        lin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                finish();
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                return imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });
        queding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = hudong_input.getText().toString();
                    Intent intent = new Intent();
                    intent.putExtra("content",content);
                    KongJian_InPut.this.setResult(3,intent);
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    finish();

            }
        });
    }
}
