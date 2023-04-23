package com.tcj.spui;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Enumeration;
import java.util.Objects;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import com.github.fzakaria.ascii85.Ascii85;



public class Encryption {

  public static IvParameterSpec iv = generateIv();

  public static SecretKey getKeyFromPassword(String password, String salt)
      throws NoSuchAlgorithmException, InvalidKeySpecException {

    SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
    KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
    return new SecretKeySpec(factory.generateSecret(spec)
        .getEncoded(), "AES");
  }

  public static IvParameterSpec generateIv() {
    byte[] iv = getMACBytes();
    return new IvParameterSpec(iv);
  }

  public static String encrypt(String input) throws NoSuchPaddingException, NoSuchAlgorithmException,
      InvalidAlgorithmParameterException, InvalidKeyException,
      BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException {
    if (input == null) {
      return null;
    }
    SecretKey key = getKeyFromPassword(Objects.requireNonNull(getMACAddress()),
        Objects.requireNonNull(getIPAddress()));
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    cipher.init(Cipher.ENCRYPT_MODE, key, iv);
    byte[] cipherText = cipher.doFinal(input.getBytes());
    String out = Ascii85.encode(cipherText);
    return out;
  }

  public static String decrypt(String cipherText) throws NoSuchPaddingException, NoSuchAlgorithmException,
      InvalidAlgorithmParameterException, InvalidKeyException,
      BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException {
    if (cipherText == null) {
      return null;
    }
    SecretKey key = getKeyFromPassword(Objects.requireNonNull(getMACAddress()),
        Objects.requireNonNull(getIPAddress()));
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    cipher.init(Cipher.DECRYPT_MODE, key, iv);
    byte[] plainText = cipher.doFinal(Ascii85.decode(cipherText));
    return new String(plainText);
  }

  public static String getIPAddress() {
    try {
      InetAddress ip = InetAddress.getLocalHost();
      return ip.getHostAddress();
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static String getMACAddress() {

    try {

      Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
      while(networkInterfaces.hasMoreElements())
      {
        NetworkInterface network = networkInterfaces.nextElement();
        byte[] mac = network.getHardwareAddress();
        if(mac != null)
        {
          StringBuilder sb = new StringBuilder();
          for (int i = 0; i < mac.length; i++) {
            sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
          }
          return sb.toString();

        }
      }
    } catch (SocketException e){

      e.printStackTrace();

    }
    return null;
  }

  public static byte[] getMACBytes() {
    byte[] macBytes = getMACAddress().getBytes();
    byte[] out = new byte[16];
    if (macBytes.length - 1 >= 0)
      System.arraycopy(macBytes, 0, out, 0, macBytes.length - 1);
    return out;
  }

  public static void main(String[] args)
      throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, IOException {



    String input = "baeldung";
    SecretKey key = getKeyFromPassword(Objects.requireNonNull(getMACAddress()),
        Objects.requireNonNull(getIPAddress()));
    String cipherText = Encryption.encrypt(input);
    String plainText = Encryption.decrypt(cipherText);
    System.out.println(cipherText);
    System.out.println(plainText);
    System.out.println(input.equals(plainText));

  }

}
