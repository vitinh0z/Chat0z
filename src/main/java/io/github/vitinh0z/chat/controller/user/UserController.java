package io.github.vitinh0z.chat.controller.user;


import io.github.vitinh0z.chat.dto.user.UserPrivateResponseDTO;
import io.github.vitinh0z.chat.dto.user.UserPublicResponseDTO;
import io.github.vitinh0z.chat.dto.user.UserStatusRequestDTO;
import io.github.vitinh0z.chat.dto.user.UserUpdateRequestDTO;
import io.github.vitinh0z.chat.entities.user.User;
import io.github.vitinh0z.chat.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RequestMapping("/user")
@RestController
public class UserController {

    private final UserService userService;

    @PatchMapping("/status")
    public ResponseEntity<UserPrivateResponseDTO> updateStatus(@AuthenticationPrincipal OAuth2User principal,
                                                               @RequestBody UserStatusRequestDTO data) {
        User user = userService.findUserByEmail(principal.getAttribute("email"));
        User updated = userService.updateStatus(user.getId(), data.status());
        return ResponseEntity.ok(UserPrivateResponseDTO.fromEntity(updated));
    }

    @PatchMapping("/me")
    public UserPrivateResponseDTO updateProfile (@AuthenticationPrincipal OAuth2User principal, @RequestBody UserUpdateRequestDTO userUpdate){

        User user = userService.findUserByEmail(principal.getAttribute("email"));

        User update= userService.updateProfile(user.getId(), userUpdate);

        return UserPrivateResponseDTO.fromEntity(update);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteUser (@AuthenticationPrincipal OAuth2User principal){

        User user = userService.findUserByEmail(principal.getAttribute("email"));

        userService.deleteUser(user.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserPrivateResponseDTO> getMyUser (@AuthenticationPrincipal OAuth2User principal){

        User user = userService.findUserByEmail(principal.getAttribute("email"));

        return ResponseEntity.ok(UserPrivateResponseDTO.fromEntity(user));
    }

    @GetMapping("/{userId}")
    public UserPublicResponseDTO getUser (@PathVariable Long userId){

        User user = userService.findUserById(userId);

        return UserPublicResponseDTO.fromEntity(user);
    }



}
