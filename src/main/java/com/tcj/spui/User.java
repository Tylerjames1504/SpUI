package com.tcj.spui;

public class User {

  private String name;
  private String email;
  private String authCode;
  private String refreshToken;

  public User(String name) {

    this.name = name;
    this.email = null; //TODO get email from db using name
    this.authCode = null; //TODO get authCode from db using name
    this.refreshToken = null; // TODO get refreshToken from db using name

  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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


}
