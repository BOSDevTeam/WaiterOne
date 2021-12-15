package data;

/**
 * Created by NweYiAung on 22-05-2017.
 */
public class STypeData {

    public String sTypeName;
    public int sTypeID;

    public void setsTypeName(String sTypeName) {
        this.sTypeName = sTypeName;
    }

    public void setsTypeID(int sTypeID) {
        this.sTypeID = sTypeID;
    }

    public int getsTypeID() {
        return sTypeID;
    }

    public String getsTypeName() {
        return sTypeName;
    }
}
