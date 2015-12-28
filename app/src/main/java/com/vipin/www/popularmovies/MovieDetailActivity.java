package com.vipin.www.popularmovies;

import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.vipin.www.popularmovies.model.Movie;
import com.vipin.www.popularmovies.tmdbapi.ApiService;

public class MovieDetailActivity extends AppCompatActivity {

    ImageView backDropImage;


    Movie movie;
    TextView tvMovieOverView;
    TextView tvReleaseDate;
    TextView tvPopularity;
    TextView tvUserRating;
    CollapsingToolbarLayout ctl;
    Target target;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        movie = getIntent().getParcelableExtra("movie");

        ctl = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        backDropImage = (ImageView) ctl.findViewById(R.id.backdrop_image);

        loadBackdrop(movie.getBackdropPath());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        tvMovieOverView = (TextView) findViewById(R.id.tv_overview);
        tvReleaseDate = (TextView) findViewById(R.id.tv_release_date);
        tvPopularity = (TextView) findViewById(R.id.tv_popularity);
        tvUserRating = (TextView) findViewById(R.id.tv_rating);


        ctl.setTitle(movie.getOriginalTitle());
        tvMovieOverView.setText(movie.getOverview() == null ? "Not Available" : movie.getOverview());
        tvReleaseDate.setText(movie.getReleaseDate() == null ? "Not Available" : movie.getReleaseDate());
        tvPopularity.setText(String.format("%.2f", movie.getPopularity()));
        tvUserRating.setText(String.format("%.2f", movie.getVoteAverage()));

    }


    public void loadBackdrop(String backdropPath) {
        final int primaryDark = ContextCompat.getColor(MovieDetailActivity.this, R.color.colorPrimaryDark);
        final int primary = ContextCompat.getColor(MovieDetailActivity.this, R.color.colorPrimary);

        if (backdropPath == null) {

            ctl.setContentScrimColor(primary);
            ctl.setStatusBarScrimColor(primaryDark);
            backDropImage.setImageResource(R.drawable.placeholder);

            backDropImage.setColorFilter(primary, PorterDuff.Mode.DARKEN);

        } else {

            target = new Target() {
                @Override
                public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {

                    Palette palette = Palette.from(bitmap).generate();

                    ctl.setContentScrimColor(palette.getVibrantColor(primary));
                    ctl.setStatusBarScrimColor(palette.getDarkVibrantColor(primaryDark));
                    backDropImage.setImageBitmap(bitmap);

                    Toast.makeText(MovieDetailActivity.this, " Loaded ", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                    Toast.makeText(MovieDetailActivity.this, "Failed to load backdrop image", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };

            Picasso.with(MovieDetailActivity.this)
                    .load(ApiService.BASE_IMAGE_URL + ApiService.RES_BACKDROP + backdropPath)
                    .into(target);
        }

    }


}
