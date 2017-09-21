package com.example.administrator.yys.kongjian;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.administrator.yys.R;
import com.example.administrator.yys.network.NetWorkRequest;
import com.example.administrator.yys.other.Other_index;
import com.example.administrator.yys.utils.AppManager;
import com.example.administrator.yys.utils.MyHandler;
import com.example.administrator.yys.view.CircularImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;

import static com.example.administrator.yys.utils.IPAddress.IP;

/**
 * Created by Administrator on 2017/7/13 0013.
 */

public class Kongjian_ChengYuan extends Activity {
    String token;
    LinearLayout lin;
    String groupid;
    String root;
    LinearLayout fanhui;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kongjian_chengyuan);
        lin = findViewById(R.id.kongjian_chengyuan_lin);
        AppManager.getAppManager().addActivity(this);
        token = getSharedPreferences("user",MODE_PRIVATE).getString("token","");
        root = getSharedPreferences("user",MODE_PRIVATE).getString("root","");
        fanhui = findViewById(R.id.kj_chengyuan_fanhui);
        fanhui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        x.Ext.init(getApplication());
        Intent intent = getIntent();
        groupid = intent.getStringExtra("groupid");
        init();
    }

    Handler handler = new MyHandler(this){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==1){
                Log.w("getgroupuser",msg.obj.toString());
                try {
                    String json = msg.obj.toString();
                    //String json = "{\"datum\":[{\"user_avatar\":\"/images/defaultUserAvatar.jpg\",\"user_id\":9,\"user_name\":\"有一生\"},{\"user_avatar\":\"/images/defaultUserAvatar.jpg\",\"user_id\":9,\"user_name\":\"有一生\"},{\"user_avatar\":\"/images/defaultUserAvatar.jpg\",\"user_id\":9,\"user_name\":\"有一生\"},{\"user_avatar\":\"/images/defaultUserAvatar.jpg\",\"user_id\":9,\"user_name\":\"有一生\"},{\"user_avatar\":\"/images/defaultUserAvatar.jpg\",\"user_id\":9,\"user_name\":\"有一生\"},{\"user_avatar\":\"/images/defaultUserAvatar.jpg\",\"user_id\":9,\"user_name\":\"有一生\"},{\"user_avatar\":\"/images/defaultUserAvatar.jpg\",\"user_id\":9,\"user_name\":\"有一生\"},{\"user_avatar\":\"/images/defaultUserAvatar.jpg\",\"user_id\":9,\"user_name\":\"有一生\"},{\"user_avatar\":\"/images/defaultUserAvatar.jpg\",\"user_id\":9,\"user_name\":\"有一生\"},{\"user_avatar\":\"/images/defaultUserAvatar.jpg\",\"user_id\":9,\"user_name\":\"有一生\"},{\"user_avatar\":\"/images/defaultUserAvatar.jpg\",\"user_id\":9,\"user_name\":\"有一生\"},{\"user_avatar\":\"/images/defaultUserAvatar.jpg\",\"user_id\":9,\"user_name\":\"有一生\"},{\"user_avatar\":\"/images/defaultUserAvatar.jpg\",\"user_id\":9,\"user_name\":\"有一生\"},{\"user_avatar\":\"/images/defaultUserAvatar.jpg\",\"user_id\":9,\"user_name\":\"有一生\"},{\"user_avatar\":\"/images/defaultUserAvatar.jpg\",\"user_id\":9,\"user_name\":\"有一生\"},{\"user_avatar\":\"/images/defaultUserAvatar.jpg\",\"user_id\":9,\"user_name\":\"有一生\"},{\"user_avatar\":\"/images/defaultUserAvatar.jpg\",\"user_id\":9,\"user_name\":\"有一生\"},{\"user_avatar\":\"/images/defaultUserAvatar.jpg\",\"user_id\":9,\"user_name\":\"有一生\"},{\"user_avatar\":\"/images/defaultUserAvatar.jpg\",\"user_id\":9,\"user_name\":\"有一生\"},{\"user_avatar\":\"/images/defaultUserAvatar.jpg\",\"user_id\":9,\"user_name\":\"有一生\"},{\"user_avatar\":\"/images/defaultUserAvatar.jpg\",\"user_id\":9,\"user_name\":\"有一生\"},{\"user_avatar\":\"/images/defaultUserAvatar.jpg\",\"user_id\":9,\"user_name\":\"有一生\"}],\"code\":1,\"message\":\"查询成功\"}";
                    JSONObject obj = new JSONObject(json);
                    String code = obj.getString("code");
                    if (code.equals("1")){
                        JSONArray datum = obj.getJSONArray("datum");
                        DisplayMetrics dm = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(dm);
                        int width = dm.widthPixels/4;
                        LinearLayout lin2 = new LinearLayout(getApplicationContext());
                        LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        lin2.setLayoutParams(param1);
                        lin2.setOrientation(LinearLayout.HORIZONTAL);
                        for (int i=0;i<datum.length();i++){
                            String user_avatar = datum.getJSONObject(i).getString("user_avatar");
                            final String user_id = datum.getJSONObject(i).getString("user_id");
                            String user_name = datum.getJSONObject(i).getString("user_name");
                            LinearLayout lin1 = new LinearLayout(getApplicationContext());
                            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            lin1.setLayoutParams(param);
                            lin1.setPadding(10,10,10,10);
                            lin1.setOrientation(LinearLayout.VERTICAL);
                            lin1.setGravity(Gravity.CENTER);
                            lin1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (!token.equals("")){
                                        Intent intent = new Intent();
                                        intent.setClass(Kongjian_ChengYuan.this, Other_index.class);
                                        intent.putExtra("user_id",user_id);
                                        startActivity(intent);
                                    }else {
                                        Toast.makeText(Kongjian_ChengYuan.this,"请先登录",Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                            CircularImage img = new CircularImage(getApplicationContext());
                            LinearLayout.LayoutParams param_img = new LinearLayout.LayoutParams(width-20, width-20);
                            img.setLayoutParams(param_img);
                            //x.image().bind(img,root+user_avatar);
                            Glide.with(getApplicationContext()).load(root+user_avatar).into(img);
                            TextView text = new TextView(getApplicationContext());
                            text.setLayoutParams(param);
                            text.setText(user_name);
                            text.setTextColor(Color.BLACK);
                            lin1.addView(img);
                            lin1.addView(text);
                            lin2.addView(lin1);
                            if (i!=0 &&(i+1)%4==0){
                                lin.addView(lin2);
                                lin2 = new LinearLayout(getApplicationContext());
                                lin2.setLayoutParams(param1);
                                lin2.setOrientation(LinearLayout.HORIZONTAL);
                            }
                            if (i==datum.length()-1&&datum.length()%4!=0){
                                lin.addView(lin2);
                            }
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
                String url = IP+"lifetime/group/account/getGroupUser?token="+token+"&groupId="+groupid;
                String  result =  new NetWorkRequest().getServiceInfo(url);
                Message msg = new Message();
                msg.obj = result;
                msg.what = 1;
                handler.sendMessage(msg);
            }
        }.start();
    }
}
