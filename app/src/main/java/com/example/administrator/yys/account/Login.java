package com.example.administrator.yys.account;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.example.administrator.yys.MainActivity;
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

public class Login extends Activity {
    EditText login_account;
    EditText login_pass;
    Button login_btn;
    TextView xieyi;
    TextView register;
    LinearLayout fanhui;
    TextView login_forgetpass;
    SharedPreferences userinfo;
    String status = "0";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        AppManager.getAppManager().addActivity(this);
        login_account = (EditText) findViewById(R.id.login_account);
        login_pass = (EditText) findViewById(R.id.login_pass);
        login_btn = (Button) findViewById(R.id.login_btn);
        register = (TextView) findViewById(R.id.register_text);
        xieyi = findViewById(R.id.login_yonghuxieyi);
        fanhui = findViewById(R.id.login_fanhui);
        login_forgetpass = (TextView) findViewById(R.id.login_forgetpass);
        userinfo =  getSharedPreferences("user",MODE_PRIVATE);
        Intent intent = getIntent();
        status = intent.getStringExtra("status");


        fanhui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (status==null ||status.equals("1")){
                    Log.w("system exit","0");
                    AppManager.getAppManager().AppExit(getApplicationContext());
                }
                finish();
            }
        });
        login_forgetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(Login.this,Account_vfPhone.class);
                startActivity(intent);
            }
        });
        xieyi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(Login.this,YongHuXieYi.class);
                startActivity(intent);
            }
        });
        //登录按钮
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String account = login_account.getText().toString().trim();
                final String pass = login_pass.getText().toString().trim();
                if(!account.equals("")&& !pass.equals("")){
                    if (account.length()>18){
                        Toast.makeText(getApplicationContext(),"帐号必须小于18个字符，请重新输入",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (account.length()<6){
                        Toast.makeText(getApplicationContext(),"帐号必须大于5个字符，请重新输入",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Log.w("useraccount:",account+"---"+pass);
                    final Handler handler = new MyHandler(Login.this){
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            JSONObject jsonObject = null;
                            try {
                                Log.w("loginmess:",msg.obj.toString());
                                jsonObject = new JSONObject(msg.obj.toString());
                                int code = Integer.parseInt(jsonObject.get("code").toString());
                                String message = jsonObject.get("message").toString();
                                if (code==1){
                                JSONObject jsonroot = jsonObject.getJSONObject("constant");
                                JSONObject jsoninfo = jsonObject.getJSONObject("info");
                                String token = jsonObject.get("token").toString();
                                String root = jsonroot.getString("resourceServer");
                                String user_name= jsoninfo.getString("user_name");
                                String user_id= jsoninfo.getString("user_id");
                                Log.w("登录获取到的userid",user_id);
                                String login_name = jsoninfo.getString("login_name");
                                String user_img = jsoninfo.getString("user_avatar");
                                String mobile = jsoninfo.getString("mobile");
                                String gexingqianmin = jsoninfo.getString("personal_signature");
                                Log.w("登录信息：",jsonObject.toString());

                                    //登录成功则保存帐号
                                    SharedPreferences.Editor editor = userinfo.edit();
                                    editor.putString("token",token);
                                    editor.putString("root",root);
                                    editor.putString("user_name",user_name);
                                    editor.putString("user_id",user_id);
                                    editor.putString("login_name",login_name);
                                    editor.putString("user_img",root+user_img);
                                    editor.putString("user_avatar",jsoninfo.getString("user_avatar"));
                                    editor.putString("mobile",mobile);
                                    editor.putString("gexingqianmin",gexingqianmin);
                                    editor.commit();

                                    Intent intent = new Intent();
                                    intent.setClass(Login.this,MainActivity.class);
                                    intent.putExtra("index","shouye");
                                    startActivity(intent);
                                }
                                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    //登录帐号
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String url = IP+"lifetime/user/account/login?loginName="+account+"&password="+pass;
                            String  result =  new NetWorkRequest().getServiceInfo(url);
                            Message msg = new Message();
                            msg.obj = result;
                            handler.sendMessage(msg);
                        }
                    }).start();
                }else{
                    Toast.makeText(getApplicationContext(),"帐号或密码不能为空",Toast.LENGTH_SHORT).show();
                }

            }
        });
        //注册按钮
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(Login.this,Register.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (status==null||status.equals("1")){
                Log.w("system exit","00");
            AppManager.getAppManager().AppExit(getApplicationContext());
        }
    }
}
