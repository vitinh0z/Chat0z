package io.github.vitinh0z.chat.controller.chat;


import io.github.vitinh0z.chat.dto.message.MessageDeleteRequestDTO;
import io.github.vitinh0z.chat.dto.message.MessageDeleteResponseDTO;
import io.github.vitinh0z.chat.dto.message.MessageRequestDTO;
import io.github.vitinh0z.chat.dto.message.MessageResponseDTO;
import io.github.vitinh0z.chat.entities.message.Message;
import io.github.vitinh0z.chat.service.message.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final MessageService messageService;

    @MessageMapping("/chat/{roomId}")
    @SendTo("topic/room/{roomId}")
    public MessageResponseDTO sendMessage(@DestinationVariable Long roomId,
                                          @Payload MessageRequestDTO requestDTO
    ){

        Message saveMessage = messageService.sendMessage(
                requestDTO.userId(),
                roomId,
                requestDTO.content()
        );

        return MessageResponseDTO.fromEntity(saveMessage);
    }

    @MessageMapping("/chat/{roomId}/delete")
    @SendTo("/topic/room/{roomId}")
    public Object deleteMessage(@DestinationVariable Long roomId,
                                @Payload MessageDeleteRequestDTO request
    ){
        messageService.excludeMessage(request.userId(), request.messageId(), roomId);
        return new MessageDeleteResponseDTO(request.messageId());
    }
}
