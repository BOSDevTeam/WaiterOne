package common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import data.ItemAndSubData;
import data.ItemSubData;
import data.ItemSubGroupData;
import data.TransactionData;

/**
 * Created by NweYiAung on 28-02-2017.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME="WaiterOne";
    private Calendar cCalendar;

    public DBHelper(Context context){
        super(context,DATABASE_NAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE Waiter "+"(WaiterID integer primary key,WaiterName text,Password text)");
        db.execSQL("CREATE TABLE TableType "+"(TableTypeID integer primary key,TableTypeName text)");
        db.execSQL("CREATE TABLE tblTable "+"(TableID integer primary key,TableName text,TableTypeID integer)");
        db.execSQL("CREATE TABLE MainMenu "+"(MainMenuID integer primary key,MainMenuName text,isAllow integer,CounterID integer)");
        db.execSQL("CREATE TABLE SubMenu "+"(SubMenuID integer primary key,SubMenuName text,MainMenuID integer,SortCode text)");
        db.execSQL("CREATE TABLE ItemSType "+"(STypeID integer primary key,STypeName text)");
        db.execSQL("CREATE TABLE Item "+"(ID integer primary key,ItemID text,ItemName text,SubMenuID integer,Price real,OutofOrder int,STypeID int,Image blob)");
        db.execSQL("CREATE TABLE ItemSubGroup "+"(PKID integer primary key,GroupName text,SubTitle text,IsSingleCheck integer)");
        db.execSQL("CREATE TABLE ItemSub "+"(PKID integer primary key,SubGroupID integer,SubName text,Price integer)");
        db.execSQL("CREATE TABLE ItemAndSub "+"(PKID integer primary key,ItemID text,SubGroupID integer,LevelNo integer)");
        db.execSQL("CREATE TABLE Taste "+"(TasteID integer primary key,TasteName text)");
        db.execSQL("CREATE TABLE Company "+"(CompanyID integer primary key,CompanyName text)");
        db.execSQL("CREATE TABLE SystemSetting "+"(Tax integer,Service integer,ShopName text,CompanyID integer)");
        db.execSQL("CREATE TABLE VoucherSetting "+"(Title text,Description text,Phone text,Message text,Address text,Message2 text)");

        db.execSQL("CREATE TABLE AdminSetting"+"(AdminPassword text)");
        db.execSQL("CREATE TABLE Module"+"(ModuleID integer primary key,ModuleName text)");
        db.execSQL("CREATE TABLE UserRight"+"(UserID integer,ModuleID integer)");
        db.execSQL("CREATE TABLE IPSetting"+"(IPAddress text,User text,Password text,Database text)");
        db.execSQL("CREATE TABLE PrinterSetting "+"(PrinterIP text,PortNumber integer)");
        db.execSQL("CREATE TABLE ItemSTypePrinterSetting "+"(ID integer primary key,STypeID integer,PrinterID integer)");
        db.execSQL("CREATE TABLE FeatureSetting"+"(FeatureID integer primary key,FeatureName text,isAllow integer)");
        db.execSQL("CREATE TABLE Register "+"(RID integer primary key,MacAddress text,Key text)");
        db.execSQL("CREATE TABLE SystemTranID "+"(TranID integer)");
        db.execSQL("CREATE TABLE VouFormat"+"(ShopName2 text,Year2 text,Month2 text,CustomNo integer)");
        db.execSQL("CREATE TABLE SlipID "+"(ID integer)");
        db.execSQL("CREATE TABLE Report "+"(ID integer primary key,ReportName text)");
        db.execSQL("CREATE TABLE Customer "+"(ID integer primary key,TranID integer,TableID integer,WaiterID integer,Date text,Time text,Male int,Female int,Child int,Total int)");
        db.execSQL("CREATE TABLE Booking "+"(ID integer primary key,WaiterID int,TableID int,GuestName text,Phone text,Date text,Time text,People integer,Purpose text,Remark text,Deleted integer)");
        db.execSQL("CREATE TABLE Printer"+"(PrinterID integer primary key,PrinterName text,ModelID integer,InterfaceID integer,WidthID integer,PrinterAddress text,PrintCount integer,isReceipt integer)");
        db.execSQL("CREATE TABLE PrinterModel"+"(ModelID integer primary key,ModelName text,InterfaceID integer,WidthID integer)");
        db.execSQL("CREATE TABLE PrinterInterface"+"(InterfaceID integer primary key,InterfaceName text)");
        db.execSQL("CREATE TABLE PaperWidth"+"(WidthID integer primary key,WidthName text)");

        db.execSQL("CREATE TABLE MasterSale "+"(TranID integer,Date text,VouNo text,TableID integer,WaiterID integer,SubTotal real,Tax real,charges real,discount real,GrandTotal real,Time text,SlipID integer,StartTime text,EndTime text)");
        db.execSQL("CREATE TABLE TranSale "+"(TranID integer,SrNo integer,ItemID text,ItemName text,Quantity real,SalePrice real,Amount real,OrderTime text,Taste text,CounterID integer,Time text)");
        db.execSQL("CREATE TABLE MasterSaleTemp "+"(TranID integer,Date text,VouNo text,TableID integer,WaiterID integer,Time text,OrderState int,StartTime text)");
        db.execSQL("CREATE TABLE TranSaleTemp "+"(TranID integer,SrNo integer,ItemID text,ItemName text,Quantity real,SalePrice real,Amount real,OrderTime text,Taste text,CounterID integer,TableID integer,ItemDeleted integer,STypeID integer,OrderOut integer)");

        db.execSQL("CREATE TABLE TotalAmountByMainMenuTemp "+"(MainMenuID integer,TotalAmount real)");
        db.execSQL("CREATE TABLE TotalAmountBySubMenuTemp "+"(SubMenuID integer,TotalAmount real)");
        db.execSQL("CREATE TABLE SaleByHourlyMasterTemp "+"(CTime text,TotalTransaction int,TotalCustomer int)");
        db.execSQL("CREATE TABLE SaleByHourlyTranTemp "+"(CTime text,TotalItem int,TotalAmount real)");
        db.execSQL("CREATE TABLE SaleByHourlyTemp "+"(CTime text,TotalTransaction int,TotalItem int,TotalAmount real,TotalCustomer int)");
        db.execSQL("CREATE TABLE SaleByWaiterTemp "+"(WaiterID int,TotalAmount real,TotalSaleTable int)");
        db.execSQL("CREATE TABLE SaleByTableTypeTemp "+"(TableTypeID int,TotalByTableType real)");
        db.execSQL("CREATE TABLE SaleByTableTemp "+"(TableID int,TotalByTable real)");
        db.execSQL("CREATE TABLE BestWaiterTemp "+"(WaiterID int,TotalSaleTable int,TotalCustomer int,Total int)");
        db.execSQL("CREATE TABLE TodayCustomerTotalAndSaleTableCountTemp "+"(TodayMaleTotal int,TodayFemaleTotal int,TodayChildTotal int,TodayCustomerTotal int,TodaySaleTableCount int)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
        onCreate(db);
    }

    //Report
    public Cursor getReportDayEnd(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateFormat=new SimpleDateFormat(SystemSetting.DATE_FORMAT);
        String date=dateFormat.format(c.getTime());

        SQLiteDatabase dbRead=this.getReadableDatabase();
        Cursor cur=dbRead.rawQuery("SELECT SlipID,SubTotal,Tax,charges,discount,GrandTotal FROM MasterSale WHERE Date='"+date+"'",null);
        return cur;
    }
    public Cursor getReportBestWaiter(String fromDate,String toDate){
        int total,maxTotal=0;
        SQLiteDatabase dbRead=this.getReadableDatabase();
        SQLiteDatabase dbWrite=this.getWritableDatabase();
        dbWrite.execSQL("delete from BestWaiterTemp");
        Cursor cur=dbRead.rawQuery("SELECT ms.WaiterID,COUNT(ms.TranID),SUM(cus.Total) FROM MasterSale ms LEFT JOIN Customer cus ON ms.TranID=cus.TranID WHERE DATE(ms.Date) BETWEEN DATE('"+fromDate+"') AND DATE('"+toDate+"') GROUP BY ms.WaiterID ORDER BY ms.WaiterID",null);
        while(cur.moveToNext()){
            ContentValues value=new ContentValues();
            value.put("WaiterID", cur.getInt(0));
            value.put("TotalSaleTable",cur.getInt(1));
            value.put("TotalCustomer",cur.getInt(2));
            total=cur.getInt(1)+cur.getInt(2);
            value.put("Total",total);
            dbWrite.insert("BestWaiterTemp", null, value);
        }
        Cursor cur_max=dbRead.rawQuery("SELECT MAX(Total) FROM BestWaiterTemp",null);
        if(cur_max.moveToFirst()){
            maxTotal=cur_max.getInt(0);
        }
        Cursor cur_best=dbRead.rawQuery("SELECT temp.WaiterID,waiter.WaiterName,TotalSaleTable,TotalCustomer,Total FROM BestWaiterTemp temp INNER JOIN Waiter waiter ON temp.WaiterID=waiter.WaiterID WHERE temp.Total="+maxTotal,null);
        return cur_best;
    }
    public Cursor getReportTodayCustomerTotalAndSaleTableCount(){
        int male=0,female=0,child=0,totalCustomer=0,saleTableCount=0;
        SQLiteDatabase dbWrite=this.getWritableDatabase();
        SQLiteDatabase dbRead=this.getReadableDatabase();
        cCalendar=Calendar.getInstance();
        SimpleDateFormat dateFormat=new SimpleDateFormat(SystemSetting.DATE_FORMAT);
        String date=dateFormat.format(cCalendar.getTime());
        Cursor cur_customer=dbRead.rawQuery("SELECT SUM(Male),SUM(Female),SUM(Child),SUM(Total) FROM Customer WHERE Date(Date)='"+date+"'",null);
        if(cur_customer.moveToFirst()){
            male=cur_customer.getInt(0);
            female=cur_customer.getInt(1);
            child=cur_customer.getInt(2);
            totalCustomer=cur_customer.getInt(3);
        }
        Cursor cur_table_count=dbRead.rawQuery("SELECT COUNT(TranID) FROM MasterSale WHERE Date(Date)='"+date+"'",null);
        if(cur_table_count.moveToFirst()){
            saleTableCount=cur_table_count.getInt(0);
        }
        ContentValues value=new ContentValues();
        value.put("TodayMaleTotal", male);
        value.put("TodayFemaleTotal",female);
        value.put("TodayChildTotal",child);
        value.put("TodayCustomerTotal",totalCustomer);
        value.put("TodaySaleTableCount",saleTableCount);
        dbWrite.insert("TodayCustomerTotalAndSaleTableCountTemp", null, value);

        Cursor cur=dbRead.rawQuery("SELECT TodayMaleTotal,TodayFemaleTotal,TodayChildTotal,TodayCustomerTotal,TodaySaleTableCount FROM TodayCustomerTotalAndSaleTableCountTemp",null);
        return cur;
    }
    public Cursor getReportWaiterPerformance(String fromDate,String toDate){
        SQLiteDatabase dbRead=this.getReadableDatabase();
        Cursor cur=dbRead.rawQuery("SELECT ms.WaiterID,waiter.WaiterName,COUNT(ms.TranID),SUM(cus.Total) FROM MasterSale ms INNER JOIN Waiter waiter ON ms.WaiterID=waiter.WaiterID LEFT JOIN Customer cus ON ms.TranID=cus.TranID WHERE DATE(ms.Date) BETWEEN DATE('"+fromDate+"') AND DATE('"+toDate+"') GROUP BY ms.WaiterID,waiter.WaiterName ORDER BY ms.WaiterID",null);
        return cur;
    }
    public Cursor getReportSaleAmountOnly(String fromDate,String toDate){
        SQLiteDatabase db=this.getReadableDatabase();
        //Cursor cur=db.rawQuery("SELECT SUM(SubTotal),SUM(Tax),SUM(charges),SUM(GrandTotal) FROM MasterSale WHERE DATE(substr(Date,7,4) ||'-'|| substr(Date,4,2) ||'-'|| substr(Date,1,2)) BETWEEN DATE('"+fromDate+"') AND DATE('"+toDate+"')",null);
        Cursor cur=db.rawQuery("SELECT SUM(SubTotal),SUM(Tax),SUM(charges),SUM(GrandTotal),SUM(discount) FROM MasterSale WHERE DATE(Date) BETWEEN DATE('"+fromDate+"') AND DATE('"+toDate+"')",null);
        return cur;
    }
    public Cursor getReportSaleInvoice(String fromDate,String toDate){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("SELECT ms.TranID,VouNo,Date,waiter.WaiterName,tab.TableName,SubTotal,Tax,charges,GrandTotal,ts.ItemName,Sum(Quantity),SalePrice,SUM(ts.Amount),discount FROM MasterSale ms INNER JOIN TranSale ts ON ms.TranID=ts.TranID INNER JOIN Item item ON ts.ItemID=item.ItemID INNER JOIN Waiter waiter ON ms.WaiterID=waiter.WaiterID INNER JOIN tblTable tab ON ms.TableID=tab.TableID WHERE DATE(ms.Date) BETWEEN DATE('"+fromDate+"') AND DATE('"+toDate+"') GROUP BY ms.TranID,VouNo,Date,waiter.WaiterName,tab.TableName,SubTotal,Tax,charges,GrandTotal,ts.ItemName,SalePrice",null);
        return cur;
    }
    public Cursor getReportDetailedSale(String fromDate,String toDate){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("SELECT ms.Date,waiter.WaiterName,tab.TableName,cus.Total,ms.SubTotal,ms.Tax,ms.charges,ms.GrandTotal,ms.discount FROM MasterSale ms INNER JOIN Waiter waiter ON ms.WaiterID=waiter.WaiterID INNER JOIN tblTable tab ON ms.TableID=tab.TableID LEFT JOIN Customer cus ON ms.TranID=cus.TranID WHERE DATE(ms.Date) BETWEEN DATE('"+fromDate+"') AND DATE('"+toDate+"')",null);
        return cur;
    }
    private void calculateTotalAmountByMainMenu(String fromDate,String toDate,String itemIDList){
        SQLiteDatabase dbRead=this.getReadableDatabase();
        SQLiteDatabase dbWrite=this.getWritableDatabase();
        dbWrite.execSQL("delete from TotalAmountByMainMenuTemp");
        Cursor cur=dbRead.rawQuery("SELECT sm.MainMenuID,SUM(ts.Amount) FROM TranSale ts INNER JOIN Item item ON ts.ItemID=item.ItemID INNER JOIN SubMenu sm ON item.SubMenuID=sm.SubMenuID INNER JOIN MasterSale ms ON ts.TranID=ms.TranID WHERE DATE(ms.Date) BETWEEN DATE('"+fromDate+"') AND DATE('"+toDate+"') AND item.ItemID IN("+itemIDList+") GROUP BY sm.MainMenuID",null);
        while(cur.moveToNext()){
            ContentValues value=new ContentValues();
            value.put("MainMenuID", cur.getInt(0));
            value.put("TotalAmount",cur.getDouble(1));
            dbWrite.insert("TotalAmountByMainMenuTemp", null, value);
        }
    }
    private void calculateTotalAmountBySubMenu(String fromDate,String toDate,String itemIDList){
        SQLiteDatabase dbRead=this.getReadableDatabase();
        SQLiteDatabase dbWrite=this.getWritableDatabase();
        dbWrite.execSQL("delete from TotalAmountBySubMenuTemp");
        Cursor cur=dbRead.rawQuery("SELECT sm.SubMenuID,SUM(ts.Amount) FROM TranSale ts INNER JOIN Item item ON ts.ItemID=item.ItemID INNER JOIN SubMenu sm ON item.SubMenuID=sm.SubMenuID INNER JOIN MasterSale ms ON ts.TranID=ms.TranID WHERE DATE(ms.Date) BETWEEN DATE('"+fromDate+"') AND DATE('"+toDate+"') AND item.ItemID IN("+itemIDList+") GROUP BY sm.SubMenuID",null);
        while(cur.moveToNext()){
            ContentValues value=new ContentValues();
            value.put("SubMenuID", cur.getInt(0));
            value.put("TotalAmount",cur.getDouble(1));
            dbWrite.insert("TotalAmountBySubMenuTemp", null, value);
        }
    }
    public Cursor getReportSummaryItem(String fromDate,String toDate,String itemIDList){
        SQLiteDatabase dbRead=this.getReadableDatabase();
        calculateTotalAmountByMainMenu(fromDate,toDate,itemIDList);
        calculateTotalAmountBySubMenu(fromDate,toDate,itemIDList);
        Cursor cur=dbRead.rawQuery("SELECT item.ItemID,item.ItemName,sm.SubMenuName,mm.MainMenuName,SUM(ts.Quantity),ts.SalePrice,SUM(ts.Amount),mmt.MainMenuID,mmt.TotalAmount,smt.SubMenuID,smt.TotalAmount FROM TranSale ts INNER JOIN Item item ON ts.ItemID=item.ItemID INNER JOIN SubMenu sm ON item.SubMenuID=sm.SubMenuID INNER JOIN MainMenu mm ON sm.MainMenuID=mm.MainMenuID INNER JOIN TotalAmountByMainMenuTemp mmt ON mmt.MainMenuID=sm.MainMenuID INNER JOIN TotalAmountBySubMenuTemp smt ON smt.SubMenuID=sm.SubMenuID INNER JOIN MasterSale ms ON ts.TranID=ms.TranID WHERE ms.Date BETWEEN DATE('"+fromDate+"') AND DATE('"+toDate+"') AND item.ItemID IN("+itemIDList+") GROUP BY item.ItemID,item.ItemName,sm.SubMenuName,mm.MainMenuName,ts.SalePrice,mmt.MainMenuID,mmt.TotalAmount,smt.SubMenuID,smt.TotalAmount ORDER BY sm.MainMenuID,sm.SubMenuID",null);
        return cur;
    }
    private void calculateTotalAmountByMainMenu(String fromDate,String toDate,String itemIDList,String itemName){
        SQLiteDatabase dbRead=this.getReadableDatabase();
        SQLiteDatabase dbWrite=this.getWritableDatabase();
        dbWrite.execSQL("delete from TotalAmountByMainMenuTemp");
        Cursor cur=dbRead.rawQuery("SELECT sm.MainMenuID,SUM(ts.Amount) FROM TranSale ts INNER JOIN Item item ON ts.ItemID=item.ItemID INNER JOIN SubMenu sm ON item.SubMenuID=sm.SubMenuID INNER JOIN MasterSale ms ON ts.TranID=ms.TranID WHERE DATE(ms.Date) BETWEEN DATE('"+fromDate+"') AND DATE('"+toDate+"') AND item.ItemID IN("+itemIDList+") AND item.ItemName LIKE '%"+itemName+"%' GROUP BY sm.MainMenuID",null);
        while(cur.moveToNext()){
            ContentValues value=new ContentValues();
            value.put("MainMenuID", cur.getInt(0));
            value.put("TotalAmount",cur.getDouble(1));
            dbWrite.insert("TotalAmountByMainMenuTemp", null, value);
        }
    }
    private void calculateTotalAmountBySubMenu(String fromDate,String toDate,String itemIDList,String itemName){
        SQLiteDatabase dbRead=this.getReadableDatabase();
        SQLiteDatabase dbWrite=this.getWritableDatabase();
        dbWrite.execSQL("delete from TotalAmountBySubMenuTemp");
        Cursor cur=dbRead.rawQuery("SELECT sm.SubMenuID,SUM(ts.Amount) FROM TranSale ts INNER JOIN Item item ON ts.ItemID=item.ItemID INNER JOIN SubMenu sm ON item.SubMenuID=sm.SubMenuID INNER JOIN MasterSale ms ON ts.TranID=ms.TranID WHERE DATE(ms.Date) BETWEEN DATE('"+fromDate+"') AND DATE('"+toDate+"') AND item.ItemID IN("+itemIDList+") AND item.ItemName LIKE '%"+itemName+"%' GROUP BY sm.SubMenuID",null);
        while(cur.moveToNext()){
            ContentValues value=new ContentValues();
            value.put("SubMenuID", cur.getInt(0));
            value.put("TotalAmount",cur.getDouble(1));
            dbWrite.insert("TotalAmountBySubMenuTemp", null, value);
        }
    }
    public Cursor getReportSummaryItem(String fromDate,String toDate,String itemIDList,String itemName){
        SQLiteDatabase dbRead=this.getReadableDatabase();
        calculateTotalAmountByMainMenu(fromDate,toDate,itemIDList,itemName);
        calculateTotalAmountBySubMenu(fromDate,toDate,itemIDList,itemName);
        Cursor cur=dbRead.rawQuery("SELECT item.ItemID,item.ItemName,sm.SubMenuName,mm.MainMenuName,SUM(ts.Quantity),ts.SalePrice,SUM(ts.Quantity)*ts.SalePrice,mmt.MainMenuID,mmt.TotalAmount,smt.SubMenuID,smt.TotalAmount FROM TranSale ts INNER JOIN Item item ON ts.ItemID=item.ItemID INNER JOIN SubMenu sm ON item.SubMenuID=sm.SubMenuID INNER JOIN MainMenu mm ON sm.MainMenuID=mm.MainMenuID INNER JOIN TotalAmountByMainMenuTemp mmt ON mmt.MainMenuID=sm.MainMenuID INNER JOIN TotalAmountBySubMenuTemp smt ON smt.SubMenuID=sm.SubMenuID INNER JOIN MasterSale ms ON ts.TranID=ms.TranID WHERE DATE(ms.Date) BETWEEN DATE('"+fromDate+"') AND DATE('"+toDate+"') AND item.ItemID IN("+itemIDList+") AND item.ItemName LIKE '%"+itemName+"%' GROUP BY item.ItemID,item.ItemName,sm.SubMenuName,mm.MainMenuName,ts.SalePrice,mmt.MainMenuID,mmt.TotalAmount,smt.SubMenuID,smt.TotalAmount ORDER BY sm.MainMenuID,sm.SubMenuID",null);
        return cur;
    }
    public Cursor getReportTopSaleItem(String fromDate,String toDate,int number,int rankTypeID){
        SQLiteDatabase dbRead=this.getReadableDatabase();
        String query="SELECT item.ItemName,sm.SubMenuName,SUM(ts.Quantity),item.ItemID FROM MasterSale ms INNER JOIN TranSale ts ON ms.TranID=ts.TranID INNER JOIN Item item ON ts.ItemID=item.ItemID INNER JOIN SubMenu sm ON item.SubMenuID=sm.SubMenuID WHERE DATE(ms.Date) BETWEEN DATE('"+fromDate+"') AND DATE('"+toDate+"') GROUP BY item.ItemName,sm.SubMenuName,item.ItemID ORDER BY SUM(ts.Quantity) DESC LIMIT "+number;
        Cursor cur;
        /**if(rankTypeID==1) query="SELECT item.ItemName,sm.SubMenuName,SUM(ts.Quantity),item.ItemID FROM MasterSale ms INNER JOIN TranSale ts ON ms.TranID=ts.TranID INNER JOIN Item item ON ts.ItemID=item.ItemID INNER JOIN SubMenu sm ON item.SubMenuID=sm.SubMenuID WHERE DATE(substr(Date,7,4) ||'-'|| substr(Date,4,2) ||'-'|| substr(Date,1,2)) BETWEEN DATE('"+fromDate+"') AND DATE('"+toDate+"') GROUP BY item.ItemName,sm.SubMenuName,item.ItemID ORDER BY Quantity DESC,item.ItemID LIMIT "+number;
        else if(rankTypeID==2)query="SELECT item.ItemName,sm.SubMenuName,SUM(ts.Quantity),item.ItemID FROM MasterSale ms INNER JOIN TranSale ts ON ms.TranID=ts.TranID INNER JOIN Item item ON ts.ItemID=item.ItemID INNER JOIN SubMenu sm ON item.SubMenuID=sm.SubMenuID WHERE DATE(substr(Date,7,4) ||'-'|| substr(Date,4,2) ||'-'|| substr(Date,1,2)) BETWEEN DATE('"+fromDate+"') AND DATE('"+toDate+"') GROUP BY item.ItemName,sm.SubMenuName,item.ItemID ORDER BY Quantity DESC,item.ItemName LIMIT "+number;
        else if(rankTypeID==3)query="SELECT item.ItemName,sm.SubMenuName,SUM(ts.Quantity),item.ItemID FROM MasterSale ms INNER JOIN TranSale ts ON ms.TranID=ts.TranID INNER JOIN Item item ON ts.ItemID=item.ItemID INNER JOIN SubMenu sm ON item.SubMenuID=sm.SubMenuID WHERE DATE(substr(Date,7,4) ||'-'|| substr(Date,4,2) ||'-'|| substr(Date,1,2)) BETWEEN DATE('"+fromDate+"') AND DATE('"+toDate+"') GROUP BY item.ItemName,sm.SubMenuName,item.ItemID ORDER BY Quantity DESC,sm.SubMenuName LIMIT "+number;**/
        cur=dbRead.rawQuery(query,null);
        return cur;
    }
    public Cursor getReportBottomSaleItem(String fromDate,String toDate,int number,int rankTypeID){
        SQLiteDatabase dbRead=this.getReadableDatabase();
        String query="SELECT item.ItemName,sm.SubMenuName,SUM(ts.Quantity),item.ItemID FROM MasterSale ms INNER JOIN TranSale ts ON ms.TranID=ts.TranID INNER JOIN Item item ON ts.ItemID=item.ItemID INNER JOIN SubMenu sm ON item.SubMenuID=sm.SubMenuID WHERE DATE(ms.Date) BETWEEN DATE('"+fromDate+"') AND DATE('"+toDate+"') GROUP BY item.ItemName,sm.SubMenuName,item.ItemID ORDER BY SUM(ts.Quantity) ASC LIMIT "+number;
        Cursor cur;
        /**if(rankTypeID==1) query="SELECT item.ItemName,sm.SubMenuName,SUM(ts.Quantity),item.ItemID FROM MasterSale ms INNER JOIN TranSale ts ON ms.TranID=ts.TranID INNER JOIN Item item ON ts.ItemID=item.ItemID INNER JOIN SubMenu sm ON item.SubMenuID=sm.SubMenuID WHERE DATE(substr(Date,7,4) ||'-'|| substr(Date,4,2) ||'-'|| substr(Date,1,2)) BETWEEN DATE('"+fromDate+"') AND DATE('"+toDate+"') GROUP BY item.ItemName,sm.SubMenuName,item.ItemID ORDER BY Quantity ASC,item.ItemID LIMIT "+number;
        else if(rankTypeID==2)query="SELECT item.ItemName,sm.SubMenuName,SUM(ts.Quantity),item.ItemID FROM MasterSale ms INNER JOIN TranSale ts ON ms.TranID=ts.TranID INNER JOIN Item item ON ts.ItemID=item.ItemID INNER JOIN SubMenu sm ON item.SubMenuID=sm.SubMenuID WHERE DATE(substr(Date,7,4) ||'-'|| substr(Date,4,2) ||'-'|| substr(Date,1,2)) BETWEEN DATE('"+fromDate+"') AND DATE('"+toDate+"') GROUP BY item.ItemName,sm.SubMenuName,item.ItemID ORDER BY Quantity ASC,item.ItemName LIMIT "+number;
        else if(rankTypeID==3)query="SELECT item.ItemName,sm.SubMenuName,SUM(ts.Quantity),item.ItemID FROM MasterSale ms INNER JOIN TranSale ts ON ms.TranID=ts.TranID INNER JOIN Item item ON ts.ItemID=item.ItemID INNER JOIN SubMenu sm ON item.SubMenuID=sm.SubMenuID WHERE DATE(substr(Date,7,4) ||'-'|| substr(Date,4,2) ||'-'|| substr(Date,1,2)) BETWEEN DATE('"+fromDate+"') AND DATE('"+toDate+"') GROUP BY item.ItemName,sm.SubMenuName,item.ItemID ORDER BY Quantity ASC,sm.SubMenuName LIMIT "+number;**/
        cur=dbRead.rawQuery(query,null);
        return cur;
    }
    public Cursor getReportTopSaleMenu(String fromDate,String toDate,int number,int rankTypeID){
        SQLiteDatabase dbRead=this.getReadableDatabase();
        String query="SELECT sm.SubMenuName,mm.MainMenuName,SUM(ts.Quantity),sm.SubMenuID FROM MasterSale ms INNER JOIN TranSale ts ON ms.TranID=ts.TranID INNER JOIN Item item ON ts.ItemID=item.ItemID INNER JOIN SubMenu sm ON item.SubMenuID=sm.SubMenuID INNER JOIN MainMenu mm ON sm.MainMenuID=mm.MainMenuID WHERE DATE(ms.Date) BETWEEN DATE('"+fromDate+"') AND DATE('"+toDate+"') GROUP BY sm.SubMenuName,mm.MainMenuName,sm.SubMenuID ORDER BY SUM(ts.Quantity) DESC LIMIT "+number;
        Cursor cur;
        /**if(rankTypeID==1) query="SELECT sm.SubMenuName,mm.MainMenuName,SUM(ts.Quantity),sm.SubMenuID FROM MasterSale ms INNER JOIN TranSale ts ON ms.TranID=ts.TranID INNER JOIN Item item ON ts.ItemID=item.ItemID INNER JOIN SubMenu sm ON item.SubMenuID=sm.SubMenuID INNER JOIN MainMenu mm ON sm.MainMenuID=mm.MainMenuID WHERE DATE(substr(Date,7,4) ||'-'|| substr(Date,4,2) ||'-'|| substr(Date,1,2)) BETWEEN DATE('"+fromDate+"') AND DATE('"+toDate+"') GROUP BY sm.SubMenuName,mm.MainMenuName,sm.SubMenuID ORDER BY Quantity DESC,sm.SubMenuID LIMIT "+number;
        else if(rankTypeID==2)query="SELECT sm.SubMenuName,mm.MainMenuName,SUM(ts.Quantity),sm.SubMenuID FROM MasterSale ms INNER JOIN TranSale ts ON ms.TranID=ts.TranID INNER JOIN Item item ON ts.ItemID=item.ItemID INNER JOIN SubMenu sm ON item.SubMenuID=sm.SubMenuID INNER JOIN MainMenu mm ON sm.MainMenuID=mm.MainMenuID WHERE DATE(substr(Date,7,4) ||'-'|| substr(Date,4,2) ||'-'|| substr(Date,1,2)) BETWEEN DATE('"+fromDate+"') AND DATE('"+toDate+"') GROUP BY sm.SubMenuName,mm.MainMenuName,sm.SubMenuID ORDER BY Quantity DESC,sm.SubMenuName LIMIT "+number;
        else if(rankTypeID==3)query="SELECT sm.SubMenuName,mm.MainMenuName,SUM(ts.Quantity),sm.SubMenuID FROM MasterSale ms INNER JOIN TranSale ts ON ms.TranID=ts.TranID INNER JOIN Item item ON ts.ItemID=item.ItemID INNER JOIN SubMenu sm ON item.SubMenuID=sm.SubMenuID INNER JOIN MainMenu mm ON sm.MainMenuID=mm.MainMenuID WHERE DATE(substr(Date,7,4) ||'-'|| substr(Date,4,2) ||'-'|| substr(Date,1,2)) BETWEEN DATE('"+fromDate+"') AND DATE('"+toDate+"') GROUP BY sm.SubMenuName,mm.MainMenuName,sm.SubMenuID ORDER BY Quantity DESC,mm.MainMenuName LIMIT "+number;**/
        cur=dbRead.rawQuery(query,null);
        return cur;
    }
    public Cursor getReportBottomSaleMenu(String fromDate,String toDate,int number,int rankTypeID){
        SQLiteDatabase dbRead=this.getReadableDatabase();
        String query="SELECT sm.SubMenuName,mm.MainMenuName,SUM(ts.Quantity),sm.SubMenuID FROM MasterSale ms INNER JOIN TranSale ts ON ms.TranID=ts.TranID INNER JOIN Item item ON ts.ItemID=item.ItemID INNER JOIN SubMenu sm ON item.SubMenuID=sm.SubMenuID INNER JOIN MainMenu mm ON sm.MainMenuID=mm.MainMenuID WHERE DATE(ms.Date) BETWEEN DATE('"+fromDate+"') AND DATE('"+toDate+"') GROUP BY sm.SubMenuName,mm.MainMenuName,sm.SubMenuID ORDER BY SUM(ts.Quantity) ASC LIMIT "+number;
        Cursor cur;
        /**if(rankTypeID==1) query="SELECT sm.SubMenuName,mm.MainMenuName,SUM(ts.Quantity),sm.SubMenuID FROM MasterSale ms INNER JOIN TranSale ts ON ms.TranID=ts.TranID INNER JOIN Item item ON ts.ItemID=item.ItemID INNER JOIN SubMenu sm ON item.SubMenuID=sm.SubMenuID INNER JOIN MainMenu mm ON sm.MainMenuID=mm.MainMenuID WHERE DATE(substr(Date,7,4) ||'-'|| substr(Date,4,2) ||'-'|| substr(Date,1,2)) BETWEEN DATE('"+fromDate+"') AND DATE('"+toDate+"') GROUP BY sm.SubMenuName,mm.MainMenuName,sm.SubMenuID ORDER BY SUM(ts.Quantity) ASC,sm.SubMenuID LIMIT "+number;
        else if(rankTypeID==2)query="SELECT sm.SubMenuName,mm.MainMenuName,SUM(ts.Quantity),sm.SubMenuID FROM MasterSale ms INNER JOIN TranSale ts ON ms.TranID=ts.TranID INNER JOIN Item item ON ts.ItemID=item.ItemID INNER JOIN SubMenu sm ON item.SubMenuID=sm.SubMenuID INNER JOIN MainMenu mm ON sm.MainMenuID=mm.MainMenuID WHERE DATE(substr(Date,7,4) ||'-'|| substr(Date,4,2) ||'-'|| substr(Date,1,2)) BETWEEN DATE('"+fromDate+"') AND DATE('"+toDate+"') GROUP BY sm.SubMenuName,mm.MainMenuName,sm.SubMenuID ORDER BY SUM(ts.Quantity) ASC,sm.SubMenuName LIMIT "+number;
        else if(rankTypeID==3)query="SELECT sm.SubMenuName,mm.MainMenuName,SUM(ts.Quantity),sm.SubMenuID FROM MasterSale ms INNER JOIN TranSale ts ON ms.TranID=ts.TranID INNER JOIN Item item ON ts.ItemID=item.ItemID INNER JOIN SubMenu sm ON item.SubMenuID=sm.SubMenuID INNER JOIN MainMenu mm ON sm.MainMenuID=mm.MainMenuID WHERE DATE(substr(Date,7,4) ||'-'|| substr(Date,4,2) ||'-'|| substr(Date,1,2)) BETWEEN DATE('"+fromDate+"') AND DATE('"+toDate+"') GROUP BY sm.SubMenuName,mm.MainMenuName,sm.SubMenuID ORDER BY SUM(ts.Quantity) ASC,mm.MainMenuName LIMIT "+number;**/
        cur=dbRead.rawQuery(query,null);
        return cur;
    }
    private void calculateTotalAmountByMainMenuForSaleByMenu(String fromDate,String toDate,String subMenuIDList){
        SQLiteDatabase dbRead=this.getReadableDatabase();
        SQLiteDatabase dbWrite=this.getWritableDatabase();
        dbWrite.execSQL("delete from TotalAmountByMainMenuTemp");
        Cursor cur=dbRead.rawQuery("SELECT sm.MainMenuID,SUM(ts.Amount) FROM TranSale ts INNER JOIN Item item ON ts.ItemID=item.ItemID INNER JOIN SubMenu sm ON item.SubMenuID=sm.SubMenuID INNER JOIN MasterSale ms ON ts.TranID=ms.TranID WHERE DATE(ms.Date) BETWEEN DATE('"+fromDate+"') AND DATE('"+toDate+"') AND sm.SubMenuID IN("+subMenuIDList+") GROUP BY sm.MainMenuID",null);
        while(cur.moveToNext()){
            ContentValues value=new ContentValues();
            value.put("MainMenuID", cur.getInt(0));
            value.put("TotalAmount",cur.getDouble(1));
            dbWrite.insert("TotalAmountByMainMenuTemp", null, value);
        }
    }
    private void calculateTotalAmountBySubMenuForSaleByMenu(String fromDate,String toDate,String subMenuIDList){
        SQLiteDatabase dbRead=this.getReadableDatabase();
        SQLiteDatabase dbWrite=this.getWritableDatabase();
        dbWrite.execSQL("delete from TotalAmountBySubMenuTemp");
        Cursor cur=dbRead.rawQuery("SELECT sm.SubMenuID,SUM(ts.Amount) FROM TranSale ts INNER JOIN Item item ON ts.ItemID=item.ItemID INNER JOIN SubMenu sm ON item.SubMenuID=sm.SubMenuID INNER JOIN MasterSale ms ON ts.TranID=ms.TranID WHERE DATE(ms.Date) BETWEEN DATE('"+fromDate+"') AND DATE('"+toDate+"') AND sm.SubMenuID IN("+subMenuIDList+") GROUP BY sm.SubMenuID",null);
        while(cur.moveToNext()){
            ContentValues value=new ContentValues();
            value.put("SubMenuID", cur.getInt(0));
            value.put("TotalAmount",cur.getDouble(1));
            dbWrite.insert("TotalAmountBySubMenuTemp", null, value);
        }
    }
    public Cursor getReportSaleByMenu(String fromDate,String toDate,String subMenuIDList){
        SQLiteDatabase dbRead=this.getReadableDatabase();
        calculateTotalAmountByMainMenuForSaleByMenu(fromDate,toDate,subMenuIDList);
        calculateTotalAmountBySubMenuForSaleByMenu(fromDate,toDate,subMenuIDList);
        Cursor cur=dbRead.rawQuery("SELECT item.ItemID,item.ItemName,sm.SubMenuName,mm.MainMenuName,SUM(ts.Quantity),SUM(ts.Amount),mmt.MainMenuID,mmt.TotalAmount,smt.SubMenuID,smt.TotalAmount FROM TranSale ts INNER JOIN Item item ON ts.ItemID=item.ItemID INNER JOIN SubMenu sm ON item.SubMenuID=sm.SubMenuID INNER JOIN MainMenu mm ON sm.MainMenuID=mm.MainMenuID INNER JOIN TotalAmountByMainMenuTemp mmt ON mmt.MainMenuID=sm.MainMenuID INNER JOIN TotalAmountBySubMenuTemp smt ON smt.SubMenuID=sm.SubMenuID INNER JOIN MasterSale ms ON ts.TranID=ms.TranID WHERE DATE(ms.Date) BETWEEN DATE('"+fromDate+"') AND DATE('"+toDate+"') AND sm.SubMenuID IN("+subMenuIDList+") GROUP BY item.ItemID,item.ItemName,sm.SubMenuName,mm.MainMenuName,mmt.MainMenuID,mmt.TotalAmount,smt.SubMenuID,smt.TotalAmount ORDER BY sm.MainMenuID,sm.SubMenuID",null);
        return cur;
    }
    public void getReportSaleByHourly(String fromDate,String toDate){
        String time="",sTime;
        int iTime;
        SQLiteDatabase dbRead=this.getReadableDatabase();
        SQLiteDatabase dbWrite=this.getWritableDatabase();
        dbWrite.execSQL("delete from SaleByHourlyMasterTemp");
        dbWrite.execSQL("delete from SaleByHourlyTranTemp");
        Cursor cur_ms=dbRead.rawQuery("SELECT ms.Time,COUNT(ms.TranID),SUM(cus.Total) FROM MasterSale ms LEFT JOIN Customer cus ON ms.TranID=cus.TranID WHERE DATE(ms.Date) BETWEEN DATE('"+fromDate+"') AND DATE('"+toDate+"') GROUP BY ms.Time",null);
        while(cur_ms.moveToNext()){
            ContentValues value=new ContentValues();
            value.put("CTime", cur_ms.getString(0));
            value.put("TotalTransaction",cur_ms.getInt(1));
            value.put("TotalCustomer",cur_ms.getInt(2));
            dbWrite.insert("SaleByHourlyMasterTemp", null, value);
        }
        Cursor cur_ts=dbRead.rawQuery("SELECT ts.Time,SUM(ts.Quantity),SUM(ts.Amount) FROM MasterSale ms INNER JOIN TranSale ts ON ms.TranID=ts.TranID WHERE DATE(Date) BETWEEN DATE('"+fromDate+"') AND DATE('"+toDate+"') GROUP BY ts.Time",null);
        while(cur_ts.moveToNext()){
            ContentValues value=new ContentValues();
            value.put("CTime", cur_ts.getString(0));
            value.put("TotalItem",cur_ts.getInt(1));
            value.put("TotalAmount",cur_ts.getDouble(2));
            dbWrite.insert("SaleByHourlyTranTemp", null, value);
        }
        Cursor cur=dbRead.rawQuery("SELECT ms.CTime,ms.TotalTransaction,ts.TotalItem,ts.TotalAmount,ms.TotalCustomer FROM SaleByHourlyMasterTemp ms INNER JOIN SaleByHourlyTranTemp ts ON ms.CTime=ts.CTime",null);
        dbWrite.execSQL("delete from SaleByHourlyTemp");
        while(cur.moveToNext()){
            iTime=cur.getInt(0);
            sTime=cur.getString(0);
            if(sTime.contains("AM")) {
                if (iTime == 1) time = "01:00AM - " + "01:59AM";
                else if (iTime == 2) time = "02:00AM - " + "02:59AM";
                else if (iTime == 3) time = "03:00AM - " + "03:59AM";
                else if (iTime == 4) time = "04:00AM - " + "04:59AM";
                else if (iTime == 5) time = "05:00AM - " + "05:59AM";
                else if (iTime == 6) time = "06:00AM - " + "06:59AM";
                else if (iTime == 7) time = "07:00AM - " + "07:59AM";
                else if (iTime == 8) time = "08:00AM - " + "08:59AM";
                else if (iTime == 9) time = "09:00AM - " + "09:59AM";
                else if (iTime == 10) time = "10:00AM - " + "10:59AM";
                else if (iTime == 11) time = "11:00AM - " + "11:59AM";
                else if (iTime == 12) time = "12:00AM - " + "12:59AM";
            }else if(sTime.contains("PM")){
                if (iTime == 1) time = "01:00PM - " + "01:59PM";
                else if (iTime == 2) time = "02:00PM - " + "02:59PM";
                else if (iTime == 3) time = "03:00PM - " + "03:59PM";
                else if (iTime == 4) time = "04:00PM - " + "04:59PM";
                else if (iTime == 5) time = "05:00PM - " + "05:59PM";
                else if (iTime == 6) time = "06:00PM - " + "06:59PM";
                else if (iTime == 7) time = "07:00PM - " + "07:59PM";
                else if (iTime == 8) time = "08:00PM - " + "08:59PM";
                else if (iTime == 9) time = "09:00PM - " + "09:59PM";
                else if (iTime == 10) time = "10:00PM - " + "10:59PM";
                else if (iTime == 11) time = "11:00PM - " + "11:59PM";
                else if (iTime == 12) time = "12:00PM - " + "12:59PM";
            }

            ContentValues value=new ContentValues();
            value.put("CTime", time);
            value.put("TotalTransaction",cur.getInt(1));
            value.put("TotalItem",cur.getInt(2));
            value.put("TotalAmount",cur.getDouble(3));
            value.put("TotalCustomer",cur.getInt(4));
            dbWrite.insert("SaleByHourlyTemp", null, value);
        }
    }
    public Cursor getReportSaleByHourly12AM() {
        SQLiteDatabase dbRead=this.getReadableDatabase();
        Cursor result= dbRead.rawQuery("SELECT CTime,SUM(TotalTransaction),SUM(TotalItem),SUM(TotalAmount),SUM(TotalCustomer) FROM SaleByHourlyTemp WHERE CTime LIKE '%12:00AM%' GROUP BY CTime",null);
        return result;
    }
    public Cursor getReportSaleByHourlyAM() {
        SQLiteDatabase dbRead=this.getReadableDatabase();
        Cursor result= dbRead.rawQuery("SELECT CTime,SUM(TotalTransaction),SUM(TotalItem),SUM(TotalAmount),SUM(TotalCustomer) FROM SaleByHourlyTemp WHERE CTime LIKE '%AM%' GROUP BY CTime",null);
        return result;
    }
    public Cursor getReportSaleByHourly12PM() {
        SQLiteDatabase dbRead=this.getReadableDatabase();
        Cursor result= dbRead.rawQuery("SELECT CTime,SUM(TotalTransaction),SUM(TotalItem),SUM(TotalAmount),SUM(TotalCustomer) FROM SaleByHourlyTemp WHERE CTime LIKE '%12:00PM%' GROUP BY CTime",null);
        return result;
    }
    public Cursor getReportSaleByHourlyPM() {
        SQLiteDatabase dbRead=this.getReadableDatabase();
        Cursor result= dbRead.rawQuery("SELECT CTime,SUM(TotalTransaction),SUM(TotalItem),SUM(TotalAmount),SUM(TotalCustomer) FROM SaleByHourlyTemp WHERE CTime LIKE '%PM%' GROUP BY CTime",null);
        return result;
    }
    public Cursor getReportSaleByWaiter(String fromDate,String toDate,String waiterIDList){
        SQLiteDatabase dbRead=this.getReadableDatabase();
        SQLiteDatabase dbWrite=this.getWritableDatabase();
        dbWrite.execSQL("delete from SaleByWaiterTemp");
        Cursor cur_ms=dbRead.rawQuery("SELECT WaiterID,SUM(GrandTotal),COUNT(TranID) FROM MasterSale WHERE DATE(Date) BETWEEN DATE('"+fromDate+"') AND DATE('"+toDate+"') AND WaiterID IN("+waiterIDList+") GROUP BY WaiterID",null);
        while(cur_ms.moveToNext()){
            ContentValues value=new ContentValues();
            value.put("WaiterID", cur_ms.getInt(0));
            value.put("TotalAmount",cur_ms.getDouble(1));
            value.put("TotalSaleTable",cur_ms.getInt(2));
            dbWrite.insert("SaleByWaiterTemp", null, value);
        }
        Cursor cur=dbRead.rawQuery("SELECT ms.WaiterID,waiter.WaiterName,ms.Date,tbl.TableName,SUM(ms.GrandTotal),temp.TotalAmount,SUM(cus.Total),temp.TotalSaleTable FROM MasterSale ms INNER JOIN tblTable tbl ON ms.TableID=tbl.TableID INNER JOIN Waiter waiter ON ms.WaiterID=waiter.WaiterID INNER JOIN SaleByWaiterTemp temp ON ms.WaiterID=temp.WaiterID LEFT JOIN Customer cus ON ms.TranID=cus.TranID WHERE DATE(ms.Date) BETWEEN DATE('"+fromDate+"') AND DATE('"+toDate+"') AND ms.WaiterID IN("+waiterIDList+") GROUP BY ms.WaiterID,waiter.WaiterName,ms.Date,tbl.TableName,temp.TotalAmount,temp.TotalSaleTable ORDER BY waiter.WaiterID",null);
        return cur;
    }
    public Cursor getReportSaleByTable(String fromDate,String toDate,String tableIDList){
        SQLiteDatabase dbRead=this.getReadableDatabase();
        SQLiteDatabase dbWrite=this.getWritableDatabase();
        dbWrite.execSQL("delete from SaleByTableTypeTemp");
        Cursor cur_table_type=dbRead.rawQuery("SELECT TableTypeID,SUM(ms.GrandTotal) FROM MasterSale ms INNER JOIN tblTable tbl ON ms.TableID=tbl.TableID WHERE DATE(ms.Date) BETWEEN DATE('"+fromDate+"') AND DATE('"+toDate+"') AND ms.TableID IN("+tableIDList+") GROUP BY tbl.TableTypeID",null);
        while(cur_table_type.moveToNext()){
            ContentValues value=new ContentValues();
            value.put("TableTypeID", cur_table_type.getInt(0));
            value.put("TotalByTableType",cur_table_type.getDouble(1));
            dbWrite.insert("SaleByTableTypeTemp", null, value);
        }
        dbWrite.execSQL("delete from SaleByTableTemp");
        Cursor cur_table=dbRead.rawQuery("SELECT TableID,SUM(GrandTotal) FROM MasterSale WHERE DATE(Date) BETWEEN DATE('"+fromDate+"') AND DATE('"+toDate+"') AND TableID IN("+tableIDList+") GROUP BY TableID",null);
        while(cur_table.moveToNext()){
            ContentValues value=new ContentValues();
            value.put("TableID", cur_table.getInt(0));
            value.put("TotalByTable",cur_table.getDouble(1));
            dbWrite.insert("SaleByTableTemp", null, value);
        }
        Cursor cur=dbRead.rawQuery("SELECT ms.TableID,tbl.TableName,waiter.WaiterName,ms.Date,SUM(ms.GrandTotal),temp.TotalByTable,tbl.TableTypeID,tableType.TableTypeName,typeTemp.TotalByTableType,SUM(cus.Total) FROM MasterSale ms INNER JOIN tblTable tbl ON ms.TableID=tbl.TableID INNER JOIN TableType tableType ON tbl.TableTypeID=tableType.TableTypeID INNER JOIN Waiter waiter ON ms.WaiterID=waiter.WaiterID INNER JOIN SaleByTableTypeTemp typeTemp ON tbl.TableTypeID=typeTemp.TableTypeID INNER JOIN SaleByTableTemp temp ON ms.TableID=temp.TableID LEFT JOIN Customer cus ON ms.TranID=cus.TranID WHERE DATE(ms.Date) BETWEEN DATE('"+fromDate+"') AND DATE('"+toDate+"') AND ms.TableID IN("+tableIDList+") GROUP BY ms.TableID,tbl.TableName,waiter.WaiterName,ms.Date,temp.TotalByTable,tbl.TableTypeID,tableType.TableTypeName,typeTemp.TotalByTableType ORDER BY tbl.TableTypeID,ms.TableID",null);
        return cur;
    }

    /**
     * truncate methods
     */
    public void truncateSetupTables(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from Waiter");
        db.execSQL("delete from UserRight");
        db.execSQL("delete from TableType");
        db.execSQL("delete from tblTable");
        db.execSQL("delete from MainMenu");
        db.execSQL("delete from SubMenu");
        db.execSQL("delete from Item");
        db.execSQL("delete from ItemSubGroup");
        db.execSQL("delete from ItemSub");
        db.execSQL("delete from ItemAndSub");
        db.execSQL("delete from Taste");
        db.execSQL("delete from ItemSType");
    }
    public void truncateFeature(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from FeatureSetting");
    }
    public void truncateMasterSale(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from MasterSale");
    }
    public void truncateTranSale(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from TranSale");
    }
    public void truncateWaiter(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from Waiter");
    }
    public void truncateUserRight(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from UserRight");
    }
    public void truncateTableType(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from TableType");
    }
    public void truncateTable(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from tblTable");
    }
    public void truncateMainMenu(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from MainMenu");
    }
    public void truncateSubMenu(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from SubMenu");
    }
    public void truncateItem(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from Item");
    }
    public void truncateItemSubGroup(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from ItemSubGroup");
    }
    public void truncateItemSub(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from ItemSub");
    }
    public void truncateItemAndSub(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from ItemAndSub");
    }
    public void truncateItemSType(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from ItemSType");
    }
    public void truncateTaste(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from Taste");
    }
    public void truncateCompany(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from Company");
    }
    public void truncateSystemSetting(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from SystemSetting");
    }
    public void truncateVoucherSetting(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from VoucherSetting");
    }
    public boolean deleteTran(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from MasterSale");
        db.execSQL("delete from TranSale");
        db.execSQL("delete from MasterSaleTemp");
        db.execSQL("delete from TranSaleTemp");
        //db.execSQL("update SystemTranID set TranID=" + 1);
        db.execSQL("update SlipID set ID=" + 1);
        return true;
    }
    public boolean deleteTranByDate(String fromDate,String toDate){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("DELETE FROM TranSale WHERE TranID IN(SELECT TranID FROM MasterSale WHERE DATE(Date) BETWEEN DATE('"+fromDate+"') AND DATE('"+toDate+"'))");
        db.execSQL("delete from MasterSale WHERE DATE(Date) BETWEEN DATE('"+fromDate+"') AND DATE('"+toDate+"')");
        db.execSQL("DELETE FROM TranSaleTemp WHERE TranID IN(SELECT TranID FROM MasterSaleTemp WHERE DATE(Date) BETWEEN DATE('"+fromDate+"') AND DATE('"+toDate+"'))");
        db.execSQL("delete from MasterSaleTemp WHERE DATE(Date) BETWEEN DATE('"+fromDate+"') AND DATE('"+toDate+"')");
        //db.execSQL("update SystemTranID set TranID=" + 1);
        db.execSQL("update SlipID set ID=" + 1);
        return true;
    }
    public boolean resetSlipId(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("update SlipID set ID=" + 1);
        return true;
    }

    /**
     * Admin Setting
     */
    public boolean deleteAdminPassword(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from AdminSetting");
        return true;
    }
    public boolean insertAdminPassword(String password){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("AdminPassword", password);
        db.insert("AdminSetting", null, value);
        return true;
    }
    public Cursor getAdminPassword(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select AdminPassword from AdminSetting",null);
        return cur;
    }

    /**
     * User Right
     */
    public boolean deleteModule(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from Module");
        return true;
    }
    public boolean insertModule(String name){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("ModuleName", name);
        db.insert("Module", null, value);
        return true;
    }
    public Cursor getAllModule(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select ModuleID,ModuleName from Module",null);
        return cur;
    }
    public boolean insertUserRight(int userid,int moduleid){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("UserID", userid);
        value.put("ModuleID",moduleid);
        db.insert("UserRight", null, value);
        return true;
    }
    public boolean deleteUserRight(int userid){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from UserRight where UserID="+userid);
        return true;
    }
    public Cursor getModuleByUserID(int userid){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select ur.ModuleID,mo.ModuleName from UserRight ur inner join Module mo on ur.ModuleID=mo.ModuleID where UserID="+userid,null);
        return cur;
    }
    public Cursor getUserRight(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select UserID,ModuleID from UserRight",null);
        return cur;
    }

    /**
     * IP Setting
     */
    public boolean insertIPSetting(String ip,String user,String password,String database){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("IPAddress", ip);
        value.put("User", user);
        value.put("Password", password);
        value.put("Database", database);
        long i= db.insert("IPSetting", null, value);
        if(i == -1)return false;
        else return true;
    }
    public void deleteIPSetting(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from IPSetting");
    }
    public Cursor getIPSetting(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select IPAddress,User,Password,Database from IPSetting",null);
        return cur;
    }

    /**
     * SlipID
     */
    public void deleteSlipID(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from SlipID");
    }
    public boolean insertSlipID(){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("ID", 1);
        db.insert("SlipID", null, value);
        return true;
    }
    public Cursor getSlipID(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select ID from SlipID",null);
        return cur;
    }
    public boolean updateSlipID() {
        int slipid=0;
        Cursor cur=getSlipID();
        if(cur.moveToFirst()){
            slipid=cur.getInt(0);
        }
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("update SlipID set ID=" + (slipid+1));
        return true;
    }

    /**
     * Report
     */
    public void deleteReport(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from Report");
    }
    public boolean insertReport(String name){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("ReportName", name);
        db.insert("Report", null, value);
        return true;
    }
    public Cursor getReport(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select ID,ReportName from Report",null);
        return cur;
    }

    /**
     * Feature Setting
     */
    public void deleteFeature(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from FeatureSetting");
    }
    public boolean insertFeature(String featureName,int isAllow){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("FeatureName", featureName);
        value.put("isAllow", isAllow);
        db.insert("FeatureSetting", null, value);
        return true;
    }
    public Cursor getFeature(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select FeatureID,FeatureName,isAllow from FeatureSetting",null);
        return cur;
    }
    public boolean updateAllowFeature(int featureid,int isAllow) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("update FeatureSetting set isAllow=" + isAllow + " where FeatureID=" + featureid);
        return true;
    }
    public int getFeatureResult(String requestFeature){
        int result=0;
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select isAllow from FeatureSetting where FeatureName='"+requestFeature+"'",null);
        if(cur.moveToNext()){
            result=cur.getInt(0);
        }
        return result;
    }
    public int getUseMultiPrinterFeatureID(){
        int id=0;
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select FeatureID from FeatureSetting where FeatureName='"+FeatureList.fUseMultiPrinter+"'",null);
        if(cur.moveToNext()){
            id=cur.getInt(0);
        }
        return id;
    }

    /**
     * Waiter
     */
    public boolean insertWaiter(String waiterName,String password){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("WaiterName", waiterName);
        value.put("Password", password);
        db.insert("Waiter", null, value);
        return true;
    }
    public boolean insertWaiter(int id,String waiterName,String password){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("WaiterID",id);
        value.put("WaiterName", waiterName);
        value.put("Password", password);
        db.insert("Waiter", null, value);
        return true;
    }
    public boolean updateWaiter(int waiterID,String waiterName,String password){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("WaiterName", waiterName);
        value.put("Password", password);
        db.update("Waiter", value, "WaiterID=?", new String[]{Integer.toString(waiterID)});
        return true;
    }
    public boolean deleteWaiter(int waiterID){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from Waiter where WaiterID="+waiterID);
        return true;
    }
    public Cursor getWaiter(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select WaiterID,WaiterName,Password from Waiter",null);
        return cur;
    }
    public Cursor getMaxWaiterID(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select MAX(WaiterID) from Waiter",null);
        return cur;
    }
    public Cursor getWaiterByFilter(String waiterName){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select WaiterID,WaiterName,Password from Waiter where WaiterName Like '%"+waiterName+"%'",null);
        return cur;
    }

    /**
     * Table Type
     */
    public boolean insertTableType(String tableTypeName){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("TableTypeName", tableTypeName);
        db.insert("TableType", null, value);
        return true;
    }
    public boolean insertTableType(int id,String tableTypeName){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("TableTypeID",id);
        value.put("TableTypeName", tableTypeName);
        db.insert("TableType", null, value);
        return true;
    }
    public boolean updateTableType(int tableTypeID,String tableTypeName){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("TableTypeName", tableTypeName);
        db.update("TableType", value, "TableTypeID=?", new String[]{Integer.toString(tableTypeID)});
        return true;
    }
    public boolean deleteTableType(int tableTypeID){
        SQLiteDatabase dbRead=this.getReadableDatabase();
        Cursor cur=dbRead.rawQuery("select * from tblTable where TableTypeID="+tableTypeID,null);
        if(cur.moveToNext()){
            return false;
        }else {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("delete from TableType where TableTypeID=" + tableTypeID);
            return true;
        }
    }
    public Cursor getTableType(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select TableTypeID,TableTypeName from TableType",null);
        return cur;
    }
    public Cursor getTableTypeByFilter(String tableTypeName){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select TableTypeID,TableTypeName from TableType where TableTypeName Like '%"+tableTypeName+"%'",null);
        return cur;
    }

    /**
     * S Type
     */
    public boolean insertSType(String sTypeName){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("STypeName", sTypeName);
        db.insert("ItemSType", null, value);
        return true;
    }
    public boolean insertSType(int id,String sTypeName){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("STypeID",id);
        value.put("STypeName", sTypeName);
        db.insert("ItemSType", null, value);
        return true;
    }
    public boolean updateSType(int id,String sTypeName){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("STypeName", sTypeName);
        db.update("ItemSType", value, "STypeID=?", new String[]{Integer.toString(id)});
        return true;
    }
    public boolean deleteSType(int id){
        SQLiteDatabase dbRead=this.getReadableDatabase();
        Cursor cur=dbRead.rawQuery("select * from Item where STypeID="+id,null);
        if(cur.moveToNext()){
            return false;
        }else {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("delete from ItemSType where STypeID=" + id);
            return true;
        }
    }
    public Cursor getSType(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select STypeID,STypeName from ItemSType",null);
        return cur;
    }
    public Cursor getSTypeByFilter(String sTypeName){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select STypeID,STypeName from ItemSType where STypeName Like '%"+sTypeName+"%'",null);
        return cur;
    }

    /**
     * Company
     */
    public boolean insertCompany(String companyName){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("CompanyName", companyName);
        db.insert("Company", null, value);
        return true;
    }
    public boolean insertCompany(int id,String companyName){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("CompanyID",id);
        value.put("CompanyName", companyName);
        db.insert("Company", null, value);
        return true;
    }
    public boolean updateCompany(int id,String companyName){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("CompanyName", companyName);
        db.update("Company", value, "CompanyID=?", new String[]{Integer.toString(id)});
        return true;
    }
    public boolean deleteCompany(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from Company where CompanyID=" + id);
        return true;
    }
    public Cursor getCompany(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select CompanyID,CompanyName from Company",null);
        return cur;
    }
    public Cursor getCompanyByFilter(String companyName){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select CompanyID,CompanyName from Company where CompanyName Like '%"+companyName+"%'",null);
        return cur;
    }

    /**
     * Table
     */
    public boolean insertTable(String tableName,int tableTypeID){
        SQLiteDatabase dbRead=this.getReadableDatabase();
        Cursor cur=dbRead.rawQuery("select TableName from tblTable where TableName='"+tableName+"'",null);
        if(cur.moveToNext()){
            return false;
        }
        else {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues value = new ContentValues();
            value.put("TableName", tableName);
            value.put("TableTypeID", tableTypeID);
            db.insert("tblTable", null, value);
            return true;
        }
    }
    public boolean insertTable(int id,String tableName,int tableTypeID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put("TableID",id);
        value.put("TableName", tableName);
        value.put("TableTypeID", tableTypeID);
        db.insert("tblTable", null, value);
        return true;
    }
    public boolean updateTable(int tableID,String tableName,int tableTypeID){
        SQLiteDatabase dbRead=this.getReadableDatabase();
        Cursor cur=dbRead.rawQuery("select TableName from tblTable where TableName='"+tableName+"' and TableID!="+tableID,null);
        if(cur.moveToNext()){
            return false;
        }
        else {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues value = new ContentValues();
            value.put("TableName", tableName);
            value.put("TableTypeID", tableTypeID);
            db.update("tblTable", value, "TableID=?", new String[]{Integer.toString(tableID)});
            return true;
        }
    }
    public boolean deleteTable(int tableID){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from tblTable where TableID="+tableID);
        return true;
    }
    public Cursor getTable(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select TableID,TableName,tbl.TableTypeID,typ.TableTypeName from tblTable tbl inner join TableType typ on tbl.TableTypeID=typ.TableTypeID order by typ.TableTypeID",null);
        return cur;
    }
    public Cursor getTableByFilter(int tableTypeID,String tableName){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur;
        if(tableTypeID!=0) {
            cur = db.rawQuery("select TableID,TableName,tbl.TableTypeID,typ.TableTypeName from tblTable tbl inner join TableType typ on tbl.TableTypeID=typ.TableTypeID where tbl.TableTypeID=" + tableTypeID + " and TableName Like '%" + tableName + "%' order by typ.TableTypeID", null);
        }else{
            cur = db.rawQuery("select TableID,TableName,tbl.TableTypeID,typ.TableTypeName from tblTable tbl inner join TableType typ on tbl.TableTypeID=typ.TableTypeID where TableName Like '%" + tableName + "%' order by typ.TableTypeID", null);
        }
        return cur;
    }
    public Cursor getTableByTableType(int tableTypeID){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select TableID,TableName,tbl.TableTypeID,typ.TableTypeName from tblTable tbl inner join TableType typ on tbl.TableTypeID=typ.TableTypeID where typ.TableTypeID="+tableTypeID+" order by typ.TableTypeID",null);
        return cur;
    }
    public Cursor getOcpTableByTableType(int tableTypeID){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select ms.TableID,tbl.TableName,tbl.TableTypeID,typ.TableTypeName from MasterSaleTemp ms inner join tblTable tbl on ms.TableID=tbl.TableID inner join TableType typ on tbl.TableTypeID=typ.TableTypeID where typ.TableTypeID="+tableTypeID+" order by tbl.TableID",null);
        return cur;
    }

    /**
     * Main Menu
     */
    public boolean insertMainMenu(String mainMenuName,int counterid){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("MainMenuName", mainMenuName);
        value.put("isAllow", 1);
        value.put("CounterID", counterid);
        db.insert("MainMenu", null, value);
        return true;
    }
    public boolean insertMainMenu(String mainMenuName,int counterid,int isAllow){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("MainMenuName", mainMenuName);
        value.put("isAllow", isAllow);
        value.put("CounterID", counterid);
        db.insert("MainMenu", null, value);
        return true;
    }
    public boolean insertMainMenu(int id,String mainMenuName,int counterid,int isAllow){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("MainMenuID",id);
        value.put("MainMenuName", mainMenuName);
        value.put("isAllow", isAllow);
        value.put("CounterID", counterid);
        db.insert("MainMenu", null, value);
        return true;
    }
    public boolean updateMainMenu(int mainMenuID,String mainMenuName,int counterid){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("MainMenuName", mainMenuName);
        value.put("isAllow", 1);
        value.put("CounterID", counterid);
        db.update("MainMenu", value, "MainMenuID=?", new String[]{Integer.toString(mainMenuID)});
        return true;
    }
    public boolean deleteMainMenu(int mainMenuID){
        SQLiteDatabase dbRead=this.getReadableDatabase();
        Cursor cur=dbRead.rawQuery("select * from SubMenu where MainMenuID="+mainMenuID,null);
        if(cur.moveToNext()){
            return false;
        }else {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("delete from MainMenu where MainMenuID=" + mainMenuID);
            return true;
        }
    }
    public Cursor getMainMenu(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select MainMenuID,MainMenuName,CounterID,isAllow from MainMenu",null);
        return cur;
    }
    public Cursor getAllowMainMenu(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select MainMenuID,MainMenuName,CounterID from MainMenu where isAllow=1",null);
        return cur;
    }
    public Cursor getMainMenuForManageMenuDialog(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select MainMenuID,MainMenuName,CounterID,isAllow from MainMenu",null);
        return cur;
    }
    public Cursor getMainMenuByFilter(String mainMenuName){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select MainMenuID,MainMenuName,CounterID from MainMenu where isAllow=1 and MainMenuName Like '%"+mainMenuName+"%'",null);
        return cur;
    }
    public boolean resetMainMenu(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("update MainMenu set isAllow=0");
        return true;
    }
    public boolean updateAllowedMainMenu(int mainMenuID){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("update MainMenu set isAllow=1 where MainMenuID="+mainMenuID);
        return true;
    }

    /**
     * Sub Menu
     */
    public boolean insertSubMenu(String subMenuName,int mainMenuID,String sortCode){
        SQLiteDatabase dbRead=this.getReadableDatabase();
        Cursor cur=dbRead.rawQuery("select * from SubMenu where MainMenuID="+mainMenuID+" and SortCode='"+sortCode+"'",null);
        if(cur.moveToNext()){
            return false;
        }
        else {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues value = new ContentValues();
            value.put("SubMenuName", subMenuName);
            value.put("MainMenuID", mainMenuID);
            value.put("SortCode", sortCode);
            db.insert("SubMenu", null, value);
            return true;
        }
    }
    public boolean insertSubMenu(int id,String subMenuName,int mainMenuID,String sortCode) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put("SubMenuID",id);
        value.put("SubMenuName", subMenuName);
        value.put("MainMenuID", mainMenuID);
        value.put("SortCode", sortCode);
        db.insert("SubMenu", null, value);
        return true;
    }
    public boolean updateSubMenu(int subMenuID,String subMenuName,int mainMenuID,String sortCode){
        SQLiteDatabase dbRead=this.getReadableDatabase();
        Cursor cur=dbRead.rawQuery("select * from SubMenu where MainMenuID="+mainMenuID+" and SortCode='"+sortCode+"' and SubMenuID!="+subMenuID,null);
        if(cur.moveToNext()){
            return false;
        }
        else {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues value = new ContentValues();
            value.put("SubMenuName", subMenuName);
            value.put("MainMenuID", mainMenuID);
            value.put("SortCode", sortCode);
            db.update("SubMenu", value, "SubMenuID=?", new String[]{Integer.toString(subMenuID)});
            return true;
        }
    }
    public boolean deleteSubMenu(int subMenuID){
        SQLiteDatabase dbRead=this.getReadableDatabase();
        Cursor cur=dbRead.rawQuery("select * from Item where SubMenuID="+subMenuID,null);
        if(cur.moveToNext()){
            return false;
        }else {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("delete from SubMenu where SubMenuID=" + subMenuID);
            return true;
        }
    }
    public Cursor getSubMenu(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select SubMenuID,SubMenuName,sub.MainMenuID,sub.SortCode,main.MainMenuName from SubMenu sub inner join MainMenu main on sub.MainMenuID=main.MainMenuID order by main.MainMenuID,cast(sub.SortCode as Integer)",null);
        return cur;
    }
    public Cursor getSubMenuByFilter(int mainMenuID,String subMenuName){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur;
        if(mainMenuID!=0) {
            cur = db.rawQuery("select SubMenuID,SubMenuName,sub.MainMenuID,sub.SortCode,main.MainMenuName from SubMenu sub inner join MainMenu main on sub.MainMenuID=main.MainMenuID where sub.MainMenuID=" + mainMenuID + " and sub.SubMenuName Like '%" + subMenuName + "%' order by main.MainMenuID,cast(sub.SortCode as Integer)", null);
        }else{
            cur = db.rawQuery("select SubMenuID,SubMenuName,sub.MainMenuID,sub.SortCode,main.MainMenuName from SubMenu sub inner join MainMenu main on sub.MainMenuID=main.MainMenuID where sub.SubMenuName Like '%" + subMenuName + "%' order by main.MainMenuID,cast(sub.SortCode as Integer)", null);
        }
        return cur;
    }
    public Cursor getSubMenuByMainMenu(int mainMenuID){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select SubMenuID,SubMenuName,sub.MainMenuID,sub.SortCode,main.MainMenuName from SubMenu sub inner join MainMenu main on sub.MainMenuID=main.MainMenuID where sub.MainMenuID="+mainMenuID+" order by main.MainMenuID,cast(sub.SortCode as Integer)",null);
        return cur;
    }

    /**
     * Item
     */
    public boolean insertItem(String itemid,String itemName,int subMenuID,double price,int outOfOrder,int sTypeID,byte[] itemImage) {
        SQLiteDatabase dbRead=this.getReadableDatabase();
        Cursor cur=dbRead.rawQuery("select * from Item where ItemID='"+itemid+"'",null);
        if(cur.moveToNext()){
            return false;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put("ItemID", itemid);
        value.put("ItemName", itemName);
        value.put("SubMenuID", subMenuID);
        value.put("Price", price);
        value.put("OutofOrder", outOfOrder);
        value.put("STypeID",sTypeID);
        value.put("Image",itemImage);
        db.insert("Item", null, value);
        return true;
    }
    public boolean insertItem(int id,String itemid,String itemName,int subMenuID,double price,int outOfOrder,int sTypeID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put("ID", id);
        value.put("ItemID", itemid);
        value.put("ItemName", itemName);
        value.put("SubMenuID", subMenuID);
        value.put("Price", price);
        value.put("OutofOrder", outOfOrder);
        value.put("STypeID", sTypeID);
        db.insert("Item", null, value);
        return true;
    }
    public boolean updateItem(int id,String itemid,String itemName,int subMenuID,double price,int outOfOrder,int sTypeID,byte[] itemImage){
        SQLiteDatabase dbRead=this.getReadableDatabase();
        Cursor cur=dbRead.rawQuery("select * from Item where ItemID='"+itemid+"' and ID!="+id,null);
        if(cur.moveToNext()){
            return false;
        }
        else {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues value = new ContentValues();
            value.put("ItemID", itemid);
            value.put("ItemName", itemName);
            value.put("SubMenuID", subMenuID);
            value.put("Price", price);
            value.put("OutofOrder", outOfOrder);
            value.put("STypeID",sTypeID);
            value.put("Image",itemImage);
            db.update("Item", value, "ID=?", new String[]{Integer.toString(id)});
            return true;
        }
    }
    public boolean deleteItem(int id,String itemid){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from Item where ID="+id);
        db.execSQL("delete from ItemAndSub where ItemID='"+itemid+"'");
        return true;
    }
    public Cursor getItem(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select ID,ItemID,ItemName,item.SubMenuID,Price,OutofOrder,sub.SubMenuName,main.MainMenuID,main.MainMenuName,item.STypeID,Image from Item item inner join SubMenu sub on item.SubMenuID=sub.SubMenuID inner join MainMenu main on sub.MainMenuID=main.MainMenuID order by sub.MainMenuID,cast(sub.SortCode as Integer),item.ItemID",null);
        return cur;
    }
    public List<Integer> getAllIdInItem(){
        List<Integer> lstItemID=new ArrayList<>();
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select ID from Item",null);
        while(cur.moveToNext()){
            lstItemID.add(cur.getInt(0));
        }
        return lstItemID;
    }
    public void updateItemImageById(int id,byte[] itemImage) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put("Image", itemImage);
        db.update("Item", value, "ID=?", new String[]{Integer.toString(id)});
    }
    public Cursor getItemByFilter(int mainMenuID,int subMenuID,String itemid,String itemName){
        String sql="";
        if(mainMenuID==0 && subMenuID==0 && itemid.equals("") && itemName.equals("")){ // not 4 types
            sql="select ID,ItemID,ItemName,item.SubMenuID,Price,OutofOrder,sub.SubMenuName,main.MainMenuID,main.MainMenuName,item.STypeID,item.Image from Item item inner join SubMenu sub on item.SubMenuID=sub.SubMenuID inner join MainMenu main on sub.MainMenuID=main.MainMenuID order by sub.MainMenuID,cast(sub.SortCode as Integer),item.ItemID";
        }else if(mainMenuID!=0 && subMenuID!=0 && !itemid.equals("") && !itemName.equals("")){ // include 4 types
            sql="select ID,ItemID,ItemName,item.SubMenuID,Price,OutofOrder,sub.SubMenuName,main.MainMenuID,main.MainMenuName,item.STypeID,Image from Item item inner join SubMenu sub on item.SubMenuID=sub.SubMenuID inner join MainMenu main on sub.MainMenuID=main.MainMenuID WHERE sub.MainMenuID="+mainMenuID+" AND item.SubMenuID="+subMenuID+" AND item.ItemID Like '%"+itemid+"%' AND item.ItemName Like '%"+itemName+"%' order by sub.MainMenuID,cast(sub.SortCode as Integer),item.ItemID";
        }else if(mainMenuID!=0 && subMenuID!=0 && !itemid.equals("") && itemName.equals("")){ //include main,sub,itemid and not name
            sql="select ID,ItemID,ItemName,item.SubMenuID,Price,OutofOrder,sub.SubMenuName,main.MainMenuID,main.MainMenuName,item.STypeID,Image from Item item inner join SubMenu sub on item.SubMenuID=sub.SubMenuID inner join MainMenu main on sub.MainMenuID=main.MainMenuID WHERE sub.MainMenuID="+mainMenuID+" AND item.SubMenuID="+subMenuID+" AND item.ItemID Like '%"+itemid+"%' order by sub.MainMenuID,cast(sub.SortCode as Integer),item.ItemID";
        }else if(mainMenuID!=0 && subMenuID!=0 && itemid.equals("") && !itemName.equals("")){ //include main,sub,name and not itemid
            sql="select ID,ItemID,ItemName,item.SubMenuID,Price,OutofOrder,sub.SubMenuName,main.MainMenuID,main.MainMenuName,item.STypeID,Image from Item item inner join SubMenu sub on item.SubMenuID=sub.SubMenuID inner join MainMenu main on sub.MainMenuID=main.MainMenuID WHERE sub.MainMenuID="+mainMenuID+" AND item.SubMenuID="+subMenuID+" AND item.ItemName Like '%"+itemName+"%' order by sub.MainMenuID,cast(sub.SortCode as Integer),item.ItemID";
        }else if(mainMenuID!=0 && subMenuID==0 && !itemid.equals("") && !itemName.equals("")){ //include main,itemid,name and not sub
            sql="select ID,ItemID,ItemName,item.SubMenuID,Price,OutofOrder,sub.SubMenuName,main.MainMenuID,main.MainMenuName,item.STypeID,Image from Item item inner join SubMenu sub on item.SubMenuID=sub.SubMenuID inner join MainMenu main on sub.MainMenuID=main.MainMenuID WHERE sub.MainMenuID="+mainMenuID+" AND item.ItemID Like '%"+itemid+"%' AND item.ItemName Like '%"+itemName+"%' order by sub.MainMenuID,cast(sub.SortCode as Integer),item.ItemID";
        }else if(mainMenuID==0 && subMenuID!=0 && !itemid.equals("") && !itemName.equals("")){ //include sub,itemid,name and not main
            sql="select ID,ItemID,ItemName,item.SubMenuID,Price,OutofOrder,sub.SubMenuName,main.MainMenuID,main.MainMenuName,item.STypeID,Image from Item item inner join SubMenu sub on item.SubMenuID=sub.SubMenuID inner join MainMenu main on sub.MainMenuID=main.MainMenuID WHERE item.SubMenuID="+subMenuID+" AND item.ItemID Like '%"+itemid+"%' AND item.ItemName Like '%"+itemName+"%' order by sub.MainMenuID,cast(sub.SortCode as Integer),item.ItemID";
        }else if(mainMenuID!=0 && subMenuID!=0 && itemid.equals("") && itemName.equals("")){ //include main,sub and not itemid,name
            sql="select ID,ItemID,ItemName,item.SubMenuID,Price,OutofOrder,sub.SubMenuName,main.MainMenuID,main.MainMenuName,item.STypeID,Image from Item item inner join SubMenu sub on item.SubMenuID=sub.SubMenuID inner join MainMenu main on sub.MainMenuID=main.MainMenuID WHERE sub.MainMenuID="+mainMenuID+" AND item.SubMenuID="+subMenuID+" order by sub.MainMenuID,cast(sub.SortCode as Integer),item.ItemID";
        }else if(mainMenuID!=0 && subMenuID==0 && !itemid.equals("") && itemName.equals("")){ //include main,itemid and not sub,name
            sql="select ID,ItemID,ItemName,item.SubMenuID,Price,OutofOrder,sub.SubMenuName,main.MainMenuID,main.MainMenuName,item.STypeID,Image from Item item inner join SubMenu sub on item.SubMenuID=sub.SubMenuID inner join MainMenu main on sub.MainMenuID=main.MainMenuID WHERE sub.MainMenuID="+mainMenuID+" AND item.ItemID Like '%"+itemid+"%' order by sub.MainMenuID,cast(sub.SortCode as Integer),item.ItemID";
        }else if(mainMenuID!=0 && subMenuID==0 && itemid.equals("") && !itemName.equals("")){ //include main,name and not sub,itemid
            sql="select ID,ItemID,ItemName,item.SubMenuID,Price,OutofOrder,sub.SubMenuName,main.MainMenuID,main.MainMenuName,item.STypeID,Image from Item item inner join SubMenu sub on item.SubMenuID=sub.SubMenuID inner join MainMenu main on sub.MainMenuID=main.MainMenuID WHERE sub.MainMenuID="+mainMenuID+" AND AND item.ItemName Like '%"+itemName+"%' order by sub.MainMenuID,cast(sub.SortCode as Integer),item.ItemID";
        }else if(mainMenuID==0 && subMenuID!=0 && !itemid.equals("") && itemName.equals("")){ //include sub,itemid and not main,name
            sql="select ID,ItemID,ItemName,item.SubMenuID,Price,OutofOrder,sub.SubMenuName,main.MainMenuID,main.MainMenuName,item.STypeID,Image from Item item inner join SubMenu sub on item.SubMenuID=sub.SubMenuID inner join MainMenu main on sub.MainMenuID=main.MainMenuID WHERE item.SubMenuID="+subMenuID+" AND item.ItemID Like '%"+itemid+"%' order by sub.MainMenuID,cast(sub.SortCode as Integer),item.ItemID";
        }else if(mainMenuID==0 && subMenuID!=0 && itemid.equals("") && !itemName.equals("")){ //include sub,name and not main,itemid
            sql="select ID,ItemID,ItemName,item.SubMenuID,Price,OutofOrder,sub.SubMenuName,main.MainMenuID,main.MainMenuName,item.STypeID,Image from Item item inner join SubMenu sub on item.SubMenuID=sub.SubMenuID inner join MainMenu main on sub.MainMenuID=main.MainMenuID WHERE item.SubMenuID="+subMenuID+" AND item.ItemName Like '%"+itemName+"%' order by sub.MainMenuID,cast(sub.SortCode as Integer),item.ItemID";
        }else if(mainMenuID==0 && subMenuID==0 && !itemid.equals("") && !itemName.equals("")){ //include itemid,name and not main,sub
            sql="select ID,ItemID,ItemName,item.SubMenuID,Price,OutofOrder,sub.SubMenuName,main.MainMenuID,main.MainMenuName,item.STypeID,Image from Item item inner join SubMenu sub on item.SubMenuID=sub.SubMenuID inner join MainMenu main on sub.MainMenuID=main.MainMenuID WHERE item.ItemID Like '%"+itemid+"%' AND item.ItemName Like '%"+itemName+"%' order by sub.MainMenuID,cast(sub.SortCode as Integer),item.ItemID";
        }else if(mainMenuID!=0 && subMenuID==0 && itemid.equals("") && itemName.equals("")){ //include main and not sub,itemid,name
            sql="select ID,ItemID,ItemName,item.SubMenuID,Price,OutofOrder,sub.SubMenuName,main.MainMenuID,main.MainMenuName,item.STypeID,Image from Item item inner join SubMenu sub on item.SubMenuID=sub.SubMenuID inner join MainMenu main on sub.MainMenuID=main.MainMenuID WHERE sub.MainMenuID="+mainMenuID+" order by sub.MainMenuID,cast(sub.SortCode as Integer),item.ItemID";
        }else if(mainMenuID==0 && subMenuID!=0 && itemid.equals("") && itemName.equals("")){ //include sub and not main,itemid,name
            sql="select ID,ItemID,ItemName,item.SubMenuID,Price,OutofOrder,sub.SubMenuName,main.MainMenuID,main.MainMenuName,item.STypeID,Image from Item item inner join SubMenu sub on item.SubMenuID=sub.SubMenuID inner join MainMenu main on sub.MainMenuID=main.MainMenuID WHERE item.SubMenuID="+subMenuID+" order by sub.MainMenuID,cast(sub.SortCode as Integer),item.ItemID";
        }else if(mainMenuID==0 && subMenuID==0 && !itemid.equals("") && itemName.equals("")){ //include itemid and not main,sub,name
            sql="select ID,ItemID,ItemName,item.SubMenuID,Price,OutofOrder,sub.SubMenuName,main.MainMenuID,main.MainMenuName,item.STypeID,Image from Item item inner join SubMenu sub on item.SubMenuID=sub.SubMenuID inner join MainMenu main on sub.MainMenuID=main.MainMenuID WHERE item.ItemID Like '%"+itemid+"%' order by sub.MainMenuID,cast(sub.SortCode as Integer),item.ItemID";
        }else if(mainMenuID==0 && subMenuID==0 && itemid.equals("") && !itemName.equals("")){ //include name and not main,sub,itemid
            sql="select ID,ItemID,ItemName,item.SubMenuID,Price,OutofOrder,sub.SubMenuName,main.MainMenuID,main.MainMenuName,item.STypeID,Image from Item item inner join SubMenu sub on item.SubMenuID=sub.SubMenuID inner join MainMenu main on sub.MainMenuID=main.MainMenuID WHERE item.ItemName Like '%"+itemName+"%' order by sub.MainMenuID,cast(sub.SortCode as Integer),item.ItemID";
        }
        //sql="select ID,ItemID,ItemName,item.SubMenuID,Price,OutofOrder,sub.SubMenuName,main.MainMenuID,main.MainMenuName,item.STypeID from Item item inner join SubMenu sub on item.SubMenuID=sub.SubMenuID inner join MainMenu main on sub.MainMenuID=main.MainMenuID WHERE sub.MainMenuID="+mainMenuID+" AND item.SubMenuID="+subMenuID+" AND item.ItemID Like '%"+itemid+"%' AND item.ItemName Like '%"+itemName+"%' order by sub.MainMenuID,cast(sub.SortCode as Integer),item.ItemID";
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery(sql,null);
        return cur;
    }
    public Cursor getItemBySubMenu(int subMenuID){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select ID,ItemID,ItemName,item.SubMenuID,Price,OutofOrder,sub.SubMenuName,item.STypeID,Image from Item item inner join SubMenu sub on item.SubMenuID=sub.SubMenuID where item.SubMenuID="+subMenuID,null);
        return cur;
    }

    /**
     * Item Sub Group
     */
    public boolean insertItemSubGroup(String groupName,String subTitle,int isSingleCheck){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("GroupName", groupName);
        value.put("SubTitle", subTitle);
        value.put("IsSingleCheck", isSingleCheck);
        db.insert("ItemSubGroup", null, value);
        return true;
    }
    public boolean insertItemSubGroup(int pkId,String groupName,String subTitle,int isSingleCheck){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("PKID",pkId);
        value.put("GroupName", groupName);
        value.put("SubTitle", subTitle);
        value.put("IsSingleCheck", isSingleCheck);
        db.insert("ItemSubGroup", null, value);
        return true;
    }
    public boolean updateItemSubGroup(int pkId,String groupName,String subTitle,int isSingleCheck){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("GroupName", groupName);
        value.put("SubTitle", subTitle);
        value.put("IsSingleCheck", isSingleCheck);
        db.update("ItemSubGroup", value, "PKID=?", new String[]{Integer.toString(pkId)});
        return true;
    }
    public boolean deleteItemSubGroup(int pkId){
        SQLiteDatabase dbRead=this.getReadableDatabase();
        Cursor cur=dbRead.rawQuery("select PKID from ItemSub where SubGroupID="+pkId,null);
        if(cur.moveToFirst()){
            return false;
        }else {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("delete from ItemSubGroup where PKID=" + pkId);
            return true;
        }
    }
    public Cursor getItemSubGroup(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select PKID,GroupName,SubTitle,IsSingleCheck from ItemSubGroup",null);
        return cur;
    }
    public Cursor getItemSubGroupByFilter(String groupName){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select PKID,GroupName,SubTitle,IsSingleCheck from ItemSubGroup where GroupName Like '%"+groupName+"%'",null);
        return cur;
    }

    /**
     * Item And Sub
     */
    public boolean insertItemAndSub(String itemId,int subGroupId,int levelNo){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("ItemID", itemId);
        value.put("SubGroupID", subGroupId);
        value.put("LevelNo", levelNo);
        db.insert("ItemAndSub", null, value);
        return true;
    }
    public boolean insertItemAndSub(int pkId,String itemId,int subGroupId,int levelNo){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("PKID",pkId);
        value.put("ItemID", itemId);
        value.put("SubGroupID", subGroupId);
        value.put("LevelNo", levelNo);
        db.insert("ItemAndSub", null, value);
        return true;
    }
    public List<ItemAndSubData> getItemAndSub(){
        SQLiteDatabase db=this.getReadableDatabase();
        List<ItemAndSubData> lstItemAndSubData=new ArrayList<>();
        Cursor cur=db.rawQuery("select PKID,ItemID,SubGroupID,LevelNo from ItemAndSub",null);
        while(cur.moveToNext()){
            ItemAndSubData data=new ItemAndSubData();
            data.setPkId(cur.getInt(0));
            data.setItemId(cur.getString(1));
            data.setSubGroupId(cur.getInt(2));
            data.setLevelNo(cur.getInt(3));
            lstItemAndSubData.add(data);
        }
        return lstItemAndSubData;
    }
    public boolean deleteItemAndSub(String itemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from ItemAndSub where ItemID='" + itemId + "'");
        return true;
    }
    public List<ItemSubGroupData> getItemSubGroupByItemID(String itemId){
        SystemSetting systemSetting=new SystemSetting();
        List<ItemSubGroupData> lstItemSubGroupData=new ArrayList<>();
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select SubGroupID from ItemAndSub where ItemID= '"+itemId+"'",null);
        while(cur.moveToNext()){
            Cursor nextCur=db.rawQuery("select PKID,GroupName,SubTitle,IsSingleCheck from ItemSubGroup where PKID= "+cur.getInt(0),null);
            while(nextCur.moveToNext()){
                ItemSubGroupData data=new ItemSubGroupData();
                data.setPkId(nextCur.getInt(0));
                data.setSubGroupName(nextCur.getString(1));
                data.setSubTitle(nextCur.getString(2));
                if(nextCur.getInt(3)==1){
                    data.setSingleCheck(1);
                    data.setCheckType(systemSetting.SINGLE_CHECK);
                }else{
                    data.setSingleCheck(0);
                    data.setCheckType(systemSetting.MULTI_CHECK);
                }
                lstItemSubGroupData.add(data);
            }
        }
        return lstItemSubGroupData;
    }

    public List<ItemSubGroupData> getItemSubByItemID(String itemId){
        List<ItemSubGroupData> lstItemSubGroupData=new ArrayList<>();
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select SubGroupID,SubTitle,IsSingleCheck from ItemAndSub iAnds inner join ItemSubGroup gp on iAnds.SubGroupID=gp.PKID where ItemID= '"+itemId+"' order by LevelNo",null);
        while(cur.moveToNext()){
            ItemSubGroupData groupData=new ItemSubGroupData();
            groupData.setPkId(cur.getInt(0));
            groupData.setSubTitle(cur.getString(1));
            groupData.setSingleCheck(cur.getInt(2));

            List<ItemSubData> lstItemSubData=new ArrayList<>();
            Cursor nextCur=db.rawQuery("select PKID,SubName,Price from ItemSub where SubGroupID= "+cur.getInt(0),null);
            while(nextCur.moveToNext()){
                ItemSubData data=new ItemSubData();
                data.setPkId(nextCur.getInt(0));
                data.setSubName(nextCur.getString(1));
                data.setPrice(nextCur.getInt(2));
                lstItemSubData.add(data);
            }
            groupData.setLstItemSubData(lstItemSubData);
            lstItemSubGroupData.add(groupData);
        }
        return lstItemSubGroupData;
    }

    /**
     * Item Sub
     */
    public boolean insertItemSub(int subGroupId,String subName,int price){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("SubGroupID", subGroupId);
        value.put("SubName", subName);
        value.put("Price", price);
        db.insert("ItemSub", null, value);
        return true;
    }
    public boolean insertItemSub(int pkId,int subGroupId,String subName,int price){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("PKID",pkId);
        value.put("SubGroupID", subGroupId);
        value.put("SubName", subName);
        value.put("Price", price);
        db.insert("ItemSub", null, value);
        return true;
    }
    public boolean updateItemSub(int pkId,int subGroupId,String subName,int price){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("SubGroupID", subGroupId);
        value.put("SubName", subName);
        value.put("Price", price);
        db.update("ItemSub", value, "PKID=?", new String[]{Integer.toString(pkId)});
        return true;
    }
    public boolean deleteItemSub(int pkId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from ItemSub where PKID=" + pkId);
        return true;
    }
    public Cursor getItemSub(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select item.PKID,SubGroupID,SubName,Price,GroupName from ItemSub item inner join ItemSubGroup gp on item.SubGroupID=gp.PKID order by SubGroupID",null);
        return cur;
    }
    public List<ItemSubData> getItemSubByGroup(int subGroupId){
        List<ItemSubData> lstItemSubData=new ArrayList<>();
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select PKID,SubName,Price from ItemSub where SubGroupID= "+subGroupId,null);
        while(cur.moveToNext()){
            ItemSubData data=new ItemSubData();
            data.setPkId(cur.getInt(0));
            data.setSubName(cur.getString(1));
            data.setPrice(cur.getInt(2));
            lstItemSubData.add(data);
        }
        return lstItemSubData;
    }
    public Cursor getItemSubByFilter(String subName){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select item.PKID,SubGroupID,SubName,Price,GroupName from ItemSub item inner join ItemSubGroup gp on item.SubGroupID=gp.PKID where SubName Like '%" + subName + "%' order by SubGroupID",null);
        return cur;
    }

    /**
     * Taste
     */
    public boolean insertTaste(String tasteName){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("TasteName", tasteName);
        db.insert("Taste", null, value);
        return true;
    }
    public boolean insertTaste(int id,String tasteName){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("TasteID",id);
        value.put("TasteName", tasteName);
        db.insert("Taste", null, value);
        return true;
    }
    public boolean updateTaste(int tasteid,String tasteName){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("TasteName", tasteName);
        db.update("Taste", value, "TasteID=?", new String[]{Integer.toString(tasteid)});
        return true;
    }
    public boolean deleteTaste(int tasteID){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from Taste where TasteID="+tasteID);
        return true;
    }
    public Cursor getTaste(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select TasteID,TasteName from Taste",null);
        return cur;
    }
    public Cursor getTasteByFilter(String tasteName){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select TasteID,TasteName from Taste where TasteName Like '%"+tasteName+"%'",null);
        return cur;
    }

    /**
     * System Setting
     */
    public void deleteSystemSetting(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from SystemSetting");
    }
    public boolean insertSystemSetting(String shopName,int companyid,int tax,int service){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from SystemSetting");
        ContentValues value=new ContentValues();
        value.put("ShopName", shopName);
        value.put("CompanyID",companyid);
        value.put("Tax", tax);
        value.put("Service", service);
        db.insert("SystemSetting", null, value);
        return true;
    }
    public Cursor getSystemSetting(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select ShopName,Tax,Service,CompanyID from SystemSetting",null);
        return cur;
    }
    public Cursor getShopName(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select ShopName from SystemSetting", null);
        return cur;
    }

    /**
     * Register
     */
    public void deleteRegister(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from Register");
    }
    public boolean insertRegister(String macAddress,String key){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from Register");
        ContentValues value=new ContentValues();
        value.put("MacAddress", macAddress);
        value.put("Key", key);
        db.insert("Register", null, value);
        return true;
    }

    /**
     * Voucher Setting
     */
    public boolean insertVoucherSetting(String title,String desp,String phone,String message,String address,String message2){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from VoucherSetting");
        ContentValues value=new ContentValues();
        value.put("Title", title);
        value.put("Description", desp);
        value.put("Phone", phone);
        value.put("Message", message);
        value.put("Address", address);
        value.put("Message2",message2);
        db.insert("VoucherSetting", null, value);
        return true;
    }
    public Cursor getVoucherSetting(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select Title,Description,Phone,Message,Address,Message2 from VoucherSetting",null);
        return cur;
    }

    /**
     * Booking
     */
    public Cursor getBookingByTableID(int tableid){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select * from Booking where TableID="+tableid,null);
        return cur;
    }
    public void deleteBooking(int tableid){
        SQLiteDatabase dbWrite = this.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put("Deleted", 1);
        dbWrite.update("Booking", value, "TableID=?", new String[]{Integer.toString(tableid)});
    }
    public Cursor getBookingByBookingID(int bookingid){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select ID,booking.TableID,tb.TableName,GuestName,Phone,Date,Time,People,Purpose,booking.Remark from Booking booking inner join tblTable tb on booking.TableID=tb.TableID where ID="+bookingid,null);
        return cur;
    }
    public Cursor getBookingByWaiterID(int waiterid){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select ID,booking.TableID,tb.TableName,GuestName,Phone,Date,Time,People,Purpose,booking.Remark from Booking booking inner join tblTable tb on booking.TableID=tb.TableID where booking.Deleted=0 and booking.WaiterID="+waiterid,null);
        return cur;
    }
    public boolean insertBooking(int waiterid,int tableid,String guestName,String phone,String date,String time,int people,String purpose,String remark){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put("WaiterID", waiterid);
        value.put("TableID", tableid);
        value.put("GuestName", guestName);
        value.put("Phone", phone);
        value.put("Date",date);
        value.put("Time", time);
        value.put("People", people);
        value.put("Purpose", purpose);
        value.put("Remark",remark);
        value.put("Deleted",0);
        db.insert("Booking", null, value);
        return true;
    }
    public boolean updateBooking(int bookingid,int waiterid,int tableid,String guestName,String phone,String date,String time,int people,String purpose,String remark){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put("WaiterID", waiterid);
        value.put("TableID", tableid);
        value.put("GuestName", guestName);
        value.put("Phone", phone);
        value.put("Date",date);
        value.put("Time", time);
        value.put("People", people);
        value.put("Purpose", purpose);
        value.put("Remark",remark);
        value.put("Deleted",0);
        db.update("Booking", value, "ID=?", new String[]{Integer.toString(bookingid)});
        return true;
    }
    public boolean deleteBookingByBookingID(int bookingid){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("update Booking set Deleted=1 where ID="+bookingid);
        return true;
    }

    /**
     * Customer Entry
     */
    public Cursor getCustomerInfoIDByTranID(int tranid){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select ID from Customer where TranID="+tranid,null);
        return cur;
    }
    public boolean insertCustomerInfo(int tranid,int tableid,int waiterid,String date,String time,int male,int female,int child,int total){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put("TranID", tranid);
        value.put("TableID", tableid);
        value.put("WaiterID", waiterid);
        value.put("Date", date);
        value.put("Time",time);
        value.put("Male", male);
        value.put("Female", female);
        value.put("Child", child);
        value.put("Total",total);
        db.insert("Customer", null, value);
        return true;
    }
    public boolean updateCustomerInfoByTranID(int tranid,int tableid,int waiterid,String date,String time,int male,int female,int child,int total){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put("TableID", tableid);
        value.put("WaiterID", waiterid);
        value.put("Date", date);
        value.put("Time",time);
        value.put("Male", male);
        value.put("Female", female);
        value.put("Child", child);
        value.put("Total",total);
        db.update("Customer", value, "TranID=?", new String[]{Integer.toString(tranid)});
        return true;
    }
    public Cursor getCustomerInfoByTranID(int tranid){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select ID,Date,Time,Male,Female,Child,Total from Customer where TranID="+tranid,null);
        return cur;
    }

    /**
     * System TranID
     */
    public boolean deleteTranID(){
        SQLiteDatabase dbWrite = this.getWritableDatabase();
        dbWrite.execSQL("delete from SystemTranID");
        return true;
    }
    public boolean setTranID(){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put("TranID", 1);
        db.insert("SystemTranID", null, value);
        return true;
    }

    /**
     * VouFormat
     */
    public boolean deleteVouFormat(){
        SQLiteDatabase dbWrite = this.getWritableDatabase();
        dbWrite.execSQL("delete from VouFormat");
        return true;
    }
    public boolean setVouFormat(){
        /**String shopName="";
        Cursor cur=getShopName();
        if(cur.moveToFirst()){
            shopName=cur.getString(0);
            shopName=shopName.substring(0,2);
        }**/
        Calendar cCalendar;
        cCalendar=Calendar.getInstance();
        SimpleDateFormat dateFormat=new SimpleDateFormat(SystemSetting.DATE_FORMAT);
        String date=dateFormat.format(cCalendar.getTime());
        String year=date.substring(2,4);
        String month=date.substring(5,7);

        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put("ShopName2", "vo");
        value.put("Year2", year);
        value.put("Month2", month);
        value.put("CustomNo", 1000);
        db.insert("VouFormat", null, value);
        return true;
    }

    /**
     * Printer Setting
     */
    public boolean insertPrinterSetting(String printerIP,int portNumber){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from PrinterSetting");
        ContentValues value=new ContentValues();
        value.put("PrinterIP", printerIP);
        value.put("PortNumber", portNumber);
        db.insert("PrinterSetting", null, value);
        return true;
    }
    public Cursor getPrinterSetting(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select PrinterIP,PortNumber from PrinterSetting",null);
        return cur;
    }

    /**
     * Item SType Printer Setting
     */
    public boolean insertItemSTypePrinterSetting(int stypeid,int printerid){
        SQLiteDatabase db=this.getWritableDatabase();
        SQLiteDatabase dbRead=this.getReadableDatabase();

        //db.execSQL("DELETE FROM ItemSTypePrinterSetting");

        Cursor cur=dbRead.rawQuery("select * from ItemSTypePrinterSetting where STypeID="+stypeid,null);
        if(cur.getCount()!=0)return false;
        Cursor cur2=dbRead.rawQuery("select * from ItemSTypePrinterSetting where PrinterID="+printerid,null);
        if(cur2.getCount()!=0)return false;

        ContentValues value=new ContentValues();
        value.put("STypeID", stypeid);
        value.put("PrinterID", printerid);
        db.insert("ItemSTypePrinterSetting", null, value);
        return true;
    }
    public Cursor getItemSTypePrinterSetting(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select sp.ID,sp.STypeID,sp.PrinterID,st.STypeName,p.PrinterName from ItemSTypePrinterSetting sp inner join ItemSType st on sp.STypeID=st.STypeID inner join Printer p on sp.PrinterID=p.PrinterID",null);
        return cur;
    }
    public boolean updateItemSTypePrinterSetting(int id,int stypeid,int printerid){
        SQLiteDatabase dbRead=this.getReadableDatabase();
        Cursor cur=dbRead.rawQuery("select * from ItemSTypePrinterSetting where ID!="+id+" and STypeID="+stypeid+" or PrinterID="+printerid,null);
        if(cur.getCount()!=0)return false;
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put("STypeID", stypeid);
        value.put("PrinterID", printerid);
        db.update("ItemSTypePrinterSetting", value, "ID=?", new String[]{Integer.toString(id)});
        return true;
    }
    public boolean deleteItemSTypePrinterSetting(int id){
        SQLiteDatabase dbWrite = this.getWritableDatabase();
        dbWrite.execSQL("delete from ItemSTypePrinterSetting where ID="+id);
        return true;
    }

    /**
     *   start transaction methods
     */
    private Cursor getVouFormat(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select ShopName2,Year2,Month2,CustomNo from VouFormat",null);
        return cur;
    }
    private int getSysTranID(){
        int tranid=0;
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select TranID from SystemTranID",null);
        if(cur.moveToFirst())tranid=cur.getInt(0);
        return tranid;
    }
    private void increaseSysTranID(){
        SQLiteDatabase dbRead=this.getReadableDatabase();
        SQLiteDatabase dbWrite=this.getWritableDatabase();
        Cursor cur=dbRead.rawQuery("select TranID from SystemTranID",null);
        if(cur.moveToFirst()){
            int tranid=cur.getInt(0);
            tranid=tranid+1;
            dbWrite.execSQL("update SystemTranID set TranID="+tranid);
        }
    }
    public void updateSysTranIDByImport(){
        SQLiteDatabase dbRead=this.getReadableDatabase();
        SQLiteDatabase dbWrite=this.getWritableDatabase();
        Cursor cur=dbRead.rawQuery("select Max(TranID) from MasterSale",null);
        if(cur.moveToFirst()){
            int tranid=cur.getInt(0);
            tranid+=1;
            dbWrite.execSQL("update SystemTranID set TranID="+tranid);
        }
    }
    private void increaseVouFormat(){
        SQLiteDatabase dbRead=this.getReadableDatabase();
        SQLiteDatabase dbWrite=this.getWritableDatabase();
        Cursor cur=dbRead.rawQuery("select CustomNo from VouFormat",null);
        if(cur.moveToFirst()){
            int customNo=cur.getInt(0);
            customNo=customNo+1;
            dbWrite.execSQL("update VouFormat set CustomNo="+customNo);
        }
    }
    public void updateVouFormatByImport(){
        SQLiteDatabase dbRead=this.getReadableDatabase();
        SQLiteDatabase dbWrite=this.getWritableDatabase();
        Cursor cur=dbRead.rawQuery("select Max(TranID) from MasterSale",null);
        if(cur.moveToFirst()){
            int tranid=cur.getInt(0);
            Cursor curMaxVou=dbRead.rawQuery("select VouNo from MasterSale where TranID="+tranid,null);
            if(curMaxVou.moveToFirst()){
                String vouNo=curMaxVou.getString(0);
                int customNo=Integer.parseInt(vouNo.substring(6));
                dbWrite.execSQL("update VouFormat set CustomNo="+customNo);
            }
        }
    }
    public int getTranIDFromMasterSaleTempByTableID(int tableid){
        int tranid = 0;
        SQLiteDatabase dbWrite = this.getWritableDatabase();
        SQLiteDatabase dbRead = this.getReadableDatabase();
        Cursor curTranID = dbRead.rawQuery("select TranID from MasterSaleTemp where TableID=" + tableid, null);
        if(!curTranID.moveToFirst()){
            tranid = getSysTranID();
            ContentValues value = new ContentValues();
            value.put("TranID", tranid);
            value.put("TableID",tableid);
            value.put("OrderState",0);
            dbWrite.insert("MasterSaleTemp", null, value);
            increaseSysTranID();
        }
        Cursor cur=dbRead.rawQuery("select TranID from MasterSaleTemp where TableID="+tableid,null);
        if(cur.moveToFirst())tranid=cur.getInt(0);
        return tranid;
    }
    public void updateMasterSaleTempByTableID(int tableid,int waiterid,String startTime){
        SQLiteDatabase dbWrite = this.getWritableDatabase();
        String vouformat = "";

        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateFormat=new SimpleDateFormat(SystemSetting.DATE_FORMAT);
        String date=dateFormat.format(c.getTime());

        SimpleDateFormat timeFormat=new SimpleDateFormat(SystemSetting.TIME_FORMAT);
        String time=timeFormat.format(c.getTime());

        Cursor curVouFormat = getVouFormat();
        increaseVouFormat();
        if (curVouFormat.moveToFirst()) {
            String shopName = curVouFormat.getString(0);
            String year = curVouFormat.getString(1);
            String month = curVouFormat.getString(2);
            int customNo = curVouFormat.getInt(3);
            vouformat = shopName + year + month + customNo;
        }

        ContentValues value = new ContentValues();
        value.put("Date", date);
        value.put("VouNo", vouformat);
        value.put("WaiterID", waiterid);
        value.put("Time",time);
        value.put("OrderState",1);
        value.put("StartTime",startTime);
        dbWrite.update("MasterSaleTemp", value, "TableID=?", new String[]{Integer.toString(tableid)});
    }
    public void updateTranIDFromMasterSaleTempByTableID(int tableid) {
        SQLiteDatabase dbWrite = this.getWritableDatabase();
        int tranid = getSysTranID();
        increaseSysTranID();
        ContentValues value = new ContentValues();
        value.put("TranID", tranid);
        value.put("OrderState",0);
        dbWrite.update("MasterSaleTemp", value, "TableID=?", new String[]{Integer.toString(tableid)});
    }
    public int isOrderStateOrNotByTableID(int tableid){
        int orderState=0;
        SQLiteDatabase dbRead = this.getReadableDatabase();
        Cursor cur=dbRead.rawQuery("select OrderState from MasterSaleTemp where TableID="+tableid,null);
        if(cur.moveToFirst())orderState=cur.getInt(0);
        return orderState;
    }
    /**public boolean insertUpdateTranIDToMasterSaleTemp(int tableid,int waiterid) {
        int tranid = 0;
        String vouformat = "";
        SQLiteDatabase dbWrite = this.getWritableDatabase();
        SQLiteDatabase dbRead = this.getReadableDatabase();
        Cursor curTranID = getSysTranID();
        increaseSysTranID();
        if (curTranID.moveToFirst()) tranid = curTranID.getInt(0);
        Cursor curVouFormat = getVouFormat();
        increaseVouFormat();
        if (curVouFormat.moveToFirst()) {
            String shopName = curVouFormat.getString(0);
            String year = curVouFormat.getString(1);
            String month = curVouFormat.getString(2);
            int customNo = curVouFormat.getInt(3);
            vouformat = shopName + year + month + customNo;
        }

        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateFormat=new SimpleDateFormat(LoginActivity.DATE_FORMAT);
        String date=dateFormat.format(c.getTime());

        SimpleDateFormat timeFormat=new SimpleDateFormat(LoginActivity.TIME_FORMAT);
        String time=timeFormat.format(c.getTime());

        Cursor curMaster = dbRead.rawQuery("select * from MasterSaleTemp where TableID=" + tableid, null);
        if (curMaster.moveToFirst()) {
            ContentValues value = new ContentValues();
            value.put("TranID", tranid);
            value.put("Date", date);
            value.put("VouNo", vouformat);
            value.put("WaiterID", waiterid);
            value.put("Time",time);
            dbWrite.update("MasterSaleTemp", value, "TableID=?", new String[]{Integer.toString(tableid)});
        }else{
            ContentValues value = new ContentValues();
            value.put("TranID", tranid);
            value.put("Date", date);
            value.put("VouNo", vouformat);
            value.put("WaiterID", waiterid);
            value.put("TableID",tableid);
            value.put("Time",time);
            dbWrite.insert("MasterSaleTemp", null, value);
        }
        return true;
    }
    public Cursor getTranIDFromMasterSaleTemp(int tableid){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select TranID from MasterSaleTemp where TableID="+tableid,null);
        return cur;
    }**/
    public boolean insertTranSaleTempByIntegerQty(int tranid,int srno,String itemid,String itemName,int qty,double salePrice,double amount,String orderTime,String taste,int counterid,int tableid,int stype){
        SQLiteDatabase dbWrite = this.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put("TranID", tranid);
        value.put("SrNo", srno);
        value.put("ItemID", itemid);
        value.put("ItemName", itemName);
        value.put("Quantity",qty);
        value.put("SalePrice", salePrice);
        value.put("Amount",amount);
        value.put("OrderTime", orderTime);
        value.put("Taste",taste);
        value.put("CounterID", counterid);
        value.put("TableID",tableid);
        value.put("ItemDeleted",0);
        value.put("STypeID",stype);
        value.put("OrderOut",0);
        dbWrite.insert("TranSaleTemp", null, value);
        return true;
    }
    public boolean insertTranSaleTempByFloatQty(int tranid,int srno,String itemid,String itemName,float qty,double salePrice,double amount,String orderTime,String taste,int counterid,int tableid,int stype){
        SQLiteDatabase dbWrite = this.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put("TranID", tranid);
        value.put("SrNo", srno);
        value.put("ItemID", itemid);
        value.put("ItemName", itemName);
        value.put("Quantity",qty);
        value.put("SalePrice", salePrice);
        value.put("Amount",amount);
        value.put("OrderTime", orderTime);
        value.put("Taste",taste);
        value.put("CounterID", counterid);
        value.put("TableID",tableid);
        value.put("ItemDeleted",0);
        value.put("STypeID",stype);
        value.put("OrderOut",0);
        dbWrite.insert("TranSaleTemp", null, value);
        return true;
    }
    public Cursor getTableByTableTypeFromTranSaleTemp(int tableTypeID){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select distinct(ts.TableID),tb.TableName from TranSaleTemp ts inner join tblTable tb on ts.TableID=tb.TableID where tb.TableTypeID="+tableTypeID+" order by tb.TableID",null);
        return cur;
    }
    public Cursor getTableFromTranSaleTemp(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select distinct(ts.TableID),tb.TableName from TranSaleTemp ts inner join tblTable tb on ts.TableID=tb.TableID order by tb.TableID",null);
        return cur;
    }
    public boolean changeTable(int oldTableID,int newTableID){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("update TranSaleTemp set TableID=" + newTableID + " where TableID=" + oldTableID);
        db.execSQL("update MasterSaleTemp set TableID=" + newTableID + " where TableID=" + oldTableID);
        return true;
    }
    public Cursor getTableByTableTypeFromBooking(int tableTypeID){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select booking.TableID,tb.TableName from Booking booking inner join tblTable tb on booking.TableID=tb.TableID where tb.TableTypeID=" + tableTypeID +" and booking.Deleted=0 order by tb.TableID",null);
        return cur;
    }
    public Cursor getTransactionFromTranSaleTemp(int tableid){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur;
        if(getFeatureResult(FeatureList.fOrderTime)==1)
            cur=db.rawQuery("select distinct(ItemID),ItemName,sum(Quantity),sum(CAST(ROUND(Amount, 2) AS Real)),TranID,SalePrice,Taste,STypeID,OrderTime from TranSaleTemp where ItemDeleted=0 AND TableID="+tableid+" group by ItemName,TranID,ItemID,SalePrice,OrderTime order by SrNo",null);
        else
            cur=db.rawQuery("select distinct(ItemID),ItemName,sum(Quantity),sum(CAST(ROUND(Amount, 2) AS Real)),TranID,SalePrice,Taste,STypeID from TranSaleTemp where ItemDeleted=0 AND TableID="+tableid+" group by ItemName,TranID,ItemID,SalePrice order by SrNo",null);
        return cur;
    }
    public Cursor getTransactionFromTranSaleTemp(List<Integer> lstTableId){
        String tables="";
        for(int i=0;i<lstTableId.size();i++){
            tables+=lstTableId.get(i)+",";
        }
        tables=tables.substring(0,tables.length()-1);
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur;
        if(getFeatureResult(FeatureList.fOrderTime)==1)
            cur=db.rawQuery("select distinct(ItemID),ItemName,sum(Quantity),sum(CAST(ROUND(Amount, 2) AS Real)),TranID,SalePrice,Taste,STypeID,OrderTime from TranSaleTemp where ItemDeleted=0 AND TableID IN("+tables+") group by ItemName,TranID,ItemID,SalePrice,OrderTime order by SrNo",null);
        else
            cur=db.rawQuery("select distinct(ItemID),ItemName,sum(Quantity),sum(CAST(ROUND(Amount, 2) AS Real)),TranID,SalePrice,Taste,STypeID from TranSaleTemp where ItemDeleted=0 AND TableID IN("+tables+") group by ItemName,TranID,ItemID,SalePrice order by SrNo",null);
        return cur;
    }
    public Cursor getTranForOrderPrint(int tableid){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select ItemID,ItemName,sum(Quantity),Taste,STypeID from TranSaleTemp where ItemDeleted=0 AND OrderOut=0 AND TableID="+tableid+" group by ItemID,ItemName,Taste,STypeID order by SrNo",null);
        return cur;
    }
    public void updateOrderOutByTableID(int tableid) {
        SQLiteDatabase dbWrite = this.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put("OrderOut", 1);
        dbWrite.update("TranSaleTemp", value, "TableID=?", new String[]{Integer.toString(tableid)});
    }
    public Cursor getMaxSrNoFromTranSaleTemp(int tableid){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select MAX(SrNo) from TranSaleTemp where ItemDeleted=0 AND TableID="+tableid,null);
        return cur;
    }
    public Cursor getTransactionDetailFromMasterSaleTemp(int tableid){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select TranID,Date,VouNo,TableID,WaiterID,StartTime from MasterSaleTemp where TableID="+tableid,null);
        return cur;
    }
    public Cursor getTransactionDetailFromTranSaleTemp(int tableid){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select TranID,SrNo,ItemID,ItemName,Quantity,SalePrice,Amount,OrderTime,Taste,CounterID from TranSaleTemp where ItemDeleted=0 AND TableID="+tableid+" order by SrNo",null);
        return cur;
    }
    public boolean insertMasterSale(int tranid,String date,String vouno,int tableid,int waiterid,double subTotal,double tax,double charges,double discount,double grandTotal,String time,int slipid,String startTime,String endTime){
        SQLiteDatabase dbWrite = this.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put("TranID", tranid);
        value.put("Date", date);
        value.put("VouNo", vouno);
        value.put("TableID", tableid);
        value.put("WaiterID",waiterid);
        value.put("SubTotal",subTotal);
        value.put("Tax",tax);
        value.put("charges",charges);
        value.put("discount",discount);
        value.put("GrandTotal",grandTotal);
        value.put("Time",time);
        value.put("SlipID",slipid);
        value.put("StartTime",startTime);
        value.put("EndTime",endTime);
        dbWrite.insert("MasterSale", null, value);
        return true;
    }
    public boolean insertTranSale(int tranid,int srno,String itemid,String itemName,double qty,double salePrice,double amount,String orderTime,String taste,int counterid,String time){
        SQLiteDatabase dbWrite = this.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put("TranID", tranid);
        value.put("SrNo", srno);
        value.put("ItemID", itemid);
        value.put("ItemName", itemName);
        value.put("Quantity",qty);
        value.put("SalePrice", salePrice);
        value.put("Amount",amount);
        value.put("OrderTime", orderTime);
        value.put("Taste",taste);
        value.put("CounterID", counterid);
        value.put("Time", time);
        dbWrite.insert("TranSale", null, value);
        return true;
    }
    public boolean deleteTransactionFromTranSaleTemp(int tableid){
        SQLiteDatabase dbWrite = this.getWritableDatabase();
        dbWrite.execSQL("delete from TranSaleTemp where TableID="+tableid);
        return true;
    }
    public boolean deleteTransactionItemFromTranSaleTemp(int tranid,String itemid) {
        SQLiteDatabase dbWrite = this.getWritableDatabase();
        dbWrite.execSQL("update TranSaleTemp set ItemDeleted=1 where TranID=" + tranid + " and ItemID='" + itemid + "'");
        return true;
    }
    public Cursor getMasterSale(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select * from MasterSale",null);
        return cur;
    }
    public Cursor getMasterSaleByDate(String fromDate,String toDate){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select * from MasterSale WHERE DATE(Date) BETWEEN DATE('"+fromDate+"') AND DATE('"+toDate+"')",null);
        return cur;
    }
    public Cursor getTranSale(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select ts.TranID,SrNo,ItemID,ItemName,Quantity,SalePrice,Amount,OrderTime,Taste,CounterID,ts.Time,ms.Date from TranSale ts inner join MasterSale ms on ts.TranID=ms.TranID",null);
        return cur;
    }
    public Cursor getTranSaleByTranID(int tranId){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select TranID,SrNo,ItemID,ItemName,Quantity,SalePrice,Amount from TranSale where TranID="+tranId,null);
        return cur;
    }
    public Cursor getTranSaleByDate(String fromDate,String toDate){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select ts.TranID,SrNo,ItemID,ItemName,Quantity,SalePrice,Amount,OrderTime,Taste,CounterID,ts.Time,ms.Date from TranSale ts inner join MasterSale ms on ts.TranID=ms.TranID WHERE DATE(Date) BETWEEN DATE('"+fromDate+"') AND DATE('"+toDate+"')",null);
        return cur;
    }
    public Cursor getMasterSaleData(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select ms.TranID,ms.VouNo,ms.Date,w.WaiterName,tb.TableName from MasterSale ms inner join Waiter w on ms.WaiterID=w.WaiterID inner join tblTable tb on ms.TableID=tb.TableID",null);
        return cur;
    }
    public Cursor getMasterSaleDataByDate(String fromDate,String toDate){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select ms.TranID,ms.VouNo,ms.Date,w.WaiterName,tb.TableName from MasterSale ms inner join Waiter w on ms.WaiterID=w.WaiterID inner join tblTable tb on ms.TableID=tb.TableID WHERE DATE(ms.Date) BETWEEN DATE('"+fromDate+"') AND DATE('"+toDate+"')",null);
        return cur;
    }
    public boolean deleteSaleVouByTranId(int tranId){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("Delete From TranSale Where TranID="+tranId);
        db.execSQL("Delete From MasterSale Where TranID="+tranId);
        return true;
    }
    public boolean deleteSaleTranByTranItemId(int tranId,String itemId){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("Delete From TranSale Where TranID="+tranId+" And ItemID='"+itemId+"'");
        return true;
    }
    public Cursor getSaleVouByVouNo(String vouNo){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select ms.TranID,ms.VouNo,ms.Date,w.WaiterName,tb.TableName from MasterSale ms inner join Waiter w on ms.WaiterID=w.WaiterID inner join tblTable tb on ms.TableID=tb.TableID where ms.VouNo='"+vouNo+"'",null);
        return cur;
    }

    //Open Order
    public Cursor getWaiterForOpenOrder(){
        SQLiteDatabase dbRead=this.getReadableDatabase();
        Cursor cur=dbRead.rawQuery("SELECT Distinct(ms.WaiterID),waiter.WaiterName FROM MasterSaleTemp ms INNER JOIN Waiter waiter ON ms.WaiterID=waiter.WaiterID ORDER BY ms.WaiterID",null);
        return cur;
    }
    public Cursor getOpenOrder(){
        SQLiteDatabase dbRead=this.getReadableDatabase();
        Cursor cur=dbRead.rawQuery("SELECT Distinct(ms.TranID),ms.WaiterID,ms.Date,tab.TableName,cus.Total,ms.Time FROM MasterSaleTemp ms INNER JOIN TranSaleTemp ts ON ms.TranID=ts.TranID INNER JOIN tblTable tab ON ms.TableID=tab.TableID LEFT JOIN Customer cus ON ms.TranID=cus.TranID ORDER BY ms.TranID",null);
        return cur;
    }

    //Printer Methods
    public boolean insertPrinterModel(String name,int interfaceId,int widthId){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("ModelName", name);
        value.put("InterfaceID", interfaceId);
        value.put("WidthID", widthId);
        db.insert("PrinterModel", null, value);
        return true;
    }
    public Cursor getPrinterModel(){
        SQLiteDatabase dbRead=this.getReadableDatabase();
        Cursor cur=dbRead.rawQuery("SELECT ModelID,ModelName,InterfaceID,WidthID FROM PrinterModel",null);
        return cur;
    }
    public boolean insertPrinterInterface(String name){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("InterfaceName", name);
        db.insert("PrinterInterface", null, value);
        return true;
    }
    public Cursor getPrinterInterface(){
        SQLiteDatabase dbRead=this.getReadableDatabase();
        Cursor cur=dbRead.rawQuery("SELECT InterfaceID,InterfaceName FROM PrinterInterface",null);
        return cur;
    }
    public boolean insertPaperWidth(String name){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("WidthName", name);
        db.insert("PaperWidth", null, value);
        return true;
    }
    public Cursor getPaperWidth(){
        SQLiteDatabase dbRead=this.getReadableDatabase();
        Cursor cur=dbRead.rawQuery("SELECT WidthID,WidthName FROM PaperWidth",null);
        return cur;
    }
    public void deletePaperWidth(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from PaperWidth");
    }
    public void deletePrinterInterface(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from PrinterInterface");
    }
    public void deletePrinterModel(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from PrinterModel");
    }
    public boolean insertPrinter(String name,int modelId,int interfaceId,String printerAddress,int widthId,int printCount,int isReceipt){
        SQLiteDatabase db=this.getWritableDatabase();
        SQLiteDatabase dbRead=this.getReadableDatabase();
        if(interfaceId==1) {
            Cursor cur = dbRead.rawQuery("SELECT InterfaceID FROM Printer WHERE InterfaceID=1", null);
            int count = cur.getCount();
            if (count == 1) return false;
        }
        ContentValues value=new ContentValues();
        value.put("PrinterName", name);
        value.put("ModelID", modelId);
        value.put("InterfaceID", interfaceId);
        value.put("PrinterAddress", printerAddress);
        value.put("WidthID", widthId);
        value.put("PrintCount",printCount);
        value.put("isReceipt",isReceipt);
        db.insert("Printer", null, value);
        return true;
    }
    public boolean updatePrinterByPrinterID(int id,String name,int modelId,int interfaceId,String printerAddress,int widthId,int printCount,int isReceipt){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("PrinterName", name);
        value.put("ModelID", modelId);
        value.put("InterfaceID", interfaceId);
        value.put("PrinterAddress", printerAddress);
        value.put("WidthID", widthId);
        value.put("PrintCount",printCount);
        value.put("isReceipt",isReceipt);
        db.update("Printer", value, "PrinterID=?", new String[]{Integer.toString(id)});
        return true;
    }
    public boolean isExistNetPrinter(int id,int interfaceId){
        SQLiteDatabase dbRead=this.getReadableDatabase();
        if(interfaceId==1) {
            Cursor cur = dbRead.rawQuery("SELECT PrinterID FROM Printer WHERE InterfaceID=1", null);
            if(cur.moveToFirst()){
                int printerid=cur.getInt(0);
                if(printerid==id){
                    return true;
                }else{
                    return false;
                }
            }else{
                return true;
            }
        }
        return true;
    }
    public boolean deletePrinterByPrinterID(int id){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from Printer where PrinterID="+id);
        db.execSQL("delete from ItemSTypePrinterSetting where PrinterID="+id);
        return true;
    }
    public Cursor getPrinter(){
        SQLiteDatabase dbRead=this.getReadableDatabase();
        Cursor cur=dbRead.rawQuery("SELECT PrinterID,PrinterName,printer.ModelID,printer.InterfaceID,PrinterAddress,printer.WidthID,model.ModelName,interface.InterfaceName,width.WidthName,PrintCount,isReceipt FROM Printer printer INNER JOIN PrinterModel model ON printer.ModelID=model.ModelID INNER JOIN PrinterInterface interface ON printer.InterfaceID=interface.InterfaceID INNER JOIN PaperWidth width ON printer.WidthID=width.WidthID",null);
        return cur;
    }
    public Cursor getPrinter80(){
        SQLiteDatabase dbRead=this.getReadableDatabase();
        Cursor cur=dbRead.rawQuery("SELECT PrinterID,PrinterName,printer.ModelID,printer.InterfaceID,PrinterAddress,printer.WidthID,model.ModelName,interface.InterfaceName,width.WidthName,PrintCount,isReceipt FROM Printer printer INNER JOIN PrinterModel model ON printer.ModelID=model.ModelID INNER JOIN PrinterInterface interface ON printer.InterfaceID=interface.InterfaceID INNER JOIN PaperWidth width ON printer.WidthID=width.WidthID WHERE printer.WidthID=2",null);
        return cur;
    }
    public Cursor getPrinterIdBySTypeId(long stypeid){
        SQLiteDatabase dbRead=this.getReadableDatabase();
        Cursor cur=dbRead.rawQuery("SELECT PrinterID FROM ItemSTypePrinterSetting WHERE STypeID="+stypeid,null);
        return cur;
    }
    public Cursor getPrinterByPrinterId(int printerid){
        SQLiteDatabase dbRead=this.getReadableDatabase();
        Cursor cur=dbRead.rawQuery("SELECT PrinterID,PrinterName,printer.ModelID,printer.InterfaceID,PrinterAddress,printer.WidthID,model.ModelName,interface.InterfaceName,width.WidthName,PrintCount,isReceipt FROM Printer printer INNER JOIN PrinterModel model ON printer.ModelID=model.ModelID INNER JOIN PrinterInterface interface ON printer.InterfaceID=interface.InterfaceID INNER JOIN PaperWidth width ON printer.WidthID=width.WidthID WHERE PrinterID="+printerid,null);
        return cur;
    }
    public Cursor getReceiptPrinter(){
        SQLiteDatabase dbRead=this.getReadableDatabase();
        Cursor cur=dbRead.rawQuery("SELECT PrinterID,PrinterName,printer.ModelID,printer.InterfaceID,PrinterAddress,printer.WidthID,model.ModelName,interface.InterfaceName,width.WidthName,PrintCount,isReceipt FROM Printer printer INNER JOIN PrinterModel model ON printer.ModelID=model.ModelID INNER JOIN PrinterInterface interface ON printer.InterfaceID=interface.InterfaceID INNER JOIN PaperWidth width ON printer.WidthID=width.WidthID WHERE isReceipt=1",null);
        return cur;
    }
    public String getNetworkPrinter(){
        String ipAddress;
        SQLiteDatabase dbRead=this.getReadableDatabase();
        Cursor cur=dbRead.rawQuery("SELECT PrinterAddress FROM Printer WHERE InterfaceID=1",null);
        if(cur.moveToFirst()){
            ipAddress=cur.getString(0);
        }else{
            ipAddress="";
        }
        return ipAddress;
    }

    /**
     * test code
     */
    public void testTranCount(){
        SQLiteDatabase dbRead=this.getReadableDatabase();
        Cursor cur=dbRead.rawQuery("SELECT ts.ItemID FROM TranSale ts INNER JOIN MasterSale ms ON ts.TranID=ms.TranID",null);
        int count= cur.getCount();
    }
    public Cursor testExtraTran(){
        List<Integer> lstMTranID=new ArrayList<>();
        String mTranID="";
        SQLiteDatabase dbRead=this.getReadableDatabase();
        Cursor cur=dbRead.rawQuery("SELECT TranID FROM MasterSale",null);

        while(cur.moveToNext()) {
            lstMTranID.add(cur.getInt(0));
        }
        for(int i=0;i<lstMTranID.size();i++){
            mTranID+=lstMTranID.get(i)+",";
        }
        if(mTranID.length()!=0) mTranID = mTranID.substring(0, mTranID.length() - 1);

        Cursor cur2=dbRead.rawQuery("SELECT TranID,SrNo,ItemID,ItemName,Quantity,SalePrice,Amount FROM TranSale WHERE TranID NOT IN ("+mTranID+")",null);
        return cur2;
    }
    public String testVouNoByTranID(int tranid){
        String vouNo="no voucher number";
        SQLiteDatabase dbRead=this.getReadableDatabase();
        Cursor cur=dbRead.rawQuery("SELECT VouNo FROM MasterSale WHERE TranID="+tranid,null);
        if(cur.moveToFirst()) vouNo=cur.getString(0);
        return vouNo;
    }
    public boolean testDelExtraAllTran(List<Integer> lstTranID){
        SQLiteDatabase db=this.getWritableDatabase();
        for(int i=0;i<lstTranID.size();i++){
            db.execSQL("Delete From TranSale Where TranID="+lstTranID.get(i));
        }
        return true;
    }
    public boolean testDelExtraTran(int tranId,int srNo,String itemId){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("Delete From TranSale Where TranID="+tranId+" And ItemID='"+itemId+"' And SrNo="+srNo);
        return true;
    }
}
