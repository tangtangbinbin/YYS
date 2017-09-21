package com.example.administrator.yys.kongjian;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.administrator.yys.R;
import com.example.administrator.yys.network.NetWorkRequest;
import com.example.administrator.yys.other.Other_index;
import com.example.administrator.yys.utils.AppManager;
import com.example.administrator.yys.utils.CheckJsonType;
import com.example.administrator.yys.utils.MediaManager;
import com.example.administrator.yys.utils.MyHandler;
import com.example.administrator.yys.utils.ParseJson;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.administrator.yys.utils.IPAddress.IP;

/**
 * Created by Administrator on 2017/6/20 0020.
 */

public class KongJian_Info extends Activity implements View.OnClickListener{
    ListView lv;
    ImageView touxiang;
    TextView name;
    TextView desc;
    LinearLayout yaoqing,yaoqing2;
    LinearLayout xiangce, xiangce2;
    TextView edit,edit2;
    InputMethodManager imm ;
    LinearLayout fanhui;
    TextView shezhi;
    String touxiangstr;
    String namestr;
    String descstr;
    String defaultid;
    ImageView fabiao_img,fabiaoluyin_img;
    SharedPreferences userinfo;
    String groupId;
    String token,root;
    String myuser_id;
    String password;
    String update_time;
    int chaxunid = 0;
    int timestatus = 0;
    int mediastatus = 0;
    MediaManager mediaManager;
    int temptime = 0;
    int activitystatus = 0;
    int amritem  = -1;
    int ischange = 0;
    int page = 1;
    int itemnumber = 0;
    LinearLayout stick_nav;
    Intent callbackintent;
    ArrayList<Map<String,String>> list = new ArrayList();
    View header,nav;
    String login_name;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kongjian_info2);
        x.Ext.init(getApplication());
        header = View.inflate(this,R.layout.kongjian_listview_header,null);
        nav = View.inflate(this,R.layout.kongjian_listview_action,null);
        edit2 = nav.findViewById(R.id.kj_info_edit_nav);
        yaoqing2 = nav.findViewById(R.id.kj_info_yq_lin_nav);
        xiangce2 = nav.findViewById(R.id.kj_info_xc_lin_nav);
        mediaManager = new MediaManager();
        AppManager.getAppManager().addActivity(this);
        lv =  findViewById(R.id.kj_gushi_listview);
        login_name = getSharedPreferences("user",MODE_PRIVATE).getString("login_name","");
        stick_nav = findViewById(R.id.stick_nav);
        touxiang = header.findViewById(R.id.kj_info_touxiang1);
        name = header.findViewById(R.id.kj_info_name1);
        fanhui =  findViewById(R.id.kongjian_info_fanhui);
        myuser_id = getSharedPreferences("user",MODE_PRIVATE).getString("user_id","");
        fabiao_img = findViewById(R.id.kj_info_fabiao_img);
        fabiaoluyin_img = findViewById(R.id.kj_info_fabiaoluyin_img);
        shezhi = (TextView) findViewById(R.id.kongjian_shezhi);
        yaoqing =  findViewById(R.id.kj_info_yq_lin2);
        xiangce = findViewById(R.id.kj_info_xc_lin);
        imm = (InputMethodManager) getApplication().getSystemService(Context.INPUT_METHOD_SERVICE);
        desc = header.findViewById(R.id.kj_info_desc1);
        edit =  findViewById(R.id.kj_info_edit);
        edit.setOnClickListener(this);
        fabiaoluyin_img.setOnClickListener(this);
        fanhui.setOnClickListener(this);
        shezhi.setOnClickListener(this);
        edit2.setOnClickListener(this);
        yaoqing2.setOnClickListener(this);
        xiangce2.setOnClickListener(this);
        fabiao_img.setOnClickListener(this);
        xiangce.setOnClickListener(this);
        yaoqing.setOnClickListener(this);

        userinfo = getSharedPreferences("user",MODE_PRIVATE);
        token = userinfo.getString("token","");
        root = userinfo.getString("root","");

        lv.addHeaderView(header);
        lv.addHeaderView(nav);
        lv.setAdapter(new MyAdapter(getApplicationContext()));
        callbackintent = getIntent();
        touxiangstr = callbackintent.getStringExtra("group_avatar");
        namestr = callbackintent.getStringExtra("group_name");
        update_time = callbackintent.getStringExtra("update_time");
        descstr = callbackintent.getStringExtra("group_describe");
        defaultid = callbackintent.getStringExtra("group_default_id");
        groupId = callbackintent.getStringExtra("group_id");
        password = callbackintent.getStringExtra("password");

        Glide.with(getApplicationContext()).load(touxiangstr).into(touxiang);
        name.setText(namestr);
        desc.setText(descstr);



        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if (i>=1){
                    stick_nav.setVisibility(View.VISIBLE);
                }else {
                    stick_nav.setVisibility(View.GONE);
                }
            }
        });
        //init();
        checkOverdue();
        if (defaultid.equals("1")){
            yaoqing.setVisibility(View.GONE);
            yaoqing2.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case 1:
                        Log.w("创建空间","创建空间");
                        String url = IP+"lifetime/group/account/addGroup";
                        RequestParams params = new RequestParams();
                        params.addBodyParameter("token",token);
                        params.addBodyParameter("groupDefaultId",defaultid);
                        HttpUtils httputils = new HttpUtils();
                        httputils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
                            @Override
                            public void onSuccess(ResponseInfo<String> responseInfo) {
                                Log.w("addgroupsuccess:",responseInfo.result.toString());
                                try {
                                    JSONObject obj = new JSONObject(responseInfo.result.toString());
                                    String code = obj.getString("code");
                                    if (code.equals("1")){
                                        Intent intent = new Intent();
                                        intent.putExtra("index","kongjian");
                                        intent.setAction("android.intent.action.UPDATE_FRAGMENT");
                                        sendBroadcast(intent);
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                String groupId1 = getGroupId();
                                                if (!groupId1.equals("")){
                                                    groupId = groupId1;
                                                    callbackintent.putExtra("group_id",groupId);
                                                   Message msg = new Message();
                                                    msg.what=2;
                                                    handler.sendMessage(msg);
                                                }
                                            }
                                        }).start();

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            @Override
                            public void onFailure(HttpException e, String s) {

                                Log.w("addgroupfaile",s.toString());
                            }
                        });
                break;
            case 2:
                KongJian_Info.this.finish();
                break;
            case 3:
                //lin2.setVisibility(View.GONE);
                final String content = data.getStringExtra("content");
                edit.setText(content);
                edit2.setText(content);
                if (content.length()>0){
                    list.clear();
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            String url = IP+"lifetime/group/account/selArticles?token="+token+"&groupId="+groupId+"&date="+content+"&password="+password;
                            String  result =  new NetWorkRequest().getServiceInfo(url);
                            Message msg = new Message();
                            msg.obj = result;
                            msg.what = 3;
                            handler.sendMessage(msg);
                        }
                    }.start();
                }else {
                    init();
                }


                break;
            case 4:
                touxiangstr = data.getStringExtra("group_avatar");
                namestr = data.getStringExtra("group_name");
                update_time = data.getStringExtra("update_time");
                descstr = data.getStringExtra("group_describe");
                callbackintent.putExtra("group_avatar",touxiangstr);
                callbackintent.putExtra("group_name",namestr);
                callbackintent.putExtra("update_time",update_time);
                callbackintent.putExtra("group_describe",descstr);
                Glide.with(getApplicationContext()).load(touxiangstr).into(touxiang);
                name.setText(namestr);
                desc.setText(descstr);
                break;
            case 5:
                init();
                break;
        }
    }


    private String getGroupId() {

        String url = IP+"lifetime/group/account/getGroups?token="+token;
        String  result =  new NetWorkRequest().getServiceInfo(url);
        if (result!=null){
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(result.toString());
                String code = jsonObject.getString("code");
                if (code.equals("1")){
                    JSONObject datum = jsonObject.getJSONObject("datum");
                    for (int i=1;i<=datum.length();i++){
                        if (new CheckJsonType().checkJsonType(datum.toString(),i).equals("array") ){
                            JSONArray array = datum.getJSONArray(i+"");
                            for (int j=0;j<array.length();j++){
                                JSONObject num = array.getJSONObject(j);
                                String group_default_id = num.getString("group_default_id");
                                if (group_default_id.equals(defaultid)){
                                    String group_id = num.getString("group_id");
                                    Log.w("getgroupid","获取到了groupid"+group_id);
                                    return group_id;
                                }
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return "";
    }

    Handler handler = new MyHandler(this){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.obj!=null && msg.what==1){
                Log.w("getArticles:",msg.obj.toString());
                loadContent(msg);
            }
            if (msg.what==2){
                Toast.makeText(getApplicationContext(),"获取成功",Toast.LENGTH_SHORT).show();
            }
            if (msg.what==3){
                Log.w("getarticls from date",msg.obj.toString());
                chaxunid = 1;
                loadContent(msg);
            }
            if (msg.what==4){
                Log.w("chengyuan",msg.obj.toString());
                try {
                    JSONObject obj = new JSONObject(msg.obj.toString());
                    JSONArray datum = obj.getJSONArray("datum");
                    if (datum.length()>=2){
                        yaoqing.setVisibility(View.GONE);
                        yaoqing2.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (msg.what==5){
                dialog();
            }
            if (msg.what==6&&msg.obj!=null){
                Log.w("msg.obj",msg.obj.toString());
                try {
                    JSONObject obj = new JSONObject(msg.obj.toString());
                    String code = obj.getString("code");
                    if (code.equals("1")){
                        JSONObject datum = obj.getJSONObject("datum");
                        if (datum!=null){
                            String vipendtime = datum.getString("expired_time");
                            Date date = null;
                            try {
                                date = new SimpleDateFormat("yyyy-MM-dd").parse(vipendtime);
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(date);
                                Log.w("aa",calendar.getTimeInMillis()-System.currentTimeMillis()+"   "+3*24*60*60*1000);
                                if(calendar.getTimeInMillis()-System.currentTimeMillis()<(3*24*60*60*1000)){
                                    String isoverdue = getSharedPreferences("user",MODE_PRIVATE).getString(login_name+groupId+"overdue","");
                                    if (!isoverdue.equals("true")){
                                        dialog2();//提示空间到期
                                    }
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    };

    private void loadContent(Message msg){
        try {
            JSONObject obj = new JSONObject(msg.obj.toString());
            String code = obj.getString("code");
            if (code.equals("1")){
                JSONObject datum = obj.getJSONObject("datum");
                JSONArray arr = datum.getJSONArray("rows");
                if (arr.length()==0 && chaxunid==1){
                    Toast.makeText(getApplicationContext(),"没有该时间的文章",Toast.LENGTH_SHORT).show();
                }else{
                    if (page==1){
                         list.clear();
                    }
                    for (int i=0;i<arr.length();i++){
                        JSONObject art = arr.getJSONObject(i);
                        String article_type = art.getString("article_type");
                        if (article_type.equals("1")){
                            Map<String,String> map = new HashMap<>();
                            map.put("create_time",art.getString("create_time").substring(0,10));
                            map.put("user_id",art.getString("user_id"));
                            String content = art.getString("content");
                            map.put("content",content);
                            map.put("article_type",art.getString("article_type"));
                            map.put("avatar",art.getString("avatar"));
                            map.put("group_id",art.getString("group_id"));
                            map.put("sound_time",art.getString("sound_time"));
                            map.put("user_id",art.getString("user_id"));
                            map.put("group_article_id",art.getString("group_article_id"));

                            list.add(map);
                        }else {
                            Map<String,String> map = new HashMap<>();
                            map.put("create_time",art.getString("create_time").substring(0,10));
                            map.put("user_id",art.getString("user_id"));
                            String content = art.getString("content");
                            map.put("content",content);
                            map.put("group_id",art.getString("group_id"));
                            map.put("article_type",art.getString("article_type"));
                            map.put("avatar",art.getString("avatar"));
                            map.put("user_id",art.getString("user_id"));
                            map.put("group_article_id",art.getString("group_article_id"));
                            String text = new ParseJson().gettext(content,"groups").replaceAll("%5Cn","\n");
                            String url = new ParseJson().geturl(content,"groups");
                            if (url.equals("")){
                                map.put("url",art.getString("thumbnail_url"));
                            }else{
                                map.put("url",url);
                            }
                            map.put("text",text);

                            list.add(map);
                        }

                    }
                    if (arr.length()>0) {
                        MyAdapter adapter = new MyAdapter(getApplicationContext());
                        lv.setAdapter(adapter);
                        lv.setSelection(itemnumber);
                        lv.setDivider(null);
                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                if (i <= 1) {
                                    return;
                                }
                                if (!list.get(i - 2).get("article_type").toString().equals("1")) {
                                    Intent intent = new Intent();
                                    Log.w("查看userid", list.get(i - 2).get("user_id").toString() + " ---- " + myuser_id);
                                    if (defaultid.equals("1") || list.get(i - 2).get("user_id").toString().equals(myuser_id)) {
                                        intent.setClass(KongJian_Info.this, KongJian_GuShi_GeRen.class);
                                    } else {
                                        intent.setClass(KongJian_Info.this, KongJian_GuShi_JAY.class);
                                    }
                                    intent.putExtra("content", list.get(i - 2).get("content").toString());
                                    intent.putExtra("group_id", list.get(i - 2).get("group_id").toString());
                                    intent.putExtra("user_id", list.get(i - 2).get("user_id").toString());
                                    intent.putExtra("group_article_id", list.get(i - 2).get("group_article_id").toString());
                                    intent.putExtra("callbackintent", callbackintent);
                                    startActivity(intent);
                                }
                            }
                        });
                    }

                }
                }else if (code.equals("0")){
                Toast.makeText(getApplicationContext(),"日期格式输入错误",Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*@Override
    public void onRefresh() {
        itemnumber = 0;
        swipe.setRefreshing(true);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipe.setRefreshing(false);
                list.clear();
                init();
            }
        },3000);
    }*/

    private class MyAdapter extends BaseAdapter{

        private LayoutInflater mInflater;//得到一个LayoutInfalter对象用来导入布局
        public MyAdapter(Context context) {
            Log.w("myadapter","construct");
            this.mInflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
             final ViewHolder holder =new ViewHolder();
            view = null;
            String article_type = list.get(i).get("article_type");
            if (article_type.equals("1")){


                final Handler handler = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        holder.time.setText(msg.what+" S");
                        holder.time.setTextColor(Color.parseColor("#4f4f4f"));
                    }
                };

                 class MyTimeTask extends TimerTask{
                     int time = Integer.parseInt(list.get(i).get("sound_time").toString())+1;
                     @Override
                     public void run() {
                         Log.w("timetask","timekask run");
                         if(temptime!=0&&ischange==0){
                             time = temptime;
                             Log.w("timetask","temptime");
                         }
                         temptime = --time;
                         Log.w("timetask","status"+activitystatus);
                         if (activitystatus==1)
                             cancel();
                         Message msg = new Message();
                         msg.what = time;
                         handler.sendMessage(msg);
                         Log.w("timetask","sendmessage");
                         if (time==0){
                             cancel();
                             timestatus=0;
                         }
                     }
                 }
                    if(defaultid.equals("1")){
                        //个人-录音
                        view = mInflater.inflate(R.layout.kongjian_luyin_geren_item,null);
                        holder.date = view.findViewById(R.id.luyin_item_time);
                        holder.time = view.findViewById(R.id.luyin_time);
                        holder.item_lin = view.findViewById(R.id.luyin_item_lin);
                        view.setTag(holder);
                        holder.date.setText(list.get(i).get("create_time").toString());
                        holder.date.setTextColor(Color.parseColor("#4f4f4f"));
                        holder.time.setText(list.get(i).get("sound_time").toString()+" S");
                        holder.time.setTextColor(Color.parseColor("#4f4f4f"));
                    }else if (defaultid.equals("3")){
                        if (list.get(i).get("user_id").toString().equals(myuser_id)){
                            //爱情-我的录音
                            view = mInflater.inflate(R.layout.kongjian_luyin_jiaren_item,null);
                            holder.date = view.findViewById(R.id.luyin_item_time);
                            holder.time = view.findViewById(R.id.luyin_time);
                            holder.touxiang = view.findViewById(R.id.info_touxiang);
                            holder.item_lin = view.findViewById(R.id.luyin_item_lin);
                            view.setTag(holder);
                            holder.date.setText(list.get(i).get("create_time").toString());
                            holder.date.setTextColor(Color.parseColor("#4f4f4f"));
                            holder.time.setText(list.get(i).get("sound_time").toString()+" S");
                            holder.time.setTextColor(Color.parseColor("#4f4f4f"));
                            Glide.with(getApplicationContext()).load(root+list.get(i).get("avatar").toString()).into(holder.touxiang);
                            holder.touxiang.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent();
                                    intent.setClass(KongJian_Info.this, Other_index.class);
                                    intent.putExtra("user_id",list.get(i).get("user_id"));
                                    startActivity(intent);
                                }
                            });
                        }else {
                            //爱情-他的录音
                            view = mInflater.inflate(R.layout.kongjian_luyin_aiqing_item,null);
                            holder.date = view.findViewById(R.id.luyin_item_time);
                            holder.time = view.findViewById(R.id.luyin_time);
                            holder.touxiang = view.findViewById(R.id.info_touxiang);
                            holder.item_lin = view.findViewById(R.id.luyin_item_lin);
                            view.setTag(holder);
                            holder.date.setText(list.get(i).get("create_time").toString());
                            holder.time.setText(list.get(i).get("sound_time").toString()+" S");
                            holder.time.setTextColor(Color.parseColor("#4f4f4f"));
                            holder.date.setTextColor(Color.parseColor("#4f4f4f"));
                            Glide.with(getApplicationContext()).load(root+list.get(i).get("avatar").toString()).into(holder.touxiang);
                            holder.touxiang.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent();
                                    intent.setClass(KongJian_Info.this, Other_index.class);
                                    intent.putExtra("user_id",list.get(i).get("user_id"));
                                    startActivity(intent);
                                }
                            });
                        }
                    }else {
                        //家人、友情-录音
                        view = mInflater.inflate(R.layout.kongjian_luyin_jiaren_item,null);
                        holder.date = view.findViewById(R.id.luyin_item_time);
                        holder.time = view.findViewById(R.id.luyin_time);
                        holder.touxiang = view.findViewById(R.id.info_touxiang);
                        holder.item_lin = view.findViewById(R.id.luyin_item_lin);
                        view.setTag(holder);
                        holder.date.setText(list.get(i).get("create_time").toString());
                        holder.time.setText(list.get(i).get("sound_time").toString()+" S");
                        holder.time.setTextColor(Color.parseColor("#4f4f4f"));
                        holder.date.setTextColor(Color.parseColor("#4f4f4f"));
                        Glide.with(getApplicationContext()).load(root+list.get(i).get("avatar").toString()).into(holder.touxiang);
                        holder.touxiang.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent();
                                intent.setClass(KongJian_Info.this, Other_index.class);
                                intent.putExtra("user_id",list.get(i).get("user_id"));
                                startActivity(intent);
                            }
                        });
                    }


                //final TimerTask task = new MyTimeTask();

                holder.item_lin.setOnClickListener(new View.OnClickListener() {
                    Timer timer = new Timer();
                    String path = root+list.get(i).get("content").toString();
                   /* String filename = new ParseJson().getname(list.get(i).get("content").toString());
                    File file = new File(Environment.getExternalStorageDirectory()+"/youyisheng/"+filename);*/
                    @Override
                    public void onClick(View view) {
                        /*if (!file.exists()){
                            Log.w("the file path",file.getPath());
                            new Thread(){
                                @Override
                                public void run() {
                                    super.run();
                                    new DownLoadFile().download(path,filename);
                                }
                            }.start();

                        }*/
                        if (amritem!=-1&&amritem!=i){
                            timestatus=0;
                            mediastatus=0;
                            ischange =1;
                            mediaManager.pause();
                            timer.cancel();
                        }else {
                            ischange = 0;
                        }
                        if(timestatus==0){
                            amritem = i;
                            timestatus=1;
                            if (mediastatus==0){
                                mediaManager.playSound(path, new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mediaPlayer) {
                                        holder.time.setText(list.get(i).get("sound_time").toString()+" S");
                                        mediastatus = 0;
                                    }
                                }, new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mediaPlayer) {
                                        timer = new Timer();
                                        Log.w("aaa","ccc");
                                        timer.scheduleAtFixedRate(new MyTimeTask(),0,1000);
                                    }

                                });
                                mediastatus = 1;
                            }
                            if (mediastatus==2){
                                mediaManager.resume();
                                timer = new Timer();
                                timer.scheduleAtFixedRate(new MyTimeTask(),0,1000);
                            }
                        }else if (timestatus==1){
                            timer.cancel();
                            timestatus=0;
                            mediaManager.pause();
                            mediastatus=2;
                        }
                    }
                });

            }else {
                if (defaultid.equals("1")){
                    view = mInflater.inflate(R.layout.kongjian_gushi_geren_list_item,null);
                    holder.title =  view.findViewById(R.id.kj_gushi_time);
                    holder.info =  view.findViewById(R.id.kj_gushi_info);
                    holder.img =  view.findViewById(R.id.kj_gushi_img);
                    view.setTag(holder);
                    //x.image().bind(holder.img,getSharedPreferences("user",MODE_PRIVATE).getString("root","")+list.get(i).get("url").toString());
                    Glide.with(getApplicationContext()).load(getSharedPreferences("user",MODE_PRIVATE).getString("root","")+list.get(i).get("url").toString()).into(holder.img);
                    holder.title.setText(list.get(i).get("create_time").toString());
                    holder.title.setTextColor(Color.parseColor("#4f4f4f"));
                    holder.info.setTextColor(Color.parseColor("#4f4f4f"));
                    holder.info.setText(list.get(i).get("text").toString());
                }else if (defaultid.equals("3")){
                    if (list.get(i).get("user_id").toString().equals(myuser_id)){
                        view = mInflater.inflate(R.layout.kongjian_gushi_jiaren_list_item,null);
                        holder.title =  view.findViewById(R.id.kj_gushi_time);
                        holder.info =  view.findViewById(R.id.kj_gushi_info);
                        holder.img =  view.findViewById(R.id.kj_gushi_img);
                        holder.touxiang = view.findViewById(R.id.kongjian_gushi_avatar);
                        view.setTag(holder);
                       // x.image().bind(holder.img,getSharedPreferences("user",MODE_PRIVATE).getString("root","")+list.get(i).get("url").toString());
                        Glide.with(getApplicationContext()).load(getSharedPreferences("user",MODE_PRIVATE).getString("root","")+list.get(i).get("url").toString()).into(holder.img);
                        holder.title.setText(list.get(i).get("create_time").toString());
                        holder.info.setText(list.get(i).get("text").toString());
                        holder.title.setTextColor(Color.parseColor("#4f4f4f"));
                        holder.info.setTextColor(Color.parseColor("#4f4f4f"));
                        Glide.with(getApplicationContext()).load(root+list.get(i).get("avatar").toString()).into(holder.touxiang);
                        holder.touxiang.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent();
                                intent.setClass(KongJian_Info.this, Other_index.class);
                                intent.putExtra("user_id",list.get(i).get("user_id"));
                                startActivity(intent);
                            }
                        });
                    }else {
                        view = mInflater.inflate(R.layout.kongjian_gushi_aiqing_list_item,null);
                        holder.title =  view.findViewById(R.id.kj_gushi_time);
                        holder.info =  view.findViewById(R.id.kj_gushi_info);
                        holder.img =  view.findViewById(R.id.kj_gushi_img);
                        holder.touxiang = view.findViewById(R.id.kongjian_gushi_avatar);
                        view.setTag(holder);
                        //x.image().bind(holder.img,getSharedPreferences("user",MODE_PRIVATE).getString("root","")+list.get(i).get("url").toString());
                        Glide.with(getApplicationContext()).load(getSharedPreferences("user",MODE_PRIVATE).getString("root","")+list.get(i).get("url").toString()).into(holder.img);
                        holder.title.setText(list.get(i).get("create_time").toString());
                        holder.info.setText(list.get(i).get("text").toString());
                        holder.title.setTextColor(Color.parseColor("#4f4f4f"));
                        holder.info.setTextColor(Color.parseColor("#4f4f4f"));
                        Glide.with(getApplicationContext()).load(root+list.get(i).get("avatar").toString()).into(holder.touxiang);
                        holder.touxiang.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent();
                                intent.setClass(KongJian_Info.this, Other_index.class);
                                intent.putExtra("user_id",list.get(i).get("user_id"));
                                startActivity(intent);
                            }
                        });
                    }
                }else {
                    view = mInflater.inflate(R.layout.kongjian_gushi_jiaren_list_item,null);
                    holder.title =  view.findViewById(R.id.kj_gushi_time);
                    holder.info =  view.findViewById(R.id.kj_gushi_info);
                    holder.img =  view.findViewById(R.id.kj_gushi_img);
                    holder.touxiang = view.findViewById(R.id.kongjian_gushi_avatar);
                    view.setTag(holder);
                    //x.image().bind(holder.img,getSharedPreferences("user",MODE_PRIVATE).getString("root","")+list.get(i).get("url").toString());
                    Glide.with(getApplicationContext()).load(getSharedPreferences("user",MODE_PRIVATE).getString("root","")+list.get(i).get("url").toString()).into(holder.img);
                    holder.title.setText(list.get(i).get("create_time").toString());
                    holder.info.setText(list.get(i).get("text").toString());
                    holder.title.setTextColor(Color.parseColor("#4f4f4f"));
                    holder.info.setTextColor(Color.parseColor("#4f4f4f"));
                    Glide.with(getApplicationContext()).load(root+list.get(i).get("avatar").toString()).into(holder.touxiang);
                    holder.touxiang.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent();
                            intent.setClass(KongJian_Info.this, Other_index.class);
                            intent.putExtra("user_id",list.get(i).get("user_id"));
                            startActivity(intent);
                        }
                    });
                }

            }

            if (i==list.size()-1&&(i+1)%15==0){
                loadmessage();
                itemnumber = i;
            }
            return view;
        }
    }

    private void loadmessage() {
        page++;
        new Thread(){
            @Override
            public void run() {
                super.run();
                //String url = IP+"lifetime/group/account/getArticles?token="+token+"&groupId="+groupId+"&rows=15&page="+page;
                String url;
                if (password.equals("")){
                    url = IP+"lifetime/group/account/getArticles?token="+token+"&groupId="+groupId+"&rows=15&page="+page;
                }else{
                    url = IP+"lifetime/group/account/getArticles?token="+token+"&groupId="+groupId+"&password="+password+"&rows=15&page="+page;
                }
                String  result =  new NetWorkRequest().getServiceInfo(url);
                Message msg2 = new Message();
                msg2.obj = result;
                msg2.what =1;
                handler.sendMessage(msg2);
            }
        }.start();
    }
    public final class ViewHolder{
        public TextView title;
        public TextView info;
        public ImageView img;
        public CircularImage touxiang;

        public TextView date,time;
        public LinearLayout item_lin;
    }

    private void checkOverdue(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                String url2 = IP+"lifetime/groupGrade/account/getGroupGrade?token="+token+"&groupId="+groupId;
                String  result2 =  new NetWorkRequest().getServiceInfo(url2);
                Message msg2 = new Message();
                msg2.obj = result2;
                msg2.what = 6;
                handler.sendMessage(msg2);
            }
        }.start();
    }
    private void init() {
        new Thread(new Runnable() {
            @Override
          public void run() {
                if (!groupId.equals("")){
                    String url;
                    if (password.equals("")){
                       url = IP+"lifetime/group/account/getArticles?token="+token+"&groupId="+groupId+"&rows=15&page=1";
                    }else{
                        url = IP+"lifetime/group/account/getArticles?token="+token+"&groupId="+groupId+"&password="+password+"&rows=15&page=1";
                    }

                    String  result =  new NetWorkRequest().getServiceInfo(url);
                    Message msg = new Message();
                    msg.obj = result;
                    msg.what = 1;
                    handler.sendMessage(msg);
                }else {
                   /* Intent intent = new Intent();
                    intent.setClass(KongJian_Info.this, InitSpace.class);
                    startActivityForResult(intent,0);*/
                    Message msg = new Message();
                    msg.what = 5;
                    handler.sendMessage(msg);
                    Log.w("send 5","1");
                }
                if (defaultid.equals("3")){
                    String url = IP+"lifetime/group/account/getGroupUser?token="+token+"&groupId="+groupId;
                    String  result =  new NetWorkRequest().getServiceInfo(url);
                    Message msg = new Message();
                    msg.obj = result;
                    msg.what = 4;
                    handler.sendMessage(msg);
                }


            }
        }).start();
    }

    protected void dialog2() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("VIP即将到期，到期后将删除空间内所有数据，请及时续费！");

        builder.setTitle("提示");

        builder.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                SharedPreferences.Editor editor = getSharedPreferences("user",MODE_PRIVATE).edit();
                editor.putString(login_name+groupId+"overdue","true");
                editor.commit();
            }
        });

        builder.setCancelable(false);
        builder.create().show();

    }
    protected void dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("免费获取200M空间大小");

        builder.setTitle("提示");

        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Log.w("创建空间","创建空间");
                String url = IP+"lifetime/group/account/addGroup";
                RequestParams params = new RequestParams();
                params.addBodyParameter("token",token);
                params.addBodyParameter("groupDefaultId",defaultid);
                HttpUtils httputils = new HttpUtils();
                httputils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        Log.w("addgroupsuccess:",responseInfo.result.toString());
                        try {
                            JSONObject obj = new JSONObject(responseInfo.result.toString());
                            String code = obj.getString("code");
                            if (code.equals("1")){
                                Intent intent = new Intent();
                                intent.putExtra("index","kongjian");
                                intent.setAction("android.intent.action.UPDATE_FRAGMENT");
                                sendBroadcast(intent);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String groupId1 = getGroupId();
                                        if (!groupId1.equals("")){
                                            groupId = groupId1;
                                            callbackintent.putExtra("group_id",groupId);
                                            Message msg = new Message();
                                            msg.what=2;
                                            handler.sendMessage(msg);
                                        }
                                    }
                                }).start();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFailure(HttpException e, String s) {

                        Log.w("addgroupfaile",s.toString());
                    }
                });
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                KongJian_Info.this.finish();
            }
        });

        builder.setCancelable(false);
        builder.create().show();

    }

    @Override
    protected void onStart() {
        super.onStart();
        list.clear();
        init();
        activitystatus = 0;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mediaManager.pause();
        activitystatus = 1;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.kj_info_edit:
                Intent intent6 = new Intent();
                intent6.putExtra("content",edit.getText().toString());
                intent6.setClass(KongJian_Info.this, KongJian_InPut.class);
                startActivityForResult(intent6,0);
                break;
            case R.id.kj_info_edit_nav:
                Intent intent7 = new Intent();
                intent7.putExtra("content",edit.getText().toString());
                intent7.setClass(KongJian_Info.this, KongJian_InPut.class);
                startActivityForResult(intent7,0);
                break;
            case R.id.kongjian_info_fanhui:
                finish();
                break;
            case R.id.kj_info_fabiaoluyin_img:
                Intent intent1 = new Intent();
                intent1.putExtra("groupid",groupId);
                intent1.putExtra("callbackintent",callbackintent);
                intent1.setClass(KongJian_Info.this,KongJian_LuYin.class);
                startActivityForResult(intent1,0);
                break;
            case R.id.kongjian_shezhi:
                Intent intent = new Intent();
                intent.setClass(KongJian_Info.this,KongJian_SheZhi.class);
                intent.putExtra("touxiangstr",touxiangstr);
                intent.putExtra("namestr",namestr);
                intent.putExtra("update_time",update_time);
                intent.putExtra("descstr",descstr);
                intent.putExtra("password",password);
                intent.putExtra("defaultid",defaultid);
                intent.putExtra("groupid",groupId);
                intent.putExtra("callbackintent",callbackintent);
                startActivityForResult(intent,0);
                break;
            case R.id.kj_info_fabiao_img:
                Intent intent2 = new Intent();
                intent2.putExtra("default_id",defaultid);
                intent2.putExtra("group_id",groupId);
                intent2.putExtra("callbackintent",callbackintent);
                intent2.setClass(KongJian_Info.this,KongJian_BianJi.class);
                startActivityForResult(intent2,0);
                break;
            case R.id.kj_info_xc_lin:
                Intent intent3 = new Intent();
                intent3.putExtra("default_id",defaultid);
                intent3.putExtra("group_id",groupId);
                intent3.putExtra("password",password);
                intent3.setClass(KongJian_Info.this,KongJian_XiangCe2.class);
                startActivity(intent3);
                break;
            case R.id.kj_info_xc_lin_nav:
                Intent intent8 = new Intent();
                intent8.putExtra("default_id",defaultid);
                intent8.putExtra("group_id",groupId);
                intent8.putExtra("password",password);
                intent8.setClass(KongJian_Info.this,KongJian_XiangCe2.class);
                startActivity(intent8);
                break;
            case R.id.kj_info_yq_lin:
                Intent intent4 = new Intent();
                intent4.putExtra("default_id",defaultid);
                intent4.putExtra("group_id",groupId);
                intent4.setClass(KongJian_Info.this,KongJian_YaoQing.class);
                startActivity(intent4);
                break;
            case R.id.kj_info_yq_lin_nav:
                Intent intent9 = new Intent();
                intent9.putExtra("default_id",defaultid);
                intent9.putExtra("group_id",groupId);
                intent9.setClass(KongJian_Info.this,KongJian_YaoQing.class);
                startActivity(intent9);
                break;
        }

    }
}
