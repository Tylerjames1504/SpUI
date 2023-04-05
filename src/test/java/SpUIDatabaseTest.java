import static com.tcj.spui.SpUIDatabase.INITIAL_ENDPOINT;

import com.tcj.spui.CFServiceToken;
import com.tcj.spui.SpUIDatabase;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import org.junit.*;

import static org.junit.Assert.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SpUIDatabaseTest {

//  @Test
//  public void testNoAuthHeader() throws URISyntaxException, IOException, InterruptedException {
//
//    SpUIDatabase db = new SpUIDatabase();
//    HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(INITIAL_ENDPOINT)).build();
//    Assertions.assertEquals(401, db.databaseResponse().statusCode());
//
//  }

  @Test
  public void testSuccessfulGetOnInitEndpoint()
      throws URISyntaxException, IOException, InterruptedException {

    SpUIDatabase db = new SpUIDatabase();
    Assertions.assertEquals(200, db.databaseResponse().statusCode());

  }

}
