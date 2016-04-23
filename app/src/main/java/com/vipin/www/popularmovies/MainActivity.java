package com.vipin.www.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.vipin.www.popularmovies.data.model.Movie;
import com.vipin.www.popularmovies.tmdbapi.ApiService;
import com.vipin.www.popularmovies.tmdbapi.ServiceGenerator;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements MovieListFragment.MovieSelectListener, ImageAdapter.InitDetailFragment {

    ApiService apiService;
    MovieListFragment mlf;
    List<Movie> movies = new ArrayList<>();
    private boolean masterDetailLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        apiService = ServiceGenerator.createService(ApiService.class);

        mlf = (MovieListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_movie_list);


        if (findViewById(R.id.movie_detail_container) != null) {

            masterDetailLayout = true;

        }

    }




    @Override
    public void onMovieSelected(Movie movie) {

        if (masterDetailLayout) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, MovieDetailFragment.newInstance(movie))
                    .commit();

        } else {
            Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);
            intent.putExtra("movie", movie);
            startActivity(intent);

        }

    }

    @Override
    public void onMovieLoaded(Movie movie) {


        if (masterDetailLayout) {

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, MovieDetailFragment.newInstance(movie))
                    .commit();

        }



    }
}
