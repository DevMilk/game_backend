package com.devmilk.gameserver.auth.service;

import com.devmilk.gameserver.auth.exceptions.*;
import com.devmilk.gameserver.auth.models.*;
import com.devmilk.gameserver.auth.repository.TournamentRepository;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.*;

@Service
public class TournamentServiceImplTournament implements TournamentService{

    @Autowired
    private UserService userService;

    @Autowired
    private TournamentRepository tournamentRepository;

    private int calculateReward(int userRank){
        int reward = 0;

        for(RewardEnum place: RewardEnum.values())
            if(userRank> place.getBaseOrder())
                return reward;
            else
                reward = place.getReward();

        return reward;

    }

    private Long getCurrentDay(){
        ZonedDateTime utcTime = ZonedDateTime.now(ZoneOffset.UTC);
        int hour = utcTime.getHour();
        long day = LocalDate.now().toEpochDay();
        return hour < 20 ? day : day +1;

    }

    private boolean isCurrentTournamentExists(){
        return tournamentRepository.findByTournamentDay(getCurrentDay()).isPresent();
    }
    private synchronized TournamentGroup createNewTournamentWithFirstParticipant(User user, int userLevelRange, Long currentDay){
        Tournament tournament = new Tournament(currentDay);
        TournamentGroup tournamentGroup = new TournamentGroup(userLevelRange, (new Date()).getTime());
        LeaderboardRecord leaderboardRecord = new LeaderboardRecord(user.getUserId());
        tournamentGroup.getLeaderboard().add(leaderboardRecord);
        tournament.getTournamentGroups().add(tournamentGroup);
        System.out.println(tournamentGroup.getGroupId());
        if(isCurrentTournamentExists()){
            return null;
        }

        tournamentRepository.save(tournament);

        return tournamentGroup;
    }

    @Override

    public List<LeaderboardRecord> register(Long userId) throws UserNotFoundException, ConditionsDoesntMetException, GroupNotFoundException {

        User user = userService.getUser(userId);
        int userLevel = user.getUserProgress().getLevel();
        if(userLevel<20)
            throw new ConditionsDoesntMetException("User's level must be more or equal than 20 to enter a tournament");

        if(user.getIsClaimedLastReward() == Boolean.FALSE)
            throw new ConditionsDoesntMetException("User must claim last tournament's reward");

        int userLevelRange = (int) Math.floor(userLevel/100);

        // Check if tournament of day is created
        TournamentGroup group;
        if(!isCurrentTournamentExists()){
            group = createNewTournamentWithFirstParticipant(user,userLevelRange,getCurrentDay());
            if(group != null)
                return new ArrayList<>(group.getLeaderboard());
        }

        group = tournamentRepository.getLastCreatedGroupOfLevelRange(userLevelRange);

        if(group.getLeaderboard().size()>20){
            group = new TournamentGroup();
            tournamentRepository.addTournamentGroup(
                    group.getGroupId(),
                    userLevelRange,
                    getCurrentDay(),
                    (new Date()).getTime()
                    );
        }
        else
            tournamentRepository.addUserToLeaderBoard(user.getUserId(), group.getGroupId());

        return getLeaderboardOfGroup(getCurrentDay(), userId);
    }

    @Override
    @SneakyThrows
    public UserProgress claim(Long tournamentId, Long userId) {

        User user = userService.getUser(userId);
        if(user.getIsClaimedLastReward()==Boolean.TRUE)
            throw new ConditionsDoesntMetException("User must claim last participated tournament's reward to enter a new tournament");

        int rank = getRankOfUserInTournament(tournamentId,userId);
        int reward = calculateReward(rank);

        user.setIsClaimedLastReward(Boolean.TRUE);

        if(reward!=0)
            userService.update(user);

        return user.getUserProgress();

    }

    @Override
    public int getRankOfUserInTournament(Long tournamentId, Long userId) throws  RecordNotFoundException {
        List<LeaderboardRecord> leaderboard = tournamentRepository.
                findGroup(tournamentId,userId).getLeaderboard();

        if(leaderboard.isEmpty())
            throw new RecordNotFoundException("User record in given tournament not exists");

        int index = 0;
        for(LeaderboardRecord record: leaderboard){
            if(record.getUserId().equals(userId))
                return index;
            index++;
        }
        return index;
    }

    private List sortRecordsAndReturn(List<LeaderboardRecord> records) throws GroupNotFoundException {
        if(records.isEmpty())
            throw new GroupNotFoundException("Group not found");

        Collections.sort(records, Comparator.comparingInt(LeaderboardRecord::getScore));
        return records;
    }

    @Override
    public List<LeaderboardRecord> getLeaderboardOfGroup(Long groupId) throws GroupNotFoundException {
        List records = tournamentRepository.findGroup(groupId).getLeaderboard();
        return sortRecordsAndReturn(records);
    }

    private List<LeaderboardRecord> getLeaderboardOfGroup(Long tournamentId, Long userId) throws GroupNotFoundException {
        List records = tournamentRepository.findGroup(tournamentId, userId).getLeaderboard();
        return sortRecordsAndReturn(records);
    }
}
