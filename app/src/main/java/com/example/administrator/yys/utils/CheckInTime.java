package com.example.administrator.yys.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2017/6/27 0027.
 */

public class CheckInTime {
    //检查更新时间是否在当天6点到第二天6点之间
    public Boolean checkInTime(String time) {
        Calendar cal1 = Calendar.getInstance();
        Long currenttime1=0L;
        try {
            cal1.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time) );
            currenttime1=cal1.getTimeInMillis();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date currentTime = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String daydate = simpleDateFormat.format(currentTime);
        Date date = null;
        try {
            date = simpleDateFormat.parse(daydate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        Long time1 = cal.getTimeInMillis();
        Long firsttime = time1+(6*60*60*1000);
        Long secondtime = time1+(30*60*60*1000);
        if (currenttime1>firsttime&&currenttime1<secondtime){
            return true;
        }
        return false;
    }
}
