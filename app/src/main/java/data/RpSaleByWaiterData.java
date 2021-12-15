package data;

/**
 * Created by NweYiAung on 27-12-2016.
 */
public class RpSaleByWaiterData {

    public String waiterName,tableName,date;
    public int waiterID,totalCustomer,totalSaleTable;
    public double grandTotal,total;

    public RpSaleByWaiterData(){
        super();
    }

    public int getWaiterID(){
        return waiterID;
    }

    public void setWaiterID(int waiterID){
        this.waiterID=waiterID;
    }

    public String getWaiterName(){
        return waiterName;
    }

    public void setWaiterName(String waiterName){
        this.waiterName=waiterName;
    }

    public String getTableName(){
        return tableName;
    }

    public void setTableName(String tableName){
        this.tableName=tableName;
    }

    public String getDate(){
        return date;
    }

    public void setDate(String date){
        this.date=date;
    }

    public double getGrandTotal(){
        return grandTotal;
    }

    public void setGrandTotal(double grandTotal){
        this.grandTotal = grandTotal;
    }

    public double getTotal(){
        return total;
    }

    public void setTotal(double total){
        this.total=total;
    }

    public int getTotalCustomer(){
        return totalCustomer;
    }

    public void setTotalCustomer(int totalCustomer){
        this.totalCustomer=totalCustomer;
    }

    public int getTotalSaleTable(){
        return totalSaleTable;
    }

    public void setTotalSaleTable(int totalSaleTable){
        this.totalSaleTable=totalSaleTable;
    }
}
