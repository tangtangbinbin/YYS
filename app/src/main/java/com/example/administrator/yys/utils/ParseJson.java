package com.example.administrator.yys.utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/10 0010.
 */

public class ParseJson {
    public String getname(String content){
        String [] str = content.split("/");
        Log.w("the name",str[str.length-1]);
        return str[str.length-1];
    }
    public String gettext(String content,String type){
        String[] str = content.split("＜img＞");
        String text = "";
        for (int i=0;i<str.length;i++){
            if (str[i].length()>=type.length()+2&&!str[i].substring(0,type.length()+2).equals("/"+type+"/")){
                text +=str[i];
            }else if (str[i].length()<8){
                text +=str[i];
            }
        }
        return text.replaceAll("%5Cn","\n");
    }
    public String geturl(String content,String type){
        String[] str = content.split("＜img＞");
        String url = "";
        for (int i=0;i<str.length;i++){
            if (str[i].length()>=type.length()+2&&str[i].substring(0,type.length()+2).equals("/"+type+"/")){
                url = str[i];
                break;
            }
    }
        return url;
    }
    //添加＜img＞
    public String addimg(String content){
        String str1 = ".jpg";
        String str2 = ".png";
        String str3 = ".jpeg";
        int a = 0;
        int b = 0;
        String newcontent1 = content;
        List list1 = new ArrayList();
        List list2 = new ArrayList();
        for (int i=0;i<content.length()-4;i++){
            String temp = content.substring(i,i+4);
            if (temp.equals(str1) ||temp.equals(str2)){
                list1.add(i);
               // Log.w("添加img1","i为："+i+"temp为："+temp);
            }
        }
        for (int i=0;i<content.length()-5;i++){
            String temp = content.substring(i,i+5);
            if (temp.equals(str3)){
                list2.add(i);
            }
        }
        for (int j=0;j<list1.size();j++){
            int i = (int) list1.get(j);
            if ((i+4)<=content.length()-5){
                String temp = content.substring(i+4,i+9);
              //  Log.w("添加img2","i为："+i+"temp为："+temp);
                if (!temp.equals("＜img＞")){
                    StringBuilder sb = new StringBuilder(newcontent1);
                    newcontent1 = sb.insert((i+4)+(a*5),"＜img＞").toString();
                    ++a;
                 //   Log.w("添加img2",sb.substring(i,i+9));
                }
            }else{
                StringBuilder sb = new StringBuilder(newcontent1);
                newcontent1 = sb.insert((i+4)+(a*5),"＜img＞").toString();
               // Log.w("添加img3",sb.substring(i+(a*5),i+(a*5)+9));
            }

        }
        for (int j=0;j<list2.size();j++){
            int i = (int) list2.get(j);
            if ((i+5)<=content.length()-5){
                String temp = content.substring(i+5,i+10);
                if (!temp.equals("＜img＞")){
                    StringBuilder sb = new StringBuilder(newcontent1);
                    newcontent1 = sb.insert((i+5)+(b*5),"＜img＞").toString();
                    ++b;
                }
            }else{
                StringBuilder sb = new StringBuilder(newcontent1);
                newcontent1 = sb.insert((i+5)+(b*5),"＜img＞").toString();
            }

        }
       // Log.w("添加img之后内容",newcontent1);
        return newcontent1;
    }
}
