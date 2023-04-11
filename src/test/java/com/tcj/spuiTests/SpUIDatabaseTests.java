package com.tcj.spuiTests;

import static com.tcj.spui.SpUIDatabase.stripResponse;

import com.tcj.spui.SpUIDatabase;
import java.io.IOException;

import java.net.URISyntaxException;

import java.util.ArrayList;


import java.util.Map;
import org.junit.platform.commons.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SpUIDatabaseTests {

  @Test
  public void TestSuccessfulGetOnInitEndpoint()
      throws URISyntaxException, IOException, InterruptedException {

    SpUIDatabase db = new SpUIDatabase();
    Assertions.assertEquals(200, db.getResponse().statusCode());

  }

  @Test
  public void testGetClientSecret() throws Exception {
    String CLIENT_SECRET = System.getenv("CLIENT_SECRET");
    SpUIDatabase db = new SpUIDatabase();
    Assertions.assertEquals(CLIENT_SECRET, db.getClientSecret());
  }

  @Test
  public void TestStripResponse() throws Exception {
    SpUIDatabase db = new SpUIDatabase();

    Assertions.assertInstanceOf(Map.class, stripResponse(db.getResponse()));
  }



}
