import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;


public class App extends Application {
    public static ClientManager session; // session is accessible across all controllers which allows them to access spotifyApi data and also refresh the auth token
    @Override
    public void start(Stage initialStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
        session = new ClientManager();
        Parent base = loader.load();
        initialStage.setTitle("Login");
        initialStage.centerOnScreen();
        initialStage.getIcons().add(new Image(getClass().getResourceAsStream("Icon.png")));
        Scene login = new Scene(base);
        initialStage.setScene(login);
        initialStage.setResizable(false);
        login.getStylesheets().add(Objects.requireNonNull(this.getClass().getResource("login_style.css")).toExternalForm());
        initialStage.show();
    }

    public static void main(String[] args) { launch();}


}