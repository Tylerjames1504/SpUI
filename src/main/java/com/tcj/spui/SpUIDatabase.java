package com.tcj.spui;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Objects;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.json.JSONArray;
import org.json.JSONObject;


public class SpUIDatabase {

  public static final String INITIAL_ENDPOINT = "https://spui.tylerturner.tech";
  public static final String CF_ACCESS_CLIENT_ID = System.getProperty("CF_ACCESS_CLIENT_ID");
  public static final String CF_ACCESS_CLIENT_SECRET = System.getProperty(
      "CF_ACCESS_CLIENT_SECRET");
  public HttpClient client;
  private HttpResponse<String> response;


  public SpUIDatabase() throws IOException, InterruptedException {

    this.client = HttpClient.newHttpClient();
    this.response = initConnect();

  }

  public static Map<String, Object> stripResponse(HttpResponse<String> response)
      throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {

    JSONArray jsonArray;
    Map<String, Object> json;
    if (response.body().charAt(0) == '[') {
      jsonArray = new JSONArray(response.body());
      if (jsonArray.length() < 1) {
        return null;
      } else {
        json = jsonArray.getJSONObject(0).toMap();
      }
    } else {
      json = new JSONObject(response.body()).toMap();
    }

    if (json.get("auth_code") != null) {
      json.replace("auth_code", Encryption.decrypt((String) json.get("auth_code")));
    }
    if (json.get("refresh_token") != null) {
      json.replace("refresh_token", Encryption.decrypt((String) json.get("refresh_token")));
    }
    return json;

  }

  public static Map<String, Object> stripResponse(String response)
      throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {

    JSONArray jsonArray = null;
    Map<String, Object> json;
    if (response.charAt(0) == '[') {
      jsonArray = new JSONArray(response);
      if (jsonArray.length() < 1) {
        return null;
      } else {
        json = jsonArray.getJSONObject(0).toMap();
      }
    } else {
      json = new JSONObject(response).toMap();
    }

    if (json.get("auth_code") != null) {
      json.replace("auth_code", Encryption.decrypt((String) json.get("auth_code")));
    }
    if (json.get("refresh_token") != null) {
      json.replace("refresh_token", Encryption.decrypt((String) json.get("refresh_token")));
    }
    return json;

  }

  public HttpResponse<String> initConnect() throws IOException, InterruptedException {

    HttpResponse<String> response;
    long startTime = System.currentTimeMillis();
    long currentTime;
    do {
      response = this.client.send(HttpRequest.newBuilder().GET().uri(URI.create(INITIAL_ENDPOINT))
          .headers("CF-Access-Client-Id", CF_ACCESS_CLIENT_ID, "CF-Access-Client-Secret",
              CF_ACCESS_CLIENT_SECRET).build(), HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() == 200) {
        return response;
      }
      Thread.sleep(500);
      currentTime = System.currentTimeMillis();
    } while (currentTime < (startTime + 5000));

    switch (response.statusCode()) {
      case 401 -> {
        throw new HttpConnectTimeoutException(String.format(
            "\nCode %d\nPermission Denied (headers were not passed in correctly)\nID: %s\nSecret: %s",
            response.statusCode(), CF_ACCESS_CLIENT_ID, CF_ACCESS_CLIENT_SECRET));
      }
      case 404 -> {
        throw new HttpConnectTimeoutException(
            String.format("\nCode %d\nCannot find server", response.statusCode()));
      }
      default -> {
        throw new HttpConnectTimeoutException(
            String.format("\nCode %d\nCould not connect to server", response.statusCode()));
      }
    }
  }

  public String getClientSecret()
      throws URISyntaxException, IOException, InterruptedException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {

    return (String) Objects.requireNonNull(stripResponse(this.client.send(
        HttpRequest.newBuilder().GET().uri(new URI(INITIAL_ENDPOINT + "/app"))
            .headers("CF-Access-Client-Id", CF_ACCESS_CLIENT_ID, "CF-Access-Client-Secret",
                CF_ACCESS_CLIENT_SECRET).build(), BodyHandlers.ofString()))).get("client_secret");

  }

  public Map<String, Object> getUser(String usersEmail)
      throws URISyntaxException, IOException, InterruptedException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {

    HttpResponse<String> response = this.client.send(HttpRequest.newBuilder().GET()
        .uri(new URI(INITIAL_ENDPOINT + "/user" + "?user_email=eq." + usersEmail))
        .headers("CF-Access-Client-Id", CF_ACCESS_CLIENT_ID, "CF-Access-Client-Secret",
            CF_ACCESS_CLIENT_SECRET).build(), HttpResponse.BodyHandlers.ofString());
    return stripResponse(response);

  }

  public Map<String, Object> getUser(int userId)
      throws URISyntaxException, IOException, InterruptedException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {

    HttpResponse<String> response = this.client.send(HttpRequest.newBuilder().GET()
        .uri(new URI(INITIAL_ENDPOINT + "/user" + "?user_id=eq." + userId))
        .headers("CF-Access-Client-Id", CF_ACCESS_CLIENT_ID, "CF-Access-Client-Secret",
            CF_ACCESS_CLIENT_SECRET).build(), HttpResponse.BodyHandlers.ofString());
    return stripResponse(response);

  }

  public HttpResponse<String> initUser(String userEmail, String authCode, String refreshToken)
      throws URISyntaxException, IOException, InterruptedException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {

    return this.client.send(HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(
                String.format("{\"user_email\":\"%s\",\"auth_code\":\"%s\",\"refresh_token\":\"%s\"}",
                    userEmail, Encryption.encrypt(authCode), Encryption.encrypt(refreshToken))))
            .uri(new URI(INITIAL_ENDPOINT + "/user?on_conflict=user_email"))
            .headers("CF-Access-Client-Id", CF_ACCESS_CLIENT_ID, "CF-Access-Client-Secret",
                CF_ACCESS_CLIENT_SECRET, "Prefer", "resolution=merge-duplicates").build(),
        HttpResponse.BodyHandlers.ofString());

  }

  public HttpResponse<String> deleteUser(String userEmail)
      throws URISyntaxException, IOException, InterruptedException {
    return this.client.send(HttpRequest.newBuilder().DELETE()
        .uri(new URI(INITIAL_ENDPOINT + "/user" + "?user_email=eq." + userEmail))
        .headers("CF-Access-Client-Id", CF_ACCESS_CLIENT_ID, "CF-Access-Client-Secret",
            CF_ACCESS_CLIENT_SECRET).build(), HttpResponse.BodyHandlers.ofString());
  }

  public HttpResponse<String> getResponse() {
    return response;
  }

  public void setResponse(HttpResponse<String> response) {
    this.response = response;
  }

}

