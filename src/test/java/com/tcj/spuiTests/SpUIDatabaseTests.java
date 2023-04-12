package com.tcj.spuiTests;

import static com.tcj.spui.SpUIDatabase.stripResponse;

import com.tcj.spui.SpUIDatabase;
import java.io.IOException;

import java.net.URISyntaxException;


import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SpUIDatabaseTests {

  SpUIDatabase db = new SpUIDatabase();

  public SpUIDatabaseTests() throws IOException, InterruptedException {
  }

  @Test
  public void TestSuccessfulGetOnInitEndpoint()
      throws URISyntaxException, IOException, InterruptedException {

    SpUIDatabase db = new SpUIDatabase();
    Assertions.assertEquals(200, db.getResponse().statusCode());

  }

  @Test
  public void TestGetClientSecret() throws Exception {
    String CLIENT_SECRET = System.getenv("CLIENT_SECRET");
    Assertions.assertEquals(CLIENT_SECRET, this.db.getClientSecret());
  }

  @Test
  public void TestStripResponseResponse() {

    Assertions.assertInstanceOf(Map.class, stripResponse(this.db.getResponse()));

  }

  @Test
  public void TestStripResponseString() {
    String testUser = "{\"user_id\":1,\"user_email\":\"admin@gmail.com\",\"auth_code\":\"aslkdfjaio\",\"refresh_token\":\"asldkfjawefj\"}";
    Assertions.assertEquals("{refresh_token=asldkfjawefj, user_email=admin@gmail.com, user_id=1, auth_code=aslkdfjaio}", stripResponse(testUser).toString());

  }

  @Test
  public void AddUserTest() throws URISyntaxException, IOException, InterruptedException {

    Assertions.assertEquals(201, db.initUser("testUser", "testAuthCode", "testRefreshToken").statusCode());
    Assertions.assertEquals("testUser", db.getUser("testUser").get("user_email"));

  }
  @Test
  public void TestDeleteUser() throws URISyntaxException, IOException, InterruptedException {

    Assertions.assertEquals(204, db.deleteUser("testUser").statusCode());
    Assertions.assertNull(db.getUser("testUser"));

  }



}
