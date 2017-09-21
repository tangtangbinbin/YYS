package com.example.administrator.yys.wode;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
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
 * Created by Administrator on 2017/7/26 0026.
 */

public class WoDe_SheZhi_ZhangHao_BandDing extends Activity implements View.OnClickListener{
    LinearLayout fanhui;
    EditText phone,code;
    Button getcode,wancheng;
    String token;
    String mobile;
    TimeCount time = new TimeCount(60000,1000);
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wode_shezhi_zhanghao_bangding);
        fanhui = findViewById(R.id.zhanghao_bangding_fanhui);
        AppManager.getAppManager().addActivity(this);
        phone = findViewById(R.id.zhanghao_bangding_edit_phone);
        code = findViewById(R.id.zhanghao_bangding_edit_code);
        token = getSharedPreferences("user",MODE_PRIVATE).getString("token","");
        getcode = findViewById(R.id.zhanghao_bangding_getcode);
        wancheng = findViewById(R.id.zhanghao_bangding_wancheng);

        fanhui.setOnClickListener(this);
        getcode.setOnClickListener(this);
        wancheng.setOnClickListener(this);
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==1){
                Log.w("sendCode",msg.obj.toString());
            }
        }
    };
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.zhanghao_bangding_fanhui:
                finish();
                break;
            case R.id.zhanghao_bangding_getcode:
                final String phonenum = phone.getText().toString();
                mobile = phonenum;
                if (phonenum.length()==11){
                    time.start();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String url = IP+"lifetime/user/account/sendCode?mobile="+phonenum;
                            String  result =  new NetWorkRequest().getServiceInfo(url);
                            Message msg = new Message();
                            msg.obj = result;
                            msg.what=1;
                            handler.sendMessage(msg);
                        }
                    }).start();
                }else {
                    Toast.makeText(getApplicationContext(),"手机号输入有误",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.zhanghao_bangding_wancheng:
                String strcode = code.getText().toString();
                if (mobile==null ||mobile.length()!=11){
                    mobile = phone.getText().toString();
                }
                String url = IP+"lifetime/user/account/bindMobile";
                RequestParams params = new RequestParams();
                params.addBodyParameter("token",token);
                params.addBodyParameter("code",strcode);
                params.addBodyParameter("mobile",mobile);
                HttpUtils httputils = new HttpUtils();
                httputils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        Log.w("bindmobile success:",responseInfo.result.toString());
                        try {
                            JSONObject obj = new JSONObject(responseInfo.result.toString());
                            String code = obj.getString("code");
                            if (code.equals("1")){
                                SharedPreferences.Editor editor = getSharedPreferences("user",MODE_PRIVATE).edit();
                                editor.putString("mobile",mobile);
                                editor.commit();
                                finish();
                            }
                            Toast.makeText(getApplicationContext(),obj.getString("message"),Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFailure(HttpException e, String s) {
                        Log.w("bindmobile faile",s.toString());
                    }
                });
                break;
        }
    }
    class TimeCount extends CountDownTimer{

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
            getcode.setClickable(false);
            getcode.setBackgroundColor(Color.parseColor("#999999"));
            getcode.setText(l/1000+"秒");
        }

        @Override
        public void onFinish() {
            getcode.setText("重新获取");
            getcode.setClickable(true);
            getcode.setBackgroundColor(Color.parseColor("#f08f1d"));
        }
    }
}
