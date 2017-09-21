package com.example.administrator.yys.kongjian;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.yys.R;
import com.example.administrator.yys.utils.AppManager;

/**
 * Created by Administrator on 2017/7/7 0007.
 */

public class KongJian_GetPassword extends Activity implements View.OnClickListener{
    TextView t1,t2,t3,t4,t5,t6;
    LinearLayout hui_lin;
    EditText edit;
    String key;
    Button quxiao,queding;
    String password1 = "";
    String password2 = "";
    TextView tishi;
    String token;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kongjian_password);
        hui_lin = findViewById(R.id.kj_pass_hui_lin);
        t1 = findViewById(R.id.t1);
        t2 = findViewById(R.id.t2);
        AppManager.getAppManager().addActivity(this);
        t3 = findViewById(R.id.t3);
        t4 = findViewById(R.id.t4);
        t5 = findViewById(R.id.t5);
        t6 = findViewById(R.id.t6);
        token = getSharedPreferences("user",MODE_PRIVATE).getString("token","");
        quxiao = findViewById(R.id.kj_pass_quxiao);
        queding = findViewById(R.id.kj_pass_queding);
        edit = findViewById(R.id.kj_pass_edit);
        tishi = findViewById(R.id.kj_pass_tishi);
        edit.requestFocus();
        hui_lin.setOnClickListener(this);
        queding.setOnClickListener(this);
        quxiao.setOnClickListener(this);
        hui_lin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                finish();
                return imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });

        quxiao.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                finish();
                return imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });

        edit.addTextChangedListener(tw);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.kj_pass_queding:
                if (edit.getText().length()<6){
                    Toast.makeText(KongJian_GetPassword.this,"密码必须为6位数",Toast.LENGTH_SHORT).show();
                }else{
                    if (password1.equals("")){
                        password1 = edit.getText().toString();
                        Intent intent = new Intent();
                        intent.putExtra("password",password1);
                        KongJian_GetPassword.this.setResult(1,intent);
                        finish();
                    }

                }

                break;
        }
    }
    private  void setKey(){
        char arr[] = key.toCharArray();
        t1.setText("");
        t2.setText("");
        t3.setText("");
        t4.setText("");
        t5.setText("");
        t6.setText("");
        for (int i = 0; i < arr.length; i++) {
            if (i == 0) {
                t1.setText(String.valueOf(arr[0]));
            } else if (i == 1) {
                t2.setText(String.valueOf(arr[1]));
            } else if (i == 2) {
                t3.setText(String.valueOf(arr[2]));
            } else if (i == 3) {
                t4.setText(String.valueOf(arr[3]));
            }else if (i == 4) {
                t5.setText(String.valueOf(arr[4]));
            }else if (i == 5) {
                t6.setText(String.valueOf(arr[5]));
            }
        }
    }
    TextWatcher tw = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            key = editable.toString();
            setKey();
        }
    };
}
