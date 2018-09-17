package alex.winescanner;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
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
    private StorageReference storageRef;

    FirebaseFirestore fs;
    DataBaseHandler dbh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("***Debug***", "inside LibraryActivity: onCreate");
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);

        dbh = new DataBaseHandler();

        fs = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
        setContentView(R.layout.activity_library);

        wineReviewArrayList = new ArrayList<>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //calls load wine reviews after successful get
        getWineReviews(fs);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("***Debug***", "inside LibraryActivity: onActivityResult");

        if (resultCode == RESULT_OK) {
            Log.d("***Debug***", "onActivityResult: result Code: " + resultCode);
            Log.d("***Debug***", "onActivityResult: request Code: " + requestCode);
            String wineReviewJSON = data.getStringExtra("wineReviewJSON");
            if (wineReviewJSON != null) {
                WineReview wineReview = new Gson().fromJson(wineReviewJSON, WineReview.class);
                if (requestCode == NEW_WR) {
                    wineReviewArrayList.add(wineReview);
                    uploadFile(wineReview);
                    createWineReview(wineReview, fs);
                }
                else if (requestCode == EDIT_WR) {
                    for(WineReview wr : wineReviewArrayList) {
                        if(wr.id.equals(wineReview.id)) {
                            wineReviewArrayList.set(wineReviewArrayList.indexOf(wr), wineReview);

                            updateWineReview(wineReview, fs);
                            break;
                        }

                    }

                }
            }
        }
        loadWineReviews();
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

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Log.d("***Debug***", "inside LibraryActivity: onNavigationItemSelected");
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent intent = new Intent(this, NewWineEntryActivity.class);
            startActivityForResult(intent, NEW_WR);

        } else if (id == R.id.nav_library) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    //Create the card to go in the list for display
    /**
     * Used when restoring a wine review from deletion
     *
     */
    public void reAddWineReview(WineReview wr) {
        Log.d("***Debug***", "inside LibraryActivity: reAddWineReview");
        //Establish layout
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout scrollContainer = findViewById(R.id.content_library_container);
        View wineCard = inflater.inflate(R.layout.wine_card_component, null, false);

        //add tag for data matching to pick out of list
        wineCard.setTag(wr);

        //add the remove card listener
        TextView remove = (TextView) wineCard.findViewById(R.id.tvRemove);
        remove.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onWineCardRemove(v);
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
        createWineReview(wr, fs);

        //sort and add to local list

        wineReviewArrayList.add(wr);
        loadWineReviews();
    }

    public void loadWineReviews() {
        Log.d("***Debug***", "inside loadWineReviews");

        File pictureFile;
        Bitmap myBitmap;

        //redraw view
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout scrollContainer = findViewById(R.id.content_library_container);
        scrollContainer.removeAllViews();

        wineReviewArrayList = sortByRatingDescending(wineReviewArrayList);

        for(WineReview wr : wineReviewArrayList) {

            if(wr.filePath != null) {
                pictureFile = new File(wr.filePath);
                myBitmap = BitmapFactory.decodeFile(pictureFile.getAbsolutePath());
                wr.imageBitmap = myBitmap;
            }

            //setup layout stuff
            View wineCard = inflater.inflate(R.layout.wine_card_component, null, false);
            wineCard.setPadding(0,0,0, 10);
            wineCard.setTag(wr);

            TextView remove = wineCard.findViewById(R.id.tvRemove);
            remove.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    onWineCardRemove(v);
                }
            });

            //initialize the layout fields
            TextView wineName = wineCard.findViewById(R.id.tvWineName);
            ImageView winePicture = wineCard.findViewById(R.id.ivWine_Picture);
            RatingBar wineRating = wineCard.findViewById(R.id.ratingBar);
            TextView wineDesc =  wineCard.findViewById(R.id.tvWine_Desc);

            //TODO: Implement rating system
            TextView wineRatingCount = wineCard.findViewById(R.id.tvRatingsCount);
            TextView winePts = wineCard.findViewById(R.id.tvWinePoints);
            TextView wineRatingSource = wineCard.findViewById(R.id.tvWine_Source);

            //set values for layout fields
            String wineTitle = wr.name + " - " + wr.maker;

            //set values of imported components
            wineName.setText(wineTitle);
            wineRating.setRating(wr.rating);
            wineDesc.setText(wr.description);

            //null out unused fields atm
            wineRatingCount.setText("");
            winePts.setText("");
            wineRatingSource.setText("");

            winePicture.setImageDrawable(new BitmapDrawable(getResources(), wr.imageBitmap));

            //add wine card to scroll view
            scrollContainer.addView(wineCard);
        }

    }

    public void onWineCardRemove(View v) {
        Log.d("***Debug***", "inside LibraryActivity: onWineCardRemove");

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
            Log.d("***ERROR***", "onWineCardRemove: " + e.getMessage());
            Log.d("***ERROR***", "onWineCardRemove: " + e);
            Toast.makeText(this, "Error Occurred", Toast.LENGTH_SHORT).show();

        }
        if(indexToRemove != 999) {
            deleteWineReview(wineReviewArrayList.get(indexToRemove), fs);
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
        wineReviewArrayList.add(wr);
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
    public void onWineCardEdit(View v) {
        Log.d("***Debug***", "inside LibraryActivity: onWineCardEdit");

        try {
            ViewGroup wineCard = (ViewGroup) v.getParent();
            for (WineReview wr : wineReviewArrayList) {
                if (wr == wineCard.getTag()) {
                    Gson gson = new Gson();
                    String wineReviewJSON = gson.toJson(wr);
                    Intent intent = new Intent(this, NewWineEntryActivity.class);
                    intent.putExtra("edit", wineReviewJSON);
                    startActivityForResult(intent, EDIT_WR);
                }
            }
        } catch(Exception e) {
            Log.d("***ERROR***", "onWineCardRemove: " + e.getMessage());
            Log.d("***ERROR***", "onWineCardRemove: " + e);
            Toast.makeText(this, "Error Occurred", Toast.LENGTH_SHORT).show();
        }
    }

    public void onWineCardDetails(View v) {
        Log.d("***Debug***", "inside LibraryActivity: onWineCardDetails");

        try {
            ViewGroup wineCard = (ViewGroup) v.getParent();
            for (WineReview wr : wineReviewArrayList) {
                if (wr == wineCard.getTag()) {
                    Gson gson = new Gson();
                    String wineReviewJSON = gson.toJson(wr);
                    Intent intent = new Intent(this, CardDetailsActivity.class);
                    intent.putExtra("edit", wineReviewJSON);
                    //startActivityForResult(intent, EDIT_WR);
                    startActivity(intent);
                }
            }
        } catch(Exception e) {
            Log.d("***ERROR***", "onWineCardRemove: " + e.getMessage());
            Log.d("***ERROR***", "onWineCardRemove: " + e);
            Toast.makeText(this, "Error Occurred", Toast.LENGTH_SHORT).show();
        }
    }

    public void getWineReviews(FirebaseFirestore db) {

        final ArrayList<WineReview> wineReviewList = new ArrayList<WineReview>();
        db.collection("WineReviews")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("***DEBUG***", document.getId() + " => " + document.getData());
                                WineReview wr = document.toObject(WineReview.class);
                                wineReviewList.add(wr);
                            }
                            //set local list with list returned
                            wineReviewArrayList = wineReviewList;
                            loadWineReviews();
                        } else {
                            Toast.makeText(ctx, "Error Occurred retrieving reviews", Toast.LENGTH_SHORT).show();
                            Log.d("***ERROR***", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void createWineReview(final WineReview wr, FirebaseFirestore db) {
        Log.d("***DEBUG***", "inside reAddWineReview");

        String docId;

        // Create a new user with a first and last name
        final Map<String, Object> wineReview = new HashMap<>();
        wineReview.put("id", wr.getId());
        wineReview.put("name", wr.name);
        wineReview.put("maker", wr.maker);
        wineReview.put("type", wr.type);
        wineReview.put("year", wr.year);
        wineReview.put("location", wr.location);
        wineReview.put("description", wr.description);
        wineReview.put("filePath", wr.filePath);
        wineReview.put("imageURL", wr.imageURL);
        wineReview.put("rating", wr.rating);

        // Add a new document with a generated ID
        db.collection("WineReviews")
                .add(wineReview)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("***DEBUG***", "Document added with ID: " + documentReference.getId());
                        //Store the docReference
                        saveDocReference(documentReference.getId(), wr);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    public void saveDocReference(String docRef, WineReview wineReview) {
        Log.d("***Debug***", "inside saveDocReference");
        for(WineReview wr : wineReviewArrayList) {
            if(wr.id.equals(wineReview.id)) {
                //update to save the docId to cloud
                wr.docId = docRef;
                updateWineReview(wr, fs);
                return;
            }
        }
    }

    public void updateWineReview(final WineReview wr, FirebaseFirestore db) {
        Log.d("***Debug***", "inside updateWineReview");
        // Create a new user with a first and last name
        final Map<String, Object> wineReviewMap = new HashMap<>();

        try {
            wineReviewMap.put("id", wr.id);
            wineReviewMap.put("docId", wr.docId);
            wineReviewMap.put("name", wr.name);
            wineReviewMap.put("maker", wr.maker);
            wineReviewMap.put("type", wr.type);
            wineReviewMap.put("year", wr.year);
            wineReviewMap.put("location", wr.location);
            wineReviewMap.put("description", wr.description);
            wineReviewMap.put("filePath", wr.filePath);
            wineReviewMap.put("imageURL", wr.imageURL);
            wineReviewMap.put("rating", wr.rating);

            // Add a new document with a generated ID
            db.collection("WineReviews").document(wr.docId)
                    .update(wineReviewMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("***Debug***", "DocumentSnapshot successfully updated!");
                            Toast.makeText(ctx, "Updated " + wr.name, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("***Debug***", "Error updating document", e);
                            Toast.makeText(ctx, "Failed to delete " + wr.name, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        catch (Exception e) {
            Log.w("***Debug***", "Error updating document", e);
        }

    }

    public void deleteWineReview(final WineReview wineReview, FirebaseFirestore db) {
        Log.d("***Debug***", "inside deleteWineReview");
        // Add a new document with a generated ID
        db.collection("WineReviews").document(wineReview.docId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        Toast.makeText(ctx, "Successfully deleted " + wineReview.name, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                        Toast.makeText(ctx, "Failed to delete " + wineReview.name, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void uploadFile(final WineReview wineReview) {
        Log.d("***Debug***", "inside uploadFile");
        Uri fileURI = Uri.fromFile(new File(wineReview.filePath));
        final StorageReference ref = storageRef.child(fileURI.toString());

        ref.putFile(fileURI)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        // Get a URL to the uploaded content
                        Uri downloadUrl = ref.getDownloadUrl().getResult();
                        wineReview.imageURL = downloadUrl.toString();
                        //Store the download URL as the wine review image data

                        Toast.makeText(ctx, "Successfully uploaded File " + wineReview.name, Toast.LENGTH_SHORT).show();
                        updateWineReview(wineReview, fs);
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
}
