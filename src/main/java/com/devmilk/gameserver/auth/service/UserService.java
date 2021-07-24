package com.devmilk.gameserver.auth.service;
import com.devmilk.gameserver.auth.exceptions.UserNotFoundException;
import com.devmilk.gameserver.auth.models.User;
import com.devmilk.gameserver.auth.models.UserProgress;

public interface UserService {
    User getUser(Long user_id) throws UserNotFoundException;
    UserProgress levelUp(Long user_id) throws UserNotFoundException;
    User register(String username);
    void update(User user);
}
