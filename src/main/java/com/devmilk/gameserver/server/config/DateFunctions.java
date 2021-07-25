package com.devmilk.gameserver.server.config;

import com.devmilk.gameserver.server.config.GAME_SETTINGS;

import java.time.*;
public class DateFunctions {

    public static Long getNow(){
        return Instant.now().toEpochMilli();
    }

    public static Long getCurrentTournamentDay(){
        ZonedDateTime utcTime = ZonedDateTime.now(ZoneOffset.UTC);
        int hour = utcTime.getHour();
        long day = LocalDate.now(ZoneOffset.UTC).toEpochDay();
        return hour < GAME_SETTINGS.TOURNAMENT_FINISH_HOUR_UTC ? day : day +1;

    }
}
