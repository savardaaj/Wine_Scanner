package alex.winescanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

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
    TextView txtNotableChars;
    TextView tvRatingsCount;
    CheckBox cbIsShared;
    CheckBox cbIsSharedNotableChar;

    ImageView ivWineImage;
    ImageView ivPlaceholderAdd;
    ImageView ivWineLike;
    ImageView ivWineLikeInactive;

    boolean ratingsShowing = false;

    RatingBar rbWineRating;
    RatingBar rbUserRating;

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
                loadWineReview(wr);
                drawChosenCharacteristics(wr);
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
            rbWineRating =  cl.findViewById(R.id.rb_details_community);
            rbUserRating = cl.findViewById(R.id.rb_user_rating);
            ivWineImage = cl.findViewById(R.id.iv_wine_picture);
            cbIsShared = cl.findViewById(R.id.cb_carddetails_isshared);
            txtNotableChars = cl.findViewById(R.id.tv_notable_char_placeholder);
            cbIsSharedNotableChar = cl.findViewById(R.id.cb_isshared_notablechars);
            cbIsShared.setTypeface(ResourcesCompat.getFont(this, R.font.comfortaa_light));
            cbIsSharedNotableChar.setTypeface(ResourcesCompat.getFont(this, R.font.comfortaa_light));
            tvRatingsCount = cl.findViewById(R.id.tv_ratings_count);

            rbWineRating.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    view.performClick();
                    return false;
                }
            });

            //populate form from existing data
            txtWineName.setText(wr.name);
            txtWineMaker.setText(wr.maker);
            txtWineType.setText(wr.type);
            txtWineYear.setText(wr.year);
            txtWineLocation.setText(wr.location);
            cbIsShared.setChecked(wr.shareReview);
            cbIsSharedNotableChar.setChecked(wr.shareCharacteristics);



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

            if(wr.characteristics.size() > 0) {
                txtNotableChars.setVisibility(View.GONE);
            }
            else { txtNotableChars.setVisibility(View.VISIBLE); }

            String barcode = "[" + wr.barcode + "]";
            txtWineBarcode.setText(barcode);
            txtWineDescription.setText(wr.description);
            rbWineRating.setRating(wr.rating);
            rbUserRating.setRating(wr.rating);

            if(wr.imageBitmap != null) {
                ivWineImage.setImageBitmap(wr.imageBitmap);

            }

        }
        catch(Exception e) {
            Log.d("**ERROR**",": " + e.getMessage());
            Toast.makeText(this, "An Error Occurred in CardDetailsActivity", Toast.LENGTH_LONG).show();
            //return to menu_library

            Intent intent = new Intent(this, LibraryActivity.class);
            setResult(RESULT_CANCELED, intent);
        }
    }

    public void getComments(WineReview wr) {
        Log.d("***Debug***", "inside getComments");
        //query all reviews with the same barcode

        dbh.queryWineReviews(fs, this, wr);
    }

    public void populateRatingsBreakdown() {
        Log.d("***Debug***", "inside populateRatingsBreakdown");
        ConstraintLayout dropdownContainer = findViewById(R.id.new_wine_entry_ratings_container);

        ProgressBar pbSub5 = dropdownContainer.findViewById(R.id.pb_sub5);
        ProgressBar pbSub4 = dropdownContainer.findViewById(R.id.pb_sub4);
        ProgressBar pbSub3 = dropdownContainer.findViewById(R.id.pb_sub3);
        ProgressBar pbSub2 = dropdownContainer.findViewById(R.id.pb_sub2);
        ProgressBar pbSub1 = dropdownContainer.findViewById(R.id.pb_sub1);

        TextView tvSub5 = dropdownContainer.findViewById(R.id.tv_sub5);
        TextView tvSub4 = dropdownContainer.findViewById(R.id.tv_sub4);
        TextView tvSub3 = dropdownContainer.findViewById(R.id.tv_sub3);
        TextView tvSub2 = dropdownContainer.findViewById(R.id.tv_sub2);
        TextView tvSub1 = dropdownContainer.findViewById(R.id.tv_sub1);

        float fiveStarPercent = 0;
        float fourStarPercent = 0;
        float threeStarPercent = 0;
        float twoStarPercent = 0;
        float oneStarPercent = 0;

        float count5 = 0;
        float count4 = 0;
        float count3 = 0;
        float count2 = 0;
        float count1 = 0;

        float totalReviews = commentsArrayList.size();
        float totalRating = 0;

        for(WineReview comment : commentsArrayList) {
            if(comment.rating == 5) {
                count5++;
                totalRating += 5;
            }
            else if(comment.rating == 4) {
                count4++;
                totalRating += 4;
            }
            else if(comment.rating == 3) {
                count3++;
                totalRating += 3;
            }
            else if(comment.rating == 2) {
                count2++;
                totalRating += 2;
            }
            else if(comment.rating == 1) {
                count1++;
                totalRating += 1;
            }
        }

        totalRating = totalRating/totalReviews;

        rbWineRating.setRating(totalRating);
        String text = "(" + (int)totalReviews + ")";
        tvRatingsCount.setText(text);

        if(count5 != 0) {
            fiveStarPercent = (1 / (totalReviews / count5)) * 100;
            pbSub5.setProgress((int)fiveStarPercent);
        }
        if(count4 != 0) {
            fourStarPercent = (1 / (totalReviews / count4)) * 100;
            pbSub4.setProgress((int)fourStarPercent);
        }
        if(count3 != 0) {
            threeStarPercent = (1 / (totalReviews / count3)) * 100;
            pbSub3.setProgress((int)threeStarPercent);
        }
        if(count2 != 0) {
            twoStarPercent = (1 / (totalReviews / count2)) * 100;
            pbSub2.setProgress((int)twoStarPercent);
        }
        if(count1 != 0) {
            oneStarPercent = (1 / (totalReviews / count1)) * 100;
            pbSub1.setProgress((int)oneStarPercent);
        }


        String c5 = fiveStarPercent + "%";
        String c4 = fourStarPercent + "%";
        String c3 = threeStarPercent + "%";
        String c2 = twoStarPercent + "%";
        String c1 = oneStarPercent + "%";

        tvSub5.setText(c5);
        tvSub4.setText(c4);
        tvSub3.setText(c3);
        tvSub2.setText(c2);
        tvSub1.setText(c1);
    }

    public void onClickToggleRatings(View view) {
        Log.d("***Debug***", "inside onClickToggleRatings");

        final ConstraintLayout ratingsContainer = findViewById(R.id.new_wine_entry_ratings_container);

        if(ratingsShowing) {

            ratingsShowing = false;
            ratingsContainer.measure(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            final int targetHeight = ratingsContainer.getMeasuredHeight();

            // Older versions of android (pre API 21) cancel animations for views with a height of 0.
            ratingsContainer.getLayoutParams().height = 1;
            ratingsContainer.setVisibility(View.VISIBLE);
            Animation a = new Animation()
            {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    ratingsContainer.getLayoutParams().height = interpolatedTime == 1
                            ? RelativeLayout.LayoutParams.WRAP_CONTENT
                            : (int)(targetHeight * interpolatedTime);
                    ratingsContainer.requestLayout();
                }

                @Override
                public boolean willChangeBounds() {
                    return true;
                }
            };

            // 1dp/ms
            a.setDuration((int)(targetHeight / ratingsContainer.getContext().getResources().getDisplayMetrics().density));
            ratingsContainer.startAnimation(a);
        }
        else {

            ratingsShowing = true;

            final int initialHeight = ratingsContainer.getMeasuredHeight();

            Animation a = new Animation()
            {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    if(interpolatedTime == 1){
                        ratingsContainer.setVisibility(View.GONE);
                    }else{
                        ratingsContainer.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                        ratingsContainer.requestLayout();
                    }
                }

                @Override
                public boolean willChangeBounds() {
                    return true;
                }
            };

            // 1dp/ms
            a.setDuration((int)(initialHeight / ratingsContainer.getContext().getResources().getDisplayMetrics().density));
            ratingsContainer.startAnimation(a);
        }
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
                if(comment.shareReview) {
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
    }

    public void setCommentsArrayList(ArrayList<WineReview> list) {
        commentsArrayList = list;
        populateRatingsBreakdown();
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

    //draws what the user has chosen for characteristics
    public void drawChosenCharacteristics(WineReview wr) {
        Log.d("***DEBUG***", "inside drawChosenCharacteristics");

        LayoutInflater inflater = LayoutInflater.from(this);
        com.google.android.flexbox.FlexboxLayout fbChosenContainer = this.findViewById(R.id.fb_chosen_char_container);
        fbChosenContainer.removeAllViews();

        //Display all active
        for(Map.Entry<String,String> str : wr.characteristics.entrySet()) {
            View activeChar = inflater.inflate(R.layout.characteristic_view, fbChosenContainer, false);

            CardView cvContainer = activeChar.findViewById(R.id.cv_char_container);
            TextView charText = cvContainer.findViewById(R.id.tv_char_text);
            charText.setText(str.getValue());
            fbChosenContainer.addView(activeChar);
        }
    }

}
