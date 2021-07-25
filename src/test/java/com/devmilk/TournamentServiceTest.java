package com.devmilk;

import com.devmilk.gameserver.server.config.DateFunctions;
import com.devmilk.gameserver.server.config.GAME_SETTINGS;
import com.devmilk.gameserver.server.exceptions.ConditionsDoesntMetException;
import com.devmilk.gameserver.server.exceptions.GroupNotFoundException;
import com.devmilk.gameserver.server.exceptions.TournamentNotFoundException;
import com.devmilk.gameserver.server.exceptions.UserNotFoundException;
import com.devmilk.gameserver.server.models.*;
import com.devmilk.gameserver.server.repository.LeaderboardRepository;
import com.devmilk.gameserver.server.repository.TournamentGroupRepository;
import com.devmilk.gameserver.server.service.TournamentServiceImpl;
import com.devmilk.gameserver.server.service.UserServiceImpl;
import junit.framework.AssertionFailedError;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class TournamentServiceTest {

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();
    @InjectMocks
    private TournamentServiceImpl tournamentService;
    @Mock
    private UserServiceImpl testUserService;
    @Mock
    private LeaderboardRepository leaderboardRepository;
    @Mock
    private TournamentGroupRepository tournamentGroupRepository;

    @Test
    public void It_Should_Throw_ConditionsDoesntMetException_If_User_Below_Entrance_Level_When_Entering_Tournament() {
        expectedEx.expect(ConditionsDoesntMetException.class);
        expectedEx.expectMessage("User's level must be more or equal than 20 to enter a tournament");

        //Given
        User testUser = MockFields.getTestUser();
        testUser.getUserProgress().setLevel(GAME_SETTINGS.TOURNAMENT_ENTRANCE_LEVEL_REQUIREMENT - 1);

        Mockito.when(testUserService.getUser(testUser.getUserId()))
                .thenReturn(testUser);

        //Assert Throw
        tournamentService.register(testUser.getUserId());
    }

    @Test
    public void It_Should_Throw_ConditionsDoesntMetException_If_User_Coins_Below_Fee_When_Entering_Tournament() {
        expectedEx.expect(ConditionsDoesntMetException.class);
        expectedEx.expectMessage("User must pay 1000 coins to enter a tournament");

        //Given
        User testUser = MockFields.getTestUser();
        testUser.getUserProgress().setCoins(0);

        Mockito.when(testUserService.getUser(testUser.getUserId()))
                .thenReturn(testUser);

        //Assert Throw
        tournamentService.register(testUser.getUserId());
    }

    @Test
    public void It_Should_Throw_ConditionsDoesntMetException_If_User_Not_Claimed_Last_Entered_Tournament_Reward_When_Entering_Tournament() {
        expectedEx.expect(ConditionsDoesntMetException.class);
        expectedEx.expectMessage("User must claim last entered tournament's reward or already in a tournament");

        //Given
        User testUser = MockFields.getTestUser();
        testUser.setIsClaimedLastReward(Boolean.FALSE);

        TournamentGroup testGroup = MockFields.getTestGroupCurrent();
        testGroup.setTournamentDay(DateFunctions.getCurrentTournamentDay() - 1);
        LeaderboardRecord testLeaderboard = LeaderboardRecord.builder().groupId(testGroup).build();
        Mockito.when(testUserService.getUser(testUser.getUserId()))
                .thenReturn(testUser);
        Mockito.when(leaderboardRepository.getLastLeaderboardRecordOfUser(testUser.getUserId()))
                .thenReturn(Optional.ofNullable(testLeaderboard));

        //Assert Throw
        tournamentService.register(testUser.getUserId());
    }

    @Test
    public void It_Should_Create_New_Group_If_Suitable_Groups_Are_Full_When_Entering_Tournament() {

        //Given
        User testUser = MockFields.getTestUser();
        testUser.setIsClaimedLastReward(Boolean.TRUE);
        int userLevelRange = Math.floorDiv(testUser.getUserProgress().getLevel(), 100);
        TournamentGroup testGroup = MockFields.getTestGroupCurrent();

        //Generate 20 users to reach group size limit
        LinkedList<LeaderboardRecord> records = new LinkedList<>();

        for (int i = 0; i < GAME_SETTINGS.GROUP_SIZE_LIMIT; i++)
            records.add(LeaderboardRecord.builder()
                    .userId((long) i + testUser.getUserId())
                    .score(2)
                    .timeLastUpdated(DateFunctions.getNow())
                    .build());
        testGroup.setLeaderboard(records);

        Mockito.when(testUserService.getUser(testUser.getUserId()))
                .thenReturn(testUser);
        Mockito.when(tournamentGroupRepository.getLastCreatedGroupWithGivenLevelRange(userLevelRange))
                .thenReturn(testGroup);

        List<LeaderboardRecord> currentLeaderboard = tournamentService.register(testUser.getUserId());

        //Assert Throw
        assertEquals(1, currentLeaderboard.size());
        assertEquals(testUser.getUserId(), currentLeaderboard.get(0).getUserId());
    }

    @Test(expected = TournamentNotFoundException.class)
    public void It_Should_Throw_TournamentNotFoundException_If_Non_Existing_Tournament_Given_When_User_Claim_Reward() {
        //Assert Throw
        tournamentService.claim(DateFunctions.getCurrentTournamentDay() + 1, 0L);
    }

    @Test
    public void It_Should_Throw_ConditionsDoesntMetException_If_User_Try_To_Claim_Active_Tournament_Reward_When_User_Claim_Reward() {
        expectedEx.expect(ConditionsDoesntMetException.class);
        expectedEx.expectMessage("Tournament have not finished yet");

        //Assert Throw
        tournamentService.claim(DateFunctions.getCurrentTournamentDay(), 0L);
    }

    @Test
    public void It_Should_Throw_ConditionsDoesntMetException_If_User_Try_To_Claim_Expired_Tournament_Reward_When_User_Claim_Reward() {
        expectedEx.expect(ConditionsDoesntMetException.class);
        expectedEx.expectMessage("User can't claim rewards before last 5 tournaments");

        //Assert Throw
        tournamentService.claim(DateFunctions.getCurrentTournamentDay() - 6, 0L);
    }

    @Test(expected = GroupNotFoundException.class)
    public void It_Should_Throw_GroupNotFoundException_If_User_Not_In_A_Tournament_When_User_Claim_Reward() {
        //Given
        User testUser = User.builder().isClaimedLastReward(Boolean.TRUE).build();

        Mockito.when(testUserService.getUser(testUser.getUserId()))
                .thenReturn(testUser);

        //Assert Throw
        tournamentService.claim(DateFunctions.getCurrentTournamentDay() - 1, testUser.getUserId());
    }

    @Test
    public void It_Should_Update_User_Coins_After_Claiming_Reward() {
        //Given
        User testUser = MockFields.getTestUser();
        testUser.getUserProgress().setCoins(0);
        testUser.setIsClaimedLastReward(Boolean.FALSE);

        TournamentGroup testGroup = MockFields.getTestGroupCurrent();
        testGroup.setTournamentDay(DateFunctions.getCurrentTournamentDay() - 1);
        testGroup.setLeaderboard(MockFields.getLeaderboardTestUserIsWinner());

        Mockito.when(testUserService.getUser(testUser.getUserId()))
                .thenReturn(testUser);

        Mockito.when(tournamentGroupRepository.findGroup(testGroup.getTournamentDay(), testUser.getUserId()))
                .thenReturn(testGroup);

        //When
        UserProgress testUserProgress = tournamentService.claim(DateFunctions.getCurrentTournamentDay() - 1, testUser.getUserId());

        //Assert
        assertEquals(GAME_SETTINGS.RewardEnum.WINNER.getReward(),
                testUserProgress.getCoins());
    }

    @Test(expected = GroupNotFoundException.class)
    public void It_Should_Throw_GroupNotFoundException_If_User_Not_Registered_To_Tournament_When_Claiming_Reward() {
        //Given
        User testUser = MockFields.getTestUser();
        testUser.setIsClaimedLastReward(Boolean.FALSE);

        Mockito.when(testUserService.getUser(testUser.getUserId()))
                .thenReturn(testUser);

        //Assert
        tournamentService.claim(DateFunctions.getCurrentTournamentDay() - 1, testUser.getUserId());
    }

    @Test
    public void It_Should_Return_Leaderboard_As_Sorted_When_Getting_Leaderboard() {
        //Given
        TournamentGroup group = MockFields.getTestGroupCurrent();
        group.setLeaderboard(MockFields.getLeaderboardTestUserIsWinner());
        Mockito.when(tournamentGroupRepository.findByGroupId(group.getGroupId()))
                .thenReturn(Optional.of(group));

        //When
        List<LeaderboardRecord> returnedLeaderboard = tournamentService.getLeaderboardOfGroup(group.getGroupId());

        //Assert
        LeaderboardRecord current = returnedLeaderboard.get(0);
        for (int i = 1; i < returnedLeaderboard.size(); i++) {
            LeaderboardRecord compared = returnedLeaderboard.get(i);
            int score = compared.getScore();
            int current_score = current.getScore();

            if (score > current_score || (score == current_score && compared.getTimeLastUpdated() < current.getTimeLastUpdated()))
                throw new AssertionFailedError("Returned leaderboard is not ordered");

            current = compared;
        }
    }

    @Test
    public void It_Should_Throw_ConditionsDoesntMetException_If_Leaderboard_Age_Is_Older_Than_5_Days_When_Getting_Leaderboard() {
        expectedEx.expect(ConditionsDoesntMetException.class);
        expectedEx.expectMessage("User can't get leaderboards of tournaments before last 5 tournaments");

        //Given
        TournamentGroup testGroup = MockFields.getTestGroupCurrent();
        testGroup.setTournamentDay(DateFunctions.getCurrentTournamentDay() - 6);

        Mockito.when(tournamentGroupRepository.findByGroupId(testGroup.getGroupId()))
                .thenReturn(Optional.of(testGroup));

        //Assert Throw
        tournamentService.getLeaderboardOfGroup(testGroup.getGroupId());
    }

    @Test(expected = GroupNotFoundException.class)
    public void It_Should_Throw_GroupNotFoundException_If_Group_Not_Exists_When_Getting_Messages() {
        //Given
        Mockito.when(tournamentGroupRepository.findByGroupId(0L))
                .thenReturn(null);

        //Assert
        tournamentService.getLastMessagesFromGroup(0L);
    }

    @Test(expected = GroupNotFoundException.class)
    public void It_Should_Throw_GroupNotFoundException_If_Group_Not_Exists_When_Sending_Messages() {
        //Given
        Mockito.when(testUserService.getUser(0L))
                .thenReturn(User.builder().isClaimedLastReward(Boolean.FALSE).build());

        //Assert
        tournamentService.sendMessageToTournamentGroup("test message", 0L);
    }

    @Test(expected = UserNotFoundException.class)
    public void It_Should_Throw_UserNotFoundException_If_Group_Not_Exists_When_Sending_Messages() {
        //Given
        Mockito.when(testUserService.getUser(0L))
                .thenReturn(null);
        //Assert
        tournamentService.sendMessageToTournamentGroup("test message", 0L);
    }


}
