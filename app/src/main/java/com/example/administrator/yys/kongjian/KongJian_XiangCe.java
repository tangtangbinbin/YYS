package com.example.administrator.yys.kongjian;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.administrator.yys.R;
import com.example.administrator.yys.network.NetWorkRequest;
import com.example.administrator.yys.utils.AppManager;
import com.example.administrator.yys.utils.MyHandler;
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
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;

import me.iwf.photopicker.PhotoPickerActivity;
import me.iwf.photopicker.utils.PhotoPickerIntent;

import static com.example.administrator.yys.utils.IPAddress.IP;

/**
 * Created by Administrator on 2017/7/6 0006.
 */

public class KongJian_XiangCe extends Activity implements View.OnClickListener{
    LinearLayout fanhui;
    TextView shangchuan;
    LinearLayout xiangce;
    String token;
    String password;
    String groupid;
    String root;
    ArrayList<String> urls = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kongjian_xiangce);
        fanhui = findViewById(R.id.kongjian_xiangce_fanhui);
        AppManager.getAppManager().addActivity(this);
        shangchuan = findViewById(R.id.kongjian_xiangce_shangchuan);
        xiangce = findViewById(R.id.kj_xiangce_xclin);
        token = getSharedPreferences("user",MODE_PRIVATE).getString("token","");
        root = getSharedPreferences("user",MODE_PRIVATE).getString("root","");
        x.Ext.init(getApplication());
        Intent intent = getIntent();
        password = intent.getStringExtra("password");
        groupid = intent.getStringExtra("group_id");
        fanhui.setOnClickListener(this);
        shangchuan.setOnClickListener(this);
        init();
    }

    Handler handler = new MyHandler(this){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==1 && msg.obj!=null){
                Log.w("xiangcejson",msg.obj.toString());
                try {
                    JSONObject obj = new JSONObject(msg.obj.toString());
                    String code = obj.getString("code");
                    if (code.equals("1")){
                        JSONObject datum = obj.getJSONObject("datum");
                        JSONArray rows = datum.getJSONArray("rows");
                        DisplayMetrics dm = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(dm);
                        int width = dm.widthPixels/3;
                        Log.w("imgwidth",width+" "+dm.widthPixels);

                        LinearLayout lin =new LinearLayout(getApplicationContext());
                        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        lin.setLayoutParams(param);
                        lin.setOrientation(LinearLayout.HORIZONTAL);
                        for (int i=0;i<rows.length();i++){
                            JSONObject jobj = (JSONObject) rows.get(i);
                            final String url = jobj.getString("photo_url");
                            Log.w("geturl:",url);
                            urls.add(root+url);
                            ImageView img = new ImageView(getApplicationContext());
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width,width);
                            img.setLayoutParams(params);
                            img.setPadding(5,5,5,5);
                            final int finalI = i;
                            img.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent();
                                    intent.setClass(KongJian_XiangCe.this, BigPhoto.class);
                                    intent.putStringArrayListExtra("url",urls);
                                    intent.putExtra("position", finalI);
                                    startActivity(intent);
                                }
                            });
                           // x.image().bind(img,root+url);
                            img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            Glide.with(getApplicationContext()).load(root+url).into(img);
                            lin.addView(img);
                            if (i!=0 && (i+1)%3==0){
                                xiangce.addView(lin);
                                lin = new LinearLayout(getApplicationContext());
                                lin.setLayoutParams(param);
                                lin.setOrientation(LinearLayout.HORIZONTAL);

                            }if (i==rows.length()-1 && rows.length()%3!=0){
                                xiangce.addView(lin);
                            }


                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK ) {
            if (requestCode == 1) {
                Toast.makeText(getApplicationContext(),"上传中...",Toast.LENGTH_SHORT).show();
                RequestParams params = new RequestParams();
                if (data != null) {
                    ArrayList<String> photos =
                            data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
                    for (int i=0;i<photos.size();i++){
                        Log.w("the photo url",photos.get(i).toString());
                        File file = new File(photos.get(i).toString());
                        params.addBodyParameter("upload"+i,file);
                    }
                }

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
                                xiangce.removeAllViews();
                                init();
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

    private String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    private void init() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                String url = IP+"lifetime/group/account/getImgs?token="
                        +token+"&groupId="+groupid+"&password="+password;
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
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.kongjian_xiangce_fanhui:
                finish();
                break;
            case R.id.kongjian_xiangce_shangchuan:
                PhotoPickerIntent intent = new PhotoPickerIntent(KongJian_XiangCe.this);
                intent.setPhotoCount(9);
                intent.setShowCamera(false);
                startActivityForResult(intent, 1);
                break;
        }
    }
}
