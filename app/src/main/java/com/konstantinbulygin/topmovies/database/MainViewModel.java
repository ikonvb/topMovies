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
    private LiveData<List<FavouriteMovies>> favouriteMovies;

    public MainViewModel(@NonNull Application application) {
        super(application);
        database = MovieDatabase.getInstance(getApplication());
        movies = database.moviDao().getAllMovies();
        favouriteMovies = database.moviDao().getAllFavouriteMovies();
    }

    public LiveData<List<FavouriteMovies>> getFavouriteMovies() {
        return favouriteMovies;
    }

    public LiveData<List<Movie>> getMovies() {
        return movies;
    }

    public FavouriteMovies getFavouriteMovieById(int id) {
        try {
            return new GetFavouriteMovieTask().execute(id).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
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


    public void inserFavouritetMovie(FavouriteMovies movie) {
        new InsertFavouriteMovieTask().execute(movie);
    }

    public void deleteFavouriteMovie(FavouriteMovies movie) {
        new DeleteFavouriteMovieTask().execute(movie);
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

    public static class GetFavouriteMovieTask extends AsyncTask<Integer, Void, FavouriteMovies> {

        @Override
        protected FavouriteMovies doInBackground(Integer... integers) {
            if (integers != null && integers.length > 0) {
                return database.moviDao().getFavouriteMovieById(integers[0]);
            }
            return null;
        }
    }

    public static class DeleteMovieTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {


            database.moviDao().deleteAllMovies();
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

    public static class InsertFavouriteMovieTask extends AsyncTask<FavouriteMovies, Void, Void> {

        @Override
        protected Void doInBackground(FavouriteMovies... movies) {

            if (movies != null && movies.length > 0) {
                database.moviDao().insertFavouriteMovie(movies[0]);
            }

            return null;
        }
    }

    public static class DeleteFavouriteMovieTask extends AsyncTask<FavouriteMovies, Void, Void> {

        @Override
        protected Void doInBackground(FavouriteMovies... movies) {

            if (movies != null && movies.length > 0) {
                database.moviDao().deleteFavouriteMovie(movies[0]);
            }
            return null;
        }
    }
}
