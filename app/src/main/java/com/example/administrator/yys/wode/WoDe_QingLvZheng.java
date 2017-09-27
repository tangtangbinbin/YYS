package com.example.administrator.yys.wode;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.administrator.yys.R;
import com.example.administrator.yys.network.NetWorkRequest;
import com.example.administrator.yys.view.BigPhoto;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.example.administrator.yys.utils.IPAddress.IP;

/**
 * Created by Administrator on 2017/9/22 0022.
 */

public class WoDe_QingLvZheng extends Activity {
    LinearLayout fanhui;
    ImageView img;
    String token,root;
    Button save;
    String path;
    ArrayList urls = new ArrayList();
    private static final String SAVE_PIC_PATH = Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED) ? Environment.getExternalStorageDirectory().getAbsolutePath() :
            "/mnt/sdcard";//保存到SD卡
    private static final String SAVE_REAL_PATH = SAVE_PIC_PATH + "/YYSPic";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wode_qinglvzheng);
        token = getSharedPreferences("user",MODE_PRIVATE).getString("token","");
        root = getSharedPreferences("user",MODE_PRIVATE).getString("root","");
        img = findViewById(R.id.wode_qinglvzheng_img);
        fanhui = findViewById(R.id.qinglvzheng_fanhui);
        save = findViewById(R.id.qinglvzheng_saveimg);
        fanhui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(WoDe_QingLvZheng.this, BigPhoto.class);
                intent.putStringArrayListExtra("url",urls);
                intent.putExtra("position", 0);
                startActivity(intent);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        Looper.prepare();
                        Bitmap bitmap = getBitmap(root+path);
                        saveFile(bitmap,"qinglvzheng.jpg","");
                        Looper.loop();
                    }
                }.start();

            }
        });
        init();
    }
    public static Bitmap getBitmap(String path){
        URL url = null;
        try {
            url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            if(conn.getResponseCode() == 200){
                InputStream inputStream = conn.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
        }

    public void saveFile(Bitmap bm, String fileName, String path)  {
        String subForder = SAVE_REAL_PATH + path;
        File foder = new File(subForder);
        if (!foder.exists()) {
            foder.mkdirs();
        }
        File myCaptureFile = new File(subForder, fileName);
        try {
            if (!myCaptureFile.exists()) {
                myCaptureFile.createNewFile();
            }
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
            bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            bos.flush();
            bos.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.fromFile(new File(myCaptureFile.getPath()))));
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1&&msg.obj!=null){
                Log.w("msg1",msg.obj.toString());
                try {
                    JSONObject obj = new JSONObject(msg.obj.toString());
                    String code = obj.getString("code");
                    if (code.equals("1")){
                        String url = obj.getString("datum");
                        path = url;
                        Glide.with(getApplicationContext()).load(root+url).into(img);
                        urls.add(root+url);
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
                String url2 = IP+"lifetime/activity/lovers/getCredentials?token="+token;
                Log.w("token1",token);
                String  result2 =  new NetWorkRequest().getServiceInfo(url2);
                Message msg = new Message();
                msg.obj = result2;
                msg.what = 1;
                handler.sendMessage(msg);
            }
        }.start();

    }
}
