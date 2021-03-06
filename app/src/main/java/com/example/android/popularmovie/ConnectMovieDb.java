package com.example.android.popularmovie;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Dev Chanchal on 3/17/2016.
 */
public class ConnectMovieDb extends AsyncTask<String, Void, List<Movie>>{

    public AsyncResponse delegate;
    private final String LOG_TAG = ConnectMovieDb.class.getSimpleName();
    private final String API_KEY = "API_Key";
    private final String MOVIE_IMAGE_BASE = "http://image.tmdb.org/t/p/";
    private final String MOVIE_IMAGE_SIZE = "w185";

    public ConnectMovieDb(AsyncResponse delegate){
        this.delegate = delegate;
    }


    @Override
    protected List<Movie> doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String moviesJsonStr = null;

        try {

            final String BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
            final String SORT_BY = "sort_by";
            final String KEY = "api_key";
            String sortBy = params[0];

            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(SORT_BY, sortBy)
                    .appendQueryParameter(KEY, API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }
            moviesJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            return extractData(moviesJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<Movie> results) {
        if (results != null) {
            // return the List of movies back to the caller.
            delegate.onTaskCompleted(results);
        }
    }

    private String getYear(String date){
        final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        final Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(df.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return Integer.toString(cal.get(Calendar.YEAR));
    }

    private List<Movie> extractData(String moviesJsonStr) throws JSONException {

        // Items to extract
        final String ARRAY_OF_MOVIES = "results";
        final String ORIGINAL_TITLE = "original_title";
        final String IMAGE_PATH = "poster_path";
        final String DESCRIPTION = "overview";
        final String RATING = "vote_average";
        final String RELEASE_DATE = "release_date";

        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        JSONArray moviesArray = moviesJson.getJSONArray(ARRAY_OF_MOVIES);
        int moviesLength =  moviesArray.length();
        List<Movie> movies = new ArrayList<Movie>();

        for(int i = 0; i < moviesLength; ++i) {

            // for every movie in the JSON object create a new
            // movie object with all required data
            JSONObject movie = moviesArray.getJSONObject(i);
            String title = movie.getString(ORIGINAL_TITLE);
            String image = MOVIE_IMAGE_BASE + MOVIE_IMAGE_SIZE + movie.getString(IMAGE_PATH);
            String description = movie.getString(DESCRIPTION);
            String rating = movie.getString(RATING);
            String releaseDate = getYear(movie.getString(RELEASE_DATE));

            movies.add(new Movie(title, image, description, rating, releaseDate));

        }

        return movies;

    }
}
