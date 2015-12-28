package com.vipin.www.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.vipin.www.popularmovies.model.Discover;
import com.vipin.www.popularmovies.model.Movie;
import com.vipin.www.popularmovies.tmdbapi.ApiService;
import com.vipin.www.popularmovies.tmdbapi.ServiceGenerator;

import java.util.ArrayList;
import java.util.List;

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

        if (new Util(this).checkInternet()) {

            apiService = ServiceGenerator.createService(ApiService.class);
            gvMovie = (GridView) findViewById(R.id.gv_movie);
            List<Movie> movies = new ArrayList<Movie>();
            discoverMovies();

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


        } else {

            (findViewById(R.id.layout_no_internet)).setVisibility(View.VISIBLE);

        }
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
        if (new Util(this).checkInternet()) {

            int id = item.getItemId();

            switch (id) {
                case R.id.menuSortPopular:
                    sortBy = ApiService.POPULARITY_DESC;
                    break;
                case R.id.menuSortRating:
                    sortBy = ApiService.RATING_DESC;
                    break;
                default:
                    return false;

            }

            if (adapter != null) adapter.removeAll();
            pageCount = 0;
            discoverMovies();


        } else {
            Toast.makeText(MainActivity.this, "No Connection", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);


    }


    public void discoverMovies() {


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


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);
        intent.putExtra("movie", adapter.getItem(position));
        startActivity(intent);
    }


}
