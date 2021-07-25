package com.devmilk;

import com.devmilk.gameserver.server.config.GAME_SETTINGS;
import com.devmilk.gameserver.server.exceptions.UserNotFoundException;
import com.devmilk.gameserver.server.models.User;
import com.devmilk.gameserver.server.models.UserProgress;
import com.devmilk.gameserver.server.repository.UserRepository;
import com.devmilk.gameserver.server.service.TournamentServiceImpl;
import com.devmilk.gameserver.server.service.UserServiceImpl;
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
    public void It_Should_Throw_UserNotFoundException_If_User_Not_Exists_In_Database_When_Getting_User() {
        //Given
        Mockito.when(userRepository.findByUserId(0L)).thenReturn(Optional.empty());

        //Assert Throw
        userService.getUser(0L);
    }

    @Test(expected = UserNotFoundException.class)
    public void It_Should_Throw_UserNotFoundException_If_User_Not_Exists_In_Database_When_Leveling_Up_User() {
        //Given
        Mockito.when(userRepository.findByUserId(0L)).thenReturn(Optional.empty());

        //Assert Throw
        userService.levelUp(0L);
    }

    @Test(expected = UserNotFoundException.class)
    public void It_Should_Throw_GroupNotFoundException_If_User_Not_Exists_In_Database_When_Leveling_Up_User() {
        //Given
        Mockito.when(userRepository.findByUserId(0L)).thenReturn(Optional.empty());

        //Assert Throw
        userService.levelUp(0L);
    }

    @Test
    public void It_Should_Update_User_Progress_When_Leveling_Up_User() {
        //Given
        User testUser = User.builder().userId(0L).isClaimedLastReward(Boolean.TRUE).userProgress(
                UserProgress.builder().level(1).coins(0).build())
                .build();
        Mockito.when(userRepository.findByUserId(0L)).thenReturn(Optional.ofNullable(testUser));

        //When
        UserProgress progressOfReturnedUser = userService.levelUp(0L);

        //Assert
        assertEquals(1 + 1,
                progressOfReturnedUser.getLevel());

        assertEquals(GAME_SETTINGS.COIN_INCREASE_AMOUNT_PER_LEVEL,
                progressOfReturnedUser.getCoins());

    }

    @Test
    public void It_Should_Set_Starting_Amount_Of_User_Progress_When_Registering_New_User() {
        //Given
        User createdUser = userService.register("testUsername");

        //When
        UserProgress progressOfNewCreatedUser = createdUser.getUserProgress();

        //Assert
        assertEquals(1, progressOfNewCreatedUser.getLevel());
        assertEquals(GAME_SETTINGS.STARTING_COIN_AMOUNT, progressOfNewCreatedUser.getCoins());

    }
}
