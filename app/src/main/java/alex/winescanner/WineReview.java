package alex.winescanner;

import android.media.Image;
import android.media.Rating;
import android.view.View;
import android.widget.RatingBar;

import java.util.UUID;

public class WineReview {

    private String wineName;
    private String wineMaker;
    private String wineType;
    private String wineYear;
    private String wineLocation;
    private Image wineImage;
    private String wineDescription;
    private float rating;
    int id;

    public WineReview() {
        this.id = View.generateViewId();
    }

    public WineReview(String wineName, String wineMaker, String wineType, String wineYear,
                      String wineLocation, Image wineImage, String wineDescription, float rating) {
        this.wineName = wineName;
        this.wineMaker = wineMaker;
        this.wineType = wineType;
        this.wineYear = wineYear;
        this.wineLocation = wineLocation;
        this.wineImage = wineImage;
        this. wineDescription = wineDescription;
        this.rating = rating;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getWineName() {
        return wineName;
    }

    public String getWineMaker() {
        return wineMaker;
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

    public void setWineMaker(String wineMaker) {
        this.wineMaker = wineMaker;
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

    public WineReview createTestWineReview() {
        WineReview wr = new WineReview();
        wr.id = View.generateViewId();
        wr.wineName = "test Wine Name";
        wr.wineMaker = "Winery Name";
        wr.wineDescription = "A shitty red";
        wr.wineType = "red";
        wr.wineYear = "2018";
        wr.wineLocation = "Nappa Falley";
        wr.rating = 2;
        return wr;
    }
}
