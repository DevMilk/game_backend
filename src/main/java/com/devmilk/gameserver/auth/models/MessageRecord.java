package com.devmilk.gameserver.auth.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Date;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
@Entity
@Table(name = "message_record")
public class MessageRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private Long messageId;

    private Long sentTime;
    private String senderUsername;
    private String messageText;


    @ManyToOne
    @JoinColumn(name = "group_id")
    @JsonIgnore
    private TournamentGroup groupId;

}