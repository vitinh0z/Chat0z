package io.github.vitinh0z.chat.dto.room;

import io.github.vitinh0z.chat.entities.room.Room;

public record RoomResponseDTO(
        Long roomId,
        String name){

    public static RoomResponseDTO fromEntity(Room room){
        return new RoomResponseDTO(
                room.getId(),
                room.getRoomName()
        );
    }
}
