package com.devmilk.gameserver.auth.models;

import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
public class RecordID implements Serializable {
    private Long groupId; // corresponds to ID type of Event
    private Long userId;

    public RecordID(Long groupId, Long userId){
        this.groupId = groupId;
        this.userId = userId;
    }

}