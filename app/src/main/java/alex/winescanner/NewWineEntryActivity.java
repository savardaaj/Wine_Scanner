package alex.winescanner;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.gson.Gson;

public class NewWineEntryActivity extends AppCompatActivity {

    private ViewGroup mConstraintLayout;
    private int NEW_WR_IMAGE = 2;
    View rootView;
    LayoutInflater li;
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
        setContentView(R.layout.activity_new_wine_entry);

        cl = (ConstraintLayout)findViewById(R.id.new_wine_form);
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
            populateExistingReview();
        }
    }



    public void populateExistingReview() {
        Log.d("***DEBUG***", "Inside new saveWineReview");

        try {
            //populate form from existing data
            txtWineName.setText(wr.name);
            txtWineMaker.setText(wr.maker);
            txtWineType.setText(wr.type);
            txtWineYear.setText(wr.year);
            txtWineLocation.setText(wr.location);
            txtWineDescription.setText(wr.description);
            //wineReview.setWineImage(wineImage.getImage);
            wineRating.setRating(wr.rating);
        }
        catch(Exception e) {
            Log.d("**ERROR**",": " + e.getMessage());
            Toast.makeText(this, "An Error Occurred", Toast.LENGTH_LONG).show();
            //return to library

            Intent intent = new Intent(this, LibraryActivity.class);
            setResult(RESULT_CANCELED, intent);
        }

    }

    public void saveNewWineReview(View v) {
        Log.d("***DEBUG***", "Inside new saveWineReview");

        try {
            WineReview wineReview =  new WineReview();

            if(wr != null) {
                wineReview = wr;
            }

            //populate data from form
            wineReview.name = txtWineName.getText().toString();
            wineReview.maker = txtWineMaker.getText().toString();
            wineReview.type = txtWineType.getText().toString();
            wineReview.year = txtWineYear.getText().toString();
            wineReview.location = txtWineLocation.getText().toString();
            wineReview.description = txtWineDescription.getText().toString();
            //wineReview.setWineImage(wineImage.getImage);
            wineReview.rating = (wineRating.getRating());

            Gson gson = new Gson();
            String wineReviewJSON = gson.toJson(wineReview);

            //store new wine review in local storage? local and cloud?
            if(wr != null) {
                Toast.makeText(this, "Edited review for " + wineReview.name,
                        Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, "Created a review for " + wineReview.name,
                        Toast.LENGTH_LONG).show();
            }

            Intent intent = new Intent(this, LibraryActivity.class);
            intent.putExtra("wineReviewJSON", wineReviewJSON);
            setResult(RESULT_OK, intent);
        }
        catch(Exception e) {
            Log.d("**ERROR**",": " + e.getMessage());
            Log.d("**ERROR2**",": " + e.getStackTrace().toString());
            Toast.makeText(this, "An Error Occurred", Toast.LENGTH_LONG).show();
            //return to library

            Intent intent = new Intent(this, LibraryActivity.class);
            setResult(RESULT_CANCELED, intent);
        }

        super.finish();

    }

    public void onAddWineReviewImageClicked() {
        //open camera, user takes picture, return with image
        Intent intent = new Intent(this, AndroidCameraApi.class);
        startActivityForResult(intent, NEW_WR_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("***Debug***", "inside onActivityResult");

        if (resultCode == RESULT_OK && requestCode == NEW_WR_IMAGE) {
            ConstraintLayout cl = (ConstraintLayout) findViewById(R.id.new_wine_form);
            ImageView wineImage = (ImageView) cl.findViewById(R.id.imageView);
            String wineReviewImage = (String) data.getStringExtra("wineReviewImage");
            //wineImage.setImageDrawable(wineReviewImage);

        }
    }
}
