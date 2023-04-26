package com.tcj.spuiTests;

import com.tcj.spui.Encryption;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class EncryptionTests {

    @Test
    public void getMACAddressTest() {
        Assertions.assertTrue(Objects.requireNonNull(Encryption.getMACAddress()).matches("([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})"));
    }

    @Test
    public void getMACBytesSizeTest() {
        Assertions.assertEquals(16, Encryption.getMACBytes().length);
    }

    @Test
    public void getIPAddressTest() {
        Assertions.assertTrue(Objects.requireNonNull(Encryption.getIPAddress()).matches("([0-9]{1,3}[.]){3}[0-9]{1,3}"));
    }

    @Test
    public void encryptTextTest() throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String text = "Hello World!";
        String encryptedText = Encryption.encrypt(text);
        Assertions.assertNotEquals(text, encryptedText);
    }

    @Test
    public void decryptTextTest() throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String text = "Hello World!";
        String encryptedText = Encryption.encrypt(text);
        String decryptedText = Encryption.decrypt(encryptedText);
        Assertions.assertEquals(text, decryptedText);
    }

    @Test
    public void generateKeyTest() throws NoSuchAlgorithmException {
        Assertions.assertEquals(SecretKeySpec.class, Encryption.generateKey(256).getClass());
    }

    @Test
    public void generateIvTest() {
        Assertions.assertEquals(IvParameterSpec.class, Encryption.generateIv().getClass());
    }

}
