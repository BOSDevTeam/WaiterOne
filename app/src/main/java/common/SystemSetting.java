package common;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bosictsolution.waiterone.R;
import com.bosictsolution.waiterone.SystemActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import data.CompanyData;
import data.ItemAndSubData;
import data.ItemData;
import data.ItemSubData;
import data.ItemSubGroupData;
import data.MainMenuData;
import data.STypeData;
import data.SubMenuData;
import data.SystemSettingData;
import data.TableData;
import data.TableTypeData;
import data.TasteData;
import data.VoucherSettingData;
import data.WaiterData;

public class SystemSetting {
    public static boolean isTestPrint,isSpTxtWhite;
    public static final int WARNING=1,ERROR=2,SUCCESS=3,INFO=4;
    public static final String DATE_FORMAT="yyyy-MM-dd";
    public static final String TIME_FORMAT="hh:mm a";
    public static final String DATE_TIME_FORMAT="yyyy-MM-dd hh:mm:ss";
    public static final String MM_DATE_FORMAT="dd-MM-yyyy";
    public static final String ORDER_PRINT_TIME_FORMAT="hh:mm:ss a";
    public enum BackupType {SystemBackup,UserBackup,UserCustomBackup}
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final int USE_TAKE_AWAY=1,USE_BOTH_NORMAL_TAKE_AWAY=2;
    public static final String SINGLE_CHECK="Single";
    public static final String MULTI_CHECK="Multi";

    public SystemSetting(){

    }

    public void dataBackup(DBHelper db,boolean isBackup,BackupType backupType) {
        File exportDir=null;
        if(isBackup) {
            switch (backupType) {
                case SystemBackup:
                    exportDir = new File(Environment.getExternalStorageDirectory().getPath(), "/WaiterOneDB/SystemBackup");
                    break;

                case UserBackup:
                    exportDir = new File(Environment.getExternalStorageDirectory().getPath(), "/WaiterOneDB/UserBackup");
                    break;

                case UserCustomBackup:
                    exportDir = new File(Environment.getExternalStorageDirectory().getPath(), SystemActivity.customBackupPath);
                    break;

                default:
                    System.out.println("");
                    break;
            }
        }
        else exportDir = new File(Environment.getExternalStorageDirectory().getPath(), "/WaiterOneDB");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }

        File fileWaiter = new File(exportDir, "User.csv");
        File fileUserRight = new File(exportDir, "UserRight.csv");
        File fileTableType = new File(exportDir, "TableType.csv");
        File fileTable = new File(exportDir, "Table.csv");
        File fileMainMenu = new File(exportDir, "MainMenu.csv");
        File fileSubMenu = new File(exportDir, "SubMenu.csv");
        File fileItem = new File(exportDir, "Item.csv");
        File fileTaste = new File(exportDir, "Taste.csv");
        File fileSystemSetting = new File(exportDir, "SystemSetting.csv");
        File fileVoucherSetting = new File(exportDir, "VoucherSetting.csv");
        File fileSType = new File(exportDir, "ItemSType.csv");
        File fileCompany = new File(exportDir, "Company.csv");
        File fileItemSubGroup = new File(exportDir, "ItemSubGroup.csv");
        File fileItemSub = new File(exportDir, "ItemSub.csv");
        File fileItemAndSub = new File(exportDir, "ItemAndSub.csv");

