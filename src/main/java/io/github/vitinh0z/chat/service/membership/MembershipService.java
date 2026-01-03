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


    //enterRoom(User user, Room room)

    public void enterRoom(User user, Room room){

        Optional<Membership> member = membershipRepository.findByUserAndRoomId(user.getId(), room.getId());

        if(member.isPresent()){
            throw new IllegalArgumentException("Ja é membro da sala");
        }

        Membership membership = new Membership();
        membership.setUser(user);
        membership.setRoom(room);
        membership.setRoomRole(RoomRole.MEMBERSHIP);

        membershipRepository.save(membership);

    }

    //exitRoom(User user, Room room)
    public void exitRoom(User user, Room room){

        Optional<Membership> member = membershipRepository.findByUserAndRoomId(user.getId(), room.getId());

        if (member.isEmpty()){
            throw new IllegalArgumentException("Voce não pertence ao grupo");
        }

        membershipRepository.delete(member.get());

    }


    //AlternRole (User Owner, User member, Room room, RoomRole role) verificar se user é owner
    public void AlternRole(User owner, User member, Room room, Room role){



    }


    public void BanUser(User solicitante, User alvo, Room room){

    }

}
