package com.example.administrator.yys.wode;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.administrator.yys.R;
import com.example.administrator.yys.hudong.GuShi_Info;
import com.example.administrator.yys.hudong.WenDa_Info;
import com.example.administrator.yys.network.NetWorkRequest;
import com.example.administrator.yys.other.Other_index;
import com.example.administrator.yys.utils.AppManager;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.administrator.yys.utils.IPAddress.IP;

/**
 * Created by Administrator on 2017/7/6 0006.
 */

public class WoDe_TongZhi extends Activity implements SwipeRefreshLayout.OnRefreshListener{
    String token,root;
    ArrayList<Map<String,String>> list = new ArrayList();
    ListView lv;
    LinearLayout fanhui;
    SwipeRefreshLayout swipe;
    int page = 1;
    int itemnumber = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wode_xiaoxitongzhi);
        fanhui = findViewById(R.id.wode_tongzhi_fanhui);
        swipe = findViewById(R.id.wode_tongzhi_swipe);
        AppManager.getAppManager().addActivity(this);
        fanhui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        token = getSharedPreferences("user", MODE_PRIVATE).getString("token","");
        root = getSharedPreferences("user", MODE_PRIVATE).getString("root","");
        lv = findViewById(R.id.wode_tongzhi_item);
        x.Ext.init(getApplication());
        swipe.setOnRefreshListener(this);
        swipe.setColorSchemeColors(Color.GREEN,Color.BLUE,Color.RED);
        init();
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.w("the msg.obj",msg.obj.toString());
            if (msg.what==1){
                Log.w("getmessage",msg.obj.toString());
                //message_type:1 系统 2，3邀请 4关注 5故事 6问答
                try {
                    JSONObject obj = new JSONObject(msg.obj.toString());
                    String code = obj.getString("code");
                    if (code.equals("1")){
                        JSONObject datum = obj.getJSONObject("datum");
                        JSONArray rows = datum.getJSONArray("rows");
                            for (int i=0;i<rows.length();i++){
                                String message_type = rows.getJSONObject(i).getString("message_type");
                                if (message_type.equals("5")){ //故事
                                    String user_avatar = rows.getJSONObject(i).getString("user_avatar");
                                    String from_id = rows.getJSONObject(i).getString("from_id");
                                    String user_id = rows.getJSONObject(i).getString("user_id");
                                    String contentmsg = rows.getJSONObject(i).getString("content");
                                    String user_name = rows.getJSONObject(i).getString("user_name");
                                    String message_id = rows.getJSONObject(i).getString("message_id");
                                    String status = rows.getJSONObject(i).getString("status");
                                    Log.w("rows.getjson",rows.getJSONObject(i).toString());
                                    String interact_story_id="",thumbnail_url="",content="",authorid="";
                                    try {
                                        JSONObject interact_story = rows.getJSONObject(i).getJSONObject("interact_story");
                                        interact_story_id = interact_story.getString("interact_story_id");
                                        thumbnail_url = interact_story.getString("thumbnail_url");
                                        authorid = interact_story.getString("user_id");
                                        content = interact_story.getString("content");
                                    }catch (JSONException e){
                                        e.printStackTrace();
                                    }
                                    String text = new ParseJson().gettext(content,"stories").replaceAll("%5Cn","\n");
                                    if (text.length()==0){
                                        text = "该内容已被删除!";
                                    }
                                    String url = new ParseJson().geturl(content,"stories");
                                    Map<String,String> map = new HashMap<>();
                                    map.put("user_avatar",user_avatar);
                                    map.put("from_id",from_id);
                                    map.put("message_type",message_type);
                                    map.put("user_id",user_id);
                                    map.put("contentmsg",contentmsg);
                                    map.put("user_name",user_name);
                                    map.put("authorid",authorid);
                                    map.put("message_id",message_id);
                                    map.put("status",status);
                                    map.put("interact_story_id",interact_story_id);
                                    map.put("text",text);
                                    if (url.equals("")){
                                        map.put("url",thumbnail_url);
                                    }else {
                                        map.put("url",url);
                                    }
                                    map.put("content",content);
                                    list.add(map);
                                }
                                if (message_type.equals("6")){//问答
                                    String user_avatar = rows.getJSONObject(i).getString("user_avatar");
                                    String from_id = rows.getJSONObject(i).getString("from_id");
                                    String user_id = rows.getJSONObject(i).getString("user_id");
                                    String contentmsg = rows.getJSONObject(i).getString("content");
                                    String user_name = rows.getJSONObject(i).getString("user_name");
                                    String message_id = rows.getJSONObject(i).getString("message_id");
                                    String status = rows.getJSONObject(i).getString("status");
                                    String interact_answer_id="",title="该内容已被删除！",content="",authorid="";
                                    try{
                                        JSONObject interact_answer = rows.getJSONObject(i).getJSONObject("interact_answer");
                                        interact_answer_id = interact_answer.getString("interact_answer_id");
                                        title = interact_answer.getString("title");
                                        authorid = interact_answer.getString("user_id");
                                        content = interact_answer.getString("content");
                                    }catch (JSONException e){
                                        e.printStackTrace();
                                    }
                                    Map<String,String> map = new HashMap<>();
                                    map.put("user_avatar",user_avatar);
                                    map.put("from_id",from_id);
                                    map.put("user_id",user_id);
                                    map.put("message_type",message_type);
                                    map.put("contentmsg",contentmsg);
                                    map.put("authorid",authorid);
                                    map.put("user_name",user_name);
                                    map.put("message_id",message_id);
                                    map.put("status",status);
                                    map.put("interact_answer_id",interact_answer_id);
                                    map.put("title",title);
                                    map.put("content",content);
                                    list.add(map);
                                }
                                if (message_type.equals("2")||message_type.equals("3")){//邀请
                                    String user_avatar = rows.getJSONObject(i).getString("user_avatar");
                                    String from_id = rows.getJSONObject(i).getString("from_id");
                                    String user_id = rows.getJSONObject(i).getString("user_id");
                                    String contentmsg = rows.getJSONObject(i).getString("content");
                                    String content_id = rows.getJSONObject(i).getString("content_id");
                                    String user_name = rows.getJSONObject(i).getString("user_name");
                                    String message_id = rows.getJSONObject(i).getString("message_id");
                                    String status = rows.getJSONObject(i).getString("status");
                                    Map<String,String> map = new HashMap<>();
                                    map.put("user_avatar",user_avatar);
                                    map.put("from_id",from_id);
                                    map.put("user_id",user_id);
                                    map.put("content_id",content_id);
                                    map.put("message_type",message_type);
                                    map.put("contentmsg",contentmsg);
                                    map.put("user_name",user_name);
                                    map.put("message_id",message_id);
                                    map.put("status",status);
                                    list.add(map);
                                }
                                if (message_type.equals("4")){//邀请
                                    String user_avatar = rows.getJSONObject(i).getString("user_avatar");
                                    String from_id = rows.getJSONObject(i).getString("from_id");
                                    String user_id = rows.getJSONObject(i).getString("user_id");
                                    String contentmsg = rows.getJSONObject(i).getString("content");
                                    String user_name = rows.getJSONObject(i).getString("user_name");
                                    String message_id = rows.getJSONObject(i).getString("message_id");
                                    String status = rows.getJSONObject(i).getString("status");
                                    Map<String,String> map = new HashMap<>();
                                    map.put("user_avatar",user_avatar);
                                    map.put("from_id",from_id);
                                    map.put("user_id",user_id);
                                    map.put("message_type",message_type);
                                    map.put("contentmsg",contentmsg);
                                    map.put("user_name",user_name);
                                    map.put("message_id",message_id);
                                    map.put("status",status);
                                    list.add(map);
                                }
                                if (message_type.equals("1")){//邀请
                                    String user_avatar = rows.getJSONObject(i).getString("user_avatar");
                                    String user_id = rows.getJSONObject(i).getString("user_id");
                                    String contentmsg = rows.getJSONObject(i).getString("content");
                                    String user_name = rows.getJSONObject(i).getString("user_name");
                                    String message_id = rows.getJSONObject(i).getString("message_id");
                                    String status = rows.getJSONObject(i).getString("status");
                                    Map<String,String> map = new HashMap<>();
                                    map.put("user_avatar",user_avatar);
                                    map.put("user_id",user_id);
                                    map.put("message_type",message_type);
                                    map.put("contentmsg",contentmsg);
                                    map.put("user_name",user_name);
                                    map.put("message_id",message_id);
                                    map.put("status",status);
                                    list.add(map);
                                }

                                }
                                if (rows.length()>0){
                                    MyAdapter adapter = new MyAdapter(WoDe_TongZhi.this);
                                    lv.setAdapter(adapter);
                                    lv.setSelection(itemnumber);
                                    readMessage(list);
                                }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (msg.what==2){
                Log.w("redmessage",msg.obj.toString());
            }
        }
    };

    private void readMessage(ArrayList<Map<String, String>> list) {
        ArrayList<Map<String, String>> lists = list;
        String messageid ="";
        for (int i=0;i<lists.size();i++){
            if(!lists.get(i).get("status").equals("0")){
                messageid += lists.get(i).get("message_id")+"-";
            }
        }
        if (messageid.length()>=2){
            final String allmessageid = messageid.substring(0,messageid.length()-1);
            Log.w("messageid",allmessageid);
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    String url = IP+"lifetime/message/account/readMessage?token="+token+"&messageIds="+allmessageid;
                    String  result =  new NetWorkRequest().getServiceInfo(url);
                    Message msg2 = new Message();
                    msg2.obj = result;
                    msg2.what =2;
                    handler.sendMessage(msg2);
                }
            }.start();
        }
    }

    @Override
    public void onRefresh() {
        itemnumber = 0;
        page = 1;
        swipe.setRefreshing(true);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipe.setRefreshing(false);
                list.clear();
                init();
            }
        },3000);
    }

    private class MyAdapter extends BaseAdapter {

        private LayoutInflater mInflater;
        public MyAdapter(Context context) {
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
            ViewHolder holder;
            String message_type = list.get(i).get("message_type");
            view = null;
            if (message_type.equals("5")){
                if (view ==null) {
                    view = mInflater.inflate(R.layout.wode_tongzhi_gushi_item, null);
                    holder = new ViewHolder();
                    holder.touxiang = view.findViewById(R.id.gushi_item_touxiang);
                    holder.name = view.findViewById(R.id.gushi_item_name);
                    holder.content = view.findViewById(R.id.gushi_item_content);
                    holder.tupian = view.findViewById(R.id.gushi_item_gushi_img);
                    holder.info = view.findViewById(R.id.gushi_item_gushi_info);
                    holder.lin = view.findViewById(R.id.gushi_item_info_lin);
                    view.setTag(holder);
                }else{
                    holder = (ViewHolder) view.getTag();
                }
                holder.touxiang.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!token.equals("")){
                            Intent intent = new Intent();
                            intent.setClass(WoDe_TongZhi.this, Other_index.class);
                            intent.putExtra("user_id",list.get(i).get("from_id"));
                            startActivity(intent);
                        }else {
                            Toast.makeText(WoDe_TongZhi.this,"请先登录",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                holder.lin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(WoDe_TongZhi.this, GuShi_Info.class);
                        intent.putExtra("content",list.get(i).get("content"));
                        intent.putExtra("interact_story_id",list.get(i).get("interact_story_id"));
                        intent.putExtra("user_id",list.get(i).get("authorid"));
                        intent.putExtra("from_id",list.get(i).get("from_id"));
                        startActivity(intent);
                    }
                });
               // x.image().bind(holder.touxiang,root+list.get(i).get("user_avatar"));
                Glide.with(getApplicationContext()).load(root+list.get(i).get("user_avatar")).into(holder.touxiang);
                holder.name.setText(list.get(i).get("user_name"));
                holder.content.setText(list.get(i).get("contentmsg"));
                //x.image().bind(holder.tupian,root+list.get(i).get("url"));
                holder.tupian.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Glide.with(getApplicationContext()).load(root+list.get(i).get("url")).into(holder.tupian);
                holder.info.setText(list.get(i).get("text"));
            }
            if (message_type.equals("6")){
                if (view ==null) {
                    view = mInflater.inflate(R.layout.wode_tongzhi_wenda_item, null);
                    holder = new ViewHolder();
                    holder.touxiang = view.findViewById(R.id.wenda_item_touxiang);
                    holder.name = view.findViewById(R.id.wenda_item_name);
                    holder.content = view.findViewById(R.id.wenda_item_content);
                    holder.info = view.findViewById(R.id.wenda_item_gushi_info);
                    holder.lin = view.findViewById(R.id.wenda_item_info_lin);
                    view.setTag(holder);
                }else{
                    holder = (ViewHolder) view.getTag();
                }
                holder.touxiang.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!token.equals("")){
                            Intent intent = new Intent();
                            intent.setClass(WoDe_TongZhi.this, Other_index.class);
                            intent.putExtra("user_id",list.get(i).get("from_id"));
                            startActivity(intent);
                        }else {
                            Toast.makeText(WoDe_TongZhi.this,"请先登录",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                holder.lin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(WoDe_TongZhi.this, WenDa_Info.class);
                        intent.putExtra("content",list.get(i).get("content"));
                        intent.putExtra("title",list.get(i).get("title"));
                        intent.putExtra("interact_answer_id",list.get(i).get("interact_answer_id"));
                        intent.putExtra("user_id",list.get(i).get("authorid"));
                        intent.putExtra("from_id",list.get(i).get("from_id"));
                        startActivity(intent);
                    }
                });
                //x.image().bind(holder.touxiang,root+list.get(i).get("user_avatar"));
                Glide.with(getApplicationContext()).load(root+list.get(i).get("user_avatar")).into(holder.touxiang);
                holder.name.setText(list.get(i).get("user_name"));
                holder.content.setText(list.get(i).get("contentmsg"));
                holder.info.setText(list.get(i).get("title"));
            }
            if (message_type.equals("2")||message_type.equals("3")){
                if (view ==null) {
                    view = mInflater.inflate(R.layout.wode_tongzhi_yaoqing_item, null);
                    holder = new ViewHolder();
                    holder.touxiang = view.findViewById(R.id.yaoqing_item_touxiang);
                    holder.name = view.findViewById(R.id.yaoqing_item_name);
                    holder.content = view.findViewById(R.id.yaoqing_item_content);
                    holder.btn = view.findViewById(R.id.yaoqing_item_btn);
                    view.setTag(holder);
                }else{
                    holder = (ViewHolder) view.getTag();
                }
                if (message_type.equals("3")){
                    holder.btn.setClickable(false);
                    holder.btn.setText("已接受");
                    holder.btn.setVisibility(View.GONE);
                }
                holder.touxiang.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!token.equals("")){
                            Intent intent = new Intent();
                            intent.setClass(WoDe_TongZhi.this, Other_index.class);
                            intent.putExtra("user_id",list.get(i).get("from_id"));
                            Log.w("from id",list.get(i).get("from_id"));
                            startActivity(intent);
                        }else {
                            Toast.makeText(WoDe_TongZhi.this,"请先登录",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                final ViewHolder finalHolder = holder;
                holder.btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new Thread(){
                            @Override
                            public void run() {
                                super.run();
                                String url = IP+"lifetime/group/account/join";
                                RequestParams params = new RequestParams();
                                params.addBodyParameter("token",token);
                                params.addBodyParameter("messageId",list.get(i).get("message_id"));
                                HttpUtils httputils = new HttpUtils();
                                httputils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
                                    @Override
                                    public void onSuccess(ResponseInfo<String> responseInfo) {
                                        Log.w("join success:",responseInfo.result.toString());
                                        try {
                                            JSONObject obj = new JSONObject(responseInfo.result.toString());
                                            String code = obj.getString("code");
                                            if (code.equals("1")){
                                               finalHolder.btn.setText("已接受");
                                                finalHolder.btn.setClickable(false);
                                                finalHolder.btn.setVisibility(View.GONE);
                                                Intent intent = new Intent();
                                                intent.putExtra("index","kongjian");
                                                intent.setAction("android.intent.action.UPDATE_FRAGMENT");
                                                sendBroadcast(intent);
                                            }else{
                                                finalHolder.btn.setClickable(false);
                                                finalHolder.btn.setVisibility(View.GONE);
                                            }

                                            Toast.makeText(getApplicationContext(),obj.getString("message"),Toast.LENGTH_SHORT).show();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    @Override
                                    public void onFailure(HttpException e, String s) {
                                        Log.w("join faile",s.toString());
                                    }
                                });
                            }
                        }.start();
                    }
                });
               // x.image().bind(holder.touxiang,root+list.get(i).get("user_avatar"));
                Glide.with(getApplicationContext()).load(root+list.get(i).get("user_avatar")).into(holder.touxiang);
                holder.name.setText(list.get(i).get("user_name"));
                holder.content.setText(list.get(i).get("contentmsg"));
            }
            if (message_type.equals("4")){
                if (view ==null) {
                    view = mInflater.inflate(R.layout.wode_tongzhi_fensi_item, null);
                    holder = new ViewHolder();
                    holder.touxiang = view.findViewById(R.id.fensi_item_touxiang);
                    holder.name = view.findViewById(R.id.fensi_item_name);
                    holder.content = view.findViewById(R.id.fensi_item_content);
                    view.setTag(holder);
                }else{
                    holder = (ViewHolder) view.getTag();
                }

                holder.touxiang.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!token.equals("")){
                            Intent intent = new Intent();
                            intent.setClass(WoDe_TongZhi.this, Other_index.class);
                            intent.putExtra("user_id",list.get(i).get("from_id"));
                            startActivity(intent);
                        }else {
                            Toast.makeText(WoDe_TongZhi.this,"请先登录",Toast.LENGTH_SHORT).show();
                        }

                    }
                });

               // x.image().bind(holder.touxiang,root+list.get(i).get("user_avatar"));
                Glide.with(getApplicationContext()).load(root+list.get(i).get("user_avatar")).into(holder.touxiang);
                holder.name.setText(list.get(i).get("user_name"));
                holder.content.setText(list.get(i).get("contentmsg"));
            }
            if (message_type.equals("1")){
                if (view ==null) {
                    view = mInflater.inflate(R.layout.wode_tongzhi_xitong_item, null);
                    holder = new ViewHolder();
                    holder.touxiang = view.findViewById(R.id.xitong_item_touxiang);
                    holder.name = view.findViewById(R.id.xitong_item_name);
                    holder.content = view.findViewById(R.id.xitong_item_content);
                    view.setTag(holder);
                }else{
                    holder = (ViewHolder) view.getTag();
                }

                //x.image().bind(holder.touxiang,root+list.get(i).get("user_avatar"));
                Glide.with(getApplicationContext()).load(root+list.get(i).get("user_avatar")).into(holder.touxiang);
                holder.name.setText(list.get(i).get("user_name"));
                holder.content.setText(list.get(i).get("contentmsg"));
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
                String url = IP+"lifetime/message/account/getMessages?token="+token+"&rows=15&page="+page;
                String  result =  new NetWorkRequest().getServiceInfo(url);
                Message msg2 = new Message();
                msg2.obj = result;
                msg2.what =1;
                handler.sendMessage(msg2);
            }
        }.start();
    }
    public final class ViewHolder{
        public CircularImage touxiang;
        public TextView name;
        public TextView content;
        public ImageView tupian;
        public TextView info;
        public LinearLayout lin;
        public Button btn;
    }
    private void init() {
Log.w("token",token);
        new Thread(){
            @Override
            public void run() {
                super.run();
                String url = IP+"lifetime/message/account/getMessages?token="+token+"&rows=15&page=1";
                String  result =  new NetWorkRequest().getServiceInfo(url);
                Log.w("the result",result);
                Message msg2 = new Message();
                msg2.obj = result;
                msg2.what =1;
                handler.sendMessage(msg2);
            }
        }.start();
    }
}
