package com.example.administrator.yys.utils;

/**
 * Created by Administrator on 2017/7/5 0005.
 */

public class CheckJsonType {
    public String checkJsonType(String str,int i){
        String temp = "\""+i+"\""+":";
        int iindex = str.indexOf(temp);
        String content = str.substring(iindex);
        String obj = content.substring(4,5);
        if (obj.equals("[")){
            return "array";
        }
        if (obj.equals("{")){
            return "object";
        }
        return  obj.toString();
    }
}
