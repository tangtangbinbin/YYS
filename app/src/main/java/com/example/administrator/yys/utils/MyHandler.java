package com.example.administrator.yys.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;

import com.example.administrator.yys.account.Login;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Administrator on 2017/7/20 0020.
 */

public class MyHandler extends Handler {

    private Context context;
    public MyHandler(Context context) {
        this.context = context;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        try {
            if (msg.obj!=null){
                JSONObject obj = new JSONObject(msg.obj.toString());
                String code = obj.getString("code");
                if (code.equals("422")){
                    if (context!=null){
                        SharedPreferences.Editor editor = context.getSharedPreferences("user",MODE_PRIVATE).edit();
                        editor.putString("token","");
                        editor.commit();
                        Intent intent = new Intent();
                        intent.setClass(context, Login.class);
                        intent.putExtra("status","1");
                        context.startActivity(intent);
                    }

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
