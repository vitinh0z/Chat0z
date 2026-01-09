package io.github.vitinh0z.chat.service.user;


import io.github.vitinh0z.chat.dto.user.UserPrivateResponseDTO;
import io.github.vitinh0z.chat.dto.user.UserUpdateRequestDTO;
import io.github.vitinh0z.chat.entities.user.User;
import io.github.vitinh0z.chat.enums.user.UserStatus;
import io.github.vitinh0z.chat.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User processOauth2Login(String email, String fotoPerfil){

        User findUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not found"));

        if (findUser.getPicProfile() == null) {

            findUser.setPicProfile(fotoPerfil);

            return userRepository.save(findUser);
        }

        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPicProfile(fotoPerfil);
        String firstNick = email.split("@")[0];
        newUser.setNickname(firstNick);

        return userRepository.save(newUser);
    }

    public User updateStatus(long userId, UserStatus status){

        User findUser = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        if(findUser.getUserStatus() != null) findUser.setUserStatus(status);

        return userRepository.save(findUser);
    }

    public User getUser (long userId){

        return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User updateProfile(long userId, UserUpdateRequestDTO data){

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found")
        );

        if(user.getNickname() != null || !data.nickname().isEmpty()){

            if(userRepository.existsByNickname(data.nickname())){
                throw new IllegalArgumentException("Nickname already taken");
            }
        }

        user.setNickname(data.nickname());

        if(data.picProfile() != null || !user.getPicProfile().isBlank()){
            user.setPicProfile(data.picProfile());
        }

        return userRepository.save(user);

    }

    public User findUserByEmail(String email){

        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

    }

    public User findUserById(Long userId){
        return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("user not found"));
    }

    public void deleteUser (long userId){
        User findUser = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        userRepository.delete(findUser);
    }

}
