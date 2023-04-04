package com.tcj.spui;

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

    public SpotifyApi callApi() {
        return this.spotifyApi;
    }
    public ClientManager() {
        this.spotifyApi = new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret("b604863adab94bfb947b81500e03c78e") // get from database, currently hardcoded
                .setRedirectUri(redirectUri)
                .build();
    }
    public String authorizeUser() {
        AuthorizationCodeUriRequest authorizationCodeUriRequest = this.spotifyApi.authorizationCodeUri()
                .state("x4xkmn9pu3j6uwt7en")
                .scope("user-read-private, user-read-email, playlist-read-private, playlist-read-collaborative, " +
                        "playlist-modify-public, playlist-modify-private, user-top-read")
                .show_dialog(true).build();
        return authorizationCodeUriRequest.execute().toString();
    }

    public void initiateApp() {
        try {
            if (Desktop.isDesktopSupported()) Desktop.getDesktop().browse(uri);
            String cmd = "";
            ServerSocket ss=new ServerSocket(8080, 1, InetAddress.getByName("127.0.0.1"));
            Socket s=ss.accept();//establishes connection
            var rawIn = s.getInputStream();
            var in = new BufferedReader(new InputStreamReader(rawIn, StandardCharsets.US_ASCII)); {
                while (true){
                    cmd = in.readLine().trim();
                    if (cmd == null) break; //client is hung up
                    if (cmd.isEmpty()) continue; //empty line was sent
                    if(cmd.indexOf("code") != -1){
                        cmd = cmd.substring(cmd.indexOf("code")+5,cmd.indexOf("state") - 1);
                        cmd = cmd.replaceAll("\\s.*", "");
                        break;
                    }
                }
            }
            ss.close(); s.close(); rawIn.close(); in.close();
            AuthorizationCodeRequest authorizationCodeRequest = this.spotifyApi.authorizationCode(cmd)
                    .build();
            AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();
            this.spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken()); // database
            this.spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken()); // database
        }
        catch (IOException | SpotifyWebApiException | ParseException e) { System.out.println(e);}
    }

    public void refreshAuthCode() {
         AuthorizationCodeRefreshRequest authorizationCodeRefreshRequest = this.spotifyApi.authorizationCodeRefresh()
                .build();
         try {
             AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRefreshRequest.execute();
             this.spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken()); // database
         }
         catch (Exception e) {System.out.println(e);}
    }
}
