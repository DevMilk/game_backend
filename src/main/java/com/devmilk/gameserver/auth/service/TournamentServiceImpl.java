package com.devmilk.gameserver.auth.service;

import com.devmilk.gameserver.auth.config.GAME_CONSTANTS;
import com.devmilk.gameserver.auth.exceptions.*;
import com.devmilk.gameserver.auth.models.*;
import com.devmilk.gameserver.auth.repository.ChatGroupRepository;
import com.devmilk.gameserver.auth.repository.TournamentGroupRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;


/*TODO: Unit testleri
        Concurrency
        */
@Service
public class TournamentServiceImpl implements TournamentService{

    @Autowired
    private UserService userService;

    @Autowired
    private TournamentGroupRepository tournamentGroupRepository;

    @Autowired
    private ChatGroupRepository chatGroupRepository;

    private int calculateReward(int userRank){
        int reward = 0;

        for(GAME_CONSTANTS.RewardEnum place: GAME_CONSTANTS.RewardEnum.values())
            if(userRank> place.getBaseOrder())
                return reward;
            else
                reward = place.getReward();

        return reward;

    }


    private List sortRecordsAndReturn(List<LeaderboardRecord> records) throws GroupNotFoundException {
        if(records.isEmpty())
            throw new GroupNotFoundException("Group not found");

        Collections.sort(records, Comparator.comparingInt(LeaderboardRecord::getScore).reversed()
                .thenComparingLong(LeaderboardRecord::getTimeLastUpdated));
        return records;
    }

    private TournamentGroup getTournamentGroup(Long groupId) throws GroupNotFoundException, ConditionsDoesntMetException {
        Optional<TournamentGroup> group = tournamentGroupRepository.findByGroupId(groupId);
        if(group==null || !group.isPresent())
            throw new GroupNotFoundException("Group not found");
        if(group.get().getTournamentDay()<DateFunctions.getCurrentDay()-5)
            throw new ConditionsDoesntMetException("User can't get leaderboards of tournaments before last 5 tournaments");
        return  group.get();
    }

    private TournamentGroup getTournamentGroup(Long tournamentId, Long userId) throws GroupNotFoundException {
        TournamentGroup group = tournamentGroupRepository.findGroup(tournamentId, userId);
        if(group== null)
            throw new GroupNotFoundException("Group not found");
        return group;
    }

    private List<LeaderboardRecord> getLeaderboardOfGroup(Long tournamentId, Long userId) throws GroupNotFoundException {
        List records = getTournamentGroup(tournamentId, userId).getLeaderboard();
        return sortRecordsAndReturn(records);
    }

    @Override
    @Transactional
    public List<LeaderboardRecord> register(Long userId){

        Long currentDay = DateFunctions.getCurrentDay() ;

        //Check user attributes
        User user = userService.getUser(userId);
        UserProgress userProgress = user.getUserProgress();
        int userLevel = userProgress.getLevel();
        int userCoins = userProgress.getCoins();

        if(userCoins<GAME_CONSTANTS.TOURNAMENT_ENTRANCE_FEE)
            throw new ConditionsDoesntMetException("User must pay "+GAME_CONSTANTS.TOURNAMENT_ENTRANCE_FEE + " coins to enter a tournament");

        if(userLevel<GAME_CONSTANTS.TOURNAMENT_ENTRANCE_LEVEL_REQUIREMENT)
            throw new ConditionsDoesntMetException("User's level must be more or equal than "+
                    GAME_CONSTANTS.TOURNAMENT_ENTRANCE_LEVEL_REQUIREMENT+" to enter a tournament");

        Long lastEnteredTournamentDay = tournamentGroupRepository.getLastTournamentDayOfUser(userId);

        //If user cannot claim last reward from its last entered tournament, check it as claimed
        if(lastEnteredTournamentDay==null || lastEnteredTournamentDay<currentDay-5)
            user.setIsClaimedLastReward(Boolean.TRUE);

        if(user.getIsClaimedLastReward().equals(Boolean.FALSE))
            throw new ConditionsDoesntMetException("User must claim last entered tournament's reward or already entered to tournament");

        int userLevelRange = (int) Math.floor(userLevel/GAME_CONSTANTS.GROUP_LEVEL_RANGE);

        synchronized (this) {
            // Create new tournament group
            TournamentGroup group = tournamentGroupRepository.getLastCreatedGroupOfLevelRange(userLevelRange);

            //If group full or not exists or not today's daily, create new one
            if (group == null || group.getLeaderboard().size() >= GAME_CONSTANTS.GROUP_SIZE_LIMIT
                    || group.getTournamentDay()<DateFunctions.getCurrentDay()) {
            /*Tournament tournament = new Tournament(currentDay);
            tournamentRepository.save(tournament);*/
                group = TournamentGroup.builder().
                        tournamentDay(currentDay)
                        .groupCreationDate(DateFunctions.getNow())
                        .levelRange(userLevelRange)
                        .leaderboard(new ArrayList<>())
                        .build();
            }


            // Create leaderboard record for adding to group
            LeaderboardRecord record = LeaderboardRecord.builder()
                    .userId(userId)
                    .username(user.getUsername())
                    .groupId(group)
                    .timeLastUpdated(DateFunctions.getNow())
                    .build();


            // Save group
            group.getLeaderboard().add(record);
            tournamentGroupRepository.save(group);
        }
        //Reset reward claim of user and withdraw enterance fee
        user.setIsClaimedLastReward(Boolean.FALSE);
        userProgress.setCoins(userCoins - GAME_CONSTANTS.TOURNAMENT_ENTRANCE_FEE);
        user.setUserProgress(userProgress);
        userService.update(user);

        return getLeaderboardOfGroup(DateFunctions.getCurrentDay(), userId);
    }

