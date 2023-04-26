package com.tcj.spui;

import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/*
 * Class to handle all of Spotify Web API auth flow
 * See: https://developer.spotify.com/documentation/web-api/tutorials/code-flow
 *
 * Uses thelinmichael's SpotifyAPI wrapper to abstract sending HTTP requests
 */
public class SpotifyUserAuthorizationManager {

    private final SpotifyApi retrievedApi;
    private final String authorizationCodeRequestLink;

    /*
     * Use the client ID, client secret (retrieved from database) and redirect URI to build a SpotifyAPI object
     * Create an authorization request link by building and executing an authorizationCodeUriRequest that specifies scope and state
     * Stores the SpotifyAPI object 'retrievedAPI' that is used to build and execute Web API calls
     */
    public SpotifyUserAuthorizationManager() {

        String clientId = "b6425f62083d455a89f1b1af33a23ca8"; // public info
        URI redirectUri = URI.create("http://localhost:8080/callback");
        try {
            this.retrievedApi = new SpotifyApi.Builder()
                    .setClientId(clientId)
                    .setClientSecret(App.db.getClientSecret()).setRedirectUri(redirectUri).build();
        } catch (URISyntaxException | IOException | InterruptedException | InvalidAlgorithmParameterException |
                 NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException |
                 InvalidKeyException e) {
            throw new RuntimeException(e);
        }

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

    /*
     * Using an auth token that was retrieved via the WebEngine in LoginController
     * Build and send a request that exchanges this auth token for an access token and a refresh token
     * Add these tokens to the API object
     */
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

    /*
     * The first access token that is received in exchange for the auth token expires after 3600 seconds
     * Using the refresh token stored in 'retrievedAPI', refresh the access token
     * Add the new access token to the API object
     * NOTE: The refresh token can be recycled many times, you can use the SAME refresh token to get many new access tokens
     *       The refresh token expires after a couple months (There is no given expiry time)
     */
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
