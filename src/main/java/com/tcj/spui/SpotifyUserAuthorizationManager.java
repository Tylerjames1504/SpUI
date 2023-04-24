package com.tcj.spui;

import java.io.IOException;
import java.net.URI;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

public class SpotifyUserAuthorizationManager {

  private final SpotifyApi retrievedApi;
  private final String authorizationCodeRequestLink;

  public SpotifyUserAuthorizationManager() {

    String clientId = "4076d474b9884c8f88f3c9cf40f80890"; // public info
    URI redirectUri = URI.create("http://localhost:8080/callback");
    this.retrievedApi = new SpotifyApi.Builder()
        .setClientId(clientId)
        .setClientSecret(
            "b604863adab94bfb947b81500e03c78e") // get from database, currently hardcoded
        .setRedirectUri(redirectUri)
        .build();

    AuthorizationCodeUriRequest authorizationCodeUriRequest = this.retrievedApi.authorizationCodeUri()
        .state("x4xkmn9pu3j6uwt7en")
        .scope(
            "user-read-private, user-read-email, playlist-read-private, playlist-read-collaborative, "
                +
                "playlist-modify-public, playlist-modify-private, user-top-read")
        .show_dialog(true).build();

    this.authorizationCodeRequestLink = authorizationCodeUriRequest.execute().toString();
  }

  public SpotifyApi getRetrievedApi() {
    return this.retrievedApi;
  }

  public String getAuthorizationCodeRequestLink() {
    return this.authorizationCodeRequestLink;
  }

  public void completeAuthorization(String token) {
    try {
      AuthorizationCodeRequest authorizationCodeRequest = this.retrievedApi.authorizationCode(token)
          .build();
      AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();
      this.retrievedApi.setAccessToken(authorizationCodeCredentials.getAccessToken()); // database
      this.retrievedApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken()); // database
    } catch (IOException | SpotifyWebApiException | ParseException e) {
      e.printStackTrace();
    }
  }

  public void refreshAuthCode() {
    AuthorizationCodeRefreshRequest authorizationCodeRefreshRequest = this.retrievedApi.authorizationCodeRefresh()
        .build();
    try {
      AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRefreshRequest.execute();
      this.retrievedApi.setAccessToken(authorizationCodeCredentials.getAccessToken()); // database
    } catch (IOException | SpotifyWebApiException | ParseException e) {
      e.printStackTrace();
    }
  }

}
