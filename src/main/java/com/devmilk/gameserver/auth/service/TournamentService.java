package com.devmilk.gameserver.auth.service;

import com.devmilk.gameserver.auth.exceptions.*;
import com.devmilk.gameserver.auth.models.LeaderboardRecord;
import com.devmilk.gameserver.auth.models.MessageRecord;
import com.devmilk.gameserver.auth.models.TournamentGroup;
import com.devmilk.gameserver.auth.models.UserProgress;

import java.util.ArrayList;
import java.util.List;

public interface TournamentService {

    List<LeaderboardRecord> register(Long userId) throws UserNotFoundException, ConditionsDoesntMetException, GroupNotFoundException, UserAlreadyInGroupException;
    UserProgress claim(Long tournamentId, Long userId) throws TournamentNotFoundException, ConditionsDoesntMetException, UserNotFoundException;
    int getRankOfUserInTournament(Long tournamentId, Long userId) throws TournamentNotFoundException, UserNotFoundException, GroupNotFoundException;
    List<LeaderboardRecord> getLeaderboardOfGroup(Long groupId) throws GroupNotFoundException, ConditionsDoesntMetException;
    void incrementScoreOfUser(Long userId) throws UserNotFoundException, GroupNotFoundException;
    List<MessageRecord> getLastMessagesFromGroup(Long groupId) throws GroupNotFoundException;
    MessageRecord sendMessageToTournamentGroup(String messageText,Long userId) throws UserNotFoundException, GroupNotFoundException;
}
