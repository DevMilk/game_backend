package com.devmilk.gameserver.server.service;

import com.devmilk.gameserver.server.exceptions.*;
import com.devmilk.gameserver.server.models.LeaderboardRecord;
import com.devmilk.gameserver.server.models.MessageRecord;
import com.devmilk.gameserver.server.models.UserProgress;

import java.util.List;

public interface TournamentService {

    List<LeaderboardRecord> register(Long userId) throws UserNotFoundException, ConditionsDoesntMetException, GroupNotFoundException, UserAlreadyInGroupException;

    UserProgress claim(Long tournamentDay, Long userId) throws TournamentNotFoundException, ConditionsDoesntMetException, UserNotFoundException;

    int getRankOfUserInTournament(Long tournamentDay, Long userId) throws TournamentNotFoundException, UserNotFoundException, GroupNotFoundException;

    List<LeaderboardRecord> getLeaderboardOfGroup(Long groupId) throws GroupNotFoundException, ConditionsDoesntMetException;

    void incrementScoreOfUser(Long userId) throws UserNotFoundException, GroupNotFoundException;

    List<MessageRecord> getLastMessagesFromGroup(Long groupId) throws GroupNotFoundException;

    MessageRecord sendMessageToTournamentGroup(String messageText, Long userId) throws UserNotFoundException, GroupNotFoundException;
}
