package com.konstantinbulygin.topmovies;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.konstantinbulygin.topmovies.adapters.ReviewAdapter;
import com.konstantinbulygin.topmovies.adapters.TrailerAdapter;
import com.konstantinbulygin.topmovies.database.FavouriteMovies;
import com.konstantinbulygin.topmovies.database.MainViewModel;
import com.konstantinbulygin.topmovies.database.Movie;
import com.konstantinbulygin.topmovies.database.Review;
import com.konstantinbulygin.topmovies.database.Trailer;
import com.konstantinbulygin.topmovies.utils.JSONUtils;
import com.konstantinbulygin.topmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private ImageView imageViewBigPoster;

    private TextView textViewTitle;
    private TextView textViewOriginalTitle;
    private TextView textViewRating;
    private TextView textViewReleaseDate;
    private TextView textViewOverview;
    private int id;
    private MainViewModel viewModel;
    private Movie movie;
    private ImageView imageViewAddToFavorite;
    private FavouriteMovies favouriteMovie;

    private RecyclerView recyclerViewTrailers;
    private RecyclerView recyclerViewReviews;
    private ReviewAdapter reviewAdapter;
    private TrailerAdapter trailerAdapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case R.id.itemMain:
                Intent intent1 = new Intent(this, MainActivity.class);
                startActivity(intent1);
                break;
            case R.id.itemFavourite:
                Intent intent2 = new Intent(this, FavouriteActivity.class);
                startActivity(intent2);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        imageViewAddToFavorite = findViewById(R.id.imageViewAddToFavorite);
        imageViewBigPoster = findViewById(R.id.imageViewBigPoster);
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewOriginalTitle = findViewById(R.id.textViewOriginalTitle);
        textViewRating = findViewById(R.id.textViewRating);
        textViewReleaseDate = findViewById(R.id.textViewReleaseDate);
        textViewOverview = findViewById(R.id.textViewOverview);

        Intent intent = getIntent();

        if (intent.hasExtra("id")) {
            id = intent.getIntExtra("id", -1);
        } else {
            finish();
        }

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        movie = viewModel.getMovieById(id);
        Picasso.get().load(movie.getBigPosterPath()).into(imageViewBigPoster);
        textViewTitle.setText(movie.getTitle());
        textViewOriginalTitle.setText(movie.getOriginalTitle());
        textViewRating.setText(Double.toString(movie.getVoteAverage()));
        textViewReleaseDate.setText(movie.getReleaseDate());
        textViewOverview.setText(movie.getOverview());
        setFavourite();
    }

    public void onClickChangeFavourite(View view) {

        if (favouriteMovie == null) {
            viewModel.inserFavouritetMovie(new FavouriteMovies(movie));
            Toast.makeText(this, R.string.add_to_favourite, Toast.LENGTH_SHORT).show();
        } else {
            viewModel.deleteFavouriteMovie(favouriteMovie);
            Toast.makeText(this, R.string.remove_from_favourite, Toast.LENGTH_SHORT).show();
        }
        setFavourite();

        recyclerViewTrailers = findViewById(R.id.recyclerViewTrailers);
        recyclerViewReviews = findViewById(R.id.recyclerViewReviews);
        reviewAdapter = new ReviewAdapter();
        trailerAdapter = new TrailerAdapter();
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTrailers.setLayoutManager(new LinearLayoutManager(this));

        recyclerViewReviews.setAdapter(reviewAdapter);
        recyclerViewTrailers.setAdapter(trailerAdapter);
        JSONObject jsonObjectTrailers = NetworkUtils.getJSONForVideo(movie.getId());
        JSONObject jsonObjectReviews = NetworkUtils.getJSONForReviews(movie.getId());

        List<Trailer> trailers = JSONUtils.getTrailerFromJSON(jsonObjectTrailers);
        List<Review> reviews = JSONUtils.getReviewsFromJSON(jsonObjectReviews);
        reviewAdapter.setReviews(reviews);
        trailerAdapter.setTrailers(trailers);
    }

    private void setFavourite() {
        favouriteMovie = viewModel.getFavouriteMovieById(id);
        if (favouriteMovie == null) {
            imageViewAddToFavorite.setImageResource(R.drawable.ic_unfavorite_grey_40dp);
        } else {
            imageViewAddToFavorite.setImageResource(R.drawable.ic_favorite_yellow_40dp);
        }
    }
}
