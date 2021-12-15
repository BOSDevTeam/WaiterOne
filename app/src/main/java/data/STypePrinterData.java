package data;

/**
 * Created by User on 6/16/2017.
 */
public class STypePrinterData {

    public String printerName;
    public String sTypeName;
    public int id;
    public int sTypeID;
    public int printerID;

    public int getPrinterID() {
        return printerID;
    }

    public void setPrinterID(int printerID) {
        this.printerID = printerID;
    }

    public String getsTypeName() {
        return sTypeName;
    }

    public void setsTypeName(String sTypeName) {
        this.sTypeName = sTypeName;
    }

    public void setPrinterName(String printerName) {
        this.printerName = printerName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setsTypeID(int sTypeID) {
        this.sTypeID = sTypeID;
    }

    public String getPrinterName() {
        return printerName;
    }

    public int getId() {
        return id;
    }

    public int getsTypeID() {
        return sTypeID;
    }
}
