package alex.winescanner;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

public class ScanDataHandler {

    //call UPC Item DB for UPC Codes: http://www.upcitemdb.com/upc/31259001043
    //BarcodeWine Bottles require a UPC Code!
    JSONRequestHandler jsonRH;
    JSONObject jsonResult;
    String upcURL;
    BarcodeWine barcodeWine;
    int wineCount;

    public ScanDataHandler() {
        Log.d("ScanDataHandler", "");

        barcodeWine = new BarcodeWine();
        wineCount = 0;
    }

    public void sendRequest(String upcID) {
        Log.d("sendRequest", "");
        try {
            upcURL = "https://api.upcitemdb.com/prod/trial/lookup?upc=" + upcID;
            jsonResult = jsonRH.processUPCID(upcURL);

            populateWineObj(barcodeWine, jsonResult);
            Log.d("wineTitle", barcodeWine.getTitle());

            //TODO: take title and do a web scrape search for ratings/reviews on barcodeWine searcher or total barcodeWine
            //searchWineSearcher();
            //searchTotalWine();
            //https://www.wine-searcher.com/find/Joseph+Carr+Josh+Cellars+Rose+Wine,+750+mL
            //do brand + title?. works for the josh barcodeWine

            //TODO: need to complete website to register for snooth
            //http://api.snooth.com/wines/?akey=<your api key>&ip=66.28.234.115&q=napa+cabernet&xp=30

            //for now, just use data from upcdb

        }
        catch (Exception e) {

        }

    }

    public BarcodeWine getBarcodeWine() {
        return barcodeWine;
    }

    public int getWineCount() {
        return wineCount;
    }

    private void populateWineObj(BarcodeWine barcodeWine, JSONObject jsonResult) {
        Log.d("populateWineObj", "");
        try {

            JSONArray barcodesArray = jsonResult.getJSONArray("items");
            int len = barcodesArray.length();
            String[] barcodeWines = new String[len];
            if (len > 0) {
                for (int i = 0; i < len; i++) {
                    barcodeWines[i] = barcodesArray.getString(i);
                }
            }

            if(barcodesArray.length() < 1) {
                Log.d("***DEBUG***","No data found for barcode");
            }
            else {
                String title = jsonResult.getString("title");
                String description = jsonResult.getString("description");
                String brand = jsonResult.getString("brand");
                String color = jsonResult.getString("color");
                Double lowest = Double.parseDouble(jsonResult.getString("lowest_recorded_price"));
                Double highest = Double.parseDouble(jsonResult.getString("highest_recorded_price"));

                if(title != null && !title.isEmpty()) {
                    barcodeWine.setTitle(title);
                }
                if(description != null && !description.isEmpty()) {
                    barcodeWine.setDescription(description);
                }
                if(brand != null && !brand.isEmpty()) {
                    barcodeWine.setBrand(brand);
                }
                if(color != null && !color.isEmpty()) {
                    barcodeWine.setColor(color);
                }
                if(!lowest.isNaN() && lowest != null) {
                    barcodeWine.setLowest_recorded_price(lowest);
                }
                if(!highest.isNaN() && highest != null) {
                    barcodeWine.setHighest_recorded_price(highest);
                }

                Log.d("***DEBUG***", "pop wine obj title" + barcodeWine.title);

                //Returns an array
                JSONArray arr = jsonResult.getJSONArray("images");

                len = arr.length();
                String[] jsonImages = new String[len];
                if (len > 0) {
                    for (int i = 0; i < len; i++) {
                        jsonImages[i] = arr.getString(i);
                    }
                }

                //barcodeWine.setImages(jsonImages);
                wineCount++;
            }


        }
        catch (Exception e) {

        }
    }

}
