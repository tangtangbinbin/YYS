package com.example.administrator.yys.network;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import java.util.List;

/**
 * Created by Administrator on 2017/6/15 0015.
 */

public class NetWorkRequestPost {
    //传入url
    public String getServiceInfo(String url, List<NameValuePair> pairList) {

        HttpResponse response = null;
        try {
            HttpEntity requestHttpEntity = new UrlEncodedFormEntity(
                    pairList, HTTP.UTF_8);
            // URL使用基本URL即可，其中不需要加参数
            HttpPost httpPost = new HttpPost(url);
            // 将请求体内容加入请求中
            httpPost.setEntity(requestHttpEntity);
            // 需要客户端对象来发送请求
            HttpClient httpClient = new DefaultHttpClient();
            // 发送请求
            response = httpClient.execute(httpPost);
            // 显示响应
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  response.toString();
    }
}
