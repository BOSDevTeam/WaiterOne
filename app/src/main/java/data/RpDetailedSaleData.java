package data;

/**
 * Created by NweYiAung on 26-12-2016.
 */
public class RpDetailedSaleData {
    public String date,waiter,table;
    public int customerNumber;
    public double subtotal;
    public double grandtotal;
    public double tax;
    public double charges;
    public double discount;

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public RpDetailedSaleData(){
        super();
    }

    public String getDate(){
        return date;
    }

    public void setDate(String date){
        this.date=date;
    }

    public String getWaiter(){
        return waiter;
    }

    public void setWaiter(String waiter){
        this.waiter=waiter;
    }

    public String getTable(){
        return table;
    }

    public void setTable(String table){
        this.table=table;
    }

    public int getCustomerNumber(){
        return customerNumber;
    }

    public void setCustomerNumber(int customerNumber){
        this.customerNumber=customerNumber;
    }

    public double getSubtotal(){
        return subtotal;
    }

    public void setSubtotal(double subtotal){
        this.subtotal=subtotal;
    }

    public double getTax(){
        return tax;
    }

    public void setTax(double tax){
        this.tax=tax;
    }

    public double getCharges(){
        return charges;
    }

    public void setCharges(double charges){
        this.charges=charges;
    }

    public double getGrandtotal(){
        return grandtotal;
    }

    public void setGrandtotal(double grandtotal){
        this.grandtotal=grandtotal;
    }
}
