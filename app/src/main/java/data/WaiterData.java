package data;

/**
 * Created by NweYiAung on 14-02-2017.
 */
public class WaiterData {

    int waiterid;
    int saledTableCount;
    int saledCustomerNumber;
    String waiterName,password;
    public boolean selected;
    int moduleid;

    public int getModuleid() {
        return moduleid;
    }

    public void setModuleid(int moduleid) {
        this.moduleid = moduleid;
    }

    public int getSaledTableCount() {
        return saledTableCount;
    }

    public int getSaledCustomerNumber() {
        return saledCustomerNumber;
    }

    public void setSaledTableCount(int saledTableCount) {
        this.saledTableCount = saledTableCount;
    }

    public void setSaledCustomerNumber(int saledCustomerNumber) {
        this.saledCustomerNumber = saledCustomerNumber;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getWaiterid() {
        return waiterid;
    }

    public String getWaiterName() {
        return waiterName;
    }

    public String getPassword() {
        return password;
    }

    public void setWaiterid(int waiterid) {
        this.waiterid = waiterid;
    }

    public void setWaiterName(String waiterName) {
        this.waiterName = waiterName;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
