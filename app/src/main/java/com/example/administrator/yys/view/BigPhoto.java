package com.example.administrator.yys.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.administrator.yys.R;

import java.util.List;


public class BigPhoto extends AppCompatActivity{
    ImageViewPagerAdapter adapter;
    HackyViewPager pager;
    List<String> list;
    int position = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bigphoto);
        pager = (HackyViewPager) findViewById(R.id.pager);
        Intent intent = getIntent();
        list = intent.getStringArrayListExtra("url");
        position = intent.getIntExtra("position",0);
        Log.w("listinfo",list.toString());
        adapter = new ImageViewPagerAdapter(getSupportFragmentManager(), list);
        pager.setAdapter(adapter);
        pager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() ==MotionEvent.ACTION_DOWN){
                    Log.w("bigontouch","1");
                }
                if (motionEvent.getAction() ==MotionEvent.ACTION_MOVE){
                    Log.w("bigontouch","2");
                }
                if (motionEvent.getAction() ==MotionEvent.ACTION_UP){
                    Log.w("bigontouch","3");
                }
                return false;
            }
        });
        pager.setCurrentItem(position);
    }





}
