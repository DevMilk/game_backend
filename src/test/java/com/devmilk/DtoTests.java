package com.devmilk;

import com.devmilk.gameserver.server.config.DateFunctions;
import com.devmilk.gameserver.server.models.LeaderboardRecord;
import com.devmilk.gameserver.server.models.MessageRecord;
import com.devmilk.gameserver.server.models.User;
import com.devmilk.gameserver.server.payload.LeaderboardRecordDTO;
import com.devmilk.gameserver.server.payload.MessageRecordDTO;
import com.devmilk.gameserver.server.payload.UserDTO;
import com.devmilk.gameserver.server.payload.UserProgressDTO;
import org.junit.Test;
import org.modelmapper.ModelMapper;

import static org.junit.Assert.assertEquals;

public class DtoTests {

    private final ModelMapper modelMapper = new ModelMapper();

    @Test
    public void It_Should_Convert_User_Object_To_User_DTO_Correctly() {
        User testUser = MockFields.getTestUser();

        UserDTO userDTO = modelMapper.map(
                testUser,UserDTO.class);

        assertEquals(testUser.getUserId(),userDTO.getUserId());
        assertEquals(testUser.getUserProgress().getLevel(),userDTO.getUserProgress().getLevel());
        assertEquals(testUser.getUserProgress().getCoins(),userDTO.getUserProgress().getCoins());

    }

    @Test
    public void It_Should_Convert_UserProgress_Object_To_UserProgress_DTO_Correctly() {
        User testUser = MockFields.getTestUser();

        UserProgressDTO userProgressDTO = modelMapper.map(
                testUser.getUserProgress(), UserProgressDTO.class);

        assertEquals(testUser.getUserProgress().getLevel(),userProgressDTO.getLevel());
        assertEquals(testUser.getUserProgress().getCoins(),userProgressDTO.getCoins());

    }

    @Test
    public void It_Should_Convert_TournamentRecord_Object_To_TournamentRecord_DTO_Correctly() {
        LeaderboardRecord leaderboardRecord = MockFields.getLeaderboardTestUserIsWinner().get(0);

        LeaderboardRecordDTO leaderboardRecordDTO = modelMapper.map(
                leaderboardRecord, LeaderboardRecordDTO.class);

        assertEquals(leaderboardRecord.getUserId(),leaderboardRecordDTO.getUserId());
        assertEquals(leaderboardRecord.getScore(),leaderboardRecordDTO.getScore());
        assertEquals(leaderboardRecord.getUsername(),leaderboardRecordDTO.getUsername());

    }

    @Test
    public void It_Should_Convert_MessageRecord_Object_To_MessageRecord_DTO_Correctly() {
        MessageRecord testMessage = MessageRecord.builder()
                .messageId(0L)
                .messageText("Test Message")
                .senderUsername("Test Username")
                .sentTime(DateFunctions.getNow())
                .build();

        MessageRecordDTO messageRecordDTO = modelMapper.map(
                testMessage, MessageRecordDTO.class);

        assertEquals(testMessage.getMessageText(),messageRecordDTO.getMessageText());
        assertEquals(testMessage.getSenderUsername(),messageRecordDTO.getSenderUsername());
        assertEquals(testMessage.getSentTime(),messageRecordDTO.getSentTime());


    }

}
