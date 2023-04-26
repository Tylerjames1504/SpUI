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
import java.util.Objects;

import static com.tcj.spui.SpUIDatabase.stripResponse;


public class ClientManager {

    private static final String CLIENT_ID = "b6425f62083d455a89f1b1af33a23ca8";
    private static final URI REDIRECT_URI = URI.create("http://localhost:8080/callback");
    private final SpotifyApi spotifyApi;
    private final AuthorizationCodeUriRequest authorizationCodeUriRequest;
    public SpUIDatabase db = new SpUIDatabase();
    public User user;

    public ClientManager()
            throws IOException, InterruptedException, URISyntaxException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        this.spotifyApi = new SpotifyApi.Builder()
                .setClientId(CLIENT_ID)
                .setClientSecret(db.getClientSecret())
                .setRedirectUri(REDIRECT_URI)
                .build();
        this.authorizationCodeUriRequest = this.spotifyApi.authorizationCodeUri()
                .state("x4xkmn9pu3j6uwt7en")
                .scope(
                        "user-read-private, user-read-email, playlist-read-private, playlist-read-collaborative, "
                                +
                                "playlist-modify-public, playlist-modify-private, user-top-read")
                .show_dialog(true).build();
    }

    public SpotifyApi grabApi() {
        return this.spotifyApi;
    }

    public String getAuthRequestLink() {
        return authorizationCodeUriRequest.execute().toString();
    }

    public void initiateApp(String token) {
        try {
            AuthorizationCodeRequest authorizationCodeRequest = this.spotifyApi.authorizationCode(token)
                    .build();
            AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();
            String accessToken = authorizationCodeCredentials.getAccessToken();
            String refreshToken = authorizationCodeCredentials.getRefreshToken();
            this.spotifyApi.setAccessToken(accessToken);
            this.spotifyApi.setRefreshToken(refreshToken);
            String email = Objects.requireNonNull(
                            stripResponse(spotifyApi.getCurrentUsersProfile().build().getJson())).get("email")
                    .toString();
            db.initUser(email, accessToken, refreshToken);
            user = new User(email, db);

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            e.printStackTrace();
        } catch (
                URISyntaxException | InterruptedException | InvalidAlgorithmParameterException |
                NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException |
                BadPaddingException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public void refreshAuthCode() {
        AuthorizationCodeRefreshRequest authorizationCodeRefreshRequest = this.spotifyApi.authorizationCodeRefresh()
                .build();
        try {
            AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRefreshRequest.execute();
            user.setAuthCode(authorizationCodeCredentials.getAccessToken());
//      this.spotifyApi.setAccessToken(
//          authorizationCodeCredentials.getAccessToken()); //TODO, replace current access token in database NOTE: Refresh token does not change
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
