package io.github.vitinh0z.chat.controller.room;


import io.github.vitinh0z.chat.dto.invite.InviteResponseDTO;
import io.github.vitinh0z.chat.dto.room.RoomResponseDTO;
import io.github.vitinh0z.chat.entities.room.Room;
import io.github.vitinh0z.chat.entities.user.User;
import io.github.vitinh0z.chat.service.room.RoomService;
import io.github.vitinh0z.chat.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/room")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;
    private final UserService userService;


    public ResponseEntity<RoomResponseDTO> enterRoom(@AuthenticationPrincipal OAuth2User principal,
                                                     @RequestBody InviteResponseDTO data){

        User user = userService.findUserByEmail(principal.getAttribute("email"));
        Room room = roomService.joinRoom(user, data.inviteCode());

        return ResponseEntity.ok(RoomResponseDTO.fromEntity(room));
    }
}
