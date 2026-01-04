package io.github.vitinh0z.chat.repository.user;

import io.github.vitinh0z.chat.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {



    Optional<User> findById(Long userId);

    Optional<User> findByEmail(String email);

    User getUserById(Long id);

    boolean findByNickname(String nickname);

    boolean existsByNickname(String nickname);
}
