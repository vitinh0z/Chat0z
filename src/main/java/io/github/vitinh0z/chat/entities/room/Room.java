package io.github.vitinh0z.chat.entities.room;

import io.github.vitinh0z.chat.entities.membership.Membership;
import io.github.vitinh0z.chat.entities.message.Message;
import io.github.vitinh0z.chat.entities.user.User;
import io.github.vitinh0z.chat.enums.room.RoomRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rooms")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String roomName;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    private String inviteCode;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<Membership> memberships = new ArrayList<>();

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<Message> messages = new ArrayList<>();

}
