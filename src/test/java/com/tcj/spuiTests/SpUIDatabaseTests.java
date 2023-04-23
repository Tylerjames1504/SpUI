package com.tcj.spuiTests;

import static com.tcj.spui.SpUIDatabase.stripResponse;

import com.tcj.spui.SpUIDatabase;
import java.io.IOException;

import java.net.URISyntaxException;


import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;
import java.util.Objects;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SpUIDatabaseTests {

  SpUIDatabase db = new SpUIDatabase();

  public SpUIDatabaseTests() throws IOException, InterruptedException {
  }

  @Test
  public void successfulGetOnInitEndpointTest()
      throws URISyntaxException, IOException, InterruptedException {

    SpUIDatabase db = new SpUIDatabase();
    Assertions.assertEquals(200, db.getResponse().statusCode());

  }

  @Test
  public void getClientSecretTest() throws Exception {
    String CLIENT_SECRET = System.getenv("CLIENT_SECRET");
    Assertions.assertEquals(CLIENT_SECRET, this.db.getClientSecret());
  }

  @Test
  public void stripResponseResponseTest()
      throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeySpecException, InvalidKeyException {

    Assertions.assertInstanceOf(Map.class, stripResponse(this.db.getResponse()));

  }

  @Test
  public void stripResponseStringTest()
      throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeySpecException, InvalidKeyException {

    String testUser = "{\"user_id\":1,\"user_email\":\"admin@gmail.com\",\"auth_code\":\"aslkdfjaio\",\"refresh_token\":\"asldkfjawefj\"}";
    Assertions.assertEquals("{refresh_token=asldkfjawefj, user_email=admin@gmail.com, user_id=1, auth_code=aslkdfjaio}", Objects.requireNonNull(
        stripResponse(testUser)).toString());

  }

  @Test
  public void stripResponseStringFailTest() {

    String testUser = "{\"user_id\":1,\"user_email\":\"admin@gmail.com\",\"auth_code\":\"aslkdfjaio\",\"refresh_token";
    Assertions.assertThrows(JSONException.class, () -> {
      stripResponse(testUser);
    });

  }


  @Test
  public void addUserTest()
      throws URISyntaxException, IOException, InterruptedException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeySpecException, InvalidKeyException {

    Assertions.assertEquals(201, db.initUser("testUser", "testAuthCode", "testRefreshToken").statusCode());
    Assertions.assertEquals("testUser", db.getUser("testUser").get("user_email"));

  }

  @Test
  public void addUserThatIsAlreadyInDatabaseTest()
      throws URISyntaxException, IOException, InterruptedException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeySpecException, InvalidKeyException {

    Assertions.assertEquals(201,
        db.initUser("testUser", "testAuthCode2", "testRefreshToken2").statusCode());
    Assertions.assertEquals("testAuthCode2", db.getUser("testUser").get("auth_code"));
    Assertions.assertEquals("testRefreshToken2", db.getUser("testUser").get("refresh_token"));

  }


  @Test
  public void deleteUserTest()
      throws URISyntaxException, IOException, InterruptedException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeySpecException, InvalidKeyException {

    Assertions.assertEquals(204, db.deleteUser("testUser").statusCode());
    Assertions.assertNull(db.getUser("testUser"));

  }





}
