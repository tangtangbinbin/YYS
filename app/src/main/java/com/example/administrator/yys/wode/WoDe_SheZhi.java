package com.example.administrator.yys.wode;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.yys.R;
import com.example.administrator.yys.account.Login;
import com.example.administrator.yys.account.YongHuXieYi;
import com.example.administrator.yys.utils.AppManager;

import java.io.File;

/**
 * Created by Administrator on 2017/7/6 0006.
 */

public class WoDe_SheZhi extends Activity implements View.OnClickListener{
    LinearLayout fanhui;
    TextView xieyi;
    LinearLayout ziliao,tuichu,zhanghao,huancun,guanyu;
    TextView huancuntext;
    String musicdir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/YYSMusic";//音乐缓存
    String picdir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/youyisheng";//图片，录音缓存
    String musicname;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wode_shezhi);
        fanhui = findViewById(R.id.wode_shezhi_fanhui);
        ziliao = findViewById(R.id.wd_shezhi_ziliao_lin);
        zhanghao = findViewById(R.id.wd_shezhi_zhanghao_lin);
        AppManager.getAppManager().addActivity(this);
        xieyi = findViewById(R.id.wode_shezhi_xieyi);
        huancun = findViewById(R.id.wd_shezhi_huancun_lin);
        guanyu = findViewById(R.id.wd_shezhi_banben_lin);
        huancuntext = findViewById(R.id.wode_huanchun_text);
        musicname = getSharedPreferences("music",MODE_PRIVATE).getString("musicName","");
        tuichu = findViewById(R.id.wd_shezhi_tuichu_lin);

        guanyu.setOnClickListener(this);
        fanhui.setOnClickListener(this);
        huancun.setOnClickListener(this);
        ziliao.setOnClickListener(this);
        xieyi.setOnClickListener(this);
        tuichu.setOnClickListener(this);
        zhanghao.setOnClickListener(this);
        init();
    }

    private void init() {
        showCache();
    }

    private void showCache() {
        double musicsize = getDirSize(new File(musicdir));
        double picsize = getDirSize(new File(picdir));
        Log.w("size","musicsize:"+musicsize+" picsize:"+picsize);
        int allsize = (int) (musicsize+picsize);
        huancuntext.setText(allsize+" M");
    }

    public double getDirSize(File file) {
        //判断文件是否存在
        if (file.exists()) {
            //如果是目录则递归计算其内容的总大小
            if (file.isDirectory()) {
                File[] children = file.listFiles();
                double size = 0;
                for (File f : children)
                    size += getDirSize(f);
                return size;
            } else {//如果是文件则直接返回其大小,以“兆”为单位
                double size = (double) file.length() / 1024 / 1024;
                return size;
            }
        } else {
            System.out.println("文件或者文件夹不存在，请检查路径是否正确！");
            return 0.0;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void deleteFile(File file){
              if(file.isDirectory()){
                       File[] files = file.listFiles();
                       for(int i=0; i<files.length; i++){
                           if (files[i].toString().indexOf(musicname)==-1)
                                files[i].delete();
                           Log.w("musicname",files[i].toString()+" "+musicname);
                           }
                  }
         }
    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("是否删除应用产生的缓存");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                //删除缓存
                deleteFile(new File(musicdir));
                deleteFile(new File(picdir));
                showCache();
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
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
            case R.id.wode_shezhi_fanhui:
                finish();
                break;
            case R.id.wd_shezhi_huancun_lin:
                showDialog();
                break;
            case R.id.wd_shezhi_ziliao_lin:
                Intent intent = new Intent();
                intent.setClass(WoDe_SheZhi.this,WoDe_SheZhi_ZiLiao.class);
                startActivity(intent);
                break;
            case R.id.wd_shezhi_tuichu_lin:
                SharedPreferences.Editor editor = getSharedPreferences("user",MODE_PRIVATE).edit();
                editor.putString("token","");
                editor.commit();
                Intent intent2 = new Intent();
                intent2.putExtra("status","1");
                intent2.setClass(WoDe_SheZhi.this,Login.class);
                startActivity(intent2);
                break;
            case R.id.wd_shezhi_zhanghao_lin:
                Intent intent3 = new Intent();
                intent3.setClass(WoDe_SheZhi.this,WoDe_sheZhi_ZhangHao.class);
                startActivity(intent3);
                break;
            case R.id.wode_shezhi_xieyi:
                Intent intent1 = new Intent();
                intent1.setClass(WoDe_SheZhi.this, YongHuXieYi.class);
                startActivity(intent1);
                break;
            case R.id.wd_shezhi_banben_lin:
                Intent intent4 = new Intent();
                intent4.setClass(WoDe_SheZhi.this,WoDe_GuanYu.class);
                startActivity(intent4);
                break;
        }
    }
}
