package com.konstantinbulygin.topmovies.database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainViewModel extends AndroidViewModel {

    private static MovieDatabase database;
    private LiveData<List<Movie>> movies;

    public MainViewModel(@NonNull Application application) {
        super(application);
        database = MovieDatabase.getInstance(getApplication());
        movies = database.moviDao().getAllMovies();
    }

    public Movie getMovieById(int id) {
        try {
            return new GetMovieTask().execute(id).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteAllMovies() {
        new DeleteMovieTask().execute();
    }

    public void insertMovie(Movie movie) {
        new InsertMovieTask().execute(movie);
    }

    public void deleteOneMovie(Movie movie) {
        new DeleteOneMovieTask().execute(movie);
    }

    public static class GetMovieTask extends AsyncTask<Integer, Void, Movie> {

        @Override
        protected Movie doInBackground(Integer... integers) {
            if (integers != null && integers.length > 0) {
                return database.moviDao().getMovieById(integers[0]);
            }

            return null;
        }
    }

    public static class DeleteMovieTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            if (voids != null && voids.length > 0) {
                database.moviDao().deleteAllMovies();
            }
            return null;
        }
    }

    public static class DeleteOneMovieTask extends AsyncTask<Movie, Void, Void> {

        @Override
        protected Void doInBackground(Movie... movies) {

            if (movies != null && movies.length > 0) {
                database.moviDao().deleteMovie(movies[0]);
            }
            return null;
        }
    }

    public static class InsertMovieTask extends AsyncTask<Movie, Void, Void> {

        @Override
        protected Void doInBackground(Movie... movies) {

            if (movies != null && movies.length > 0) {
                database.moviDao().insertMovie(movies[0]);
            }

            return null;
        }
    }
}
