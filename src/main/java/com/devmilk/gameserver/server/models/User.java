package com.devmilk.gameserver.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;

    private String username;

    private Boolean isClaimedLastReward = Boolean.TRUE ;

    @Embedded
    private UserProgress userProgress;



    public User(String username){
        this.username=username;
        userProgress = new UserProgress();
    }

}
