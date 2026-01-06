package io.github.vitinh0z.chat.dto.member;

import io.github.vitinh0z.chat.dto.user.UserPublicResponseDTO;
import io.github.vitinh0z.chat.entities.membership.Membership;
import io.github.vitinh0z.chat.enums.room.RoomRole;

public record MemberResponseDTO(Long memberId, RoomRole role, UserPublicResponseDTO user) {

    public static MemberResponseDTO fromEntity (Membership membership){
        return new MemberResponseDTO(
                membership.getId(),
                membership.getRoomRole(),
                UserPublicResponseDTO.fromEntity(membership.getUser())
        );

    }
}
