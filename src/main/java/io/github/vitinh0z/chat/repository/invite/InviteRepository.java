package io.github.vitinh0z.chat.repository.invite;

import io.github.vitinh0z.chat.entities.invite.Invite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InviteRepository extends JpaRepository<Invite, Long> {


    Optional<Invite> findByUrlInvite(String code);
}
