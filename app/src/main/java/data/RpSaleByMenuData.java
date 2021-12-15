package data;

/**
 * Created by NweYiAung on 28-12-2016.
 */
public class RpSaleByMenuData {

    public String itemID,itemName,subMenuName,mainMenuName;
    public int subMenuID,mainMenuID,quantity;
    public double saleAmount,totalBySubMenu,totalByMainMenu;

    public RpSaleByMenuData(){
        super();
    }

    public String getItemID(){
        return itemID;
    }

    public void setItemID(String itemID){
        this.itemID=itemID;
    }

    public String getItemName(){
        return itemName;
    }

    public void setItemName(String itemName){
        this.itemName=itemName;
    }

    public int getSubMenuID(){
        return subMenuID;
    }

    public void setSubMenuID(int subMenuID){
        this.subMenuID=subMenuID;
    }

    public String getSubMenuName(){
        return subMenuName;
    }

    public void setSubMenuName(String subMenuName){
        this.subMenuName=subMenuName;
    }

    public int getMainMenuID(){
        return mainMenuID;
    }

    public void setMainMenuID(int mainMenuID){
        this.mainMenuID=mainMenuID;
    }

    public String getMainMenuName(){
        return mainMenuName;
    }

    public void setMainMenuName(String mainMenuName){
        this.mainMenuName=mainMenuName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getSaleAmount() {
        return saleAmount;
    }

    public void setSaleAmount(double saleAmount) {
        this.saleAmount = saleAmount;
    }

    public double getTotalBySubMenu() {
        return totalBySubMenu;
    }

    public void setTotalBySubMenu(double totalBySubMenu) {
        this.totalBySubMenu = totalBySubMenu;
    }

    public double getTotalByMainMenu() {
        return totalByMainMenu;
    }

    public void setTotalByMainMenu(double totalByMainMenu) {
        this.totalByMainMenu = totalByMainMenu;
    }

}
