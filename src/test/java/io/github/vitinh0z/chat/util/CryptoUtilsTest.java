package io.github.vitinh0z.chat.util;

import io.github.vitinh0z.chat.utils.crypto.CryptoUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CryptoUtilsTest {

    @Test
    void deveEncriptarEDescriptarCorretamente() {
        String mensagemOriginal = "Fuck you donald trump";
        String senhaDaSala = "batata-frita";

        String encriptado = CryptoUtils.encrypt(mensagemOriginal, senhaDaSala);

        Assertions.assertNotEquals(mensagemOriginal, encriptado);

        String descriptado = CryptoUtils.decrypt(encriptado, senhaDaSala);

        Assertions.assertEquals(mensagemOriginal, descriptado);
    }

    @Test
    void naoDeveDescriptarComSenhaErrada() {
        String mensagemOriginal = "Ola Mundo";
        String senhaCerta = "senha1";
        String senhaErrada = "senha2";

        String encriptado = CryptoUtils.encrypt(mensagemOriginal, senhaCerta);
        String resultado = CryptoUtils.decrypt(encriptado, senhaErrada);

        Assertions.assertNotEquals(mensagemOriginal, resultado);
    }
}
