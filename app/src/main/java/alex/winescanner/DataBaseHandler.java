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
        wineReview.put("Name", wr.getWineName());
        wineReview.put("Maker", wr.getWineMaker());
        wineReview.put("Type", wr.getWineType());
        wineReview.put("Location", wr.getWineLocation());
        wineReview.put("Description", wr.getWineDescription());
        wineReview.put("Image", wr.getWineImage());
        wineReview.put("Rating", wr.getRating());

        // Add a new document with a generated ID
        db.collection("users")
            .add(wineReview)
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error adding document", e);
                }
        });
    }

    public void removeWineReview(WineReview wineReview, FirebaseFirestore db) {

        // Add a new document with a generated ID
        db.collection("winereviews").document(wineReview.docId)
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

    public ArrayList<WineReview> getWineReviews(FirebaseFirestore db) {

        final ArrayList<WineReview> wineReviewList = new ArrayList<WineReview>();
        db.collection("winereviews")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                WineReview wr = (WineReview) document.getData();
                                wineReviewList.add(wr);
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        return wineReviewList;
    }
}
