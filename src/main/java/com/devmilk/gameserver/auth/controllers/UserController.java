package com.devmilk.gameserver.auth.controllers;

import com.devmilk.gameserver.auth.exceptions.UserNotFoundException;
import com.devmilk.gameserver.auth.models.User;
import com.devmilk.gameserver.auth.service.UserService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user")
public class UserController {

	@Autowired
	UserService userService;

	@SneakyThrows
	@PostMapping("/levelup")
	public ResponseEntity levelupUser(@RequestParam("userId") Long userId)  {
			User updatedUser = userService.levelUp(userId);
			return ResponseEntity.ok(updatedUser.getUserProgress());

	}


	@PostMapping("/register")
	public ResponseEntity registerUser(@RequestParam("username") String username) {
		return ResponseEntity.ok(userService.register(username));
	}


}