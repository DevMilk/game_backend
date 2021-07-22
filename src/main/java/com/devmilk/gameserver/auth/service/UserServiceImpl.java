package com.devmilk.gameserver.auth.service;
import com.devmilk.gameserver.auth.exceptions.UserNotFoundException;
import com.devmilk.gameserver.auth.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.devmilk.gameserver.auth.repository.UserRepository;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository userRepository;

	@Override
	public User getUser(Long user_id) throws UserNotFoundException {
		Optional<User> user = userRepository.findByUserId(user_id);
		if(!user.isPresent()) throw new UserNotFoundException("User Not Found");
		return user.get();
	}

	@Override
	public User levelUp(Long user_id) throws UserNotFoundException {
		User user = getUser(user_id);
		/*
		UserProgress progress = user.getUserProgress().levelUp();
		progress.setLevel(progress.getLevel() + 1);
		progress.setCoins(progress.getCoins() +25);
		user.setUserProgress(progress);
		*/
		user.getUserProgress().levelUp();
		userRepository.save(user);
		return user;
	}

	public User register(String username) {
		User new_user = new User(username);
		userRepository.save(new_user);
		return new_user;
	}

	@Override
	public void update(User user) {
		userRepository.save(user);
	}


}
