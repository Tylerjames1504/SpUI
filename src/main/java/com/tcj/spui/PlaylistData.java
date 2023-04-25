package com.tcj.spui;

import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.*;
import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class PlaylistData {
    private String playlistId;
    public Playlist thisPlaylist;
    public String topGenre = "Miscellaneous";
    public PlaylistData(String playlistId) {
        this.playlistId = playlistId;
        refresh();
    }

    public void refresh() {
        updatePlaylist();
        //calculateTopGenre();
    }
    public void calculateTopGenre() {
        HashMap<String, Integer> genreMap = new HashMap<>();
        String max = "";
        int maxAmount = 0;
        try {
            SpotifyApi spotifyApi = App.session.getAppUser()
                    .getUserAuthorizationManager()
                    .getRetrievedApi();
            for (PlaylistTrack track : this.thisPlaylist.getTracks().getItems()) {
                Track track1 = spotifyApi.getTrack(track.getTrack().getId()).build().execute();
                List<ArtistSimplified> artistSimplifiedList = List.of(track1.getArtists());
                for (ArtistSimplified artistSimplified : artistSimplifiedList) {
                    Artist artist = spotifyApi.getArtist(artistSimplified.getId()).build().execute();
                    List<String> genreList = List.of(artist.getGenres());
                    for (String genre : genreList) {
                        if (genreMap.containsKey(genre)) {
                            int count = genreMap.get(genre) + 1;
                            genreMap.put(genre, genreMap.get(genre) + 1);
                            if (count > maxAmount) {
                                maxAmount = count;
                                max = genre;
                            }
                        } else {
                            genreMap.put(genre, 1);
                        }
                    }
                }
            }
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            e.printStackTrace();
        }
        this.topGenre = max;
    }

    public void updatePlaylist() {
        try {
            final GetPlaylistRequest getPlaylistRequest = App.session.getAppUser()
                    .getUserAuthorizationManager()
                    .getRetrievedApi()
                    .getPlaylist(this.playlistId)
                    .build();
            this.thisPlaylist = getPlaylistRequest.execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println(e);
        }
    }

}
