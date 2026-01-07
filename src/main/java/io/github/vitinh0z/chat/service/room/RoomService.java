package io.github.vitinh0z.chat.service.room;

import io.github.vitinh0z.chat.entities.membership.Membership;
import io.github.vitinh0z.chat.entities.room.Room;
import io.github.vitinh0z.chat.entities.user.User;
import io.github.vitinh0z.chat.enums.room.RoomRole;
import io.github.vitinh0z.chat.repository.membership.MembershipRepository;
import io.github.vitinh0z.chat.repository.room.RoomRepository;
import io.github.vitinh0z.chat.repository.user.UserRepository;
import io.github.vitinh0z.chat.service.membership.MembershipService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;
    private final MembershipService membershipService;

    public Room createRoom(User user, String roomName) {

        User findUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found")
                );

        if (roomName == null || roomName.trim().isEmpty()) {
            throw new IllegalArgumentException("The Room dont be empty");
        }

        Room room = new Room();

        room.setRoomName(roomName);
        room.setOwner(findUser);

        Room roomSave = roomRepository.save(room);

        Membership membership = new Membership();

        membership.setUser(roomSave.getOwner());
        membership.setRoom(roomSave);
        membership.setRoomRole(RoomRole.OWNER);

        membershipRepository.save(membership);

        return roomSave;
    }

    public void deleteRoom(User user, long roomId) {

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not Found")

                );

        if (!room.getOwner().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You Cannot be Delete this Room");
        }

        roomRepository.delete(room);
    }

    public void allRooms() {
        roomRepository.findAll();
    }

    public Room joinRoom(User user, String inviteCode){
        Room room = roomRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new RuntimeException("Invite not found")
        );

        User findUser = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found")
        );
        membershipService.enterRoom(findUser, room);

        return room;
    }

    public List<Room> getAllRoomsByUser(User user){

        User findUser = userRepository.findById(user.getId()).orElseThrow(() -> new RuntimeException("User not found")
        );    

        return roomRepository.findAllRoomsByUserId(findUser.getId());


    }


}
