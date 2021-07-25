package com.devmilk.gameserver.auth.models;

import com.devmilk.gameserver.auth.config.GAME_CONSTANTS;

import java.time.*;
public class DateFunctions {

    public static Long getNow(){
        return Instant.now().toEpochMilli();
    }

    public static Long getCurrentTournamentDay(){
        ZonedDateTime utcTime = ZonedDateTime.now(ZoneOffset.UTC);
        int hour = utcTime.getHour();
        Long day = LocalDate.now(ZoneOffset.UTC).toEpochDay();
        return hour < GAME_CONSTANTS.TOURNAMENT_FINISH_HOUR_UTC ? day : day +1;

    }
}
