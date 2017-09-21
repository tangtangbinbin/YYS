package com.example.administrator.yys.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.administrator.yys.R;
import com.example.administrator.yys.account.Login;
import com.example.administrator.yys.kongjian.KongJian_ChuangJian;
import com.example.administrator.yys.kongjian.KongJian_GetPassword;
import com.example.administrator.yys.kongjian.KongJian_Info;
import com.example.administrator.yys.kongjian.KongJian_SetPassword;
import com.example.administrator.yys.kongjian.KongJian_UpdatePassword;
import com.example.administrator.yys.network.NetWorkRequest;
import com.example.administrator.yys.utils.CheckJsonType;
import com.example.administrator.yys.utils.MyHandler;
import com.example.administrator.yys.utils.PxAndDp;
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

import static android.content.Context.MODE_PRIVATE;
import static com.example.administrator.yys.utils.IPAddress.IP;

/**
 * Created by Administrator on 2017/6/14 0014.
 */

public class Fragment_kongjian extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    ImageView img_menu;
    TextView create;
    TextView pass;
    SharedPreferences userinfo;
    String token = "";
    LinearLayout linroot ;
    ListView lv;
    ArrayList<HashMap<String,Object>> list = new ArrayList<>();
    String spacePassword ="";
    Intent passPwd;
    SwipeRefreshLayout swipe;
    MyReceiver receiver;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.kongjian,container,false);
        img_menu = (ImageView) view.findViewById(R.id.kj_menu);
        swipe = view.findViewById(R.id.kongjian_swipe);
        userinfo =  getActivity().getSharedPreferences("user",MODE_PRIVATE);
        linroot = (LinearLayout) view;
        token = userinfo.getString("token","");
        lv = (ListView) view.findViewById(R.id.kj_listview);
        x.Ext.init(getActivity().getApplication());

        receiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.UPDATE_FRAGMENT");
        getActivity().registerReceiver(receiver,intentFilter);
        swipe.setOnRefreshListener(this);
        swipe.setColorSchemeColors(Color.GREEN,Color.BLUE,Color.RED);
        init();
        Log.w("oncreate","fragment_kongjian");
        return view;
    }

    private class MyReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.w("myreceiver","onrecieve");
            String index = intent.getStringExtra("index");
            if (index.equals("kongjian")){
                Log.w("onreceiver","q");
                init();
               /* if (MyApplication.getMyApplicationInstance().getSetpassword()==1){
                    MyApplication.getMyApplicationInstance().setSetpassword(0);
                    Log.w("onreceive","setpasswrod1");
                    Message msg = new Message();
                    msg.what=4;
                    msg.obj = " ";
                    handler1.sendMessage(msg);
                }*/
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case 1:
                final String password = data.getStringExtra("password");
                Log.w("getpassword",password);
                //验证密码
                String url = IP+"lifetime/space/account/validatePwd";
                RequestParams params = new RequestParams();
                params.addBodyParameter("token",token);
                params.addBodyParameter("password",password);
                HttpUtils httputils = new HttpUtils();
                httputils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        Log.w("vpasssuccess:",responseInfo.result.toString());
                        try {
                            JSONObject obj = new JSONObject(responseInfo.result.toString());
                            String code = obj.getString("code");
                            if (code.equals("1")){
                                passPwd.putExtra("password",password);
                                startActivity(passPwd);
                            }if (code.equals("0")){
                                Toast.makeText(getActivity(),obj.getString("message"),Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFailure(HttpException e, String s) {
                        Log.w("vpassfaile",s.toString());
                    }
                });
                break;
            case 3:
                break;
        }
    }

    private void showDialog() {
        Log.w("showdialog","1");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("忘记该密码将不可被找回，请牢记！");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.create().show();
        Log.w("showdialog","2");
    }
    Handler handler1 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==4 ){
                Log.w("handler1","showdialog");
                showDialog();
            }
        }
    };
     Handler handler = new MyHandler(getActivity()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.obj!=null){
                try {
                    JSONObject obj = new JSONObject(msg.obj.toString());
                    String code = obj.getString("code");
                    if (code.equals("422")){
                        Intent intent = new Intent();
                        intent.setClass(getActivity(), Login.class);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Log.w("Message.what",msg.what+"");
            if (msg.obj!=null && msg.what==3 ){
                Log.w("spaceinfo:",msg.obj.toString());
            }


            //没有登录情况下获取到的空间信息
            if (msg.obj!=null && msg.what==2 ){
                Log.w("getGroups",msg.obj.toString());
                try {
                    JSONObject jsonObject = new JSONObject(msg.obj.toString());
                    String code = jsonObject.getString("code");
                    if (code.equals("1")){
                        String root = jsonObject.getJSONObject("constant").getString("resourceServer");
                        JSONArray jsonArray = jsonObject.getJSONObject("info").getJSONArray("groups");
                        list.clear();
                        for (int i=0;i<jsonArray.length();i++){
                            JSONObject jso = (JSONObject) jsonArray.get(i);
                            String color = jso.getString("colour");
                            String group_describe = jso.getString("group_describe");
                            String group_default_id = jso.getString("group_default_id");
                            String group_name = jso.getString("group_name");
                            String group_avatar = jso.getString("group_avatar");
                           HashMap<String,Object> map = new HashMap<>();
                            map.put("group_name",group_name);
                            map.put("group_avatar",root+group_avatar);
                            map.put("color",color);
                            list.add(map);
                        }
                        MyAdapter adapter = new MyAdapter(getActivity());
                        lv.setAdapter(adapter);
                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                Toast.makeText(getActivity(),"请先登录",Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //登录后获取的空间信息
            if (msg.obj!=null && msg.what==1 ){
                Log.w("getGroups",msg.obj.toString());
                try {
                    JSONObject jsonObject = new JSONObject(msg.obj.toString());
                    String code = jsonObject.getString("code");
                    if (code.equals("422")){
                        noLogin();
                    }
                    if (code.equals("1")){
                        String root = userinfo.getString("root","");
                        JSONObject datum = jsonObject.getJSONObject("datum");
                        spacePassword = datum.getString("spacePassword");
                        list.clear();
                        for (int a=0;a<2;a++){
                        for (int i=1;i<=datum.length()-1;i++){
                            if (new CheckJsonType().checkJsonType(datum.toString(),i).equals("object") ){
                                JSONObject num = datum.getJSONObject(i+"");
                                if (a==0){
                                    String color = num.getString("colour");
                                    String group_describe = num.getString("group_describe");
                                    String group_default_id = num.getString("group_default_id");
                                    //String update_time = num.getString("update_time");
                                    String group_name = num.getString("group_name");
                                    String group_id = "";
                                    String group_avatar = num.getString("group_avatar");
                                    HashMap<String,Object> map = new HashMap<>();
                                    map.put("group_name",group_name);
                                    map.put("update_time","");
                                    map.put("group_avatar",root+group_avatar);
                                    map.put("group_describe",group_describe);
                                    map.put("group_default_id",group_default_id);
                                    map.put("group_id",group_id);
                                    map.put("color",color);
                                   list.add(map);
                                }

                            }
                            if (new CheckJsonType().checkJsonType(datum.toString(),i).equals("array") ){
                                JSONArray array = datum.getJSONArray(i+"");
                                if (array.length()-1>=a){
                                    JSONObject num = array.getJSONObject(a);
                                    String color = num.getString("colour");
                                    String group_describe = num.getString("group_describe");
                                    String update_time = num.getString("update_time");
                                    String group_id = num.getString("group_id");
                                    String group_name = num.getString("group_name");
                                    String group_default_id = num.getString("group_default_id");
                                    String group_avatar = num.getString("group_avatar");
                                    HashMap<String,Object> map = new HashMap<>();
                                    map.put("group_name",group_name);
                                    map.put("update_time",update_time);
                                    map.put("group_avatar",root+group_avatar);
                                    map.put("group_describe",group_describe);
                                    map.put("group_default_id",group_default_id);
                                    map.put("group_id",group_id);
                                    map.put("color",color);
                                    list.add(map);
                                }

                            }

                        }
                        }
                        MyAdapter adapter = new MyAdapter(getActivity());
                        Log.w("List大小",list.size()+"");
                        lv.setAdapter(adapter);
                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                               /* new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String url = IP+"lifetime/space/account/getSpaceInfo?token="+token;
                                        String  result =  new NetWorkRequest().getServiceInfo(url);
                                        Message msg = new Message();
                                        msg.obj = result;
                                        msg.what = 3;
                                        handler.sendMessage(msg);
                                    }
                                }).start();*/

                                Intent intent1 = new Intent();
                                intent1.setClass(getActivity(),KongJian_Info.class);
                                intent1.putExtra("group_describe",list.get(i).get("group_describe").toString());
                                intent1.putExtra("update_time",list.get(i).get("update_time").toString());
                                intent1.putExtra("group_avatar",list.get(i).get("group_avatar").toString());
                                intent1.putExtra("group_default_id",list.get(i).get("group_default_id").toString());
                                intent1.putExtra("group_id",list.get(i).get("group_id").toString());
                                intent1.putExtra("group_name",list.get(i).get("group_name").toString());
                                passPwd = intent1;
                                intent1.putExtra("password","");
                            //判断进入空间是否需要密码
                               if (spacePassword.equals("false")){
                                   startActivity(intent1);
                               }else if (spacePassword.equals("true")){
                                    Intent intent = new Intent();
                                   intent.setClass(getActivity(),KongJian_GetPassword.class);
                                   startActivityForResult(intent,0);
                               }

                            }
                        });

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    };

    @Override
    public void onRefresh() {
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

    private class MyAdapter extends BaseAdapter{

        private LayoutInflater mInflater;//得到一个LayoutInfalter对象用来导入布局

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
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view ==null){
                view = mInflater.inflate(R.layout.kongjian_listview_item,null);
                holder = new ViewHolder();
                holder.linearLayout = (LinearLayout) view.findViewById(R.id.kj_view_lin);
                holder.text = (TextView) view.findViewById(R.id.kj_view_text);
                holder.img = (ImageView) view.findViewById(R.id.kj_view_img);
                view.setTag(holder);
            }else{
                holder = (ViewHolder) view.getTag();
            }
            holder.linearLayout.setBackgroundColor(Color.parseColor( list.get(i).get("color").toString()));
            //x.image().bind(holder.img,list.get(i).get("group_avatar").toString());
            Glide.with(getActivity()).load(list.get(i).get("group_avatar").toString()).into(holder.img);
            holder.text.setText(list.get(i).get("group_name").toString());
            return view;
        }
    }


    public final class ViewHolder{
        public TextView text;
        public ImageView img;
        public LinearLayout linearLayout;
    }
    private void init() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                String token = userinfo.getString("token","");
                if (isLogin()){
                    String url = IP+"lifetime/group/account/getGroups?token="+token;
                    String  result =  new NetWorkRequest().getServiceInfo(url);
                    Message msg = new Message();
                    msg.obj = result;
                    msg.what = 1;
                    handler.sendMessage(msg);
               }else{
                    noLogin();
                }

            }
        }).start();
    }

    public void noLogin(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences.Editor editor = userinfo.edit();
                editor.putString("token","");
                editor.commit();
                String url = IP+"lifetime/group/account/getDefaultGroups";
                String  result =  new NetWorkRequest().getServiceInfo(url);
                Message msg = new Message();
                msg.obj = result;
                msg.what = 2;
                handler.sendMessage(msg);
            }
        }).start();
    }

    public boolean isLogin(){
        String token = userinfo.getString("token","");
        if (token.equals("")){
            return false;
        }else{
            return true;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        img_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupWindow(view);
            }
        });

    }

    private void showPopupWindow(View view) {
        final View contentview = LayoutInflater.from(getActivity()).inflate(R.layout.kj_menu_popupwindow,null);
        TextView create = (TextView) contentview.findViewById(R.id.kj_create);
        TextView pass = (TextView) contentview.findViewById(R.id.kj_password);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLogin()){
                    Intent intent = new Intent();
                    intent.setClass(getActivity(),KongJian_ChuangJian.class);
                    contentview.setVisibility(View.GONE);
                    startActivity(intent);
                }else{
                    Toast.makeText(getActivity(),"请先登录",Toast.LENGTH_SHORT).show();
                }

            }
        });
        pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLogin()){
                    if (spacePassword.equals("true")){
                        Intent intent = new Intent();
                        intent.setClass(getActivity(),KongJian_UpdatePassword.class);
                        contentview.setVisibility(View.GONE);
                        startActivity(intent);
                    }else{
                        Intent intent = new Intent();
                        intent.setClass(getActivity(),KongJian_SetPassword.class);
                        contentview.setVisibility(View.GONE);
                        getActivity().startActivityForResult(intent,0);
                    }

                }else{
                    Toast.makeText(getActivity(),"请先登录",Toast.LENGTH_SHORT).show();
                }
            }
        });
        PopupWindow popupWindow = new PopupWindow(contentview, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,true);
        popupWindow.setTouchable(true);
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });
        popupWindow.setBackgroundDrawable(getResources().getDrawable(
                R.drawable.shape_popupwindow));
        Log.w("the location",PxAndDp.dip2px(getActivity(),50)+" "+getActivity().getResources().getDisplayMetrics().density);
        popupWindow.showAtLocation(view, Gravity.RIGHT |Gravity.TOP,20, PxAndDp.dip2px(getActivity(),50));

    }

}
