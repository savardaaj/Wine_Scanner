package alex.winescanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class CardDetailsActivity extends AppCompatActivity {

    ArrayList<WineReview> wineReviewArrayList;
    WineReview wr;

    DataBaseHandler dbh;

    ConstraintLayout cl;
    EditText txtWineName;
    EditText txtWineMaker;
    EditText txtWineType;
    EditText txtWineYear;
    EditText txtWineLocation;
    EditText txtWineBarcode;
    EditText txtWineDescription;
    ImageView ivWineImage;
    ImageView ivPlaceholderAdd;

    RatingBar wineRating;

    private StorageReference storageRef;
    FirebaseFirestore fs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FirebaseApp.initializeApp(this);
        fs = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        cl = findViewById(R.id.content_new_wine_entry);
        txtWineName =  cl.findViewById(R.id.txtWineName);
        txtWineMaker =  cl.findViewById(R.id.txtWineMaker);
        txtWineType =  cl.findViewById(R.id.txtWineType);
        txtWineYear =  cl.findViewById(R.id.txtWineYear);
        txtWineLocation =  cl.findViewById(R.id.txtWineLocation);
        txtWineBarcode = cl.findViewById(R.id.txt_barcode);
        txtWineDescription =  cl.findViewById(R.id.txtWineDescription);
        wineRating =  cl.findViewById(R.id.ratingBar2);
        ivWineImage = cl.findViewById(R.id.iv_wine_picture);

        Intent intent = getIntent();
        if(intent != null) {
            String JSON  = intent.getStringExtra("details");
            wr = new Gson().fromJson(JSON, WineReview.class);
            loadWineReview(wr);
        }
        getComments();
    }

    public void loadWineReview(WineReview wr) {
        Log.d("***Debug***", "inside loadWineReview");

        try {
            String barcode = "[" + wr.barcode + "]";
            //populate form from existing data
            txtWineName.setText(wr.name);
            txtWineMaker.setText(wr.maker);
            txtWineType.setText(wr.type);
            txtWineYear.setText(wr.year);
            txtWineLocation.setText(wr.location);
            txtWineBarcode.setText(barcode);
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

    public void getComments() {
        Log.d("***Debug***", "inside getComments");
        //query all reviews with the same barcode

        queryWineReviews(fs, wr);
        //Create a card for each one
    }

    public void loadComments() {
        Log.d("***Debug***", "inside loadComments");

        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout scrollContainer = findViewById(R.id.content_card_details_container);
        //scrollContainer.removeAllViews();

        //Create a card for each one
        for(WineReview wr : wineReviewArrayList) {
            //setup layout stuff
            View comment = inflater.inflate(R.layout.content_comment, null, false);
            comment.setPadding(0,0,0, 10);
            comment.setTag(wr);

            //initialize the layout fields
            TextView txtProfileName = comment.findViewById(R.id.tv_profile_name);
            ImageView ivProfilePicture = comment.findViewById(R.id.iv_profile);
            RatingBar rbRating = comment.findViewById(R.id.rb_comment);
            TextView txtDescription = comment.findViewById(R.id.tv_comment_description);

            //txtProfileName.setText(user.name);
            //ivProfilePicture.setImageDrawable(user.profile_picture);
            rbRating.setRating(wr.rating);
            txtDescription.setText(wr.description);

            scrollContainer.addView(comment);
        }
    }

    private void queryWineReviews(FirebaseFirestore db, WineReview wr) {
        Log.d("***Debug***", "inside queryWineReview");
        wineReviewArrayList = new ArrayList<WineReview>();
        db.collection("WineReviews").whereEqualTo("barcode", wr.barcode).whereEqualTo("shareReview", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("***DEBUG***", document.getId() + " => " + document.getData());
                                WineReview wr = document.toObject(WineReview.class);
                                wineReviewArrayList.add(wr);
                            }
                            loadComments();
                        } else {
                            Log.d("***ERROR***", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

}
