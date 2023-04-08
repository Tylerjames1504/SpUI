package com.tcj.spui;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SpUIDatabase {

  public static final String INITIAL_ENDPOINT = "https://spui.tylerturner.tech";

  public HttpClient client;

  private String authHeader;


  private HttpResponse<String> response;


  public SpUIDatabase() throws URISyntaxException, IOException, InterruptedException {

    this.client = HttpClient.newHttpClient();
    this.response = initalConnect();

  }

  public HttpResponse<String> initalConnect() throws IOException, InterruptedException {

    HttpResponse<String> response;
    long startTime = System.currentTimeMillis();
    long currentTime;
    do {
      response = this.client.send(
          HttpRequest.newBuilder().GET().uri(URI.create(INITIAL_ENDPOINT))
              .headers("CF-Access-Client-Id", Secrets.id, "CF-Access-Client-Secret",
                  Secrets.secret).build(), HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() == 200) {
        return response;
      }
      currentTime = System.currentTimeMillis();
    }
    while (currentTime < (startTime + 5000));

    switch (response.statusCode()) {
      case 401 -> {
        throw new HttpConnectTimeoutException(
            String.format("\nCode %d\nPermission Denied", response.statusCode()));
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

  public static HashMap<String, String> stripResponse(HttpResponse<String> response) throws Exception {

    ArrayList<String> responseAsList = new ArrayList<>(
        List.of(response.body().replaceAll("[\\[\\]{\"}]", "").split(":")));
    if (responseAsList.size() % 2 != 0) {
      throw new Exception("Response not an even number");
    }
    HashMap<String, String> outputMap = new HashMap<>();
    for (int i = 0; i < responseAsList.size(); i += 2) {
      outputMap.put(responseAsList.get(i), responseAsList.get(i+1));
    }
    return outputMap;
  }

  public String getClientSecret()
      throws Exception {

    return stripResponse(this.client.send(HttpRequest.newBuilder().GET().uri(new URI(INITIAL_ENDPOINT + "/app")).headers("CF-Access-Client-Id", Secrets.id, "CF-Access-Client-Secret",
        Secrets.secret).build(), HttpResponse.BodyHandlers.ofString())).get("client_secret");

  }

  public HttpResponse<String> getResponse() {
    return response;
  }

  public void setResponse(HttpResponse<String> response) {
    this.response = response;
  }

  public static void main(String[] args)
      throws Exception {

    SpUIDatabase db = new SpUIDatabase();
    System.out.println(db.response.body());

    System.out.println(db.getClientSecret());
  }

}
