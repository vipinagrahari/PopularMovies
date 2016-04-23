package com.vipin.www.popularmovies;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.ShareActionProvider;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.vipin.www.popularmovies.data.MovieContract;
import com.vipin.www.popularmovies.data.model.Movie;
import com.vipin.www.popularmovies.tmdbapi.ApiService;
import com.vipin.www.popularmovies.tmdbapi.ServiceGenerator;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieDetailFragment extends Fragment {

    Movie movie;
    TextView tvMovieOverView;
    TextView tvReleaseDate;
    TextView tvPopularity;
    TextView tvUserRating;
    TextView tvReviewText;
    TextView tvReviewAuthor;

    LinearLayout llVideo;
    ImageView backDropImage;


    boolean reviewTextExpanded;
    boolean isFavorite;

    FloatingActionButton fab;
    ShareActionProvider provider;
    String firstTrailer;

    Target target;
    ApiService apiService;

    int primaryDark;
    int primary;
    CollapsingToolbarLayout ctl;
    private boolean masterDetailLayout;

    public MovieDetailFragment() {
        // Required empty public constructor
    }

    public static MovieDetailFragment newInstance(Movie movie) {

        Bundle args = new Bundle();
        MovieDetailFragment fragment = new MovieDetailFragment();
        args.putParcelable("movie", movie);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        movie = getArguments().getParcelable("movie");
        isFavorite = isFavorite();
        apiService = ServiceGenerator.createService(ApiService.class);

        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        tvMovieOverView = (TextView) view.findViewById(R.id.tv_overview);
        tvReleaseDate = (TextView) view.findViewById(R.id.tv_release_date);
        tvPopularity = (TextView) view.findViewById(R.id.tv_popularity);
        tvUserRating = (TextView) view.findViewById(R.id.tv_rating);
        tvReviewAuthor = (TextView) view.findViewById(R.id.tv_review_author);
        tvReviewText = (TextView) view.findViewById(R.id.tv_review);
        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab_favorite);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        tvMovieOverView.setText(movie.getOverview() == null ? "Not Available" : movie.getOverview());
        tvReleaseDate.setText(movie.getReleaseDate() == null ? "Not Available" : movie.getReleaseDate());
        tvPopularity.setText(String.format("%.2f", movie.getPopularity()));
        tvUserRating.setText(String.format("%.2f", movie.getVoteAverage()));


        /*
        Load backdrop Image in either CollapsingToolbarLayout or Fragment Card view depending on layout mode
         */
        if (getActivity().getClass().getSimpleName().compareTo(MainActivity.class.getSimpleName()) == 0) {
            masterDetailLayout = true;
            backDropImage = (ImageView) view.findViewById(R.id.backdrop_image);
        } else {
            ctl = ((MovieDetailActivity) getActivity()).ctl;
            backDropImage = (ImageView) ctl.findViewById(R.id.backdrop_image);
        }

        backDropImage.setImageResource(R.drawable.placeholder);
        backDropImage.setColorFilter(primary, PorterDuff.Mode.DARKEN);
        loadBackdrop(movie.getBackdropPath());


        /*******************************************************************************************/


        /*
        Change the drawable of FAB depending on whether a movie is favorite or not
         */
        if (isFavorite) {
            fab.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_favorite_white_24dp));
        } else {
            fab.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_favorite_border_white_24dp));
        }

        /*
        Add a movie as favourite
         */

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFavorite(v);
            }
        });

        /*******************************************************************************************/




         /*
         Expand or fold the review text on click (if larger than 10 lines)

         */
        tvReviewText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleEllipsize(v);
            }
        });


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getReviews();
        getTrailers();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_scrolling, menu);
        MenuItem item = menu.findItem(R.id.menu_share);
        // Fetch and store ShareActionProvider
        provider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);


    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        if (firstTrailer != null) {
            provider.setShareIntent(getShareIntent());
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case R.id.menu_share:
                break;
            default:
                return false;


        }
        return super.onOptionsItemSelected(item);
    }

    private Intent getShareIntent() {

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, "https://www.youtube.com/watch?v=" + firstTrailer);
        return sendIntent;

    }


    public void getReviews() {

        Call<JsonObject> getReviewsCall = apiService.getReview(movie.getId(), getResources().getString(R.string.api_key));
        getReviewsCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Response<JsonObject> response, Retrofit retrofit) {

                if (response.body() != null) {

                    JsonArray reviews = (JsonArray) response.body().get("results");

                    if (reviews.size() > 0) {
                        tvReviewText.setText(((JsonObject) reviews.get(0)).get("content").getAsString());
                        tvReviewAuthor.setText(((JsonObject) reviews.get(0)).get("author").getAsString());

                    } else {

                        tvReviewAuthor.setText(R.string.message_no_reviews);
                        tvReviewText.setText("");
                    }

                }

            }

            @Override
            public void onFailure(Throwable t) {

            }

        });


    }

    public void getTrailers() {
        llVideo = (LinearLayout) getView().findViewById(R.id.ll_trailers);

        Call<JsonObject> getTrailersCall = apiService.getTrailers(movie.getId(), getResources().getString(R.string.api_key));

        getTrailersCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Response<JsonObject> response, Retrofit retrofit) {
                if (response.body() != null) {

                    JsonArray videos = (JsonArray) response.body().get("results");
                    if (videos.size() > 0) {
                        firstTrailer = ((JsonObject) videos.get(0)).get("key").getAsString();
                        if (isAdded()) getActivity().invalidateOptionsMenu();


                        for (int i = 0; i < videos.size(); i++) {
                            JsonObject object = (JsonObject) videos.get(i);
                            String name = object.get("name").getAsString();
                            final String id = object.get("key").getAsString();
                            if (name.toLowerCase().contains("trailer")) {
                                TextView tv = new TextView(llVideo.getContext());
                                tv.setClickable(true);

                                tv.setText(name);
                                tv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        playVideo(id);
                                    }
                                });


                                tv.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_media_play, 0, 0, 0);
                                tv.setCompoundDrawablePadding(6);


                                tv.setPadding(24, 6, 0, 6);

                                tv.setGravity(Gravity.CENTER_VERTICAL);

                                llVideo.addView(tv);


                            }

                        }
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    public void toggleEllipsize(View v) {

        if (reviewTextExpanded) tvReviewText.setMaxLines(10); //Fold
        else tvReviewText.setMaxLines(Integer.MAX_VALUE); // Expand
        reviewTextExpanded = !reviewTextExpanded;

    }

    private void playVideo(String videoId) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        StringBuilder videoUrl = new StringBuilder("https://www.youtube.com/watch?v=");
        videoUrl.append(videoId);
        intent.setData(Uri.parse(videoUrl.toString()));
        startActivity(intent);
    }


    public void addFavorite(View view) {
        if (!isFavorite) {

            ContentValues movieValues = new ContentValues();
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getId());
            movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
            movieValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, movie.getOriginalTitle());
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW, movie.getOverview());
            movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
            movieValues.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, movie.getBackdropPath());
            movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
            movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, movie.getPopularity());
            movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
            getActivity().getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, movieValues);

            ((FloatingActionButton) view).setImageDrawable(ContextCompat.getDrawable(getActivity(),
                    R.drawable.ic_favorite_white_24dp));
            Toast.makeText(getActivity(), "Marked Favorite", Toast.LENGTH_SHORT).show();
            isFavorite = true;
        } else {
            Toast.makeText(getActivity(), "Already Marked Favorite", Toast.LENGTH_SHORT).show();
        }

    }


    private boolean isFavorite() {
        System.out.println("Movie " + movie);

        Cursor c = getActivity().getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI, null, MovieContract.MovieEntry.COLUMN_MOVIE_ID + " =? ",
                new String[]{movie.getId() + ""}, null);

        boolean fav = c.moveToFirst();
        c.close();
        return fav;


    }

    public void loadBackdrop(String backdropPath) {


        if (backdropPath == null) {

            //Do nothing

        } else {

            target = new Target() {
                @Override
                public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {

                    Palette palette = Palette.from(bitmap).generate();

                    if (!masterDetailLayout) {
                        ctl.setContentScrimColor(palette.getVibrantColor(primary));
                        ctl.setStatusBarScrimColor(palette.getDarkVibrantColor(primaryDark));
                    }
                    backDropImage.setImageBitmap(bitmap);
                    backDropImage.clearColorFilter();

                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                    // Toast.makeText(getActivity(), "Failed to load backdrop image", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };

            Picasso.with(getActivity())
                    .load(ApiService.BASE_IMAGE_URL + ApiService.RES_BACKDROP + backdropPath)
                    .into(target);
        }

    }





}
