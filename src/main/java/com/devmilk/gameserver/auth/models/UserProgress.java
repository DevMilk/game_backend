package com.devmilk.gameserver.auth.models;

import com.devmilk.gameserver.auth.config.GAME_CONSTANTS;
import lombok.*;

import javax.persistence.Embeddable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserProgress {

    private int coins;
    private int level;

}
