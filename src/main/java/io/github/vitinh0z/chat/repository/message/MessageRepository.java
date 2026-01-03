package io.github.vitinh0z.chat.repository.message;

import io.github.vitinh0z.chat.entities.message.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {

}
