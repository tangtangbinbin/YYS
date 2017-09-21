package com.example.administrator.yys.account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.yys.R;
import com.example.administrator.yys.network.NetWorkRequest;
import com.example.administrator.yys.utils.AppManager;
import com.example.administrator.yys.utils.MyHandler;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.administrator.yys.utils.IPAddress.IP;

/**
 * Created by Administrator on 2017/6/14 0014.
 */

public class Register extends Activity {
    EditText register_account;
    EditText register_pass;
    EditText register_repass;
    Button register_btn;
    TextView xieyi;
    LinearLayout fanhui;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        AppManager.getAppManager().addActivity(this);
        register_account = (EditText) findViewById(R.id.register_account);
        register_pass = (EditText) findViewById(R.id.register_setpass);
        xieyi = findViewById(R.id.register_yonghuxieyi);
        register_repass = (EditText) findViewById(R.id.register_resetpass);
        register_btn = (Button) findViewById(R.id.register_btn);
        fanhui = findViewById(R.id.register_fanhui);

        fanhui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        xieyi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(Register.this,YongHuXieYi.class);
                startActivity(intent);
            }
        });

      //提示用户帐号是否注册
        final Handler handler1 = new MyHandler(this){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                try {
                    JSONObject jsonObject = new JSONObject(msg.obj.toString());
                    int codeNum = Integer.parseInt(jsonObject.get("code").toString());
                    String message = jsonObject.get("message").toString();
                    Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        register_account.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b){
                    //检测用户帐号是否存在
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String url = IP+"lifetime/user/account/checkUser?loginName="+register_account.getText().toString();
                            String  result =  new NetWorkRequest().getServiceInfo(url);
                           Message msg = new Message();
                            msg.obj = result;
                            handler1.sendMessage(msg);
                        }
                    }).start();
                }
            }
        });

        //注册按钮
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String acc = register_account.getText().toString();
                final String pass = register_pass.getText().toString();
                String repass = register_repass.getText().toString();
                if (!acc.equals("")&& !pass.equals("") && !repass.equals("")){
                    if (pass.equals(repass)){
                        if (acc.length()>20){
                            Toast.makeText(getApplicationContext(),"帐号必须小于21个字符，请重新输入",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (acc.length()<6){
                            Toast.makeText(getApplicationContext(),"帐号必须大于5个字符，请重新输入",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (pass.length()>20){
                            Toast.makeText(getApplicationContext(),"密码必须小于21个字符，请重新输入",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (pass.length()<6){
                            Toast.makeText(getApplicationContext(),"密码必须大于5个字符，请重新输入",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Log.w("useraccount:",acc+"---"+pass+"--"+repass);

                        final Handler handler = new MyHandler(Register.this){
                            @Override
                            public void handleMessage(Message msg) {
                                super.handleMessage(msg);
                                JSONObject jsonObject = null;
                                try {
                                    jsonObject = new JSONObject(msg.obj.toString());
                                    int codeNum = Integer.parseInt(jsonObject.get("code").toString());
                                    String message = jsonObject.get("message").toString();
                                    Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                                    if (codeNum==1){
                                        Intent intent = new Intent();
                                        intent.setClass(Register.this,Login.class);
                                        startActivity(intent);
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        };

                        //注册帐号
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String url = IP+"lifetime/user/account/register?loginName="+acc+"&password="+pass;
                               String  result =  new NetWorkRequest().getServiceInfo(url);
                                Message msg = new Message();
                                msg.obj = result;
                                handler.sendMessage(msg);
                            }
                        }).start();


                    }else{
                        Toast.makeText(getApplicationContext(),"密码输入不一致",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"帐号或密码不能为空",Toast.LENGTH_SHORT).show();
                }
            }
        });



    }
}
