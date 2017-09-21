package com.example.administrator.yys.hudong;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.yys.R;
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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.administrator.yys.utils.IPAddress.IP;


/**
 * Created by Administrator on 2017/7/3 0003.
 */

public class GuShi_BianJi extends Activity implements View.OnClickListener{
    LinearLayout fanhui;
    TextView jilu;
    EditText edit;
    LinearLayout chatu;
    Uri originalUri;
    Bitmap bitmap;
    SharedPreferences userinfo;
    String token;
    ArrayList<Map<String,String>> list = new ArrayList<>();
    SharedPreferences wenzhanginfo;
    String wenzhangtext;
    String newfilePath;
    LinearLayout progress;
    int progressflag = 1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hudong_gushi_bianji);
        AppManager.getAppManager().addActivity(this);
        progress = findViewById(R.id.hudong_gushi_progress);
        fanhui = (LinearLayout) findViewById(R.id.hudong_gushi_bianji_fanhui);
        jilu = (TextView) findViewById(R.id.hudong_gushi_bianji_jilu);
        edit = (EditText) findViewById(R.id.hd_bianji_edit);
        chatu = (LinearLayout) findViewById(R.id.hd_bianji_chatu);
        userinfo = getSharedPreferences("user",MODE_PRIVATE);
        wenzhanginfo = getSharedPreferences("wenzhang",MODE_PRIVATE);

        token = userinfo.getString("token","");
        x.Ext.init(getApplication());

        fanhui.setOnClickListener(this);
        jilu.setOnClickListener(this);
        chatu.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 1&&data!=null) {
                originalUri =data.getData();
                String path = getPath(originalUri);
                Log.w("Path:",path);
                Log.w("originalUri:",originalUri.toString());
                Bitmap originalBitmap = BitmapFactory.decodeFile(path);
                bitmap = resizeImage1(originalBitmap);
                addMap(originalUri,newfilePath);
                if (bitmap != null) {
                    insertIntoEditText(getBitmapMime(bitmap, originalUri));
                } else {
                    Toast.makeText(GuShi_BianJi.this, "获取图片失败",Toast.LENGTH_SHORT).show();
                }
        }
        /*if (resultCode == 2) {
            originalUri =data.getData();
            String path = originalUri.getPath();
            Log.w("Path:",path);
            Log.w("originalUri:",originalUri.toString());
            Bitmap originalBitmap = BitmapFactory.decodeFile(path);
            bitmap = resizeImage1(originalBitmap);
            addMap(originalUri,newfilePath);
            if (bitmap != null) {
                insertIntoEditText(getBitmapMime(bitmap, originalUri));
            } else {
                Toast.makeText(GuShi_BianJi.this, "获取图片失败",Toast.LENGTH_SHORT).show();
            }
        }*/
    }

    private void addMap(Uri originalUri, String path) {
        String uri;
        if (originalUri.toString().substring(0,4).equals("file")){
            uri = originalUri.toString().substring(7);
        }else {
            uri = originalUri.toString().substring(15);
        }

        Log.w("newuri:",uri);
        Map<String,String> map = new HashMap<>();
        map.put("uri",uri);
        map.put("path",path);
        list.add(map);

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
        Bitmap compressbitmap = compressImage(resizedBitmap);
        return compressbitmap;
    }

    private Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while ( baos.toByteArray().length / 1024>50) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        Log.w("bitmap size",baos.toByteArray().length/1024+"");
        saveFile(bitmap);
        return bitmap;
    }

    private void saveFile(Bitmap bitmap) {
        File file=new File(Environment.getExternalStorageDirectory(),"youyisheng");
        if(!file.exists()){
            file.mkdir();
        }
        File output=new File(file,System.currentTimeMillis()+".jpg");
        newfilePath = output.getPath();
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(output));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.w("get filepath",output.getPath());

    }

    private SpannableString getBitmapMime(Bitmap pic, Uri uri) {
        String path = uri.getPath();
        SpannableString ss = new SpannableString(path);
        ImageSpan span = new ImageSpan(this, pic);
        ss.setSpan(span, 0, path.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }

    private void insertIntoEditText(SpannableString ss) {
        Editable et = edit.getText();// 先获取Edittext中的内容
        //et.append("\n");
        int start = edit.getSelectionStart();
        et.insert(start, ss);// 设置ss要添加的位置
        edit.setText(et);// 把et添加到Edittext中
        edit.append("\n");
        Log.w("图片插入message:","插入了一次");
        //edit.setSelection(start + ss.length());// 设置Edittext中光标在最后面显示
        edit.setSelection(edit.getSelectionStart());// 设置Edittext中光标在最后面显示

    }

    Handler handler = new MyHandler(this){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1 && msg.obj!=null){
                Log.w("bianji:",msg.obj.toString());
                try {
                    JSONObject obj = new JSONObject(msg.obj.toString());
                    String code = obj.getString("code");
                    if (code.equals("1")){
                        Toast.makeText(GuShi_BianJi.this,obj.getString("message"),Toast.LENGTH_SHORT).show();
                        finish();
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
                jilu.setClickable(true);
            }
        };
        Handler progresshandler = new Handler();
        progresshandler.postDelayed(runnable,20000);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.hudong_gushi_bianji_fanhui:
                baocun();
                finish();
                break;
            case R.id.hudong_gushi_bianji_jilu:

                if (edit.getText().toString().length()<120){
                    Toast.makeText(GuShi_BianJi.this,"故事字数不能少于120个字",Toast.LENGTH_SHORT).show();
                }else {
                    progress.setVisibility(View.VISIBLE);
                    setprogress();
                    jilu.setClickable(false);
                    tijiao();
                }


                break;
            case R.id.hd_bianji_chatu:
                if (checkNum(edit.getText().toString())){
                    Toast.makeText(this,"插入图片不能超过3张", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent();
                    //intent.setClass(GuShi_BianJi.this, SelectPicActivity.class);
                    intent.setAction(Intent.ACTION_PICK);
                    intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent,1);

                }

                break;
        }
    }

    public long getFileSizes(File f) {//取得文件大小
        long s=0;
        if (f.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(f);
                s= fis.available();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("文件不存在");
        }
        return s;
    }

    private void tijiao(){
        Log.w("content:",edit.getText().toString());
        final String content = edit.getText().toString();
        SharedPreferences.Editor editor = wenzhanginfo.edit();
        editor.putString("wenzhangtext",edit.getText().toString());
        editor.commit();
        //有图片则执行for
        if (edit.getText().toString().indexOf("/external/")!=-1||edit.getText().toString().indexOf("/emulated/")!=-1){
        for (int i=0;i<list.size();i++){
            Map<String,String> map = new HashMap();
            map = list.get(i);
            final String uri = map.get("uri");
            if (content.indexOf(uri)!=-1){
                String path = map.get("path");
                Log.w("filepath:",path);
                File file = new File(path);
                Log.w("get file size1",getFileSizes(file)+"");
                if (file.exists()){
                    //上传文件
                    String url = IP+"lifetime/upload/account/interactUpload";
                    RequestParams params = new RequestParams();
                    params.addBodyParameter("upload",file);
                    params.addBodyParameter("token",token);
                    final HttpUtils httputils = new HttpUtils();
                    httputils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                            Log.w("imguploadsuccess",responseInfo.result.toString());
                            try {
                                JSONObject obj = new JSONObject(responseInfo.result.toString());
                                int code = obj.getInt("code");
                                if (code==1){
                                    String neturl = obj.getJSONObject("datum").getString("upload");
                                    Log.w("neturl:",neturl);
                                    wenzhangtext = wenzhanginfo.getString("wenzhangtext","");
                                    String str = wenzhangtext.replaceAll(uri,"＜img＞"+neturl+"＜img＞");
                                    SharedPreferences.Editor editor = wenzhanginfo.edit();
                                    editor.putString("wenzhangtext",str);
                                    editor.commit();
                                    Log.w("the end content:",str);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.w("wenzhangsp:",wenzhanginfo.getString("wenzhangtext",""));
                            if (wenzhanginfo.getString("wenzhangtext","").indexOf("/external/")==-1&&wenzhanginfo.getString("wenzhangtext","").indexOf("/emulated/")==-1){
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String wenzhangcontent = null;
                                        wenzhangcontent = wenzhanginfo.getString("wenzhangtext","").replaceAll("\n","%5Cn");
                                        String url = IP+"lifetime/story/account/addStory";
                                        RequestParams params = new RequestParams();
                                        params.addBodyParameter("content",wenzhangcontent);
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
                        }

                        @Override
                        public void onFailure(HttpException e, String s) {
                            Log.w("imguploadfailure",s.toString());
                        }
                    });
                }else{
                    Log.w("filestatus:","文件不存在");
                }
            }

        }

        }else{
            //纯文字
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.w("wenzhangtijiao","纯文本提交");
                    String wenzhangcontent = null;
                    wenzhangcontent = wenzhanginfo.getString("wenzhangtext","").replaceAll("\n","%5Cn");
                    String url = IP+"lifetime/story/account/addStory";
                        RequestParams params = new RequestParams();
                        params.addBodyParameter("content",wenzhangcontent);
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


    }
    private void baocun(){
        Log.w("content:",edit.getText().toString());
        final String content = edit.getText().toString();
        SharedPreferences.Editor editor = wenzhanginfo.edit();
        editor.putString("wenzhangtext",edit.getText().toString());
        editor.commit();
        //有图片则执行for
        if (edit.getText().toString().indexOf("/external/")!=-1||edit.getText().toString().indexOf("/emulated/")!=-1){
        for (int i=0;i<list.size();i++){
            Map<String,String> map = new HashMap();
            map = list.get(i);
            final String uri = map.get("uri");
            if (content.indexOf(uri)!=-1){
                String path = map.get("path");
                Log.w("filepath:",path);
                File file = new File(path);
                if (file.exists()){
                    //上传文件
                    String url = IP+"lifetime/upload/account/interactUpload";
                    RequestParams params = new RequestParams();
                    params.addBodyParameter("upload",file);
                    params.addBodyParameter("token",token);
                    final HttpUtils httputils = new HttpUtils();
                    httputils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                            Log.w("imguploadsuccess",responseInfo.result.toString());
                            try {
                                JSONObject obj = new JSONObject(responseInfo.result.toString());
                                int code = obj.getInt("code");
                                if (code==1){
                                    String neturl = obj.getJSONObject("datum").getString("upload");
                                    Log.w("neturl:",neturl);
                                    wenzhangtext = wenzhanginfo.getString("wenzhangtext","");
                                    String str = wenzhangtext.replaceAll(uri,"＜img＞"+neturl+"＜img＞");
                                    SharedPreferences.Editor editor = wenzhanginfo.edit();
                                    editor.putString("wenzhangtext",str);
                                    editor.commit();
                                    Log.w("the end content:",str);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.w("wenzhangsp:",wenzhanginfo.getString("wenzhangtext",""));
                            if (wenzhanginfo.getString("wenzhangtext","").indexOf("/external/")==-1&&wenzhanginfo.getString("wenzhangtext","").indexOf("/emulated/")==-1){
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String wenzhangcontent = null;
                                        wenzhangcontent = wenzhanginfo.getString("wenzhangtext","").replaceAll("\n","%5Cn");
                                        String url = IP+"lifetime/story/account/addDraftBoxStory";
                                        RequestParams params = new RequestParams();
                                        params.addBodyParameter("content",wenzhangcontent);
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
                                                        Toast.makeText(getApplicationContext(),"已保存草稿箱",Toast.LENGTH_SHORT).show();
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
                        }

                        @Override
                        public void onFailure(HttpException e, String s) {
                            Log.w("imguploadfailure",s.toString());
                        }
                    });
                }else{
                    Log.w("filestatus:","文件不存在");
                }
            }

        }

        }else{
            //纯文字
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.w("wenzhangtijiao","纯文本提交");
                    String wenzhangcontent = null;
                    wenzhangcontent = wenzhanginfo.getString("wenzhangtext","").replaceAll("\n","%5Cn");
                    String url = IP+"lifetime/story/account/addDraftBoxStory";
                        RequestParams params = new RequestParams();
                        params.addBodyParameter("content",wenzhangcontent);
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
                                        Toast.makeText(getApplicationContext(),"已保存草稿箱",Toast.LENGTH_SHORT).show();
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


    }
    private String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    public boolean checkNum(String str){
        int count = 0;
        String s = "/external/";
        String ss = "/emulated/";
        String tempstr = "";
        int m = str.length();
        for(int i=0;i<m-10;i++){
            tempstr = str.substring(i,i+10);
            if (tempstr.equals(s)||tempstr.equals(ss)){
                count++;
            }
        }
        Log.w("count:",count+"");
        if (count>2){
            return true;
        }
        return  false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        progressflag = 0;
    }
}
