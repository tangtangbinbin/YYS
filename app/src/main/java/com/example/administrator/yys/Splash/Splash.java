package com.example.administrator.yys.Splash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import com.example.administrator.yys.MainActivity;
import com.example.administrator.yys.R;
import com.example.administrator.yys.network.NetWorkRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.administrator.yys.utils.IPAddress.IP;

/**
 * Created by Administrator on 2017/8/8 0008.
 */

public class Splash extends Activity {
    WebView img;
    ImageView img2;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        img = findViewById(R.id.splash_img);
        img2 = findViewById(R.id.splash_img2);
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.setClass(Splash.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        };
        new Handler().postDelayed(runnable,3000);
        init();
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.obj!=null){
                Log.w("getsplashs",msg.obj.toString());
                try {
                    JSONObject obj = new JSONObject(msg.obj.toString());
                    String code = obj.getString("code");
                    if (code.equals("1")){
                       JSONObject datum = obj.getJSONObject("datum");
                        String resous = datum.getString("resous");
                        JSONArray splashs = datum.getJSONArray("splashs");
                        if (splashs.length()>0){
                            String splash_url = "";
                            for (int i=0;i<splashs.length();i++){
                                splash_url = splashs.getJSONObject(i).getString("splash_url");
                            }
                            Log.w("splash url",resous+splash_url);
                            img2.setVisibility(View.GONE);
                            img.setVisibility(View.VISIBLE);
                           img.loadDataWithBaseURL(null,"<HTML><body bgcolor='#f3f3f3'><div align=center><IMG style='width:100%;height:100%' src='"+resous+splash_url+"'/></div></body></html>", "text/html", "UTF-8",null);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    };
    private void init() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                String url1 = IP+"lifetime/splash/account/getSplashs";
                String  result =  new NetWorkRequest().getServiceInfo(url1);
                Message msg1 = new Message();
                msg1.obj = result;
                msg1.what = 1;
                handler.sendMessage(msg1);
            }
        }.start();
    }
}
