package alex.winescanner;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

    public void addWineReview(WineReview wr, FirebaseFirestore db) {

        // Create a new user with a first and last name
        final Map<String, Object> wineReview = new HashMap<>();
        wineReview.put("id", wr.getId());
        wineReview.put("name", wr.name);
        wineReview.put("maker", wr.maker);
        wineReview.put("type", wr.type);
        wineReview.put("year", wr.year);
        wineReview.put("location", wr.location);
        wineReview.put("description", wr.description);
        wineReview.put("image", wr.image);
        wineReview.put("rating", wr.rating);

        // Add a new document with a generated ID
        db.collection("WineReviews")
            .add(wineReview)
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    //Store the docReference

                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error adding document", e);
                }
        });
    }

    public void deleteWineReview(WineReview wineReview, FirebaseFirestore db) {

        // Add a new document with a generated ID
        db.collection("WineReviews").document(wineReview.docId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }

    public void updateWineReview(WineReview wr, FirebaseFirestore db) {
        Log.d("***Debug***", "inside updateWineReview");
        // Create a new user with a first and last name
        final Map<String, Object> wineReview = new HashMap<>();

        try {
            wineReview.put("id", wr.id);
            wineReview.put("name", wr.name);
            wineReview.put("maker", wr.maker);
            wineReview.put("type", wr.type);
            wineReview.put("year", wr.year);
            wineReview.put("location", wr.location);
            wineReview.put("description", wr.description);
            wineReview.put("image", wr.image);
            wineReview.put("rating", wr.rating);

            // Add a new document with a generated ID
            db.collection("WineReviews").document(wr.docId)
                    .update(wineReview)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
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

}
