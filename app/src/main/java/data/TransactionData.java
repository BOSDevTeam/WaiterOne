package data;

/**
 * Created by NweYiAung on 14-02-2017.
 */
public class TransactionData {

    int tranid;
    int tableid;
    int waiterid;
    int integerQty;
    int counterID;
    int stype;
    String tableName;
    String waiterName;
    String itemid;
    String itemName;
    String taste;
    String stringQty;
    String orderTime;
    int srNo;
    double salePrice,amount;
    float floatQty;
    String date;
    String vouno;
    double subTotal,tax,charges,discount,grandTotal;
    byte[] itemImage;

    public void setItemImage(byte[] itemImage) {
        this.itemImage = itemImage;
    }

    public byte[] getItemImage() {

        return itemImage;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    String time;
    int slipid;
    String startTime;
    String endTime;

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public int getSlipid() {
        return slipid;
    }

    public void setSlipid(int slipid) {
        this.slipid = slipid;
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

    public double getGrandTotal() {
        return grandTotal;
    }

    public String getTime() {
        return time;
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

    public void setGrandTotal(double grandTotal) {
        this.grandTotal = grandTotal;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getVouno() {
        return vouno;
    }

    public void setVouno(String vouno) {
        this.vouno = vouno;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getStype() {
        return stype;
    }

    public void setStype(int stype) {
        this.stype = stype;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public int getSrNo() {
        return srNo;
    }

    public void setSrNo(int srNo) {
        this.srNo = srNo;
    }

    public int getCounterID() {
        return counterID;
    }

    public void setCounterID(int counterID) {
        this.counterID = counterID;
    }

    public String getStringQty() {
        return stringQty;
    }

    public void setStringQty(String stringQty) {
        this.stringQty = stringQty;
    }

    public int getTranid() {
        return tranid;
    }

    public int getTableid() {
        return tableid;
    }

    public int getWaiterid() {
        return waiterid;
    }

    public int getIntegerQty() {
        return integerQty;
    }

    public String getTableName() {
        return tableName;
    }

    public String getWaiterName() {
        return waiterName;
    }

    public String getItemid() {
        return itemid;
    }

    public String getItemName() {
        return itemName;
    }

    public String getTaste() {
        return taste;
    }

    public double getSalePrice() {
        return salePrice;
    }

    public double getAmount() {
        return amount;
    }

    public float getFloatQty() {
        return floatQty;
    }

    public void setTranid(int tranid) {
        this.tranid = tranid;
    }

    public void setTableid(int tableid) {
        this.tableid = tableid;
    }

    public void setWaiterid(int waiterid) {
        this.waiterid = waiterid;
    }

    public void setIntegerQty(int integerQty) {
        this.integerQty = integerQty;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setWaiterName(String waiterName) {
        this.waiterName = waiterName;
    }

    public void setItemid(String itemid) {
        this.itemid = itemid;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setTaste(String taste) {
        this.taste = taste;
    }

    public void setSalePrice(double salePrice) {
        this.salePrice = salePrice;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setFloatQty(float floatQty) {
        this.floatQty = floatQty;
    }
}
