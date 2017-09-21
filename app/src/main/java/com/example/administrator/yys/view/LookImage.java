package com.example.administrator.yys.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.example.administrator.yys.R;

import org.xutils.x;

/**
 * Created by Administrator on 2017/7/12 0012.
 */

public class LookImage extends Activity {
    LinearLayout lin;
    ImageView img;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lookimg);
        lin = findViewById(R.id.lookimg_lin);
        img = findViewById(R.id.lookimg_img);
        x.Ext.init(getApplication());
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        //x.image().bind(img,url);
        Glide.with(getApplicationContext()).load(url).into(img);
        lin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
