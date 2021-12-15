package data;

/**
 * Created by NweYiAung on 14-02-2017.
 */
public class SystemSettingData {

    int service;
    int tax;
    int companyid;
    String adminPassword;
    String shopName;

    public int getCompanyid() {
        return companyid;
    }

    public void setCompanyid(int companyid) {
        this.companyid = companyid;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public int getTax() {
        return tax;
    }

    public int getService() {
        return service;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setTax(int tax) {
        this.tax = tax;
    }

    public void setService(int service) {
        this.service = service;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }
}
