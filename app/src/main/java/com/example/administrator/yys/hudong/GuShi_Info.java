package com.example.administrator.yys.hudong;

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
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.administrator.yys.R;
import com.example.administrator.yys.network.NetWorkRequest;
import com.example.administrator.yys.other.Other_index;
import com.example.administrator.yys.utils.AppManager;
import com.example.administrator.yys.utils.MyHandler;
import com.example.administrator.yys.utils.PxAndDp;
import com.example.administrator.yys.view.CircularImage;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static com.example.administrator.yys.utils.IPAddress.IP;

/**
 * Created by Administrator on 2017/6/19 0019.
 */

public class GuShi_Info extends Activity implements View.OnClickListener{
    TextView authorname;
    LinearLayout lin;
    LinearLayout fanhui;
    LinearLayout lin_input;
    ImageView top;
    ImageView zan;
    ImageView schang;
    ImageView fenxiang;
    TextView edit;
    LinearLayout input1;
    TextView jubao;
    String content;
    String interact_story_id;
    String user_id,author_id;
    String root;
    String token;
    String myuser_id;
    Button guanzhu;
    CircularImage authorimg;
    LinearLayout pinlun_lin;
    String text,url;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        Intent intent = getIntent();
        content = intent.getStringExtra("content");
        text = intent.getStringExtra("text");
        url = intent.getStringExtra("url");
        interact_story_id = intent.getStringExtra("interact_story_id");
        user_id = intent.getStringExtra("user_id");
        if (interact_story_id.equals("")||interact_story_id==""){
            setContentView(R.layout.delcontent);
            fanhui = findViewById(R.id.del_fanhui);
            fanhui.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }else {
            setContentView(R.layout.gushi_info);
            x.Ext.init(getApplication());
            authorname = (TextView) findViewById(R.id.authorname);
            fanhui = (LinearLayout) findViewById(R.id.gushi_info_fanhui);
            lin =  findViewById(R.id.gushi_info_id);
            lin_input = (LinearLayout) findViewById(R.id.gushi_info_input);
            top = (ImageView) findViewById(R.id.pinlun_top);
            zan = (ImageView) findViewById(R.id.zan);
            authorimg = findViewById(R.id.gushi_info_touxiang1);
            pinlun_lin = findViewById(R.id.gushi_pinglun_lin);
            guanzhu = findViewById(R.id.gushi_info_guanzhu);
            input1 = (LinearLayout) findViewById(R.id.gushi_info_input);
            jubao = (TextView) findViewById(R.id.gushi_jubao);
            schang = (ImageView) findViewById(R.id.schang);
            fenxiang = (ImageView) findViewById(R.id.fenxiang);
            edit =  findViewById(R.id.gushi_info_edit);

            guanzhu.setOnClickListener(this);
            fanhui.setOnClickListener(this);
            edit.setOnClickListener(this);
            lin_input.setOnClickListener(this);
            top.setOnClickListener(this);
            zan.setOnClickListener(this);
            schang.setOnClickListener(this);
            fenxiang.setOnClickListener(this);
            jubao.setOnClickListener(this);
            root = getSharedPreferences("user",MODE_PRIVATE).getString("root","");
            token = getSharedPreferences("user",MODE_PRIVATE).getString("token","");
            myuser_id = getSharedPreferences("user",MODE_PRIVATE).getString("user_id","");



            //作者id和用户id相同就隐藏关注按钮
            if (myuser_id.equals(user_id)){
                guanzhu.setVisibility(View.GONE);
            }
            authorimg.setOnClickListener(this);
            init();
        }
    }

    Handler handler = new MyHandler(this){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                ImageView img = new ImageView(getApplicationContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                //Bitmap bitmap = (Bitmap) msg.obj;
                img.setLayoutParams(params);
                //img.setImageBitmap(bitmap);
                Glide.with(getApplicationContext()).load(msg.obj.toString()).into(img);
                lin.addView(img);
            }  if (msg.what == 2) {
                TextView text = new TextView(getApplicationContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                text.setLayoutParams(params);
                String text1 = msg.obj.toString();
                text.setText(text1.replaceAll("%5Cn", "\n"));
                text.setTextColor(Color.parseColor("#4f4f4f"));
                lin.addView(text);
            }
            super.handleMessage(msg);
            if (msg.what==3){
                Log.w("isConcern",msg.obj.toString());
                try {
                    JSONObject obj = new JSONObject(msg.obj.toString());
                    String code = obj.getString("code");
                    if (code.equals("1")){
                        String isconcern = obj.getJSONObject("datum").getString("isConcern");
                        if (isconcern.equals("true")){
                            guanzhu.setText("已关注");
                            guanzhu.setClickable(false);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }if (msg.what==4){
                Log.w("getUserInfoById",msg.obj.toString());
                try {
                    JSONObject obj = new JSONObject(msg.obj.toString());
                    String code = obj.getString("code");
                    if (code.equals("1")){
                        String user_avatar = obj.getJSONObject("datum").getString("user_avatar");
                        String user_name = obj.getJSONObject("datum").getString("user_name");
                        authorname.setText(user_name);
                        authorname.setTextColor(Color.BLACK);
                       // x.image().bind(authorimg,root+user_avatar);
                        Glide.with(getApplicationContext()).load(root+user_avatar).into(authorimg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
             if (msg.what==5){
                Log.w("tianjiaguanzhu",msg.obj.toString());
                try {
                    JSONObject obj = new JSONObject(msg.obj.toString());
                    String code = obj.getString("code");
                    if (code.equals("1")){
                        guanzhu.setText("已关注");
                        guanzhu.setClickable(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (msg.what==6){
                Log.w("getcomments",msg.obj.toString());
                try {
                    JSONObject obj = new JSONObject(msg.obj.toString());
                    String code = obj.getString("code");
                    if (code.equals("1")){
                        JSONObject datum= obj.getJSONObject("datum");
                        JSONArray rows = datum.getJSONArray("rows");
                        if (rows.length()!=0){
                            pinlun_lin.setVisibility(View.VISIBLE);
                            pinlun_lin.removeAllViews();
                            for (int i=0;i<rows.length();i++){
                                String user_avatar = rows.getJSONObject(i).getString("user_avatar");
                                final String user_id = rows.getJSONObject(i).getString("user_id");
                                String user_name = rows.getJSONObject(i).getString("user_name");
                                final String isLikeComment;
                                if (!token.equals("")){
                                    isLikeComment = rows.getJSONObject(i).getString("isLikeComment");
                                }else {
                                    isLikeComment = "false";
                                }

                                final String story_comment_id = rows.getJSONObject(i).getString("story_comment_id");
                                String content = rows.getJSONObject(i).getString("content");
                                //动态添加评论
                                LinearLayout hlin = new LinearLayout(getApplicationContext());
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(PxAndDp.dip2px(getApplicationContext(),41.6f),PxAndDp.dip2px(getApplicationContext(),41.6f));
                                LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,1);
                                hlin.setLayoutParams(params);
                                hlin.setOrientation(LinearLayout.HORIZONTAL);
                                hlin.setGravity(Gravity.CENTER);
                                hlin.setPadding(0,10,0,0);
                                CircularImage img = new CircularImage(getApplicationContext());
                                img.setLayoutParams(param1);
                                img.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (!token.equals("")){
                                            Intent intent = new Intent();
                                            intent.setClass(getApplicationContext(),Other_index.class);
                                            intent.putExtra("user_id",user_id);
                                            startActivity(intent);
                                        }else {
                                            Toast.makeText(GuShi_Info.this,"请先登录",Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });
                                //x.image().bind(img,root+user_avatar);
                                Glide.with(getApplicationContext()).load(root+user_avatar).into(img);
                                TextView name = new TextView(getApplicationContext());
                                name.setLayoutParams(param2);
                                name.setPadding(10,0,0,0);
                                name.setText(user_name);
                                name.setTextColor(Color.BLACK);
                                TextView text = new TextView(getApplicationContext());
                                text.setLayoutParams(param2);
                                final ImageView zan_pinlun = new ImageView(getApplicationContext());
                                if (isLikeComment.equals("false")){
                                    zan_pinlun.setImageResource(R.mipmap.dianzan1);
                                }else{
                                    zan_pinlun.setImageResource(R.mipmap.dianzan2);
                                    zan_pinlun.setClickable(false);
                                }
                                zan_pinlun.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Log.w("gushiinfo token",token);
                                        if (!token.equals("")) {
                                            if (isLikeComment.equals("true")){
                                                return;
                                            }
                                        //给评论点赞
                                        final String url = IP + "lifetime/story/account/likeComment";
                                        new Thread() {
                                            @Override
                                            public void run() {
                                                super.run();
                                                RequestParams params = new RequestParams();
                                                params.addBodyParameter("commentId", story_comment_id);
                                                params.addBodyParameter("token", token);
                                                HttpUtils httputils = new HttpUtils();
                                                httputils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {

                                                    @Override
                                                    public void onSuccess(ResponseInfo<String> responseInfo) {
                                                        Log.w("dianzanpinlun success", responseInfo.result.toString());
                                                        try {
                                                            JSONObject obj = new JSONObject(responseInfo.result.toString());
                                                            String code = obj.getString("code");
                                                            if (code.equals("1")) {
                                                                Toast.makeText(getApplicationContext(), "点赞成功", Toast.LENGTH_SHORT).show();
                                                                zan_pinlun.setImageResource(R.mipmap.dianzan2);
                                                                zan_pinlun.setClickable(false);
                                                            }
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(HttpException e, String s) {
                                                        Log.w("dianzan failure", s.toString());
                                                    }
                                                });
                                            }
                                        }.start();
                                    }else {
                                            Toast.makeText(getApplicationContext(),"请先登录",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                TextView info = new TextView(getApplicationContext());
                                info.setLayoutParams(param2);
                                info.setText(content);
                                info.setTextColor(Color.parseColor("#4f4f4f"));
                                hlin.addView(img);
                                hlin.addView(name);
                                hlin.addView(text);
                                hlin.addView(zan_pinlun);
                                pinlun_lin.addView(hlin);
                                pinlun_lin.addView(info);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

             if(msg.what==7){
                Log.w("islikestory",msg.obj.toString());
                try {
                    JSONObject obj = new JSONObject(msg.obj.toString());
                    String code = obj.getString("code");
                    if (code.equals("1")){
                        JSONObject datum = obj.getJSONObject("datum");
                        String isLike = datum.getString("isLike");
                        if (isLike.equals("true")){
                            zan.setImageResource(R.mipmap.zan2);
                            zan.setClickable(false);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
             if(msg.what==8){
                Log.w("collectStory",msg.obj.toString());
                try {
                    JSONObject obj = new JSONObject(msg.obj.toString());
                    String code = obj.getString("code");
                    if (code.equals("1")){
                        schang.setImageResource(R.mipmap.schang2);
                        schang.setClickable(false);
                        Toast.makeText(getApplicationContext(),"收藏成功",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
             if(msg.what==9){
                Log.w("iscollectStory",msg.obj.toString());
                try {
                    JSONObject obj = new JSONObject(msg.obj.toString());
                    String code = obj.getString("code");
                    if (code.equals("1")){
                        JSONObject datum = obj.getJSONObject("datum");
                        String isLike = datum.getString("isCollect");
                        if (isLike.equals("true")){
                            schang.setImageResource(R.mipmap.schang2);
                            schang.setClickable(false);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
           if(msg.what==10){
                Log.w("reportStory",msg.obj.toString());
                try {
                    JSONObject obj = new JSONObject(msg.obj.toString());
                    String code = obj.getString("code");
                    if (code.equals("1")){
                        Toast.makeText(getApplicationContext(),"已举报，待审核",Toast.LENGTH_SHORT).show();
                        finish();
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
                //动态添加故事内容
                String[] str = content.split("＜img＞");
                for (int i=0;i<str.length;i++){
                    if (str[i].length()>=9&&str[i].substring(0,9).equals("/stories/")){
                        String url = root+str[i];
                       /* Bitmap bitmap = getBitmap(url);
                        Bitmap bitmap2 = resizeImage1(bitmap);
                        Message msg = new Message();
                        msg.obj = bitmap2;*/
                        Message msg = new Message();
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

                //是否关注
                if (!token.equals("")){
                    String url = IP+"lifetime/concern/account/isConcern?concernId="+user_id+"&token="+token;
                    String  result1 =  new NetWorkRequest().getServiceInfo(url);
                    Message msg = new Message();
                    msg.obj = result1;
                    msg.what = 3;
                    handler.sendMessage(msg);
                }

                //获取作者信息
                String url2 = IP+"lifetime/user/account/getUserInfoById?userId="+user_id+"&token="+token;
                String  result2 =  new NetWorkRequest().getServiceInfo(url2);
                Message msg1 = new Message();
                msg1.obj = result2;
                msg1.what = 4;
                handler.sendMessage(msg1);
                //获取评论
                String url3 = IP+"lifetime/story/account/getComments?storyId="+interact_story_id+"&token="+token;
                String  result3 =  new NetWorkRequest().getServiceInfo(url3);
                Message msg3 = new Message();
                msg3.obj = result3;
                msg3.what = 6;
                handler.sendMessage(msg3);

                //是否点赞文章
                if (!token.equals("")){
                    String url4 = IP+"lifetime/story/account/isLikeStory?storyId="+interact_story_id+"&token="+token;
                    String  result4 =  new NetWorkRequest().getServiceInfo(url4);
                    Message msg4 = new Message();
                    msg4.obj = result4;
                    msg4.what = 7;
                    handler.sendMessage(msg4);
                }

                //是否收藏
                if (!token.equals("")){
                    String url5 = IP+"lifetime/story/account/isCollectStory?storyId="+interact_story_id+"&token="+token;
                    String  result5 =  new NetWorkRequest().getServiceInfo(url5);
                    Message msg5 = new Message();
                    msg5.obj = result5;
                    msg5.what = 9;
                    handler.sendMessage(msg5);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.gushi_info_fanhui:
                finish();
                break;
            case R.id.gushi_jubao://举报
                if (!token.equals("")){
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                            String url1 = IP+"lifetime/story/account/report?storyId="+interact_story_id+"&token="+token;
                            String  result =  new NetWorkRequest().getServiceInfo(url1);
                            Message msg1 = new Message();
                            msg1.obj = result;
                            msg1.what = 10;
                            handler.sendMessage(msg1);
                    }
                }.start();
                }else {
                    Toast.makeText(getApplicationContext(),"请先登录",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.fenxiang:
                if (!token.equals("")){
                    Intent intent4 = new Intent();
                    intent4.putExtra("text",text);
                    intent4.putExtra("url",url);
                    intent4.putExtra("flag","gushi");
                    intent4.putExtra("interact_story_id",interact_story_id);
                    intent4.setClass(getApplicationContext(),HuDong_FenXiang.class);
                    startActivity(intent4);
                }else {
                    Toast.makeText(getApplicationContext(),"请先登录",Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.schang:
                if (!token.equals("")){
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                            String url1 = IP+"lifetime/story/account/collectStory?storyId="+interact_story_id+"&token="+token;
                            String  result =  new NetWorkRequest().getServiceInfo(url1);
                            Message msg1 = new Message();
                            msg1.obj = result;
                            msg1.what = 8;
                            handler.sendMessage(msg1);
                    }
                }.start();
                }else {
                    Toast.makeText(getApplicationContext(),"请先登录",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.gushi_info_guanzhu://关注
                if (!token.equals("")){
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                            String url1 = IP+"lifetime/concern/account/addConcern?concernId="+user_id+"&token="+token;
                            String  result =  new NetWorkRequest().getServiceInfo(url1);
                            Message msg1 = new Message();
                            msg1.obj = result;
                            msg1.what = 5;
                            handler.sendMessage(msg1);
                    }
                }.start();
                }else {
                    Toast.makeText(getApplicationContext(),"请先登录",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.gushi_info_touxiang1://作者头像
                if (!token.equals("")){
                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(), Other_index.class);
                    intent.putExtra("user_id",user_id);
                    startActivity(intent);
                }else {
                    Toast.makeText(GuShi_Info.this,"请先登录",Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.gushi_info_edit:
                if (!token.equals("")){
                    Intent intent2 = new Intent();
                    intent2.setClass(getApplicationContext(),HuDong_InPut.class);
                    startActivityForResult(intent2,0);
                }else {
                    Toast.makeText(getApplicationContext(),"请先登录",Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.pinlun_top:
                if (!token.equals("")){
                    Intent intent3 = new Intent();
                    intent3.setClass(getApplicationContext(),HuDong_InPut.class);
                    startActivityForResult(intent3,0);
                }else {
                    Toast.makeText(getApplicationContext(),"请先登录",Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.zan:
                if (!token.equals("")){
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        String url = IP+"lifetime/story/account/likeStory";
                        RequestParams params = new RequestParams();
                        params.addBodyParameter("storyId",interact_story_id);
                        params.addBodyParameter("token",token);
                        HttpUtils httputils = new HttpUtils();
                        httputils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>(){

                            @Override
                            public void onSuccess(ResponseInfo<String> responseInfo) {
                                Log.w("dianzan success",responseInfo.result.toString());
                                try {
                                    JSONObject obj = new JSONObject(responseInfo.result.toString());
                                    String code = obj.getString("code");
                                    if (code.equals("1")){
                                        Toast.makeText(getApplicationContext(),"点赞成功",Toast.LENGTH_SHORT).show();
                                        zan.setImageResource(R.mipmap.zan2);
                                        zan.setClickable(false);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(HttpException e, String s) {
                                Log.w("dianzan failure",s.toString());
                            }
                        });

                    }
                }.start();
                }else {
                Toast.makeText(getApplicationContext(),"请先登录",Toast.LENGTH_SHORT).show();
            }
                break;

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==1){
            final String content = data.getStringExtra("content");
            Log.w("getcontent",content);
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    //评论文章
                    String url = IP+"lifetime/story/account/addComment";
                    RequestParams params = new RequestParams();
                    params.addBodyParameter("storyId",interact_story_id);
                    params.addBodyParameter("token",token);
                    params.addBodyParameter("content",content);
                    HttpUtils httputils = new HttpUtils();
                    httputils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>(){

                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                            Log.w("pinlun success",responseInfo.result.toString());
                            try {
                                JSONObject obj = new JSONObject(responseInfo.result.toString());
                                String code = obj.getString("code");
                                if (code.equals("1")){
                                    new Thread(){
                                        @Override
                                        public void run() {
                                            super.run();
                                            //获取评论
                                            String url3 = IP+"lifetime/story/account/getComments?storyId="+interact_story_id+"&token="+token;
                                            String  result3 =  new NetWorkRequest().getServiceInfo(url3);
                                            Message msg3 = new Message();
                                            msg3.obj = result3;
                                            msg3.what = 6;
                                            handler.sendMessage(msg3);
                                        }
                                    }.start();

                                }
                                Toast.makeText(getApplicationContext(),obj.getString("message"),Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(HttpException e, String s) {
                            Log.w("pinlun failure",s.toString());
                        }
                    });
                }
            }.start();
        }
    }
}
