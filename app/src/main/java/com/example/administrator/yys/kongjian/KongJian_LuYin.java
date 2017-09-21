package com.example.administrator.yys.kongjian;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.yys.R;
import com.example.administrator.yys.network.NetWorkRequest;
import com.example.administrator.yys.utils.AppManager;
import com.example.administrator.yys.utils.AudioManager;
import com.example.administrator.yys.utils.MediaManager;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Timer;

import static com.example.administrator.yys.utils.IPAddress.IP;

/**
 * Created by Administrator on 2017/7/31 0031.
 */

public class KongJian_LuYin extends Activity  implements View.OnClickListener{
    LinearLayout fanhui;
    TextView baocun;
    TextView luyin;
    Button shiting,chonglai;
    String luyinstatus = "zhunbei";
    String listenstatus = "zhunbei";
    String mediastatus = "zhunbei";
    AudioManager manager;
    MediaManager mediaManager;
    Chronometer chronometer;
    Timer timer;
    long starttime=0,endtime=0;
    String groupid;
    String token;
    Intent callbackintent;
    int usedsize,totalsize,useingsize;
    LinearLayout progress;
    int progressflag = 1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kongjian_luyin);
        fanhui = findViewById(R.id.kongjian_luyin_fanhui);
        baocun = findViewById(R.id.kongjian_luyin_baocun);
        luyin = findViewById(R.id.luyin_start);
        progress = findViewById(R.id.luyin_progress);
        AppManager.getAppManager().addActivity(this);
        chronometer = findViewById(R.id.luyin_chronometer);
        timer = new Timer(true);
        token = getSharedPreferences("user",MODE_PRIVATE).getString("token","");
        manager = AudioManager.getInstance(Environment.getExternalStorageDirectory()+"/youyisheng");
        mediaManager = new MediaManager();
        shiting = findViewById(R.id.luyin_listen);
        shiting.setClickable(false);
        Intent intent = getIntent();
        callbackintent = intent.getParcelableExtra("callbackintent");
        groupid = intent.getStringExtra("groupid");
        chonglai = findViewById(R.id.luyin_reset);
        chonglai.setClickable(false);
        baocun.setOnClickListener(this);
        fanhui.setOnClickListener(this);
        luyin.setOnClickListener(this);
        fanhui.setOnClickListener(this);
        shiting.setOnClickListener(this);
        chonglai.setOnClickListener(this);
        getSpaceSize();
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==1){
            }
            if (msg.what==2&& msg.obj!=null){
                Log.w("getusedinfo",msg.obj.toString());
                try {
                    JSONObject obj = new JSONObject(msg.obj.toString());
                    String code = obj.getString("code");
                    if (code.equals("1")){
                        JSONObject datum = obj.getJSONObject("datum");
                        totalsize = datum.getInt("totalSize");
                        usedsize = datum.getInt("usedSize");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    };


    private void setprogress() {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                progress.setVisibility(View.GONE);
                if (progressflag==1){
                    Toast.makeText(getApplicationContext(),"发布失败，请重试！",Toast.LENGTH_SHORT).show();
                }
                baocun.setClickable(true);
            }
        };
        Handler progresshandler = new Handler();
        progresshandler.postDelayed(runnable,20000);
    }
    private void getSpaceSize(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                String url = IP+"lifetime/group/account/getUsedInfo?token="+token+"&groupId="+groupid;
                String  result =  new NetWorkRequest().getServiceInfo(url);
                Message msg = new Message();
                msg.obj = result;
                msg.what = 2;
                handler.sendMessage(msg);
            }
        }.start();
    }
    private void showdialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("空间内存不足，请先升级！");
        builder.setTitle("提示");
        builder.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.setCancelable(false);
        builder.create().show();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.kongjian_luyin_fanhui:
                finish();
                break;
            case R.id.kongjian_luyin_baocun:
                if (manager.getCurrentFilePath()==null){
                    Toast.makeText(getApplicationContext(),"文件不存在",Toast.LENGTH_SHORT).show();
                    return;
                }
                File file = new File(manager.getCurrentFilePath());
                useingsize = (int) file.length();
                if (totalsize-usedsize<useingsize){
                    showdialog();
                    return;
                }
                progress.setVisibility(View.VISIBLE);
                setprogress();
                baocun.setClickable(false);
                final String luyintimme =  ((endtime-starttime)/1000)+"";
                String url = IP+"lifetime/upload/account/groupUpload";
                RequestParams params = new RequestParams();
                params.addBodyParameter("token",token);
                params.addBodyParameter("groupId",groupid);
                params.addBodyParameter("file",file);
                HttpUtils httputils = new HttpUtils();
                httputils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        Log.w("upload sound uccess:",responseInfo.result.toString());
                        try {
                            JSONObject obj = new JSONObject(responseInfo.result.toString());
                            String code = obj.getString("code");
                            if (code.equals("1")){
                                String neturl = obj.getJSONObject("datum").getString("file");
                                Log.w("neturl:",neturl);
                                String url = IP+"lifetime/group/account/addArticleSound";
                                RequestParams params = new RequestParams();
                                params.addBodyParameter("token",token);
                                params.addBodyParameter("groupId",groupid);
                                params.addBodyParameter("content",neturl);
                                params.addBodyParameter("soundTime",luyintimme);
                                HttpUtils httputils = new HttpUtils();
                                httputils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
                                    @Override
                                    public void onSuccess(ResponseInfo<String> responseInfo) {
                                        Log.w("addArticle success:",responseInfo.result.toString());
                                        try {
                                            JSONObject obj = new JSONObject(responseInfo.result.toString());
                                            String code = obj.getString("code");
                                            if (code.equals("1")){
                                                KongJian_LuYin.this.setResult(5,callbackintent);
                                                finish();

                                            }
                                            Toast.makeText(getApplicationContext(),obj.getString("message"),Toast.LENGTH_SHORT).show();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    @Override
                                    public void onFailure(HttpException e, String s) {
                                        Log.w("addArticle faile",s.toString());
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFailure(HttpException e, String s) {
                        Log.w("upload sound faild",s.toString());
                    }
                });
                break;
            case R.id.luyin_start://开始录音
                if (luyinstatus.equals("zhunbei")){
                    luyin.setText("结束录音");
                    manager.prepareAudio();
                    starttime = SystemClock.elapsedRealtime();
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    chronometer.start();
                    luyinstatus = "luzhi";
                }
                else if (luyinstatus.equals("luzhi")){
                    luyin.setText("完成");
                    endtime = SystemClock.elapsedRealtime();
                    shiting.setClickable(true);
                    chonglai.setClickable(true);
                    luyin.setClickable(false);
                    luyinstatus = "wancheng";
                    chronometer.stop();
                    manager.release();
                }
                break;
            case R.id.luyin_listen://试听
                if (listenstatus.equals("zhunbei")&&luyinstatus.equals("wancheng")){
                    shiting.setText("暂停");
                    listenstatus = "bofang";
                    String path = manager.getCurrentFilePath();
                    if (path!=null&&mediastatus.equals("zhunbei")){
                        mediaManager.playSound(path, new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                shiting.setText("试听");
                                listenstatus = "zhunbei";
                                mediastatus = "zhunbei";
                            }
                        }, new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mediaPlayer) {

                            }
                        });
                        mediastatus="bofang";
                    }
                    if (mediastatus.equals("zanting")){
                        mediaManager.resume();
                    }
                }else if (listenstatus.equals("bofang")){
                    shiting.setText("试听");
                    listenstatus = "zhunbei";
                    mediaManager.pause();
                    mediastatus = "zanting";
                }
                break;
            case R.id.luyin_reset://重来
                luyinstatus="zhunbei";
                luyin.setText("开始录音");
                manager.cancel();
                mediaManager.release();
                chronometer.setBase(SystemClock.elapsedRealtime());
                shiting.setClickable(false);
                chonglai.setClickable(false);
                luyin.setClickable(true);
                break;
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        progressflag = 0;
    }
}
