package com.example.administrator.yys.account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.yys.R;
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
 * Created by Administrator on 2017/8/2 0002.
 */

public class ReSet_PassWord extends Activity {
    EditText newpass,verpass;
    Button wancheng;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password);
        Intent intent = getIntent();
        final String pwdCode = intent.getStringExtra("pwdCode");
        final String straccount = intent.getStringExtra("straccount");
        newpass = findViewById(R.id.reset_newpass);
        verpass = findViewById(R.id.reset_verpass);
        wancheng = findViewById(R.id.reset_wancheng);
        AppManager.getAppManager().addActivity(this);
        wancheng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strnewpass = newpass.getText().toString();
                String strverpass = verpass.getText().toString();
                if (strnewpass.length()<0){
                   Toast.makeText(getApplicationContext(),"密码不能小于6位数",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!strnewpass.equals(strverpass)){
                    Toast.makeText(getApplicationContext(),"两次输入的密码不一致",Toast.LENGTH_SHORT).show();
                    return;
                }
                String url = IP+"lifetime/user/account/uptPwdToCode";
                RequestParams params = new RequestParams();
                params.addBodyParameter("pwdCode",pwdCode );
                params.addBodyParameter("loginName",straccount);
                params.addBodyParameter("newPwd",strnewpass);
                HttpUtils httputils = new HttpUtils();
                httputils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        Log.w("uptPwdToCode success:",responseInfo.result.toString());
                        try {
                            JSONObject obj = new JSONObject(responseInfo.result.toString());
                            String code = obj.getString("code");
                            if (code.equals("1")){
                                Intent intent = new Intent();
                                intent.setClass(ReSet_PassWord.this,Login.class);
                                startActivity(intent);
                            }
                            Toast.makeText(getApplicationContext(),obj.getString("message"),Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFailure(HttpException e, String s) {
                        Log.w("uptPwdToCode faile",s.toString());
                    }
                });
            }
        });
    }
}
