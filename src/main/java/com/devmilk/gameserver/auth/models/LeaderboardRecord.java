package com.devmilk.gameserver.auth.models;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table
@NoArgsConstructor
@IdClass(RecordID.class)
@Getter
@Setter
public class LeaderboardRecord {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "group_id")
    @Id
    private Long groupId;

    private int score = 0;

    @OneToOne( cascade = CascadeType.ALL)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    public LeaderboardRecord(Long userId){
        this.userId = userId;
    }
    public LeaderboardRecord(User user){
        this.user = user;
    }

}
