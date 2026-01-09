package io.github.vitinh0z.chat.Service;

import io.github.vitinh0z.chat.dto.room.RoomCreateDTO;
import io.github.vitinh0z.chat.entities.room.Room;
import io.github.vitinh0z.chat.entities.user.User;
import io.github.vitinh0z.chat.repository.membership.MembershipRepository;
import io.github.vitinh0z.chat.repository.room.RoomRepository;
import io.github.vitinh0z.chat.repository.user.UserRepository;
import io.github.vitinh0z.chat.service.room.RoomService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @InjectMocks
    private RoomService roomService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoomRepository roomRepository;
    @Mock
    private MembershipRepository membershipRepository;

    @Test
    void deveCriarSalaEAdicionarDono() {

        String email = "teste@gmail.com";
        User userMock = new User();
        userMock.setEmail(email);

        RoomCreateDTO dto = new RoomCreateDTO(null,"Sala VIP", "senha123");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userMock));
        when(roomRepository.findByInviteCode(dto.invite())).thenReturn(Optional.empty());
        when(roomRepository.save(any(Room.class))).thenAnswer(i -> i.getArguments()[0]);
        Room salaCriada = roomService.createRoom(dto, email);

        Assertions.assertNotNull(salaCriada);
        Assertions.assertEquals("Sala VIP", salaCriada.getRoomName());

        verify(membershipRepository, Mockito.times(1)).save(any());
    }
}