    @Override
    @SneakyThrows
    @Transactional
    public UserProgress claim(Long tournamentId, Long userId) {

        if(tournamentId>DateFunctions.getCurrentDay())
            throw new TournamentNotFoundException("Tournament not exists");

        if (tournamentId.equals(DateFunctions.getCurrentDay()))
            throw new ConditionsDoesntMetException("Tournament have not finished yet");

        if(tournamentId<DateFunctions.getCurrentDay()-5)
            throw new ConditionsDoesntMetException("User can't claim rewards before last 5 tournaments");

        User user = userService.getUser(userId);

        //Claimed reward true ise last entered null dönmez bu yüzden null olup olmadığını kontrol etmeye gerek yok
        if(user.getIsClaimedLastReward().equals(Boolean.TRUE))
            throw new GroupNotFoundException("User not in a tournament");

        int rank = getRankOfUserInTournament(tournamentId,userId);
        int reward = calculateReward(rank);

        user.setIsClaimedLastReward(Boolean.TRUE);

        UserProgress progress = user.getUserProgress();
        progress.setCoins(progress.getCoins()+reward);
        user.setUserProgress(progress);
        if(reward!=0)
            userService.update(user);

        return user.getUserProgress();

    }

    @Override
    public int getRankOfUserInTournament(Long tournamentId, Long userId) {
        List<LeaderboardRecord> leaderboard = getLeaderboardOfGroup(tournamentId,userId);

        int index = 0;
        for(LeaderboardRecord record: leaderboard){
            if(record.getUserId().equals(userId))
                return index + 1;
            index++;
        }
        return index + 1;
    }

    @Override
    public List<LeaderboardRecord> getLeaderboardOfGroup(Long groupId){
        TournamentGroup group = getTournamentGroup(groupId);
        List records = group.getLeaderboard();
        return sortRecordsAndReturn(records);
    }

    @Override
    @Transactional
    public void incrementScoreOfUser(Long userId) {

        TournamentGroup group = getTournamentGroup(DateFunctions.getCurrentDay(), userId);

        if(group == null)
            return;

        LeaderboardRecord record = group.getLeaderboard().stream().
                filter(p -> p.getUserId().equals(userId)).
                findFirst().get();

        record.setScore(record.getScore() + 1);
        record.setTimeLastUpdated(DateFunctions.getNow());
        tournamentGroupRepository.save(group);


    }

    @Override
    public List<MessageRecord> getLastMessagesFromGroup(Long groupId) {
        return chatGroupRepository.
                findFirst100ByGroupIdOrderBySentTimeAsc(getTournamentGroup(groupId));
    }

    @Override
    public MessageRecord sendMessageToTournamentGroup(String messageText, Long userId){
        User user = userService.getUser(userId);

        if(user==null)
            throw new UserNotFoundException("User not found");

        if(user.getIsClaimedLastReward().equals(Boolean.TRUE))
            throw new GroupNotFoundException("User not in a tournament");

        TournamentGroup group = getTournamentGroup(DateFunctions.getCurrentDay(),userId);

        MessageRecord messageRecord = MessageRecord.builder()
                .messageText(messageText)
                .senderUsername(user.getUsername())
                .groupId(group)
                .sentTime(DateFunctions.getNow()).build();
        chatGroupRepository.save(messageRecord);
        return messageRecord;
    }


}
