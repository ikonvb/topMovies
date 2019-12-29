package com.konstantinbulygin.topmovies.utils;


import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URL;

// class for work with internet data
public class NetworkUtils {

    /* EXAMPLE OF QUERY STRING
    *
    * https://api.themoviedb.org/3/discover/movie?
    * api_key=04c7da1b576ba3df53d95260684498af&
    * language=en-US&
    * sort_by=vote_average.desc&
    * page=1
    * */

    //base params
    private static final String BASE_URL = "https://api.themoviedb.org/3/discover/movie";

    //list of params for query
    private static final String PARAMS_API_KEY = "api_key";
    private static final String PARAMS_LANGUAGE = "language";
    private static final String PARAMS_SORT_BY = "sort_by";
    private static final String PARAMS_PAGE = "page";

    //list of params for query
    private static final String API_KEY = "04c7da1b576ba3df53d95260684498af";
    private static final String LANGUAGE_VALUES = "en-Us";
    private static final String SORT_BY_POPULARITY = "popularity.desc";
    private static final String SORT_BY_TOP_RATED = "vote_average.desc";

    //list of params for sorting by...
    public static final int POPULARITY = 0;
    public static final int TOP_RATED = 1;

    //method for creating string of URL
    public static URL buildUrl(int sortBy, int page) {
        URL result = null;
        String SORT_TYPE = "";

        //choose sort method
        if (sortBy == POPULARITY) {
            SORT_TYPE = SORT_BY_POPULARITY;
        } else if (sortBy == TOP_RATED){
            SORT_TYPE = SORT_BY_TOP_RATED;
        }

        //create string of url with query
        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(PARAMS_API_KEY, API_KEY)             // insert api_key params
                .appendQueryParameter(PARAMS_LANGUAGE, LANGUAGE_VALUES)    // insert language values
                .appendQueryParameter(PARAMS_SORT_BY, SORT_TYPE)           // insert method of sort
                .appendQueryParameter(PARAMS_PAGE, Integer.toString(page)) // insert page
                .build();

        try {

            result = new URL(uri.toString());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return result;
    }

}
