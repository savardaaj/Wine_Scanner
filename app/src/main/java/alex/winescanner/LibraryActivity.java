package alex.winescanner;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Rating;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.Login;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.support.constraint.Constraints.TAG;

public class LibraryActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    final Context ctx = this;

    private Gson gson;
    private ArrayList<WineReview> wineReviewArrayList;
    private static int NEW_WR = 1;
    private static int EDIT_WR = 2;
    private static int COMPARE = 3;
    private static int SINGLE_ENTRY = 4;

    private StorageReference storageRef;


    //storage/emulated/0/WineScanner/Images/337039ea-41ed-4a6d-aa4a-a1b583011539.png
    final static File wineScannerImagesDirectory = new File(Environment.getExternalStorageDirectory().getPath() + "/WineScanner/Images");;

    FirebaseUser user;
    FirebaseFirestore fs;
    DataBaseHandler dbh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("***Debug***", "inside LibraryActivity: onCreate");
        super.onCreate(savedInstanceState);
        user = (new Bundle(getIntent().getExtras()).getParcelable("userData"));
        FirebaseApp.initializeApp(this);
        dbh = new DataBaseHandler();
        fs = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        createPictureStorage();

        setContentView(R.layout.activity_library);

        wineReviewArrayList = new ArrayList<>();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout scrollContainer = findViewById(R.id.content_library_container);
        View loading = inflater.inflate(R.layout.loading_screen, null, false);
        scrollContainer.addView(loading);

        //calls load barcodeWine reviews after successful get
        dbh.getWineReviews(fs, this, user);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("***Debug***", "inside LibraryActivity: onActivityResult: " + requestCode);

        String wineReviewJSON;

        if (resultCode == RESULT_OK) {
            if(data != null) {
                if (requestCode == NEW_WR) {
                    wineReviewJSON = data.getStringExtra("wineReviewJSON");
                    if (wineReviewJSON != null) {
                        WineReview wineReview = new Gson().fromJson(wineReviewJSON, WineReview.class);
                        wineReviewArrayList.add(wineReview);
                        dbh.createWineReview(fs, this, wineReview, user);

                        if(wineReview.pictureFilePath != null) {
                            //TODO: keep receiving error "task is not yet complete
                            //uploadFile(wineReview);
                        }
                    }
                }
                else if (requestCode == EDIT_WR) {
                    wineReviewJSON = data.getStringExtra("wineReviewJSON");
                    if (wineReviewJSON != null) {
                        WineReview wineReview = new Gson().fromJson(wineReviewJSON, WineReview.class);
                        Log.d("***DEBUG***", "outside for");
                        for (WineReview wr : wineReviewArrayList) {
                            Log.d("***DEBUG***", "inside for");
                            if (wr.id.equals(wineReview.id)) {
                                wineReviewArrayList.set(wineReviewArrayList.indexOf(wr), wineReview);
                                dbh.updateWineReview(fs, this, wineReview);
                                dbh.getWineReviews(fs, this, user);
                                if (wineReview.pictureFilePath != null) {
                                    //keep receiving task is not yet complete
                                    //uploadFile(wineReview);
                                }

                                break;
                            }
                        }

                    }
                    else {
                        Log.d("***DEBUG***", "WineReview JSON NULL");
                    }
                }
                else if(requestCode == COMPARE) {
                    //TODO process array of barcodes to compare

                }
                else if(requestCode == SINGLE_ENTRY) {
                    Bundle bundle = data.getExtras();
                    if(bundle != null) {
                        Log.d("***Debug***", "Bundle: " + bundle);
                        BarcodeWine bcWine = (BarcodeWine) data.getSerializableExtra("barcodeWine");
                        Log.d("***Debug***", "bcWine" + bcWine);
                        Log.d("***Debug***", "bcWine.upc " + bcWine.upc);
                        if (bcWine != null) {
                            //call newwineentry
                            Intent i = new Intent(this, NewWineEntryActivity.class);
                            i.putExtra("barcodeWine", bcWine);
                            i.putExtra("userData", user);
                            startActivityForResult(i, NEW_WR);
                        }
                    }
                }
            }
        }
        else {
            //result code not OK
            Log.d("***Debug***", "Result code not OK");

        }
        //reload the wines
        dbh.getWineReviews(fs, this, user);
    }

    @Override
    public void onBackPressed() {
        Log.d("***Debug***", "inside LibraryActivity: onBackPressed");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("***Debug***", "inside LibraryActivity: onCreateOptionsMenu");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_library, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("***Debug***", "inside LibraryActivity: onOptionsItemSelected");
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.action_signout) {
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Log.d("***Debug***", "inside LibraryActivity: onNavigationItemSelected");
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_add_wine) {
            Intent intent = new Intent(this, NewWineEntryActivity.class);
            startActivityForResult(intent, NEW_WR);
        }
        else if (id == R.id.nav_add_wine_by_barcode) {
            Intent intent = new Intent(this, BarcodeScanner.class);
            intent.putExtra("requestCode", SINGLE_ENTRY);
            startActivityForResult(intent, SINGLE_ENTRY);
        }
        else if (id == R.id.nav_compare_wine) {
            Intent intent = new Intent(this, BarcodeScanner.class);
            startActivityForResult(intent, COMPARE);

        }
        else if (id == R.id.nav_library) {
            loadWineReviews();
        }
        else if (id == R.id.nav_share) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    //Create the card to go in the list for display
    /**
     * Used when restoring a barcodeWine review from deletion
     *
     */
    public void reAddWineReview(WineReview wr) {
        Log.d("***Debug***", "inside LibraryActivity: reAddWineReview");
        //Establish layout
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout scrollContainer = findViewById(R.id.content_library_container);
        View wineCard = inflater.inflate(R.layout.wine_card, null, false);

        //add tag for data matching to pick out of list
        wineCard.setTag(wr);

        //add the remove card listener
        TextView remove = (TextView) wineCard.findViewById(R.id.tvRemove);
        remove.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onClickWineCardRemove(v);
            }
        });

        //initialize the layout fields
        TextView wineName = wineCard.findViewById(R.id.tvWineName);
        RatingBar wineRating = wineCard.findViewById(R.id.ratingBar);
        TextView wineDesc = wineCard.findViewById(R.id.tvWine_Desc);


        ImageView winePicture = wineCard.findViewById(R.id.ivWine_Picture);

        //TODO: Implement community rating system
        TextView wineRatingCount = wineCard.findViewById(R.id.tvRatingsCount);
        TextView winePts = wineCard.findViewById(R.id.tvWinePoints);
        TextView wineRatingSource = wineCard.findViewById(R.id.tvWine_Source);

        //set values for winecard layout fields
        String wineTitle = wr.name + " - " + wr.maker;
        wineName.setText(wineTitle);
        wineRating.setRating(wr.rating);
        wineDesc.setText(wr.description);

        //null out unused fields atm
        wineRatingCount.setText("");
        winePts.setText("");
        wineRatingSource.setText("");

        winePicture.setImageDrawable(new BitmapDrawable(getResources(), wr.imageBitmap));

        //add wineCard to the scroll view
        scrollContainer.addView(wineCard);

        //create the review for cloud database
        dbh.createWineReview(fs, this, wr, user);

        //sort and add to local list

        wineReviewArrayList.add(wr);
        Toast.makeText(this, "Restored " + wr.name, Toast.LENGTH_SHORT).show();
        loadWineReviews();
    }

    public void loadWineReviews() {
        Log.d("***Debug***", "inside loadWineReviews");

        try {
            Bitmap myBitmap;

            //redraw view
            LayoutInflater inflater = LayoutInflater.from(this);
            LinearLayout scrollContainer = findViewById(R.id.content_library_container);
            scrollContainer.removeAllViews();

            if(wineReviewArrayList.size() < 1) {
                View placeholder = inflater.inflate(R.layout.placeholder_text, null, false);
                scrollContainer.addView(placeholder);
            }
            else {
                wineReviewArrayList = sortByRatingDescending(wineReviewArrayList);
            }

            for(WineReview wr : wineReviewArrayList) {

                if(wr.pictureFilePath != null) {
                    myBitmap = BitmapFactory.decodeFile(wr.pictureFilePath);
                    wr.imageBitmap = myBitmap;
                }

                //setup layout stuff
                View wineCard = inflater.inflate(R.layout.wine_card, null, false);
                wineCard.setPadding(0,0,0, 10);
                wineCard.setTag(wr);

                TextView remove = wineCard.findViewById(R.id.tvRemove);
                remove.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        onClickWineCardRemove(v);
                    }
                });

                //initialize the layout fields
                TextView wineName = wineCard.findViewById(R.id.tvWineName);
                ImageView winePicture = wineCard.findViewById(R.id.ivWine_Picture);
                RatingBar wineRating = wineCard.findViewById(R.id.ratingBar);
                TextView wineDesc =  wineCard.findViewById(R.id.tvWine_Desc);
                ImageView shareReview = wineCard.findViewById(R.id.iv_share_active);

                //TODO: Implement rating system
                TextView wineRatingCount = wineCard.findViewById(R.id.tvRatingsCount);
                TextView winePts = wineCard.findViewById(R.id.tvWinePoints);
                TextView wineRatingSource = wineCard.findViewById(R.id.tvWine_Source);

                //set values of imported components
                wineName.setText((wr.name + " - " + wr.maker));

                wineDesc.setText(wr.description);

                if(wr.shareReview) {
                    shareReview.setVisibility(View.VISIBLE);
                }
                else {
                    shareReview.setVisibility(View.INVISIBLE);
                }

                //placeholders until set later by a callback,
                wineRating.setRating(0);
                wineRatingCount.setText("N/A");

                //unimplemented fields, sources would be web based
                winePts.setText("");
                wineRatingSource.setText("");

                if(wr.imageBitmap != null) {
                    winePicture.setImageDrawable(new BitmapDrawable(getResources(), wr.imageBitmap));
                }

                //add Wine card to scroll view
                scrollContainer.addView(wineCard);

            }
        }
        catch(Exception e) {
            Log.d("***DEUBUG***", "ERROR" + e.getMessage());
        }

    }

    public void onClickHelpShare(View v) {
        AlertDialogBuilder adb = new AlertDialogBuilder();
        adb.createNewAlertDialog(this, "Help", "This review is being shared.");
    }

    public void onClickWineCardRemove(View v) {
        Log.d("***Debug***", "inside LibraryActivity: onClickWineCardRemove");

        int indexToRemove = 999;

        try {
            ViewGroup wineCard = (ViewGroup) v.getParent();
            for (WineReview wr : wineReviewArrayList) {
                if (wr == wineCard.getTag()) {
                    indexToRemove = wineReviewArrayList.indexOf(wr);
                    ((ViewManager) wineCard.getParent()).removeView(wineCard);
                    String message = "Removed " + wr.name;

                    showSnackBar(findViewById(R.id.content_library_container), message, 7000, wr);
                }
            }
        } catch(Exception e) {
            Log.d("***ERROR***", "onClickWineCardRemove: " + e.getMessage());
            Log.d("***ERROR***", "onClickWineCardRemove: " + e);
            Toast.makeText(this, "Error Occurred", Toast.LENGTH_SHORT).show();

        }
        if(indexToRemove != 999) {
            dbh.deleteWineReview(fs, this, wineReviewArrayList.get(indexToRemove));
            wineReviewArrayList.remove(indexToRemove);
        }
    }

    public void showSnackBar(View view, String message, int duration, WineReview wr) {
        Log.d("***Debug***", "inside LibraryActivity: showSnackBar");
        final Snackbar undoSnackBar = Snackbar.make(view, message, duration);
        final WineReview wineReview = wr;
        undoSnackBar.setAction("Undo", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restoreWineReview(wineReview);
                undoSnackBar.dismiss();
            }
        });
        undoSnackBar.show();
    }

    public void restoreWineReview(WineReview wr) {
        Log.d("***Debug***", "inside LibraryActivity: restoreWineReview");
        reAddWineReview(wr);
    }

    public ArrayList<WineReview> sortByRatingDescending(ArrayList<WineReview> wineReviewArrayList) {
        Log.d("***Debug***", "inside LibraryActivity: sortByRatingDescending");

        Object[] wrArray = wineReviewArrayList.toArray();
        ArrayList<WineReview> sortedArrayList = new ArrayList<WineReview>();

        WineReview temp;

        //sort descending based on highest rating
        for(int j = 0; j < wrArray.length; j++) {
            for(int i = 0; i < wrArray.length; i++) {
                if(wrArray.length > 1) {
                    WineReview curr = (WineReview) wrArray[i];

                    if((i + 1) != wrArray.length) {

                        WineReview next = (WineReview) wrArray[i + 1];
                        if(curr.rating < next.rating) {
                            temp = next;
                            wrArray[i + 1] = curr;
                            wrArray[i] = temp;
                        }
                    }
                }
            }
        }

        //convert to arraylist
        for(Object wr : wrArray) {
            sortedArrayList.add((WineReview) wr);
        }
        return sortedArrayList;
    }
    public void onClickWineCardEdit(View v) {
        Log.d("***Debug***", "inside LibraryActivity: onClickWineCardEdit");

        try {
            ViewGroup wineCard = (ViewGroup) v.getParent();
            for (WineReview wr : wineReviewArrayList) {
                if (wr == wineCard.getTag()) {
                    Gson gson = new Gson();
                    String wineReviewJSON = gson.toJson(wr);
                    Intent intent = new Intent(this, NewWineEntryActivity.class);
                    intent.putExtra("edit", wineReviewJSON);
                    intent.putExtra("userData", user);
                    startActivityForResult(intent, EDIT_WR);
                }
            }
        } catch(Exception e) {
            Log.d("***ERROR***", "onClickWineCardRemove: " + e.getMessage());
            Log.d("***ERROR***", "onClickWineCardRemove: " + e);
            Toast.makeText(this, "Error Occurred", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickWineCardDetails(View v) {
        Log.d("***Debug***", "inside LibraryActivity: onClickWineCardDetails");

        try {
            ViewGroup wineCard = (ViewGroup) v.getParent();
            for (WineReview wr : wineReviewArrayList) {
                if (wr == wineCard.getTag()) {
                    Gson gson = new Gson();
                    String wineReviewJSON = gson.toJson(wr);
                    Intent intent = new Intent(this, CardDetailsActivity.class);
                    Bundle extras = new Bundle();
                    extras.putParcelable("user", user);
                    extras.putString("details", wineReviewJSON);
                    intent.putExtras(extras);
                    //startActivityForResult(intent, EDIT_WR);
                    startActivity(intent);
                }
            }
        } catch(Exception e) {
            Log.d("***ERROR***", "onClickWineCardRemove: " + e.getMessage());
            Log.d("***ERROR***", "onClickWineCardRemove: " + e);
            Toast.makeText(this, "Error Occurred", Toast.LENGTH_SHORT).show();
        }
    }

    //Calback from DBH
    public void setWineReviewArrayList(ArrayList<WineReview> list) {
        Log.d("***Debug***", "inside setWineReviewArrayList");
        wineReviewArrayList = list;
        for(WineReview wr : wineReviewArrayList) {
            dbh.getRatingsForWineReview(fs, this, wr);
        }
    }

    //Callback from DBH
    public void setRatingsForWineReview(WineReview wr, ArrayList<WineReview> list) {
        Log.d("***Debug***", "inside setRatingsForWineReviewArrayList");

        if(list != null) {
            wr.ratingCount = list.size();
            float temp = 0;
            for(WineReview w : list) {
                temp += w.rating;
            }
            wr.avgRating = temp / wr.ratingCount;
        }

        //redraw the specific review
        updateWineCardView(wr);
    }

    public void updateWineCardView(WineReview wr) {
        Log.d("***Debug***", "inside updateWineCardView");

        int index = 0;

        LinearLayout scrollContainer = findViewById(R.id.content_library_container);

        index = wineReviewArrayList.indexOf(wr);

        if(index >= 0) {
            View card = scrollContainer.getChildAt(index);

            TextView txtRatingsCount = card.findViewById(R.id.tvRatingsCount);
            RatingBar rbAvgRating = card.findViewById(R.id.ratingBar);

            String count = "" + wr.ratingCount;

            txtRatingsCount.setText(count);
            rbAvgRating.setRating(wr.avgRating);
        }
        else {
            Log.d("***DEBUG***", "Wine Object Not Found");
        }
    }

    public void saveDocReference(String docRef, WineReview wineReview) {
        Log.d("***Debug***", "inside saveDocReference");
        for(WineReview wr : wineReviewArrayList) {
            if(wr.id.equals(wineReview.id)) {
                //update to save the docId to cloud
                wr.docId = docRef;
                dbh.updateWineReview(fs, this, wr);
                return;
            }
        }
    }

    public void uploadFile(final WineReview wineReview) {
        Log.d("***Debug***", "inside uploadFile");
        Uri fileURI = Uri.fromFile(new File(wineReview.pictureFilePath));
        final StorageReference ref = storageRef.child(fileURI.toString());
        Log.d("***Debug***", "fileURI: " + fileURI);
        Log.d("***Debug***", "pictureFilePath: " + wineReview.pictureFilePath);
        ref.putFile(fileURI)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        // Get a URL to the uploaded content
                        Uri downloadUrl = ref.getDownloadUrl().getResult();
                        wineReview.imageURL = downloadUrl.toString();
                        //Store the download URL as the barcodeWine review image data

                        Toast.makeText(ctx, "Successfully uploaded File " + wineReview.name, Toast.LENGTH_SHORT).show();
                        dbh.updateWineReview(fs, ctx, wineReview);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle unsuccessful uploads
                        Log.d("***ERROR***", "Failed to download File " + e.getMessage());
                        Toast.makeText(ctx, "Failed to upload File ", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void downloadFile(WineReview wineReview) {
        Log.d("***Debug***", "inside downloadFile");
        try {
            File localFile = File.createTempFile("images", "jpg");

            StorageReference ref = storageRef.child(wineReview.imageURL);
            ref.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            // Successfully downloaded data to local file
                            // ...

                            Toast.makeText(ctx, "Successfully downloaded ", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle failed download
                    // ...

                    Toast.makeText(ctx, "Failed to download ", Toast.LENGTH_SHORT).show();
                }
            });
        }
        catch (Exception e) {
            Log.d("***ERROR***", "Failed to download File " + e.getMessage());
        }


    }
    public void createPictureStorage() {
        Log.d("***Debug***", "inside createPictureStorage");
        try {
            // create a File object for the parent directory

            if(!wineScannerImagesDirectory.exists()) {
                // have the object build the directory structure, if needed.
                if(!wineScannerImagesDirectory.mkdirs()) {
                    Log.d("***DEBUG***", "Problem creating barcodeWine scanner directory");
                }
                else {
                    Log.d("***DEBUG***", "Success creating barcodeWine scanner directory");
                }
            }
        }
        catch(Exception e) {
            Log.d("***ERROR***", "" + e.getMessage());
        }

    }

}
