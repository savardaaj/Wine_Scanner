package alex.winescanner;

import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

public class NewWineEntryActivity extends AppCompatActivity {

    private ViewGroup mConstraintLayout;
    View rootView;
    LayoutInflater li;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_wine_entry);


    }

    public void saveNewWineReview(View v) {
        Log.d("*****", "Inside nwe saveWineReview");


        try {
            WineReview wineReview = new WineReview();

            ConstraintLayout cl = (ConstraintLayout)findViewById(R.id.new_wine_form);
            EditText txtWineName = (EditText) cl.findViewById(R.id.txtWineName);
            EditText txtWineMaker = (EditText) cl.findViewById(R.id.txtWineMaker);
            EditText txtWineType = (EditText) cl.findViewById(R.id.txtWineType);
            EditText txtWineYear = (EditText) cl.findViewById(R.id.txtWineYear);
            EditText txtWineLocation = (EditText) cl.findViewById(R.id.txtWineLocation);
            EditText txtWineDescription = (EditText) cl.findViewById(R.id.txtWineDescription);
            ImageView wineImage = (ImageView) cl.findViewById(R.id.imageView);
            RatingBar wineRating = (RatingBar) cl.findViewById(R.id.ratingBar2);

            //populate data from form
            wineReview.setWineName(txtWineName.getText().toString());
            wineReview.setWineMaker(txtWineMaker.getText().toString());
            wineReview.setWineType(txtWineType.getText().toString());
            wineReview.setWineYear(txtWineYear.getText().toString());
            wineReview.setWineLocation(txtWineLocation.getText().toString());
            wineReview.setWineDescription(txtWineDescription.getText().toString());
            //wineReview.setWineImage(wineImage.getImage);
            wineReview.setWineRating(wineRating.getRating());

            //ArrayList<WineReview> wineReviewArrayList = new ArrayList<>();
            //wineReviewArrayList.add(wineReview);

            Gson gson = new Gson();
            String wineReviewJSON = gson.toJson(wineReview);

            Log.d("***Log***","winereviewJSON: " + wineReviewJSON);

            //store new wine review in local storage? local and cloud?

            Toast.makeText(this, "Created a review for " + wineReview.getWineName(),
                    Toast.LENGTH_LONG).show();

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
}
