package com.tcj.spui;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
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

public class Encryption {

  public static SecretKey key;

  static {
    try {
      key = getKeyFromPassword();
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      throw new RuntimeException(e);
    }
  }

  public static IvParameterSpec ivParameterSpec = Encryption.generateIv();

  public static SecretKey generateKey(int n) throws NoSuchAlgorithmException {
    KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
    keyGenerator.init(n);
    return keyGenerator.generateKey();
  }

  public static SecretKey getKeyFromPassword()
      throws NoSuchAlgorithmException, InvalidKeySpecException {

    SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
    KeySpec spec = new PBEKeySpec(Objects.requireNonNull(getMACAddress()).toCharArray(),
        Objects.requireNonNull(getMACBytes()), 65536, 256);
    return new SecretKeySpec(factory.generateSecret(spec)
        .getEncoded(), "AES");
  }

  public static IvParameterSpec generateIv() {
    byte[] iv = getMACBytes();
    return new IvParameterSpec(iv);
  }

  public static String encrypt(String input)
      throws NoSuchPaddingException, NoSuchAlgorithmException,
      InvalidAlgorithmParameterException, InvalidKeyException,
      BadPaddingException, IllegalBlockSizeException {
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);
    byte[] cipherText = cipher.doFinal(input.getBytes());
    return Base64.getEncoder()
        .encodeToString(cipherText);
  }

  // TODO change alg
  public static String decrypt(String cipherText)
      throws NoSuchPaddingException, NoSuchAlgorithmException,
      InvalidAlgorithmParameterException, InvalidKeyException,
      BadPaddingException, IllegalBlockSizeException {
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
    byte[] plainText = cipher.doFinal(Base64.getDecoder()
        .decode(cipherText));
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
      while (networkInterfaces.hasMoreElements()) {
        NetworkInterface network = networkInterfaces.nextElement();
        byte[] mac = network.getHardwareAddress();
        if (mac != null) {
          StringBuilder sb = new StringBuilder();
          for (int i = 0; i < mac.length; i++) {
            sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
          }
          return sb.toString();

        }
      }
    } catch (SocketException e) {

      e.printStackTrace();

    }
    return null;
  }

  public static byte[] getMACBytes() {
    byte[] macBytes = Objects.requireNonNull(getMACAddress()).getBytes();
    byte[] out = new byte[16];
    if (macBytes.length - 1 >= 0)
      System.arraycopy(macBytes, 0, out, 0, macBytes.length - 1);
    return out;
  }

  public static void main(String[] args)
      throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, IOException {

    System.out.println(getIPAddress().length() - 1);

    String input = "6QZtJ6QQaa0a6p2BF+jNRhujUdg7ptCYjoLU86UpWSntNJFmQwKN87kWUwuLcuSZK7EPULNYx/udW9UuNeMnFBk+SSTXxm4Ug6DsuCAuRSPjftt+u2Gno62+EtVXCtLdzbgXGqf/98IHE1ZMYvFS4j4+ZW6Qd2YgUtJ833z8cOvWrVpVgWKPuhzqTDKs3dEHM4CAJOajVEbMDfqpkmKpNBJpdcZ2zBRlpNLdGztr1DaQtr2nqSoEnGXLJIMG141aq6FPfi/7FBFzybE03h08qgfm3ns7M3Ywk0zLU5PV31oUaYzfEGN0307iNcAgFPaByC7UZ2ju44s/B6VjbnhpwisirOoGmFK9LTZMVcK0GnY=";
//    String cipherText = Encryption.encrypt(input);
    String plainText = Encryption.decrypt(input);
//    System.out.println(cipherText);
    System.out.println(plainText);
//    System.out.println(input.equals(plainText));



  }

}
