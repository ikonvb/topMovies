package com.konstantinbulygin.topmovies.utils;


import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

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
    private static final String BASE_VIDEO_URL = "https://api.themoviedb.org/3/movie/%s/videos";
    private static final String BASE_REVIEWS_URL = "https://api.themoviedb.org/3/movie/%s/reviews";

    //list of params for query with arguments
    private static final String PARAMS_API_KEY = "api_key";
    private static final String PARAMS_LANGUAGE = "language";
    private static final String PARAMS_SORT_BY = "sort_by";
    private static final String PARAMS_PAGE = "page";
    private static final String PARAMS_MIN_VOTE_COUNT = "vote_count.gte";

    //list of params for query with values
    private static final String API_KEY = "04c7da1b576ba3df53d95260684498af";
    private static final String LANGUAGE_VALUES = "en-Us";
    private static final String SORT_BY_POPULARITY = "popularity.desc";
    private static final String SORT_BY_TOP_RATED = "vote_average.desc";
    private static final String MIN_VOTE_COUNT_VALUE = "1000";

    //list of params for sorting by...
    public static final int POPULARITY = 0;
    public static final int TOP_RATED = 1;


    public static URL buildUrlToReviews(int id, String lang) {

        URL result = null;

        //create string of url with query with params and values
        Uri uri = Uri.parse(String.format(BASE_REVIEWS_URL, id)).buildUpon()
                .appendQueryParameter(PARAMS_API_KEY, API_KEY)             // insert api_key params
                .appendQueryParameter(PARAMS_LANGUAGE, lang)    // insert language values
                .build();

        try {

            result = new URL(uri.toString());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return result;
    }


    public static JSONObject getJSONForReviews(int id, String lang) {

        JSONObject result = null;
        URL url = buildUrlToReviews(id, lang);

        try {

            result = new JSONLoadTask().execute(url).get();

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static URL buildUrlToVideo(int id, String lang) {

        URL result = null;

        //create string of url with query with params and values
        Uri uri = Uri.parse(String.format(BASE_VIDEO_URL, id)).buildUpon()
                .appendQueryParameter(PARAMS_API_KEY, API_KEY)             // insert api_key params
                .appendQueryParameter(PARAMS_LANGUAGE, lang)    // insert language values
                .build();

        try {

            result = new URL(uri.toString());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static JSONObject getJSONForVideo(int id, String lang) {

        JSONObject result = null;
        URL url = buildUrlToVideo(id, lang);

        try {

            result = new JSONLoadTask().execute(url).get();

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result;
    }



    //method for creating string of URL
    public static URL buildUrl(int sortBy, int page, String lang) {

        URL result = null;
        String SORT_TYPE = "";

        //choose sort method
        if (sortBy == POPULARITY) {
            SORT_TYPE = SORT_BY_POPULARITY;
        } else if (sortBy == TOP_RATED){
            SORT_TYPE = SORT_BY_TOP_RATED;
        }

        //create string of url with query with params and values
        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(PARAMS_API_KEY, API_KEY)             // insert api_key params
                .appendQueryParameter(PARAMS_LANGUAGE, lang)    // insert language values
                .appendQueryParameter(PARAMS_SORT_BY, SORT_TYPE)           // insert method of sort
                .appendQueryParameter(PARAMS_MIN_VOTE_COUNT, MIN_VOTE_COUNT_VALUE)
                .appendQueryParameter(PARAMS_PAGE, Integer.toString(page)) // insert page
                .build();

        try {

            result = new URL(uri.toString());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return result;
    }



    //class for async download JSON object from internet
    public static class JSONLoadTask extends AsyncTask<URL, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(URL... urls) {

            JSONObject result = null;

            if (urls == null || urls.length == 0) {
                return result;
            } else {
                //create url connection
                HttpURLConnection urlConnection = null;

                try {

                    urlConnection = (HttpURLConnection) urls[0].openConnection();
                    InputStream inputStream = urlConnection.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader reader = new BufferedReader(inputStreamReader);

                    StringBuilder builder = new StringBuilder();
                    String line = reader.readLine();

                    while (line != null) {
                        builder.append(line);
                        line = reader.readLine();
                    }

                    result = new JSONObject(builder.toString());

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }

                return result;
            }
        }
    }


    public static JSONObject getJSONFromNetwork(int sortBy, int page, String lang) {

        JSONObject result = null;
        URL url = buildUrl(sortBy, page, lang);
        try {

            result = new JSONLoadTask().execute(url).get();

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result;
    }


    public static class JSONLoader extends AsyncTaskLoader<JSONObject> {

        private Bundle bundle;
        private OnStartLoadingListener onStartLoadingListener;

        public interface OnStartLoadingListener{
            void onStartLoading();
        }

        public void setOnStartLoadingListener(OnStartLoadingListener onStartLoadingListener) {
            this.onStartLoadingListener = onStartLoadingListener;
        }

        public JSONLoader(@NonNull Context context, Bundle bundle) {
            super(context);
            this.bundle = bundle;
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            if (onStartLoadingListener != null) {
                onStartLoadingListener.onStartLoading();
            }
            forceLoad();
        }

        @Nullable
        @Override
        public JSONObject loadInBackground() {

            if (bundle == null) {
                return null;
            }



            String urlAsString = bundle.getString("url");
            URL url = null;

            try {
                url = new URL(urlAsString);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            JSONObject result = null;

            if (url == null) {
                return null;
            } else {
                //create url connection
                HttpURLConnection urlConnection = null;

                try {

                    urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = urlConnection.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader reader = new BufferedReader(inputStreamReader);

                    StringBuilder builder = new StringBuilder();
                    String line = reader.readLine();

                    while (line != null) {
                        builder.append(line);
                        line = reader.readLine();
                    }

                    result = new JSONObject(builder.toString());

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }

                return result;
            }
        }
    }


}
