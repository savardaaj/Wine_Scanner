package alex.winescanner;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;

public class NewWineEntryActivity extends AppCompatActivity {

    private int NEW_WR_IMAGE = 2;

    WineReview existingWineReview;
    WineReview newWineReview;

    ConstraintLayout cl;
    EditText txtWineName;
    EditText txtWineMaker;
    EditText txtWineType;
    EditText txtWineYear;
    EditText txtWineLocation;
    EditText txtWineDescription;
    CheckBox cbShareReview;

    ImageView ivWinePicture;
    ImageView ivPlaceholderAdd;

    RatingBar wineRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_wine_entry);

        newWineReview = new WineReview();

        cl = findViewById(R.id.new_wine_entry);
        txtWineName = cl.findViewById(R.id.txtWineName);
        txtWineMaker = cl.findViewById(R.id.txtWineMaker);
        txtWineType = cl.findViewById(R.id.txtWineType);
        txtWineYear = cl.findViewById(R.id.txtWineYear);
        txtWineLocation = cl.findViewById(R.id.txtWineLocation);
        txtWineDescription = cl.findViewById(R.id.txtWineDescription);
        wineRating = cl.findViewById(R.id.ratingBar2);
        ivWinePicture = cl.findViewById(R.id.iv_wine_picture);
        ivPlaceholderAdd = cl.findViewById(R.id.iv_placeholder_add);
        cbShareReview = cl.findViewById(R.id.cb_share_review);

        txtWineName.clearFocus();

        Intent intent = getIntent();
        if(intent != null) {
            String JSON  = intent.getStringExtra("edit");
            existingWineReview = new Gson().fromJson(JSON, WineReview.class);
            if(existingWineReview != null) {
                populateExistingReview();
            }
        }
    }

    public void populateExistingReview() {
        Log.d("***DEBUG***", "Inside populateExistingReview");

        try {
            //populate form from existing data
            txtWineName.setText(existingWineReview.name);
            txtWineMaker.setText(existingWineReview.maker);
            txtWineType.setText(existingWineReview.type);
            txtWineYear.setText(existingWineReview.year);
            txtWineLocation.setText(existingWineReview.location);
            txtWineDescription.setText(existingWineReview.description);
            wineRating.setRating(existingWineReview.rating);
            cbShareReview.setChecked(existingWineReview.shareReview);

            if(existingWineReview.imageBitmap != null) {
                ivWinePicture.setImageBitmap(existingWineReview.imageBitmap);
            }

        }
        catch(Exception e) {
            Log.d("**ERROR**",": " + e.getMessage());
            Log.d("**ERROR**",": " + e.getStackTrace());
            Toast.makeText(this, "An Error Occurred", Toast.LENGTH_LONG).show();

            //return to menu_library
            Intent intent = new Intent(this, LibraryActivity.class);
            setResult(RESULT_CANCELED, intent);
            finish();
        }

    }

    public void onClickSaveWineReview(View v) {
        Log.d("***DEBUG***", "Inside saveWineReview");

        try {
            Gson gson = new Gson();
            File newFile;

            //if we are editing, populate with existing
            if(existingWineReview != null) {
                newWineReview = existingWineReview;
            }

            //change the storage location of the image taken
            if(!newWineReview.pictureFilePath.isEmpty() || newWineReview.pictureFilePath != null) {

                File directory = new File (LibraryActivity.wineScannerImagesDirectory.getPath());
                File oldFile = new File(newWineReview.pictureFilePath);

                newFile = new File(directory, newWineReview.id + ".png");
                if(oldFile.renameTo(newFile)) {
                    Log.d("***DEBUG***", "Rename Successful");
                }

                newWineReview.pictureFilePath = newFile.getPath();
                //wine review image holds a file path
                newWineReview.imageBitmap = BitmapFactory.decodeResource(this.getResources(), R.id.iv_wine_picture);
            }

            //populate data from form
            newWineReview.name = txtWineName.getText().toString();
            newWineReview.maker = txtWineMaker.getText().toString();
            newWineReview.type = txtWineType.getText().toString();
            newWineReview.year = txtWineYear.getText().toString();
            newWineReview.location = txtWineLocation.getText().toString();
            newWineReview.description = txtWineDescription.getText().toString();
            newWineReview.rating = wineRating.getRating();
            newWineReview.shareReview = cbShareReview.isChecked();

            if(existingWineReview != null) {
                Toast.makeText(this, "Edited review for " + newWineReview.name,
                        Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, "Created a review for " + newWineReview.name,
                        Toast.LENGTH_LONG).show();
            }

            String wineReviewJSON = gson.toJson(newWineReview);
            //return to library activity
            Intent intent = new Intent(this, LibraryActivity.class);
            intent.putExtra("wineReviewJSON", wineReviewJSON);
            setResult(RESULT_OK, intent);
        }
        catch(Exception e) {
            Log.d("**ERROR**",": " + e.getLocalizedMessage());
            Log.d("**ERROR2**",": " + e.getCause());
            Toast.makeText(this, "An Error Occurred", Toast.LENGTH_LONG).show();

            //return to library activity
            Intent intent = new Intent(this, LibraryActivity.class);
            setResult(RESULT_CANCELED, intent);
        }

        super.finish();

    }

    public void onClickHelp(View v) {
        String message = "This will allow your review to show up to other people for this specific barcodeWine";
        String title = "Help";

        AlertDialogBuilder adb = new AlertDialogBuilder();
        adb.createNewAlertDialog(this, title, message);
    }

    public void onClickCancel(View v) {
        finish();
    }

    public void onClickAddWineImage(View v) {
        //open camera, user takes picture, return with image
        Intent intent = new Intent(this, AndroidCameraApi.class);
        startActivityForResult(intent, NEW_WR_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("***Debug***", "inside onActivityResult");

        File pictureFile;
        Bitmap myBitmap;

        if (resultCode == RESULT_OK && requestCode == NEW_WR_IMAGE) {
            Bundle bundle = data.getExtras();
            Log.d("***Debug***", "inside bundle " + bundle);
            if(bundle != null) {
                pictureFile = (File) bundle.getSerializable("file");
                myBitmap = BitmapFactory.decodeFile(pictureFile.getAbsolutePath());
                ivWinePicture.setImageBitmap(myBitmap);
                setWineReviewImage(pictureFile);
            }

        }
    }

    public void setWineReviewImage(File file) {
        Log.d("***Debug***", "inside setWineReviewImage");

        //sets the path of the default location for the picture
        if(existingWineReview != null) {
            existingWineReview.pictureFilePath = file.getPath();
        } else {
            newWineReview.pictureFilePath = file.getPath();
        }
    }


}
