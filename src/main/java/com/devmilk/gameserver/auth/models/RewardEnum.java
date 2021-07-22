package com.devmilk.gameserver.auth.models;

import lombok.Getter;

@Getter
public enum RewardEnum {
    TOP_TEN(10,10000),
    THIRD_PLACE(3,3000),
    SECOND_PLACE(2,5000),
    WINNER(1,10000);

    private final int baseOrder;
    private final int reward;

    RewardEnum(int baseOrder, int reward) {
        this.baseOrder = baseOrder;
        this.reward = reward;
    }

}
