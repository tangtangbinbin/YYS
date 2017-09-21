package com.example.administrator.yys.kongjian;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.administrator.yys.R;
import com.example.administrator.yys.network.NetWorkRequest;
import com.example.administrator.yys.other.Other_index;
import com.example.administrator.yys.utils.AppManager;
import com.example.administrator.yys.utils.MyHandler;
import com.example.administrator.yys.view.CircularImage;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;

import static com.example.administrator.yys.utils.IPAddress.IP;

/**
 * Created by Administrator on 2017/7/6 0006.
 */

public class KongJian_YaoQing extends Activity implements View.OnClickListener{
    LinearLayout fanhui;
    EditText acc;
    TextView wancheng;
    TextView name;
    CircularImage img;
    Button yaoqing;
    LinearLayout lin;
    String token;
    String root ;
    String account;
    String group_id;
    String user_id;
    String user_name;
    String user_avatar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kongjian_yaoqing);
        fanhui = findViewById(R.id.kongjian_yaoqing_fanhui);
        acc = findViewById(R.id.kj_yaoqing_acc);
        AppManager.getAppManager().addActivity(this);
        wancheng = findViewById(R.id.kongjian_yaoqing_wancheng);
        lin = findViewById(R.id.kj_yaoqing_lin);
        name = findViewById(R.id.kj_yaoqing_name);
        yaoqing = findViewById(R.id.kj_yaoqing_btn);
        x.Ext.init(getApplication());
        token = getSharedPreferences("user",MODE_PRIVATE).getString("token","");
        root = getSharedPreferences("user",MODE_PRIVATE).getString("root","");
        img = findViewById(R.id.kj_yaoqing_touxiang);
        Intent intent = getIntent();
        group_id = intent.getStringExtra("group_id");
        fanhui.setOnClickListener(this);
        wancheng.setOnClickListener(this);
        yaoqing.setOnClickListener(this);
        img.setOnClickListener(this);

    }

    Handler handler = new MyHandler(this){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==1){
                Log.w("getsearchuser",msg.obj.toString());
                try {
                    JSONObject obj = new JSONObject(msg.obj.toString());
                    String code = obj.getString("code");
                    if (code.equals("0")){
                        Toast.makeText(getApplicationContext(),obj.getString("message"),Toast.LENGTH_SHORT).show();
                    }else if (code.equals("1")){
                        lin.setVisibility(View.VISIBLE);
                        JSONObject datum = obj.getJSONObject("datum");
                        user_avatar = datum.getString("user_avatar");
                        user_id = datum.getString("user_id");
                        user_name = datum.getString("user_name");
                        //x.image().bind(img,root+user_avatar);
                        Glide.with(getApplicationContext()).load(root+user_avatar).into(img);
                        name.setText(user_name);
                        yaoqing.setText("邀请");
                        yaoqing.setClickable(true);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (msg.what ==2){
                try {
                    JSONObject obj = new JSONObject(msg.obj.toString());
                    String code = obj.getString("code");
                    if (code.equals("1")){
                        yaoqing.setText("已邀请");
                        yaoqing.setClickable(false);
                    }else if (code.equals("0")){
                        Toast.makeText(getApplicationContext(),obj.getString("message"),Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.kongjian_yaoqing_fanhui:
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                finish();
                break;
            case R.id.kongjian_yaoqing_wancheng:
                try {
                    ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }catch (Exception e){
                    e.printStackTrace();
                }
                account = acc.getText().toString();
                if (account.length()==0){
                    Toast.makeText(getApplicationContext(),"请输入帐号",Toast.LENGTH_SHORT).show();
                }else{
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            String url1 = IP+"lifetime/user/account/getSearchUser?loginName="+account+"&token="+token;
                            String  result =  new NetWorkRequest().getServiceInfo(url1);
                            Message msg1 = new Message();
                            msg1.obj = result;
                            msg1.what = 1;
                            handler.sendMessage(msg1);
                        }
                    }.start();

                }
                break;
            case R.id.kj_yaoqing_btn:
                String url = IP+"lifetime/group/account/invite";
                RequestParams params = new RequestParams();
                params.addBodyParameter("token",token);
                params.addBodyParameter("groupId",group_id);
                params.addBodyParameter("userId",user_id);
                HttpUtils httputils = new HttpUtils();
                httputils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        Log.w("yaoqingsuccess:",responseInfo.result.toString());
                        Message msg = new Message();
                        msg.what = 2;
                        msg.obj = responseInfo.result.toString();
                        handler.sendMessage(msg);
                    }
                    @Override
                    public void onFailure(HttpException e, String s) {
                        Log.w("yaoqnigfaile",s.toString());
                    }
                });
                break;
            case R.id.kj_yaoqing_touxiang:
                if (!token.equals("")){
                    Intent intent = new Intent();
                    intent.setClass(KongJian_YaoQing.this,Other_index.class);
                    intent.putExtra("user_id",user_id);
                    startActivity(intent);
                }else {
                    Toast.makeText(KongJian_YaoQing.this,"请先登录",Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }
}
