package com.tcj.spui;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class User {

  private int id;
  private String email;
  private String authCode;
  private String refreshToken;

  public User(String email, SpUIDatabase db)
      throws NoSuchElementException, URISyntaxException, IOException, InterruptedException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {

    Map<String, Object> userMap = db.getUser(email);
    if (userMap == null) {
      throw new NoSuchElementException("User not found");
    }
    this.id = Integer.parseInt(String.valueOf(userMap.get("user_id")));
    this.email = email;
    this.authCode = String.valueOf(userMap.get("auth_code"));
    this.refreshToken = String.valueOf(userMap.get("refresh_token"));

  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getAuthCode() {
    return authCode;
  }

  public void setAuthCode(String authCode) {
    this.authCode = authCode;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public String toString() {
    return "User: " + this.id + " " + this.email + " " + this.authCode + " " + this.refreshToken;
  }

}
