package io.github.vitinh0z.chat.service.user;


import io.github.vitinh0z.chat.entities.user.User;
import io.github.vitinh0z.chat.enums.user.UserStatus;
import io.github.vitinh0z.chat.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User processOauth2Login(String email, String fotoPerfil){

        Optional<User> findUser = userRepository.findByEmail(email);

        if (findUser.isPresent()) {
            User user = findUser.get();
            user.setPicProfile(fotoPerfil);

            return userRepository.save(user);
        }

        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPicProfile(fotoPerfil);
        String firstNick = email.split("@")[0];
        newUser.setNickname(firstNick);

        return userRepository.save(newUser);
    }

    public User updateNickName(Long userId, String newNickname){

        User findUser = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        if (newNickname == null || newNickname.isBlank()){
            throw new IllegalArgumentException("NickName cannot be empty");
        }

        if(userRepository.existsByNickname(newNickname)){
            throw new IllegalArgumentException("Nickname already taken");
        }

        findUser.setNickname(newNickname);

        return userRepository.save(findUser);
    }

    public User updateStatus(long userId, UserStatus status){

        User findUser = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        if(findUser.getUserStatus() == null) findUser.setUserStatus(status);

        return userRepository.save(findUser);
    }

    public User getUser (long userId){

        return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void deleteUser (long userId){
        User findUser = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        userRepository.delete(findUser);
    }

}
