package com.konstantinbulygin.topmovies;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.konstantinbulygin.topmovies.utils.NetworkUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //create string of url
        String url = NetworkUtils.buildUrl(NetworkUtils.POPULARITY, 1).toString();
        Log.i("orororor", url);
    }
}
