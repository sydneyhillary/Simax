package com.simax.si_max;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.simax.si_max.Interface.OnFavoritesCallback;
import com.simax.si_max.model.FavMovie;
import com.simax.si_max.model.Movie;
import com.simax.si_max.room.FavModel;

import java.util.List;

import butterknife.BindView;

import static com.simax.si_max.DetailsFavoriteActivity.FAVS_ID;

public class FavoritesActivity extends AppCompatActivity {

    @BindView(R.id.recycler)
    RecyclerView recyclerView;

    private FavModel favModel;
    private FavAdapter adapter;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        //recyclerView.setAdapter(adapter);
        setupToolbar();
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        int numberOfColumns = 2;
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        recyclerView.setAdapter(adapter);


        favModel = ViewModelProviders.of(this).get(FavModel.class);

       /* favModel.getAllFavs().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies, OnFavoritesCallback callback) {

                adapter = new FavAdapter(movies, callback);

                //adapter.clearMovies();
                adapter.setFavorites(movies);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                //deleteBtn.setVisibility(View.VISIBLE);

            }});*/
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.clear_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear_data:
                favModel.deleteAllMovie();
                Toast.makeText(this, "Clearing favorites....", Toast.LENGTH_SHORT).show();
            default:
                return super.onOptionsItemSelected(item);
        }


    }
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.newAccent));
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle("Favorites");
            //getSupportActionBar().se("Favorites");
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}
