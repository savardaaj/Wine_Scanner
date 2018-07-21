package alex.winescanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class LibraryActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Gson gson;
    private ArrayList<WineReview> wineReviewArrayList;
    private static int NEW_WR = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        wineReviewArrayList = new ArrayList<>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        loadWineReviews();
        //testloadWineReviews();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("***Debug***", "inside onActivityResult");

        if (resultCode == RESULT_OK && requestCode == NEW_WR) {
            String wineReviewJSON = (String) data.getStringExtra("wineReviewJSON");
            if (wineReviewJSON != null) {
                WineReview newWineReview = new Gson().fromJson(wineReviewJSON, WineReview.class);
                wineReviewArrayList.add(newWineReview);
            }
        }
        loadWineReviews();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.library, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent intent = new Intent(this, NewWineEntryActivity.class);
            startActivityForResult(intent, NEW_WR);

        } else if (id == R.id.nav_library) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void btnNewWineClicked() {

    }

    public void loadWineReviews() {
        Log.d("***Debug***", "inside loadWineReviews");

         for(WineReview wr : wineReviewArrayList) {
             //setup layout stuff
             LayoutInflater inflater = LayoutInflater.from(this);
             ScrollView scrollView = findViewById(R.id.content_library);
             LinearLayout scrollContainer = findViewById(R.id.content_library_container);
             View wineCard= inflater.inflate(R.layout.wine_card_component, null, false);

             //initialize the layout fields
             TextView wineName = (TextView) wineCard.findViewById(R.id.tvWineName);
             ImageView winePicture = (ImageView) wineCard.findViewById(R.id.ivWine_Picture);
             RatingBar wineRating = (RatingBar) wineCard.findViewById(R.id.ratingBar);
             TextView wineDesc = (TextView) wineCard.findViewById(R.id.tvWine_Desc);

             //set values for layout fields
             String winetitle = wr.getWineName() + " - " + wr.getWineMaker();
             //set values of imported components
             wineName.setText(winetitle);
             //winePicture.setImageDrawable();
             wineRating.setRating(wr.getRating());
             wineDesc.setText(wr.getWineDescription());

             scrollContainer.addView(wineCard);
         }

    }


    public void testloadWineReviews() {
        Log.d("***Debug***", "inside testloadWineReviews");

        //display a test wine review, no db
        WineReview wr = new WineReview();
        wr = wr.createTestWineReview();

        LayoutInflater inflater = LayoutInflater.from(this);
        ScrollView scrollView = findViewById(R.id.content_library);
        View wineCard = inflater.inflate(R.layout.wine_card_component, null, false);

        TextView wineName = (TextView) wineCard.findViewById(R.id.tvWineName);
        ImageView winePicture = (ImageView) wineCard.findViewById(R.id.ivWine_Picture);
        RatingBar wineRating = (RatingBar) wineCard.findViewById(R.id.ratingBar);
        TextView wineDesc = (TextView) wineCard.findViewById(R.id.tvWine_Desc);

        String winetitle = wr.getWineName() + " - " + wr.getWineMaker();
        //set values of imported components
        wineName.setText(winetitle);
        //winePicture.setImageDrawable();
        wineRating.setRating(wr.getRating());
        wineDesc.setText(wr.getWineDescription());

        scrollView.addView(wineCard);
    }

    public void onWineCardRemoved() {
        //delete wine card from arraylist and from layout
        //show toast to undo action
    }
}
