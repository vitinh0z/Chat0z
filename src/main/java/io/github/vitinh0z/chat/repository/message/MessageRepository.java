package io.github.vitinh0z.chat.repository.message;

import io.github.vitinh0z.chat.entities.message.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByRoomIdOrderByTimestempAsc(Long roomId);
}
