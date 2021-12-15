package data;

import java.util.List;

/**
 * Created by User on 5/30/2018.
 */
public class RpDayEndData {

    public String reportName,shopName,shopDesp,printDate,slipHeader,taxHeader,chargesHeader,disHeader,totalHeader,netHeader;
    public int slipid;
    public double totalAmt;
    public double netAmt;
    public double tax;
    public double charges;
    public double discount;
    public List<TransactionData> lstTran;

    public String getReportName() {
        return reportName;
    }

    public String getShopName() {
        return shopName;
    }

    public String getShopDesp() {
        return shopDesp;
    }

    public String getPrintDate() {
        return printDate;
    }

    public String getSlipHeader() {
        return slipHeader;
    }

    public String getTaxHeader() {
        return taxHeader;
    }

    public String getChargesHeader() {
        return chargesHeader;
    }

    public String getDisHeader() {
        return disHeader;
    }

    public String getTotalHeader() {
        return totalHeader;
    }

    public String getNetHeader() {
        return netHeader;
    }

    public int getSlipid() {
        return slipid;
    }

    public double getTotalAmt() {
        return totalAmt;
    }

    public double getNetAmt() {
        return netAmt;
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

    public List<TransactionData> getLstTran() {
        return lstTran;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public void setShopDesp(String shopDesp) {
        this.shopDesp = shopDesp;
    }

    public void setPrintDate(String printDate) {
        this.printDate = printDate;
    }

    public void setSlipHeader(String slipHeader) {
        this.slipHeader = slipHeader;
    }

    public void setTaxHeader(String taxHeader) {
        this.taxHeader = taxHeader;
    }

    public void setChargesHeader(String chargesHeader) {
        this.chargesHeader = chargesHeader;
    }

    public void setDisHeader(String disHeader) {
        this.disHeader = disHeader;
    }

    public void setTotalHeader(String totalHeader) {
        this.totalHeader = totalHeader;
    }

    public void setNetHeader(String netHeader) {
        this.netHeader = netHeader;
    }

    public void setSlipid(int slipid) {
        this.slipid = slipid;
    }

    public void setTotalAmt(double totalAmt) {
        this.totalAmt = totalAmt;
    }

    public void setNetAmt(double netAmt) {
        this.netAmt = netAmt;
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

    public void setLstTran(List<TransactionData> lstTran) {
        this.lstTran = lstTran;
    }
}
