package com.devmilk.gameserver.server.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageRecordDTO {

    private Long sentTime;

    private String senderUsername;

    private String messageText;

}
