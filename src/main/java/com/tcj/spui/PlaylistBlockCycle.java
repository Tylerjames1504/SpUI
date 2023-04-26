package com.tcj.spui;

/*
 * Linked list node for storing chunks of 4 PlaylistData objects (returned from Spotify Web API Calls)
 * Each node points to another node which is the next Block, and eventually forms a cycle
 * Allows for easy pagination of a user's playlists in the Playlist Manager scene
 */
public class PlaylistBlockCycle {

    public PlaylistData[] block = new PlaylistData[4];
    public PlaylistBlockCycle next;
    public PlaylistBlockCycle previous;


}
