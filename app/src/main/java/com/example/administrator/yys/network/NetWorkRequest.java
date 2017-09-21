package com.example.administrator.yys.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2017/6/15 0015.
 */

public class NetWorkRequest {
    //传入url
    public String getServiceInfo(String str){
        HttpURLConnection conn;
        try {
            conn = (HttpURLConnection) new URL(str).openConnection();
            conn.setConnectTimeout(5000);
            conn.connect();

            if (conn.getResponseCode()==200){
                InputStream is = conn.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String inputLine = "";
                String resultData = "";
                while((inputLine = br.readLine())!=null){
                    resultData+=inputLine+"\n";
                }
                return  resultData;
            }else{
                 return "服务器连接失败";
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
