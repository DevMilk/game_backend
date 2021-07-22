package com.devmilk.gameserver.auth.controllers;

import com.devmilk.gameserver.auth.exceptions.ConditionsDoesntMetException;
import com.devmilk.gameserver.auth.exceptions.GroupNotFoundException;
import com.devmilk.gameserver.auth.exceptions.TournamentNotFoundException;
import com.devmilk.gameserver.auth.exceptions.UserNotFoundException;
import com.devmilk.gameserver.auth.models.UserProgress;
import com.devmilk.gameserver.auth.service.TournamentService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/tournament")
public class TournamentController {

	@Autowired
	private TournamentService tournamentService;

	private int getDayNumber(){
		return 0;
	}

	// LeaderboardRecord'ı döndür
	@SneakyThrows
	@PostMapping("/enter")
	public ResponseEntity registerToCurrentTournament(@RequestParam Long userId) {
		List leaderboard = tournamentService.register(userId);
		return ResponseEntity.ok(leaderboard);
	}
	
	@SneakyThrows
	@GetMapping("/claim")
	public ResponseEntity claimTournamentReward(@RequestParam Long tournamentId, @RequestParam Long userId) {
		UserProgress userProgress = tournamentService.claim(tournamentId, userId);
		return ResponseEntity.ok(userProgress);
	}
	@SneakyThrows
	@GetMapping("/rank")
	public ResponseEntity getTournamentRankOfUser(@RequestParam Long tournamentId, @RequestParam Long userId) {
		int rank = tournamentService.getRankOfUserInTournament(tournamentId,userId);
		return ResponseEntity.ok(rank);
	}

	@GetMapping("/")
	@SneakyThrows
	public ResponseEntity getLeaderboard(@RequestParam Long groupId) {
		List leaderboard = tournamentService.getLeaderboardOfGroup(groupId);
		return ResponseEntity.ok(leaderboard);
	}
}
