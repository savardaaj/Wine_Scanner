package alex.winescanner;

import android.content.Intent;
import android.icu.util.RangeValueIterator;
import android.os.Bundle;
import android.renderscript.Element;
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
import android.view.ViewManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class LibraryActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Gson gson;
    private ArrayList<WineReview> wineReviewArrayList;
    private static int NEW_WR = 1;
    private static int EDIT_WR = 2;
    FirebaseFirestore fs;
    DataBaseHandler dbh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("***Debug***", "inside LibraryActivity: onCreate");
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);

        dbh = new DataBaseHandler();

        fs = FirebaseFirestore.getInstance();
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
        Log.d("***Debug***", "inside LibraryActivity: onActivityResult");

        if (resultCode == RESULT_OK) {
            String wineReviewJSON = (String) data.getStringExtra("wineReviewJSON");
            if (wineReviewJSON != null) {
                WineReview wineReview = new Gson().fromJson(wineReviewJSON, WineReview.class);
                if (requestCode == NEW_WR) {
                    wineReviewArrayList.add(wineReview);
                } else if (requestCode == EDIT_WR) {
                    for(WineReview wr : wineReviewArrayList) {
                        //update existing wine review
                        if(wr.getId() == wineReview.getId()) {
                            wineReviewArrayList.set(wineReviewArrayList.indexOf(wr), wineReview);
                        }
                    }
                }
            }
        }
        loadWineReviews();
    }

    @Override
    public void onBackPressed() {
        Log.d("***Debug***", "inside LibraryActivity: onBackPressed");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("***Debug***", "inside LibraryActivity: onCreateOptionsMenu");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.library, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("***Debug***", "inside LibraryActivity: onOptionsItemSelected");
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
        Log.d("***Debug***", "inside LibraryActivity: onNavigationItemSelected");
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

    public void addWineReview(WineReview wr) {
        Log.d("***Debug***", "inside LibraryActivity: addWineReview");
        //redraw
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout scrollContainer = findViewById(R.id.content_library_container);

        View wineCard = inflater.inflate(R.layout.wine_card_component, null, false);

        wineCard.setTag(wr);

        TextView remove = (TextView) wineCard.findViewById(R.id.tvRemove);
        remove.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onWineCardRemove(v);
            }
        });

        //initialize the layout fields
        TextView wineName = (TextView) wineCard.findViewById(R.id.tvWineName);
        RatingBar wineRating = (RatingBar) wineCard.findViewById(R.id.ratingBar);
        TextView wineDesc = (TextView) wineCard.findViewById(R.id.tvWine_Desc);

        //TODO: Implement adding a picture
        ImageView winePicture = (ImageView) wineCard.findViewById(R.id.ivWine_Picture);

        //TODO: Implement rating system
        TextView wineRatingCount = (TextView) wineCard.findViewById(R.id.tvRatingsCount);
        TextView winePts = (TextView) wineCard.findViewById(R.id.tvWinePoints);
        TextView wineRatingSource = (TextView) wineCard.findViewById(R.id.tvWine_Source);

        //set values for winecard layout fields
        String winetitle = wr.getWineName() + " - " + wr.getWineMaker();
        wineName.setText(winetitle);
        wineRating.setRating(wr.getRating());
        wineDesc.setText(wr.getWineDescription());

        //null out unused fields atm
        wineRatingCount.setText("");
        winePts.setText("");
        wineRatingSource.setText("");

        //TODO: implement picture system
        //winePicture.setImageDrawable();

        //add wineCard to the scroll view
        scrollContainer.addView(wineCard);

        //TODO:  Firebase: complete firebase database implementation
        //dbh.addWineReview(wr, fs);
        wineReviewArrayList.add(wr);
    }

    public void loadWineReviews() {
        Log.d("***Debug***", "inside loadWineReviews");

        //redraw view
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout scrollContainer = findViewById(R.id.content_library_container);
        scrollContainer.removeAllViews();

        //TODO: Firebase: implement firebase database to load user's data
        //wineReviewArrayList = dbh.getWineReviews(fs);
        wineReviewArrayList = sortByRatingDescending(wineReviewArrayList);

        for(WineReview wr : wineReviewArrayList) {
            //setup layout stuff
            Log.d("***Debug***", "loading wr: " + wr);

            View wineCard = inflater.inflate(R.layout.wine_card_component, null, false);
            wineCard.setPadding(0,0,0, 10);
            wineCard.setTag(wr);

            TextView remove = (TextView) wineCard.findViewById(R.id.tvRemove);
            remove.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    onWineCardRemove(v);
                }
            });

            //initialize the layout fields
            TextView wineName = (TextView) wineCard.findViewById(R.id.tvWineName);
            ImageView winePicture = (ImageView) wineCard.findViewById(R.id.ivWine_Picture);
            RatingBar wineRating = (RatingBar) wineCard.findViewById(R.id.ratingBar);
            TextView wineDesc = (TextView) wineCard.findViewById(R.id.tvWine_Desc);

            //TODO: Implement rating system
            TextView wineRatingCount = (TextView) wineCard.findViewById(R.id.tvRatingsCount);
            TextView winePts = (TextView) wineCard.findViewById(R.id.tvWinePoints);
            TextView wineRatingSource = (TextView) wineCard.findViewById(R.id.tvWine_Source);

            //set values for layout fields
            String wineTitle = wr.getWineName() + " - " + wr.getWineMaker();

            //set values of imported components
            wineName.setText(wineTitle);
            wineRating.setRating(wr.getRating());
            wineDesc.setText(wr.getWineDescription());

            //null out unused fields atm
            wineRatingCount.setText("");
            winePts.setText("");
            wineRatingSource.setText("");

            //TODO: picture: implement picture system
            //winePicture.setImageDrawable();

            //add wine card to scroll view
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

    public void onWineCardRemove(View v) {
        Log.d("***Debug***", "inside LibraryActivity: onWineCardRemove");

        int indexToRemove = 999;

        try {
            ViewGroup wineCard = (ViewGroup) v.getParent();
            int wineCardId = wineCard.getId();
            for (WineReview wr : wineReviewArrayList) {
                if (wr == wineCard.getTag()) {
                    indexToRemove = wineReviewArrayList.indexOf(wr);
                    ((ViewManager) wineCard.getParent()).removeView(wineCard);
                    //dbh.removeWineReview(wr, fs);
                    String message = "Removed " + wr.getWineName();

                    showSnackBar(findViewById(R.id.content_library_container), message, 7000, wr);
                }
            }
        } catch(Exception e) {
            Log.d("***ERROR***", "onWineCardRemove: " + e.getMessage());
            Log.d("***ERROR***", "onWineCardRemove: " + e);
            Toast.makeText(this, "Error Occurred", Toast.LENGTH_SHORT).show();

        }
        if(indexToRemove != 999) {
            wineReviewArrayList.remove(indexToRemove);

        }
    }

    public void showSnackBar(View view, String message, int duration, WineReview wr) {
        Log.d("***Debug***", "inside LibraryActivity: showSnackBar");
        final Snackbar undoSnackBar = Snackbar.make(view, message, duration);
        final WineReview wineReview = wr;
        undoSnackBar.setAction("Undo", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restoreWineReview(wineReview);
                undoSnackBar.dismiss();
            }
        });
        undoSnackBar.show();
    }

    public void restoreWineReview(WineReview wr) {
        Log.d("***Debug***", "inside LibraryActivity: restoreWineReview");
        wineReviewArrayList.add(wr);
        addWineReview(wr);
        dbh.addWineReview(wr, fs);
    }

    public ArrayList<WineReview> sortByRatingDescending(ArrayList<WineReview> wineReviewArrayList) {
        Log.d("***Debug***", "inside LibraryActivity: sortByRatingDescending");

        Object[] wrArray = wineReviewArrayList.toArray();
        ArrayList<WineReview> sortedArrayList = new ArrayList<WineReview>();

        WineReview temp = new WineReview();

        //sort descending based on highest rating
        for(int j = 0; j < wrArray.length; j++) {
            for(int i = 0; i < wrArray.length; i++) {
                if(wrArray.length > 1) {
                    WineReview curr = (WineReview) wrArray[i];

                    if((i + 1) != wrArray.length) {

                        WineReview next = (WineReview) wrArray[i + 1];
                        if(curr.getRating() < next.getRating()) {
                            temp = next;
                            wrArray[i + 1] = curr;
                            wrArray[i] = temp;
                        }
                    }
                }
            }
        }

        //convert to arraylist
        for(Object wr : wrArray) {
            sortedArrayList.add((WineReview) wr);
        }
        return sortedArrayList;
    }
    public void onWineCardEdit(View v) {
        Log.d("***Debug***", "inside LibraryActivity: onWineCardEdit");

        int indexToEdit = 999;

        try {
            ViewGroup wineCard = (ViewGroup) v.getParent();
            int wineCardId = wineCard.getId();
            for (WineReview wr : wineReviewArrayList) {
                if (wr == wineCard.getTag()) {
                    Gson gson = new Gson();
                    String wineReviewJSON = gson.toJson(wr);
                    indexToEdit = wineReviewArrayList.indexOf(wr);
                    Intent intent = new Intent(this, NewWineEntryActivity.class);
                    intent.putExtra("edit", wineReviewJSON);
                    startActivityForResult(intent, EDIT_WR);
                }
            }
        } catch(Exception e) {
            Log.d("***ERROR***", "onWineCardRemove: " + e.getMessage());
            Log.d("***ERROR***", "onWineCardRemove: " + e);
            Toast.makeText(this, "Error Occurred", Toast.LENGTH_SHORT).show();

        }
        if(indexToEdit != 999) {
            wineReviewArrayList.remove(indexToEdit);
        }

    }
}
