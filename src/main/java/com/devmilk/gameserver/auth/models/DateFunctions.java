package com.devmilk.gameserver.auth.models;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

public class DateFunctions {

    public static Long getNow(){
        return (new Date()).getTime();
    }

    public static Long getCurrentDay(){
        ZonedDateTime utcTime = ZonedDateTime.now(ZoneOffset.UTC);
        int hour = utcTime.getHour();
        long day = LocalDate.now().toEpochDay();
        return hour < 20 ? day : day +1;

    }
}
