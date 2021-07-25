package com.devmilk.gameserver.server.config;

import lombok.Getter;

public class GAME_SETTINGS {
    public static final int TOURNAMENT_ENTRANCE_FEE = 1000;
    public static final int TOURNAMENT_ENTRANCE_LEVEL_REQUIREMENT = 20;
    public static final int GROUP_SIZE_LIMIT = 20;
    public static final int GROUP_LEVEL_RANGE = 100;
    public static final int STARTING_COIN_AMOUNT = 5000;
    public static final int COIN_INCREASE_AMOUNT_PER_LEVEL = 25;
    public static final int MAX_LEVEL = 2000;
    public static final int TOURNAMENT_FINISH_HOUR_UTC = 20;

    @Getter
    public enum RewardEnum {
        TOP_TEN(10, 10000),
        THIRD_PLACE(3, 3000),
        SECOND_PLACE(2, 5000),
        WINNER(1, 10000);

        private final int baseOrder;
        private final int reward;

        RewardEnum(int baseOrder, int reward) {
            this.baseOrder = baseOrder;
            this.reward = reward;
        }

    }
}
