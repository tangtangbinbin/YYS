package com.example.administrator.yys;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ViewSwitcher;

import com.example.administrator.yys.account.Login;
import com.example.administrator.yys.fragment.Fragment_hudong;
import com.example.administrator.yys.fragment.Fragment_kongjian;
import com.example.administrator.yys.fragment.Fragment_music;
import com.example.administrator.yys.fragment.Fragment_wode;
import com.example.administrator.yys.network.CheckNetWork;
import com.example.administrator.yys.network.NetWorkRequest;
import com.example.administrator.yys.utils.AppManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

import static com.example.administrator.yys.utils.IPAddress.IP;


public class MainActivity extends Activity {

    RadioGroup rg;
    Fragment shouye;
    Fragment hudong;
    Fragment kongjian;
    Fragment wode;
    SharedPreferences userinfo;
    SharedPreferences musicinfo;
    boolean musicisdowned = false;
    boolean musiclrcisdowned = false;
    String musicdir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/YYSMusic";
    String token;
    String index = "";
    RadioButton Rkongjian,Rwode,Rhudong,Rshouye;
    String version = "";
    ViewSwitcher switcher;
    int switcher_flag = 0;
    int myindex = 0;
    final static String APKPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/youyisheng/yys.apk";
    //当前显示的fragment
    private static final String CURRENT_FRAGMENT = "STATE_FRAGMENT_SHOW";
    private FragmentManager fragmentManager;
    private Fragment currentFragment = new Fragment();
    private List<Fragment> fragments = new ArrayList<>();
    private int currentIndex = 0;
    Intent updataService;
    //WebView img;
    GifImageView img2;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        switcher = findViewById(R.id.main_viewswitcher);
        x.Ext.init(getApplication());
        Rkongjian = findViewById(R.id.main_kongjian);
        Rwode = findViewById(R.id.main_wode);
        AppManager.getAppManager().addActivity(this);
        Rhudong = findViewById(R.id.main_hudong);
        Rshouye = findViewById(R.id.main_shouye);
        musicinfo = getSharedPreferences("music",MODE_PRIVATE);
        //获取用户token
        userinfo = getSharedPreferences("user",MODE_PRIVATE);
        token = userinfo.getString("token","");
       // img = findViewById(R.id.main_splash_img);
        img2 = findViewById(R.id.main_splash_img2);

        Intent intent = getIntent();
        index = intent.getStringExtra("index");
        fragmentManager = getFragmentManager();
        if (savedInstanceState != null) { // “内存重启”时调用
            //获取“内存重启”时保存的索引下标
            currentIndex = savedInstanceState.getInt(CURRENT_FRAGMENT,0);
            fragments.removeAll(fragments);
            fragments.add(fragmentManager.findFragmentByTag(0+""));
            fragments.add(fragmentManager.findFragmentByTag(1+""));
            fragments.add(fragmentManager.findFragmentByTag(2+""));
            fragments.add(fragmentManager.findFragmentByTag(3+""));
            //恢复fragment页面
            restoreFragment();
        }else{      //正常启动时调用
            fragments.add(new Fragment_music());
            fragments.add(new Fragment_hudong());
            fragments.add(new Fragment_kongjian());
            fragments.add(new Fragment_wode());
            showFragment();
        }

