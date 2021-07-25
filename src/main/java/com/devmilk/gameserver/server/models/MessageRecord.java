package com.devmilk.gameserver.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;


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
    private Long messageId;

    private Long sentTime;
    private String senderUsername;
    private String messageText;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    @JsonIgnore
    private TournamentGroup groupId;

}
