package com.devmilk.gameserver.auth.service;

import com.devmilk.gameserver.auth.exceptions.*;
import com.devmilk.gameserver.auth.models.LeaderboardRecord;
import com.devmilk.gameserver.auth.models.UserProgress;

import java.util.ArrayList;
import java.util.List;

public interface TournamentService {

    public List<LeaderboardRecord> register(Long userId) throws UserNotFoundException, ConditionsDoesntMetException, GroupNotFoundException;
    public UserProgress claim(Long tournamentId, Long userId) throws TournamentNotFoundException, ConditionsDoesntMetException, UserNotFoundException, RecordNotFoundException;
    public int getRankOfUserInTournament(Long tournamentId, Long userId) throws TournamentNotFoundException, UserNotFoundException, RecordNotFoundException;
    public List<LeaderboardRecord> getLeaderboardOfGroup(Long groupId) throws GroupNotFoundException;
}
