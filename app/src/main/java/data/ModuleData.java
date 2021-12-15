package data;

/**
 * Created by User on 6/5/2017.
 */
public class ModuleData {

    int moduleID;
    String moduleName;
    boolean selected;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public void setModuleID(int moduleID) {
        this.moduleID = moduleID;
    }

    public int getModuleID() {
        return moduleID;
    }

    public String getModuleName() {
        return moduleName;
    }
}
