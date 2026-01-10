package io.github.vitinh0z.chat.controller.message;


import io.github.vitinh0z.chat.dto.message.MessageResponseDTO;
import io.github.vitinh0z.chat.entities.user.User;
import io.github.vitinh0z.chat.service.message.MessageService;
import io.github.vitinh0z.chat.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;
    private final UserService userService;

    @GetMapping("/{roomId}")
    public ResponseEntity<List<MessageResponseDTO>> getAllMessages (@PathVariable Long roomId,
                                                                    @AuthenticationPrincipal OAuth2User principal){

        User user = userService.findUserByEmail(principal.getAttribute("email"));
        List<MessageResponseDTO> history = messageService.getAllMessages(user.getId(), roomId);
        return ResponseEntity.ok(history);
    }
}
