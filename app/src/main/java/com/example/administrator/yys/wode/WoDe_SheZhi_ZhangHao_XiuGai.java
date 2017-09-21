package com.example.administrator.yys.wode;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
 * Created by Administrator on 2017/7/26 0026.
 */

public class WoDe_SheZhi_ZhangHao_XiuGai extends Activity implements View.OnClickListener{
    LinearLayout fanhui;
    EditText oldpass,newpass,verpass;
    Button wancheng;
    String token;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wode_shezhi_zhanghao_xiugai);
        AppManager.getAppManager().addActivity(this);
        fanhui = findViewById(R.id.zhanghao_xiugai_fanhui);
        oldpass = findViewById(R.id.zhanghao_xiugai_oldpass);
        token = getSharedPreferences("user",MODE_PRIVATE).getString("token","");
        newpass = findViewById(R.id.zhanghao_xiugai_newpass);
        verpass = findViewById(R.id.zhanghao_xiugai_verpass);
        wancheng = findViewById(R.id.zhanghao_xiugai_wancheng);

        wancheng.setOnClickListener(this);
        fanhui.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.zhanghao_xiugai_fanhui:
                finish();
                break;
            case R.id.zhanghao_xiugai_wancheng:
                String stroldpass = oldpass.getText().toString();
                String strnewpass = newpass.getText().toString();
                String strverpass = verpass.getText().toString();
                if (!stroldpass.equals("")){
                    if (!strnewpass.equals("")){
                        if (strnewpass.equals(strverpass)){
                            //修改密码
                            String url = IP+"lifetime/user/account/uptPassword";
                            RequestParams params = new RequestParams();
                            params.addBodyParameter("token",token);
                            params.addBodyParameter("oldPwd",stroldpass);
                            params.addBodyParameter("newPwd",strnewpass);
                            HttpUtils httputils = new HttpUtils();
                            httputils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
                                @Override
                                public void onSuccess(ResponseInfo<String> responseInfo) {
                                    Log.w("updatepass success:",responseInfo.result.toString());
                                    try {
                                        JSONObject obj = new JSONObject(responseInfo.result.toString());
                                        String code = obj.getString("code");
                                        if (code.equals("1")){
                                            finish();
                                        }
                                        Toast.makeText(getApplicationContext(),obj.getString("message"),Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                @Override
                                public void onFailure(HttpException e, String s) {
                                    Log.w("updatepass faile",s.toString());
                                }
                            });
                        }else {
                            Toast.makeText(getApplicationContext(),"新密码两次输入不一致",Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(getApplicationContext(),"新密码不能为空",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getApplicationContext(),"原密码不能为空",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
