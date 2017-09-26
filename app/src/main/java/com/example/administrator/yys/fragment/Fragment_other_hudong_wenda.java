package com.example.administrator.yys.fragment;

import android.annotation.SuppressLint;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.yys.R;
import com.example.administrator.yys.hudong.WenDa_Info;
import com.example.administrator.yys.network.NetWorkRequest;
import com.example.administrator.yys.utils.MyHandler;
import com.example.administrator.yys.view.MyFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.example.administrator.yys.utils.IPAddress.IP;

/**
 * Created by Administrator on 2017/6/15 0015.
 */
@SuppressLint("ValidFragment")
public class Fragment_other_hudong_wenda extends MyFragment implements SwipeRefreshLayout.OnRefreshListener{
    String userid = "";
    public Fragment_other_hudong_wenda(String id){
        this.userid = id;
    }
    ListView lv;
    String token;
    ArrayList<Map<String,String>> list = new ArrayList();
    SwipeRefreshLayout swipe;
    int page = 1;
    int itemnumber = 0;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.other_hudong_wenda,container,false);
        lv = (ListView) view.findViewById(R.id.other_wenda_listview_list);
        swipe = view.findViewById(R.id.other_wenda_swipe);
        x.Ext.init(getActivity().getApplication());
        token = getActivity().getSharedPreferences("user", MODE_PRIVATE).getString("token","");
        swipe.setOnRefreshListener(this);
        swipe.setColorSchemeColors(Color.GREEN,Color.BLUE,Color.RED);
        init();
        return view;
    }


    Handler handler = new MyHandler(getActivity()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==1){
                Log.w("getanswer",msg.obj.toString());
                try {
                    JSONObject obj = new JSONObject(msg.obj.toString());
                    String code = obj.getString("code");
                    if (code.equals("1")){
                        JSONObject datum = obj.getJSONObject("datum");
                        JSONArray rows = datum.getJSONArray("rows");
                        for (int i=0;i<rows.length();i++){
                            Map<String,String> map = new HashMap();
                            JSONObject content = rows.getJSONObject(i);
                            map.put("interact_answer_id",content.getString("interact_answer_id"));
                            map.put("create_time",content.getString("create_time").substring(0,10));
                            map.put("user_id",content.getString("user_id"));
                            map.put("title",content.getString("title"));
                            map.put("content",content.getString("content"));
                            map.put("status",content.getString("status"));
                            list.add(map);
                        }
                        if (rows.length()>0){
                            MyAdapter adapter = new MyAdapter(getActivity());
                            lv.setAdapter(adapter);
                            lv.setSelection(itemnumber);
                            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    Intent intent = new Intent();
                                    intent.setClass(getActivity(), WenDa_Info.class);
                                    intent.putExtra("content",list.get(i).get("content").toString());
                                    intent.putExtra("title",list.get(i).get("title").toString());
                                    intent.putExtra("interact_answer_id",list.get(i).get("interact_answer_id").toString());
                                    intent.putExtra("user_id",list.get(i).get("user_id").toString());
                                    startActivity(intent);
                                }
                            });
                        }else {
                            String[] strs = {"还没有内容哦~"};
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),R.layout.nomessage_item,strs);
                            lv.setAdapter(adapter);
                            lv.setDivider(null);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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
            ViewHolder holder;
            if (view ==null){
                view = mInflater.inflate(R.layout.wenda_list_item,null);
                holder = new ViewHolder();
                holder.info =  view.findViewById(R.id.wenda_item_info);
                holder.title =  view.findViewById(R.id.wenda_item_title);
                view.setTag(holder);
            }else{
                holder = (ViewHolder) view.getTag();
            }
            Map map = (Map) list.get(i);

            holder.info.setText(list.get(i).get("content").toString());
            holder.title.setText(list.get(i).get("title").toString());
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
                String url = IP+"lifetime/answer/account/getOtherAnswers?token="+token+"&rows=15&page="+page+"&userId="+userid;
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
                String url = IP+"lifetime/answer/account/getOtherAnswers?token="+token+"&rows=15&page=1"+"&userId="+userid;
                String  result =  new NetWorkRequest().getServiceInfo(url);
                Message msg2 = new Message();
                msg2.obj = result;
                msg2.what =1;
                handler.sendMessage(msg2);
            }
        }.start();
    }
}
