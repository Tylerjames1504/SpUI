package com.tcj.spui;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.Gson;

public class SpUIDatabase {

  public static final String INITIAL_ENDPOINT = "https://spui.tylerturner.tech";

  public HttpClient client;

  private String authHeader;


  public SpUIDatabase() throws URISyntaxException {

    this.client = HttpClient.newHttpClient();


  }

  public HttpResponse<String> databaseResponse(HttpRequest request)
      throws IOException, InterruptedException {

    return this.client.send(request, HttpResponse.BodyHandlers.ofString());

  }

  public static void main(String[] args)
      throws IOException, InterruptedException, URISyntaxException {

    SpUIDatabase db = new SpUIDatabase();

    HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(INITIAL_ENDPOINT))
        .headers("CF-Access-Client-Id", CFServiceToken.id, "CF-Access-Client-Secret",
            CFServiceToken.secret).build();
    HttpResponse<String> response = db.client.send(request, HttpResponse.BodyHandlers.ofString());
    System.out.println(response.statusCode());
  }

}
