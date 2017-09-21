package com.example.administrator.yys.huodong;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.yys.R;
import com.example.administrator.yys.view.MyAutoCompleteTextView;


/**
 * Created by Administrator on 2017/9/13 0013.
 */

public class HuoDong_qinglvzheng_Make extends Activity {
    LinearLayout fanhui;
    MyAutoCompleteTextView auto1,auto2;
    EditText edit1,edit2;
    ImageView img;
    TextView pay;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.huodong_make);
        fanhui = findViewById(R.id.huodong_make_fanhui);
        auto1 = findViewById(R.id.auto1);
        auto2 = findViewById(R.id.auto2);
        edit1 = findViewById(R.id.huodong_edit1);
        pay = findViewById(R.id.huodong_pay);
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
                Intent intent = new Intent();
                intent.setClass(HuoDong_qinglvzheng_Make.this,HuoDong_qinglvzheng_MakeImg.class);
                startActivity(intent);
            }
        });


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
            Bundle extras = data.getExtras();
            Bitmap bitmap = extras.getParcelable("data");
            img.setImageBitmap( bitmap);
        }
    }

    private void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        //下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 3);
        intent.putExtra("aspectY", 2);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 200);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 5);
    }
    private String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
