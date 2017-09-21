package com.example.administrator.yys.kongjian;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.yys.R;
import com.example.administrator.yys.network.NetWorkRequest;
import com.example.administrator.yys.utils.AppManager;
import com.example.administrator.yys.utils.MyHandler;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.example.administrator.yys.utils.IPAddress.IP;

/**
 * Created by Administrator on 2017/6/29 0029.
 */

public class KongJian_SheZhi extends Activity implements View.OnClickListener{
    LinearLayout fanhui;
    TextView baocun;
    String touxiangstr;
    String namestr;
    String descstr;
    String defaultid;
    String groupid;
    String newAvatar = "";
    String newname = "";
    String newDesc = "";
    LinearLayout touxiang_lin;
    LinearLayout name_lin;
    LinearLayout desc_lin;
    LinearLayout shengji_lin;
    LinearLayout shanchu_lin;
    LinearLayout chengyuan_lin;
    TextView name;
    TextView desc;
    String token,root;
    String update_time;
    TextView size_total;
    TextView size_used;
    ImageView toux;
    String newfilePath;
    Intent callbackintent;
    String password;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kongjian_shezhi);
        AppManager.getAppManager().addActivity(this);
        fanhui = (LinearLayout) findViewById(R.id.kongjian_gushi_info_geren_fanhui);
        baocun = (TextView) findViewById(R.id.kj_shezhi_baocun);
        touxiang_lin = (LinearLayout) findViewById(R.id.kj_shezhi_touxiang_lin);
        name_lin = (LinearLayout) findViewById(R.id.kj_shezhi_name_lin);
        desc_lin = (LinearLayout) findViewById(R.id.kj_shezhi_desc_lin);
        size_total = findViewById(R.id.kongjian_size_total);
        toux = findViewById(R.id.shezhi_touxiang);
        size_used = findViewById(R.id.kongjian_size_used);
        x.Ext.init(getApplication());
        shengji_lin = (LinearLayout) findViewById(R.id.kj_shezhi_shengji_lin);
        chengyuan_lin = (LinearLayout) findViewById(R.id.kj_shezhi_chengyuan_lin);
        shanchu_lin = (LinearLayout) findViewById(R.id.kj_shezhi_shanchu_lin);
        token = getSharedPreferences("user",MODE_PRIVATE).getString("token","");
        root = getSharedPreferences("user",MODE_PRIVATE).getString("root","");
        name = (TextView) findViewById(R.id.kj_shezhi_name);
        desc = (TextView) findViewById(R.id.kj_shezhi_desc);


        baocun.setOnClickListener(this);
        fanhui.setOnClickListener(this);
        touxiang_lin.setOnClickListener(this);
        name_lin.setOnClickListener(this);
        desc_lin.setOnClickListener(this);
        shengji_lin.setOnClickListener(this);
        chengyuan_lin.setOnClickListener(this);
        shanchu_lin.setOnClickListener(this);

        Intent intent = getIntent();
        callbackintent = intent.getParcelableExtra("callbackintent");
        touxiangstr = intent.getStringExtra("touxiangstr");
        namestr = intent.getStringExtra("namestr");
        descstr = intent.getStringExtra("descstr");
        password = intent.getStringExtra("password");
        update_time = intent.getStringExtra("update_time");
        defaultid = intent.getStringExtra("defaultid");
        groupid = intent.getStringExtra("groupid");
        if (defaultid.equals("1")){
            chengyuan_lin.setVisibility(View.GONE);
        }
        name.setText(namestr);
        desc.setText(descstr);
    }

    @Override
    protected void onStart() {
        super.onStart();
        init();
    }

    Handler handler = new MyHandler(this){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==1){
                Log.w("getusedinfo",msg.obj.toString());
                try {
                    JSONObject obj = new JSONObject(msg.obj.toString());
                    String code = obj.getString("code");
                    if (code.equals("1")){
                        JSONObject datum = obj.getJSONObject("datum");
                        long total = datum.getLong("totalSize");
                        long used = datum.getLong("usedSize");
                        long stotal = total/1024/1024;
                        Log.w("sizeinfo","total:"+total+" stotal:"+stotal);
                        size_total.setText(stotal+"");
                        long sused = used/1024/1024;
                        size_used.setText(sused+"");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (msg.what==2){
                showDialog();
            }
        }
    };

    private void init() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                String url = IP+"lifetime/group/account/getUsedInfo?token="+token+"&groupId="+groupid;
                String  result =  new NetWorkRequest().getServiceInfo(url);
                Message msg = new Message();
                msg.obj = result;
                msg.what = 1;
                handler.sendMessage(msg);
            }
        }.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case 1:
                Uri originalUri =data.getData();
                startPhotoZoom(originalUri);
                break;

            case 2:
                Uri originalUri1 =data.getData();
                startPhotoZoom(originalUri1);
                break;
            case 4:
                String namestr = data.getStringExtra("name");
                name.setText(namestr);
                newname = namestr;
                break;
            case 3:
                String descstr = data.getStringExtra("desc");
                if (descstr.length()>11){
                    String tempdesc = descstr.substring(0,11);
                    desc.setText(tempdesc+"...");
                }else{
                    desc.setText(descstr);
                }
                newDesc = descstr;
                break;
        }
        if (requestCode==5){
            if (data!=null){
                saveFile(data);
            }
        }
        if (requestCode==1 &&data!=null){
            Uri originalUri =data.getData();
            startPhotoZoom(originalUri);
        }
    }

    private void saveFile(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            File file=new File(Environment.getExternalStorageDirectory(),"youyisheng");
            if(!file.exists()){
                file.mkdir();
            }
            File output=new File(file,System.currentTimeMillis()+".jpg");
            newfilePath = output.getPath();
            BufferedOutputStream bos = null;
            try {
                bos = new BufferedOutputStream(new FileOutputStream(output));
                photo.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                bos.flush();
                bos.close();
                uploadavatar();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadavatar() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                File file = new File(newfilePath);
                if (file.exists()) {
                    //上传文件
                    String url = IP + "lifetime/upload/account/avatarUpload";
                    RequestParams params = new RequestParams();
                    params.addBodyParameter("avatar", file);
                    params.addBodyParameter("token", token);
                    //params.addBodyParameter("groupId", groupid);
                    final HttpUtils httputils = new HttpUtils();
                    httputils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                            Log.w("imguploadsuccess", responseInfo.result.toString());
                            try {
                                JSONObject obj = new JSONObject(responseInfo.result.toString());
                                int code = obj.getInt("code");
                                if (code == 1) {
                                    String neturl = obj.getString("datum");
                                    Log.w("neturl:", neturl);
                                    newAvatar = neturl;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onFailure(HttpException e, String s) {
                            Log.w("imguploadfailure", s.toString());
                        }
                    });
                }
            }
        }.start();
    }

    private void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        //下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 5);
    }

    private boolean checkTime(){
        Log.w("update_time",update_time);
        if (update_time != null && !update_time.equals("") && !update_time.equals("null")) {//不是第一次修改
            //修改时限30天
            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(update_time);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(Calendar.DATE, 30);//上次更新时间加30天
               if (calendar.getTimeInMillis()-System.currentTimeMillis()>0){
                   return  false;
               }else {
                   return true;
               }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            return true;
        }

        return false;
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.kongjian_gushi_info_geren_fanhui:
                finish();
                break;
            case R.id.kj_shezhi_baocun:
                if (checkTime()){
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            String url = IP+"lifetime/group/account/uptGroup";
                            RequestParams params = new RequestParams();
                            params.addBodyParameter("token",token);
                            params.addBodyParameter("groupId",groupid);
                            if (!newAvatar.equals("")){
                                params.addBodyParameter("groupAvatar",newAvatar);
                                callbackintent.putExtra("group_avatar",root+newAvatar);
                            }
                            if (!newname.equals("")){
                                params.addBodyParameter("groupName",newname);
                                callbackintent.putExtra("group_name",newname);
                            }
                            if (!newDesc.equals("")){
                                params.addBodyParameter("groupDescribe",newDesc);
                                callbackintent.putExtra("group_describe",newDesc);
                            }
                            HttpUtils httputils = new HttpUtils();
                            httputils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
                                @Override
                                public void onSuccess(ResponseInfo<String> responseInfo) {
                                    Log.w("uptgroup success:",responseInfo.result.toString());
                                    try {
                                        JSONObject obj = new JSONObject(responseInfo.result.toString());
                                        String code = obj.getString("code");
                                        if (code.equals("1")){
                                            callbackintent.putExtra("update_time",new SimpleDateFormat("yyyy-MM-dd").format(new Date()).toString());
                                            KongJian_SheZhi.this.setResult(4,callbackintent);
                                            Intent intent = new Intent();
                                            intent.putExtra("index","kongjian");
                                            intent.setAction("android.intent.action.UPDATE_FRAGMENT");
                                            sendBroadcast(intent);
                                            finish();
                                        }
                                        Toast.makeText(getApplicationContext(),obj.getString("message"),Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                @Override
                                public void onFailure(HttpException e, String s) {
                                    Log.w("uptgroup faile",s.toString());
                                }
                            });
                        }
                    }.start();
                }else {
                    showDialog2();
                }

                break;
            case R.id.kj_shezhi_touxiang_lin:
                if (checkTime()){
                    Intent intent = new Intent();
                    //intent.setClass(KongJian_SheZhi.this, SelectPicActivity.class);
                    intent.setAction(Intent.ACTION_PICK);
                    intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent,1);
                }else {
                    showDialog2();
                }

                break;
             case R.id.kj_shezhi_name_lin:
                 if (checkTime()){
                     Intent intent2 = new Intent();
                     intent2.putExtra("namestr",namestr);
                     intent2.setClass(KongJian_SheZhi.this,KongJian_SheZhi_Name.class);
                     startActivityForResult(intent2,0);
                 }else {
                     showDialog2();
                 }

                break;
             case R.id.kj_shezhi_desc_lin:
                 if (checkTime()){
                     Intent intent1 = new Intent();
                     intent1.putExtra("descstr",descstr);
                     intent1.setClass(KongJian_SheZhi.this,KongJian_SheZhi_Desc.class);
                     startActivityForResult(intent1,0);
                 }else {
                     showDialog2();
                 }

                break;
             case R.id.kj_shezhi_shengji_lin:
                 //Toast.makeText(getApplicationContext(),"敬请期待！",Toast.LENGTH_SHORT).show();
                 Intent intent4 = new Intent();
                 intent4.putExtra("groupid",groupid);
                 intent4.setClass(KongJian_SheZhi.this,KongJian_shenJi.class);
                 startActivity(intent4);
                break;
            case R.id.kj_shezhi_shanchu_lin:
                Message msg = new Message();
                msg.what = 2;
                handler.sendMessage(msg);
                break;
            case R.id.kj_shezhi_chengyuan_lin:
                Intent intent3 = new Intent();
                intent3.setClass(KongJian_SheZhi.this,Kongjian_ChengYuan.class);
                intent3.putExtra("groupid",groupid);
                startActivity(intent3);
                break;
        }
    }

    private void showDialog2() {
        AlertDialog.Builder builder = new AlertDialog.Builder(KongJian_SheZhi.this);
        builder.setMessage("修改周期为30天");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

       /* builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                i = 0;
            }
        });
*/
        builder.setCancelable(false);
        builder.create().show();
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(KongJian_SheZhi.this);
        builder.setMessage("将删除空间所有内容，确定删除吗");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                //删除空间
                String url = IP+"lifetime/group/account/secede";
                RequestParams params = new RequestParams();
                params.addBodyParameter("token",token);
                params.addBodyParameter("groupId",groupid);
                params.addBodyParameter("password",password);
                HttpUtils httputils = new HttpUtils();
                httputils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        Log.w("delgroup success:",responseInfo.result.toString());
                        try {
                            JSONObject obj = new JSONObject(responseInfo.result.toString());
                            String code = obj.getString("code");
                            if (code.equals("1")){
                                Intent intent = new Intent();
                                intent.putExtra("index","kongjian");
                                intent.setAction("android.intent.action.UPDATE_FRAGMENT");
                                sendBroadcast(intent);
                                KongJian_SheZhi.this.setResult(2);
                                Toast.makeText(getApplicationContext(),"已删除",Toast.LENGTH_SHORT).show();
                                finish();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFailure(HttpException e, String s) {
                        Log.w("delgroup faile",s.toString());
                    }
                });
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                i = 0;
            }
        });

        builder.setCancelable(false);
        builder.create().show();
    }
}
