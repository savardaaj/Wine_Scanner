package alex.winescanner;

public class Characteristic {

    String docID;
    String name;
    String description;

    public Characteristic(String docID, String name, String description) {
        this.docID = docID;
        this.name = name;
        this.description = description;
    }
    public Characteristic(String name, String description) {
        this.name = name;
        this.description = description;
    }

}
