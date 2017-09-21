package com.example.administrator.yys.utils;

import android.app.Application;

import com.mob.MobSDK;

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
    }
}
