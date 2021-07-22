package com.devmilk.gameserver.auth.models;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@Table
@NoArgsConstructor
public class Tournament {
    @Id
    @Column(name="tournament_day")
    private Long tournamentDay;

    @OneToMany(cascade = CascadeType.ALL)
            @JoinColumn(name= "tournament_day")
    List<TournamentGroup> tournamentGroups;

    public Tournament (Long tournamentDay){
        this.tournamentDay = tournamentDay;
        tournamentGroups = new ArrayList<>();
    }
}
