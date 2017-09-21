package com.example.administrator.yys.account;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Window;

import com.example.administrator.yys.R;

/**
 * Created by Administrator on 2017/6/15 0015.
 */

public class Account_vfPersion extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.forgetpass_vfpersion);

    }
}
