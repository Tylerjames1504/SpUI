package com.tcj.spuiTests;

import static org.junit.Assert.assertThrows;

import com.tcj.spui.SpUIDatabase;
import com.tcj.spui.User;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UserTests {

  SpUIDatabase db = new SpUIDatabase();

  public UserTests() throws URISyntaxException, IOException, InterruptedException {
  }


//  @Test
//  public void getUserIdFromUserClassTest()
//      throws Exception {
//    User user = new User("user@gmail.com", this.db);
//    Assertions.assertEquals(4, user.getId());
//  }
  @Test
  public void getUserEmailFromUserClassTest()
      throws Exception {
    User user = new User("user@gmail.com", this.db);
    Assertions.assertEquals(user.getEmail(), "user@gmail.com");
  }

  @Test
  public void getAuthCodeFromUserClassTest()
      throws Exception {
    User user = new User("user@gmail.com", this.db);
    Assertions.assertEquals(user.getAuthCode(), "aslkdfjaio");
  }

  @Test
  public void getRefreshTokenFromUserClassTest()
      throws Exception {
    User user = new User("user@gmail.com", this.db);
    Assertions.assertEquals(user.getRefreshToken(), "asldkfjawefj");
  }

//  @Test void getUserThatDoesNotExistTest() throws Exception {
//    User user = new User("noSuchUser@gmail.com", this.db);
//    Assertions.assertNull(user.getEmail());
//  }
}
