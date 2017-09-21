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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.administrator.yys.R;
import com.example.administrator.yys.network.NetWorkRequest;
import com.example.administrator.yys.other.Other_index;
import com.example.administrator.yys.utils.AppManager;
import com.example.administrator.yys.view.CircularImage;

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

public class WoDe_FenSi extends Activity implements SwipeRefreshLayout.OnRefreshListener{
    ListView lv;
    String token;
    String myuserid;
    LinearLayout fanhui;
    SwipeRefreshLayout swipe;
    int page = 1;
    int itemnumber = 0;
    ArrayList<Map<String,String>> list = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wode_fensi);
        fanhui = findViewById(R.id.wd_fensi_fanhui);
        swipe = findViewById(R.id.wode_fensi_swipe);
        AppManager.getAppManager().addActivity(this);
        fanhui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        token = getSharedPreferences("user",MODE_PRIVATE).getString("token","");
        myuserid = getSharedPreferences("user",MODE_PRIVATE).getString("user_id","");
        lv = findViewById(R.id.wd_fensi_listview);
        swipe.setOnRefreshListener(this);
        swipe.setColorSchemeColors(Color.GREEN,Color.BLUE,Color.RED);
        x.Ext.init(getApplication());
        init();
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1){
                Log.w("getFans",msg.obj.toString());
                try {
                    JSONObject obj = new JSONObject(msg.obj.toString());
                    String code = obj.getString("code");
                    if (code.equals("1")) {
                        JSONObject datum = obj.getJSONObject("datum");
                        JSONArray rows = datum.getJSONArray("rows");
                        for (int i = 0; i < rows.length(); i++) {
                            String user_id = rows.getJSONObject(i).get("user_id").toString();
                            String user_avatar = rows.getJSONObject(i).get("user_avatar").toString();
                            String user_name = rows.getJSONObject(i).get("user_name").toString();
                            String isConcern = rows.getJSONObject(i).get("isConcern").toString();
                            if (!user_id.equals(myuserid)) {
                                Map<String, String> map = new HashMap<>();
                                map.put("user_avatar", user_avatar);
                                map.put("user_name", user_name);
                                map.put("isConcern", isConcern);
                                map.put("user_id", user_id);
                                list.add(map);
                            }
                        }
                        if (rows.length() > 0) {
                            MyAdapter adapter = new MyAdapter(WoDe_FenSi.this);
                            lv.setAdapter(adapter);
                            lv.setSelection(itemnumber);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else if (msg.what==2){
                Log.w("addConcern",msg.obj.toString());
                if (msg.obj!=null){
                    try {
                        JSONObject obj = new JSONObject(msg.obj.toString());
                        Toast.makeText(getApplicationContext(),obj.getString("message"),Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

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
        public View getView(int i, View view, ViewGroup viewGroup) {

            final ViewHolder holder;
            if (view ==null){
                view = mInflater.inflate(R.layout.wode_fensi_item,null);
                holder = new ViewHolder();
                holder.name =  view.findViewById(R.id.fensi_item_name);
                holder.img =  view.findViewById(R.id.fensi_item_img);
                holder.btn =  view.findViewById(R.id.wode_fensi_guanzhu_btn);
                view.setTag(holder);
            }else{
                holder = (ViewHolder) view.getTag();
            }
            final Handler myhandler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (msg.what==1){
                        holder.btn.setVisibility(View.GONE);
                    }
                }
            };
           // x.image().bind(holder.img,getSharedPreferences("user",MODE_PRIVATE).getString("root","")+list.get(i).get("user_avatar").toString());
            Glide.with(getApplicationContext()).load(getSharedPreferences("user",MODE_PRIVATE).getString("root","")+list.get(i).get("user_avatar").toString()).into(holder.img);
            holder.name.setText(list.get(i).get("user_name").toString());
            final String userid = list.get(i).get("user_id").toString();
            final String isConcern = list.get(i).get("isConcern").toString();
            if (isConcern.equals("true")){
                holder.btn.setVisibility(View.GONE);
            }
            holder.btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            String url1 = IP+"lifetime/concern/account/addConcern?concernId="+userid+"&token="+token;
                            String  result =  new NetWorkRequest().getServiceInfo(url1);
                            try {
                                JSONObject obj = new JSONObject(result.toString());
                                String code = obj.getString("code");
                                if (code.equals("1")){
                                   Message msg = new Message();
                                    msg.what=1;
                                    myhandler.sendMessage(msg);
                                }
                                Message msg = new Message();
                                msg.obj = result;
                                msg.what=2;
                                handler.sendMessage(msg);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
            });
            holder.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!token.equals("")){
                        Intent intent = new Intent();
                        intent.setClass(getApplicationContext(),Other_index.class);
                        intent.putExtra("user_id",userid);
                        startActivity(intent);
                    }else {
                        Toast.makeText(WoDe_FenSi.this,"请先登录",Toast.LENGTH_SHORT).show();
                    }

                }
            });
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
                String url = IP+"lifetime/concern/account/getFans?token="+token+"&rows=15&page="+page;
                String  result =  new NetWorkRequest().getServiceInfo(url);
                Message msg2 = new Message();
                msg2.obj = result;
                msg2.what =1;
                handler.sendMessage(msg2);
            }
        }.start();
    }
    public final class ViewHolder{
        public TextView name;
        public CircularImage img;
        public Button btn;
    }
    private void init() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                String url = IP+"lifetime/concern/account/getFans?token="+token+"&rows=15&page=1";
                String  result =  new NetWorkRequest().getServiceInfo(url);
                Message msg = new Message();
                msg.what = 1;
                msg.obj = result;
                handler.sendMessage(msg);
            }
        }.start();
    }
}
