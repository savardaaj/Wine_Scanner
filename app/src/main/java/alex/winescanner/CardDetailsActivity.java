package alex.winescanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

public class CardDetailsActivity extends AppCompatActivity {

    ArrayList<WineReview> wineReviewArrayList;
    WineReview wr;

    ConstraintLayout cl;
    EditText txtWineName;
    EditText txtWineMaker;
    EditText txtWineType;
    EditText txtWineYear;
    EditText txtWineLocation;
    EditText txtWineDescription;

    RatingBar wineRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cl = (ConstraintLayout)findViewById(R.id.content_new_wine_entry);
        txtWineName = (EditText) cl.findViewById(R.id.txtWineName);
        txtWineMaker = (EditText) cl.findViewById(R.id.txtWineMaker);
        txtWineType = (EditText) cl.findViewById(R.id.txtWineType);
        txtWineYear = (EditText) cl.findViewById(R.id.txtWineYear);
        txtWineLocation = (EditText) cl.findViewById(R.id.txtWineLocation);
        txtWineDescription = (EditText) cl.findViewById(R.id.txtWineDescription);
        wineRating = (RatingBar) cl.findViewById(R.id.ratingBar2);

        Intent intent = getIntent();
        if(intent != null) {
            String JSON  = intent.getStringExtra("edit");
            wr = new Gson().fromJson(JSON, WineReview.class);
            Log.d("***DEBUG***", "Inside newwineentry " + wr);
            loadWineReview(wr);
        }
    }

    public void loadWineReview(WineReview wr) {
        Log.d("***Debug***", "inside loadWineReviews");

        try {
            //populate form from existing data
            txtWineName.setText(wr.name);
            txtWineMaker.setText(wr.maker);
            txtWineType.setText(wr.type);
            txtWineYear.setText(wr.year);
            txtWineLocation.setText(wr.location);
            txtWineDescription.setText(wr.description);
            //newWineReview.setWineImage(wineImage.getImage);
            wineRating.setRating(wr.rating);
        }
        catch(Exception e) {
            Log.d("**ERROR**",": " + e.getMessage());
            Toast.makeText(this, "An Error Occurred", Toast.LENGTH_LONG).show();
            //return to menu_library

            Intent intent = new Intent(this, LibraryActivity.class);
            setResult(RESULT_CANCELED, intent);
        }
    }

}
