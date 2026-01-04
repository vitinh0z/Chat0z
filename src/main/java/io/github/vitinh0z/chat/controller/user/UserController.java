package io.github.vitinh0z.chat.controller.user;


import io.github.vitinh0z.chat.dto.user.UserPrivateResponseDTO;
import io.github.vitinh0z.chat.dto.user.UserPublicResponseDTO;
import io.github.vitinh0z.chat.dto.user.UserUpdateRequestDTO;
import io.github.vitinh0z.chat.entities.user.User;
import io.github.vitinh0z.chat.repository.user.UserRepository;
import io.github.vitinh0z.chat.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    @PatchMapping("/me")
    public UserPrivateResponseDTO updateProfile (@AuthenticationPrincipal OAuth2User principal, @RequestBody UserUpdateRequestDTO userUpdate){

        User user = userService.findUserByEmail(principal.getAttribute("email"));

        User update= userService.updateProfile(user.getId(), userUpdate);

        return UserPrivateResponseDTO.fromEntity(update);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteUser (@AuthenticationPrincipal OAuth2User principal){

        User user = userService.findUserByEmail(principal.getAttribute("email"))

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
