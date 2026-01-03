package io.github.vitinh0z.chat.entities.user;

import io.github.vitinh0z.chat.entities.membership.Membership;
import io.github.vitinh0z.chat.entities.room.Room;
import io.github.vitinh0z.chat.enums.user.UserStatus;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private UserStatus userStatus;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String email;

    private String picProfile;

    @OneToMany(mappedBy = "owner")
    private List<Room> roomsCreated = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Membership> participating = new ArrayList<>();


}
