package com.example.administrator.yys.kongjian;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.example.administrator.yys.utils.MyApplication;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.administrator.yys.utils.IPAddress.IP;

/**
 * Created by Administrator on 2017/7/7 0007.
 */

public class KongJian_SetPassword extends Activity implements View.OnClickListener{
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
        AppManager.getAppManager().addActivity(this);
        t1 = findViewById(R.id.t1);
        t2 = findViewById(R.id.t2);
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
                    Toast.makeText(KongJian_SetPassword.this,"密码必须为6位数",Toast.LENGTH_SHORT).show();
                }else{
                    if (password1.equals("")){
                        password1 = edit.getText().toString();
                        edit.setText("");
                        tishi.setText("确认密码");
                    }else if (password2.equals("")){
                            password2 = edit.getText().toString();
                        if (password2.equals(password1)){
                            //提交密码
                            String url = IP+"lifetime/space/account/setSpacePwd";
                            RequestParams params = new RequestParams();
                            params.addBodyParameter("token",token);
                            params.addBodyParameter("spacePassword",password2);
                            HttpUtils httputils = new HttpUtils();
                            Log.w("token:",token);
                            httputils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
                                @Override
                                public void onSuccess(ResponseInfo<String> responseInfo) {
                                    Log.w("setpasssuccess:",responseInfo.result.toString());
                                    try {
                                        JSONObject obj = new JSONObject(responseInfo.result.toString());
                                        String code = obj.getString("code");
                                        if (code.equals("1")){
                                            finish();
                                            Intent intent = new Intent();
                                            intent.putExtra("index","kongjian");
                                            intent.setAction("android.intent.action.UPDATE_FRAGMENT");
                                            sendBroadcast(intent);
                                            Toast.makeText(getApplicationContext(),"密码不可找回，请牢记",Toast.LENGTH_SHORT).show();
                                            MyApplication.getMyApplicationInstance().setSetpassword(1);
                                            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                @Override
                                public void onFailure(HttpException e, String s) {
                                    Log.w("setpassfaile",s.toString());
                                }
                            });
                        }else{
                            Toast.makeText(KongJian_SetPassword.this,"两次密码不一致，请重新输入",Toast.LENGTH_SHORT).show();
                            edit.setText("");
                            password1 = "";
                            password2 = "";
                            tishi.setText("输入密码");
                        }
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
