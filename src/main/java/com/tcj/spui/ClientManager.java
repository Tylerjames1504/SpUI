package com.tcj.spui;

import java.net.URISyntaxException;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.nio.charset.StandardCharsets;


public class ClientManager {
    private final String clientId = "4076d474b9884c8f88f3c9cf40f80890";
    private final URI redirectUri = URI.create("http://localhost:8080/callback");
    private SpotifyApi spotifyApi;
    private AuthorizationCodeUriRequest authorizationCodeUriRequest;

    public SpUIDatabase db = new SpUIDatabase();

    public SpotifyApi grabApi() {
        return this.spotifyApi;
    }
    public String getAuthRequestLink() { return authorizationCodeUriRequest.execute().toString(); }
    public ClientManager() throws IOException, InterruptedException, URISyntaxException {
        this.spotifyApi = new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(db.getClientSecret()) //TODO get from database, seems like its implemeted here
                .setRedirectUri(redirectUri)
                .build();
        this.authorizationCodeUriRequest = this.spotifyApi.authorizationCodeUri()
                .state("x4xkmn9pu3j6uwt7en")
                .scope("user-read-private, user-read-email, playlist-read-private, playlist-read-collaborative, " +
                        "playlist-modify-public, playlist-modify-private, user-top-read")
                .show_dialog(true).build();
        System.out.println(this.authorizationCodeUriRequest.getBody());
    }
    public void initiateApp(String token) {
        try {
            AuthorizationCodeRequest authorizationCodeRequest = this.spotifyApi.authorizationCode(token)
                    .build();
            AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();
            this.spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken()); //TODO put user access token into database
            this.spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken()); //TODO put user refresh token into database
        }
        catch (IOException | SpotifyWebApiException | ParseException e) { System.out.println(e);}
    }

    public void refreshAuthCode() {
         AuthorizationCodeRefreshRequest authorizationCodeRefreshRequest = this.spotifyApi.authorizationCodeRefresh()
                .build();
         try {
             AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRefreshRequest.execute();
             this.spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken()); //TODO, replace current access token in database NOTE: Refresh token does not change
         }
         catch (Exception e) { System.out.println(e); }
    }
}
