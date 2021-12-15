package data;

/**
 * Created by NweYiAung on 30-12-2016.
 */
public class RpSaleByTableData {

    public String waiterName,tableName,date,tableTypeName;
    public int tableTypeID,tableID,totalCustomer;
    public double grandTotal,totalByTable,totalByTableType;

    public RpSaleByTableData(){
        super();
    }

    public int getTableTypeID(){
        return tableTypeID;
    }

    public void setTableTypeID(int tableTypeID){
        this.tableTypeID=tableTypeID;
    }

    public int getTableID(){
        return tableID;
    }

    public void setTableID(int tableID){
        this.tableID=tableID;
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

    public String getTableTypeName(){
        return tableTypeName;
    }

    public void setTableTypeName(String tableTypeName){
        this.tableTypeName=tableTypeName;
    }

    public double getGrandTotal(){
        return grandTotal;
    }

    public void setGrandTotal(double grandTotal){
        this.grandTotal = grandTotal;
    }

    public double getTotalByTable(){
        return totalByTable;
    }

    public void setTotalByTable(double totalByTable){
        this.totalByTable=totalByTable;
    }

    public double getTotalByTableType(){
        return totalByTableType;
    }

    public void setTotalByTableType(double totalByTableType){
        this.totalByTableType=totalByTableType;
    }

    public int getTotalCustomer(){
        return totalCustomer;
    }

    public void setTotalCustomer(int totalCustomer){
        this.totalCustomer=totalCustomer;
    }
}
