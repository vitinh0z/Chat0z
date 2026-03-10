package io.github.vitinh0z.chat.controller.membership;

import io.github.vitinh0z.chat.dto.member.MemberResponseDTO;
import io.github.vitinh0z.chat.dto.membership.MembershipBanRequestDTO;
import io.github.vitinh0z.chat.dto.membership.MembershipRoleRequestDTO;
import io.github.vitinh0z.chat.entities.room.Room;
import io.github.vitinh0z.chat.entities.user.User;
import io.github.vitinh0z.chat.service.membership.MembershipService;
import io.github.vitinh0z.chat.service.room.RoomService;
import io.github.vitinh0z.chat.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/membership")
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;
    private final UserService userService;
    private final RoomService roomService;

    @GetMapping("/{roomId}")
    public ResponseEntity<List<MemberResponseDTO>> getMembersByRoom(@PathVariable Long roomId,
                                                                    @AuthenticationPrincipal OAuth2User principal) {
        List<MemberResponseDTO> members = membershipService.getMembersByRoom(roomId)
                .stream()
                .map(MemberResponseDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(members);
    }

    @DeleteMapping("/exit/{roomId}")
    public ResponseEntity<Void> exitRoom(@PathVariable Long roomId,
                                         @AuthenticationPrincipal OAuth2User principal) {
        User user = userService.findUserByEmail(principal.getAttribute("email"));
        membershipService.exitRoom(user.getId(), roomId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/role")
    public ResponseEntity<Void> changeRole(@AuthenticationPrincipal OAuth2User principal,
                                           @RequestBody MembershipRoleRequestDTO data) {
        User owner = userService.findUserByEmail(principal.getAttribute("email"));
        User member = userService.findUserById(data.targetUserId());
        Room room = roomService.findById(data.roomId());
        membershipService.alternRole(owner, member, room, data.role());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/ban")
    public ResponseEntity<Void> banUser(@AuthenticationPrincipal OAuth2User principal,
                                        @RequestBody MembershipBanRequestDTO data) {
        User requester = userService.findUserByEmail(principal.getAttribute("email"));
        User target = userService.findUserById(data.targetUserId());
        Room room = roomService.findById(data.roomId());
        membershipService.banUser(requester, target, room);
        return ResponseEntity.noContent().build();
    }
}
