package com.example.administrator.yys.wode;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.yys.R;

/**
 * Created by Administrator on 2017/9/12 0012.
 */

public class WoDe_GuanYu extends Activity {
    LinearLayout fanhui;
    TextView version;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wode_guanyu);
        version = findViewById(R.id.guanyu_version);
        fanhui = findViewById(R.id.wode_guanyu_fanhui);
        fanhui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        showVersion();
    }
    private void showVersion() {
        PackageInfo pkg;
        try {
            pkg = getPackageManager().getPackageInfo(getApplication().getPackageName(), 0);
            version.setText(pkg.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
