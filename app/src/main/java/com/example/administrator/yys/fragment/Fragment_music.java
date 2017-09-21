package com.example.administrator.yys.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.administrator.yys.R;
import com.example.administrator.yys.network.NetWorkRequest;
import com.example.administrator.yys.other.Other_index;
import com.example.administrator.yys.utils.MyApplication;
import com.example.administrator.yys.utils.MyHandler;
import com.example.administrator.yys.utils.decoder.Bitstream;
import com.example.administrator.yys.utils.decoder.Header;
import com.example.administrator.yys.view.CircularImage;
import com.example.administrator.yys.view.LyricView;
import com.example.administrator.yys.view.MusicList;
import com.example.administrator.yys.view.MyFragment;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import static android.content.Context.MODE_PRIVATE;
import static com.example.administrator.yys.utils.IPAddress.IP;

/**
 * Created by Administrator on 2017/6/13 0013.
 */

public class Fragment_music extends MyFragment {
    private LyricView lyricView;
    private MediaPlayer mediaPlayer;
    private ImageView img;
    private SeekBar seekBar;
    private String mp3Path = "";
    private int INTERVAL=13;//歌词每行的间隔
    private TextView music_title;
    private TextView music_start;
    private TextView music_end,username;
    private CircularImage touxiang_img;
    private  ImageView music_xihuan;
    private ImageView shangchuan;
    private Activity ctx;
    String token;
    SharedPreferences musicinfo;
    SharedPreferences userinfo;
    String musicdir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/YYSMusic";
    String musicName;
    String userId;
    private Button guanzhu;
    PowerManager pm;
    PowerManager.WakeLock wl;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.music,container,false);
        x.Ext.init(getActivity().getApplication());
        pm = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "MyTag");
        musicinfo = getActivity().getSharedPreferences("music",MODE_PRIVATE);
        userinfo = getActivity().getSharedPreferences("user",MODE_PRIVATE);
        music_start = view.findViewById(R.id.music_start);
        music_end = view.findViewById(R.id.music_end);
        lyricView = view.findViewById(R.id.mylrc);
        guanzhu = view.findViewById(R.id.music_guanzhu);
        username = view.findViewById(R.id.music_username);
        music_title = view.findViewById(R.id.music_title) ;
        touxiang_img = view.findViewById(R.id.music_touxiang);
        shangchuan = view.findViewById(R.id.music_shangchuan);
        music_xihuan = view.findViewById(R.id.music_xihuan);
        token = getActivity().getSharedPreferences("user",MODE_PRIVATE).getString("token","");
        mediaPlayer = new MediaPlayer();
        img = view.findViewById(R.id.img_bofang);

        img.setBackgroundResource(R.mipmap.bofang3);
        seekBar = view.findViewById(R.id.seekbarmusic);

        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                ResetMusic(mp3Path);
                lyricView.SetTextSize();
                lyricView.setOffsetY(200);
                mediaPlayer.start();
            }
        });
        getMusicInfo();
        new Thread(new runable()).start();
        Log.w("oncreate","fragment_music");
        return view;
    }

    private int getMusicTime(){
        URL urlfile = null;
        try {
            urlfile = new URL(mp3Path);
            URLConnection con = urlfile.openConnection();
            int b = con.getContentLength();// 得到音乐文件的总长度
            BufferedInputStream bis = new BufferedInputStream(con.getInputStream());
            Bitstream bt = new Bitstream(bis);
            Header h = bt.readFrame();
            int time = (int) h.total_ms(b);
            return  time;
        } catch (Exception e) {
            e.printStackTrace();
        }
       return 0;
    }
    private void initmusic(){
        ResetMusic(mp3Path);
        SerchLrc();
        lyricView.SetTextSize();
        Log.w("getduration",mediaPlayer.getDuration()+"");

        seekBar.setMax(mediaPlayer.getDuration());
        music_end.setText(dateFormat(mediaPlayer.getDuration()));
    }
    //下载音乐
    private void downMusic(String url,String musicname) {
        Log.w("musicurl",url);
        File dir = new File(musicdir);
        if (!dir.exists() || !dir.isDirectory()){
            dir.mkdir();
            Log.w("musicdir:"," dir is created");
        }
        RequestParams requestParams = new RequestParams(url);
        requestParams.setSaveFilePath(musicdir+"/"+musicname+".mp3");
        x.http().get(requestParams,new Callback.ProgressCallback<File>(){

            @Override
            public void onSuccess(File result) {
                File file = new File(musicdir+"/"+musicName+".mp3");
                Log.w("downloadmusic succ",file.getPath());
                if (file.exists()){
                    mp3Path = file.getPath();
                    //initmusic();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.w("downloadmusic fail",ex.toString());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {

            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {

            }
        });

    }

    //下载歌词
    private void downMusiclrc(String url,String musiclrcname) {
        Log.w("musiclrcurl",url);
        File dir = new File(musicdir);
        if (!dir.exists() || !dir.isDirectory()){
            dir.mkdir();
        }

        RequestParams requestParams = new RequestParams(url);
        requestParams.setSaveFilePath(musicdir+"/"+musiclrcname+".lrc");
        x.http().get(requestParams,new Callback.ProgressCallback<File>(){

            @Override
            public void onSuccess(File result) {
                File file = new File(musicdir+"/"+musicName+".lrc");
                Log.w("downloadlrc succ",file.getPath());
                if (file.exists()){
                    //mp3Path = file.getPath();
                    //initmusic();
                    SerchLrc();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.w("downloadlrc fail",ex.toString());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {

            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {

            }
        });


    }


    //获取音乐信息
    private void getMusicInfo(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url;
                if (token==null||token==""){
                    url = IP+"lifetime/music/account/musicOnlineInfo";
                }else {
                    url = IP+"lifetime/music/account/musicOnlineInfo?token="+token;
                }
                String  result =  new NetWorkRequest().getServiceInfo(url);
                Message msg = new Message();
                msg.what=11;
                msg.obj = result;
                if (msg.obj!=null){
                    handler.sendMessage(msg);
                }

            }
        }).start();
    }
    Handler handler = new MyHandler(getActivity()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==2){
                Log.w("concern",msg.obj.toString());
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
            }
            else if (msg.what==11){
            Log.w("musicinfo",msg.obj.toString());
            try {
                JSONObject jsonObject = new JSONObject(msg.obj.toString());

                if (jsonObject.getString("code").equals("1")) {
                    JSONObject jsonroot = jsonObject.getJSONObject("constant");
                    JSONObject jsoninfo = jsonObject.getJSONObject("info");
                    String root = jsonroot.getString("resourceServer");
                    String musrcAddress = jsoninfo.getString("musicAddress");
                    String musicLrcAddress = jsoninfo.getString("musicLrcAddress");
                    String musicId = jsoninfo.getString("musicId");
                    String isLike = jsoninfo.getString("isLike");
                    String userAvatar = jsoninfo.getString("userAvatar");
                    String userName = jsoninfo.getString("userName");
                    userId = jsoninfo.getString("userId");
                    musicName = jsoninfo.getString("musicName");

                    SharedPreferences.Editor editor = musicinfo.edit();
                    editor.putString("musrcAddress", root + musrcAddress);
                    editor.putString("musicLrcAddress", root + musicLrcAddress);
                    editor.putString("musicId", musicId);
                    editor.putString("isLike", isLike);
                    editor.putString("userAvatar", userAvatar);
                    editor.putString("userName", userName);
                    editor.putString("userId", userId);
                    editor.putString("musicName", musicName);
                    editor.commit();


                    if (isLike.equals("true")){
                        music_xihuan.setBackgroundResource(R.mipmap.xihuan2);
                        music_xihuan.setClickable(false);
                    }else {
                        music_xihuan.setBackgroundResource(R.mipmap.xihuan1);
                        music_xihuan.setClickable(true);
                    }
                    music_title.setText(musicName);
                    Glide.with(getActivity()).load(root+userAvatar).into(touxiang_img);
                    username.setText(userName);
                    isconcern();


                    File file = new File(musicdir+"/"+musicName+".mp3");
                    Log.w("file path",file.getPath());
                    if (file.exists()){
                        mp3Path = file.getPath();
                        initmusic();
                    }else {
                        //下载歌曲
                        downMusic(root + musrcAddress, musicName);
                        mp3Path = root+musrcAddress;
                        initmusic();
                        Log.w("mp3path1",mp3Path);
                    }

                    if (musicLrcAddress==null||musicLrcAddress.equals("null")){
                        Toast.makeText(getActivity(),"没找到歌词",Toast.LENGTH_SHORT).show();
                    }else {
                        File file1 = new File(musicdir+"/"+musicName+".lrc");
                        if (!file1.exists()){
                            //下载歌词
                            //img.setClickable(false);
                            downMusiclrc(root + musicLrcAddress, musicName);
                        }

                    }


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            }
        }
    };

    private void isconcern() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                //是否关注
                if (!token.equals("")){
                    String url = IP+"lifetime/concern/account/isConcern?concernId="+userId+"&token="+token;
                    String  result1 =  new NetWorkRequest().getServiceInfo(url);
                    Message msg = new Message();
                    msg.obj = result1;
                    msg.what = 2;
                    handler.sendMessage(msg);
                }
            }
        }.start();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final Handler handler1 = new MyHandler(getActivity()){
            @Override
            public void handleMessage(Message msg) {
                Log.w("msg info",msg.obj.toString());
                super.handleMessage(msg);
                if (msg.what==1){
                    Toast.makeText(getActivity(),"请先登录",Toast.LENGTH_SHORT).show();
                }
                if (msg.what==2){
                    try {
                        JSONObject jsonObject = new JSONObject(msg.obj.toString());
                        if (jsonObject.getString("code").equals("1")){
                            music_xihuan.setBackgroundResource(R.mipmap.xihuan2);
                            music_xihuan.setClickable(false);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (msg.what==3){
                    try {
                        JSONObject jsonObject = new JSONObject(msg.obj.toString());
                        if (jsonObject.getString("code").equals("1")){
                            guanzhu.setText("已关注");
                            guanzhu.setClickable(false);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


            }
        };


        touxiang_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!token.equals("")){
                    img.setBackgroundResource(R.mipmap.bofang3);
                    MyApplication.getMyApplicationInstance().setMusicispause(1);
                    MyApplication.getMyApplicationInstance().setMusiccurrent(mediaPlayer.getCurrentPosition());
                    if (mediaPlayer.isPlaying()){
                        mediaPlayer.pause();
                    }
                    Intent intent = new Intent();
                    intent.putExtra("user_id",userId);
                    intent.setClass(getActivity(),Other_index.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(getActivity(),"请先登录",Toast.LENGTH_SHORT).show();
                }

            }
        });

        guanzhu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!token.equals("")){
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            String url1 = IP+"lifetime/concern/account/addConcern?concernId="+userId+"&token="+token;
                            String  result =  new NetWorkRequest().getServiceInfo(url1);
                            Message msg1 = new Message();
                            msg1.obj = result;
                            msg1.what = 3;
                            handler1.sendMessage(msg1);
                        }
                    }.start();
                }else {
                    Toast.makeText(getActivity(),"请先登录",Toast.LENGTH_SHORT).show();
                }
            }
        });

        music_xihuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String token = userinfo.getString("token","");
                        if(token.equals("")){
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Message msg = new Message();
                                    msg.what = 1;
                                    msg.obj=" ";
                                    handler1.sendMessage(msg);
                                }
                            }).start();

                        }else{

                            String musicId = musicinfo.getString("musicId","");
                            String url = IP+"lifetime/music/account/likeMusic?token="+token+"&musicId="+musicId;
                            Log.w("xihuanurl",url);
                            String  result =  new NetWorkRequest().getServiceInfo(url);
                            Message msg = new Message();
                            msg.obj = result;
                            msg.what=2;
                            handler1.sendMessage(msg);
                            Log.w("xihuanresult",result);
                        }

                    }
                }).start();

            }


        });

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()){
                    img.setBackgroundResource(R.mipmap.bofang3);
                    mediaPlayer.pause();
                    wl.release();//释放电源管理
                }else{
                    wl.acquire();//保持屏幕常量
                    if (MyApplication.getMyApplicationInstance().getMusicispause()==1){
                        try {
                            mediaPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        MyApplication.getMyApplicationInstance().setMusicispause(0);
                        mediaPlayer.seekTo(MyApplication.getMyApplicationInstance().getMusiccurrent());
                        Log.w("ispause",MyApplication.getMyApplicationInstance().getMusiccurrent()+"");
                    }
                    img.setBackgroundResource(R.mipmap.bofang4);
                    mediaPlayer.start();
                    lyricView.setOffsetY(220 - lyricView.SelectIndex(mediaPlayer.getCurrentPosition())
                            * (lyricView.getSIZEWORD() + INTERVAL-1));
                }

            }
        });



        shangchuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String token = userinfo.getString("token","");
                if(token.equals("")){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message msg = new Message();
                            msg.obj = " ";
                            msg.what = 1;
                            handler1.sendMessage(msg);
                        }
                    }).start();
                }else{
                    if (mediaPlayer.isPlaying()){
                        mediaPlayer.pause();
                    }
                    MyApplication.getMyApplicationInstance().setMusicispause(1);
                    MyApplication.getMyApplicationInstance().setMusiccurrent(mediaPlayer.getCurrentPosition());
                    img.setBackgroundResource(R.mipmap.bofang3);
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), MusicList.class);
                    startActivityForResult(intent,0);
                }


            }
        });


    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        //当music页面不可见时关闭播放器
        if (hidden){
            if (mediaPlayer.isPlaying()){
                mediaPlayer.pause();
                img.setBackgroundResource(R.mipmap.bofang3);
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1){
                String path = data.getStringExtra("url");
                Log.w("uri",path);
                if(!path.substring(path.length()-4,path.length()).equals(".mp3")){
                    Toast.makeText(getActivity(),"请选择mp3格式文件",Toast.LENGTH_SHORT).show();
                    Log.w("the type",path.substring(path.length()-4,path.length()));
                    return;
                }

                //上传文件
                String url = IP+"lifetime/upload/account/musicUpload";
                com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
                params.addBodyParameter("music",new File(path));
                params.addBodyParameter("token",token);
                HttpUtils httputils = new HttpUtils();
                httputils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        try {
                            JSONObject obj = new JSONObject(responseInfo.result.toLowerCase());
                            Toast.makeText(getActivity(),obj.getString("message"),Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.w("success",responseInfo.result.toString());
                    }

                    @Override
                    public void onFailure(com.lidroid.xutils.exception.HttpException e, String s) {
                        Log.w("failure",s.toString());
                    }

                });

        }
    }

    private String getPath(Uri uri) {
        String[] projection = {MediaStore.Audio.Media.DATA};
        Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    public void SerchLrc() {
        String lrc = mp3Path;
        //lrc = lrc.substring(0, lrc.length() - 4).trim() + ".lrc".trim();
        lrc = musicdir+"/"+musicName+".lrc";
        LyricView.read(lrc);
        lyricView.SetTextSize();
        lyricView.setOffsetY(200);
    }

    public void ResetMusic(String path) {

        mediaPlayer.reset();
        try {

            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    class runable implements Runnable {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            while (true) {

                try {
                    Thread.sleep(100);
                    if (mediaPlayer.isPlaying()) {
                        lyricView.setOffsetY(lyricView.getOffsetY() - lyricView.SpeedLrc());
                        lyricView.SelectIndex(mediaPlayer.getCurrentPosition());
                        seekBar.setProgress(mediaPlayer.getCurrentPosition());
                        Message msg = new Message();
                        msg.obj = dateFormat(mediaPlayer.getCurrentPosition());
                        myhandler.sendMessage(msg);
                        mHandler.post(mUpdateResults);
                    }
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    private String dateFormat(int ms){
        int f = ms/(60*1000);
        int m = (ms-(f*60*1000))/1000;
        if (m<10){
            return f+":"+"0"+m;
        }else {
            return f+":"+m;
        }

    }
    Handler mHandler = new MyHandler(getActivity());
    Runnable mUpdateResults = new Runnable() {
        public void run() {
            lyricView.invalidate(); // 更新视图
        }
    };


    Handler myhandler = new MyHandler(getActivity()){
        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            music_start.setText(msg.obj.toString());//显示歌词的播放时间
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
    }

    @Override
    public void onStop() {
        super.onStop();
        mediaPlayer.stop();
    }

}
