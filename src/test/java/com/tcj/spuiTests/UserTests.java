package com.tcj.spuiTests;

import com.tcj.spui.SpUIDatabase;
import com.tcj.spui.User;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.NoSuchElementException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UserTests {

  SpUIDatabase db = new SpUIDatabase();

  String testUserEmail = "user@test.com";

  String testAuthCode = "BQDGsB78fqCI4qPmDlwWzmTiJf4sVab7QY852yx7XgxpdCB6ehTeTT0zQgF-YKAvFcF_BtUnwpkhwgy3fnpA8bLibeLC1auUYb2Jg5PJYtOGHzoxtqqUFmaT8QXOjsLcKrf5fRLaeOVvU3jvehMJ-GGY-oiNTUKBruuHnAeY0V9WPbE7mswqt8D2iSKBZcK799ugs1ejxI-M9jaYC4GSD49fyr0sn6Eh5WpdP1-LBqAZ7E4pfoA5NCEQASHKYxBbGwasdfghjklz";

  String testRefreshToken = "AQApD0xfa9TypNeAjB5LC5x7fss0YwmHPYtfGzIbaPzgN8_VEOO_vaBF-TpCsC-z_YNgtePPDdr_kfhVOIpHm8lm3HNXu0B7wcFEhhMnbH0Xbz2b-KfC2K0OYasdfghjklz";

  int testUserId;

  public UserTests()
      throws IOException, InterruptedException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, URISyntaxException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {

    db.initUser(testUserEmail, testAuthCode, testRefreshToken);
    testUserId = (int) db.getUser(testUserEmail).get("user_id");

  }




  @Test
  public void getUserIdFromUserClassTest() throws Exception {
    User user = new User("user@test.com", this.db);
    Assertions.assertEquals(testUserId, user.getId());
  }

  @Test
  public void getUserEmailFromUserClassTest() throws Exception {
    User user = new User("user@test.com", this.db);
    Assertions.assertEquals(testUserEmail, user.getEmail());
  }

  @Test
  public void getAuthCodeFromUserClassTest() throws Exception {
    User user = new User("user@test.com", this.db);
    Assertions.assertEquals(testAuthCode, user.getAuthCode());
  }

  @Test
  public void getRefreshTokenFromUserClassTest() throws Exception {
    User user = new User("user@test.com", this.db);
    Assertions.assertEquals(testRefreshToken, user.getRefreshToken());
  }

  @Test
  public void getUserThatDoesNotExistTest() throws Exception {

    Assertions.assertThrows(NoSuchElementException.class, () -> {
      User user = new User("noSuchUser@gmail.com", this.db);
      user.toString();
    });
  }


}
