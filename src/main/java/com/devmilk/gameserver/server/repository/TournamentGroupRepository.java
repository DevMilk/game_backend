package com.devmilk.gameserver.server.repository;


import com.devmilk.gameserver.server.models.TournamentGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TournamentGroupRepository extends JpaRepository<TournamentGroup, Long> {

    Optional<TournamentGroup> findByGroupId(Long groupId);

    @Query(value = "SELECT group FROM " +
            "TournamentGroup group INNER JOIN LeaderboardRecord record " +
            "ON group.groupId= record.groupId " +
            "WHERE group.tournamentDay = ?1 AND record.userId = ?2")
    TournamentGroup findGroup(Long tournamentDay,Long userId);

    @Query(value = "SELECT * FROM tournament_group WHERE " +
            "level_range = ?1 AND group_creation_date = "+
            "(SELECT MAX(group_creation_date) FROM tournament_group WHERE level_range = ?1)"
            ,nativeQuery = true)
    TournamentGroup getLastCreatedGroupWithGivenLevelRange(int levelRange);




}
