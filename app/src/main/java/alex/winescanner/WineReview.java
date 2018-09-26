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
    public String userId;

    public WineReview() {
        this.id = UUID.randomUUID().toString();
    }

    public WineReview(String wineName, String wineMaker, String wineType, String wineYear,
                      String wineLocation, Bitmap wineImage, String wineDescription, float wineRating) {
        this.name = wineName;
        this.maker = wineMaker;
        this.type = wineType;
        this.year = wineYear;
        this.location = wineLocation;
        this.imageBitmap = wineImage;
        this.description = wineDescription;
        this.rating = wineRating;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    /*
    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getWineName() {
        return wineName;
    }

    public String getWineMaker() {
        return maker;
    }

    public String getWineType() {
        return wineType;
    }

    public String getWineYear() {
        return wineYear;
    }

    public String getWineLocation() {
        return wineLocation;
    }

    public Image getWineImage() {
        return wineImage;
    }

    public float getRating() {
        return rating;
    }

    public String getWineDescription() {
        return wineDescription;
    }

    public void setWineRating(float rating) {
        this.rating = rating;
    }

    public void setWineDescription(String wineDescription) {
        this.wineDescription = wineDescription;
    }

    public void setWineImage(Image wineImage) {
        this.wineImage = wineImage;
    }

    public void setWineLocation(String wineLocation) {
        this.wineLocation = wineLocation;
    }

    public void setmaker(String maker) {
        this.maker = maker;
    }

    public void setWineName(String wineName) {
        this.wineName = wineName;
    }

    public void setWineType(String wineType) {
        this.wineType = wineType;
    }

    public void setWineYear(String wineYear) {
        this.wineYear = wineYear;
    }

*/
    public WineReview createTestWineReview() {
        WineReview wr = new WineReview();
        wr.id = UUID.randomUUID().toString();
        wr.name = "test Wine Name";
        wr.maker = "Winery Name";
        wr.description = "A shitty red";
        wr.type = "red";
        wr.year = "2018";
        wr.location = "Nappa Falley";
        wr.rating = 2;
        return wr;
    }
}
