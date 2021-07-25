package com.devmilk.gameserver.auth.models;

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
