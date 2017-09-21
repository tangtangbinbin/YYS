package com.example.administrator.yys.utils;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;


/**
 * Created by Administrator on 2017/8/3 0003.
 */

public class DownLoadFile {
    public String download(String url1,String name){
        File file=new File(Environment.getExternalStorageDirectory(),"youyisheng");
        if(!file.exists()){
            file.mkdir();
        }
        File output=new File(file,name);
        // 构造URL
        URL url = null;
        try {
            url = new URL(url1);
            // 打开连接
            URLConnection con = url.openConnection();
            // 输入流
            InputStream is = con.getInputStream();
            // 1K的数据缓冲
            byte[] bs = new byte[1024];
            // 读取到的数据长度
            int len;
            // 输出的文件流
            OutputStream os = new FileOutputStream(output.getPath());
            // 开始读取
            while ((len = is.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
            // 完毕，关闭所有链接
            os.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return output.getPath();
    }
}
