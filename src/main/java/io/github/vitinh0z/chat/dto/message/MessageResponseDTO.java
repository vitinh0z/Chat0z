package io.github.vitinh0z.chat.dto.message;

import io.github.vitinh0z.chat.entities.message.Message;

public record MessageResponseDTO(
        Long id,
        String content,
        String senderName,
        String sendAt
) {

    public static MessageResponseDTO fromEntity(Message message){
        return new MessageResponseDTO(
                message.getId(),
                message.getContent(),
                message.getUser().getNickname(),
                message.getTimestemp().toString()
        );
    }
}
