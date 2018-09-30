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
    ImageView ivWineImage;
    ImageView ivPlaceholderAdd;

    RatingBar wineRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cl = findViewById(R.id.content_new_wine_entry);
        txtWineName =  cl.findViewById(R.id.txtWineName);
        txtWineMaker =  cl.findViewById(R.id.txtWineMaker);
        txtWineType =  cl.findViewById(R.id.txtWineType);
        txtWineYear =  cl.findViewById(R.id.txtWineYear);
        txtWineLocation =  cl.findViewById(R.id.txtWineLocation);
        txtWineDescription =  cl.findViewById(R.id.txtWineDescription);
        wineRating =  cl.findViewById(R.id.ratingBar2);
        ivWineImage = cl.findViewById(R.id.iv_wine_picture);
        ivPlaceholderAdd = cl.findViewById(R.id.iv_placeholder_add);

        Intent intent = getIntent();
        if(intent != null) {
            String JSON  = intent.getStringExtra("details");
            wr = new Gson().fromJson(JSON, WineReview.class);
            Log.d("***DEBUG***", "Inside new barcodeWine entry " + wr);
            loadWineReview(wr);
        }
    }

    public void loadWineReview(WineReview wr) {
        Log.d("***Debug***", "inside loadWineReview");

        try {
            //populate form from existing data
            txtWineName.setText(wr.name);
            txtWineMaker.setText(wr.maker);
            txtWineType.setText(wr.type);
            txtWineYear.setText(wr.year);
            txtWineLocation.setText(wr.location);
            txtWineDescription.setText(wr.description);
            wineRating.setRating(wr.rating);

            if(wr.imageBitmap != null) {
                ivWineImage.setImageBitmap(wr.imageBitmap);
            }
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
