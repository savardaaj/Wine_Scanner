package alex.winescanner;

import java.io.Serializable;

public class Wine implements Serializable{

    String description;
    String title;
    String upc;
    String brand;
    String color;
    double lowest_recorded_price;
    double highest_recorded_price;
    String[] images;

    public Wine() {

    }

    public Wine(String description, String title, String upc, String brand, String color, double lowest_recorded_price, double highest_recorded_price, String[] images) {
        this.description = description;
        this.title = title;
        this.upc = upc;
        this.brand = brand;
        this.color = color;
        this.lowest_recorded_price = lowest_recorded_price;
        this.highest_recorded_price = highest_recorded_price;
        this.images = images;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUpc() {
        return upc;
    }

    public void setUpc(String upc) {
        this.upc = upc;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public double getHighest_recorded_price() {
        return highest_recorded_price;
    }

    public void setHighest_recorded_price(double highest_recorded_price) {
        this.highest_recorded_price = highest_recorded_price;
    }

    public double getLowest_recorded_price() {
        return lowest_recorded_price;
    }

    public void setLowest_recorded_price(double lowest_recorded_price) {
        this.lowest_recorded_price = lowest_recorded_price;
    }

    public String[] getImages() {
        return images;
    }

    public void setImages(String[] images) {
        this.images = images;
    }
}
