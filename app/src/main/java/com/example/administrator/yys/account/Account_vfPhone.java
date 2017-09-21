package com.example.administrator.yys.account;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.administrator.yys.R;
import com.example.administrator.yys.network.NetWorkRequest;
import com.example.administrator.yys.utils.AppManager;
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
 * Created by Administrator on 2017/6/15 0015.
 */

public class Account_vfPhone extends Activity {
    EditText account,code,phone;
    Button getbtn,finishbtn;
    LinearLayout fanhui;
    TimeCount time = new TimeCount(60000,1000);

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what==1){
                Log.w("sendCode",msg.obj.toString());
                try {
                    JSONObject jsonObject = new JSONObject(msg.obj.toString());
                    String code = jsonObject.getString("code");
                    if (code.equals("1")){
                        time.start();
                    }
                    String message = jsonObject.get("message").toString();
                    Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.forgetpass_vfphone);
        AppManager.getAppManager().addActivity(this);
        account = findViewById(R.id.vfphone_account);
        code = findViewById(R.id.vfphone_code);
        fanhui = findViewById(R.id.vfphone_fanhui);
        phone = findViewById(R.id.vfphone_phone);
        getbtn = findViewById(R.id.vfphone_btn);
        finishbtn = findViewById(R.id.vfphone_finish);

        fanhui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        /*account.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b){
                    //判断帐号是否存在
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String url = IP+"lifetime/user/account/checkUser?loginName="+account.getText().toString();
                            String  result =  new NetWorkRequest().getServiceInfo(url);
                            Message msg = new Message();
                            msg.obj = result;
                            msg.what=2;
                            handler.sendMessage(msg);
                        }
                    }).start();
                }
            }
        });*/
        getbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String strphone = phone.getText().toString();
                        if (strphone.length()!=11){
                            Toast.makeText(getApplicationContext(),"手机号输入有误",Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String url = IP+"lifetime/user/account/sendCodeToUptPwd?mobile="+strphone+"&loginName="+account.getText().toString();
                        String  result =  new NetWorkRequest().getServiceInfo(url);
                        Message msg = new Message();
                        msg.obj = result;
                        msg.what=1;
                        handler.sendMessage(msg);
                    }
                }).start();
            }
        });
        finishbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String straccount = account.getText().toString();
                String strphone = phone.getText().toString();
                String strcode = code.getText().toString();
                if (straccount.length()==0){
                    Toast.makeText(getApplicationContext(),"帐号输入有误",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (strphone.length()!=11){
                    Toast.makeText(getApplicationContext(),"手机号输入有误",Toast.LENGTH_SHORT).show();
                    return;
                }
                String url = IP+"lifetime/user/account/getPwdToCode";
                RequestParams params = new RequestParams();
                params.addBodyParameter("mobile",strphone);
                params.addBodyParameter("code",strcode);
                params.addBodyParameter("loginName",straccount);
                HttpUtils httputils = new HttpUtils();
                httputils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        Log.w("getpwd success:",responseInfo.result.toString());
                        try {
                            JSONObject obj = new JSONObject(responseInfo.result.toString());
                            String code = obj.getString("code");
                            if (code.equals("1")){
                                JSONObject datum = obj.getJSONObject("datum");
                                String pwdCode = datum.getString("pwdCode");
                                //重置密码
                                Intent intent = new Intent();
                                intent.putExtra("pwdCode",pwdCode);
                                intent.putExtra("straccount",straccount);
                                intent.setClass(Account_vfPhone.this,ReSet_PassWord.class);
                                startActivity(intent);
                            }else{
                                Toast.makeText(getApplicationContext(),obj.getString("message"),Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFailure(HttpException e, String s) {
                        Log.w("getpwd faile",s.toString());
                    }
                });

            }
        });
    }

    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
            getbtn.setClickable(false);
            getbtn.setBackgroundColor(Color.parseColor("#999999"));
            getbtn.setText(l/1000+"秒");
        }

        @Override
        public void onFinish() {
            getbtn.setText("重新获取");
            getbtn.setClickable(true);
            getbtn.setBackgroundColor(Color.parseColor("#f08f1d"));
        }
    }
}
