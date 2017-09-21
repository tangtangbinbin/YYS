package com.example.administrator.yys.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.administrator.yys.R;
import com.jude.rollviewpager.OnItemClickListener;
import com.jude.rollviewpager.adapter.StaticPagerAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Administrator on 2017/8/22 0022.
 */

public class MyRollViewPager extends Activity {
    String[] urls = new String[]{};
    private RollPagerView rollPagerView;
    private LruCache<String,Bitmap> mLruCaches;    //使用LruCahe缓存，可以节省流量，速度也快
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myrollviewpager);
        Intent intent = getIntent();
        urls =  intent.getStringArrayExtra("url");
        rollPagerView=  findViewById(R.id.id_rollviewpager);
        rollPagerView.setAdapter(new TestNomalAdapter());

        //这里是rollviewpager的点击事件
        rollPagerView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                finish();
            }
        });
        //设置缓存
        int maxMemory= (int) Runtime.getRuntime().maxMemory();
        int cacheSize=maxMemory/4;
        mLruCaches=new LruCache<String,Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }
    private class TestNomalAdapter extends StaticPagerAdapter {

        @Override
        public View getView(ViewGroup container, int position) {
            ImageView view = new ImageView(container.getContext());
            //view.setImageResource(R.mipmap.ic_launcher);   //先加载一个默认的图片，当网络不给力时，有个过度效果
            Bitmap bitmap=getBitmapFromCache(urls[position]);   //先从缓存中读取图片
            if(bitmap!=null){
                view.setImageBitmap(bitmap);  //如果缓存中有图片，就直接放上去行了
            }else {
                //缓存中没有，只要去异步下载了
                AddbBitmapToView addbBitmapToView=new AddbBitmapToView(view);
                addbBitmapToView.execute(urls[position]);
            }
            view.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return view;
        }

        @Override
        public int getCount() {
            return urls.length;
        }
    }

    //这里使用Asyn异步的方式加载网络图片
    class AddbBitmapToView extends AsyncTask<String,Void,Bitmap> {
        private ImageView imageView;
        public AddbBitmapToView(ImageView  imageView){
            this.imageView=imageView;
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if(bitmap!=null){
                imageView.setImageBitmap(bitmap);
            }
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap=getPicture(params[0]);
            if(bitmap!=null){
                addBitmapToCache(params[0],bitmap);
            }
            return bitmap;
        }
    }

    //将图片增加到缓存中
    private void addBitmapToCache(String url,Bitmap bitmap){
        if(getBitmapFromCache(url)==null){   //如果没有缓存，则增加到缓存中
            mLruCaches.put(url,bitmap);
        }
    }
    //从缓存中获取图片
    private Bitmap getBitmapFromCache(String url){
        return mLruCaches.get(url);
    }
    //网络地址获取图片
    private Bitmap getPicture(String path){
        Bitmap bm=null;
        InputStream is;
        try{
            URL url=new URL(path);
            URLConnection connection=url.openConnection();
            connection.connect();
            is=connection.getInputStream();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;//图片宽高都为原来的二分之一，即图片为原来的四分之一
            bm= BitmapFactory.decodeStream(is,null,options);
            is.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bm;
    }

}
