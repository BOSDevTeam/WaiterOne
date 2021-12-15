package data;

/**
 * Created by NweYiAung on 14-02-2017.
 */
public class TableData {

    int tableTypeID;
    int tableid;
    int waiterSaledTableCount;
    String tableTypeName,tableName,waiterName;
    public boolean selected;

    public int getWaiterSaledTableCount() {
        return waiterSaledTableCount;
    }

    public String getWaiterName() {
        return waiterName;
    }

    public void setWaiterSaledTableCount(int waiterSaledTableCount) {
        this.waiterSaledTableCount = waiterSaledTableCount;
    }

    public void setWaiterName(String waiterName) {
        this.waiterName = waiterName;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getTableTypeID() {
        return tableTypeID;
    }

    public int getTableid() {
        return tableid;
    }

    public String getTableTypeName() {
        return tableTypeName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableTypeID(int tableTypeID) {
        this.tableTypeID = tableTypeID;
    }

    public void setTableid(int tableid) {
        this.tableid = tableid;
    }

    public void setTableTypeName(String tableTypeName) {
        this.tableTypeName = tableTypeName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
