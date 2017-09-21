package com.example.administrator.yys.kongjian;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.administrator.yys.view.BigPhoto;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.iwf.photopicker.PhotoPickerActivity;
import me.iwf.photopicker.utils.PhotoPickerIntent;

import static com.example.administrator.yys.utils.IPAddress.IP;

/**
 * Created by Administrator on 2017/9/4 0004.
 */

public class KongJian_XiangCe2 extends Activity implements View.OnClickListener{
    ListView lv;
    String token;
    String password;
    String groupid;
    String root;
    int page = 1;
    int itemnumber = 0;
    int currentindex = 0;
    LinearLayout fanhui;
    TextView shangchuan;
    ArrayList<String> urls = new ArrayList<>();
    ArrayList<List<String>> list = new ArrayList<>();
    int usedsize,totalsize,useingsize;
    LinearLayout progress;
    int progressflag = 1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kongjian_xiangce2);
        lv =  findViewById(R.id.kongjian_xiangce2_listview);
        fanhui = findViewById(R.id.kongjian_xiangce2_fanhui);
        AppManager.getAppManager().addActivity(this);
        shangchuan = findViewById(R.id.kongjian_xiangce2_shangchuan);
        fanhui.setOnClickListener(this);
        progress = findViewById(R.id.xiangce_progress);
        shangchuan.setOnClickListener(this);
        token = getSharedPreferences("user",MODE_PRIVATE).getString("token","");
        root = getSharedPreferences("user",MODE_PRIVATE).getString("root","");
        Intent intent = getIntent();
        password = intent.getStringExtra("password");
        groupid = intent.getStringExtra("group_id");
        initview();
        getSpaceSize();
    }

    private void getSpaceSize(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                String url = IP+"lifetime/group/account/getUsedInfo?token="+token+"&groupId="+groupid;
                String  result =  new NetWorkRequest().getServiceInfo(url);
                Message msg = new Message();
                msg.obj = result;
                msg.what = 2;
                handler.sendMessage(msg);
            }
        }.start();
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==1&&msg.obj!=null){
                Log.w("xiangcejson",msg.obj.toString());
                JSONObject obj = null;
                try {
                    obj = new JSONObject(msg.obj.toString());
                    String code = obj.getString("code");
                    if (code.equals("1")) {
                        JSONObject datum = obj.getJSONObject("datum");
                        JSONArray rows = datum.getJSONArray("rows");
                        List<String> listitem = new ArrayList<>();
                        for (int i=0;i<rows.length();i++){
                            JSONObject jobj = (JSONObject) rows.get(i);
                            final String url = jobj.getString("photo_url");
                            Log.w("geturl:",url);
                            urls.add(root+url);
                            listitem.add(root+url);
                            if (i!=0&&(i+1)%3==0){
                                list.add(listitem);
                                listitem=new ArrayList<>();
                            }
                            if (i==rows.length()-1 && rows.length()%3!=0){
                                list.add(listitem);
                            }

                        }
                        //list = inverted(list);
                        if (rows.length()>0){
                            MyAdapter adapter = new MyAdapter(getApplicationContext());
                            adapter.notifyDataSetChanged();
                            lv.setAdapter(adapter);
                            lv.setSelection(itemnumber);
                            lv.setDivider(null);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            if (msg.what==2&& msg.obj!=null){
                Log.w("getusedinfo",msg.obj.toString());
                try {
                    JSONObject obj = new JSONObject(msg.obj.toString());
                    String code = obj.getString("code");
                    if (code.equals("1")){
                        JSONObject datum = obj.getJSONObject("datum");
                        totalsize = datum.getInt("totalSize");
                        usedsize = datum.getInt("usedSize");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private ArrayList<List<String>> inverted(ArrayList<List<String>> list) {
        ArrayList<List<String>> alist = new ArrayList<>();
        for (int i=0;i<list.size();i++){
            alist.add(list.get(i));
        }
        return  alist;
    }

    private void initview() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                String url = IP+"lifetime/group/account/getImgs?token="
                        +token+"&groupId="+groupid+"&password="+password+"&rows=24&page="+page;
                Log.w("xiangceurl:",url);
                String  result =  new NetWorkRequest().getServiceInfo(url);
                Message msg = new Message();
                msg.obj = result;
                msg.what = 1;
                handler.sendMessage(msg);
            }
        }.start();
    }

    private void showdialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("空间内存不足，请先升级！");
        builder.setTitle("提示");
        builder.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.setCancelable(false);
        builder.create().show();
    }

    private void setprogress() {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                progress.setVisibility(View.GONE);
                if (progressflag==1){
                    Toast.makeText(getApplicationContext(),"发布失败，请重试！",Toast.LENGTH_SHORT).show();
                }
                shangchuan.setClickable(true);
            }
        };
        Handler progresshandler = new Handler();
        progresshandler.postDelayed(runnable,20000);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK ) {
            if (requestCode == 1) {
                RequestParams params = new RequestParams();
                if (data != null) {
                    ArrayList<String> photos =
                            data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
                    for (int i=0;i<photos.size();i++){
                        Log.w("the photo url",photos.get(i).toString());
                        File file = new File(photos.get(i).toString());
                        useingsize+=file.length();
                        params.addBodyParameter("upload"+i,file);
                    }
                }
                if (totalsize-usedsize<useingsize){
                    showdialog();
                    return;
                }
                progress.setVisibility(View.VISIBLE);
                setprogress();
                String url = IP+"lifetime/upload/account/groupPhotoUpload";

                params.addBodyParameter("token",token);
                params.addBodyParameter("groupId",groupid);
                HttpUtils httputils = new HttpUtils();
                httputils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        Log.w("addimgsuccess:",responseInfo.result.toString());
                        try {
                            JSONObject obj = new JSONObject(responseInfo.result.toString());
                            String code = obj.getString("code");
                            if (code.equals("1")){
                                progress.setVisibility(View.GONE);
                                progressflag = 0;
                                if (page==1){
                                    list.clear();
                                    urls.clear();
                                }
                               initview();
                            }
                            Toast.makeText(getApplicationContext(),obj.getString("message"),Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFailure(HttpException e, String s) {

                        Log.w("addimgfaile",s.toString());
                    }
                });
            }
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.kongjian_xiangce2_fanhui:
                finish();
                break;
            case R.id.kongjian_xiangce2_shangchuan:
                PhotoPickerIntent intent = new PhotoPickerIntent(KongJian_XiangCe2.this);
                intent.setPhotoCount(9);
                intent.setShowCamera(false);
                startActivityForResult(intent, 1);
                break;
        }
    }

    public final class ViewHolder{
        public ImageView img1;
        public ImageView img2;
        public ImageView img3;
    }
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
            ViewHolder holder;
            if (view ==null){
                view = mInflater.inflate(R.layout.kongjian_xiangce2_item,null);
                holder = new ViewHolder();
                holder.img1 =  view.findViewById(R.id.xiangce2_img1);
                holder.img2 =  view.findViewById(R.id.xiangce2_img2);
                holder.img3 =  view.findViewById(R.id.xiangce2_img3);
                view.setTag(holder);
            }else{
                holder = (ViewHolder) view.getTag();
            }
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            int width = dm.widthPixels/3;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width,width);
            holder.img1.setLayoutParams(params);
            holder.img2.setLayoutParams(params);
            holder.img3.setLayoutParams(params);
            Log.w("list info",list.toString());
            holder.img1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //设置当前点击的个数
                    currentindex = i*3+1;
                    Log.w("currentindex",i*3+1+"");
                        Intent intent = new Intent();
                        intent.setClass(KongJian_XiangCe2.this, BigPhoto.class);
                        intent.putStringArrayListExtra("url",urls);
                        intent.putExtra("position", i*3);
                        startActivity(intent);
                }
            });
            holder.img2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentindex = i*3+2;
                    Log.w("currentindex",i*3+2+"");
                        Intent intent = new Intent();
                        intent.setClass(KongJian_XiangCe2.this, BigPhoto.class);
                        intent.putStringArrayListExtra("url",urls);
                        intent.putExtra("position", i*3+1);
                        startActivity(intent);
                }
            });
            holder.img3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentindex = i*3+3;
                    Log.w("currentindex",i*3+3+"");
                        Intent intent = new Intent();
                        intent.setClass(KongJian_XiangCe2.this, BigPhoto.class);
                        intent.putStringArrayListExtra("url",urls);
                        intent.putExtra("position", i*3+2);
                        startActivity(intent);
                }
            });
            Log.w("list i",list.size()+" "+i);
            if (list.get(i).size()%3==0){
                Glide.with(getApplicationContext()).load(list.get(i).get(0)).into(holder.img1);
                Glide.with(getApplicationContext()).load(list.get(i).get(1)).into(holder.img2);
                Glide.with(getApplicationContext()).load(list.get(i).get(2)).into(holder.img3);
                if (list.size()==i+1 &&list.size()%8==0){
                    page++;
                    itemnumber = i;
                    loadmore();
                }
            }else if (list.size()==i+1){
                if (list.get(i).size()%3==1){
                    Glide.with(getApplicationContext()).load(list.get(i).get(0)).into(holder.img1);
                    Glide.with(getApplicationContext()).load("").into(holder.img2);
                    Glide.with(getApplicationContext()).load("").into(holder.img3);
                }else if (list.get(i).size()%3==2){
                    Glide.with(getApplicationContext()).load(list.get(i).get(0)).into(holder.img1);
                    Glide.with(getApplicationContext()).load(list.get(i).get(1)).into(holder.img2);
                    Glide.with(getApplicationContext()).load("").into(holder.img3);
                }
            }

            return view;
        }
    }

    private void loadmore() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                String url = IP+"lifetime/group/account/getImgs?token="
                        +token+"&groupId="+groupid+"&password="+password+"&rows=24&page="+page;
                Log.w("xiangceurl:",url);
                String  result =  new NetWorkRequest().getServiceInfo(url);
                Message msg = new Message();
                msg.obj = result;
                msg.what = 1;
                handler.sendMessage(msg);
            }
        }.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        progressflag = 0;
    }
}
