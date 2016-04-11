package com.vipin.www.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;


import com.facebook.stetho.Stetho;
import com.vipin.www.popularmovies.data.MovieContract;
import com.vipin.www.popularmovies.data.model.Discover;
import com.vipin.www.popularmovies.data.model.Movie;
import com.vipin.www.popularmovies.tmdbapi.ApiService;
import com.vipin.www.popularmovies.tmdbapi.ServiceGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    String sortBy = ApiService.POPULARITY_DESC; //Show popular movies by default
    ApiService apiService;
    GridView gvMovie;
    ImageAdapter adapter;
    int pageCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Stetho.initializeWithDefaults(this);

        apiService = ServiceGenerator.createService(ApiService.class);
        gvMovie = (GridView) findViewById(R.id.gv_movie);
        List<Movie> movies = new ArrayList<Movie>();

        if(savedInstanceState!=null){
            movies=savedInstanceState.getParcelableArrayList("movies");
        }
        else{
            discoverMovies();
        }


        adapter = new ImageAdapter(MainActivity.this, movies);
        gvMovie.setAdapter(adapter);
        gvMovie.setOnItemClickListener(this);
        gvMovie.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (firstVisibleItem + visibleItemCount + 10 > totalItemCount) {
                    discoverMovies();
                }
            }
        });

/*
        else {

         (findViewById(R.id.layout_no_internet)).setVisibility(View.VISIBLE);

        }
        */
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        {

            int id = item.getItemId();

            switch (id) {
                case R.id.menuSortPopular:
                    sortBy = ApiService.POPULARITY_DESC;
                    break;
                case R.id.menuSortRating:
                    sortBy = ApiService.RATING_DESC;
                    break;

                case R.id.menuSortFavourite:
                    getFavorites();
                    return true;
                default:
                    return false;

            }

            if (adapter != null) adapter.removeAll();
            pageCount = 0;
            discoverMovies();


        }
        return super.onOptionsItemSelected(item);


    }


    public void getFavorites() {

        Cursor c = getBaseContext().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, null, null, null);


        adapter = new ImageAdapter(MainActivity.this, cursorToMovies(c));
        gvMovie.setAdapter(adapter);
        gvMovie.setOnItemClickListener(this);
        gvMovie.setOnScrollListener(null);

        c.close();

    }


    public void discoverMovies() {

        if(new Util(this).checkInternet()) {


            Call<Discover> getMovie = apiService.discoverMovies(getResources().getString(R.string.api_key), ++pageCount, sortBy);

            getMovie.enqueue(new Callback<Discover>() {
                @Override
                public void onResponse(Response<Discover> response, Retrofit retrofit) {

                    if (response.body() != null) {
                        List<Movie> movies = response.body().getResults();
                        adapter.addMovies(movies);

                    }

                }

                @Override
                public void onFailure(Throwable t) {

                }


            });
        }

        else {
            Toast.makeText(MainActivity.this, "No Connection", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);
        intent.putExtra("movie", adapter.getItem(position));
        startActivity(intent);
    }

    public List<Movie> cursorToMovies(Cursor c) {

        List<Movie> movies = new ArrayList<Movie>();
        if (c.moveToFirst()) {
            do {
                Movie movie = new Movie();
                System.out.println(c.getString(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)));

                movie.setTitle(c.getString(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)));
                movie.setId(c.getInt(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID)));
                movie.setBackdropPath(c.getString(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH)));
                movie.setPosterPath(c.getString(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH)));
                movie.setReleaseDate(c.getString(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE)));
                movie.setOverview(c.getString(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW)));
                movie.setPopularity(c.getDouble(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_POPULARITY)));
                movie.setVoteAverage(c.getDouble(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE)));
                movie.setOriginalTitle(c.getString(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE)));
                movies.add(movie);

            } while (c.moveToNext());
        }
        return movies;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList("movies", (ArrayList<? extends Parcelable>)adapter.movies);

    }


}
