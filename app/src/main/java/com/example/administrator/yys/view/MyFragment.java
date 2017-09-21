package com.example.administrator.yys.view;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;

/**
 * Created by Administrator on 2017/7/21 0021.
 */

public class MyFragment extends Fragment {
    protected Activity mActivity ;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (Activity) context;
    }


}
