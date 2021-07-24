package com.devmilk.gameserver.auth.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.*;
import org.springframework.boot.context.properties.bind.DefaultValue;

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
