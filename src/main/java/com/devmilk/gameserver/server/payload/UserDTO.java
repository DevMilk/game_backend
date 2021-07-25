package com.devmilk.gameserver.server.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

    private Long userId;

    private UserProgressDTO userProgress;


}
