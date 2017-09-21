package com.example.administrator.yys.wode;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.yys.R;
import com.example.administrator.yys.kongjian.KongJian_SheZhi_Desc;
import com.example.administrator.yys.kongjian.KongJian_SheZhi_Name;
import com.example.administrator.yys.utils.AppManager;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import static com.example.administrator.yys.utils.IPAddress.IP;

/**
 * Created by Administrator on 2017/7/24 0024.
 */

public class WoDe_SheZhi_ZiLiao extends Activity implements View.OnClickListener{
    LinearLayout fanhui;
    TextView baocun,text_name,text_desc;
    LinearLayout touxiang,mingcheng,miaoshu;
    String user_avatar,user_name,gexingqianmin,newname="",newdesc="",newAvatar="",token;
    SharedPreferences userinfo;
    String newfilePath;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wode_shezhi_ziliao);
        AppManager.getAppManager().addActivity(this);
        fanhui = findViewById(R.id.wode_shezhi_ziliao_fanhui);
        userinfo = getSharedPreferences("user",MODE_PRIVATE);
        token = userinfo.getString("token","");
        baocun = findViewById(R.id.wd_shezhi_ziliao_baocun);
        touxiang = findViewById(R.id.wd_shezhi_touxiang_lin);
        mingcheng = findViewById(R.id.wd_shezhi_mingcheng_lin);
        miaoshu = findViewById(R.id.wd_shezhi_miaoshu_lin);
        text_name = findViewById(R.id.wd_shezhi_name);
        text_desc = findViewById(R.id.wd_shezhi_miaoshu);
        user_avatar = getSharedPreferences("user",MODE_PRIVATE).getString("user_avatar","");
        user_name = getSharedPreferences("user",MODE_PRIVATE).getString("user_name","");
        gexingqianmin = getSharedPreferences("user",MODE_PRIVATE).getString("gexingqianmin","");
        if (gexingqianmin.equals("null")){
            gexingqianmin="";
        }
        newname = user_name;
        newdesc = gexingqianmin;
        newAvatar = user_avatar;
        Log.w("check gexin",gexingqianmin);
        text_name.setText(user_name);
        if (gexingqianmin.length()>=10){
            text_desc.setText(gexingqianmin.substring(0,10)+"...");
        }else {
            text_desc.setText(gexingqianmin);
        }
        fanhui.setOnClickListener(this);
        baocun.setOnClickListener(this);
        touxiang.setOnClickListener(this);
        mingcheng.setOnClickListener(this);
        miaoshu.setOnClickListener(this);

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
                text_name.setText(namestr);
                newname = namestr;
                break;
            case 3:
                String descstr = data.getStringExtra("desc");
                if (descstr.length()>=10){
                    String tempdesc = descstr.substring(0,10);
                    text_desc.setText(tempdesc+"...");
                }else{
                    text_desc.setText(descstr);
                }
                newdesc = descstr;
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
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==1){
                Toast.makeText(getApplicationContext(),"帐号名称不能为空",Toast.LENGTH_SHORT).show();
            }
            if (msg.what==2){
                Toast.makeText(getApplicationContext(),"没有修改信息",Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.wode_shezhi_ziliao_fanhui:
                finish();
                break;
            case R.id.wd_shezhi_ziliao_baocun://保存
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        String url = IP+"lifetime/user/account/uptUserInfo";
                        RequestParams params = new RequestParams();
                        params.addBodyParameter("token",token);
                        if (newname.equals("")){
                            Message msg = new Message();
                            msg.what=1;
                            handler.sendMessage(msg);
                            return;
                        }
                        int i=0;
                        if (!newname.equals(user_name)){
                            params.addBodyParameter("userName",newname);
                            Log.w("newname",newname);
                            i=1;
                        }
                        if (!newAvatar.equals(user_avatar)){
                            params.addBodyParameter("avatar",newAvatar);
                            Log.w("newAvatar",newAvatar+" user_avatar"+user_avatar);
                            i=1;
                        }
                        if (!newdesc.equals(gexingqianmin)){
                            params.addBodyParameter("personalSignature",newdesc);
                            Log.w("newdesc",newdesc);
                            i=1;
                        }
                        if (i==0){
                            Message msg = new Message();
                            msg.what=2;
                            handler.sendMessage(msg);
                            return;
                        }
                        HttpUtils httputils = new HttpUtils();
                        httputils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
                            @Override
                            public void onSuccess(ResponseInfo<String> responseInfo) {
                                Log.w("uptUserInfo success:",responseInfo.result.toString());
                                try {
                                    JSONObject obj = new JSONObject(responseInfo.result.toString());
                                    String code = obj.getString("code");
                                    if (code.equals("1")){
                                        SharedPreferences.Editor editor = userinfo.edit();
                                            editor.putString("user_name",newname);
                                            editor.putString("user_avatar",newAvatar);
                                            editor.putString("gexingqianmin",newdesc);
                                        editor.commit();
                                        Intent intent = new Intent();
                                        intent.setAction("android.intent.action.UPDATE_FRAGMENT");
                                        intent.putExtra("index","wode");
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
                                Log.w("uptUserInfo faile",s.toString());
                            }
                        });
                    }
                }.start();
                break;
            case R.id.wd_shezhi_touxiang_lin:
                Intent intent = new Intent();
               // intent.setClass(WoDe_SheZhi_ZiLiao.this,SelectPicActivity.class);
                intent.setAction(Intent.ACTION_PICK);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,1);
                break;
            case R.id.wd_shezhi_mingcheng_lin:
                Intent intent2 = new Intent();
                intent2.putExtra("namestr",user_name);
                intent2.setClass(WoDe_SheZhi_ZiLiao.this,KongJian_SheZhi_Name.class);
                startActivityForResult(intent2,0);
                break;
            case R.id.wd_shezhi_miaoshu_lin:
                Intent intent3 = new Intent();
                intent3.putExtra("descstr",gexingqianmin);
                intent3.setClass(WoDe_SheZhi_ZiLiao.this,KongJian_SheZhi_Desc.class);
                startActivityForResult(intent3,0);
                break;
        }
    }
}
