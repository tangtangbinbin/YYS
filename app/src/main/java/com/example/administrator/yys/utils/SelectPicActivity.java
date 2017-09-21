package com.example.administrator.yys.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.yys.R;

import java.io.File;

/**
 * Created by Administrator on 2017/7/27 0027.
 */

public class SelectPicActivity extends Activity implements View.OnClickListener {

    /***
     * 使用照相机拍照获取图片
     */
    public static final int SELECT_PIC_BY_TACK_PHOTO = 1;
    /***
     * 使用相册中的图片
     */
    public static final int SELECT_PIC_BY_PICK_PHOTO = 2;

    /***
     * 从Intent获取图片路径的KEY
     */
    public static final String KEY_PHOTO_PATH = "photo_path";
    private static final String TAG = "SelectPicActivity";


    /**获取到的图片路径*/
    private String picPath;

    private Intent lastIntent ;

    private Uri photoUri;
    TextView canccel,take_photo,pick_photo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_pic);
        canccel = findViewById(R.id.select_cancel);
        take_photo = findViewById(R.id.select_take_photo);
        pick_photo = findViewById(R.id.select_pick_photo);

        canccel.setOnClickListener(this);
        take_photo.setOnClickListener(this);
        pick_photo.setOnClickListener(this);
        initView();
    }
    /**
     * 初始化加载View
     */
    private void initView() {


        lastIntent = getIntent();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.select_cancel:
                finish();
                break;
            case R.id.select_take_photo:
                takePhoto();
                break;
            case R.id.select_pick_photo:
                pickPhoto();
                break;

        }
    }

    /**
     * 拍照获取图片
     */
    private void takePhoto() {
//执行拍照前，应该先判断SD卡是否存在
        String SDState = Environment.getExternalStorageState();
        if(SDState.equals(Environment.MEDIA_MOUNTED))
        {

            /**
             * 最后一个参数是文件夹的名称，可以随便起
             */
            File file=new File(Environment.getExternalStorageDirectory(),"youyisheng");
            if(!file.exists()){
                file.mkdir();
            }
            /**
             * 这里将时间作为不同照片的名称
             */
            File output=new File(file,System.currentTimeMillis()+".jpg");

            /**
             * 如果该文件夹已经存在，则删除它，否则创建一个
             */
            try {
                if (output.exists()) {
                    output.delete();
                }
                output.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
            /**
             * 隐式打开拍照的Activity，并且传入CROP_PHOTO常量作为拍照结束后回调的标志
             * 将文件转化为uri
             */
            photoUri = Uri.fromFile(output);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, SELECT_PIC_BY_TACK_PHOTO);
        }else{
            Toast.makeText(this,"内存卡不存在", Toast.LENGTH_LONG).show();
        }
    }

    /***
     * 从相册中取图片
     */
    private void pickPhoto() {
        Intent intent = new Intent();
        /*intent.setType("image*//*");
        intent.setAction(Intent.ACTION_GET_CONTENT);*/
        intent.setAction(Intent.ACTION_PICK);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, SELECT_PIC_BY_PICK_PHOTO);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return super.onTouchEvent(event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK)
        {
            doPhoto(requestCode,data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 选择图片后，获取图片的路径
     * @param requestCode
     * @param data
     */
    private void doPhoto(int requestCode,Intent data)
    {
        if(requestCode == SELECT_PIC_BY_PICK_PHOTO ) //从相册取图片，有些手机有异常情况，请注意
        {
            Intent intent = new Intent();
            intent.setData(data.getData());
            SelectPicActivity.this.setResult(1, intent);
            finish();
        }
        if (requestCode==SELECT_PIC_BY_TACK_PHOTO){

            Intent intent = new Intent();
                intent.setData(photoUri);
                SelectPicActivity.this.setResult(2, intent);
                finish();

        }


    }
}
