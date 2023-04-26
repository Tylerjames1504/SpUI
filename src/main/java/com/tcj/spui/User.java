package com.tcj.spui;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.NoSuchElementException;

public class User {

    private final int id;
    private final String email;
    private final String refreshToken;
    private String authCode;

    /*
     * Constructor for User class
     * Takes in a users email that should already be in the database and creates a User object
     * so there will only need to be one database call
     */
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

    public String getEmail() {
        return email;
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

    public String toString() {
        return "User: " + this.id + " " + this.email + " " + this.authCode + " " + this.refreshToken;
    }

}
