package io.github.vitinh0z.chat.dto.membership;

import io.github.vitinh0z.chat.enums.room.RoomRole;

public record MembershipRoleRequestDTO(Long roomId, Long targetUserId, RoomRole role) {
}
