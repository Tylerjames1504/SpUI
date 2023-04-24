import se.michaelthelin.spotify.model_objects.specification.Playlist;
import se.michaelthelin.spotify.requests.data.playlists.CreatePlaylistRequest;
import se.michaelthelin.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;

import static org.fusesource.jansi.Ansi.ansi;

public class CreatePlaylist {
    private static CreatePlaylistRequest createPlaylistRequest;
    private static GetCurrentUsersProfileRequest getCurrentUsersProfileRequest;

    public static void execute() {
        try {
            getCurrentUsersProfileRequest = MainHELLO.spotifyApi.getCurrentUsersProfile()
                    .build();

            createPlaylistRequest = MainHELLO.spotifyApi.createPlaylist(getCurrentUsersProfileRequest.execute().getId(), MainHELLO.name)
                    .public_(MainHELLO.playlistPublic)
                    .build();

            Playlist playlist = createPlaylistRequest.execute();

            System.out.println(ansi().render("@|green Name: |@" + playlist.getName()));
        } catch (Exception e) {
            System.out.println(ansi().render("@|red Error: " + e.getMessage() + "|@"));
        }
    }

}
