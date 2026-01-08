package io.github.vitinh0z.chat.service.message;

import io.github.vitinh0z.chat.entities.membership.Membership;
import io.github.vitinh0z.chat.entities.message.Message;
import io.github.vitinh0z.chat.enums.room.RoomRole;
import io.github.vitinh0z.chat.repository.membership.MembershipRepository;
import io.github.vitinh0z.chat.repository.message.MessageRepository;
import io.github.vitinh0z.chat.utils.crypto.CryptoUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final MembershipRepository membershipRepository;


    public Message sendMessage (Long userId, Long roomId, String content){

        Membership membership = membershipRepository.findByUserAndRoomId(userId, roomId)
                .orElseThrow(() -> new RuntimeException("Você precisar estar na sala para enviar mensagem")
        );

        String roomPassword = membership.getRoom().getInviteCode();

        String encryptedContent = CryptoUtils.encrypt(content, roomPassword);

        Message message = new Message();
        message.setContent(encryptedContent);
        message.setUser(membership.getUser());
        message.setRoom(membership.getRoom());
        message.setTimestemp(LocalDateTime.now());

        return messageRepository.save(message);
    }

    public void excludeMessage(Long userId, Long messageId, Long roomId ) {

        Message message = messageRepository.findByIdAndSenderId(messageId, userId).orElseThrow();

        if (message.getUser().getId().equals(userId)) {
            messageRepository.delete(message);
        }

        Membership solicited = membershipRepository.findByUserAndRoomId(userId, roomId)
                .orElseThrow(() -> new RuntimeException("Precisa estar na sala da mensagem")
        );

        if (solicited.getRoomRole() == RoomRole.OWNER || solicited.getRoomRole() == RoomRole.ADMIN){
            messageRepository.delete(message);
        }
        else {
            throw new IllegalArgumentException("You Dont have permission for delete the message");
        }
    }
}
