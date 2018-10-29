package alex.winescanner;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.File;
import java.util.ArrayList;

public class BarcodeScanner extends AppCompatActivity {

    int requestCode;

    final int SINGLE_ENTRY = 4;
    final int COMPARE = 2;
    final int UPDATE_BARCODE = 5;

    Button scan;
    Button goBack;
    Button reviewSelection;

    ScanDataHandler scanDataHandler;
    BarcodeWine barcodeWine;
    ArrayList<BarcodeWine> barcodeWineList;
    int wineCount;

    private void init() {
        wineCount = 0;
        barcodeWineList = new ArrayList<>();
        scanDataHandler = new ScanDataHandler();
        barcodeWine = new BarcodeWine();
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
                onClickScanBarcode(v);
            }
        });

        Intent data = getIntent();
        Bundle bundle = data.getExtras();
        Log.d("***Debug***", "inside bundle " + bundle);
        if(bundle != null) {
            requestCode = bundle.getInt("requestCode");
            Log.d("***DEBUG***", "request code on entry: " + requestCode);
        }

        if(requestCode == SINGLE_ENTRY) {
            //SINGLE_ENTRY: Initiate scanner, scan a barcode, return to
            initiateScan();
        }
        else if (requestCode == COMPARE) {
            //COMPARE

        }
        else if (requestCode == UPDATE_BARCODE) {
            //Update a wine's barcode
            //Prompt for manual text entry or scan?
            initiateScan();
        }
    }

    /**
     * event handler for scan button
     * @param view view of the activity
     */
    public void onClickScanBarcode(View view){
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
     * @param rc
     * @param resultCode
     * @param intent
     */
    public void onActivityResult(int rc, int resultCode, Intent intent) {
        Log.d("***DEBUG***", "inside BarcodeScanner: onActivityResult");
        try {
            //retrieve scan result
            IntentResult scanningResult = IntentIntegrator.parseActivityResult(rc, resultCode, intent);

            if (scanningResult != null) {
                //we have a result
                String scanContent = scanningResult.getContents();
                String scanFormat = scanningResult.getFormatName();

                Log.d("***DEBUG***", "ScanContent: " + scanContent);
                Log.d("***DEBUG***", "ScanFormat: " + scanFormat);

                //Scan content is barcode number
                if(scanContent != null) {
                    if (!scanContent.equals("")) {
                        scanDataHandler.sendRequest(scanContent);

                        barcodeWine = scanDataHandler.getBarcodeWine();
                        if (requestCode == SINGLE_ENTRY) {
                            //return user to new wine entry screen, notify that no wine found
                            Intent i = new Intent(this, LibraryActivity.class);

                            if (barcodeWine.title.equals("") || barcodeWine.title == null) {
                                barcodeWine = new BarcodeWine();

                                //scan found nothing
                                barcodeWine.upc = scanContent;
                                i.putExtra("barcodeWine", barcodeWine);
                            } else {
                                i.putExtra("barcodeWine", barcodeWine);
                            }

                            setResult(RESULT_OK, i);
                            super.finish();
                        } else if (requestCode == COMPARE) {
                            if (barcodeWine.upc != null) {
                                barcodeWineList.add(barcodeWine);
                                wineCount++;
                            } else if (barcodeWine.upc == null) {
                                //Scan didn't find data for this wine.
                                barcodeWineList.add(barcodeWine);
                                wineCount++;
                            }
                        } else if (requestCode == UPDATE_BARCODE) {
                            String strBarcode = scanContent;
                            Intent i = new Intent(this, NewWineEntryActivity.class);
                            i.putExtra("barcode", strBarcode);
                            setResult(RESULT_OK, i);
                            super.finish();
                        }
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(),"No scan data received one!", Toast.LENGTH_SHORT).show();
                    Log.d("***DEBUG***", "Scan content was null");
                    setResult(RESULT_CANCELED);
                    super.finish();
                }
            }
            else {
                Log.d("***DEBUG***", "Scanning result was null");
            }
        }
        catch(Exception e) {

            Log.d("***DEBUG***", "Exception Occurred");
            Log.d("***DEBUG***", e.getMessage());
        }

    }

    protected void reviewSelection(View view) {
        Log.d("ReviewSelection", "");
        //do something in response to button
        Intent intent = new Intent(this, ReviewSelectionScreen.class);
        intent.putExtra("WineList", barcodeWineList);
        startActivity(intent);
    }

    public void initiateScan(){
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
}
