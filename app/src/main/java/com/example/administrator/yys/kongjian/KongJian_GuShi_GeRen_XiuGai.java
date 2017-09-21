package com.example.administrator.yys.kongjian;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.yys.R;
import com.example.administrator.yys.network.NetWorkRequest;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.administrator.yys.utils.IPAddress.IP;

/**
 * Created by Administrator on 2017/7/11 0011.
 */

public class KongJian_GuShi_GeRen_XiuGai extends Activity implements View.OnClickListener{
    TextView jilu;
    EditText edit;
    LinearLayout chatu;
    String content = "";
    LinearLayout fanhui;
    Bitmap bitmap;
    Uri originalUri;
    String root;
    SharedPreferences wenzhanginfo;
    String group_id;
    String group_article_id;
    String token;
    String wenzhangtext;
    Intent callbackintent;
    ArrayList<Map<String,String>> list = new ArrayList<>();
    int usedsize,totalsize,useingsize;
    LinearLayout progress;
    int progressflag = 1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kongjian_gushi_geren_xiugai);
        jilu = findViewById(R.id.kongjian_gushi_info_geren_jilu);
        edit = findViewById(R.id.kj_geren_bianji_edit);
        chatu = findViewById(R.id.kj_geren_bianji_chatu);
        AppManager.getAppManager().addActivity(this);
        progress = findViewById(R.id.kongjian_xiugai_progress);
        token = getSharedPreferences("user",MODE_PRIVATE).getString("token","");
        wenzhanginfo = getSharedPreferences("wenzhang",MODE_PRIVATE);
        fanhui = findViewById(R.id.kongjian_gushi_info_geren_fanhui);
        root = getSharedPreferences("user",MODE_PRIVATE).getString("root","");
        fanhui.setOnClickListener(this);
        chatu.setOnClickListener(this);
        jilu.setOnClickListener(this);
        Intent intent = getIntent();
        callbackintent = intent.getParcelableExtra("callbackintent");
        content = intent.getStringExtra("content");
        group_id = intent.getStringExtra("group_id");
        group_article_id = intent.getStringExtra("group_article_id");
        init();
        getSpaceSize();
    }

    private void getSpaceSize(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                String url = IP+"lifetime/group/account/getUsedInfo?token="+token+"&groupId="+group_id;
                String  result =  new NetWorkRequest().getServiceInfo(url);
                Message msg = new Message();
                msg.obj = result;
                msg.what = 3;
                handler1.sendMessage(msg);
            }
        }.start();
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
                //edit.append("\n");
            }else if (msg.what==2){
                String text= msg.obj.toString().replaceAll("%5Cn","\n");
                //加到末尾
                SpannableString ss = new SpannableString(text);
                edit.append(ss);
            }
            if (msg.what==3&& msg.obj!=null){
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
    private void init() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                String[] str = content.split("＜img＞");
                for (int i=0;i<str.length;i++){
                    if (str[i].length()>=8&&str[i].substring(0,8).equals("/groups/")){
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
        if (resultCode == RESULT_OK) {
            if (requestCode == 1 &&data!=null) {
                originalUri = data.getData();
                String path = getPath(originalUri);
                Log.w("Path:",path);
                Log.w("originalUri:",originalUri.toString());
                Bitmap originalBitmap = BitmapFactory.decodeFile(path);
                bitmap = resizeImage1(originalBitmap);
                addMap(originalUri,path);
                if (bitmap != null) {
                    insertIntoEditText(getBitmapMime(bitmap, originalUri));
                } else {
                    Toast.makeText(KongJian_GuShi_GeRen_XiuGai.this, "获取图片失败",Toast.LENGTH_SHORT).show();
                }
            }
        }
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
                        Toast.makeText(KongJian_GuShi_GeRen_XiuGai.this,obj.getString("message"),Toast.LENGTH_SHORT).show();
                        KongJian_GuShi_GeRen_XiuGai.this.setResult(1);
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private void addMap(Uri originalUri, String path) {
        String uri = originalUri.toString().substring(15);
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
        useingsize+=baos.toByteArray().length;
        return bitmap;
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
            case R.id.kongjian_gushi_info_geren_jilu:
                if (edit.getText().toString().length()==0){
                    Toast.makeText(KongJian_GuShi_GeRen_XiuGai.this,"内容不能为空",Toast.LENGTH_SHORT).show();
                }else{
                    Log.w("size info","total:"+totalsize+" used:"+usedsize+" useing:"+useingsize);
                    if (totalsize-usedsize<useingsize){
                        showdialog();
                        return;
                    }else {
                        progress.setVisibility(View.VISIBLE);
                        setprogress();
                        tijiao();
                        jilu.setClickable(false);
                    }
                }
                break;
            case R.id.kj_geren_bianji_chatu:
                if (checkNum(edit.getText().toString())){
                    Toast.makeText(this,"插入图片不能超过3张", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_PICK);
                    intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent,1);
                }
                break;
            case R.id.kongjian_gushi_info_geren_fanhui:
                finish();
                break;
        }
    }

    private void tijiao() {
        final String content = edit.getText().toString();
        SharedPreferences.Editor editor = wenzhanginfo.edit();
        editor.putString("wenzhangtext",edit.getText().toString());
        editor.commit();
        //有图片则执行for
        if (edit.getText().toString().indexOf("/external/")!=-1){
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
                        String url = IP+"lifetime/upload/account/groupUpload";
                        RequestParams params = new RequestParams();
                        params.addBodyParameter("file",file);
                        params.addBodyParameter("token",token);
                        params.addBodyParameter("groupId",group_id);
                        Log.w("groupid是",group_id);
                        HttpUtils httputils = new HttpUtils();
                        httputils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
                            @Override
                            public void onSuccess(ResponseInfo<String> responseInfo) {
                                Log.w("imguploadsuccess",responseInfo.result.toString());
                                try {
                                    JSONObject obj = new JSONObject(responseInfo.result.toString());
                                    int code = obj.getInt("code");
                                    if (code==1){
                                        String neturl = obj.getJSONObject("datum").getString("file");
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
                                if (wenzhanginfo.getString("wenzhangtext","").indexOf("/external/")==-1){
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            String wenzhangcontent = null;
                                            //wenzhangcontent = URLEncoder.encode(wenzhanginfo.getString("wenzhangtext","").replaceAll("\n","%5Cn"),"UTF-8");
                                            wenzhangcontent = wenzhanginfo.getString("wenzhangtext","").replaceAll("\n","%5Cn");
                                            String newwenzhang = wenzhangcontent.replaceAll("http://120.27.217.2/upload","＜img＞");
                                            Log.w("文章更改后：",newwenzhang);
                                            try {
                                                String newwenzhang2 = URLEncoder.encode(new ParseJson().addimg(newwenzhang),"UTF-8");
                                                  String url = IP+"lifetime/group/account/uptArticle?token="
                                                        +token+"&articleId="+group_article_id+"&content="+newwenzhang2;
                                                Log.w("bianjiurl:",url);
                                                String  result =  new NetWorkRequest().getServiceInfo(url);
                                                Message msg = new Message();
                                                msg.obj = result;
                                                msg.what = 1;
                                                handler.sendMessage(msg);
                                            } catch (UnsupportedEncodingException e) {
                                                e.printStackTrace();
                                            }

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
                    try {
                        String newwenzhang2 = URLEncoder.encode(new ParseJson().addimg(newwenzhang),"UTF-8");
                        String url = IP+"lifetime/group/account/uptArticle?token="
                                +token+"&articleId="+group_article_id+"&content="+newwenzhang2;
                         Log.w("bianjiurl:",url);
                         String  result =  new NetWorkRequest().getServiceInfo(url);
                         Message msg = new Message();
                         msg.obj = result;
                         msg.what = 1;
                         handler.sendMessage(msg);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                }
            }).start();
        }
    }

    private boolean checkNum(String s) {
            int count1 = 0;
            int count2 = 0;
            String str1 = "/external/";
            String str2 = "/groups/";
            String tempstr = "";
            int m = s.length();
            for(int i=0;i<m-10;i++){
                tempstr = s.substring(i,i+10);
                if (tempstr.equals(str1)){
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
