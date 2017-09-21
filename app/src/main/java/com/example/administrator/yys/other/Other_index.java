package com.example.administrator.yys.other;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.administrator.yys.R;
import com.example.administrator.yys.fragment.Fragment_other_hudong_gushi;
import com.example.administrator.yys.fragment.Fragment_other_hudong_wenda;
import com.example.administrator.yys.network.NetWorkRequest;
import com.example.administrator.yys.utils.AppManager;
import com.example.administrator.yys.utils.MyHandler;
import com.example.administrator.yys.view.CircularImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.administrator.yys.utils.IPAddress.IP;

/**
 * Created by Administrator on 2017/6/19 0019.
 */

public class Other_index extends Activity implements View.OnClickListener{
    Button guanzhu_btn;
    RadioGroup rg;
    RadioButton rb_gushi;
    RadioButton rb_wenda;
    LinearLayout fanhui;
    String userid;
    String myuserid;
    TextView temp;
    String token,root;
    CircularImage otheravatar;
    TextView othername,otherdesc;
    private FragmentManager fragmentManager;
    private Fragment currentFragment = new Fragment();
    private List<Fragment> fragments = new ArrayList<>();

    private int currentIndex = 0;
    //当前显示的fragment
    private static final String CURRENT_FRAGMENT = "STATE_FRAGMENT_SHOW";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_index);
        AppManager.getAppManager().addActivity(this);
        fragmentManager = getFragmentManager();
        otheravatar = findViewById(R.id.other_avatar);
        othername = findViewById(R.id.other_id);
        otherdesc = findViewById(R.id.other_desc);
        guanzhu_btn = (Button) findViewById(R.id.other_guanzhu);
        rg = (RadioGroup) findViewById(R.id.other_rg);
        fanhui = (LinearLayout) findViewById(R.id.other_fanhui);
        rb_gushi = (RadioButton) findViewById(R.id.other_gushi);
        temp = findViewById(R.id.other_temp);
        rb_wenda = (RadioButton) findViewById(R.id.other_wenda);
        guanzhu_btn.setOnClickListener(this);
        token = getSharedPreferences("user",MODE_PRIVATE).getString("token","");
        root = getSharedPreferences("user",MODE_PRIVATE).getString("root","");
        myuserid = getSharedPreferences("user",MODE_PRIVATE).getString("user_id","");
        Intent intent = getIntent();
        userid = intent.getStringExtra("user_id");

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i){
                    case R.id.other_gushi:
                        currentIndex = 0;
                        break;
                    case R.id.other_wenda:
                        currentIndex = 1;
                        break;
                }
                showFragment();
            }
        });
        if (savedInstanceState != null) { // “内存重启”时调用

            //获取“内存重启”时保存的索引下标
            currentIndex = savedInstanceState.getInt(CURRENT_FRAGMENT,0);

            fragments.removeAll(fragments);
            fragments.add(fragmentManager.findFragmentByTag(0+""));
            fragments.add(fragmentManager.findFragmentByTag(1+""));

            //恢复fragment页面
            restoreFragment();

        }else{      //正常启动时调用
            fragments.add(new Fragment_other_hudong_gushi(userid));
            fragments.add(new Fragment_other_hudong_wenda(userid));
            showFragment();
        }
        fanhui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if (userid!=null&&userid.equals(myuserid)){
            guanzhu_btn.setVisibility(View.GONE);
            temp.setVisibility(View.GONE);
        }
        initview();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //“内存重启”时保存当前的fragment名字
        outState.putInt(CURRENT_FRAGMENT,currentIndex);
        super.onSaveInstanceState(outState);
    }

    Handler handler = new MyHandler(this){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1){
                try {
                    JSONObject obj = new JSONObject(msg.obj.toString());
                    String code = obj.getString("code");
                    if (code.equals("1")){
                        JSONObject datum = obj.getJSONObject("datum");
                        String  isConcern = datum.getString("isConcern");
                        if (isConcern.equals("true")){
                            guanzhu_btn.setText("已关注");
                            guanzhu_btn.setClickable(false);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
               }
            }
            if (msg.what==2){
                Log.w("guanzhu",msg.obj.toString());
                try {
                    JSONObject obj = new JSONObject(msg.obj.toString());
                    String code = obj.getString("code");
                    if (code.equals("1")){
                        Toast.makeText(getApplicationContext(),obj.getString("message"),Toast.LENGTH_SHORT).show();
                        guanzhu_btn.setText("已关注");
                        guanzhu_btn.setClickable(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (msg.what==3){
                Log.w("getuserinfobyid",msg.obj.toString());
                try {
                    JSONObject obj = new JSONObject(msg.obj.toString());
                    String code = obj.getString("code");
                    if (code.equals("1")){
                        JSONObject datum = obj.getJSONObject("datum");
                        String useravatar = datum.getString("user_avatar");
                        String username = datum.getString("user_name");
                        String personal_signature = datum.getString("personal_signature");
                        Glide.with(getApplicationContext()).load(root+useravatar).into(otheravatar);
                        othername.setText(username);
                        otherdesc.setText(personal_signature);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    private void initview() {

        new Thread(){
            @Override
            public void run() {
                super.run();
                String url2 = IP+"lifetime/concern/account/isConcern?concernId="+userid+"&token="+token;
                String  result1 =  new NetWorkRequest().getServiceInfo(url2);
                Message msg2 = new Message();
                msg2.obj = result1;
                msg2.what = 1;
                handler.sendMessage(msg2);

                String url3 = IP+"lifetime/user/account/getUserInfoById?userId="+userid;
                String  result2 =  new NetWorkRequest().getServiceInfo(url3);
                Message msg3 = new Message();
                msg3.obj = result2;
                msg3.what = 3;
                handler.sendMessage(msg3);
            }
        }.start();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.other_guanzhu:
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        String url1 = IP+"lifetime/concern/account/addConcern?concernId="+userid+"&token="+token;
                        String  result =  new NetWorkRequest().getServiceInfo(url1);
                        Message msg1 = new Message();
                        msg1.obj = result;
                        msg1.what = 2;
                        handler.sendMessage(msg1);
                    }
                }.start();
                break;
        }
    }

    /**
     * 使用show() hide()切换页面
     * 显示fragment
     */
    private void showFragment(){

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        //如果之前没有添加过
        if(!fragments.get(currentIndex).isAdded()){
            transaction
                    .hide(currentFragment)
                    .add(R.id.other_content,fragments.get(currentIndex),""+currentIndex);  //第三个参数为添加当前的fragment时绑定一个tag

        }else{
            transaction
                    .hide(currentFragment)
                    .show(fragments.get(currentIndex));
        }

        currentFragment = fragments.get(currentIndex);

        transaction.commit();

    }

    /**
     * 恢复fragment
     */
    private void restoreFragment(){


        FragmentTransaction mBeginTreansaction = fragmentManager.beginTransaction();

        for (int i = 0; i < fragments.size(); i++) {

            if(i == currentIndex){
                mBeginTreansaction.show(fragments.get(i));
            }else{
                mBeginTreansaction.hide(fragments.get(i));
            }

        }

        mBeginTreansaction.commit();

        //把当前显示的fragment记录下来
        currentFragment = fragments.get(currentIndex);

    }
}
