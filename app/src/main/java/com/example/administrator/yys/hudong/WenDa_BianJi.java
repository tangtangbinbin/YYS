package com.example.administrator.yys.hudong;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
 * Created by Administrator on 2017/7/3 0003.
 */

public class WenDa_BianJi extends Activity implements View.OnClickListener{
    LinearLayout fanhui;
    TextView fabiao;
    EditText title;
    EditText info;
    String strtitle;
    String strinfo;
    String token;
    LinearLayout progress;
    int progressflag = 1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hudong_wenda_bianji);
        fabiao = findViewById(R.id.hudong_wenda_bianji_fabiao);
        fanhui = findViewById(R.id.hudong_wenda_bianji_fanhui);
        title = findViewById(R.id.wenda_title_edit);
        AppManager.getAppManager().addActivity(this);
        info = findViewById(R.id.wenda_info_edit);
        progress = findViewById(R.id.hudong_wenda_progress);
        token = getSharedPreferences("user",MODE_PRIVATE).getString("token","");
        fabiao.setOnClickListener(this);
        fanhui.setOnClickListener(this);
    }

    private void setprogress() {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                progress.setVisibility(View.GONE);
                if (progressflag==1){
                    Toast.makeText(getApplicationContext(),"发布失败，请重试！",Toast.LENGTH_SHORT).show();
                }
                fabiao.setClickable(true);
            }
        };
        Handler progresshandler = new Handler();
        progresshandler.postDelayed(runnable,20000);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.hudong_wenda_bianji_fabiao:
                strtitle = title.getText().toString();
                strinfo = info.getText().toString();
                if (strinfo.length()==0){
                    Toast.makeText(getApplicationContext(),"内容不能为空",Toast.LENGTH_SHORT).show();
                }else if (strtitle.length()==0){
                    Toast.makeText(getApplicationContext(),"标题不能为空",Toast.LENGTH_SHORT).show();
                }else{
                    progress.setVisibility(View.VISIBLE);
                    setprogress();
                    fabiao.setClickable(false);
                    //发表
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String url = IP+"lifetime/answer/account/addAnswer";
                            RequestParams params = new RequestParams();
                            params.addBodyParameter("content",strinfo.replaceAll("\n","%5Cn"));
                            params.addBodyParameter("title",strtitle.replaceAll("\n","%5Cn"));
                            params.addBodyParameter("token",token);
                            HttpUtils httputils = new HttpUtils();
                            httputils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>(){
                                @Override
                                public void onSuccess(ResponseInfo<String> responseInfo) {
                                    Log.w("fabiao success",responseInfo.result.toString());
                                    try {
                                        JSONObject obj = new JSONObject(responseInfo.result.toString());
                                        String code = obj.getString("code");
                                        if (code.equals("1")){
                                            Toast.makeText(getApplicationContext(),"等待审核",Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(HttpException e, String s) {
                                    Log.w("fabiao failure",s.toString());
                                }
                            });

                        }
                    }).start();
                }
                break;
            case R.id.hudong_wenda_bianji_fanhui:
                finish();
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        progressflag = 0;
    }
}
