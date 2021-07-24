package com.devmilk.gameserver.auth.repository;


import com.devmilk.gameserver.auth.models.LeaderboardRecord;
import com.devmilk.gameserver.auth.models.MessageRecord;
import com.devmilk.gameserver.auth.models.TournamentGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface TournamentGroupRepository extends JpaRepository<TournamentGroup, Long> {

    Optional<TournamentGroup> findByGroupId(Long groupId);

    @Query(value = "SELECT group FROM " +
            "TournamentGroup group INNER JOIN LeaderboardRecord record " +
            "ON group.groupId= record.groupId " +
            "WHERE group.tournamentDay = ?1 AND record.userId = ?2")
    TournamentGroup findGroup(Long tournamentId,Long userId);

    @Query(value = "SELECT * FROM tournament_group WHERE " +
            "level_range = ?1 AND group_creation_date = "+
           "(SELECT MAX(group_creation_date) FROM tournament_group)"
            ,nativeQuery = true)
    TournamentGroup getLastCreatedGroupOfLevelRange(int levelRange);

    @Query(value = "SELECT max(grp.tournament_day) FROM " +
            "tournament_group grp INNER JOIN leaderboard_record record " +
            "ON grp.group_id= record.group_id " +
            "WHERE record.user_id=?1",nativeQuery = true)
    Long getLastTournamentDayOfUser(Long userId);

    /*@Query(value = "SELECT msg FROM message_record msg " +
            "WHERE msg.group_id = ?1 " +
            "ORDER BY sent_time ASC " +
            "LIMIT 100", nativeQuery = true)
    List<MessageRecord> getLastMessagesOfGroup(Long groupId);

    @Modifying
    @Query(value = "")
    void addMessageToGroup(MessageRecord messageRecord);*/
}
