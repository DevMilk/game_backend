package com.devmilk.gameserver.auth.controllers;


import com.devmilk.gameserver.auth.models.LeaderboardRecord;
import com.devmilk.gameserver.auth.models.MessageRecord;
import com.devmilk.gameserver.auth.models.UserProgress;
import com.devmilk.gameserver.auth.service.TournamentService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/tournament")
public class TournamentController {

	@Autowired
	private TournamentService tournamentService;

	// LeaderboardRecord'ı döndür
	@SneakyThrows
	@PostMapping("/enter")
	public ResponseEntity<List<LeaderboardRecord>> registerToCurrentTournament(@RequestParam Long userId) {
		List<LeaderboardRecord> leaderboard = tournamentService.register(userId);
		return ResponseEntity.ok(leaderboard);
	}
	
	@SneakyThrows
	@GetMapping("/claim")
	public ResponseEntity<UserProgress> claimTournamentReward(@RequestParam Long tournamentDay, @RequestParam Long userId) {
		UserProgress userProgress = tournamentService.claim(tournamentDay, userId);
		return ResponseEntity.ok(userProgress);
	}
	@SneakyThrows
	@GetMapping("/rank")
	public ResponseEntity<Integer> getTournamentRankOfUser(@RequestParam Long tournamentDay, @RequestParam Long userId) {
		int rank = tournamentService.getRankOfUserInTournament(tournamentDay,userId);
		return ResponseEntity.ok(rank);
	}

	@GetMapping("/")
	@SneakyThrows
	public ResponseEntity<List<LeaderboardRecord>> getLeaderboard(@RequestParam Long groupId) {
		List<LeaderboardRecord> leaderboard = tournamentService.getLeaderboardOfGroup(groupId);
		return ResponseEntity.ok(leaderboard);
	}

	@GetMapping("/chat")
	public ResponseEntity<List<MessageRecord>> getLastMessages(@RequestParam Long groupId) {
		List<MessageRecord> lastMessages = tournamentService.getLastMessagesFromGroup(groupId);
		return ResponseEntity.ok(lastMessages);
	}
	@PostMapping("/chat")
	public ResponseEntity<MessageRecord> sendMessage(@RequestParam Long userId, @RequestParam String messageText) {
		MessageRecord messageRecord = tournamentService.sendMessageToTournamentGroup(messageText, userId);
		return ResponseEntity.ok(messageRecord);
	}
}
