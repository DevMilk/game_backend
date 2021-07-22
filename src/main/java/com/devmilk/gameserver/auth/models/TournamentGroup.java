package com.devmilk.gameserver.auth.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="tournament_group")
@Getter
@Setter
@NoArgsConstructor
public class TournamentGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "group_id")
    private Long groupId;

    private int levelRange;

    @Column(name = "tournament_day")
    private Long tournamentDay;

    private Long groupCreationDate;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    List<LeaderboardRecord> leaderboard;

    public TournamentGroup(int levelRange, Long groupCreationDate){
        this.levelRange = levelRange;
        this.groupCreationDate = groupCreationDate;
        leaderboard = new ArrayList<>();
    }
    /*@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="tournamentDay")
    Tournament tournament;*/

}
