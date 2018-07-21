package alex.winescanner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("1", "Inside main oncreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //Called when user taps the Compare Wines button
    public void startCamera(View view) {
        //do something in response to button
        Intent intent = new Intent(this, AndroidCameraApi.class);
        startActivity(intent);
    }

    //Called when user taps the Compare Wines button
    public void scanBarcodes(View view) {
        Log.d("2", "Inside main scanBarcodes");
        //do something in response to button
        Intent intent = new Intent(this, BarcodeScanner.class);
        startActivity(intent);
    }

    public void viewLibrary(View view) {
        Log.d("2", "Inside main viewLibrary");
        //do something in response to button
        Intent intent = new Intent(this, LibraryActivity.class);
        startActivity(intent);
    }

}
