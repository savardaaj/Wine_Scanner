package alex.winescanner;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

public class ScanDataHandler {

    //call UPC Item DB for UPC Codes: http://www.upcitemdb.com/upc/31259001043
    //Wine Bottles require a UPC Code!
    JSONRequestHandler jsonRH;
    JSONObject jsonResult;
    String upcURL;
    Wine wine;
    int wineCount;

    public ScanDataHandler() {
        Log.d("ScanDataHandler", "");

        wine = new Wine();
        wineCount = 0;
    }

    public void sendRequest(String upcID) {
        Log.d("sendRequest", "");
        try {
            //upcURL = "http://www.upcitemdb.com/upc/" + upcID;

            //For testing only
            upcURL = "http://www.upcitemdb.com/upc/31259001043";
            jsonResult = jsonRH.processUPCID(upcURL);

            populateWineObj(wine, jsonResult);
            Log.d("wineTitle", wine.getTitle());

            //TODO: take title and do a web scrape search for ratings/reviews on wine searcher or total wine
            //searchWineSearcher();
            //searchTotalWine();
            //https://www.wine-searcher.com/find/Joseph+Carr+Josh+Cellars+Rose+Wine,+750+mL
            //do brand + title?. works for the josh wine

            //TODO: need to complete website to register for snooth
            //http://api.snooth.com/wines/?akey=<your api key>&ip=66.28.234.115&q=napa+cabernet&xp=30

            //for now, just use data from upcdb

        }
        catch (Exception e) {

        }

    }

    public Wine getWine() {
        return wine;
    }

    public int getWineCount() {
        return wineCount;
    }

    private void populateWineObj(Wine wine, JSONObject jsonResult) {
        Log.d("populateWineObj", "");
        try {

            wine.setTitle(jsonResult.getString("title"));
            wine.setDescription(jsonResult.getString("description"));
            wine.setUpc(jsonResult.getString("upc"));
            wine.setBrand(jsonResult.getString("brand"));
            wine.setColor(jsonResult.getString("color"));
            wine.setLowest_recorded_price(Double.parseDouble(jsonResult.getString("lowest_recorded_price")));
            wine.setHighest_recorded_price(Double.parseDouble(jsonResult.getString("highest_recorded_price")));

            //Returns an array
            JSONArray arr = jsonResult.getJSONArray("images");

            int len = arr.length();
            String[] jsonImages = new String[len];
            if (len > 0) {
                for (int i = 0; i < len; i++) {
                    jsonImages[i] = arr.getString(i);
                }
            }

            wine.setImages(jsonImages);
            wineCount++;
        }
        catch (Exception e) {

        }
    }

}
