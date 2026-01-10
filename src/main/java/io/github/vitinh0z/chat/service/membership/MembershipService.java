package io.github.vitinh0z.chat.service.membership;

import io.github.vitinh0z.chat.entities.membership.Membership;
import io.github.vitinh0z.chat.entities.room.Room;
import io.github.vitinh0z.chat.entities.user.User;
import io.github.vitinh0z.chat.enums.room.RoomRole;
import io.github.vitinh0z.chat.repository.membership.MembershipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MembershipService {

    private final MembershipRepository membershipRepository;

    public void enterRoom(User user, Room room){

        Optional<Membership> member = membershipRepository.findByUserIdAndRoomId(user.getId(), room.getId());

        if(member.isPresent()){
            throw new IllegalArgumentException("Ja é membro da sala");
        }

        Membership membership = new Membership();
        membership.setUser(user);
        membership.setRoom(room);
        membership.setRoomRole(RoomRole.MEMBER);

        membershipRepository.save(membership);
    }

    public void exitRoom(Long userId, Long roomId){

        Membership member = membershipRepository.findByUserIdAndRoomId(userId, roomId)
                .orElseThrow(() -> new RuntimeException("User or Room not found")
        );

        if (member.getRoomRole() == RoomRole.OWNER){
            throw new IllegalArgumentException("You cannot exit. Transfer ownership or delete the room."
            );
        }
        membershipRepository.delete(member);
    }

    public void alternRole(User owner, User member, Room room, RoomRole role){

        Membership requester = membershipRepository.findByUserIdAndRoomId(owner.getId(), room.getId())
                .orElseThrow(() -> new RuntimeException("User or Room not Found")
        );

        if (requester.getRoomRole() != RoomRole.OWNER){
            throw new IllegalArgumentException("Only Owner can change role");
        }

        Membership membership = membershipRepository.findByUserIdAndRoomId(member.getId(), room.getId())
                .orElseThrow(() -> new RuntimeException("User or Room not Found")
        );

        membership.setRoomRole(role);
        membershipRepository.save(membership);
    }

    public void banUser(User solicitante, User alvo, Room room){

        Membership requester = membershipRepository.findByUserIdAndRoomId(solicitante.getId(), room.getId())
                .orElseThrow(() -> new RuntimeException("User or Room not Found")
        );

        Membership member = membershipRepository.findByUserIdAndRoomId(alvo.getId(), room.getId())
                .orElseThrow(() -> new RuntimeException("User or Room not found"));

        if (requester.getRoomRole() == RoomRole.MEMBER || requester.getRoomRole() == RoomRole.SPECTATOR){
            throw new IllegalArgumentException("You dont have permission to ban user");
        }

        if(member.getRoomRole() == RoomRole.OWNER){
            throw new IllegalArgumentException("You cannot ban the Admin");
        }

        if(requester.getRoomRole() == RoomRole.ADMIN && member.getRoomRole() == RoomRole.ADMIN){
            throw new IllegalArgumentException("You cannot ban another Admin");
        }
        membershipRepository.delete(member);
    }

}
