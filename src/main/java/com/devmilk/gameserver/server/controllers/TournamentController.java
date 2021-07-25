package com.devmilk.gameserver.server.controllers;


import com.devmilk.gameserver.server.models.LeaderboardRecord;
import com.devmilk.gameserver.server.models.MessageRecord;
import com.devmilk.gameserver.server.models.UserProgress;
import com.devmilk.gameserver.server.payload.LeaderboardRecordDTO;
import com.devmilk.gameserver.server.payload.MessageRecordDTO;
import com.devmilk.gameserver.server.payload.UserProgressDTO;
import com.devmilk.gameserver.server.service.TournamentService;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

//@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/tournament")
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private ModelMapper modelMapper;

    <S, T> List<T> mapList(List<S> source, Class<T> targetClass) {
        return source
                .stream()
                .map(element -> modelMapper.map(element, targetClass))
                .collect(Collectors.toList());
    }

    @SneakyThrows
    @PostMapping("/enter")
    public ResponseEntity<List<LeaderboardRecordDTO>> registerToCurrentTournament(@RequestParam Long userId) {
        List<LeaderboardRecordDTO> leaderboardDTO = mapList(
                tournamentService.register(userId), LeaderboardRecordDTO.class);
        return ResponseEntity.ok(leaderboardDTO);
    }

    @SneakyThrows
    @PostMapping("/claim")
    public ResponseEntity<UserProgressDTO> claimTournamentReward(@RequestParam Long tournamentDay, @RequestParam Long userId) {
        UserProgressDTO userProgressDTO = modelMapper.map(
                tournamentService.claim(tournamentDay, userId), UserProgressDTO.class);
        return ResponseEntity.ok(userProgressDTO);
    }

    @SneakyThrows
    @GetMapping("/rank")
    public ResponseEntity<Integer> getTournamentRankOfUser(@RequestParam Long tournamentDay, @RequestParam Long userId) {
        int rank = tournamentService.getRankOfUserInTournament(tournamentDay, userId);
        return ResponseEntity.ok(rank);
    }

    @GetMapping("/")
    @SneakyThrows
    public ResponseEntity<List<LeaderboardRecordDTO>> getLeaderboard(@RequestParam Long groupId) {
        List<LeaderboardRecordDTO> leaderboardDTO = mapList(
                tournamentService.getLeaderboardOfGroup(groupId),LeaderboardRecordDTO.class);
        return ResponseEntity.ok(leaderboardDTO);
    }

    @GetMapping("/chat")
    public ResponseEntity<List<MessageRecordDTO>> getLastMessages(@RequestParam Long groupId) {
        List<MessageRecordDTO> lastMessagesDTO = mapList(
                tournamentService.getLastMessagesFromGroup(groupId),MessageRecordDTO.class);
        return ResponseEntity.ok(lastMessagesDTO);
    }

    @PostMapping("/chat")
    public ResponseEntity<MessageRecordDTO> sendMessage(@RequestParam Long userId, @RequestParam String messageText) {
        MessageRecordDTO messageRecordDTO = modelMapper.map(
                tournamentService.sendMessageToTournamentGroup(messageText, userId),MessageRecordDTO.class);
        return ResponseEntity.ok(messageRecordDTO);
    }
}
