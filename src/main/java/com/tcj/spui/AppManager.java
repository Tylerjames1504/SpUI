package com.tcj.spui;
import javafx.scene.Scene;
import javafx.stage.Stage;
import kotlin.reflect.KParameter;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;

public class AppManager {
    private AppUser user;
    private GuiManager guiManager;
    public AppManager(Stage initialStage) {
        AppUser user = new AppUser();
        GuiManager guiManager = new GuiManager(initialStage);
    }

    public AppUser getUser() {
        return user;
    }
    public GuiManager getGuiManager() {
        return guiManager;
    }

    public class AppUser {
        private AuthorizationManager authManager;
        public AppUser() {
            this.authManager = new AuthorizationManager();
        }

        private AuthorizationManager getAuthManager() {
            return this.authManager;
        }
        public class AuthorizationManager {
            private final String clientId = "4076d474b9884c8f88f3c9cf40f80890";
            private final URI redirectUri = URI.create("http://localhost:8080/callback");
            private SpotifyApi retrievedApi;
            private String authorizationCodeRequestLink;
            public AuthorizationManager() {

                this.retrievedApi = new SpotifyApi.Builder()
                        .setClientId(clientId)
                        .setClientSecret("b604863adab94bfb947b81500e03c78e") // get from database, currently hardcoded
                        .setRedirectUri(redirectUri)
                        .build();

                AuthorizationCodeUriRequest authorizationCodeUriRequest = this.retrievedApi.authorizationCodeUri()
                        .state("x4xkmn9pu3j6uwt7en")
                        .scope("user-read-private, user-read-email, playlist-read-private, playlist-read-collaborative, " +
                                "playlist-modify-public, playlist-modify-private, user-top-read")
                        .show_dialog(true).build();

                this.authorizationCodeRequestLink = authorizationCodeUriRequest.execute().toString();
            }

            public SpotifyApi getRetrievedApi() {
                return this.retrievedApi;
            }
            public String getAuthorizationCodeRequestLink() {
                return this.authorizationCodeRequestLink;
            }

        }
    }

    public class GuiManager { // current Stages: login
        public StageManager stageManager;
        public GuiManager(Stage initialStage) {
            this.stageManager.addStage("login", initialStage);
        }
        public class StageManager {
            private HashMap<String, SceneManager> stageSet = new HashMap();
            public SceneManager retrieveStageSubNetworkWithKey(String stageKey) {
                return stageSet.get(stageKey);
            }
            public void addStage(String stageName, Stage stage) {
                SceneManager stageChild = new SceneManager(stage);
                stageSet.put(stageName, stageChild);
            }
            //public void buildStage(Stage stage,String iconPath, String title, Boolean centerOnScreen, )
            public class SceneManager {
                private HashMap<String, Scene> sceneSetOfStageSet = new HashMap();
                private Stage parentStage;
                public SceneManager(Stage stage) {
                    this.parentStage = stage;
                }
                public Stage getParentStage() {
                    return this.parentStage;
                }
                public void addScene(String sceneName, Scene scene) {
                    sceneSetOfStageSet.put(sceneName, scene);
                }
            }

        }



    }


}
