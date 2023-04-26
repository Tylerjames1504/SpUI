package com.tcj.spui;

import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.special.SnapshotResult;
import se.michaelthelin.spotify.model_objects.specification.*;
import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistsItemsRequest;
import se.michaelthelin.spotify.requests.data.playlists.ReorderPlaylistsItemsRequest;

import java.io.IOException;

public class PlaylistData {
    private SpotifyApi spotifyApi = App.session.getAppUser()
            .getUserAuthorizationManager()
            .getRetrievedApi();
    private String playlistId;
    public Playlist thisPlaylist;
    public Paging<PlaylistTrack> playlistInfo;
    public int position;
    public boolean selected = false;
    public PlaylistData(String playlistId, int position) {
        this.position = position;
        this.playlistId = playlistId;
        refresh();
    }

    public void refresh() {
        updatePlaylist();
        getElements();
    }
    public void getElements() {
        try {
            GetPlaylistsItemsRequest getPlaylistsItemsRequest = this.spotifyApi
                    .getPlaylistsItems(this.playlistId)
                    .build();
            this.playlistInfo = getPlaylistsItemsRequest.execute();
        }
        catch (IOException | SpotifyWebApiException | ParseException e) {
            e.printStackTrace();
        }

    }

    public void updatePlaylist() {
        try {
            final GetPlaylistRequest getPlaylistRequest = this.spotifyApi
                    .getPlaylist(this.playlistId)
                    .build();
            this.thisPlaylist = getPlaylistRequest.execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println(e);
        }
    }
    public void reorderItems() {
        ReorderPlaylistsItemsRequest reorderPlaylistsItemsRequest = this.spotifyApi.
                reorderPlaylistsItems(this.playlistId, 0, 0)
//          .range_length(rangeLength)
//          .snapshot_id("JbtmHBDBAYu3/bt8BOXKjzKx3i0b6LCa/wVjyl6qQ2Yf6nFXkbmzuEa+ZI/U1yF+")
                .build();
        try {
            SnapshotResult snapshotResult = reorderPlaylistsItemsRequest.execute();
        }
        catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println(e);
        }
    }

}
