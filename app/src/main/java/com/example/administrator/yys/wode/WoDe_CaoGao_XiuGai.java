package com.example.administrator.yys.wode;

import android.app.Activity;
import android.content.ContentResolver;
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
import com.example.administrator.yys.utils.ParseJson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.administrator.yys.utils.IPAddress.IP;

/**
 * Created by Administrator on 2017/7/11 0011.
 */

public class WoDe_CaoGao_XiuGai extends Activity implements View.OnClickListener{
    TextView jilu;
    EditText edit;
    LinearLayout chatu;
    String content = "",storyid;
    LinearLayout fanhui;
    Bitmap bitmap;
    Uri originalUri;
    String root,newfilePath;
    SharedPreferences wenzhanginfo;
    String token;
    String wenzhangtext;
    int storystatus = 0;//更新为1 发表为0
    ArrayList<Map<String,String>> list = new ArrayList<>();
    LinearLayout progress;
    int progressflag = 1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wode_caogao_xiugai);
        jilu = findViewById(R.id.wode_caogao_jilu);
        edit = findViewById(R.id.wode_geren_bianji_edit);
        chatu = findViewById(R.id.wode_geren_bianji_chatu);
        AppManager.getAppManager().addActivity(this);
        token = getSharedPreferences("user",MODE_PRIVATE).getString("token","");
        wenzhanginfo = getSharedPreferences("wenzhang",MODE_PRIVATE);
        fanhui = findViewById(R.id.kongjian_gushi_info_geren_fanhui);
        root = getSharedPreferences("user",MODE_PRIVATE).getString("root","");
        progress = findViewById(R.id.caogao_progress);
        fanhui.setOnClickListener(this);
        chatu.setOnClickListener(this);
        jilu.setOnClickListener(this);
        Intent intent = getIntent();
        content = intent.getStringExtra("content");
        storyid = intent.getStringExtra("interact_story_id");
        init();
    }

    Handler handler1 = new MyHandler(this){
        @Override
        public void handleMessage(Message msg) {
           super.handleMessage(msg);
            if (msg.what==1){
                Map map = (Map) msg.obj;
                String url = map.get("url").toString();
                Bitmap bitmap1 = (Bitmap) map.get("bitmap");
                Bitmap bitmap2 = resizeImage1(bitmap1);
               //edit.append("\n");
                edit.append(getBitmapMime2(bitmap2, url));
                //当有图片的时候  换行
               // edit.append("\n");
            }else if (msg.what==2){
                String text= msg.obj.toString().replaceAll("%5Cn","\n");
                //加到末尾
                SpannableString ss = new SpannableString(text);
                edit.append(ss);
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
                    if (str[i].length()>=9&&str[i].substring(0,9).equals("/stories/")){
                        String url = root+str[i];
                        //把图片 加到末尾
                        Bitmap bitmap = getBitmap(url);
                        Message msg = new Message();
                        Map map = new HashMap();
                        map.put("bitmap",bitmap);
                        map.put("url",url);
                        msg.obj = map;
                        msg.what = 1;
                        handler1.sendMessage(msg);
                    }else{
                        Message msg = new Message();
                        msg.obj = str[i];
                        msg.what = 2;
                        handler1.sendMessage(msg);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ContentResolver resolver = getContentResolver();
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
                Toast.makeText(WoDe_CaoGao_XiuGai.this, "获取图片失败",Toast.LENGTH_SHORT).show();
            }
        }
       /* if (resultCode == 2) {
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
                Toast.makeText(WoDe_GuShi_XiuGai.this, "获取图片失败",Toast.LENGTH_SHORT).show();
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
    private String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
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
    private SpannableString getBitmapMime2(Bitmap pic, String url) {
        String path = url;
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
        edit.setSelection(edit.getSelectionStart());// 设置Edittext中光标在最后面显示
    }

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
            case R.id.wode_caogao_jilu:
                if (edit.getText().toString().length()<=120){
                    Toast.makeText(WoDe_CaoGao_XiuGai.this,"内容不能小于120字",Toast.LENGTH_SHORT).show();
                }
                else{
                    progress.setVisibility(View.VISIBLE);
                    setprogress();
                    jilu.setClickable(false);
                    tijiao();
                }
                break;
            case R.id.wode_geren_bianji_chatu:
                if (checkNum(edit.getText().toString())){
                    Toast.makeText(this,"插入图片不能超过3张", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent();
                    //intent.setClass(WoDe_GuShi_XiuGai.this, SelectPicActivity.class);
                    intent.setAction(Intent.ACTION_PICK);
                    intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent,1);
                }
                break;
            case R.id.kongjian_gushi_info_geren_fanhui:
                //保存草稿
                storystatus = 1;
                tijiao();
                break;
        }
    }

    private void tijiao() {
        final String content = edit.getText().toString();
        SharedPreferences.Editor editor = wenzhanginfo.edit();
        editor.putString("wenzhangtext",edit.getText().toString());
        editor.commit();
        //有图片则执行for
        if (edit.getText().toString().indexOf("/external/")!=-1||edit.getText().toString().indexOf("/emulated/")!=-1){
            for (int i=0;i<list.size();i++){
                Map<String,String> map = new HashMap<>();
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
                        HttpUtils httputils = new HttpUtils();
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
                                            //wenzhangcontent = URLEncoder.encode(wenzhanginfo.getString("wenzhangtext","").replaceAll("\n","%5Cn"),"UTF-8");
                                            wenzhangcontent = wenzhanginfo.getString("wenzhangtext","").replaceAll("\n","%5Cn");
                                            String newwenzhang = wenzhangcontent.replaceAll("http://120.27.217.2/upload","＜img＞");
                                            Log.w("文章更改后：",newwenzhang);
                                            String newwenzhang2 = new ParseJson().addimg(newwenzhang);
                                            String url = "";
                                            if (storystatus==0){
                                                url = IP+"lifetime/story/account/publishDraftBoxStory";
                                            }else if (storystatus==1){
                                                url = IP+"lifetime/story/account/updateDraftBoxStory";
                                            }
                                            RequestParams params = new RequestParams();
                                            params.addBodyParameter("content",newwenzhang2);
                                            params.addBodyParameter("token",token);
                                            params.addBodyParameter("storyId",storyid);
                                            HttpUtils httputils = new HttpUtils();
                                            httputils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>(){
                                                @Override
                                                public void onSuccess(ResponseInfo<String> responseInfo) {
                                                    Log.w("fabiao success",responseInfo.result.toString());
                                                    try {
                                                        JSONObject obj = new JSONObject(responseInfo.result.toString());
                                                        String code = obj.getString("code");
                                                        if (code.equals("1")){
                                                            WoDe_CaoGao_XiuGai.this.setResult(1);
                                                            if(storystatus==0){
                                                                Toast.makeText(getApplicationContext(),"等待审核",Toast.LENGTH_SHORT).show();
                                                            }
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
                    //wenzhangcontent = URLEncoder.encode(wenzhanginfo.getString("wenzhangtext","").replaceAll("\n","%5Cn"),"UTF-8");
                    wenzhangcontent = wenzhanginfo.getString("wenzhangtext","").replaceAll("\n","%5Cn");
                    String newwenzhang = wenzhangcontent.replaceAll("http://120.27.217.2/upload","＜img＞");
                    Log.w("文章更改后：",newwenzhang);
                    String newwenzhang2 = new ParseJson().addimg(newwenzhang);
                    String url = "";
                    if (storystatus==0){
                        url = IP+"lifetime/story/account/publishDraftBoxStory";
                    }else if (storystatus==1){
                        url = IP+"lifetime/story/account/updateDraftBoxStory";
                    }
                    RequestParams params = new RequestParams();
                    params.addBodyParameter("content",newwenzhang2);
                    params.addBodyParameter("token",token);
                    params.addBodyParameter("storyId",storyid);
                    HttpUtils httputils = new HttpUtils();
                    httputils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>(){
                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                            Log.w("fabiao success",responseInfo.result.toString());
                            try {
                                JSONObject obj = new JSONObject(responseInfo.result.toString());
                                String code = obj.getString("code");
                                if (code.equals("1")){
                                    WoDe_CaoGao_XiuGai.this.setResult(1);
                                    if(storystatus==0){
                                        Toast.makeText(getApplicationContext(),"等待审核",Toast.LENGTH_SHORT).show();
                                    }
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

    private boolean checkNum(String s) {
            int count1 = 0;
            int count2 = 0;
            String str1 = "/external/";
            String str2 = "/storys/";
            String ss = "/emulated/";
            String tempstr = "";
            int m = s.length();
            for(int i=0;i<m-10;i++){
                tempstr = s.substring(i,i+10);
                if (tempstr.equals(str1)||tempstr.equals(ss)){
                    count1++;
                }
            }
        for(int i=0;i<m-8;i++){
            tempstr = s.substring(i,i+8);
            if (tempstr.equals(str2)){
                count2++;
            }
        }
            Log.w("count:",count1+count2+"");
            if ((count1+count2)>2){
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
