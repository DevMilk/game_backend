package com.devmilk;

import com.devmilk.gameserver.server.config.DateFunctions;
import com.devmilk.gameserver.server.config.GAME_SETTINGS;
import com.devmilk.gameserver.server.models.*;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@NoArgsConstructor
public class MockFields {
    public static User getTestUser(){
        return User.builder()
                .userId(0L)
                .username("testUser")
                .isClaimedLastReward(Boolean.TRUE)
                .userProgress(
                        UserProgress.builder()
                                .level(GAME_SETTINGS.TOURNAMENT_ENTRANCE_LEVEL_REQUIREMENT)
                                .coins(GAME_SETTINGS.TOURNAMENT_ENTRANCE_FEE)
                                .build()
                ).build();
    }

    public static ArrayList<LeaderboardRecord> getLeaderboardTestUserIsWinner(){
        ArrayList<LeaderboardRecord> records = new ArrayList<>();
        records.add(LeaderboardRecord.builder().userId(0L).score(10).timeLastUpdated(DateFunctions.getNow()).build());
        //Generate 10 users who have lower score than main test user
        for(int i=0;i<10;i++)
            records.add(LeaderboardRecord.builder().userId((long) i+1).score(2).timeLastUpdated(DateFunctions.getNow()).build());
        return records;
    }
    public static TournamentGroup getTestGroupCurrent(){
        return TournamentGroup.builder().tournamentDay(DateFunctions.getCurrentTournamentDay())
                .leaderboard(new ArrayList<>()).build();
    }
}
