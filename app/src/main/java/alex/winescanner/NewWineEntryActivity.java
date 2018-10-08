package alex.winescanner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;

public class NewWineEntryActivity extends AppCompatActivity {

    private static final String[] WINETYPES = new String[] {
            "Barbera", "Cabernet Sauvignon", "Chardonnay", "Malbec", "Merlot",
            "Moscato", "Syrah", "Pinot Grigio", "Pinot Noir", "Riesling",
            "Sangiovese", "Sauvignon Blanc", "Zinfandel",
    };


    boolean isNewEntry = false;

    private int NEW_WR_IMAGE = 2;
    private int UPDATE_BARCODE = 5;

    WineReview existingWineReview = new WineReview();
    WineReview newWineReview;
    BarcodeWine bcWine;

    ConstraintLayout cl;
    EditText txtWineName;
    EditText txtWineMaker;
    AutoCompleteTextView acWineType;
    EditText txtWineYear;
    EditText txtWineLocation;
    EditText txtWineDescription;
    CheckBox cbShareReview;
    EditText txtBarcode;

    ImageView ivWinePicture;
    ImageView ivPlaceholderAdd;

    RatingBar wineRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_wine_entry);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, WINETYPES);
        AutoCompleteTextView textView = findViewById(R.id.ac_type);
        textView.setAdapter(adapter);


        newWineReview = new WineReview();

        cl = findViewById(R.id.new_wine_entry);
        txtWineName = cl.findViewById(R.id.txtWineName);
        txtWineMaker = cl.findViewById(R.id.txtWineMaker);
        acWineType = cl.findViewById(R.id.ac_type);
        txtWineYear = cl.findViewById(R.id.txtWineYear);
        txtWineLocation = cl.findViewById(R.id.txtWineLocation);
        txtWineDescription = cl.findViewById(R.id.txtWineDescription);
        txtBarcode = cl.findViewById(R.id.txt_barcode);
        wineRating = cl.findViewById(R.id.ratingBar2);
        ivWinePicture = cl.findViewById(R.id.iv_wine_picture);
        ivPlaceholderAdd = cl.findViewById(R.id.iv_placeholder_add);
        cbShareReview = cl.findViewById(R.id.cb_share_review);

        cbShareReview.setTypeface(ResourcesCompat.getFont(this, R.font.comfortaa_light));

        txtWineName.clearFocus();

        Intent data = getIntent();
        Bundle bundle = data.getExtras();
        if(bundle != null) {
            bcWine = (BarcodeWine) bundle.getSerializable("barcodeWine");
            String JSON  = bundle.getString("edit");
            if(bcWine != null) {
                isNewEntry = true;
                populateFromBarcodeScan(bcWine);
            }
            else if(JSON != null) {
                isNewEntry = false;
                existingWineReview = new Gson().fromJson(JSON, WineReview.class);
                if(existingWineReview != null) {
                    populateExistingReview();
                }
            }
        }
    }

    public void populateFromBarcodeScan(BarcodeWine bcWine) {
        Log.d("***DEBUG***", "Inside populateFromBarcodeScan");

        try {
            if(bcWine.title == null || bcWine.title.equals("")) {
                AlertDialogBuilder adb = new AlertDialogBuilder();
                adb.createNewAlertDialog(this, "No Results",
                        "The barcode you scanned was not found in the database. " +
                                 "Please enter the data manually");

            }

            existingWineReview.barcode = bcWine.upc;
            existingWineReview.name = bcWine.title;
            existingWineReview.maker = bcWine.brand;
            existingWineReview.type = bcWine.color;
            existingWineReview.description = bcWine.description;

            String barCode = "[" + existingWineReview.barcode + "]";
            //populate form from barcode data
            txtBarcode.setText(barCode);
            txtWineName.setText(bcWine.title);
            txtWineMaker.setText(bcWine.brand);
            acWineType.setText(bcWine.color);
            //txtWineYear.setText(existingWineReview.year);
            //txtWineLocation.setText(existingWineReview.location);
            txtWineDescription.setText(bcWine.description);
            //wineRating.setRating(existingWineReview.rating);
            //cbShareReview.setChecked(existingWineReview.shareReview);

            //TODO: incorporate later
            //if(existingWineReview.imageBitmap != null) {
                //ivWinePicture.setImageBitmap(existingWineReview.imageBitmap);
            //}

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

    public void populateExistingReview() {
        Log.d("***DEBUG***", "Inside populateExistingReview");

        try {
            String barcode = "[" + existingWineReview.barcode + "]";

            //populate form from existing data
            txtWineName.setText(existingWineReview.name);
            txtWineMaker.setText(existingWineReview.maker);
            acWineType.setText(existingWineReview.type);
            txtWineYear.setText(existingWineReview.year);
            txtWineLocation.setText(existingWineReview.location);
            txtWineDescription.setText(existingWineReview.description);
            txtBarcode.setText(barcode);
            wineRating.setRating(existingWineReview.rating);
            cbShareReview.setChecked(existingWineReview.shareReview);

            if(existingWineReview.imageBitmap != null) {
                ivWinePicture.setImageBitmap(existingWineReview.imageBitmap);
                ivPlaceholderAdd.setVisibility(View.INVISIBLE);
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
        Log.d("***DEBUG***", "Inside onclickSaveWineReview");

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
            newWineReview.type = acWineType.getText().toString();
            newWineReview.year = txtWineYear.getText().toString();
            newWineReview.location = txtWineLocation.getText().toString();
            newWineReview.description = txtWineDescription.getText().toString();
            newWineReview.barcode = txtBarcode.getText().toString().replaceAll("[^0-9]", "");
            newWineReview.rating = wineRating.getRating();
            newWineReview.shareReview = cbShareReview.isChecked();

            //Add more wines in future
            if(newWineReview.name.equals("")) {
                Toast.makeText(this, "Name cannot be blank", Toast.LENGTH_SHORT).show();
                return;
            }

            if(!isNewEntry) {
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

    public void onClickUpdateBarcode(View v) {
        Intent intent = new Intent(this, BarcodeScanner.class);
        intent.putExtra("requestCode", UPDATE_BARCODE);
        startActivityForResult(intent, UPDATE_BARCODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("***Debug***", "inside onActivityResult");

        File pictureFile;
        Bitmap myBitmap;

        if (resultCode == RESULT_OK) {
            if(requestCode == NEW_WR_IMAGE) {
                Bundle bundle = data.getExtras();
                Log.d("***Debug***", "inside bundle " + bundle);
                if (bundle != null) {
                    pictureFile = (File) bundle.getSerializable("file");
                    myBitmap = BitmapFactory.decodeFile(pictureFile.getAbsolutePath());
                    ivWinePicture.setImageBitmap(myBitmap);
                    setWineReviewImage(pictureFile);
                }
            }
            else if(requestCode == UPDATE_BARCODE) {
                String strBarcode = data.getStringExtra("barcode");
                txtBarcode.setText(strBarcode);
                setWineReviewBarcode(strBarcode);
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

    public void setWineReviewBarcode(String barcode) {
        Log.d("***Debug***", "inside setWineReviewImage");

        //sets the path of the default location for the picture
        if(existingWineReview != null) {
            existingWineReview.barcode = barcode;
        } else {
            newWineReview.barcode = barcode;
        }
    }


}
