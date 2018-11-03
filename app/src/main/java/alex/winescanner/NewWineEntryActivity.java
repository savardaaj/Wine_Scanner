package alex.winescanner;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class NewWineEntryActivity extends AppCompatActivity {

    private static final String[] WINETYPES = new String[] {
            "Barbera", "Cabernet Sauvignon", "Chardonnay", "Malbec", "Merlot",
            "Moscato", "Syrah", "Pinot Grigio", "Pinot Noir", "Riesling",
            "Sangiovese", "Sauvignon Blanc", "Zinfandel",
    };

    Map<String, String> characteristicsMap;
    Map<String, String> inactiveCharMap;
    Map<String, String> activeCharMap;


    boolean isNewEntry = false;

    private int NEW_WR_IMAGE = 2;
    private int UPDATE_BARCODE = 5;

    DataBaseHandler dbh;

    WineReview existingWineReview = new WineReview();
    WineReview newWineReview;
    BarcodeWine bcWine;
    FirebaseFirestore fs;
    FirebaseUser user;

    ConstraintLayout cl;
    EditText txtWineName;
    EditText txtWineMaker;
    AutoCompleteTextView acWineType;
    EditText txtWineYear;
    EditText txtWineLocation;
    EditText txtWineDescription;
    CheckBox cbShareReview;
    CheckBox cbShareCharacteristics;
    EditText txtBarcode;

    TextView tvNotableChars;

    ImageView ivWinePicture;
    ImageView ivPlaceholderAdd;

    RatingBar wineRating;

    SearchView searchView;
    View root;

    Dialog charDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_wine_entry);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, WINETYPES);
        AutoCompleteTextView textView = findViewById(R.id.ac_type);
        textView.setAdapter(adapter);

        newWineReview = new WineReview();

        activeCharMap = new HashMap<>();
        inactiveCharMap = new HashMap<>();
        characteristicsMap = new HashMap<>();

        cl = findViewById(R.id.cl_new_wine_entry);
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
        tvNotableChars = cl.findViewById(R.id.tv_notable_char_placeholder);
        cbShareCharacteristics = cl.findViewById(R.id.cb_isshared_notablechars);

        cbShareReview.setTypeface(ResourcesCompat.getFont(this, R.font.comfortaa_light));
        cbShareCharacteristics.setTypeface(ResourcesCompat.getFont(this, R.font.comfortaa_light));

        root = cl;
        txtWineName.clearFocus();

        dbh = new DataBaseHandler();
        fs = FirebaseFirestore.getInstance();


        user = (new Bundle(getIntent().getExtras()).getParcelable("userData"));

        dbh.getCharacteristics(fs, this);


        Intent data = getIntent();
        Bundle bundle = data.getExtras();
        if(bundle != null) {
            bcWine = (BarcodeWine) bundle.getSerializable("barcodeWine");
            String JSON  = bundle.getString("edit");
            if(bcWine != null) {
                if(!bcWine.title.equals("")) {
                    isNewEntry = true;
                    populateFromBarcodeScan(bcWine);
                    dbh.getWineCharacteristics(fs, bcWine.upc,this);
                }
                else {
                    searchAppDatabase(bcWine.upc);
                }

            }
            else if(JSON != null) {
                isNewEntry = false;
                existingWineReview = new Gson().fromJson(JSON, WineReview.class);
                if(existingWineReview != null) {
                    populateExistingReview();
                    dbh.getWineCharacteristics(fs, existingWineReview.barcode,this);
                }
            }
        }
    }

    public void populateFromBarcodeScan(BarcodeWine bcWine) {
        Log.d("***DEBUG***", "Inside populateFromBarcodeScan");

        try {
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
            txtWineDescription.setText(bcWine.description);

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
            cbShareCharacteristics.setChecked(existingWineReview.shareCharacteristics);

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

    public void populateFromUserReview(ArrayList<WineReview> reviewList) {
        Log.d("***DEBUG***", "inside populateFromuserReview");

        if(reviewList != null) {
            if(reviewList.size() > 0) {
                WineReview userReview = reviewList.get(0);

                AlertDialogBuilder alertDialogBuilder = new AlertDialogBuilder();
                alertDialogBuilder.createNewAlertDialog(this, "DataFound", "We were able to find a user review that matched this barcode. Modify the data as you please.");

                try {
                    String barcode = "[" + userReview.barcode + "]";

                    //populate form from existing data
                    txtWineName.setText(userReview.name);
                    txtWineMaker.setText(userReview.maker);
                    acWineType.setText(userReview.type);
                    txtWineYear.setText(userReview.year);
                    txtWineLocation.setText(userReview.location);
                    txtWineDescription.setText("");
                    txtBarcode.setText(barcode);
                    wineRating.setRating(userReview.rating);
                    cbShareReview.setChecked(false);

                    if(userReview.imageBitmap != null) {
                        ivWinePicture.setImageBitmap(userReview.imageBitmap);
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
            else {
                //no existing reviews found for barcode

            }
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
            newWineReview.shareCharacteristics = cbShareCharacteristics.isChecked();
            newWineReview.characteristics = activeCharMap;

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
        Log.d("**DEBUG***", "Inside onClickHelp");
        String message = "This will allow your review to show up to other people for this specific barcodeWine";
        String title = "Help";

        AlertDialogBuilder adb = new AlertDialogBuilder();
        adb.createNewAlertDialog(this, title, message);
    }

    public void onClickCancel(View v) {
        finish();
    }

    public void onClickAddWineImage(View v) {
        Log.d("**DEBUG***", "Inside onClickAddWineImage");
        //open camera, user takes picture, return with image
        Intent intent = new Intent(this, AndroidCameraApi.class);
        startActivityForResult(intent, NEW_WR_IMAGE);
    }

    public void onClickUpdateBarcode(View v) {
        Log.d("**DEBUG***", "Inside onClickUpdateBarcode");
        Intent intent = new Intent(this, BarcodeScanner.class);
        intent.putExtra("requestCode", UPDATE_BARCODE);
        startActivityForResult(intent, UPDATE_BARCODE);
    }
    public void onClickAddCharacteristics(View v) {
        Log.d("**DEBUG***", "Inside onClickAddCharacteristics");
        //set up dialog
        charDialog = new Dialog(this);
        charDialog.setContentView(R.layout.fragment_characteristics);
        charDialog.setTitle("Characteristics");
        charDialog.setCancelable(true);
        ScrollView svActiveContainer = charDialog.findViewById(R.id.sv_active_char_container);
        ScrollView svInactiveContainer = charDialog.findViewById(R.id.sv_inactive_char_container);
        com.google.android.flexbox.FlexboxLayout fbActiveContainer = svActiveContainer.findViewById(R.id.fb_chosen_char_container);
        com.google.android.flexbox.FlexboxLayout fbInactiveContainer = svInactiveContainer.findViewById(R.id.fb_inactive_char_container);
        root = svActiveContainer.getRootView();
        LayoutInflater inflater = LayoutInflater.from(this);
        searchView = charDialog.findViewById(R.id.sv_search_characteristics);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Map<String, String> filteredMap = new HashMap<>();
                //filterinactiveCharArrayList
                for(Map.Entry<String, String> str : inactiveCharMap.entrySet()) {
                    if(str.getValue().toLowerCase().contains(s.toLowerCase())) {
                        filteredMap.put(str.getKey(), str.getValue());
                    }
                }
                //redraw inactive flexbox
                redrawInactiveCharacteristics(filteredMap);
                return false;
            }
        });

        //Display all inactive
        for(Map.Entry<String,String> str : inactiveCharMap.entrySet()) {
            View inactiveChar = inflater.inflate(R.layout.characteristic_inactive, fbInactiveContainer, false);

            CardView cvContainer = inactiveChar.findViewById(R.id.cv_char_container);
            TextView charText = cvContainer.findViewById(R.id.tv_char_text);
            charText.setText(str.getValue());
            fbInactiveContainer.addView(inactiveChar);
        }

        //Display all active
        for(Map.Entry<String,String> str : activeCharMap.entrySet()) {
            View activeChar = inflater.inflate(R.layout.characteristic_active, fbActiveContainer, false);

            CardView cvContainer = activeChar.findViewById(R.id.cv_char_container);
            TextView charText = cvContainer.findViewById(R.id.tv_char_text);
            charText.setText(str.getValue());
            fbActiveContainer.addView(activeChar);
        }

        charDialog.show();
    }

    //Remove from inactive and add to active
    public void onClickAddCharacteristic(View v) {
        Log.d("**DEBUG***", "Inside onClickAddCharacteristic");
        //add the char to active
        View root = v.getRootView();
        TextView name = v.findViewById(R.id.tv_char_text);
        String charName = name.getText().toString();

        //get Key for description
        String key = getKeyByValue(inactiveCharMap, charName);

        if(key != null) {
            activeCharMap.put(key, charName);
            inactiveCharMap.remove(charName);
        }
        else {
            Log.d("***DEBUG***", "Failed to find key");
            finish();
        }

        addActiveCharacteristic(root, charName);

        ((ViewGroup) v.getParent()).removeView(v);
    }

    //remove from active and add to inactive
    public void onClickRemoveCharacteristic(View v) {
        Log.d("**DEBUG***", "Inside onClickRemoveCharacteristic");
        //remove from active, move to inactive
        View root = v.getRootView();
        View card = (View) v.getParent().getParent();

        //View cl = v.findViewById(R.id.cl_char_container);
        TextView name = card.findViewById(R.id.tv_char_text);
        String charName = name.getText().toString();

        //get Key for description
        String key = getKeyByValue(inactiveCharMap, charName);

        if(key != null) {
            inactiveCharMap.put(key, charName);
            activeCharMap.remove(charName);
        }
        else {
            Log.d("***DEBUG***", "Failed to find key");
            finish();
        }

        removeActiveCharacteristic(root, charName);

        ((ViewGroup) card.getParent()).removeView(card);
    }

    public void addActiveCharacteristic(View root, String str) {
        Log.d("***DEBUG***", "inside addActiveCharacteristic");

        ScrollView svActiveContainer = root.findViewById(R.id.sv_active_char_container);

        com.google.android.flexbox.FlexboxLayout fbActiveContainer = svActiveContainer.findViewById(R.id.fb_chosen_char_container);
        LayoutInflater inflater = LayoutInflater.from(this);
        View activeChar = inflater.inflate(R.layout.characteristic_active, fbActiveContainer, false);

        CardView cvContainer = activeChar.findViewById(R.id.cv_char_container);
        TextView charText = cvContainer.findViewById(R.id.tv_char_text);
        charText.setText(str);
        fbActiveContainer.addView(activeChar);
    }

    public void removeActiveCharacteristic(View root, String str) {
        Log.d("***DEBUG***", "inside removeActiveCharacteristic");

        ScrollView svInactiveContainer = root.findViewById(R.id.sv_inactive_char_container);

        com.google.android.flexbox.FlexboxLayout fbInactiveContainer = svInactiveContainer.findViewById(R.id.fb_inactive_char_container);
        LayoutInflater inflater = LayoutInflater.from(this);
        View inactiveChar = inflater.inflate(R.layout.characteristic_inactive, fbInactiveContainer, false);

        CardView cvContainer = inactiveChar.findViewById(R.id.cv_char_container);
        TextView charText = cvContainer.findViewById(R.id.tv_char_text);
        charText.setText(str);
        fbInactiveContainer.addView(inactiveChar);
    }

    public void redrawInactiveCharacteristics(Map<String, String> inactiveCharMap) {
        Log.d("***DEBUG***", "inside redrawInactiveCharacteristics");
        LayoutInflater inflater = LayoutInflater.from(this);
        //final ViewGroup v = (ViewGroup) ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);
        ConstraintLayout cl = root.findViewById(R.id.cl_characteristics_container);
        ScrollView svInactiveContainer = cl.findViewById(R.id.sv_inactive_char_container);

        com.google.android.flexbox.FlexboxLayout fbInactiveContainer = svInactiveContainer.findViewById(R.id.fb_inactive_char_container);

        fbInactiveContainer.removeAllViews();
        for(Map.Entry<String,String> str : inactiveCharMap.entrySet()) {
            Log.d("***DEBUG***", "inactive chars: " + str);
            View inactiveChar = inflater.inflate(R.layout.characteristic_inactive, fbInactiveContainer, false);

            CardView cvContainer = inactiveChar.findViewById(R.id.cv_char_container);
            TextView charText = cvContainer.findViewById(R.id.tv_char_text);
            charText.setText(str.getValue());
            fbInactiveContainer.addView(inactiveChar);
        }
    }

    //draws what the user has chosen for characteristics
    public void drawChosenCharacteristics() {
        Log.d("***DEBUG***", "inside drawChosenCharacteristics");

        //char.setVisibility(View.GONE);
        if(activeCharMap.isEmpty()) {
            tvNotableChars.setVisibility(View.VISIBLE);
        }
        else { tvNotableChars.setVisibility(View.GONE); }

        LayoutInflater inflater = LayoutInflater.from(this);
        com.google.android.flexbox.FlexboxLayout fbChosenContainer = this.findViewById(R.id.fb_chosen_char_container);
        fbChosenContainer.removeAllViews();

        //Display all active
        for(Map.Entry<String,String> str : activeCharMap.entrySet()) {
            View activeChar = inflater.inflate(R.layout.characteristic_view, fbChosenContainer, false);

            CardView cvContainer = activeChar.findViewById(R.id.cv_char_container);
            TextView charText = cvContainer.findViewById(R.id.tv_char_text);
            charText.setText(str.getValue());
            fbChosenContainer.addView(activeChar);
        }
    }

    public void onClickSaveCharacteristics(View v) {
        Log.d("***Debug***", "inside onClickSaveCharacteristics");

        if(charDialog.isShowing()) {
            charDialog.dismiss();
        }
        drawChosenCharacteristics();
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

    public void searchAppDatabase(String barcode) {
        dbh.searchReviewsByBarcode(fs, this, barcode);
    }

    public void setCharacteristicsMap(Map<String,String> charsMap) {
        TreeMap<String,String> sortedMap = new TreeMap<>(charsMap);
        this.characteristicsMap = sortedMap;

        //separate query to populate active characteristics (user chosen)
        //filter out what user already has and populate inactive list
        if(activeCharMap.size() != 0) {
            for(Map.Entry<String,String> str : sortedMap.entrySet()) {
                if(!activeCharMap.entrySet().contains(str)) {
                    inactiveCharMap.put(str.getKey(), str.getValue());
                }
            }
        }
        else {
            inactiveCharMap = sortedMap;
        }
    }

    public void setUserCharacteristicsMap(Map<String,String> charsMap) {
        TreeMap<String,String> sortedMap = new TreeMap<>(charsMap);
        this.characteristicsMap = sortedMap;

        activeCharMap = sortedMap;
        drawChosenCharacteristics();
    }

    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }


}
