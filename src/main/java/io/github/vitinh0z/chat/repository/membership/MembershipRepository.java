package io.github.vitinh0z.chat.repository.membership;

import io.github.vitinh0z.chat.entities.membership.Membership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MembershipRepository extends JpaRepository<Membership, Long> {

    Optional<Membership> findByUserAndRoomId(Long userId, Long roomId);
}
