package io.github.vitinh0z.chat.alltest;

import io.github.vitinh0z.chat.entities.membership.Membership;
import io.github.vitinh0z.chat.entities.room.Room;
import io.github.vitinh0z.chat.entities.user.User;
import io.github.vitinh0z.chat.enums.room.RoomRole;
import io.github.vitinh0z.chat.enums.user.UserStatus;
import io.github.vitinh0z.chat.repository.membership.MembershipRepository;
import io.github.vitinh0z.chat.repository.room.RoomRepository;
import io.github.vitinh0z.chat.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
@Transactional
class IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private MembershipRepository membershipRepository;

    private User testUser;
    private Room testRoom;

    @BeforeEach
    void setup() {
        testUser = new User();
        testUser.setNickname("Testador");
        testUser.setEmail("teste@gmail.com");
        testUser.setUserStatus(UserStatus.ONLINE);
        testUser.setPicProfile("img.png");
        userRepository.save(testUser);

        testRoom = new Room();
        testRoom.setRoomName("Sala de Teste");
        testRoom.setInviteCode("123456");
        testRoom.setOwner(testUser);
        roomRepository.save(testRoom);

        Membership membership = new Membership();
        membership.setUser(testUser);
        membership.setRoom(testRoom);
        membership.setRoomRole(RoomRole.OWNER);
        membershipRepository.save(membership);
    }

    @Test
    void deveListarMensagensSeEstiverLogado() throws Exception {
        mockMvc.perform(get("/messages/" + testRoom.getId())
                        .with(oauth2Login().attributes(attrs -> {
                            attrs.put("email", "teste@gmail.com");
                            attrs.put("name", "Testador");
                        })))
                .andExpect(status().isOk());
    }

    @Test
    void deveBloquearAcessoSemLogin() throws Exception {
        mockMvc.perform(get("/messages/" + testRoom.getId()))
                .andExpect(status().is3xxRedirection());
    }
}
