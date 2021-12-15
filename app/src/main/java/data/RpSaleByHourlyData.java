package data;

/**
 * Created by NweYiAung on 07-02-2017.
 */
public class RpSaleByHourlyData {
    public String ctime;
    public int totalTransaction,totalItem,totalCustomer;
    public double totalAmount;

    public String getCTime(){
        return ctime;
    }

    public void setCTime(String ctime){
        this.ctime=ctime;
    }

    public int getTotalTransaction(){
        return totalTransaction;
    }

    public void setTotalTransaction(int totalTransaction){
        this.totalTransaction=totalTransaction;
    }

    public int getTotalItem(){
        return totalItem;
    }

    public void setTotalItem(int totalItem){
        this.totalItem=totalItem;
    }

    public int getTotalCustomer(){
        return totalCustomer;
    }

    public void setTotalCustomer(int totalCustomer){
        this.totalCustomer=totalCustomer;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
}
