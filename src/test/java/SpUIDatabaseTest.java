import static com.tcj.spui.SpUIDatabase.INITIAL_ENDPOINT;

import com.tcj.spui.SpUIDatabase;
import java.io.IOException;
import java.lang.reflect.MalformedParameterizedTypeException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import org.junit.*;

import static com.tcj.spui.SpUIDatabase.stripResponse;
import static org.junit.Assert.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SpUIDatabaseTest {

  @Test
  public void testSuccessfulGetOnInitEndpoint()
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
  public void testStripResponse() throws Exception {
    SpUIDatabase db = new SpUIDatabase();
    Assertions.assertInstanceOf(HashMap.class, stripResponse(db.getResponse()));
  }

}
