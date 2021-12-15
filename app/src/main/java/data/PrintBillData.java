package data;

import java.util.List;

/**
 * Created by User on 7/30/2018.
 */
public class PrintBillData {
    public String shopName;
    public String shopDesp;
    public String address;
    public String phone;
    public String printDate;
    public String slipNo;
    public String table;
    public String user;
    public String message1;
    public String message2;
    String orderNumber;

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getOrderNumber() {

        return orderNumber;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String startTime;
    public String endTime;
    public double subTotal,tax,charges,discount,netAmount, paidAmount, changeAmount;
    public List<TransactionData> lstTran;

    public String getShopName() {
        return shopName;
    }

    public String getShopDesp() {
        return shopDesp;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getPrintDate() {
        return printDate;
    }

    public String getSlipNo() {
        return slipNo;
    }

    public String getTable() {
        return table;
    }

    public String getUser() {
        return user;
    }

    public String getMessage1() {
        return message1;
    }

    public String getMessage2() {
        return message2;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public double getTax() {
        return tax;
    }

    public double getCharges() {
        return charges;
    }

    public double getDiscount() {
        return discount;
    }

    public double getNetAmount() {
        return netAmount;
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public double getChangeAmount() {
        return changeAmount;
    }

    public List<TransactionData> getLstTran() {
        return lstTran;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public void setShopDesp(String shopDesp) {
        this.shopDesp = shopDesp;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPrintDate(String printDate) {
        this.printDate = printDate;
    }

    public void setSlipNo(String slipNo) {
        this.slipNo = slipNo;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setMessage1(String message1) {
        this.message1 = message1;
    }

    public void setMessage2(String message2) {
        this.message2 = message2;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public void setCharges(double charges) {
        this.charges = charges;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public void setNetAmount(double netAmount) {
        this.netAmount = netAmount;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public void setChangeAmount(double changeAmount) {
        this.changeAmount = changeAmount;
    }

    public void setLstTran(List<TransactionData> lstTran) {
        this.lstTran = lstTran;
    }
}
