package io.github.vitinh0z.chat.utils.crypto;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoUtils {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final int IV_SIZE = 16;
    
    private static SecretKey getKeyFromInvite(String inviteCode){

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(inviteCode.getBytes(StandardCharsets.UTF_8));
            return new SecretKeySpec(hash, "AES");
        } catch (Exception e) {
            throw new RuntimeException("Error generating key from invite code", e);
        }
    }


    public static String encrypt (String content, String inviteCode){

        try {
            SecretKey key = getKeyFromInvite(inviteCode);
            byte[] iv = new byte[IV_SIZE];
            new SecureRandom().nextBytes(iv);
            IvParameterSpec ivParams = new IvParameterSpec(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key, ivParams);

            byte[] encrypted = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));

            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + encrypted.length);
            byteBuffer.put(iv);
            byteBuffer.put(encrypted);

            return Base64.getEncoder().encodeToString(byteBuffer.array());

        } catch (Exception e) {
            throw new RuntimeException("Error encrypting content", e);
        }
    }

    public static String decrypt (String fullContent, String inviteCode){

        try {
            SecretKey key = getKeyFromInvite(inviteCode);
            byte[] fullBytes = Base64.getDecoder().decode(fullContent);

            ByteBuffer byteBuffer = ByteBuffer.wrap(fullBytes);
            byte[] iv = new byte[IV_SIZE];
            byteBuffer.get(iv);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            byte[] encryptedMessage = new byte[byteBuffer.remaining()];
            byteBuffer.get(encryptedMessage);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
            
            return new String(cipher.doFinal(encryptedMessage), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "[Mensagem ilegível]";
        } 

    }


}
