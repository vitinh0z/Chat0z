package io.github.vitinh0z.chat.dto.user;

import io.github.vitinh0z.chat.entities.user.User;
import io.github.vitinh0z.chat.enums.user.UserStatus;

public record UserPublicResponseDTO (
                                 String nickname,
                                 String picProfile,
                                 UserStatus status)
{
    public static UserPublicResponseDTO fromEntity(User user) {

        return new UserPublicResponseDTO(
                user.getNickname(),
                user.getPicProfile(),
                user.getUserStatus()
        );

    }

}

