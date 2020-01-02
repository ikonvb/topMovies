package com.konstantinbulygin.topmovies;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.konstantinbulygin.topmovies.adapters.MovieAdapter;
import com.konstantinbulygin.topmovies.database.MainViewModel;
import com.konstantinbulygin.topmovies.database.Movie;
import com.konstantinbulygin.topmovies.utils.JSONUtils;
import com.konstantinbulygin.topmovies.utils.NetworkUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewPosters;
    private MovieAdapter adapter;
    private Switch switchSort;
    private TextView textViewPopularity;
    private TextView textViewTopRated;

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

    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        textViewPopularity = findViewById(R.id.textViewPopularity);
        textViewTopRated = findViewById(R.id.textViewTopRated);

        switchSort = findViewById(R.id.switchSort);
        recyclerViewPosters = findViewById(R.id.recyclerViewPosters);
        recyclerViewPosters.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new MovieAdapter();
        recyclerViewPosters.setAdapter(adapter);
        switchSort.setChecked(true);

        switchSort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                setMethodOfSort(isChecked);
            }
        });

        switchSort.setChecked(false);
        adapter.setOnPosterClickListener(new MovieAdapter.OnPosterClickListener() {
            @Override
            public void onPosterClick(int position) {
               Movie movie = adapter.getMovies().get(position);
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("id", movie.getId());
                startActivity(intent);
            }
        });

        adapter.setOnReachEndListener(new MovieAdapter.OnReachEndListener() {
            @Override
            public void onReachEnd() {
                Toast.makeText(MainActivity.this, "sdasdasdasdasd", Toast.LENGTH_SHORT).show();
            }
        });

        LiveData<List<Movie>> moviesFromLiveData = mainViewModel.getMovies();
        moviesFromLiveData.observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> movies) {
                adapter.setMovies(movies);
            }
        });
    }

    public void onClickSetPopularity(View view) {

        setMethodOfSort(false);
        switchSort.setChecked(false);
    }

    public void onClickSetTopRated(View view) {

        setMethodOfSort(true);
        switchSort.setChecked(true);
    }

    private void setMethodOfSort(boolean isTopRated) {

        int methodOfSort;

        if (isTopRated) {
            methodOfSort = NetworkUtils.TOP_RATED;
            textViewTopRated.setTextColor(getResources().getColor(R.color.colorAccent));
            textViewPopularity.setTextColor(getResources().getColor(R.color.colorWhite));
        } else {
            methodOfSort = NetworkUtils.POPULARITY;
            textViewTopRated.setTextColor(getResources().getColor(R.color.colorWhite));
            textViewPopularity.setTextColor(getResources().getColor(R.color.colorAccent));
        }

        downloadData(methodOfSort, 1);

    }

    private void downloadData(int methodOfSOrt, int page) {
        JSONObject jsonObject = NetworkUtils.getJSONFromNetwork(methodOfSOrt, page);
        ArrayList<Movie> movies = JSONUtils.getMoviesFromJSON(jsonObject);

        if (movies != null && !movies.isEmpty()) {
            mainViewModel.deleteAllMovies();

            for (Movie movie : movies) {
                mainViewModel.insertMovie(movie);
            }
        }
    }

}
