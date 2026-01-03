package io.github.vitinh0z.chat.entities.invite;

import io.github.vitinh0z.chat.entities.room.Room;
import io.github.vitinh0z.chat.entities.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Invite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String urlInvite;

    private LocalDateTime expireAt;

    private Integer maxUse;

    private Integer currentUses = 0;


    public boolean isValid (){

        if(getExpireAt() != null && LocalDateTime.now().isAfter(expireAt)){
            return false;
        }
        return getMaxUse() == null || getCurrentUses() < getMaxUse();
    }
}
