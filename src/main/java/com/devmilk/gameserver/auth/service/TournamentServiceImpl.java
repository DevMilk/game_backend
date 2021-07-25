package com.devmilk.gameserver.auth.service;

import com.devmilk.gameserver.auth.config.GAME_CONSTANTS;
import com.devmilk.gameserver.auth.exceptions.ConditionsDoesntMetException;
import com.devmilk.gameserver.auth.exceptions.GroupNotFoundException;
import com.devmilk.gameserver.auth.exceptions.TournamentNotFoundException;
import com.devmilk.gameserver.auth.exceptions.UserNotFoundException;
import com.devmilk.gameserver.auth.models.*;
import com.devmilk.gameserver.auth.repository.ChatGroupRepository;
import com.devmilk.gameserver.auth.repository.TournamentGroupRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;


@Service
public class TournamentServiceImpl implements TournamentService{

    @Autowired
    private UserService userService;

    @Autowired
    private TournamentGroupRepository tournamentGroupRepository;

    @Autowired
    private ChatGroupRepository chatGroupRepository;

    private static boolean[] groupCreationHashMap = new boolean[Math.floorDiv(GAME_CONSTANTS.MAX_LEVEL,GAME_CONSTANTS.GROUP_LEVEL_RANGE)];

    private int calculateReward(int userRank){
        int reward = 0;

        //Calculate reward by iterating over reward enum
        for(GAME_CONSTANTS.RewardEnum place: GAME_CONSTANTS.RewardEnum.values())
            if(userRank> place.getBaseOrder())
                return reward;
            else
                reward = place.getReward();

        return reward;

    }

    private List<LeaderboardRecord> sortRecordsAndReturn(List<LeaderboardRecord> records) throws GroupNotFoundException {
        if(records.isEmpty())
            throw new GroupNotFoundException("Group not found");

        //Sort Leaderboard by score and last updated time
        records.sort(Comparator.comparingInt(LeaderboardRecord::getScore).reversed()
                .thenComparingLong(LeaderboardRecord::getTimeLastUpdated));

        return records;
    }

    private TournamentGroup getTournamentGroup(Long groupId) throws GroupNotFoundException, ConditionsDoesntMetException {
        //Search tournament group in repository
        Optional<TournamentGroup> group = tournamentGroupRepository.findByGroupId(groupId);

        //Check if group exists
        if(group==null || !group.isPresent())
            throw new GroupNotFoundException("Group not found");

        //If group's tournament day is expired throw exception
        if(group.get().getTournamentDay()<DateFunctions.getCurrentTournamentDay()-5)
            throw new ConditionsDoesntMetException("User can't get leaderboards of tournaments before last 5 tournaments");

        return group.get();
    }

    private TournamentGroup getTournamentGroup(Long tournamentDay, Long userId) throws GroupNotFoundException {
        //Search for a tournament group which in given tournament and have a user within
        TournamentGroup group = tournamentGroupRepository.findGroup(tournamentDay, userId);

        //Check if group exists
        if(group== null)
            throw new GroupNotFoundException("Group not found");

        return group;
    }

    private List<LeaderboardRecord> getLeaderboardOfGroup(Long tournamentDay, Long userId) throws GroupNotFoundException {
        List<LeaderboardRecord> records = getTournamentGroup(tournamentDay, userId).getLeaderboard();
        return sortRecordsAndReturn(records);
    }

    private Boolean getIsUserHaveARewardToClaim(User user){

        //Get last leaderboard record of user
        Optional<Long> lastGroupIdOfUser = tournamentGroupRepository
                .getLastRecordOfUser(user.getUserId());

        //If no record found, then user have never entered a tournament so cant have a reward to claim
        if(lastGroupIdOfUser == null || !lastGroupIdOfUser.isPresent())
            return Boolean.FALSE;

        //Get group from leaderboard record of user
        TournamentGroup lastEnteredTournamentGroupOfUser = getTournamentGroup(lastGroupIdOfUser.get());

        List<LeaderboardRecord> leaderboardOfLastEnteredTournamentOfUser = lastEnteredTournamentGroupOfUser.getLeaderboard();
        Long lastEnteredTournamentDay = lastEnteredTournamentGroupOfUser.getTournamentDay();

        //If user cannot claim a reward from its last entered tournament, check it as claimed
        int reward = getRankOfGivenLeaderboardAndUser(leaderboardOfLastEnteredTournamentOfUser,user.getUserId());

        return !(lastEnteredTournamentDay<DateFunctions.getCurrentTournamentDay()-5
                || reward==0 || user.getIsClaimedLastReward().equals(Boolean.TRUE));
    }

    private int getRankOfGivenLeaderboardAndUser(List<LeaderboardRecord> leaderboard, Long userId) {
        int index = 0;
        for(LeaderboardRecord record: leaderboard){
            if(record.getUserId().equals(userId))
                return index + 1;
            index++;
        }
        return index + 1;
    }

