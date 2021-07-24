package com.devmilk;

import com.devmilk.gameserver.auth.config.GAME_CONSTANTS;
import com.devmilk.gameserver.auth.exceptions.*;
import com.devmilk.gameserver.auth.models.*;
import com.devmilk.gameserver.auth.repository.TournamentGroupRepository;
import com.devmilk.gameserver.auth.service.TournamentService;
import com.devmilk.gameserver.auth.service.TournamentServiceImpl;
import com.devmilk.gameserver.auth.service.UserService;
import com.devmilk.gameserver.auth.service.UserServiceImpl;
import junit.framework.AssertionFailedError;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class TournamentServiceTest {

    @InjectMocks
    private TournamentServiceImpl tournamentService;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private TournamentGroupRepository tournamentGroupRepository;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    private Long mockTournamentDay;
    private Long mockUserId;
    private Long mockGroupId;
    private TournamentGroup mockTournamentGroup;

    @Before
    public void mockVariables(){
        mockTournamentDay = DateFunctions.getCurrentDay()-1;
        mockUserId = new Long(0);
        mockGroupId = new Long(0);

        mockTournamentGroup = TournamentGroup.builder().tournamentDay(mockTournamentDay).build();

        ArrayList<LeaderboardRecord> records = new ArrayList<>();
        records.add(LeaderboardRecord.builder().userId(mockUserId).score(10).timeLastUpdated(DateFunctions.getNow()).build());
        records.add(LeaderboardRecord.builder().userId(mockUserId+1).score(2).timeLastUpdated(DateFunctions.getNow()).build());
        records.add(LeaderboardRecord.builder().userId(mockUserId+2).score(4).timeLastUpdated(DateFunctions.getNow()).build());
        mockTournamentGroup.setLeaderboard(records);
    }

    @Test
    public void It_Should_Throw_ConditionsDoesntMetException_If_User_Below_Entrance_Level_When_Entering_Tournament(){
        expectedEx.expect(ConditionsDoesntMetException.class);
        expectedEx.expectMessage("User's level must be more or equal than 20 to enter a tournament");


        User user = User.builder()
                .userId(mockUserId)
                .userProgress(
                UserProgress.builder()
                        .level(GAME_CONSTANTS.TOURNAMENT_ENTRANCE_LEVEL_REQUIREMENT - 1)
                        .coins(GAME_CONSTANTS.TOURNAMENT_ENTRANCE_FEE)
                        .build()
        ).build();
        Mockito.when(userService.getUser(user.getUserId()))
                .thenReturn(user);

        tournamentService.register(user.getUserId());
    }

    @Test
    public void It_Should_Throw_ConditionsDoesntMetException_If_User_Coins_Below_Fee_When_Entering_Tournament(){
        expectedEx.expect(ConditionsDoesntMetException.class);
        expectedEx.expectMessage("User must pay 1000 coins to enter a tournament");


        User user = User.builder()
                .userId(mockUserId)
                .userProgress(
                        UserProgress.builder()
                                .level(GAME_CONSTANTS.TOURNAMENT_ENTRANCE_LEVEL_REQUIREMENT)
                                .coins(0)
                                .build()
                ).build();
        Mockito.when(userService.getUser(user.getUserId()))
                .thenReturn(user);

        tournamentService.register(user.getUserId());
    }

    @Test
    public void It_Should_Throw_ConditionsDoesntMetException_If_User_Not_Claimed_Last_Entered_Tournament_Reward_When_Entering_Tournament(){
        expectedEx.expect(ConditionsDoesntMetException.class);
        expectedEx.expectMessage("User must claim last entered tournament's reward or already entered to tournament");

        User user = User.builder()
                .userId(mockUserId)
                .isClaimedLastReward(Boolean.FALSE)
                .userProgress(
                        UserProgress.builder()
                                .level(GAME_CONSTANTS.TOURNAMENT_ENTRANCE_LEVEL_REQUIREMENT)
                                .coins(GAME_CONSTANTS.TOURNAMENT_ENTRANCE_FEE)
                                .build()
                ).build();
        Mockito.when(userService.getUser(user.getUserId()))
                .thenReturn(user);
        Mockito.when(tournamentGroupRepository.getLastTournamentDayOfUser(user.getUserId()))
                .thenReturn(DateFunctions.getCurrentDay()-1);

        tournamentService.register(user.getUserId());
    }

    @Test(expected = TournamentNotFoundException.class)
    public void It_Should_Throw_TournamentNotFoundException_If_Non_Existing_Tournament_Given_When_User_Claim_Reward(){
        tournamentService.claim(DateFunctions.getCurrentDay()+1,mockUserId);
    }

    @Test
    public void It_Should_Throw_ConditionsDoesntMetException_If_User_Try_To_Claim_Active_Tournament_Reward_When_User_Claim_Reward(){
        expectedEx.expect(ConditionsDoesntMetException.class);
        expectedEx.expectMessage("Tournament have not finished yet");

        tournamentService.claim(DateFunctions.getCurrentDay(),mockUserId);
    }

    @Test
    public void It_Should_Throw_ConditionsDoesntMetException_If_User_Try_To_Claim_Expired_Tournament_Reward_When_User_Claim_Reward(){
        expectedEx.expect(ConditionsDoesntMetException.class);
        expectedEx.expectMessage("User can't claim rewards before last 5 tournaments");

        tournamentService.claim(DateFunctions.getCurrentDay()-6,mockUserId);
    }

    @Test(expected = GroupNotFoundException.class)
    public void It_Should_Throw_GroupNotFoundException_If_User_Not_In_A_Tournament_When_User_Claim_Reward(){
        User user = User.builder().isClaimedLastReward(Boolean.TRUE).build();

        Mockito.when(userService.getUser(mockUserId))
                .thenReturn(user);
        tournamentService.claim(DateFunctions.getCurrentDay()-1,mockUserId);
    }
    @Test
    public void It_Should_Update_User_Coins_After_Claiming_Reward(){

        int mockCoins = 100;
        User user = User.builder()
                .userId(mockUserId)
                .isClaimedLastReward(Boolean.FALSE)
                .userProgress(
                        UserProgress.builder()
                                .coins(mockCoins)
                                .build()
                ).build();

        Mockito.when(userService.getUser(user.getUserId()))
                .thenReturn(user);

        Mockito.when(tournamentGroupRepository.findGroup(mockTournamentDay,user.getUserId()))
                .thenReturn(mockTournamentGroup);

        UserProgress userProgress = tournamentService.claim(DateFunctions.getCurrentDay()-1,user.getUserId());

        assertEquals(mockCoins+GAME_CONSTANTS.RewardEnum.WINNER.getReward(),
                userProgress.getCoins());
    }

    @Test(expected = GroupNotFoundException.class)
    public void It_Should_Throw_GroupNotFoundException_If_User_Not_Registered_To_Tournament_When_Claiming_Reward(){
        //Given

        User user = User.builder().userId(mockUserId).isClaimedLastReward(Boolean.FALSE).build();

        Mockito.when(userService.getUser(user.getUserId()))
                .thenReturn(user);

        Mockito.when(tournamentGroupRepository.findGroup(mockTournamentDay,user.getUserId()))
                .thenReturn(TournamentGroup.builder().leaderboard(new ArrayList<>()).build());

        //Assert
        UserProgress userProgress = tournamentService.claim(DateFunctions.getCurrentDay()-1,user.getUserId());

    }

    @Test
    public void It_Should_Return_Leaderboard_As_Sorted_When_Getting_Leaderboard(){

        //Given

        Mockito.when(tournamentGroupRepository.findByGroupId(mockGroupId))
                .thenReturn(Optional.ofNullable(mockTournamentGroup));

        //When
        List<LeaderboardRecord> returnedLeaderboard = tournamentService.getLeaderboardOfGroup(mockGroupId);

        //Assert
        LeaderboardRecord current = returnedLeaderboard.get(0);
        for(int i=1; i<returnedLeaderboard.size();i++){
            LeaderboardRecord compared = returnedLeaderboard.get(i);
            int score = compared.getScore();
            int current_score = current.getScore();
            Long date = compared.getTimeLastUpdated();

            if(score > current_score || (score == current_score && compared.getTimeLastUpdated() < current.getTimeLastUpdated()))
                throw new AssertionFailedError("Returned leaderboard is not ordered");

            current = compared;
        }
    }

    @Test
    public void It_Should_Throw_ConditionsDoesntMetException_If_Leaderboard_Age_Is_Older_Than_5_Days_When_Getting_Leaderboard(){

        expectedEx.expect(ConditionsDoesntMetException.class);
        expectedEx.expectMessage("User can't get leaderboards of tournaments before last 5 tournaments");

        TournamentGroup mockGroup = TournamentGroup.builder()
                .groupId(new Long(0))
                .tournamentDay(DateFunctions.getCurrentDay()-6)
                .build();
        //Given
        Mockito.when(tournamentGroupRepository.findByGroupId(mockGroup.getGroupId()))
                .thenReturn(Optional.ofNullable(mockGroup));

        //When & Assert
        List<LeaderboardRecord> returnedLeaderboard = tournamentService.getLeaderboardOfGroup(mockGroupId);
        }

    @Test(expected = GroupNotFoundException.class)
    public void It_Should_Throw_GroupNotFoundException_If_Group_Not_Exists_When_Getting_Messages(){
        //Given

        Mockito.when(tournamentGroupRepository.findByGroupId(mockGroupId))
                .thenReturn(null);

        //Assert
        tournamentService.getLastMessagesFromGroup(mockGroupId);

    }

    @Test(expected = GroupNotFoundException.class)
    public void It_Should_Throw_GroupNotFoundException_If_Group_Not_Exists_When_Sending_Messages(){
        Mockito.when(userService.getUser(mockUserId))
                .thenReturn(User.builder().isClaimedLastReward(Boolean.FALSE).build());

        tournamentService.sendMessageToTournamentGroup("test message",mockUserId);

    }

    @Test(expected = UserNotFoundException.class)
    public void It_Should_Throw_UserNotFoundException_If_Group_Not_Exists_When_Sending_Messages(){
        //Given
        Mockito.when(tournamentGroupRepository.findGroup(DateFunctions.getCurrentDay(),mockUserId))
                .thenReturn(mockTournamentGroup);
        Mockito.when(userService.getUser(mockUserId))
                .thenReturn(null);
        //Assert
        tournamentService.sendMessageToTournamentGroup("test message",mockUserId);

    }


}
