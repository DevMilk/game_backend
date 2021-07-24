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

		UserProgress progress = user.getUserProgress();
		progress.setLevel(progress.getLevel() + 1);
		progress.setCoins(progress.getCoins() + GAME_CONSTANTS.COIN_INCREASE_AMOUNT_PER_LEVEL);
		user.setUserProgress(progress);

		userRepository.save(user);
		try {
			//If user claimed last reward that means user not in a tournament
			if(!user.getIsClaimedLastReward())
				tournamentService.incrementScoreOfUser(userId);
		} catch (GroupNotFoundException e) {}

		return user.getUserProgress();
	}

	public User register(String username) {
		User new_user = new User(username);
		new_user.setUserProgress(new UserProgress(GAME_CONSTANTS.STARTING_COIN_AMOUNT,1));
		userRepository.save(new_user);
		return new_user;
	}

	@Override
	public void update(User user) {
		userRepository.save(user);
	}


}