    @Override
    @Transactional
    public List<LeaderboardRecord> register(Long userId){

        Long currentDay = DateFunctions.getCurrentTournamentDay() ;

        //Check user attributes
        User user = userService.getUser(userId);
        UserProgress userProgress = user.getUserProgress();
        int userLevel = userProgress.getLevel();
        int userCoins = userProgress.getCoins();

        //Check conditions to enter a tournament
        if(userCoins<GAME_CONSTANTS.TOURNAMENT_ENTRANCE_FEE)
            throw new ConditionsDoesntMetException("User must pay "+GAME_CONSTANTS.TOURNAMENT_ENTRANCE_FEE + " coins to enter a tournament");

        if(userLevel<GAME_CONSTANTS.TOURNAMENT_ENTRANCE_LEVEL_REQUIREMENT)
            throw new ConditionsDoesntMetException("User's level must be more or equal than "+
                    GAME_CONSTANTS.TOURNAMENT_ENTRANCE_LEVEL_REQUIREMENT+" to enter a tournament");

        /*
        Check If user have a reward to claim, in order to return false:
         1. user's last entered tournament may be expired or
         2. user may not have any previous entered tournament or
         3. user may not have a reward to claim from last entered tournament
         4. user claimed last reward
         */
        if(getIsUserHaveARewardToClaim(user).equals(Boolean.TRUE))
            throw new ConditionsDoesntMetException("User must claim last entered tournament's reward or already in a tournament");

        //get user level range (0 -> [20,100), 1-> [100,200), 2->[200,300) and goes on )
        Integer userLevelRange = Math.floorDiv(userLevel,GAME_CONSTANTS.GROUP_LEVEL_RANGE);

        /*
        Tournament group creation must be synchronized for same ranges in order to
        avoid creating multiple redundant group for same range
         */
        //TODO: concurrency olmayabilir
        TournamentGroup group;
        synchronized (this) {
            // Create new tournament group
            group = tournamentGroupRepository.getLastCreatedGroupOfLevelRange(userLevelRange);

            //If group full or not exists or not today's daily, create new one
            if (group == null || group.getLeaderboard().size() >= GAME_CONSTANTS.GROUP_SIZE_LIMIT
                    || group.getTournamentDay()<DateFunctions.getCurrentTournamentDay()) {

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

        //Save User
        userService.updateOrCreate(user);

        //Return current leaderboard
        return group.getLeaderboard();
    }

    @Override
    @SneakyThrows
    @Transactional
    public UserProgress claim(Long tournamentDay, Long userId) {

        //Check conditions to claim reward
        if(tournamentDay>DateFunctions.getCurrentTournamentDay())
            throw new TournamentNotFoundException("Tournament not exists");

        if (tournamentDay.equals(DateFunctions.getCurrentTournamentDay()))
            throw new ConditionsDoesntMetException("Tournament have not finished yet");

        if(tournamentDay<DateFunctions.getCurrentTournamentDay()-5)
            throw new ConditionsDoesntMetException("User can't claim rewards before last 5 tournaments");

        User user = userService.getUser(userId);

        if(user.getIsClaimedLastReward().equals(Boolean.TRUE))
            throw new GroupNotFoundException("User not in a tournament");

        //Calculate reward by checking rank
        int rank = getRankOfUserInTournament(tournamentDay,userId);
        int reward = calculateReward(rank);

        //Update user entity
        user.setIsClaimedLastReward(Boolean.TRUE);
        UserProgress progress = user.getUserProgress();
        progress.setCoins(progress.getCoins()+reward);
        user.setUserProgress(progress);
        userService.updateOrCreate(user);

        return user.getUserProgress();

    }

    @Override
    public int getRankOfUserInTournament(Long tournamentDay, Long userId) {
        List<LeaderboardRecord> leaderboard = getLeaderboardOfGroup(tournamentDay,userId);
        return getRankOfGivenLeaderboardAndUser(leaderboard,userId);
    }

    @Override
    public List<LeaderboardRecord> getLeaderboardOfGroup(Long groupId){
        //Get tournament group
        TournamentGroup group = getTournamentGroup(groupId);

        //Get leaderboard of group
        List<LeaderboardRecord> records = group.getLeaderboard();

        //Sort leaderboard and return sorted list
        return sortRecordsAndReturn(records);
    }

    @Override
    @Transactional
    public void incrementScoreOfUser(Long userId) {
        //Get current tournament group of user
        TournamentGroup group = getTournamentGroup(DateFunctions.getCurrentTournamentDay(), userId);

        //Get user's record from current tournament group's full leaderboard
        LeaderboardRecord record = group.getLeaderboard().stream().
                filter(p -> p.getUserId().equals(userId)).
                findFirst().get();

        //update score and last updated time of score and save changes on database
        record.setScore(record.getScore() + 1);
        record.setTimeLastUpdated(DateFunctions.getNow());
        tournamentGroupRepository.save(group);
    }

    @Override
    public List<MessageRecord> getLastMessagesFromGroup(Long groupId) {
        //Get last 100 messages (older messages first)
        return chatGroupRepository.
                findFirst100ByGroupIdOrderBySentTimeAsc(getTournamentGroup(groupId));
    }

    @Override
    public MessageRecord sendMessageToTournamentGroup(String messageText, Long userId){
        User user = userService.getUser(userId);

        //Control user
        if(user==null)
            throw new UserNotFoundException("User not found");

        if(user.getIsClaimedLastReward().equals(Boolean.TRUE))
            throw new GroupNotFoundException("User not in a tournament");

        //Get group
        TournamentGroup group = getTournamentGroup(DateFunctions.getCurrentTournamentDay(),userId);

        //Save new message record on chat repository
        MessageRecord messageRecord = MessageRecord.builder()
                .messageText(messageText)
                .senderUsername(user.getUsername())
                .groupId(group)
                .sentTime(DateFunctions.getNow()).build();
        chatGroupRepository.save(messageRecord);
        return messageRecord;
    }


}
