package com.example.administrator.yys.kongjian;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.yys.R;
import com.example.administrator.yys.network.NetWorkRequest;
import com.example.administrator.yys.utils.MyHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.administrator.yys.utils.IPAddress.IP;

/**
 * Created by Administrator on 2017/9/11 0011.
 */

public class KongJian_shenJi extends Activity {
    LinearLayout fanhui,pay_weixin,pay_zhifubao;
    String token ,groupid;
    GridView gridView;
    int vipgrade = 0,tempvip,tempvipgrade=0;
    String vipstarttime,vipendtime;
    TextView tv1,tv2,tv3,tv4,tv5,tv6,tv7;
    int actualPay = -1;
    ArrayList<Map<String,String>> list = new ArrayList<>();
    MyAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kongjian_shenji);
        fanhui = findViewById(R.id.kongjian_shenji_fanhui);
        pay_weixin = findViewById(R.id.pay_weixin);
        pay_zhifubao = findViewById(R.id.pay_zhifubao);
        fanhui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        gridView = findViewById(R.id.kongjian_shenji_gridview);
        Intent intent = getIntent();
        groupid = intent.getStringExtra("groupid");
        token = getSharedPreferences("user",MODE_PRIVATE).getString("token","");
        tv1 = findViewById(R.id.kongjian_shenji_vip_text);
        tv2 = findViewById(R.id.kongjian_shenji_price_title);
        tv3 = findViewById(R.id.kongjian_shenji_price_text);
        tv4 = findViewById(R.id.kongjian_shenji_info_price_title);
        tv5 = findViewById(R.id.kongjian_shenji_info_price);
        tv6 = findViewById(R.id.kongjian_shenji_info_price_total);
        tv7 = findViewById(R.id.kongjian_shenji_tv7);
        pay_zhifubao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pay("zhifubao");
            }
        });
        pay_weixin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pay("weixin");
            }
        });
        init();
    }

    private void pay(String str) {
        if (vipgrade==0&&actualPay==-1){

        }else {
                //调用支付
                if (actualPay>0){
                    Toast.makeText(getApplicationContext(),"调用支付",Toast.LENGTH_SHORT).show();
              }
        }
    }

    Handler handler = new MyHandler(this){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==1&& msg.obj!=null){
                Log.w("vip info",msg.obj.toString());
                try {
                    JSONObject obj = new JSONObject(msg.obj.toString());
                    String code = obj.getString("code");
                    if (code.equals("1")){
                        JSONArray datum = obj.getJSONArray("datum");
                        for (int i=0;i<datum.length();i++){
                            JSONObject jsobj = datum.getJSONObject(i);
                            String group_size = jsobj.getString("group_size");
                            String need_pay = jsobj.getString("need_pay");
                            String group_vip_id = jsobj.getString("group_vip_id");
                            String vip_grade = jsobj.getString("vip_grade");
                            Map<String,String> map = new HashMap<>();
                            map.put("group_size",group_size);
                            map.put("need_pay",need_pay);
                            map.put("group_vip_id",group_vip_id);
                            map.put("vip_grade",vip_grade);
                            if (!vip_grade.equals("0")){
                                list.add(map);
                            }
                        }
                        adapter = new MyAdapter(getApplicationContext());
                        gridView.setAdapter(adapter);
                        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                                tempvipgrade = Integer.parseInt(list.get(i).get("group_vip_id"));
                                if (Integer.parseInt(list.get(i).get("group_vip_id"))>vipgrade){
                                        adapter.setSelection(i);
                                        adapter.notifyDataSetChanged();
                                    tempvip = Integer.parseInt(list.get(i).get("group_vip_id"))-vipgrade;
                                    if (tempvip!=0){
                                        if (Integer.parseInt(list.get(i).get("group_vip_id"))>vipgrade){
                                            tv2.setText("等级升级：");
                                            tv3.setText("VIP"+vipgrade+"升至VIP"+list.get(i).get("group_vip_id"));
                                            tv4.setText("升级VIP"+list.get(i).get("group_vip_id")+":");
                                            tv7.setText("(所选等级金额-原等级折算金额)");
                                        }
                                    }
                                   new Thread(){
                                       @Override
                                       public void run() {
                                           super.run();
                                           String url3 = IP+"lifetime/groupGrade/account/obversion?token="+token+"&groupId="+groupid+"&groupVIPId="+list.get(i).get("group_vip_id");
                                           String  result3 =  new NetWorkRequest().getServiceInfo(url3);
                                           Message msg3 = new Message();
                                           msg3.obj = result3;
                                           msg3.what = 3;
                                           handler.sendMessage(msg3);
                                       }
                                   }.start();
                                }

                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (msg.what==2&& msg.obj!=null){
                Log.w("msg.obj",msg.obj.toString());
                try {
                    JSONObject obj = new JSONObject(msg.obj.toString());
                    String code = obj.getString("code");
                    if (code.equals("1")){
                        JSONObject datum = obj.getJSONObject("datum");
                        if (datum!=null){
                            vipgrade = datum.getInt("vip_grade");
                            //vipstarttime = datum.getString("create_time");
                            vipendtime = datum.getString("expired_time");
                            tv1.setText("VIP"+vipgrade+"(到期时间："+vipendtime+")");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (msg.what==3&& msg.obj!=null){
                Log.w("msg.3",msg.obj.toString());
                try {
                    JSONObject obj = new JSONObject(msg.obj.toString());
                    String code = obj.getString("code");
                    if (code.equals("1")){
                        JSONObject datum = obj.getJSONObject("datum");
                        String needpay = datum.getString("needPay");
                        actualPay =Integer.parseInt(needpay);
                        if (tempvip==0){
                            tv3.setText("￥"+needpay);
                            tv2.setText("等级续费：");
                            tv4.setText("等级续费：");
                            tv7.setText("");

                        }
                        if (vipgrade==0&&tempvip==0){
                            tv3.setText("￥"+needpay);
                        }
                        tv5.setText("￥"+needpay);
                        tv6.setText("￥"+needpay);
                        if (vipgrade!=0&&actualPay==0){
                            AlertDialog.Builder builder = new AlertDialog.Builder(KongJian_shenJi.this);
                            builder.setMessage("折算金额大于等于所升等级金额，无需付费即可升级，是否需要升级？");
                            builder.setTitle("提示");
                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    new Thread(){
                                        @Override
                                        public void run() {
                                            super.run();
                                            String url3 = IP+"lifetime/groupGrade/account/changeGrade?token="+token+"&groupId="+groupid+"&groupVIPId="+tempvipgrade;
                                            String  result3 =  new NetWorkRequest().getServiceInfo(url3);
                                            Message msg3 = new Message();
                                            msg3.obj = result3;
                                            msg3.what = 4;
                                            handler.sendMessage(msg3);
                                        }
                                    }.start();
                                }
                            });

                            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    adapter.setSelection(vipgrade-1);
                                    adapter.notifyDataSetChanged();
                                    tempvip = 0;
                                    new Thread(){
                                        @Override
                                        public void run() {
                                            super.run();
                                            String url3 = IP+"lifetime/groupGrade/account/obversion?token="+token+"&groupId="+groupid+"&groupVIPId="+vipgrade;
                                            String  result3 =  new NetWorkRequest().getServiceInfo(url3);
                                            Message msg3 = new Message();
                                            msg3.obj = result3;
                                            msg3.what = 3;
                                            handler.sendMessage(msg3);
                                        }
                                    }.start();
                                }
                            });

                            builder.setCancelable(false);
                            builder.create().show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (msg.what==4&& msg.obj!=null){
                Log.w("msg4 info",msg.obj.toString());
                try {
                    JSONObject obj = new JSONObject(msg.obj.toString());
                    String code = obj.getString("code");
                    if (code.equals("1")){
                        finish();
                    }
                    Toast.makeText(getApplicationContext(),obj.getString("message"),Toast.LENGTH_SHORT).show();
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
                String url2 = IP+"lifetime/groupGrade/account/getGroupGrade?token="+token+"&groupId="+groupid;
                String  result2 =  new NetWorkRequest().getServiceInfo(url2);
                Message msg2 = new Message();
                msg2.obj = result2;
                msg2.what = 2;
                handler.sendMessage(msg2);

                String url = IP+"lifetime/groupGrade/account/getGroupVIPs?token="+token;
                String  result =  new NetWorkRequest().getServiceInfo(url);
                Message msg = new Message();
                msg.obj = result;
                msg.what = 1;
                handler.sendMessage(msg);

                String url3 = IP+"lifetime/groupGrade/account/obversion?token="+token+"&groupId="+groupid+"&groupVIPId="+vipgrade;
                String  result3 =  new NetWorkRequest().getServiceInfo(url3);
                Message msg3 = new Message();
                msg3.obj = result3;
                msg3.what = 3;
                handler.sendMessage(msg3);
            }
        }.start();
    }

    private class MyAdapter extends BaseAdapter{

        private int clickTemp = -1;
        private LayoutInflater mInflater;//得到一个LayoutInfalter对象用来导入布局
        public MyAdapter(Context context) {
            Log.w("myadapter","construct");
            this.mInflater = LayoutInflater.from(context);
        }
        public void setSelection(int position){
            clickTemp = position;
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
                view = mInflater.inflate(R.layout.kongjian_shenji_item,null);
                holder = new ViewHolder();
                holder.group_size =  view.findViewById(R.id.shenji_group_size);
                holder.vip_grade =  view.findViewById(R.id.shenji_vipgrade);
                view.setTag(holder);
            }else{
                holder = (ViewHolder) view.getTag();
            }
            holder.group_size.setText(Long.parseLong(list.get(i).get("group_size"))/1024/1024/1024+"G空间￥"+list.get(i).get("need_pay"));
            holder.vip_grade.setText("VIP"+list.get(i).get("vip_grade")+"/一年");
            if (clickTemp == i){
                view.setBackgroundResource(R.drawable.shape_radious_cheng);
                holder.group_size.setTextColor(Color.WHITE);
                holder.vip_grade.setTextColor(Color.WHITE);
            }else {
                view.setBackgroundResource(R.drawable.shape_radious_boder_cheng);
                holder.group_size.setTextColor(Color.parseColor("#4f4f4f"));
                holder.vip_grade.setTextColor(Color.parseColor("#f08f1d"));
            }
            if (Integer.parseInt(list.get(i).get("group_vip_id"))<=vipgrade){
                view.setBackgroundResource(R.drawable.shape_radious_hui);
                holder.group_size.setTextColor(Color.parseColor("#999999"));
                holder.vip_grade.setTextColor(Color.parseColor("#999999"));
            }
            return view;
        }
    }
    public final class ViewHolder{
        public TextView group_size;
        public TextView vip_grade;
    }
}
