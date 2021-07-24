package com.devmilk;

import com.devmilk.gameserver.auth.config.GAME_CONSTANTS;
import com.devmilk.gameserver.auth.exceptions.UserNotFoundException;
import com.devmilk.gameserver.auth.models.User;
import com.devmilk.gameserver.auth.models.UserProgress;
import com.devmilk.gameserver.auth.repository.UserRepository;
import com.devmilk.gameserver.auth.service.TournamentServiceImpl;
import com.devmilk.gameserver.auth.service.UserService;
import com.devmilk.gameserver.auth.service.UserServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import static org.junit.Assert.assertEquals;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TournamentServiceImpl tournamentService;

    @InjectMocks
    private UserServiceImpl userService;

    @Test(expected = UserNotFoundException.class)
    public void It_Should_Throw_UserNotFoundException_If_User_Not_Exists_In_Database_When_Getting_User(){
        Long mockUserId = new Long(0);
        Mockito.when(userRepository.findByUserId(mockUserId)).thenReturn(Optional.empty());
        userService.getUser(mockUserId);
    }

    @Test(expected = UserNotFoundException.class)
    public void It_Should_Throw_UserNotFoundException_If_User_Not_Exists_In_Database_When_Leveling_Up_User(){
        Long mockUserId = new Long(0);
        Mockito.when(userRepository.findByUserId(mockUserId)).thenReturn(Optional.empty());
        userService.levelUp(mockUserId);
    }

    @Test(expected = UserNotFoundException.class)
    public void It_Should_Throw_GroupNotFoundException_If_User_Not_Exists_In_Database_When_Leveling_Up_User(){
        Long mockUserId = new Long(0);
        Mockito.when(userRepository.findByUserId(mockUserId)).thenReturn(Optional.empty());
        userService.levelUp(mockUserId);
    }

    @Test
    public void It_Should_Update_User_Progress_When_Leveling_Up_User(){
        Long testUserId = new Long(0);
        int testUserLevel = 1;
        int testUserCoins = 0;
        User testUser = User.builder().userId(testUserId).isClaimedLastReward(Boolean.TRUE).userProgress(
                UserProgress.builder().level(testUserLevel).coins(testUserCoins).build())
                .build();
        Mockito.when(userRepository.findByUserId(testUserId)).thenReturn(Optional.ofNullable(testUser));

        UserProgress progressOfReturnedUser = userService.levelUp(testUserId);

        assertEquals(testUserLevel+1,
                progressOfReturnedUser.getLevel());

        assertEquals(testUserCoins+GAME_CONSTANTS.COIN_INCREASE_AMOUNT_PER_LEVEL,
                progressOfReturnedUser.getCoins());

    }
    @Test
    public void It_Should_Set_Starting_Amount_Of_User_Progress_When_Registering_New_User(){

        User createdUser = userService.register("testUsername");
        UserProgress progressOfNewCreatedUser = createdUser.getUserProgress();

        assertEquals(1,progressOfNewCreatedUser.getLevel());

        assertEquals(GAME_CONSTANTS.STARTING_COIN_AMOUNT, progressOfNewCreatedUser.getCoins());

    }
}
