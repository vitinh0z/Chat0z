package io.github.vitinh0z.chat.service.invite;

import io.github.vitinh0z.chat.entities.invite.Invite;
import io.github.vitinh0z.chat.entities.user.User;
import io.github.vitinh0z.chat.repository.membership.invite.InviteRepository;
import io.github.vitinh0z.chat.service.membership.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InviteService {

    private final InviteRepository inviteRepository;
    private final MembershipService membershipService;


    public void enterInviteLink(String code, User user) {

        Invite invite = inviteRepository.findByUrlInvite(code)
                .orElseThrow(() -> new RuntimeException("Invite Inexistent")
        );

        if (!invite.isValid()){
            throw new IllegalArgumentException("Invite Expired");
        }

        membershipService.enterRoom(user, invite.getRoom());

    }

}
