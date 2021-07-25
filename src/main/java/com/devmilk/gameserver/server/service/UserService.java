package com.devmilk.gameserver.server.service;

import com.devmilk.gameserver.server.exceptions.UserNotFoundException;
import com.devmilk.gameserver.server.models.User;
import com.devmilk.gameserver.server.models.UserProgress;

public interface UserService {
    User getUser(Long user_id) throws UserNotFoundException;

    UserProgress levelUp(Long user_id) throws UserNotFoundException;

    User register(String username);

    void updateOrCreate(User user);
}
