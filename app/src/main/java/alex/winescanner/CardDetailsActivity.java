package alex.winescanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.util.ArrayList;

public class CardDetailsActivity extends AppCompatActivity {

    CardDetailsActivity cda;

    ArrayList<WineReview> commentsArrayList;
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
    EditText txtLikeDescription;
    CheckBox cbIsShared;

    ImageView ivWineImage;
    ImageView ivPlaceholderAdd;
    ImageView ivWineLike;
    ImageView ivWineLikeInactive;

    RatingBar wineRating;

    private StorageReference storageRef;
    FirebaseFirestore fs;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_details);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        cda = new CardDetailsActivity();

        FirebaseApp.initializeApp(this);
        fs = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
        dbh = new DataBaseHandler();

        Intent intent = getIntent();
        if(intent != null) {
            Bundle extras = intent.getExtras();
            if(extras != null) {
                user = extras.getParcelable("user");
                String JSON = extras.getString("details");
                wr = new Gson().fromJson(JSON, WineReview.class);
                Log.d("***DEBUG***", "Winereview: " + wr);
                loadWineReview(wr);
            }
        }
        getComments(wr);
    }

    public void loadWineReview(WineReview wr) {
        Log.d("***Debug***", "inside loadWineReview");

        try {
            cl = findViewById(R.id.content_new_wine_entry);
            txtWineName =  cl.findViewById(R.id.txtWineName);
            txtWineMaker =  cl.findViewById(R.id.txtWineMaker);
            txtWineType =  cl.findViewById(R.id.txtWineType);
            txtWineYear =  cl.findViewById(R.id.txtWineYear);
            txtWineLocation =  cl.findViewById(R.id.txtWineLocation);
            txtWineBarcode = cl.findViewById(R.id.txt_barcode);
            txtWineDescription =  cl.findViewById(R.id.txtWineDescription);
            txtLikeDescription = cl.findViewById(R.id.txt_like_description);
            wineRating =  cl.findViewById(R.id.ratingBar2);
            ivWineImage = cl.findViewById(R.id.iv_wine_picture);
            cbIsShared = cl.findViewById(R.id.cb_carddetails_isshared);
            cbIsShared.setTypeface(ResourcesCompat.getFont(this, R.font.comfortaa_light));

            //populate form from existing data
            txtWineName.setText(wr.name);
            txtWineMaker.setText(wr.maker);
            txtWineType.setText(wr.type);
            txtWineYear.setText(wr.year);
            txtWineLocation.setText(wr.location);
            cbIsShared.setChecked(wr.shareReview);

            if(cbIsShared.isChecked()) {
                if(wr.likes.size() == 0) {
                    txtLikeDescription.setText(R.string.like_description_none);
                }
                else if(wr.likes.size() == 1) {
                    String str = "1 " + getResources().getString(R.string.like_description_singular);
                    txtLikeDescription.setText(str);
                }
                else if(wr.likes.size() > 1) {
                    String str = "" + wr.likes.size() + " " + getResources().getString(R.string.like_description_plural);
                    txtLikeDescription.setText(str);
                }
            }
            else {
                cbIsShared.setVisibility(View.INVISIBLE);
                txtLikeDescription.setVisibility(View.INVISIBLE);
            }


            String barcode = "[" + wr.barcode + "]";
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

    public void getComments(WineReview wr) {
        Log.d("***Debug***", "inside getComments");
        //query all reviews with the same barcode

        dbh.queryWineReviews(fs, this, wr);
        //Create a card for each one
    }

    //called from the DatabaseHandler
    public void loadComments() {
        Log.d("***Debug***", "inside loadComments");

        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout scrollContainer = findViewById(R.id.content_card_details_comments);
        scrollContainer.removeAllViews();

        //Create a card for each one
        for(WineReview comment : commentsArrayList) {
            if(!user.getUid().equals(comment.userUUID)) {
                //setup layout stuff
                View commentCard = inflater.inflate(R.layout.content_comment, null, false);
                commentCard.setPadding(0,0,0, 10);
                commentCard.setTag(comment);

                //initialize the layout fields
                TextView txtProfileName = commentCard.findViewById(R.id.tv_profile_name);
                //ImageView ivProfilePicture = commentCard.findViewById(R.id.iv_profile);
                ImageView ivLike = commentCard.findViewById(R.id.iv_like);
                ImageView ivLikeInactive = commentCard.findViewById(R.id.iv_like_inactive);
                TextView tvLikeCount = commentCard.findViewById(R.id.tv_like_count);
                RatingBar rbRating = commentCard.findViewById(R.id.rb_comment);
                TextView txtDescription = commentCard.findViewById(R.id.tv_comment_description);

                txtProfileName.setText(user.getDisplayName());
                //ivProfilePicture.setImageDrawable(user.profile_picture);

                tvLikeCount.setText(comment.getLikesSize());
                rbRating.setRating(comment.rating);
                txtDescription.setText(comment.description);

                if(comment.likes.contains(user.getUid())) {
                    ivLike.setVisibility(View.VISIBLE);
                    ivLikeInactive.setVisibility(View.INVISIBLE);
                }
                else {
                    ivLike.setVisibility(View.INVISIBLE);
                    ivLikeInactive.setVisibility(View.VISIBLE);
                }


                scrollContainer.addView(commentCard);
            }

        }
    }

    public void setCommentsArrayList(ArrayList<WineReview> list) {
        commentsArrayList = list;
    }

    public void onClickLikeComment(View v) {
        Log.d("***Debug***", "inside onClickLikeComment");

        try {
            View commentCard = (View) v.getParent();
            ImageView like = commentCard.findViewById(R.id.iv_like);
            ImageView likeInactive = commentCard.findViewById(R.id.iv_like_inactive);

            //All comments for this wine review
            for (WineReview cm : commentsArrayList) {
                if (cm == commentCard.getTag()) {
                    if(cm.likes.contains(user.getUid())){
                        like.setVisibility(View.INVISIBLE);
                        likeInactive.setVisibility(View.VISIBLE);
                        //update the wine review's like_counts by storing the userUUID
                        cm.likes.remove(user.getUid());
                        dbh.updateCommentLikes(fs, this, cm);
                    }
                    else {
                        like.setVisibility(View.VISIBLE);
                        likeInactive.setVisibility(View.INVISIBLE);
                        //update the wine review's like_counts by storing the userUUID
                        cm.likes.add(user.getUid());
                        dbh.updateCommentLikes(fs, this, cm);
                    }

                }
            }
        } catch(Exception e) {
            Log.d("***ERROR***", "onClickWineCardRemove: " + e.getMessage());
            Log.d("***ERROR***", "onClickWineCardRemove: " + e);
            Toast.makeText(this, "Error Occurred", Toast.LENGTH_SHORT).show();

        }
    }

    private void updateComment(WineReview cm) {

    }

}
