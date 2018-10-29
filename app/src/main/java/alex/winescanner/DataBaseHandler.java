package alex.winescanner;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.support.constraint.Constraints.TAG;

public class DataBaseHandler {

    public String docId;
    ArrayList<WineReview> wineReviewArrayList;

    public void createWineReview(FirebaseFirestore db, final Context context, final WineReview wr, FirebaseUser user) {
        Log.d("***DEBUG***", "inside CreateWineReview");

        final LibraryActivity LA = (LibraryActivity) context;

        wr.userUUID = user.getUid();

        // Create a new user with a first and last name
        final Map<String, Object> wineReviewMap = new HashMap<>();
        wineReviewMap.put("id", wr.getId());
        wineReviewMap.put("userUUID", wr.userUUID);
        wineReviewMap.put("barcode", wr.barcode);
        wineReviewMap.put("name", wr.name);
        wineReviewMap.put("maker", wr.maker);
        wineReviewMap.put("type", wr.type);
        wineReviewMap.put("year", wr.year);
        wineReviewMap.put("location", wr.location);
        wineReviewMap.put("description", wr.description);
        wineReviewMap.put("shareReview", wr.shareReview);
        wineReviewMap.put("pictureFilePath", wr.pictureFilePath);
        wineReviewMap.put("imageURL", wr.imageURL);
        wineReviewMap.put("rating", wr.rating);
        wineReviewMap.put("likes", wr.likes);

        // Add a new document with a generated ID
        db.collection("WineReviews")
                .add(wineReviewMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("***DEBUG***", "Document added with ID: " + documentReference.getId());
                        //Store the docReference
                        LA.saveDocReference(documentReference.getId(), wr);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    public void getWineReviews(FirebaseFirestore db, final Context context, FirebaseUser user) {
        Log.d("***DEBUG***", "inside getWineReviews");

        final LibraryActivity LA = (LibraryActivity) context;

        final ArrayList<WineReview> wineReviewList = new ArrayList<WineReview>();
        //Retrieve all wine reviews from this user
        db.collection("WineReviews").whereEqualTo("userUUID", user.getUid())
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
                            LA.setWineReviewArrayList(wineReviewList);
                            LA.loadWineReviews();
                        } else {
                            Toast.makeText(context, "Error Occurred retrieving reviews", Toast.LENGTH_SHORT).show();
                            Log.d("***ERROR***", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void getRatingsForWineReview(FirebaseFirestore db, final Context context, final WineReview wineReview) {
        Log.d("***DEBUG***", "inside getRatingsForWineReviews");

        final LibraryActivity LA = (LibraryActivity) context;

        final ArrayList<WineReview> ratingsForWineReview = new ArrayList<WineReview>();
        //Retrieve all wine reviews from this user
        db.collection("WineReviews").whereEqualTo("barcode", wineReview.barcode).whereGreaterThan("rating", 0)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("***DEBUG***", document.getId() + " => " + document.getData());
                                WineReview wr = document.toObject(WineReview.class);
                                ratingsForWineReview.add(wr);
                            }
                            //set local list with list returned
                            LA.setRatingsForWineReview(wineReview, ratingsForWineReview);
                            //LA.loadRatings();
                        } else {
                            Toast.makeText(context, "Error Occurred retrieving reviews", Toast.LENGTH_SHORT).show();
                            Log.d("***ERROR***", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void updateWineReview( FirebaseFirestore db, final Context context, final WineReview wr) {
        Log.d("***Debug***", "inside updateWineReview");
        // Create a new user with a first and last name
        final Map<String, Object> wineReviewMap = new HashMap<>();

        final LibraryActivity LA = (LibraryActivity) context;

        try {
            wineReviewMap.put("id", wr.getId());
            wineReviewMap.put("userUUID", wr.userUUID);
            wineReviewMap.put("barcode", wr.barcode);
            wineReviewMap.put("name", wr.name);
            wineReviewMap.put("maker", wr.maker);
            wineReviewMap.put("type", wr.type);
            wineReviewMap.put("year", wr.year);
            wineReviewMap.put("location", wr.location);
            wineReviewMap.put("description", wr.description);
            wineReviewMap.put("shareReview", wr.shareReview);
            wineReviewMap.put("pictureFilePath", wr.pictureFilePath);
            wineReviewMap.put("imageURL", wr.imageURL);
            wineReviewMap.put("rating", wr.rating);
            wineReviewMap.put("docId", wr.docId);
            wineReviewMap.put("likes", wr.likes);

            // Add a new document with a generated ID
            db.collection("WineReviews").document(wr.docId)
                    .update(wineReviewMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("***Debug***", "DocumentSnapshot successfully updated!");
                            Toast.makeText(context, "Updated " + wr.name, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("***Debug***", "Error updating document", e);
                            Toast.makeText(context, "Failed to update " + wr.name, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        catch (Exception e) {
            Log.w("***Debug***", "Error updating document", e);
            Toast.makeText(context, "Failed to update " + wr.name, Toast.LENGTH_SHORT).show();
        }

    }

    public void updateCommentLikes(FirebaseFirestore db, Context ctx, WineReview wr) {
        Log.d("***Debug***", "inside updateWineReview");
        // Create a new user with a first and last name
        final Map<String, Object> wineReview = new HashMap<>();

        final CardDetailsActivity cda = (CardDetailsActivity) ctx;

        try {
            wineReview.put("likes", wr.likes);

            // Add a new document with a generated ID
            db.collection("WineReviews").document(wr.docId)
                    .update(wineReview)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            cda.loadComments();
                            Log.d("***Debug***", "DocumentSnapshot successfully updated!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("***Debug***", "Error updating document", e);
                        }
                    });
        }
        catch (Exception e) {
            Log.w("***Debug***", "Error updating document", e);
        }

    }

    public void queryWineReviews(FirebaseFirestore db, final Context context, WineReview wr) {
        Log.d("***Debug***", "inside queryWineReview");

        final CardDetailsActivity cda = (CardDetailsActivity) context;

        if(wr.barcode != null) {
            db.collection("WineReviews").whereEqualTo("barcode", wr.barcode)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                ArrayList<WineReview> commentsArrayList = new ArrayList<WineReview>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("***DEBUG***", document.getId() + " => " + document.getData());
                                    WineReview wr = document.toObject(WineReview.class);
                                    commentsArrayList.add(wr);
                                }
                                cda.setCommentsArrayList(commentsArrayList);
                                cda.loadComments();
                            } else {
                                Log.d("***ERROR***", "Error getting documents.", task.getException());
                            }
                        }
                    });
        }
    }

    public void searchReviewsByBarcode(FirebaseFirestore db, final Context context, String barcode) {
        Log.d("***Debug***", "inside searchReviewsByBarcode");

        final NewWineEntryActivity nwa = (NewWineEntryActivity) context;

        if(barcode != null) {
            db.collection("WineReviews").whereEqualTo("barcode", barcode)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                ArrayList<WineReview> reviewArrayList = new ArrayList<WineReview>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("***DEBUG***", document.getId() + " => " + document.getData());
                                    WineReview wr = document.toObject(WineReview.class);
                                    reviewArrayList.add(wr);
                                }
                                nwa.populateFromUserReview(reviewArrayList);
                            } else {
                                Log.d("***ERROR***", "Error getting documents.", task.getException());
                            }
                        }
                    });
        }
    }

    public void deleteWineReview(FirebaseFirestore db, final Context context, final WineReview wineReview) {
        Log.d("***Debug***", "inside deleteWineReview");

        final LibraryActivity LA = (LibraryActivity) context;

        // Add a new document with a generated ID
        db.collection("WineReviews").document(wineReview.docId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        Toast.makeText(context, "Successfully deleted " + wineReview.name, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                        Toast.makeText(context, "Failed to delete " + wineReview.name, Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
