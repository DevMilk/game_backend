package com.devmilk.gameserver.server.repository;

import com.devmilk.gameserver.server.models.LeaderboardRecord;
import com.devmilk.gameserver.server.models.LeaderboardRecordID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface LeaderboardRepository extends JpaRepository<LeaderboardRecord, LeaderboardRecordID> {

    @Query(value = "SELECT * FROM leaderboard_record WHERE " +
            "user_id = ?1 AND time_last_updated = " +
            "(SELECT MAX(time_last_updated) FROM leaderboard_record WHERE user_id = ?1)"
            ,nativeQuery = true)
    Optional<LeaderboardRecord> getLastLeaderboardRecordOfUser(Long userId);
}
