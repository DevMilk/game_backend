package com.devmilk.gameserver.auth.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class UserProgress {

    private int coins = 5000;
    private int level = 1;

    public void levelUp(){
        coins += 25;
        level += 1;
    }
}
