package com.example.administrator.yys.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.administrator.yys.R;
import com.example.administrator.yys.network.NetWorkRequest;
import com.example.administrator.yys.view.CircularImage;
import com.example.administrator.yys.view.MyFragment;
import com.example.administrator.yys.wode.WoDe_BangZhu;
import com.example.administrator.yys.wode.WoDe_CaoGao;
import com.example.administrator.yys.wode.WoDe_FenSi;
import com.example.administrator.yys.wode.WoDe_GuShi;
import com.example.administrator.yys.wode.WoDe_GuanZhu;
import com.example.administrator.yys.wode.WoDe_SheZhi;
import com.example.administrator.yys.wode.WoDe_SheZhi_ZiLiao;
import com.example.administrator.yys.wode.WoDe_ShouChang;
import com.example.administrator.yys.wode.WoDe_TongZhi;
import com.example.administrator.yys.wode.WoDe_WenDa;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;

import static android.content.Context.MODE_PRIVATE;
import static com.example.administrator.yys.utils.IPAddress.IP;

/**
 * Created by Administrator on 2017/6/14 0014.
 */

public class Fragment_wode extends MyFragment implements View.OnClickListener{
    SharedPreferences sharedPreferences;
    CircularImage img;
    LinearLayout guanzhu_lin;
    LinearLayout fensi_lin;
    LinearLayout shouchang_lin;
    LinearLayout gushi_lin;
    LinearLayout wenda_lin;
    LinearLayout tongzhi_lin;
    LinearLayout caogao_lin;
    LinearLayout bangzhu_lin;
    LinearLayout shezhi_lin;

