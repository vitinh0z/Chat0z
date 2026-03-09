package io.github.vitinh0z.chat.dto.user;

import io.github.vitinh0z.chat.entities.user.User;

public record UserPrivateResponseDTO (Long userId, String nickname, String email, String picProfile){

    public static UserPrivateResponseDTO fromEntity(User user) {
        return new UserPrivateResponseDTO(
                user.getId(),
                user.getNickname(),
                user.getEmail(),
                user.getPicProfile()
        );

}   }


