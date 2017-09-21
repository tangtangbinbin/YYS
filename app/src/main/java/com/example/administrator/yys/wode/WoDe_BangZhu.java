package com.example.administrator.yys.wode;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
 * Created by Administrator on 2017/7/6 0006.
 */

public class WoDe_BangZhu extends Activity {
    RadioGroup rg;
    RadioButton bz;
    RadioButton fk;
    TextView fasong;
    EditText edit;
    TextView info,line;
    String token;
    LinearLayout fanhui;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wode_bangzhu);
        fanhui = findViewById(R.id.wode_bangzhu_fanhui);
        rg = findViewById(R.id.wode_bangzhu_rg);
        fasong = findViewById(R.id.wode_bangzhu_fasong);
        AppManager.getAppManager().addActivity(this);
        edit = findViewById(R.id.wode_fankui_edit);
        line = findViewById(R.id.fankui_line);
        token = getSharedPreferences("user",MODE_PRIVATE).getString("token","");
        info = findViewById(R.id.wode_bangzhu_info);
        fanhui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i){
                    case R.id.wode_bangzhu_bangzhu:
                        edit.setVisibility(View.GONE);
                        fasong.setVisibility(View.GONE);
                        line.setVisibility(View.GONE);
                        info.setVisibility(View.VISIBLE);
                        break;
                    case R.id.wode_bangzhu_fankui:
                        edit.setVisibility(View.VISIBLE);
                        fasong.setVisibility(View.VISIBLE);
                        line.setVisibility(View.VISIBLE);
                        info.setVisibility(View.GONE);
                        break;
                }
            }
        });
        fasong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String strinfo = edit.getText().toString();
                if (strinfo.length()==0){
                    Toast.makeText(getApplicationContext(),"内容不能为空",Toast.LENGTH_SHORT).show();
                }else {
                    //提交反馈
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            String url = IP+"lifetime/user/account/feedback";
                            RequestParams params = new RequestParams();
                            params.addBodyParameter("content",strinfo);
                            params.addBodyParameter("token",token);
                            HttpUtils httputils = new HttpUtils();
                            httputils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>(){
                                @Override
                                public void onSuccess(ResponseInfo<String> responseInfo) {
                                    Log.w("feedback success",responseInfo.result.toString());
                                    try {
                                        JSONObject obj = new JSONObject(responseInfo.result.toString());
                                        String code = obj.getString("code");
                                        if (code.equals("1")){
                                            Toast.makeText(getApplicationContext(),"提交成功",Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(HttpException e, String s) {
                                    Log.w("feedback failure",s.toString());
                                }
                            });
                        }
                    }.start();
                }
            }
        });
    }
}
