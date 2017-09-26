package com.example.administrator.yys.utils;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.mob.MobSDK;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Administrator on 2017/8/23 0023.
 */

public class MyApplication extends Application {
    private int musicindex = 0;
    private int kongjianindex = 0;
    private int hudongindex = 0;
    private int wodeindex = 0;
    private int musicispause = 0;
    private int musiccurrent = 0;
    private int setpassword = 0;
    private int needrefresh = 0;

    public int getNeedrefresh() {
        return needrefresh;
    }

    public void setNeedrefresh(int needrefresh) {
        this.needrefresh = needrefresh;
    }

    public int getSetpassword() {
        return setpassword;
    }

    public void setSetpassword(int setpassword) {
        this.setpassword = setpassword;
    }

    public int getMusicispause() {
        return musicispause;
    }

    public void setMusicispause(int musicispause) {
        this.musicispause = musicispause;
    }

    public int getMusiccurrent() {
        return musiccurrent;
    }

    public void setMusiccurrent(int musiccurrent) {
        this.musiccurrent = musiccurrent;
    }

    private static final MyApplication instance = new MyApplication();
    public void setMusicindex(int musicindex) {
        this.musicindex = musicindex;
    }

    public void setKongjianindex(int kongjianindex) {
        this.kongjianindex = kongjianindex;
    }

    public void setHudongindex(int hudongindex) {
        this.hudongindex = hudongindex;
    }

    public void setWodeindex(int wodeindex) {
        this.wodeindex = wodeindex;
    }

    public int getMusicindex() {
        return musicindex;
    }

    public int getKongjianindex() {
        return kongjianindex;
    }

    public int getHudongindex() {
        return hudongindex;
    }

    public int getWodeindex() {
        return wodeindex;
    }

    protected String a() {
        return null;
    }

    protected String b() {
        return null;
    }
    public static MyApplication getMyApplicationInstance(){
        return  instance;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        MobSDK.init(this, this.a(), this.b());
        Context context = getApplicationContext();
// 获取当前包名
        String packageName = context.getPackageName();
// 获取当前进程名
        String processName = getProcessName(android.os.Process.myPid());
// 设置是否为上报进程
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
        strategy.setUploadProcess(processName == null || processName.equals(packageName));
        CrashReport.initCrashReport(getApplicationContext(), "622657d06a", true,strategy);
    }

    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    private static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }
}
