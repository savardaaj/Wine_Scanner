package alex.winescanner;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

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
    ArrayList<WineReview> wineReviewArrayList;

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
            //wineReview.put("image", wr.image);
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

    private void queryWineReviews(FirebaseFirestore db, WineReview wr) {

        wineReviewArrayList = new ArrayList<WineReview>();
        db.collection("WineReviews").whereEqualTo("barcode", wr.barcode)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("***DEBUG***", document.getId() + " => " + document.getData());
                                WineReview wr = document.toObject(WineReview.class);
                                wineReviewArrayList.add(wr);
                            }
                            returnWineReviews();
                        } else {
                            Log.d("***ERROR***", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void getWineReviews(FirebaseFirestore db, WineReview wr) {
        queryWineReviews(db, wr);
    }

    public List<WineReview> returnWineReviews() {
        return wineReviewArrayList;
    }

}
