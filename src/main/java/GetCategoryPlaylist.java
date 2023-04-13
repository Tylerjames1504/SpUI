import se.michaelthelin.spotify.model_objects.specification.Category;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;
import se.michaelthelin.spotify.requests.data.browse.GetCategorysPlaylistsRequest;
import se.michaelthelin.spotify.requests.data.browse.GetListOfCategoriesRequest;

import java.util.ArrayList;
import java.util.Collections;

import static org.fusesource.jansi.Ansi.ansi;

public class GetCategoryPlaylist {
    private static GetListOfCategoriesRequest getListOfCategoriesRequest;
    private static GetCategorysPlaylistsRequest getCategoryRequest;

    private static Paging<PlaylistSimplified> playlistSimplifiedPaging;
    public static int offset;



    public static Paging<PlaylistSimplified>  getPlaylists() {
        try {
            getCategoryRequest = MainHELLO.spotifyApi.getCategorysPlaylists(MainHELLO.category)
                    .country(MainHELLO.location)
                    .limit(50)
//          .offset(0)
                    .build();

            playlistSimplifiedPaging = getCategoryRequest.execute();
            return playlistSimplifiedPaging;
        } catch (Exception e) {
            System.out.println(ansi().render("@|red Error: " + e.getMessage() + "|@"));
            return null;
        }
    }

    public static void printCategoryList(){
        try {
            offset = 0;
            boolean isListComplete = false;
            ArrayList<String> categories = new ArrayList<String>();
            while(!isListComplete){
                getListOfCategoriesRequest = MainHELLO.spotifyApi.getListOfCategories()
                .limit(50)
                .offset(offset)
                .build();
                Paging<Category> categoryPaging = getListOfCategoriesRequest.execute();
                for(int i = 0; i < categoryPaging.getItems().length; i++){
                    categories.add(categoryPaging.getItems()[i].getName());
                }
                offset += 50;
                if (categoryPaging.getItems().length == 0) {
                    isListComplete = true;
                }
            }

            Collections.sort(categories);
            for (String s : categories) {
                System.out.println(s);
            }
        } catch (Exception e) {
            System.out.println(ansi().render("@|red Error: " + e.getMessage() + "|@"));
        }
    }

}
