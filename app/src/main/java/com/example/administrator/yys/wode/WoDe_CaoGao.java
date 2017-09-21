package com.example.administrator.yys.wode;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.example.administrator.yys.utils.AppManager;
import com.example.administrator.yys.utils.MyHandler;
import com.example.administrator.yys.utils.ParseJson;
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

public class WoDe_CaoGao extends Activity implements SwipeRefreshLayout.OnRefreshListener{
    ListView lv;
    ImageView fabiao;
    String token;
    LinearLayout fanhui;
    SwipeRefreshLayout swipe;
    ArrayList<Map<String,String>> list = new ArrayList();
    int page = 1;
    int itemnumber = 0;
    MyAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wode_caogaoxiang);
        lv = findViewById(R.id.wd_caogaoxiang_listview);
        AppManager.getAppManager().addActivity(this);
        fanhui = findViewById(R.id.wode_caogao_fanhui);
        fanhui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        swipe = findViewById(R.id.wode_caogao_swipe);
        token = getSharedPreferences("user", MODE_PRIVATE).getString("token","");
        swipe.setOnRefreshListener(this);
        swipe.setColorSchemeColors(Color.GREEN,Color.BLUE,Color.RED);
        x.Ext.init(getApplication());
        init();
    }

    Handler handler = new MyHandler(WoDe_CaoGao.this){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==1){
                Log.w("getstorys",msg.obj.toString());
                try {
                    JSONObject obj = new JSONObject(msg.obj.toString());
                    String code = obj.getString("code");
                    if (code.equals("1")) {
                        JSONObject datum = obj.getJSONObject("datum");
                        JSONArray rows = datum.getJSONArray("rows");
                        //list.clear();
                        for (int i = 0; i < rows.length(); i++) {
                            Map<String, String> map = new HashMap();
                            JSONObject content = rows.getJSONObject(i);
                            map.put("interact_story_id", content.getString("interact_story_id"));
                            map.put("create_time", content.getString("create_time").substring(0, 10));
                            map.put("user_id", content.getString("user_id"));
                            map.put("thumbnail_url", content.getString("thumbnail_url"));
                            map.put("content", content.getString("content"));
                            String text = new ParseJson().gettext(content.getString("content"), "stories").replaceAll("%5Cn", "\n");
                            String url = new ParseJson().geturl(content.getString("content"), "stories");
                            if (url.equals("")) {
                                map.put("url", content.getString("thumbnail_url"));
                            } else {
                                map.put("url", url);
                            }
                            map.put("text", text);

                            list.add(map);
                        }
                        if (rows.length() > 0) {
                            adapter = new MyAdapter(WoDe_CaoGao.this);
                            adapter.notifyDataSetChanged();
                            lv.setAdapter(adapter);
                            lv.setSelection(itemnumber);
                            lv.setDivider(null);
                            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    Intent intent = new Intent();
                                    intent.setClass(getApplicationContext(), WoDe_CaoGao_XiuGai.class);
                                    intent.putExtra("content", list.get(i).get("content").toString());
                                    intent.putExtra("interact_story_id", list.get(i).get("interact_story_id").toString());
                                    intent.putExtra("user_id", list.get(i).get("user_id").toString());
                                    startActivityForResult(intent, 0);
                                }
                            });
                            lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                @Override
                                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    /*Intent intent = new Intent();
                                    intent.setClass(getApplicationContext(), ShowDialog2.class);
                                    intent.putExtra("storyIds",list.get(i).get("interact_story_id").toString());
                                    startActivityForResult(intent,0);*/
                                    showDialog(list.get(i).get("interact_story_id").toString());
                                    return true;
                                }
                            });
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private void showDialog(final String storyIds) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("是否删除该内容");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                //删除空间
                String url = IP+"lifetime/story/account/delDraftBoxStorys";
                RequestParams params = new RequestParams();
                params.addBodyParameter("token",token);
                params.addBodyParameter("storyIds",storyIds);
                HttpUtils httputils = new HttpUtils();
                httputils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        Log.w("delDraftBoxsuccess:",responseInfo.result.toString());
                        try {
                            JSONObject obj = new JSONObject(responseInfo.result.toString());
                            String code = obj.getString("code");
                            if (code.equals("1")){
                                //ShowDialog2.this.setResult(1);
                                Toast.makeText(getApplicationContext(),"删除草稿成功！",Toast.LENGTH_SHORT).show();
                                //finish();
                                list.clear();
                                init();
                                adapter.notifyDataSetChanged();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFailure(HttpException e, String s) {
                        Log.w("delDraftBoxfaile",s.toString());
                    }
                });
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.setCancelable(false);
        builder.create().show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==1){
            Log.w("caogao","onactivity");
                list.clear();
                init();
        }
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
            ViewHolder holder;
            if (view ==null){
                view = mInflater.inflate(R.layout.gushi_list_item,null);
                holder = new ViewHolder();
                holder.info =  view.findViewById(R.id.gushi_itme_text);
                holder.img =  view.findViewById(R.id.gushi_item_img);
                view.setTag(holder);
            }else{
                holder = (ViewHolder) view.getTag();
            }
            Map map = (Map) list.get(i);

            //x.image().bind(holder.img,getSharedPreferences("user",MODE_PRIVATE).getString("root","")+  list.get(i).get("url"));
            Glide.with(getApplicationContext()).load(getSharedPreferences("user",MODE_PRIVATE).getString("root","")+  list.get(i).get("url")).into(holder.img);
            Log.w("infoimg",getSharedPreferences("user",MODE_PRIVATE).getString("root","")+list.get(i).get("url").toString());
            holder.info.setText(list.get(i).get("text").toString());
            Log.w("infotext",list.get(i).get("text").toString());
            if (i==list.size()-1&&(i+1)%15==0){
                loadmessage();
                itemnumber = i;
            }
            return view;
        }
    }
    public final class ViewHolder{
        public TextView title;
        public TextView info;
        public ImageView img;
    }

    private void loadmessage() {
        page++;
        new Thread(){
            @Override
            public void run() {
                super.run();
                String url = IP+"lifetime/story/account/getDraftBoxStorys?token="+token+"&rows=15&page="+page;
                String  result =  new NetWorkRequest().getServiceInfo(url);
                Message msg2 = new Message();
                msg2.obj = result;
                msg2.what =1;
                handler.sendMessage(msg2);
            }
        }.start();
    }
    private void init() {

        new Thread(){
            @Override
            public void run() {
                super.run();
                String url = IP+"lifetime/story/account/getDraftBoxStorys?token="+token+"&rows=15&page=1";
                String  result =  new NetWorkRequest().getServiceInfo(url);
                Message msg2 = new Message();
                msg2.obj = result;
                msg2.what =1;
                handler.sendMessage(msg2);
            }
        }.start();
    }
}
