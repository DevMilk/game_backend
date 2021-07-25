package com.devmilk.gameserver.server.controllers;

import com.devmilk.gameserver.server.models.User;
import com.devmilk.gameserver.server.models.UserProgress;
import com.devmilk.gameserver.server.payload.UserDTO;
import com.devmilk.gameserver.server.payload.UserProgressDTO;
import com.devmilk.gameserver.server.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    @PatchMapping("/levelup")
    public ResponseEntity<UserProgressDTO> levelupUser(@RequestParam("userId") Long userId) {
        UserProgressDTO userProgressDTO = modelMapper.map(
                userService.levelUp(userId),UserProgressDTO.class);
        return ResponseEntity.ok(userProgressDTO);
    }


    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestParam("username") String username) {
        UserDTO userDTO = modelMapper.map(
                userService.register(username),UserDTO.class);
        return ResponseEntity.ok(userDTO);
    }


}
