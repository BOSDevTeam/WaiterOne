package data;

/**
 * Created by NweYiAung on 14-02-2017.
 */
public class ItemData {

    String itemid;
    String itemName;
    String subMenuName;
    String mainMenuName;
    String itemSet;
    int subMenuID;
    int mainMenuID;
    int outOfOrder;
    int counterID;
    int id,pairedEatenTimes;
    double price;
    boolean selected;
    int sTypeID;

    public void setItemImage(byte[] itemImage) {
        this.itemImage = itemImage;
    }

    public byte[] getItemImage() {

        return itemImage;
    }

    byte[] itemImage;

    public int getsTypeID() {
        return sTypeID;
    }

    public void setsTypeID(int sTypeID) {
        this.sTypeID = sTypeID;
    }

    public String getItemSet() {
        return itemSet;
    }

    public int getPairedEatenTimes() {
        return pairedEatenTimes;
    }

    public void setItemSet(String itemSet) {
        this.itemSet = itemSet;
    }

    public void setPairedEatenTimes(int pairedEatenTimes) {
        this.pairedEatenTimes = pairedEatenTimes;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCounterID() {
        return counterID;
    }

    public void setCounterID(int counterID) {
        this.counterID = counterID;
    }

    public void setItemid(String itemid) {
        this.itemid = itemid;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setSubMenuName(String subMenuName) {
        this.subMenuName = subMenuName;
    }

    public void setMainMenuName(String mainMenuName) {
        this.mainMenuName = mainMenuName;
    }

    public void setSubMenuID(int subMenuID) {
        this.subMenuID = subMenuID;
    }

    public void setMainMenuID(int mainMenuID) {
        this.mainMenuID = mainMenuID;
    }

    public void setOutOfOrder(int outOfOrder) {
        this.outOfOrder = outOfOrder;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getItemid() {
        return itemid;
    }

    public String getItemName() {
        return itemName;
    }

    public String getSubMenuName() {
        return subMenuName;
    }

    public String getMainMenuName() {
        return mainMenuName;
    }

    public int getSubMenuID() {
        return subMenuID;
    }

    public int getMainMenuID() {
        return mainMenuID;
    }

    public int getOutOfOrder() {
        return outOfOrder;
    }

    public double getPrice() {
        return price;
    }
}
