package com.example.administrator.yys.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.yys.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2017/8/17 0017.
 */

public class MusicList extends Activity {
    ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
    ListView listView;
    LinearLayout fanhui;
    TextView queding;
    int index = -1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.musiclist);
        listView = findViewById(R.id.musiclist);
        fanhui = findViewById(R.id.musiclist_fanhui);
        queding = findViewById(R.id.musiclist_queding);
        list = scanAllAudioFiles();
        MyAdapter adapter = new MyAdapter(getApplicationContext());
        listView.setAdapter(adapter);
        fanhui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        queding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (index!=-1){
                    String url = list.get(index).get("musicFileUrl").toString();
                    Intent intent = new Intent();
                    intent.putExtra("url",url);
                    MusicList.this.setResult(1,intent);
                    Toast.makeText(getApplicationContext(),"上传中...",Toast.LENGTH_SHORT).show();
                    finish();
                }else {
                    Toast.makeText(getApplicationContext(),"请选择音乐",Toast.LENGTH_SHORT).show();
                }
            }
        });
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
        public View getView(final int i, View view, ViewGroup viewGroup) {
            final ViewHolder holder;
            if (view ==null){
                view = mInflater.inflate(R.layout.musiclist_item,null);
                holder = new ViewHolder();
                holder.text = (TextView) view.findViewById(R.id.music_name);
                holder.checkBox = view.findViewById(R.id.musiclist_checkbox);
                holder.lin = view.findViewById(R.id.musiclist_lin);
                holder.author = view.findViewById(R.id.music_author);
                view.setTag(holder);
            }else{
                holder = (ViewHolder) view.getTag();
            }
           holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
               @Override
               public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                   if (b){
                       index = i;
                       notifyDataSetChanged();
                   }
               }
           });
            if (index==i){
                holder.checkBox.setChecked(true);
            }else {
                holder.checkBox.setChecked(false);
            }
            Log.w("index",index+"");
            holder.text.setText(list.get(i).get("music_file_name").toString());
            holder.text.setTextColor(Color.BLACK);
            holder.author.setText(list.get(i).get("artist").toString());
            holder.author.setTextColor(Color.BLACK);
            return view;
        }
    }
    public final class ViewHolder{
        public TextView text,author;
        public CheckBox checkBox;
        public LinearLayout lin;
    }
    public ArrayList<HashMap<String, Object>> scanAllAudioFiles(){
//生成动态数组，并且转载数据
        ArrayList<HashMap<String, Object>> mylist = new ArrayList<HashMap<String, Object>>();

//查询媒体数据库
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
//遍历媒体数据库
        if(cursor.moveToFirst()){

            while (!cursor.isAfterLast()) {

                //歌曲编号
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                //歌曲标题
                String tilte = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                //歌曲的专辑名：MediaStore.Audio.Media.ALBUM
                String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                //歌曲的歌手名： MediaStore.Audio.Media.ARTIST
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                //歌曲文件的路径 ：MediaStore.Audio.Media.DATA
                String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                //歌曲的总播放时长 ：MediaStore.Audio.Media.DURATION
                int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                //歌曲文件的大小 ：MediaStore.Audio.Media.SIZE
                Long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
                    if (url.substring(url.length()-4,url.length()).equals(".mp3")){
                        HashMap<String, Object> map = new HashMap<String, Object>();
                        map.put("musicId", id);
                        map.put("musicTitle", tilte);
                        map.put("musicFileUrl", url);
                        map.put("artist", artist);
                        map.put("music_file_name", tilte);
                        mylist.add(map);
                    }

                cursor.moveToNext();
            }
        }
        return mylist;
    }
}
