package com.devmilk.gameserver.server.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
@IdClass(LeaderboardRecordID.class)
@Getter
@Setter
@Builder(toBuilder = true)
public class LeaderboardRecord {

    @Id
    @Column(name = "user_id")
    private Long userId;

    private String username;
    private int score = 0;

    private Long timeLastUpdated;

    @ManyToOne
    @Id
    @JoinColumn(name = "group_id" )
    @JsonIgnore
    private TournamentGroup groupId;

}
