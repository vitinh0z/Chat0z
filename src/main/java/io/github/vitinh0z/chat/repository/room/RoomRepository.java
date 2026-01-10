package io.github.vitinh0z.chat.repository.room;

import io.github.vitinh0z.chat.entities.room.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    Optional<Room> findByInviteCode(String inviteCode);

    List<Room> findByMembershipsUserId(Long userId);
}
