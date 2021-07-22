package com.devmilk.gameserver.auth.repository;

import com.devmilk.gameserver.auth.models.Tournament;
import com.devmilk.gameserver.auth.models.TournamentGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TournamentGroupRepository extends JpaRepository<TournamentGroup, Long> {

    Optional<TournamentGroup> findByGroupId(Long groupId);


    @Modifying
    @Query(value = "INSERT INTO leaderboard_record (user_id, group_id, score) VALUES (:user_id, :group_id, 0)",
            nativeQuery = true)
    void addUserToLeaderBoard(@Param("user_id") Long userId, @Param("group_id") Long groupId);


    @Modifying
    @Query(value = "INSERT INTO leaderboard_group (group_id, level_range, tournament_day, group_creation_date) " +
            "VALUES (?1, ?2, ?3, ?4)",
            nativeQuery = true)
    void addTournamentGroup(Long groupId, int levelRange, Long tournamentDay, Long groupCreationDate );


    @Query(value = "SELECT group FROM TournamentGroup group WHERE " +
            "group.levelRange = ?1")
    TournamentGroup getLastCreatedGroupOfLevelRange(int levelRange);


    @Query(value = "SELECT group FROM TournamentGroup group WHERE group.groupId = ?1")
    TournamentGroup findGroup(Long groupId);


    @Query(value = "SELECT group FROM TournamentGroup group WHERE " +
            "group.tournamentDay = ?1")
    TournamentGroup findGroup(Long tournamentId,Long userId);

    /*@Query(value = "SELECT record FROM leaderboard_record record WHERE " +
            "record.group_id = ?1 " +
            "ORDER BY score DESC")
    List<LeaderboardRecord> findLeaderboardRecords(Long groupId);*/
}
