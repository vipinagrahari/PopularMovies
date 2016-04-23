package com.vipin.www.popularmovies;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.vipin.www.popularmovies.data.model.Movie;

public class MovieDetailActivity extends AppCompatActivity {



    Movie movie;

    CollapsingToolbarLayout ctl;


    int primaryDark ;
    int primary ;
    FloatingActionButton fab;

    String firstTrailer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        primaryDark=ContextCompat.getColor(MovieDetailActivity.this, R.color.colorPrimaryDark);
        primary= ContextCompat.getColor(MovieDetailActivity.this, R.color.colorPrimary);

        movie = getIntent().getParcelableExtra("movie");

        setContentView(R.layout.activity_movie_detail);


        ctl = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        ctl.setContentScrimColor(primary);
        ctl.setStatusBarScrimColor(primaryDark);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        MovieDetailFragment fragment = MovieDetailFragment.newInstance(movie);


        getSupportFragmentManager().beginTransaction()
                .replace(R.id.movie_detail_container, fragment)
                .commit();
        ctl.setTitle(movie.getOriginalTitle());




    }




}
