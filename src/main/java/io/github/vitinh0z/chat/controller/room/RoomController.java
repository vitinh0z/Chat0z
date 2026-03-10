package io.github.vitinh0z.chat.controller.room;

import java.util.List;

import io.github.vitinh0z.chat.dto.room.RoomCreateDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.vitinh0z.chat.dto.invite.InviteResponseDTO;
import io.github.vitinh0z.chat.dto.room.RoomResponseDTO;
import io.github.vitinh0z.chat.entities.room.Room;
import io.github.vitinh0z.chat.entities.user.User;
import io.github.vitinh0z.chat.service.room.RoomService;
import io.github.vitinh0z.chat.service.user.UserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/room")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;
    private final UserService userService;

    @PostMapping("/enter")
    public ResponseEntity<RoomResponseDTO> enterRoom(@AuthenticationPrincipal OAuth2User principal,
            @RequestBody InviteResponseDTO data) {

        User user = userService.findUserByEmail(principal.getAttribute("email"));
        Room room = roomService.joinRoom(user, data.inviteCode());

        return ResponseEntity.ok(RoomResponseDTO.fromEntity(room));
    }

    @GetMapping("/me")
    public ResponseEntity<List<RoomResponseDTO>> getMyRooms(@AuthenticationPrincipal OAuth2User principal) {

        User user = userService.findUserByEmail(principal.getAttribute("email"));

        return ResponseEntity.ok(roomService.getAllRoomsByUser(user)
                .stream()
                .map(RoomResponseDTO::fromEntity)
                .toList()
        );
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteRoom(@AuthenticationPrincipal OAuth2User principal,
                                           @PathVariable Long roomId) {
        User user = userService.findUserByEmail(principal.getAttribute("email"));
        roomService.deleteRoom(user, roomId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/create")
    public ResponseEntity<RoomResponseDTO> createRoom(
        @AuthenticationPrincipal OAuth2User principal,
        @RequestBody RoomCreateDTO data
        
    ){
        String email = principal.getAttribute("email");
        Room room = roomService.createRoom(data, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(RoomResponseDTO.fromEntity(room));
    }
}
