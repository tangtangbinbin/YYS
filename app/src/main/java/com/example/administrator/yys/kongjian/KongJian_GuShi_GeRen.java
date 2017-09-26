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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.administrator.yys.R;
import com.example.administrator.yys.utils.AppManager;
import com.example.administrator.yys.utils.MyApplication;
import com.example.administrator.yys.utils.MyHandler;

import org.xutils.x;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Administrator on 2017/7/11 0011.
 */

public class KongJian_GuShi_GeRen extends Activity {
    LinearLayout lin;
    String root = "";
    LinearLayout fanhui;
    TextView xiugai;
    String content;
    String group_id;
    String group_article_id;
    Intent callbackintent;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kongjian_gushi_info_geren);
        x.Ext.init(getApplication());
        AppManager.getAppManager().addActivity(this);
        root = getSharedPreferences("user",MODE_PRIVATE).getString("root","");
        lin = findViewById(R.id.kj_gushi_info_text);
        fanhui = findViewById(R.id.kongjian_gushi_info_geren_fanhui);
        xiugai = findViewById(R.id.kongjian_gushi_info_geren_xiugai);
        final Intent intent = getIntent();
        callbackintent = intent.getParcelableExtra("callbackintent");
        content = intent.getStringExtra("content");
        group_id = intent.getStringExtra("group_id");
        group_article_id = intent.getStringExtra("group_article_id");
        xiugai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("content",content);
                intent.putExtra("group_id",group_id);
                intent.putExtra("group_article_id",group_article_id);
                intent.putExtra("callbackintent",callbackintent);
                intent.setClass(KongJian_GuShi_GeRen.this,KongJian_GuShi_GeRen_XiuGai.class);
                startActivityForResult(intent,0);
            }
        });
        fanhui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication.getMyApplicationInstance().setNeedrefresh(intent.getIntExtra("itemnum",0));
                finish();
            }
        });

        init();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==1){
            finish();
        }
    }

    Handler handler = new MyHandler(this){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==1){
                ImageView img = new ImageView(getApplicationContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
               // Bitmap bitmap = (Bitmap) msg.obj;
                img.setLayoutParams(params);
                //img.setImageBitmap(bitmap);
                Glide.with(getApplicationContext()).load(msg.obj.toString()).into(img);
                lin.addView(img);
            }else if (msg.what==2){
                TextView text = new TextView(getApplicationContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                text.setLayoutParams(params);
                String text1 = msg.obj.toString();
                text.setText(text1.replaceAll("%5Cn","\n"));
                text.setTextColor(Color.parseColor("#4f4f4f"));
                lin.addView(text);
            }
        }
    };
    private void init() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                String[] str = content.split("＜img＞");
                for (int i=0;i<str.length;i++){
                    if (str[i].length()>=8&&str[i].substring(0,8).equals("/groups/")){
                        String url = root+str[i];
                       /* Bitmap bitmap = getBitmap(url);
                        Bitmap bitmap2 = resizeImage1(bitmap);*/
                        Message msg = new Message();
                       /* msg.obj = bitmap2;*/
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
}
