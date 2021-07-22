package com.devmilk.gameserver.auth.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;

    @JsonIgnore
    private String username;

    @JsonIgnore
    private Boolean isClaimedLastReward = Boolean.TRUE ;

    @Embedded
    private UserProgress userProgress;



    public User(String username){
        this.username=username;
        userProgress = new UserProgress();
    }

}
