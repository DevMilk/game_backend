package com.devmilk.gameserver.server.repository;

import com.devmilk.gameserver.server.models.MessageRecord;
import com.devmilk.gameserver.server.models.TournamentGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatGroupRepository extends JpaRepository<MessageRecord, Long> {

    List<MessageRecord> findFirst100ByGroupIdOrderBySentTimeAsc(TournamentGroup group);
}
