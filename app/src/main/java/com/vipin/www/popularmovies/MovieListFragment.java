package com.vipin.www.popularmovies;


import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.vipin.www.popularmovies.data.MovieContract;
import com.vipin.www.popularmovies.data.model.Discover;
import com.vipin.www.popularmovies.data.model.Movie;
import com.vipin.www.popularmovies.tmdbapi.ApiService;
import com.vipin.www.popularmovies.tmdbapi.ServiceGenerator;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieListFragment extends Fragment implements AdapterView.OnItemClickListener {

    GridView gvMovie;
    ImageAdapter adapter;
    ApiService apiService;
    int pageCount;
    String sortBy = ApiService.POPULARITY; // by default show Popular movies
    ArrayList<Movie> movies = new ArrayList<>();
    String activityTitle;


    public MovieListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        apiService = ServiceGenerator.createService(ApiService.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_movie_list, container, false);


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gvMovie = (GridView) view.findViewById(R.id.gv_movie);
        if (savedInstanceState != null) {
            movies = savedInstanceState.getParcelableArrayList("movies");
            activityTitle = savedInstanceState.getString("title");
        } else {
            activityTitle = getString(R.string.menu_sort_popular);
            discoverMovies(); // fetch most popular movies
            setOnScroll();
        }

        adapter = new ImageAdapter(getContext(), movies);
        gvMovie.setAdapter(adapter);
        gvMovie.setOnItemClickListener(this);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(activityTitle);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        pageCount = 0;

        int id = item.getItemId();
        switch (id) {

            case R.id.menuSortPopular:
                sortBy = ApiService.POPULARITY;
                if (adapter != null) adapter.removeAll();
                discoverMovies();
                setOnScroll();
                activityTitle = getString(R.string.menu_sort_popular);
                actionBar.setTitle(activityTitle);
                return true;

            case R.id.menuSortRating:
                sortBy = ApiService.RATING;
                if (adapter != null) adapter.removeAll();
                discoverMovies();
                setOnScroll();
                activityTitle = getString(R.string.menu_sort_rating);
                actionBar.setTitle(activityTitle);
                return true;

            case R.id.menuSortFavourite:
                getFavorites();
                removeOnScroll();
                activityTitle = getString(R.string.menu_sort_favourite);
                actionBar.setTitle(activityTitle);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }


    }


    /*
    setOnScroll() method helps in endless scrolling effect.
     */
    private void setOnScroll() {
        gvMovie.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (firstVisibleItem + visibleItemCount >= totalItemCount) {
                    discoverMovies();
                }
            }
        });
    }

    private void removeOnScroll() {
        gvMovie.setOnScrollListener(null);

    }


    /*
    fetch movies from TMDB API
     */
    public void discoverMovies() {
        final List<Movie> movies = new ArrayList<>();

        if (new Util(getContext()).checkInternet()) {


            Call<Discover> getMovie = apiService.discoverMovies(sortBy, getResources().getString(R.string.api_key), ++pageCount);


            getMovie.enqueue(new Callback<Discover>() {
                @Override
                public void onResponse(Response<Discover> response, Retrofit retrofit) {

                    if (response.body() != null) {
                        movies.addAll(response.body().getResults());
                        adapter.addMovies(movies);
                    }
                }

                @Override
                public void onFailure(Throwable t) {

                }

            });
        } else {
            Toast.makeText(getActivity(), "No Connection", Toast.LENGTH_SHORT).show();
        }


    }


    public void getFavorites() {


        Cursor c = getContext().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, null, null, null);
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

        c.close();
        if (adapter != null) {
            adapter.removeAll();
            adapter.addMovies(movies);

        }


    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("movies", (ArrayList<? extends Parcelable>) adapter.movies);
        outState.putString("title", activityTitle);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ((MainActivity) getActivity()).onMovieSelected(adapter.movies.get(position));
    }


    /*
    Listener to pass event to Activity when a movie is selected to
    update detail fragment or start DetailActivity depending upon layout mode
     */
    public interface MovieSelectListener {
        void onMovieSelected(Movie movie);
    }


}
