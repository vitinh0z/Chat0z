package io.github.vitinh0z.chat.service.message;

import io.github.vitinh0z.chat.dto.message.MessageResponseDTO;
import io.github.vitinh0z.chat.entities.membership.Membership;
import io.github.vitinh0z.chat.entities.message.Message;
import io.github.vitinh0z.chat.enums.room.RoomRole;
import io.github.vitinh0z.chat.repository.membership.MembershipRepository;
import io.github.vitinh0z.chat.repository.message.MessageRepository;
import io.github.vitinh0z.chat.utils.crypto.CryptoUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final MembershipRepository membershipRepository;


    public Message sendMessage (Long userId, Long roomId, String content){

        Membership membership = membershipRepository.findByUserIdAndRoomId(userId, roomId)
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

    public void excludeMessage(Long userId, Long messageId, Long roomId) {

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("message not found"));

        if (!message.getRoom().getId().equals(roomId)) {
            throw new IllegalArgumentException("this message not is this room");
        }

        Membership requester = membershipRepository.findByUserIdAndRoomId(userId, roomId)
                .orElseThrow(() -> new RuntimeException("you not is member of the this room"));

        boolean isSender = message.getUser().getId().equals(userId);
        boolean isAdmin = requester.getRoomRole() == RoomRole.OWNER || requester.getRoomRole() == RoomRole.ADMIN;

        if (isSender || isAdmin) {
            messageRepository.delete(message);
        } else {
            throw new IllegalArgumentException("you not have permission for delete this message");
        }
    }

    public List<MessageResponseDTO> getAllMessages (Long userId, Long roomId){

        Membership membership = membershipRepository.findByUserIdAndRoomId(userId, roomId)
                .orElseThrow(() -> new RuntimeException("User or Room not found"));

        String roomKey = membership.getRoom().getInviteCode();

        List<Message> encrypted = messageRepository.findByRoomIdOrderByTimestempAsc(roomId);

        return encrypted.stream().map(msm -> {
            String decryptedContent = CryptoUtils.decrypt(msm.getContent(), roomKey);


            return new MessageResponseDTO(msm.getId(),
                    decryptedContent,
                    msm.getUser().getNickname(),
                    msm.getTimestemp().toString()
            );

        }).toList();

    }
}
