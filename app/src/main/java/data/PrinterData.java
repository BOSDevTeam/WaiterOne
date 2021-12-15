package data;

/**
 * Created by User on 8/23/2018.
 */
public class PrinterData {
    public int getPrinterId() {
        return printerId;
    }

    public int getModelId() {
        return modelId;
    }

    public int getInterfaceId() {
        return interfaceId;
    }

    public int getWidthId() {
        return widthId;
    }

    public String getPrinterName() {
        return printerName;
    }

    public String getPrinterAddress() {
        return printerAddress;
    }

    public void setPrinterId(int printerId) {
        this.printerId = printerId;
    }

    public void setModelId(int modelId) {
        this.modelId = modelId;
    }

    public void setInterfaceId(int interfaceId) {
        this.interfaceId = interfaceId;
    }

    public void setWidthId(int widthId) {
        this.widthId = widthId;
    }

    public void setPrinterName(String printerName) {
        this.printerName = printerName;
    }

    public void setPrinterAddress(String printerAddress) {
        this.printerAddress = printerAddress;
    }

    int printerId,modelId,interfaceId,widthId;
    String printerName;
    String printerAddress;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean selected;

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    String modelName;

    public String getInterfaceName() {
        return interfaceName;
    }

    public String getWidthName() {
        return widthName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public void setWidthName(String widthName) {
        this.widthName = widthName;
    }

    String interfaceName;
    String widthName;
    int isReceipt;

    public int getIsReceipt() {
        return isReceipt;
    }

    public int getPrintCount() {
        return printCount;
    }

    public void setIsReceipt(int isReceipt) {
        this.isReceipt = isReceipt;
    }

    public void setPrintCount(int printCount) {
        this.printCount = printCount;
    }

    int printCount;
}
