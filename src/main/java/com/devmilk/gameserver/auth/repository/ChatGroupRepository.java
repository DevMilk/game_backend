package com.devmilk.gameserver.auth.repository;

import com.devmilk.gameserver.auth.models.MessageRecord;
import com.devmilk.gameserver.auth.models.TournamentGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatGroupRepository extends JpaRepository<MessageRecord, Long> {

    List<MessageRecord> findFirst100ByGroupIdOrderBySentTimeAsc(TournamentGroup group);
}
