package alex.winescanner;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

public class ReviewSelectionScreen extends AppCompatActivity {

    private Button btnMore;
    private BarcodeWine barcodeWine;
    private ArrayList<BarcodeWine> barcodeWineList;
    private int wineCount;

    private ViewGroup mConstraintLayout;

    private void init() {
        Log.d("3", "Inside reviewselectionscreen init");
        wineCount = 0;
        barcodeWineList = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("4", "Inside reviewselectionscreen onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_selection_screen);

        btnMore = findViewById(R.id.btnMore);

        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mConstraintLayout = findViewById(R.id.layoutReviewMain);

        init();

        barcodeWineList = getWineDataFromIntent();
        if(barcodeWineList != null) {
            if(barcodeWineList.size() > 0) {
                //wines were scanned
                loadScannedWines(barcodeWineList);
            }
        }
        else {
            Log.d("***DEBUG***", "Unable to retrieve any data from intent");
        }
    }

    @SuppressWarnings("unchecked")
    private ArrayList<BarcodeWine> getWineDataFromIntent() {
        Log.d("5", "Inside reviewselectionscreen getWineDataFromIntent");

        ArrayList<BarcodeWine> bcw = new ArrayList<BarcodeWine>();

        try {
            Intent intent = getIntent();
            bcw = (ArrayList<BarcodeWine>) intent.getSerializableExtra("WineList");
        }
        catch(Exception e) {
            Log.d("***DEBUG***", "Exception occurred: " + e.getMessage());
            return null;
        }
        return bcw;
    }

    public void loadScannedWines(ArrayList<BarcodeWine> barcodeWineList) {
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout scrollContainer = findViewById(R.id.content_review_container);
        scrollContainer.removeAllViews();

        View placeholder = inflater.inflate(R.layout.placeholder_text, null, false);
        scrollContainer.addView(placeholder);

        for(BarcodeWine br : barcodeWineList) {

            //setup layout stuff
            View wineCard = inflater.inflate(R.layout.wine_card, null, false);
            wineCard.setPadding(0,0,0, 10);
            wineCard.setTag(br);

            TextView remove = wineCard.findViewById(R.id.tvRemove);
            remove.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                   //onClickWineCardRemove(v);
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
            wineName.setText((br.title + " - " + br.brand));
            //rbWineRating.setRating();
            wineDesc.setText(br.description);

            shareReview.setVisibility(View.INVISIBLE);

            //null out unused fields atm
            wineRatingCount.setText("");
            winePts.setText("");
            wineRatingSource.setText("");

            //winePicture.setImageDrawable(new BitmapDrawable(getResources(), wr.imageBitmap));

            //add barcodeWine card to scroll view
            scrollContainer.addView(wineCard);
        }
    }

    public void onClickViewDetails(View v) {
        //Open up card details activity

    }

    public void onClickChoose(View v) {
        //TBD how to handle this

    }

    public void onClickHoldSelect(View v) {
        //Noticeably mark the card as selected

    }
}
