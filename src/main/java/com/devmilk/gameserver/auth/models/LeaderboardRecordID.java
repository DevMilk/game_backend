package com.devmilk.gameserver.auth.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
public class LeaderboardRecordID implements Serializable {
    @JsonIgnore
    private Long groupId; // corresponds to ID type of Event
    private Long userId;

    public LeaderboardRecordID(Long groupId, Long userId){
        this.groupId = groupId;
        this.userId = userId;
    }

}