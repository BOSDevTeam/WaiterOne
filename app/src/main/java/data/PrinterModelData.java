package data;

/**
 * Created by User on 8/23/2018.
 */
public class PrinterModelData {
    public int getModelId() {
        return modelId;
    }

    public int getInterfaceId() {
        return interfaceId;
    }

    public int getWidthId() {
        return widthId;
    }

    public String getModelName() {
        return modelName;
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

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    int modelId,interfaceId,widthId;
    String modelName;
}
