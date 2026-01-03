package io.github.vitinh0z.chat.service.user;


import io.github.vitinh0z.chat.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // cadastrarUsuario()
    // buscarUsuarioPorId()
    // deletarConta()
    // atualizarPerfil()
}