        //检查网络是否可用
        if (new CheckNetWork().isConnect(this)){
            initview();
            checkUpdate();
            setView();
        }else{
            new CheckNetWork().showNotConn(this);
        }

    }

    private void setView() {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (switcher_flag==0)
               switcher.showNext();
            }
        };
        new Handler().postDelayed(runnable,4000);
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==1){
                Log.w("checkVersion",msg.obj.toString());
                try {
                    JSONObject obj = new JSONObject(msg.obj.toString());
                    String code = obj.getString("code");
                    if (code.equals("1")){
                        JSONObject datum = obj.getJSONObject("datum");
                        String message = datum.getString("message");
                        final String url = datum.getString("url");
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage(message);
                        builder.setTitle("更新提示");
                        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                /*Intent intent = new Intent();
                                intent.setAction("android.intent.action.VIEW");
                                Uri content_url = Uri.parse(url);
                                intent.setData(content_url);
                                startActivity(intent);*/
                                showUpdate(url);
                                //upData(url);
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else if (msg.what==2&& msg!=null){
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
                            if (Build.VERSION.SDK_INT<19){
                                img2.setVisibility(View.VISIBLE);
                                // img.setVisibility(View.GONE);
                            }else {
                                //img.loadDataWithBaseURL(null,"<HTML><body bgcolor='#fafafa'><div align=center><IMG style='width:100%;height:100%' src='"+resous+splash_url+"'/></div></body></html>", "text/html", "UTF-8",null);
                            }
                            Log.w("splash url",resous+splash_url+" api:"+Build.VERSION.SDK_INT);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

     private void showUpdate(String url) {
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("更新中...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        DownLoadFile downLoadFile = new DownLoadFile();
        downLoadFile.execute(url);
    }

    private class DownLoadFile extends AsyncTask<String,Integer,String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                int fileLenth = connection.getContentLength();
                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(APKPATH);

                byte data[]  = new byte[1024];
                long total = 0;
                int count;
                while((count=input.read(data))!=-1){
                    total+=count;
                    publishProgress((int)(total*100/fileLenth));
                    output.write(data,0,count);
                }
                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                progressDialog.setCancelable(false);
                progressDialog.show();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            Intent i = new Intent(Intent.ACTION_VIEW);
            Uri uri = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//大于等于 7.0
                i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                uri = FileProvider.getUriForFile(getApplicationContext(), "com.example.administrator.yys.fileprovider", new File(APKPATH));
            } else {//小于7.0
                uri = Uri.fromFile(new File(APKPATH));
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }

            i.setDataAndType(uri,"application/vnd.android.package-archive");
            startActivity(i);
        }
    }

    private void checkUpdate() {
        PackageInfo pkg;
        try {
            pkg = getPackageManager().getPackageInfo(getApplication().getPackageName(), 0);
            version = pkg.versionCode+"";
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = IP+"lifetime/user/account/checkVersion?client=android&version="+version;
                String  result =  new NetWorkRequest().getServiceInfo(url);
                Message msg = new Message();
                msg.what=1;
                msg.obj = result;
                if (msg.obj!=null){
                    handler.sendMessage(msg);
                }

            }
        }).start();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //“内存重启”时保存当前的fragment名字
        outState.putInt(CURRENT_FRAGMENT,currentIndex);
        super.onSaveInstanceState(outState);
    }


    private void initview() {

       /* new Thread(){
            @Override
            public void run() {
                super.run();
                String url1 = IP+"lifetime/splash/account/getSplashs";
                String  result =  new NetWorkRequest().getServiceInfo(url1);
                Message msg1 = new Message();
                msg1.obj = result;
                msg1.what = 2;
                handler.sendMessage(msg1);
            }
        }.start();*/
        //Glide.with(getApplicationContext()).load(R.drawable.a94).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(img2);




        /*if ( index!=null&&index.equals("kongjian")){
            Rkongjian.setChecked(true);
            getFragmentManager().beginTransaction().replace(R.id.main_content,new Fragment_kongjian()).commit();

        }else{
            getFragmentManager().beginTransaction().replace(R.id.main_content,new Fragment_music()).commit();
        }*/
        if ( index!=null&&index.equals("kongjian")){
            switcher.showNext();
            switcher_flag = 1;
            Rkongjian.setChecked(true);
            currentIndex = 2;
            showFragment();
        }
        if ( index!=null&&index.equals("shouye")){
            switcher.showNext();
            switcher_flag = 1;
        }
        if ( index!=null&&index.equals("wode")){
            switcher.showNext();
            switcher_flag = 1;
            Rwode.setChecked(true);
            currentIndex = 3;
            showFragment();
        }
        rg = (RadioGroup) findViewById(R.id.main_rg);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i){
                    case R.id.main_shouye:
                        currentIndex = 0;
                        myindex = 0;
                        break;
                    case R.id.main_hudong:
                        currentIndex = 1;
                        myindex = 1;
                        break;
                    case R.id.main_kongjian:
                        currentIndex = 2;
                        myindex = 2;
                        break;
                    case R.id.main_wode:
                        currentIndex = 3;
                        break;
                }
                showFragment();
            }
        });
    }


    /**
     * 使用show() hide()切换页面
     * 显示fragment
     */
    private void showFragment(){

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (currentIndex==3&&token.equals("")){
            //判断用户是否登录
                Intent intent = new Intent();
                intent.putExtra("status","0");
                intent.setClass(MainActivity.this,Login.class);
                startActivity(intent);
            if (myindex==0){
                Rshouye.setChecked(true);
            }else if (myindex==1){
                Rhudong.setChecked(true);
            }else if (myindex==2){
                Rkongjian.setChecked(true);
            }
        }
        //如果之前没有添加过
        else if(!fragments.get(currentIndex).isAdded()){
            transaction
                    .hide(currentFragment)
                    .add(R.id.main_content,fragments.get(currentIndex),""+currentIndex);  //第三个参数为添加当前的fragment时绑定一个tag

            currentFragment = fragments.get(currentIndex);
            transaction.commit();
        }else{
            transaction
                    .hide(currentFragment)
                    .show(fragments.get(currentIndex));
            currentFragment = fragments.get(currentIndex);
            transaction.commit();
        }



    }

    /**
     * 恢复fragment
     */
    private void restoreFragment(){
        FragmentTransaction mBeginTreansaction = fragmentManager.beginTransaction();
        for (int i = 0; i < fragments.size(); i++) {
            if(i == currentIndex){
                mBeginTreansaction.show(fragments.get(i));
            }else{
                mBeginTreansaction.hide(fragments.get(i));
            }
        }
        mBeginTreansaction.commit();
        //把当前显示的fragment记录下来
        currentFragment = fragments.get(currentIndex);
    }
}
