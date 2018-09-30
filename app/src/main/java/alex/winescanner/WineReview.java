package alex.winescanner;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import java.util.UUID;

public class WineReview {

    public String name;
    public String maker;
    public String type;
    public String year;
    public String location;
    public Bitmap imageBitmap;
    public String imageURL;
    public String description;
    public float rating;
    public String id;
    public String docId;
    public String pictureFilePath;
    public String userUUID;
    public String barcode;
    public boolean shareReview;

    WineReview() {
        this.id = UUID.randomUUID().toString();
        this.name = "";
        this.maker = "";
        this.type = "";
        this.year = "";
        this.location = "";
        //this.imageBitmap = Bitmap.createBitmap(); not sure how to do a new one
        this.imageURL = "";
        this.description = "";
        this.rating = 0;
        this.docId = "";
        this.pictureFilePath = "";
        this.userUUID = "";
        this.barcode = "";
        this.shareReview = false;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

}
