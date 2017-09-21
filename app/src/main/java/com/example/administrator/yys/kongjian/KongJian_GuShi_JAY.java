package com.example.administrator.yys.kongjian;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static com.example.administrator.yys.utils.IPAddress.IP;

/**
 * Created by Administrator on 2017/7/11 0011.
 */

public class KongJian_GuShi_JAY extends Activity implements View.OnClickListener{
    LinearLayout lin;
    String root = "";
    LinearLayout fanhui;
    String content;
    String group_id;
    String group_article_id;
    String user_id;
    CircularImage img;
    TextView name;
    String token;
    Button guanzhu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kongjian_gushi_info_jiaren);
        x.Ext.init(getApplication());
        root = getSharedPreferences("user",MODE_PRIVATE).getString("root","");
        lin = findViewById(R.id.kj_gushi_jiaren_info_text);
        fanhui = findViewById(R.id.kongjian_gushi_info_jiaren_fanhui);
        name = findViewById(R.id.kj_gushi_username);
        AppManager.getAppManager().addActivity(this);
        guanzhu = findViewById(R.id.kj_gushi_guanzhu);
        img = findViewById(R.id.kongjian_gushi_info_jiaren_touxiang);
        fanhui.setOnClickListener(this);
        img.setOnClickListener(this);
        guanzhu.setOnClickListener(this);
        token = getSharedPreferences("user",MODE_PRIVATE).getString("token","");

        Intent intent = getIntent();
        content = intent.getStringExtra("content");
        group_id = intent.getStringExtra("group_id");
        user_id = intent.getStringExtra("user_id");
        group_article_id = intent.getStringExtra("group_article_id");


        init();
    }
    Handler handler = new MyHandler(this){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==1){
                ImageView img = new ImageView(getApplicationContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                //Bitmap bitmap = (Bitmap) msg.obj;
                img.setLayoutParams(params);
                //img.setImageBitmap(bitmap);
                Glide.with(getApplicationContext()).load(msg.obj.toString()).into(img);
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(KongJian_GuShi_JAY.this, Other_index.class);
                        intent.putExtra("user_id",user_id);
                        startActivity(intent);
                    }
                });
                lin.addView(img);
            }else if (msg.what==2){
                TextView text = new TextView(getApplicationContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                text.setLayoutParams(params);
                String text1 = msg.obj.toString();
                text.setText(text1.replaceAll("%5Cn","\n"));
                text.setTextColor(Color.parseColor("#4f4f4f"));
                lin.addView(text);
            }else if (msg.what==3){
                Log.w("getuserinfo",msg.obj.toString());
                try {
                    JSONObject obj = new JSONObject(msg.obj.toString());
                    String code = obj.getString("code");
                    if (code.equals("1")){
                        JSONObject datum = obj.getJSONObject("datum");
                        String user_avatar = datum.getString("user_avatar");
                        String user_name = datum.getString("user_name");
                        //x.image().bind(img,root+user_avatar);
                        Glide.with(getApplicationContext()).load(root+user_avatar).into(img);
                        name.setText(user_name);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else if (msg.what==4){
                Log.w("guanzhuuser",msg.obj.toString());
                try {
                    JSONObject obj = new JSONObject(msg.obj.toString());
                    String code = obj.getString("code");
                    if (code.equals("1")){
                        Toast.makeText(getApplicationContext(),obj.getString("message"),Toast.LENGTH_SHORT).show();
                        guanzhu.setText("已关注");
                        guanzhu.setClickable(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }else if (msg.what ==5){
                Log.w("is guanzhu",msg.obj.toString());
                try {
                    JSONObject obj = new JSONObject(msg.obj.toString());
                    String code = obj.getString("code");
                    if (code.equals("1")){
                        JSONObject datum = obj.getJSONObject("datum");
                        String  isConcern = datum.getString("isConcern");
                        if (isConcern.equals("true")){
                            guanzhu.setText("已关注");
                            guanzhu.setClickable(false);
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

                String url2 = IP+"lifetime/concern/account/isConcern?concernId="+user_id+"&token="+token;
                String  result1 =  new NetWorkRequest().getServiceInfo(url2);
                Message msg2 = new Message();
                msg2.obj = result1;
                msg2.what = 5;
                handler.sendMessage(msg2);

                String url1 = IP+"lifetime/user/account/getUserInfoById?userId="+user_id+"&token="+token;
                String  result =  new NetWorkRequest().getServiceInfo(url1);
                Message msg1 = new Message();
                msg1.obj = result;
                msg1.what = 3;
                handler.sendMessage(msg1);

                String[] str = content.split("＜img＞");
                for (int i=0;i<str.length;i++){
                    if (str[i].length()>=8&&str[i].substring(0,8).equals("/groups/")){
                        String url = root+str[i];
                       /* Bitmap bitmap = getBitmap(url);
                        Bitmap bitmap2 = resizeImage1(bitmap);
                        Message msg = new Message();
                        msg.obj = bitmap2;*/
                       Message msg = new Message();
                        msg.obj = url;
                        msg.what=1;
                        handler.sendMessage(msg);
                    }else{
                        Message msg = new Message();
                        msg.obj = str[i];
                        msg.what=2;
                        handler.sendMessage(msg);
                    }
                }
            }
        }.start();
    }

    private Bitmap getBitmap(String s) {
        Bitmap draw = null;
        URL url;
        try {
            url = new URL(s);
            draw = BitmapFactory.decodeStream(url.openStream());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return draw;
    }

    public Bitmap resizeImage1(Bitmap bitmap)
    {
        Bitmap BitmapOrg = bitmap;
        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        //-100是因为edit设置了边距，不然会显示两个图片
        int sWidth = dm.widthPixels-100;
        int sHeight = dm.widthPixels*height/width-100;

        Log.w("new width height:",sWidth+"--"+sHeight);
        float scaleWidth = ((float) sWidth) / width;
        float scaleHeight = ((float) sHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 旋转
        // matrix.postRotate(45);
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
                height, matrix, true);
        return resizedBitmap;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.kongjian_gushi_info_jiaren_fanhui:
                finish();
                break;
            case R.id.kongjian_gushi_info_jiaren_touxiang://头像
                break;
            case R.id.kj_gushi_guanzhu:
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        String url1 = IP+"lifetime/concern/account/addConcern?concernId="+user_id+"&token="+token;
                        String  result =  new NetWorkRequest().getServiceInfo(url1);
                        Message msg1 = new Message();
                        msg1.obj = result;
                        msg1.what = 4;
                        handler.sendMessage(msg1);
                    }
                }.start();

                break;
        }
    }
}
