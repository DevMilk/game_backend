package com.devmilk.gameserver.auth.service;
import com.devmilk.gameserver.auth.config.GAME_CONSTANTS;
import com.devmilk.gameserver.auth.exceptions.GroupNotFoundException;
import com.devmilk.gameserver.auth.exceptions.UserNotFoundException;
import com.devmilk.gameserver.auth.models.User;
import com.devmilk.gameserver.auth.models.UserProgress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.devmilk.gameserver.auth.repository.UserRepository;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	TournamentService tournamentService;

	@Override
	public User getUser(Long userId) throws UserNotFoundException {
		Optional<User> user = userRepository.findByUserId(userId);
		if(user==null || !user.isPresent()) throw new UserNotFoundException("User Not Found");
		return user.get();
	}

	@Override
	public UserProgress levelUp(Long userId) throws UserNotFoundException {
		User user = getUser(userId);

		//update and save user progress
		UserProgress progress = user.getUserProgress();
		progress.setLevel(progress.getLevel() + 10);
		progress.setCoins(progress.getCoins() + GAME_CONSTANTS.COIN_INCREASE_AMOUNT_PER_LEVEL);
		user.setUserProgress(progress);
		updateOrCreate(user);

		//Increment score on current tournament (if it exists)
		try {
			//If user claimed last reward that means user not in a tournament
			if(!user.getIsClaimedLastReward())
				tournamentService.incrementScoreOfUser(userId);
		} catch (GroupNotFoundException e) {}

		return user.getUserProgress();
	}

	public User register(String username) {
		//Create new user object with given username, initial coin and level values
		User new_user = User.builder().username(username)
				.isClaimedLastReward(Boolean.TRUE)
				.userProgress(UserProgress.builder()
						.coins(GAME_CONSTANTS.STARTING_COIN_AMOUNT)
						.level(1)
						.build())
				.build();
		//Save user object to user repository
		updateOrCreate(new_user);
		return new_user;
	}

	@Override
	public void updateOrCreate(User user) {
		userRepository.save(user);
	}


}
