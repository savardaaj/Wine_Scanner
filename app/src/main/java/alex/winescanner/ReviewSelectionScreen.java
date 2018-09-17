package alex.winescanner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ReviewSelectionScreen extends AppCompatActivity {

    private Button btnMore;
    private Wine wine;
    private ArrayList<Wine> wineList;
    private int wineCount;

    private ViewGroup mConstraintLayout;

    private void init() {
        Log.d("3", "Inside reviewselectionscreen init");
        wineCount = 0;
        wineList = new ArrayList<>();
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

        getWineDataFromIntent();

        addLayout();

        //ConstraintLayout currentLayout = findViewById(R.id.layoutReviewMain);

        //createWineCard(currentLayout.getContext());

//        if(wineList.size() > 0) {
//            for(int i = 0; i < wineCount; i++) {
//                wine = wineList.get(i);
//                createWineCard(currentLayout.getContext());
//            }
//        }
//        else {
//            Log.d("wineListEmpty", "Wine List is empty");
//        }

    }

    @SuppressWarnings("unchecked")
    private void getWineDataFromIntent() {
        Log.d("5", "Inside reviewselectionscreen getWineDataFromIntent");
        try {
            Intent intent = getIntent();
            wineList = (ArrayList<Wine>) intent.getSerializableExtra("WineList");
            wineCount = intent.getIntExtra("wineCount", 0);
        } catch(Exception e) {

        }

    }

    private void createWineCard(Context context) {
        Log.d("6", "Inside reviewselectionscreen createWineCard");
        //LayoutInflater li = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        WineCardComponent wcc = new WineCardComponent(context);

        //TODO: Create wine_component. Populate with wine data
        //LayoutInflater li = getLayoutInflater();
        //View v = li.inflate(R.layout.wine_card_component, null);

        //View customView = v.findViewById(R.id.wine_card_component);
        //customView.findViewById(R.id.tvWineName).getContext().

        //Populate winecard with data
        //TextView tv = (TextView) v.findViewById(R.id.tvWineName);
        //tv.setText(wine.getTitle());

       // ViewGroup viewGroup = (ViewGroup) findViewById(R.id.layoutReviewMain);
        //viewGroup.addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

    }

    private void addLayout() {
        Log.d("8", "inside reviewselectionscreen addlayout");
        View layout2 = LayoutInflater.from(this).inflate(R.layout.wine_card_component, mConstraintLayout, false);

        //Create all components of imported component
        TextView tvWineName = (TextView) layout2.findViewById(R.id.tvWineName);

        //set values of imported components
        tvWineName.setText(R.string.wine_title2);

        this.setContentView(layout2);
    }
}
