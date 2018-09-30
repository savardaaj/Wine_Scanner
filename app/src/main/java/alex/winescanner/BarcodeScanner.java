package alex.winescanner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

public class BarcodeScanner extends AppCompatActivity {

    Button scan;
    Button goBack;
    Button reviewSelection;

    ScanDataHandler scanDataHandler;
    BarcodeWine barcodeWine;
    ArrayList<BarcodeWine> barcodeWineList;
    int wineCount;

    private void init() {
        wineCount = 0;
        barcodeWineList = new ArrayList<BarcodeWine>();
        scanDataHandler = new ScanDataHandler();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scanner);

        init();

        goBack = findViewById(R.id.btnGoBack);
        assert goBack != null;
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        reviewSelection = findViewById(R.id.btnReviewSelection);
        assert reviewSelection != null;
        reviewSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reviewSelection(v);
            }
        });


        scan = findViewById(R.id.btnScan);
        assert scan != null;
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanBarcode(v);
            }
        });
    }

    /**
     * event handler for scan button
     * @param view view of the activity
     */
    public void scanBarcode(View view){
        //TODO: scan in portrait mode
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        integrator.setPrompt("Scan a barcode");
        integrator.setResultDisplayDuration(0);
        //integrator.setWide();  // Wide scanning rectangle, may work better for 1D barcodes
        //integrator.setResultDisplayDuration(1000);
        integrator.setCameraId(0);  // Use a specific camera of the device
        //integrator.setCaptureLayout();
        integrator.initiateScan();
    }

    /**
     * function handle scan result
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.d("BS.onActivityResult", "");
        //retrieve scan result
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        if (scanningResult != null) {
            //we have a result
            String scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();

            Log.d("ScanContent: ", scanContent);
            Log.d("ScanFormat: ", scanFormat);


            if(!scanContent.equals("")) {
                scanDataHandler.sendRequest(scanContent);

                barcodeWine = scanDataHandler.getBarcodeWine();

                Log.d("beforeWineLoop", "");
                if(barcodeWine != null) {
                    barcodeWineList.add(barcodeWine);
                    wineCount++;
                }
            }
            else {
                Toast.makeText(getApplicationContext(),"No scan data received!", Toast.LENGTH_SHORT).show();
            }


        }
        else {
            Toast toast = Toast.makeText(getApplicationContext(),"No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    protected void reviewSelection(View view) {
        Log.d("ReviewSelection", "");
        //do something in response to button
        Intent intent = new Intent(this, ReviewSelectionScreen.class);
        intent.putExtra("WineList", barcodeWineList);
        startActivity(intent);
    }
}
