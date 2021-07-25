package com.devmilk.gameserver.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
public class LeaderboardRecordID implements Serializable {
    private Long groupId; // corresponds to ID type of Event
    private Long userId;

    public LeaderboardRecordID(Long groupId, Long userId){
        this.groupId = groupId;
        this.userId = userId;
    }

}