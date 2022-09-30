package com.example.kafeibei;

import com.example.kafeibei.util.CommonUtils;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void a(){
        Date date = new Date();
        SimpleDateFormat dtf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String last_time = dtf.format(date);
        System.out.println(last_time);
    }


    @Test
    public void b(){
        Calendar calendar = Calendar.getInstance();
        System.out.println(calendar.getTime());
        //从当前日期减去15分钟
        calendar.add(Calendar.MINUTE, -10);
        System.out.println(calendar.getTime());

        //在...之后
        System.out.println(calendar.after(Calendar.getInstance()));

    }
}