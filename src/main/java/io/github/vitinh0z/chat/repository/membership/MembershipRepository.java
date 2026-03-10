package io.github.vitinh0z.chat.repository.membership;

import io.github.vitinh0z.chat.entities.membership.Membership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MembershipRepository extends JpaRepository<Membership, Long> {

    Optional<Membership> findByUserIdAndRoomId(Long userId, Long roomId);

    List<Membership> findByRoomId(Long roomId);
}
