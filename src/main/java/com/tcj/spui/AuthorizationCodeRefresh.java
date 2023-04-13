package com.tcj.spui;

import static org.fusesource.jansi.Ansi.ansi;

import java.awt.Desktop;
import java.io.File;
import java.io.PrintWriter;
import java.net.URI;
import java.util.Scanner;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;

public class AuthorizationCodeRefresh {

  private static AuthorizationCodeRequest authorizationCodeRequest;

  public static void init() {
    try {
      File myObj = new File("setup.txt");
      Scanner reader = new Scanner(myObj);
      String data = "";
      while (reader.hasNextLine()) {
        data = reader.nextLine();
        if (data.startsWith("code=")) {
          String code = data.substring(5);
          authorizationCodeRequest = MainHELLO.spotifyApi.authorizationCode(code)
              .build();
        }
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static void authorizationCodeRefresh() {
    try {
      AuthorizationCodeRefreshRequest authorizationCodeRefreshRequest = MainHELLO.spotifyApi.authorizationCodeRefresh()
          .build();
      final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRefreshRequest.execute();

      // Set access and refresh token for further "spotifyApi" object usage
      MainHELLO.spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
    } catch (Exception e) {
      System.out.println(ansi().render("@|red Error: " + e.getMessage() + "|@"));
    }
  }

  public static void execute() {
    try {
      final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();

      // Set access and refresh token for further "spotifyApi" object usage
      MainHELLO.spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
      MainHELLO.spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());

      File myObj = new File("refresh.txt");
      PrintWriter output = new PrintWriter(myObj);
      output.print(authorizationCodeCredentials.getRefreshToken());
      output.close();
      System.out.println(ansi().render("@|green Success|@\n"));

      if (Desktop.isDesktopSupported()) {
        Desktop.getDesktop().browse(
            new URI("https://github.com/SamTheCoder777/Custom-Spotify-Manager#list-of-commands"));
      }
    } catch (Exception e) {
      System.out.println(ansi().render("@|red Error: " + e.getMessage() + "|@"));
    }
  }

  public static String getRefreshCode() {
    File myObj = new File("refresh.txt");
    try {
      Scanner reader = new Scanner(myObj);
      return reader.nextLine();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }
}
