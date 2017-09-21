package com.example.administrator.yys.kongjian;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
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
 * Created by Administrator on 2017/6/30 0030.
 */

public class KongJian_ChuangJian extends Activity implements View.OnClickListener{
    LinearLayout jiaren;
    LinearLayout aiqing;
    LinearLayout youyi,fanhui;

    TextView jiarentext;
    TextView aiqingtext;
    TextView youyitext;
    TextView queding;
    String grouo_default_id = "";
    String token = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kongjian_chuangjian);
        jiaren = findViewById(R.id.kj_chuangjian_jiaren_lin);
        AppManager.getAppManager().addActivity(this);
        aiqing = findViewById(R.id.kj_chuangjian_aiqing_lin);
        fanhui = findViewById(R.id.kongjian_gushi_info_geren_fanhui);
        youyi = findViewById(R.id.kj_chuangjian_youyi_lin);
        queding = findViewById(R.id.kongjian_chuangjian_queding);
        token = getSharedPreferences("user",MODE_PRIVATE).getString("token","");

        jiaren.setOnClickListener(this);
        aiqing.setOnClickListener(this);
        youyi.setOnClickListener(this);
        queding.setOnClickListener(this);
        fanhui.setOnClickListener(this);

        jiarentext = findViewById(R.id.kj_chuangjian_jiaren_text);
        aiqingtext = findViewById(R.id.kj_chuangjian_aiqing_text);
        youyitext = findViewById(R.id.kj_chuangjian_youyi_text);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.kongjian_gushi_info_geren_fanhui:
                finish();
                break;
            case R.id.kj_chuangjian_jiaren_lin:
                grouo_default_id = "2";
                jiaren.setBackgroundColor(Color.parseColor("#f08f1d"));
                aiqing.setBackgroundResource(0);
                youyi.setBackgroundResource(0);
                break;
            case R.id.kj_chuangjian_aiqing_lin:
                grouo_default_id = "3";
                aiqing.setBackgroundColor(Color.parseColor("#f08f1d"));
                jiaren.setBackgroundResource(0);
                youyi.setBackgroundResource(0);
                break;
            case R.id.kj_chuangjian_youyi_lin:
                grouo_default_id = "4";
                youyi.setBackgroundColor(Color.parseColor("#f08f1d"));
                aiqing.setBackgroundResource(0);
                jiaren.setBackgroundResource(0);
                break;
            case R.id.kongjian_chuangjian_queding:
                if (grouo_default_id.equals("")){
                    Toast.makeText(KongJian_ChuangJian.this,"请先选择空间属性",Toast.LENGTH_SHORT).show();
                }else{
                    String url = IP+"lifetime/group/account/addGroup";
                    RequestParams params = new RequestParams();
                    params.addBodyParameter("token",token);
                    params.addBodyParameter("groupDefaultId",grouo_default_id);
                    HttpUtils httputils = new HttpUtils();
                    httputils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                            Log.w("addgroupsuccess:",responseInfo.result.toString());
                            try {
                                JSONObject obj = new JSONObject(responseInfo.result.toString());
                                String code = obj.getString("code");
                                if (code.equals("1")){
                                    /*Intent intent = new Intent();
                                    intent.putExtra("index","kongjian");
                                    intent.setClass(KongJian_ChuangJian.this, MainActivity.class);
                                    startActivity(intent);*/
                                    Intent intent = new Intent();
                                    intent.setAction("android.intent.action.UPDATE_FRAGMENT");
                                    intent.putExtra("index","kongjian");
                                    sendBroadcast(intent);
                                    finish();

                                }
                                Toast.makeText(KongJian_ChuangJian.this,obj.getString("message"),Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onFailure(HttpException e, String s) {

                            Log.w("addgroupfaile",s.toString());
                        }
                    });
                }
                break;
        }
    }
}
