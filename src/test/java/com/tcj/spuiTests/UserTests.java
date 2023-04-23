package com.tcj.spuiTests;

import com.tcj.spui.SpUIDatabase;
import com.tcj.spui.User;
import java.io.IOException;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UserTests {

  SpUIDatabase db = new SpUIDatabase();

  public UserTests() throws IOException, InterruptedException {
  }


  @Test
  public void getUserIdFromUserClassTest() throws Exception {
    User user = new User("user@gmail.com", this.db);
    Assertions.assertEquals(69, user.getId());
  }

  @Test
  public void getUserEmailFromUserClassTest() throws Exception {
    User user = new User("user@gmail.com", this.db);
    Assertions.assertEquals(user.getEmail(), "user@gmail.com");
  }

  @Test
  public void getAuthCodeFromUserClassTest() throws Exception {
    User user = new User("user@gmail.com", this.db);
    Assertions.assertEquals(user.getAuthCode(), "aslkdfjaio");
  }

  @Test
  public void getRefreshTokenFromUserClassTest() throws Exception {
    User user = new User("user@gmail.com", this.db);
    Assertions.assertEquals(user.getRefreshToken(), "asldkfjawefj");
  }

  @Test
  public void getUserThatDoesNotExistTest() throws Exception {

    Assertions.assertThrows(NoSuchElementException.class, () -> {
      User user = new User("noSuchUser@gmail.com", this.db);
      user.toString();
    });
  }
}
