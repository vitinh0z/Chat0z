package io.github.vitinh0z.chat.repository.room;

import io.github.vitinh0z.chat.entities.room.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {

}
