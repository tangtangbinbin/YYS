package com.example.administrator.yys.hudong;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.example.administrator.yys.R;
import com.example.administrator.yys.utils.AppManager;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

import static com.example.administrator.yys.utils.IPAddress.IP;

/**
 * Created by Administrator on 2017/7/18 0018.
 */

public class HuDong_FenXiang extends Activity implements View.OnClickListener{
    LinearLayout pengyouquan;
    LinearLayout weixinhaoyou;
    LinearLayout qqhaoyou;
    LinearLayout qqkongjian;
    Platform.ShareParams sp;
    LinearLayout top;
    String flag;
    String root;
    String gs_text,gs_url,gs_id;
    String wd_title,wd_content,wd_id;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hudong_fenxiang);
        AppManager.getAppManager().addActivity(this);
        pengyouquan = findViewById(R.id.fenxiang_pengyouquan);
        weixinhaoyou = findViewById(R.id.fenxiang_weixinhaoyou);
        root = getSharedPreferences("user",MODE_PRIVATE).getString("root","");
        qqhaoyou = findViewById(R.id.fenxiang_qqhaoyou);
        top = findViewById(R.id.hudong_fenxiang_lin);
        qqkongjian = findViewById(R.id.fenxiang_qqkongjian);

        Intent intent = getIntent();
        flag = intent.getStringExtra("flag");
        if (flag.equals("gushi")){
            gs_text = intent.getStringExtra("text");
            gs_url = intent.getStringExtra("url");
            gs_id = intent.getStringExtra("interact_story_id");
        }
        if (flag.equals("wenda")){
            wd_title = intent.getStringExtra("title");
            wd_content = intent.getStringExtra("content");
            wd_id = intent.getStringExtra("interact_answer_id");
        }
        pengyouquan.setOnClickListener(this);
        weixinhaoyou.setOnClickListener(this);
        qqhaoyou.setOnClickListener(this);
        qqkongjian.setOnClickListener(this);
        top.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        Log.w("url",IP+"share/account/story?story="+gs_id);
        switch (view.getId()){
            case R.id.hudong_fenxiang_lin:
                finish();
                break;
            case R.id.fenxiang_pengyouquan:
                sp = new Platform.ShareParams();
                sp.setShareType(Platform.SHARE_WEBPAGE);
                if (flag.equals("gushi")){
                    sp.setTitle("有一生");
                    sp.setUrl("http://www.youyisheng.top:8080/lifetime/share/account/story?story="+gs_id); // 标题的超链接
                    sp.setText(gs_text);
                    sp.setImageUrl(root+gs_url);
                }
                if (flag.equals("wenda")){
                    sp.setTitle(wd_title);
                    sp.setUrl("http://www.youyisheng.top:8080/lifetime/share/account/answer?answer="+wd_id); // 标题的超链接
                    sp.setText(wd_content);
                    sp.setImageUrl(root+"/images/systemAvatar.jpg");
                }
                Platform wechatmoments = ShareSDK.getPlatform (WechatMoments.NAME);
                // 设置分享事件回调（注：回调放在不能保证在主线程调用，不可以在里面直接处理UI操作）
                wechatmoments.setPlatformActionListener (new PlatformActionListener() {
                    public void onError(Platform arg0, int arg1, Throwable arg2) {
                        //失败的回调，arg:平台对象，arg1:表示当前的动作，arg2:异常信息
                        Log.w("error info","arg0:"+arg0+"arg1:"+arg1+"arg2"+arg2.toString());
                    }
                    public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
                        //分享成功的回调
                        Log.w("complete info","arg0:"+arg0+"arg1:"+arg1+"arg2"+arg2.toString());
                    }
                    public void onCancel(Platform arg0, int arg1) {
                        //取消分享的回调
                        Log.w("cancel info","arg0:"+arg0+"arg1:"+arg1);
                    }
                });
                // 执行图文分享
                wechatmoments.share(sp);
                break;
            case R.id.fenxiang_weixinhaoyou:
                sp = new Platform.ShareParams();
                sp.setShareType(Platform.SHARE_WEBPAGE);
                if (flag.equals("gushi")){
                    sp.setTitle("有一生");
                    sp.setUrl("http://www.youyisheng.top:8080/lifetime/share/account/story?story="+gs_id); // 标题的超链接
                    sp.setText(gs_text);
                    sp.setImageUrl(root+gs_url);
                }
                if (flag.equals("wenda")){
                    sp.setText(wd_content);
                    sp.setTitle(wd_title);
                    sp.setUrl("http://www.youyisheng.top:8080/lifetime/share/account/answer?answer="+wd_id);
                }
                Platform wechat = ShareSDK.getPlatform (Wechat.NAME);
                // 设置分享事件回调（注：回调放在不能保证在主线程调用，不可以在里面直接处理UI操作）
                wechat.setPlatformActionListener (new PlatformActionListener() {
                    public void onError(Platform arg0, int arg1, Throwable arg2) {
                        //失败的回调，arg:平台对象，arg1:表示当前的动作，arg2:异常信息
                        Log.w("error info","arg0:"+arg0+"arg1:"+arg1+"arg2"+arg2.toString());
                    }
                    public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
                        //分享成功的回调
                        Log.w("complete info","arg0:"+arg0+"arg1:"+arg1+"arg2"+arg2.toString());
                    }
                    public void onCancel(Platform arg0, int arg1) {
                        //取消分享的回调
                        Log.w("cancel info","arg0:"+arg0+"arg1:"+arg1);
                    }
                });
                // 执行图文分享
                wechat.share(sp);
                break;
            case R.id.fenxiang_qqhaoyou:
                OnekeyShare oks = new OnekeyShare();
                if (flag.equals("gushi")){
                    oks.setImageUrl(root+gs_url);
                    oks.setTitleUrl("http://www.youyisheng.top:8080/lifetime/share/account/story?story="+gs_id);
                    oks.setText(gs_text);
                    oks.setTitle("有一生");
                }
                if (flag.equals("wenda")){
                    oks.setTitleUrl("http://www.youyisheng.top:8080/lifetime/share/account/answer?answer="+wd_id);
                    oks.setText(gs_text);
                    oks.setTitle(wd_title);
                }
                oks.setPlatform(QQ.NAME);
                oks.show(getApplicationContext());
                break;
            case R.id.fenxiang_qqkongjian:
                sp = new Platform.ShareParams();
                sp.setShareType(Platform.SHARE_WEBPAGE);
                if (flag.equals("gushi")){
                    sp.setTitle("有一生");
                    sp.setTitleUrl("http://www.youyisheng.top:8080/lifetime/share/account/story?story="+gs_id); // 标题的超链接
                    sp.setText(gs_text);
                    sp.setImageUrl(root+gs_url);
                }
                if (flag.equals("wenda")){
                    sp.setTitle(wd_title);
                    sp.setTitleUrl("http://www.youyisheng.top:8080/lifetime/share/account/answer?answer="+wd_id); // 标题的超链接
                    sp.setText(wd_content);
                    sp.setImageUrl(root+"/images/systemAvatar.jpg");
                }
                sp.setSite("有一生");
                sp.setSiteUrl("http://www.youyisheng.top");
                Platform qzone = ShareSDK.getPlatform (QZone.NAME);
                // 设置分享事件回调（注：回调放在不能保证在主线程调用，不可以在里面直接处理UI操作）
                qzone.setPlatformActionListener (new PlatformActionListener() {
                    public void onError(Platform arg0, int arg1, Throwable arg2) {
                        //失败的回调，arg:平台对象，arg1:表示当前的动作，arg2:异常信息
                        Log.w("error info","arg0:"+arg0+"arg1:"+arg1+"arg2"+arg2.toString());
                    }
                    public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
                        //分享成功的回调
                        Log.w("complete info","arg0:"+arg0+"arg1:"+arg1+"arg2"+arg2.toString());
                    }
                    public void onCancel(Platform arg0, int arg1) {
                        //取消分享的回调
                        Log.w("cancel info","arg0:"+arg0+"arg1:"+arg1);
                    }
                });
                // 执行图文分享
                qzone.share(sp);
                break;
        }
    }
}
