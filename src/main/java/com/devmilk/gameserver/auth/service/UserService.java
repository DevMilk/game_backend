package com.devmilk.gameserver.auth.service;
import com.devmilk.gameserver.auth.exceptions.UserNotFoundException;
import com.devmilk.gameserver.auth.models.User;

public interface UserService {
    User getUser(Long user_id) throws UserNotFoundException;
    User levelUp(Long user_id) throws UserNotFoundException;
    User register(String username);
    void update(User user);
}
