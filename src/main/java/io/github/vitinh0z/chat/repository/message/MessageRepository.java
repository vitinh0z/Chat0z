package io.github.vitinh0z.chat.repository.message;

import io.github.vitinh0z.chat.entities.message.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {

    Optional<Message> findByIdAndSenderId(Long messageId, Long userId);
}