        try {
            /** for waiter **/
            fileWaiter.createNewFile();
            CSVWriter csvWriteWaiter = new CSVWriter(new FileWriter(fileWaiter));

            Cursor curWaiter = db.getWaiter();
            List<WaiterData> lstWaiterData = new ArrayList<>();
            while (curWaiter.moveToNext()) {
                WaiterData data = new WaiterData();
                data.setWaiterid(curWaiter.getInt(0));
                data.setWaiterName(curWaiter.getString(1));
                data.setPassword(curWaiter.getString(2));
                lstWaiterData.add(data);
            }

            String headerWaiter[] = {"UserID", "User Name", "Password"};
            csvWriteWaiter.writeNext(headerWaiter);

            for (int i = 0; i < lstWaiterData.size(); i++) {
                String data[] = {String.valueOf(lstWaiterData.get(i).getWaiterid()), lstWaiterData.get(i).getWaiterName(), lstWaiterData.get(i).getPassword()};
                csvWriteWaiter.writeNext(data);
            }
            csvWriteWaiter.close();

            /** for user right **/
            fileUserRight.createNewFile();
            CSVWriter csvWriteUserRight = new CSVWriter(new FileWriter(fileUserRight));

            Cursor curUserRight = db.getUserRight();
            List<WaiterData> lstUserRightData = new ArrayList<>();
            while (curUserRight.moveToNext()) {
                WaiterData data = new WaiterData();
                data.setWaiterid(curUserRight.getInt(0));
                data.setModuleid(curUserRight.getInt(1));
                lstUserRightData.add(data);
            }

            String headerUser[] = {"UserID", "ModuleID"};
            csvWriteUserRight.writeNext(headerUser);

            for (int i = 0; i < lstUserRightData.size(); i++) {
                String data[] = {String.valueOf(lstUserRightData.get(i).getWaiterid()), String.valueOf(lstUserRightData.get(i).getModuleid())};
                csvWriteUserRight.writeNext(data);
            }
            csvWriteUserRight.close();

            /** for table type **/
            fileTableType.createNewFile();
            CSVWriter csvWriteTableType = new CSVWriter(new FileWriter(fileTableType));

            Cursor curTableType = db.getTableType();
            List<TableTypeData> lstTableTypeData = new ArrayList<>();
            while (curTableType.moveToNext()) {
                TableTypeData data = new TableTypeData();
                data.setTableTypeID(curTableType.getInt(0));
                data.setTableTypeName(curTableType.getString(1));
                lstTableTypeData.add(data);
            }

            String headerTableType[] = {"TableTypeID", "TableTypeName"};
            csvWriteTableType.writeNext(headerTableType);

            for (int i = 0; i < lstTableTypeData.size(); i++) {
                String data[] = {String.valueOf(lstTableTypeData.get(i).getTableTypeID()), lstTableTypeData.get(i).getTableTypeName()};
                csvWriteTableType.writeNext(data);
            }
            csvWriteTableType.close();

            /** for table **/
            fileTable.createNewFile();
            CSVWriter csvWriteTable = new CSVWriter(new FileWriter(fileTable));

            Cursor curTable = db.getTable();
            List<TableData> lstTableData = new ArrayList<>();
            while (curTable.moveToNext()) {
                TableData data = new TableData();
                data.setTableid(curTable.getInt(0));
                data.setTableName(curTable.getString(1));
                data.setTableTypeID(curTable.getInt(2));
                lstTableData.add(data);
            }

            String headerTable[] = {"TableID", "Table Name", "TableTypeID"};
            csvWriteTable.writeNext(headerTable);

            for (int i = 0; i < lstTableData.size(); i++) {
                String data[] = {String.valueOf(lstTableData.get(i).getTableid()), lstTableData.get(i).getTableName(), String.valueOf(lstTableData.get(i).getTableTypeID())};
                csvWriteTable.writeNext(data);
            }
            csvWriteTable.close();

            /** for main menu **/
            fileMainMenu.createNewFile();
            CSVWriter csvWriteMainMenu = new CSVWriter(new FileWriter(fileMainMenu));

            Cursor curMainMenu = db.getMainMenu();
            List<MainMenuData> lstMainMenuData = new ArrayList<>();
            while (curMainMenu.moveToNext()) {
                MainMenuData data = new MainMenuData();
                data.setMainMenuID(curMainMenu.getInt(0));
                data.setMainMenuName(curMainMenu.getString(1));
                data.setCounterid(curMainMenu.getInt(2));
                data.setIsAllow(curMainMenu.getInt(3));
                lstMainMenuData.add(data);
            }

            String headerMainMenu[] = {"MainMenuID", "MainMenuName", "CounterID", "isAllow"};
            csvWriteMainMenu.writeNext(headerMainMenu);

            for (int i = 0; i < lstMainMenuData.size(); i++) {
                String data[] = {String.valueOf(lstMainMenuData.get(i).getMainMenuID()), lstMainMenuData.get(i).getMainMenuName(), String.valueOf(lstMainMenuData.get(i).getCounterid()), String.valueOf(lstMainMenuData.get(i).getIsAllow())};
                csvWriteMainMenu.writeNext(data);
            }
            csvWriteMainMenu.close();

            /** for sub menu **/
            fileSubMenu.createNewFile();
            CSVWriter csvWriteSubMenu = new CSVWriter(new FileWriter(fileSubMenu));

            Cursor curSubMenu = db.getSubMenu();
            List<SubMenuData> lstSubMenuData = new ArrayList<>();
            while (curSubMenu.moveToNext()) {
                SubMenuData data = new SubMenuData();
                data.setSubMenuID(curSubMenu.getInt(0));
                data.setSubMenuName(curSubMenu.getString(1));
                data.setMainMenuID(curSubMenu.getInt(2));
                data.setSortCode(curSubMenu.getString(3));
                lstSubMenuData.add(data);
            }

            String headerSubMenu[] = {"SubMenuID", "SubMenuName", "MainMenuID", "SortCode"};
            csvWriteSubMenu.writeNext(headerSubMenu);

            for (int i = 0; i < lstSubMenuData.size(); i++) {
                String data[] = {String.valueOf(lstSubMenuData.get(i).getSubMenuID()), lstSubMenuData.get(i).getSubMenuName(), String.valueOf(lstSubMenuData.get(i).getMainMenuID()), lstSubMenuData.get(i).getSortCode()};
                csvWriteSubMenu.writeNext(data);
            }
            csvWriteSubMenu.close();

            /** for item **/
            fileItem.createNewFile();
            CSVWriter csvWriteItem = new CSVWriter(new FileWriter(fileItem));

            Cursor curItem = db.getItem();
            List<ItemData> lstItmeData = new ArrayList<>();
            while (curItem.moveToNext()) {
                ItemData data = new ItemData();
                data.setId(curItem.getInt(0));
                data.setItemid(curItem.getString(1));
                data.setItemName(curItem.getString(2));
                data.setSubMenuID(curItem.getInt(3));
                data.setPrice(curItem.getDouble(4));
                data.setOutOfOrder(curItem.getInt(5));
                data.setsTypeID(curItem.getInt(9));
                lstItmeData.add(data);

                int id=curItem.getInt(0);
                byte[] image=curItem.getBlob(10);
                if (image != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                    backupItemImage(id, bitmap);
                }
            }

            String headerItem[] = {"ID", "ItemID", "Item Name", "SubMenuID", "Price", "OutOfOrder", "STypeID"};
            csvWriteItem.writeNext(headerItem);

            for (int i = 0; i < lstItmeData.size(); i++) {
                String data[] = {String.valueOf(lstItmeData.get(i).getId()), lstItmeData.get(i).getItemid(), lstItmeData.get(i).getItemName(), String.valueOf(lstItmeData.get(i).getSubMenuID()), String.valueOf(lstItmeData.get(i).getPrice()), String.valueOf(lstItmeData.get(i).getOutOfOrder()), String.valueOf(lstItmeData.get(i).getsTypeID())};
                csvWriteItem.writeNext(data);
            }
            csvWriteItem.close();

            /** for taste **/
            fileTaste.createNewFile();
            CSVWriter csvWriteTaste = new CSVWriter(new FileWriter(fileTaste));

            Cursor curTaste = db.getTaste();
            List<TasteData> lstTaste = new ArrayList<>();
            while (curTaste.moveToNext()) {
                TasteData data = new TasteData();
                data.setTasteid(curTaste.getInt(0));
                data.setTasteName(curTaste.getString(1));
                lstTaste.add(data);
            }

            String headerTaste[] = {"TasteID", "TasteName"};
            csvWriteTaste.writeNext(headerTaste);

            for (int i = 0; i < lstTaste.size(); i++) {
                String data[] = {String.valueOf(lstTaste.get(i).getTasteid()), lstTaste.get(i).getTasteName()};
                csvWriteTaste.writeNext(data);
            }
            csvWriteTaste.close();

            /** for company **/
            fileCompany.createNewFile();
            CSVWriter csvWriteCompany = new CSVWriter(new FileWriter(fileCompany));

            Cursor curCompany = db.getCompany();
            List<CompanyData> lstCompany = new ArrayList<>();
            while (curCompany.moveToNext()) {
                CompanyData data = new CompanyData();
                data.setCompanyID(curCompany.getInt(0));
                data.setCompanyName(curCompany.getString(1));
                lstCompany.add(data);
            }

            String headerCompany[] = {"CompanyID", "CompanyName"};
            csvWriteCompany.writeNext(headerCompany);

            for (int i = 0; i < lstCompany.size(); i++) {
                String data[] = {String.valueOf(lstCompany.get(i).getCompanyID()), lstCompany.get(i).getCompanyName()};
                csvWriteCompany.writeNext(data);
            }
            csvWriteCompany.close();

            /** for system setting **/
            fileSystemSetting.createNewFile();
            CSVWriter csvWriteSystemSetting = new CSVWriter(new FileWriter(fileSystemSetting));

            Cursor curSystemSetting = db.getSystemSetting();
            List<SystemSettingData> lstSystemSetting = new ArrayList<>();
            if (curSystemSetting.moveToNext()) {
                SystemSettingData data = new SystemSettingData();
                data.setShopName(curSystemSetting.getString(0));
                data.setTax(curSystemSetting.getInt(1));
                data.setService(curSystemSetting.getInt(2));
                data.setCompanyid(curSystemSetting.getInt(3));
                lstSystemSetting.add(data);
            }

            String headerSystemSetting[] = {"Shop Name", "Tax", "Charges", "CompanyID"};
            csvWriteSystemSetting.writeNext(headerSystemSetting);
            if (lstSystemSetting.size() != 0) {
                String data[] = {lstSystemSetting.get(0).getShopName(), String.valueOf(lstSystemSetting.get(0).getTax()), String.valueOf(lstSystemSetting.get(0).getService()), String.valueOf(lstSystemSetting.get(0).getCompanyid())};
                csvWriteSystemSetting.writeNext(data);
            }
            csvWriteSystemSetting.close();

            /** for voucher setting **/
            fileVoucherSetting.createNewFile();
            CSVWriter csvWriteVoucherSetting = new CSVWriter(new FileWriter(fileVoucherSetting));

            Cursor curVoucherSetting = db.getVoucherSetting();
            List<VoucherSettingData> lstVoucherSetting = new ArrayList<>();
            if (curVoucherSetting.moveToNext()) {
                VoucherSettingData data = new VoucherSettingData();
                data.setTitle(curVoucherSetting.getString(0));
                data.setDescription(curVoucherSetting.getString(1));
                data.setPhone(curVoucherSetting.getString(2));
                data.setMessage(curVoucherSetting.getString(3));
                data.setAddress(curVoucherSetting.getString(4));
                data.setMessage2(curVoucherSetting.getString(5));
                lstVoucherSetting.add(data);
            }

            String headerVoucherSetting[] = {"Title", "Description", "Phone", "Message", "Address", "Message2"};
            csvWriteVoucherSetting.writeNext(headerVoucherSetting);
            if (lstVoucherSetting.size() != 0) {
                String data[] = {lstVoucherSetting.get(0).getTitle(), lstVoucherSetting.get(0).getDescription(), lstVoucherSetting.get(0).getPhone(), lstVoucherSetting.get(0).getMessage(), lstVoucherSetting.get(0).getAddress(), lstVoucherSetting.get(0).getMessage2()};
                csvWriteVoucherSetting.writeNext(data);
            }
            csvWriteVoucherSetting.close();

            /** for stype **/
            fileSType.createNewFile();
            CSVWriter csvWriteSType = new CSVWriter(new FileWriter(fileSType));

            Cursor curSType = db.getSType();
            List<STypeData> lstSTypeData = new ArrayList<>();
            while (curSType.moveToNext()) {
                STypeData data = new STypeData();
                data.setsTypeID(curSType.getInt(0));
                data.setsTypeName(curSType.getString(1));
                lstSTypeData.add(data);
            }

            String headerSType[] = {"STypeID", "STypeName"};
            csvWriteSType.writeNext(headerSType);

            for (int i = 0; i < lstSTypeData.size(); i++) {
                String data[] = {String.valueOf(lstSTypeData.get(i).getsTypeID()), lstSTypeData.get(i).getsTypeName()};
                csvWriteSType.writeNext(data);
            }
            csvWriteSType.close();

            /** for item sub group **/
            fileItemSubGroup.createNewFile();
            CSVWriter csvWriteItemSubGroup = new CSVWriter(new FileWriter(fileItemSubGroup));
            Cursor curItemSubGroup = db.getItemSubGroup();
            List<ItemSubGroupData> lstItemSubGroupData = new ArrayList<>();
            while (curItemSubGroup.moveToNext()) {
                ItemSubGroupData data = new ItemSubGroupData();
                data.setPkId(curItemSubGroup.getInt(0));
                data.setSubGroupName(curItemSubGroup.getString(1));
                data.setSubTitle(curItemSubGroup.getString(2));
                data.setSingleCheck(curItemSubGroup.getInt(3));
                lstItemSubGroupData.add(data);
            }
            String headerItemSubGroup[] = {"PKID", "GroupName","SubTitle","IsSingleCheck"};
            csvWriteItemSubGroup.writeNext(headerItemSubGroup);
            for (int i = 0; i < lstItemSubGroupData.size(); i++) {
                String data[] = {String.valueOf(lstItemSubGroupData.get(i).getPkId()), lstItemSubGroupData.get(i).getSubGroupName(), lstItemSubGroupData.get(i).getSubTitle(),String.valueOf(lstItemSubGroupData.get(i).isSingleCheck())};
                csvWriteItemSubGroup.writeNext(data);
            }
            csvWriteItemSubGroup.close();

            /** for item sub **/
            fileItemSub.createNewFile();
            CSVWriter csvWriteItemSub = new CSVWriter(new FileWriter(fileItemSub));
            Cursor curItemSub = db.getItemSub();
            List<ItemSubData> lstItemSubData = new ArrayList<>();
            while (curItemSub.moveToNext()) {
                ItemSubData data = new ItemSubData();
                data.setPkId(curItemSub.getInt(0));
                data.setSubGroupId(curItemSub.getInt(1));
                data.setSubName(curItemSub.getString(2));
                data.setPrice(curItemSub.getInt(3));
                lstItemSubData.add(data);
            }
            String headerItemSub[] = {"PKID", "SubGroupID","SubName","Price"};
            csvWriteItemSub.writeNext(headerItemSub);
            for (int i = 0; i < lstItemSubData.size(); i++) {
                String data[] = {String.valueOf(lstItemSubData.get(i).getPkId()), String.valueOf(lstItemSubData.get(i).getSubGroupId()), lstItemSubData.get(i).getSubName(),String.valueOf(lstItemSubData.get(i).getPrice())};
                csvWriteItemSub.writeNext(data);
            }
            csvWriteItemSub.close();

            /** for item and sub **/
            fileItemAndSub.createNewFile();
            CSVWriter csvWriteItemAndSub = new CSVWriter(new FileWriter(fileItemAndSub));
            List<ItemAndSubData> lstItemAndSubData = db.getItemAndSub();
            String headerItemAndSub[] = {"PKID", "ItemID","SubGroupID","LevelNo"};
            csvWriteItemAndSub.writeNext(headerItemAndSub);
            for (int i = 0; i < lstItemAndSubData.size(); i++) {
                String data[] = {String.valueOf(lstItemAndSubData.get(i).getPkId()), lstItemAndSubData.get(i).getItemId(), String.valueOf(lstItemAndSubData.get(i).getSubGroupId()),String.valueOf(lstItemAndSubData.get(i).getLevelNo())};
                csvWriteItemAndSub.writeNext(data);
            }
            csvWriteItemAndSub.close();

        } catch (IOException e) {
            Log.e("ExportImportActivity", e.getMessage(), e);
        }
    }

    public void dataRestore(DBHelper db,boolean isRestore) {
        File importDir;
        if(isRestore) importDir = new File(Environment.getExternalStorageDirectory().getPath(), "/WaiterOneDB/UserBackup");
        else importDir = new File(Environment.getExternalStorageDirectory().getPath(), "/WaiterOneDB");
        if (!importDir.exists()) {
            importDir.mkdirs();
        }

        File fileWaiter = new File(importDir, "User.csv");
        File fileUserRight = new File(importDir, "UserRight.csv");
        File fileTableType = new File(importDir, "TableType.csv");
        File fileTable = new File(importDir, "Table.csv");
        File fileMainMenu = new File(importDir, "MainMenu.csv");
        File fileSubMenu = new File(importDir, "SubMenu.csv");
        File fileItem = new File(importDir, "Item.csv");
        File fileTaste = new File(importDir, "Taste.csv");
        File fileSystemSetting = new File(importDir, "SystemSetting.csv");
        File fileVoucherSetting = new File(importDir, "VoucherSetting.csv");
        File fileSType = new File(importDir, "ItemSType.csv");
        File fileCompany = new File(importDir, "Company.csv");
        File fileItemSubGroup = new File(importDir, "ItemSubGroup.csv");
        File fileItemSub = new File(importDir, "ItemSub.csv");
        File fileItemAndSub = new File(importDir, "ItemAndSub.csv");

        try {

            CSVReader csvReadWaiter = new CSVReader(new FileReader(fileWaiter));
            String[] nextLineWaiter;
            db.truncateWaiter();
            csvReadWaiter.readNext();
            while ((nextLineWaiter = csvReadWaiter.readNext()) != null) {
                int waiterid = Integer.parseInt(nextLineWaiter[0]);
                String waiterName = nextLineWaiter[1];
                String password = nextLineWaiter[2];
                db.insertWaiter(waiterid, waiterName, password);
            }

            CSVReader csvReadUserRight = new CSVReader(new FileReader(fileUserRight));
            String[] nextLineUserRight;
            db.truncateUserRight();
            csvReadUserRight.readNext();
            while ((nextLineUserRight = csvReadUserRight.readNext()) != null) {
                int waiterid = Integer.parseInt(nextLineUserRight[0]);
                int moduleid = Integer.parseInt(nextLineUserRight[1]);
                db.insertUserRight(waiterid, moduleid);
            }

            CSVReader csvReadTableType = new CSVReader(new FileReader(fileTableType));
            String[] nextLineTableType;
            db.truncateTableType();
            csvReadTableType.readNext();
            while ((nextLineTableType = csvReadTableType.readNext()) != null) {
                int tableTypeID = Integer.parseInt(nextLineTableType[0]);
                String tableTypeName = nextLineTableType[1];
                db.insertTableType(tableTypeID, tableTypeName);
            }

            CSVReader csvReadTable = new CSVReader(new FileReader(fileTable));
            String[] nextLineTable;
            db.truncateTable();
            csvReadTable.readNext();
            while ((nextLineTable = csvReadTable.readNext()) != null) {
                int tableID = Integer.parseInt(nextLineTable[0]);
                String tableName = nextLineTable[1];
                int tableTypeID = Integer.parseInt(nextLineTable[2]);
                db.insertTable(tableID, tableName, tableTypeID);
            }

            CSVReader csvReadMainMenu = new CSVReader(new FileReader(fileMainMenu));
            String[] nextLineMainMenu;
            db.truncateMainMenu();
            csvReadMainMenu.readNext();
            while ((nextLineMainMenu = csvReadMainMenu.readNext()) != null) {
                int mainMenuID = Integer.parseInt(nextLineMainMenu[0]);
                String mainMenuName = nextLineMainMenu[1];
                int counterID = Integer.parseInt(nextLineMainMenu[2]);
                int isAllow = Integer.parseInt(nextLineMainMenu[3]);
                db.insertMainMenu(mainMenuID, mainMenuName, counterID, isAllow);
            }

            CSVReader csvReadSubMenu = new CSVReader(new FileReader(fileSubMenu));
            String[] nextLineSubMenu;
            db.truncateSubMenu();
            csvReadSubMenu.readNext();
            while ((nextLineSubMenu = csvReadSubMenu.readNext()) != null) {
                int subMenuID = Integer.parseInt(nextLineSubMenu[0]);
                String subMenuName = nextLineSubMenu[1];
                int mainMenuID = Integer.parseInt(nextLineSubMenu[2]);
                String sortCode = nextLineSubMenu[3];
                db.insertSubMenu(subMenuID, subMenuName, mainMenuID, sortCode);
            }

            CSVReader csvReadItem = new CSVReader(new FileReader(fileItem));
            String[] nextLineItem;
            db.truncateItem();
            csvReadItem.readNext();
            while ((nextLineItem = csvReadItem.readNext()) != null) {
                int ID = Integer.parseInt(nextLineItem[0]);
                String itemid = nextLineItem[1];
                String itemName = nextLineItem[2];
                int subMenuID = Integer.parseInt(nextLineItem[3]);
                double price = Double.parseDouble(nextLineItem[4]);
                int outOfOrder = Integer.parseInt(nextLineItem[5]);
                int sTypeID = Integer.parseInt(nextLineItem[6]);
                db.insertItem(ID, itemid, itemName, subMenuID, price, outOfOrder, sTypeID);
            }

            CSVReader csvReadTaste = new CSVReader(new FileReader(fileTaste));
            String[] nextLineTaste;
            db.truncateTaste();
            csvReadTaste.readNext();
            while ((nextLineTaste = csvReadTaste.readNext()) != null) {
                int tasteID = Integer.parseInt(nextLineTaste[0]);
                String tasteName = nextLineTaste[1];
                db.insertTaste(tasteID, tasteName);
            }

            CSVReader csvReadCompany = new CSVReader(new FileReader(fileCompany));
            String[] nextLineCompany;
            db.truncateCompany();
            csvReadCompany.readNext();
            while ((nextLineCompany = csvReadCompany.readNext()) != null) {
                int companyID = Integer.parseInt(nextLineCompany[0]);
                String companyName = nextLineCompany[1];
                db.insertCompany(companyID, companyName);
            }

            CSVReader csvReadSystemSetting = new CSVReader(new FileReader(fileSystemSetting));
            String[] nextLineSystemSetting;
            db.truncateSystemSetting();
            csvReadSystemSetting.readNext();
            if ((nextLineSystemSetting = csvReadSystemSetting.readNext()) != null) {
                String shopName = nextLineSystemSetting[0];
                int tax = Integer.parseInt(nextLineSystemSetting[1]);
                int charges = Integer.parseInt(nextLineSystemSetting[2]);
                int companyid = Integer.parseInt(nextLineSystemSetting[3]);
                db.insertSystemSetting(shopName, companyid, tax, charges);
            }

            CSVReader csvReadVoucherSetting = new CSVReader(new FileReader(fileVoucherSetting));
            String[] nextLineVoucherSetting;
            db.truncateVoucherSetting();
            csvReadVoucherSetting.readNext();
            if ((nextLineVoucherSetting = csvReadVoucherSetting.readNext()) != null) {
                String title = nextLineVoucherSetting[0];
                String desp = nextLineVoucherSetting[1];
                String phone = nextLineVoucherSetting[2];
                String message = nextLineVoucherSetting[3];
                String address = nextLineVoucherSetting[4];
                String message2 = nextLineVoucherSetting[5];
                db.insertVoucherSetting(title, desp, phone, message, address, message2);
            }

            CSVReader csvReadSType = new CSVReader(new FileReader(fileSType));
            String[] nextLineSType;
            db.truncateItemSType();
            csvReadSType.readNext();
            while ((nextLineSType = csvReadSType.readNext()) != null) {
                int sTypeID = Integer.parseInt(nextLineSType[0]);
                String sTypeName = nextLineSType[1];
                db.insertSType(sTypeID, sTypeName);
            }

            CSVReader csvReadItemSubGroup = new CSVReader(new FileReader(fileItemSubGroup));
            String[] nextLineItemSubGroup;
            db.truncateItemSubGroup();
            csvReadItemSubGroup.readNext();
            while ((nextLineItemSubGroup = csvReadItemSubGroup.readNext()) != null) {
                int pkId = Integer.parseInt(nextLineItemSubGroup[0]);
                String groupName = nextLineItemSubGroup[1];
                String subTitle = nextLineItemSubGroup[2];
                int isSingleCheck = Integer.parseInt(nextLineItemSubGroup[3]);
                db.insertItemSubGroup(pkId,groupName,subTitle,isSingleCheck);
            }

            CSVReader csvReadItemSub = new CSVReader(new FileReader(fileItemSub));
            String[] nextLineItemSub;
            db.truncateItemSub();
            csvReadItemSub.readNext();
            while ((nextLineItemSub = csvReadItemSub.readNext()) != null) {
                int pkId = Integer.parseInt(nextLineItemSub[0]);
                int subGroupId = Integer.parseInt(nextLineItemSub[1]);
                String subName = nextLineItemSub[2];
                int price = Integer.parseInt(nextLineItemSub[3]);
                db.insertItemSub(pkId,subGroupId,subName,price);
            }

            CSVReader csvReadItemAndSub = new CSVReader(new FileReader(fileItemAndSub));
            String[] nextLineItemAndSub;
            db.truncateItemAndSub();
            csvReadItemAndSub.readNext();
            while ((nextLineItemAndSub = csvReadItemAndSub.readNext()) != null) {
                int pkId = Integer.parseInt(nextLineItemAndSub[0]);
                String itemId = nextLineItemAndSub[1];
                int subGroupId = Integer.parseInt(nextLineItemAndSub[2]);
                int levelNo = Integer.parseInt(nextLineItemAndSub[3]);
                db.insertItemAndSub(pkId,itemId,subGroupId,levelNo);
            }

            restoreItemImage(db);

        } catch (IOException e) {
            Log.e("ExportImportActivity", e.getMessage(), e);
        }
    }

    public void showMessage(int msg_type,String msg_text,Context context,LayoutInflater inflater){
        TextView tvMessage;
        View layout;
        if(msg_type==INFO) layout=inflater.inflate(R.layout.message_info, null);
        else if(msg_type==SUCCESS) layout=inflater.inflate(R.layout.message_success, null);
        else if(msg_type==WARNING) layout=inflater.inflate(R.layout.message_warning, null);
        else if(msg_type==ERROR) layout=inflater.inflate(R.layout.message_error, null);
        else layout=inflater.inflate(R.layout.message, null);
        tvMessage=(TextView)layout.findViewById(R.id.tvMessage);
        tvMessage.setText(msg_text);
        Toast toast=new Toast(context);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    private void backupItemImage(int itemId, Bitmap bitmapImage){
        File directory = new File(Environment.getExternalStorageDirectory().getPath(), "/WaiterOneDB/ItemImage");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File path=new File(directory,itemId+".png");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG,100,fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void restoreItemImage(DBHelper db) {
        File file;
        File[] listFile;
        List<Integer> lstItemID;
        String imageName,imagePath;
        ByteArrayOutputStream bytearrayoutputstream;
        byte[] imageByte;
        int id;

        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //Toast.makeText(this, "No SD Card", Toast.LENGTH_LONG).show();
            return;
        } else {
            file = new File(Environment.getExternalStorageDirectory().getPath() + "/WaiterOneDB/ItemImage");
        }

        if (file.isDirectory()) {
            listFile = file.listFiles();
            lstItemID = db.getAllIdInItem();
            for (int i = 0; i < lstItemID.size(); i++) {
                id=lstItemID.get(i);
                imageName = lstItemID.get(i) + ".png";

                if (listFile.length != 0) {
                    for (int k = 0; k < listFile.length; k++) {
                        if (listFile[k].getName().toString().equals(imageName)) {
                            bytearrayoutputstream = new ByteArrayOutputStream();
                            imagePath = listFile[k].getAbsolutePath();
                            Bitmap bitmap=BitmapFactory.decodeFile(imagePath);
                            bitmap.compress(Bitmap.CompressFormat.PNG,100,bytearrayoutputstream);
                            imageByte = bytearrayoutputstream.toByteArray();
                            db.updateItemImageById(id,imageByte);
                            break;
                        }
                    }
                }
            }
        }
    }
}
