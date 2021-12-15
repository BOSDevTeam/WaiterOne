package data;

/**
 * Created by NweYiAung on 20-01-2017.
 */
public class RpSaleInvoiceData {

    public String date,waiter,table,voucher,itemName;
    public int qty;
    public double subtotal;
    public double grandtotal;
    public double tax;
    public double charges;
    public double price;
    public double amount;
    public double discount;


    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }
    public RpSaleInvoiceData(){
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

    public String getItemName(){
        return itemName;
    }

    public void setItemName(String itemName){
        this.itemName=itemName;
    }

    public String getVoucher(){
        return voucher;
    }

    public void setVoucher(String voucher){
        this.voucher=voucher;
    }

    public int getQty(){
        return qty;
    }

    public void setQty(int qty){
        this.qty=qty;
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
}
