package data;

/**
 * Created by NweYiAung on 26-12-2016.
 */
public class RpSummaryItemData {
    public String name,menu,subMenuName,mainMenuName;
    public int subMenuID,mainMenuID,qty;
    public double price,amount,totalBySubMenu,totalByMainMenu;

    public RpSummaryItemData(){
        super();
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name=name;
    }

    public String getMenu(){
        return menu;
    }

    public void setMenu(String menu){
        this.menu=menu;
    }

    public int getQty(){
        return qty;
    }

    public void setQty(int qty){
        this.qty=qty;
    }

    public double getPrice(){
        return price;
    }

    public void setPrice(double price){
        this.price=price;
    }

    public double getAmount(){
        return amount;
    }

    public void setAmount(double amount){
        this.amount=amount;
    }

    public String getSubMenuName(){
        return subMenuName;
    }

    public void setSubMenuName(String subMenuName){
        this.subMenuName=subMenuName;
    }

    public String getMainMenuName(){
        return mainMenuName;
    }

    public void setMainMenuName(String mainMenuName){
        this.mainMenuName=mainMenuName;
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

    public int getSubMenuID(){
        return subMenuID;
    }

    public void setSubMenuID(int subMenuID){
        this.subMenuID=subMenuID;
    }

    public int getMainMenuID(){
        return mainMenuID;
    }

    public void setMainMenuID(int mainMenuID){
        this.mainMenuID=mainMenuID;
    }

}
