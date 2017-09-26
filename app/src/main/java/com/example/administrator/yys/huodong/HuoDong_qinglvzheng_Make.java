package com.example.administrator.yys.huodong;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.yys.R;
import com.example.administrator.yys.network.NetWorkRequest;
import com.example.administrator.yys.view.MyAutoCompleteTextView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;

import static com.example.administrator.yys.utils.IPAddress.IP;


/**
 * Created by Administrator on 2017/9/13 0013.
 */

public class HuoDong_qinglvzheng_Make extends Activity {
    LinearLayout fanhui;
    MyAutoCompleteTextView auto1,auto2;
    EditText edit1,edit2;
    ImageView img;
    TextView pay;
    String token;
    String name1,sex1,name2,sex2;
    String FILEPATH= Environment.getExternalStorageDirectory().getAbsolutePath() + "/youyisheng/";
    String FILENAME = "qinglv.jpg";
    Uri imguri = Uri.fromFile(new File(FILEPATH+FILENAME));
    boolean issave;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.huodong_make);
        fanhui = findViewById(R.id.huodong_make_fanhui);
        auto1 = findViewById(R.id.auto1);
        auto2 = findViewById(R.id.auto2);
        edit1 = findViewById(R.id.huodong_edit1);
        pay = findViewById(R.id.huodong_pay);
        token = getSharedPreferences("user",MODE_PRIVATE).getString("token","");
        img = findViewById(R.id.huodong_make_img);
        edit2 = findViewById(R.id.huodong_edit2);
        auto1.setInputType(InputType.TYPE_NULL);
        auto2.setInputType(InputType.TYPE_NULL);
        String [] arr={" 男"," 女"};
        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,arr);
        auto1.setAdapter(arrayAdapter);
        auto2.setAdapter(arrayAdapter);
        fanhui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        auto1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auto1.setText(" ");
            }
        });
        auto2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auto2.setText(" ");
            }
        });
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,1);
            }
        });
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name1 = edit1.getText().toString().trim();
                name2 = edit2.getText().toString().trim();
                if (auto1.getText().toString().trim().equals("男")){
                    sex1 = "1";
                }else {
                    sex1 = "0";
                }
                if (auto2.getText().toString().trim().equals("男")){
                    sex2 = "1";
                }else {
                    sex2 = "0";
                }
                if (name1.length()<=0 || name2.length()<=0){
                    Toast.makeText(getApplicationContext(),"名字不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (sex1.length()<=0 ||sex2.length()<=0){
                    Toast.makeText(getApplicationContext(),"性别不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (issave){
                    uploadinfo();
                }else {
                    Toast.makeText(getApplicationContext(),"图片不存在",Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1&&msg.obj!=null){
                Log.w("msg1",msg.obj.toString());
                try {
                    JSONObject obj = new JSONObject(msg.obj.toString());
                    String code = obj.getString("code");
                    if (code.equals("1")){

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    };

    public void uploadinfo(){
        String url = IP+"lifetime/activity/lovers/joinActivity";
        RequestParams params = new RequestParams();
        params.addBodyParameter("token",token);
        Log.w("token",token);
        params.addBodyParameter("manName",name1);
        params.addBodyParameter("manSex",sex1);
        params.addBodyParameter("ladyName",name2);
        params.addBodyParameter("ladySex",sex2);
        File file = new File(FILEPATH+FILENAME);
        if (file.exists()){
            params.addBodyParameter("pricture",file);
        }
        HttpUtils httputils = new HttpUtils();
        httputils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                Log.w("qinglvz:",responseInfo.result.toString());
                try {
                    JSONObject obj = new JSONObject(responseInfo.result.toString());
                    String code = obj.getString("code");
                    if (code.equals("1")){
                        new Thread(){
                            @Override
                            public void run() {
                                super.run();
                                String url = IP+"lifetime/activity/lovers/payment?token="+token;
                                String  result =  new NetWorkRequest().getServiceInfo(url);
                                Message msg = new Message();
                                msg.obj = result;
                                msg.what = 1;
                                handler.sendMessage(msg);
                            }
                        }.start();
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
    private Bitmap decodeUriAsBitmap(Uri uri) {
                Bitmap bitmap = null;
                try {
                     // 先通过getContentResolver方法获得一个ContentResolver实例，
                      // 调用openInputStream(Uri)方法获得uri关联的数据流stream
                       // 把上一步获得的数据流解析成为bitmap
                       bitmap = BitmapFactory.decodeStream((getContentResolver().openInputStream(uri)));
                    } catch (FileNotFoundException e) {
                       e.printStackTrace();
                      return null;
                   }
               return bitmap;
           }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK&& requestCode==1 && data!=null){
            Uri originalUri =data.getData();
            startPhotoZoom(originalUri);
        }
        if (requestCode==5&&data!=null){
            img.setScaleType(ImageView.ScaleType.CENTER_CROP);
            if (imguri!=null){
                Bitmap bitmap = decodeUriAsBitmap(imguri);
                img.setImageBitmap( bitmap);
                issave = true;
            }

        }
    }

   /*public boolean  saveBitmap2file(Bitmap bmp,String filename){
        Bitmap.CompressFormat format= Bitmap.CompressFormat.JPEG;
        int quality = 100;
        OutputStream stream = null;
        try {
            stream = new FileOutputStream(FILEPATH+filename);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return bmp.compress(format, quality, stream);
    }*/
    private void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        //下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 3);
        intent.putExtra("aspectY", 2);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 900);
        intent.putExtra("outputY", 600);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imguri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());//图片格式
        intent.putExtra("return-data", false);
        startActivityForResult(intent, 5);
    }

}
