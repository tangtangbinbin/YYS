package com.example.administrator.yys.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by Administrator on 2017/7/13 0013.
 */

public class InitSpace extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialog();
    }
    protected void dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(InitSpace.this);
        builder.setMessage("免费获取200M空间大小");

        builder.setTitle("提示");

        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Intent intent = new Intent();
                InitSpace.this.setResult(1,intent);
                InitSpace.this.finish();
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Intent intent = new Intent();
                InitSpace.this.setResult(2,intent);
                InitSpace.this.finish();
            }
        });

        builder.setCancelable(false);
        builder.create().show();

    }
}
