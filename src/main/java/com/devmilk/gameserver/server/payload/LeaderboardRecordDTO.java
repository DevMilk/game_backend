package com.devmilk.gameserver.server.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeaderboardRecordDTO {

    private Long userId;

    private String username;

    private int score;


}
