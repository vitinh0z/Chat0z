package io.github.vitinh0z.chat.repository.user;

import io.github.vitinh0z.chat.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public abstract class UserRepository implements JpaRepository<User, Long> {

}
