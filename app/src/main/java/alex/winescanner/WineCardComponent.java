package alex.winescanner;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class WineCardComponent extends RelativeLayout {

    View rootView;
    TextView tvWineName;

    public WineCardComponent(Context context) {
        super(context);
        init(context);
    }

    public WineCardComponent(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        Log.d("7", "Inside WineCardComponent init");

        rootView = inflate(context, R.layout.wine_card, this);
        tvWineName = (TextView) rootView.findViewById(R.id.tvWineName);
        setWineNameText(R.string.wine_title2);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.wine_card, this);

        //minusButton = rootView.findViewById(R.id.minusButton);
        //plusButton = rootView.findViewById(R.id.plusButton);

        /*minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decrementValue(); //we'll define this method later
            }
        });

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incrementValue(); //we'll define this method later        }
            });
        }*/
    }

    //tvWineName.setText(R.string.wine_title2);
    public void setWineNameText(int id) {
        tvWineName.setText(id);
        //tvWineName.setText(R.string.wine_title2);
    }

}