    ImageView guanzhu_news;
    ImageView fensi_news;
    ImageView shouchang_news;
    ImageView gushi_news;
    ImageView wenda_news;
    ImageView tongzhi_news;
    ImageView caogao_news;
    ImageView bangzhu_news;
    ImageView shezhi_news;
    String token,root;
    TextView username,gexingqianmin;
    MyReceiver receiver;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.wode,container,false);
        username = view.findViewById(R.id.wode_username);
        gexingqianmin = view.findViewById(R.id.wode_gexinqianmin);
        sharedPreferences = getActivity().getSharedPreferences("user",MODE_PRIVATE);
        root = sharedPreferences.getString("root","");
        String name = sharedPreferences.getString("user_name","");
        String qianmin = sharedPreferences.getString("gexingqianmin","");
        if (!qianmin.equals("null")){
            gexingqianmin.setText(qianmin);
        }
        username.setText(name);
        String userimg = sharedPreferences.getString("user_avatar","");
        img =  view.findViewById(R.id.wode_img);
        x.Ext.init(getActivity().getApplication());
        token = sharedPreferences.getString("token","");

        //x.image().bind(img,root+userimg);
        Glide.with(getActivity()).load(root+userimg).into(img);
        Log.w("userImg",root+userimg);

        guanzhu_lin = view.findViewById(R.id.wd_guanzhu_lin);
        fensi_lin = view.findViewById(R.id.wd_fensi_lin);
        shouchang_lin = view.findViewById(R.id.wd_shouchang_lin);
        gushi_lin = view.findViewById(R.id.wd_gushi_lin);
        wenda_lin = view.findViewById(R.id.wd_wenda_lin);
        tongzhi_lin = view.findViewById(R.id.wd_tongzhi_lin);
        caogao_lin = view.findViewById(R.id.wd_caogao_lin);
        bangzhu_lin = view.findViewById(R.id.wd_bangzhu_lin);
        shezhi_lin = view.findViewById(R.id.wd_shezhi_lin);

        receiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.UPDATE_FRAGMENT");
        getActivity().registerReceiver(receiver,intentFilter);

        img.setOnClickListener(this);
        guanzhu_lin.setOnClickListener(this);
        fensi_lin.setOnClickListener(this);
        shouchang_lin.setOnClickListener(this);
        gushi_lin.setOnClickListener(this);
        wenda_lin.setOnClickListener(this);
        tongzhi_lin.setOnClickListener(this);
        caogao_lin.setOnClickListener(this);
        bangzhu_lin.setOnClickListener(this);
        shezhi_lin.setOnClickListener(this);

        guanzhu_news = view.findViewById(R.id.wode_guanzhu_news);
        fensi_news = view.findViewById(R.id.wode_fensi_news);
        shouchang_news = view.findViewById(R.id.wode_shouchang_news);
        gushi_news = view.findViewById(R.id.wode_gushi_news);
        wenda_news = view.findViewById(R.id.wode_wenda_news);
        tongzhi_news = view.findViewById(R.id.wode_tongzhi_news);
        caogao_news = view.findViewById(R.id.wode_caogao_news);
        bangzhu_news = view.findViewById(R.id.wode_bangzhu_news);
        shezhi_news = view.findViewById(R.id.wode_shezhi_news);
        init();
        Log.w("oncreate","fragment_wode");
        return view;
    }

    private class  MyReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String index = intent.getStringExtra("index");
            if (index.equals("wode")){
                username.setText(sharedPreferences.getString("user_name",""));
                gexingqianmin.setText(sharedPreferences.getString("gexingqianmin",""));
                //x.image().bind(img,root+sharedPreferences.getString("user_avatar",""));
                Glide.with(getActivity()).load(root+sharedPreferences.getString("user_avatar","")).into(img);
            }
        }
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==1 && msg.obj!=null){
                Log.w("isRead",msg.obj.toString());
                try {
                    JSONObject obj = new JSONObject(msg.obj.toString());
                    String code = obj.getString("code");
                    if (code.equals("1")){
                        JSONObject datum = obj.getJSONObject("datum");
                        String isRead = datum.getString("isRead");
                        if (isRead.equals("true")){
                            tongzhi_news.setVisibility(View.VISIBLE);
                            Glide.with(getActivity()).load(R.mipmap.img_light).into(tongzhi_news);
                        }
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
                String url = IP+"lifetime/message/account/isRead?token="+token;
                String  result =  new NetWorkRequest().getServiceInfo(url);
                Message msg2 = new Message();
                msg2.obj = result;
                msg2.what =1;
                handler.sendMessage(msg2);
            }
        }.start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.wd_guanzhu_lin:
                Intent intent1 = new Intent();
                intent1.setClass(getActivity(),WoDe_GuanZhu.class);
                startActivity(intent1);
                break;
            case R.id.wd_fensi_lin:
                Intent intent2 = new Intent();
                intent2.setClass(getActivity(),WoDe_FenSi.class);
                startActivity(intent2);
                break;
            case R.id.wd_shouchang_lin:
                Intent intent3 = new Intent();
                intent3.setClass(getActivity(),WoDe_ShouChang.class);
                startActivity(intent3);
                break;
            case R.id.wd_gushi_lin:
                Intent intent4 = new Intent();
                intent4.setClass(getActivity(),WoDe_GuShi.class);
                startActivity(intent4);
                break;
            case R.id.wd_wenda_lin:
                Intent intent5 = new Intent();
                intent5.setClass(getActivity(),WoDe_WenDa.class);
                startActivity(intent5);
                break;
            case R.id.wd_tongzhi_lin:
                Intent intent6 = new Intent();
                intent6.setClass(getActivity(),WoDe_TongZhi.class);
                startActivity(intent6);
                tongzhi_news.setVisibility(View.GONE);
                break;
            case R.id.wd_caogao_lin:
                Intent intent7 = new Intent();
                intent7.setClass(getActivity(),WoDe_CaoGao.class);
                startActivity(intent7);
                break;
            case R.id.wd_bangzhu_lin:
                Intent intent8 = new Intent();
                intent8.setClass(getActivity(),WoDe_BangZhu.class);
                startActivity(intent8);
                break;
            case R.id.wd_shezhi_lin:
                Intent intent9 = new Intent();
                intent9.setClass(getActivity(),WoDe_SheZhi.class);
                startActivity(intent9);
                break;
            case R.id.wode_img:
                Intent intent10 = new Intent();
                intent10.setClass(getActivity(), WoDe_SheZhi_ZiLiao.class);
                startActivity(intent10);
                break;

        }
    }
}
