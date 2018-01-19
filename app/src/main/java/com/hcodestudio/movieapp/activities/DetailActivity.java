package com.hcodestudio.movieapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.hcodestudio.movieapp.BuildConfig;
import com.hcodestudio.movieapp.R;
import com.hcodestudio.movieapp.adapter.TrailerAdapter;
import com.hcodestudio.movieapp.api.Client;
import com.hcodestudio.movieapp.api.Service;
import com.hcodestudio.movieapp.data.FavouriteDBHelper;
import com.hcodestudio.movieapp.model.Movie;
import com.hcodestudio.movieapp.model.Trailer;
import com.hcodestudio.movieapp.model.TrailerResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hcodestudio.movieapp.adapter.MovieAdapter.IMAGE_URL_BASE_PATH;

/**
 * Created by hassan on 11/23/2017.
 */

public class DetailActivity extends AppCompatActivity {
    TextView title, plotSynopsis, releaseDate, userRating;
    ImageView imageView;
    ActionBar actionBar;
    private RecyclerView recyclerView;
    private TrailerAdapter adapter;
    private List<Trailer> trailerList;
    private FavouriteDBHelper favouriteDBHelper;
    private Movie favorite;
    private final AppCompatActivity activity = DetailActivity.this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

       // initCollapsingToolBar();

        imageView = (ImageView) findViewById(R.id.thumbnail_img_header);
        title = (TextView) findViewById(R.id.title);
        plotSynopsis = (TextView) findViewById(R.id.plotSynopsis);
        releaseDate = (TextView) findViewById(R.id.release_date);
        userRating = (TextView) findViewById(R.id.user_rating);

        MaterialFavoriteButton fab = (MaterialFavoriteButton) findViewById(R.id.fab);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        fab.setOnFavoriteChangeListener(new MaterialFavoriteButton.OnFavoriteChangeListener() {
            @Override
            public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                if(favorite){
                    SharedPreferences.Editor editor = getSharedPreferences("com.hcodestudio.movieapp.activities.DetailActivity",
                            MODE_PRIVATE).edit();
                    editor.putBoolean("Favorite Added", true);
                    saveFavorite();
                    Snackbar.make(buttonView, "Added to favourite", Snackbar.LENGTH_LONG).show();
                }else{
                    SharedPreferences.Editor editor = getSharedPreferences("com.hcodestudio.movieapp.activities.DetailActivity",
                            MODE_PRIVATE).edit();
                    deleteFavorite();
                    editor.putBoolean("Favorite Removed", true);
                    Snackbar.make(buttonView, "Removed from favourite", Snackbar.LENGTH_LONG).show();
                }


            }
        });


        Intent intentThatStartThisActivity = getIntent();

        if(intentThatStartThisActivity.hasExtra("original_title")){
            String thumbnail = getIntent().getExtras().getString("poster_path");
            String movieTitle = getIntent().getExtras().getString("original_title");
            String rating = getIntent().getExtras().getString("vote_average");
            String synopsis = getIntent().getExtras().getString("overview");
            String dateRelease = getIntent().getExtras().getString("release_date");

            actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setTitle(movieTitle);
            }

            Glide.with(this)
                    .load(IMAGE_URL_BASE_PATH + thumbnail)
                    .placeholder(R.drawable.loading)
                    .into(imageView);

            title.setText(movieTitle);
            plotSynopsis.setText(synopsis);
            releaseDate.setText(dateRelease);
            userRating.setText(rating);

        }else {
            Toast.makeText(this, "No API data", Toast.LENGTH_SHORT).show();
        }

        initView();
    }

    private void deleteFavorite() {
        int movieid = getIntent().getExtras().getInt("id");
        favouriteDBHelper = new FavouriteDBHelper(activity);
        favouriteDBHelper.deleteFavorite(movieid);
    }

    private void saveFavorite() {
        favouriteDBHelper = new FavouriteDBHelper(activity);
        favorite = new Movie();
        int movieid = getIntent().getExtras().getInt("id");
        String rate = getIntent().getExtras().getString("vote_average");
        String poster = getIntent().getExtras().getString("poster_path");

        favorite.setId(movieid);
        favorite.setOriginalTitle(title.getText().toString().trim());
        favorite.setPosterPath(poster);
        favorite.setVoteAverage(Double.valueOf(rate));
        favorite. setOverview(plotSynopsis.getText().toString().trim());

        favouriteDBHelper.addFavorite(favorite);


    }

//  private void initCollapsingToolBar() {
//        final CollapsingToolbarLayout collapsingToolbarLayout =
//                (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
//        collapsingToolbarLayout.setTitle(" ");
//        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
//        appBarLayout.setExpanded(true);
//        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
//            boolean isShow = false;
//            int scrollRange = -1;
//            @Override
//            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
//                if(scrollRange == -1){
//                    scrollRange = appBarLayout.getTotalScrollRange();
//                }
//
//                if(scrollRange + verticalOffset == 0){
//                    collapsingToolbarLayout.setTitle(getString(R.string.title_activity_movie_detail));
//                    isShow = true;
//                }else if(isShow){
//                    collapsingToolbarLayout.setTitle(" ");
//                    isShow = false;
//                }
//            }
//        });
//
//    }

    private void initView(){
        trailerList = new ArrayList<>();
        adapter = new TrailerAdapter(this, trailerList);
        recyclerView = (RecyclerView) findViewById(R.id.trailer_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        loadJSON();
    }

    private void loadJSON() {
        final int movie_id = getIntent().getExtras().getInt("id");

        try {
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()) {
                Toast.makeText(this, "Please Get Api Key from MovieDB.org", Toast.LENGTH_SHORT).show();
                return;
            }

            Client client = new Client();
            Service apiService = client.getClient().create(Service.class);
            Call<TrailerResponse> call = apiService .getMovieTrailer(movie_id,BuildConfig.THE_MOVIE_DB_API_TOKEN);
            call.enqueue(new Callback<TrailerResponse>() {
                @Override
                public void onResponse(Call<TrailerResponse> call, Response<TrailerResponse> response) {
                    List<Trailer> trailers = response.body().getResults();
                    recyclerView.setAdapter(new TrailerAdapter (getApplicationContext(), trailers));
                    recyclerView.smoothScrollToPosition(0);
                }

                @Override
                public void onFailure(Call<TrailerResponse> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    Toast.makeText(DetailActivity.this , "Error Fetching trailer data" , Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            Log.d("Error", e.getMessage());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }
}
