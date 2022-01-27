package com.bosictsolution.waiterone;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import adapter.SpItemSubGroupAdapter;
import adapter.StCompanyListAdapter;
import adapter.SpCompanyAdapter;
import adapter.StItemListAdapter;
import adapter.StItemSubGroupListAdapter;
import adapter.StItemSubListAdapter;
import adapter.StMainMenuListAdapter;
import adapter.SpMainMenuAdapter;
import adapter.ModuleListAdapter;
import adapter.StSTypeListAdapter;
import adapter.SpSTypeAdapter;
import adapter.StSubMenuListAdapter;
import adapter.SpSubMenuAdapter;
import adapter.StTableListAdapter;
import adapter.StTableTypeListAdapter;
import adapter.SpTableTypeAdapter;
import adapter.StTasteListAdapter;
import adapter.StWaiterListAdapter;
import common.DBHelper;
import common.FeatureList;
import common.SystemSetting;
import data.CompanyData;
import data.ItemData;
import data.ItemSubData;
import data.ItemSubGroupData;
import data.STypeData;
import data.MainMenuData;
import data.ModuleData;
import data.SubMenuData;
import data.TableData;
import data.TableTypeData;
import data.TasteData;
import data.WaiterData;
import listener.ModuleCheckedListener;
import listener.SetupEditDeleteButtonClickListener;

public class SetupActivity extends AppCompatActivity implements SetupEditDeleteButtonClickListener, ModuleCheckedListener {

    Button btnWaiterModule, btnTableModule, btnTableTypeModule, btnTasteModule, btnMainMenuModule, btnSubMenuModule, btnItemModule, btnSystemSettingModule,btnSTypeModule,btnCompanyModule,
            btnNewAddWaiter, btnNewAddTable, btnNewAddTableType, btnNewAddTaste, btnNewAddMainMenu, btnNewAddSubMenu, btnNewAddItem,btnNewAddItemSubGroup,btnNewAddItemSub,btnNewAddSType,btnNewAddCompany,btnAddSystemSetting,btnAddVoucherSetting,
            btnSearchWaiter,btnSearchTableType,btnSearchTable,btnSearchMainMenu,btnSearchTaste,btnSearchSubMenu,btnSearchItem,btnSearchSType,btnSearchCompany, btnVoucherSettingModule,btnItemSubGroupModule,btnItemSubModule,btnAddItemSub;
    ImageButton btnShopLogo,btnCancelLogo;
    EditText etSearchWaiterName, etSearchTableType, etSearchTable, etSearchTaste, etSearchMainMenu, etSearchSubMenu, etSearchItemID, etSearchItemName,etSearchSType,etSearchCompany,etShopName,etCharges,etTax,
            etTitle,etDescription,etPhone,etMessage,etMessage2,etAddress,etText;
    Spinner spSearchTableType, spSearchMainMenu, spSearchSubMenu, spSearchMainMenuForItem,spCompany;
    TextView tvCharges,tvTax,tvShopName,tvCompany,tvHeaderSubMenu,tvHeaderMainMenuName,tvHeaderSortCode,tvHeaderItemID,tvHeaderItemName,tvHeaderSubMenuName,tvHeaderPrice,tvHeaderOutOfOrder,tvConfirmMessage,
            tvLogo,tvTitle,tvDescription,tvPhone,tvMessage,tvMessage2,tvAddress;
    ListView lvSetup;
    Switch switchBOSKeyboard;
    LinearLayout layoutSetupWaiter,layoutSetupTable,layoutSetupTableType,layoutSetupTaste,layoutSetupMainMenu,layoutSetupSubMenu,layoutSetupItem,layoutSetupSystemSetting,layoutSetupVoucherSetting,layoutSetupSType,layoutSetupCompany,layoutSetupItemSubGroup,layoutSetupItemSub;
    DBHelper db;
    SystemSetting systemSetting=new SystemSetting();

    StWaiterListAdapter waiterListAdapter;
    StTableListAdapter tableListAdapter;
    StTableTypeListAdapter tableTypeListAdapter;
    StTasteListAdapter tasteListAdapter;
    StMainMenuListAdapter mainMenuListAdapter;
    StSubMenuListAdapter subMenuListAdapter;
    StItemListAdapter itemListAdapter;
    StItemSubGroupListAdapter itemSubGroupListAdapter;
    StItemSubListAdapter itemSubListAdapter;
    StSTypeListAdapter stSTypeListAdapter;
    StCompanyListAdapter stCompanyListAdapter;

    List<WaiterData> lstWaiterData;
    List<TableTypeData> lstTableTypeData;
    List<TableData> lstTableData;
    List<TasteData> lstTasteData;
    List<MainMenuData> lstMainMenuData;
    List<SubMenuData> lstSubMenuData,lstSubMenuDataForItemSearch;
    List<ItemData> lstItemData;
    List<ItemSubGroupData> lstItemSubGroupData,lstIncludeItemSubGroup=new ArrayList<>();
    List<ItemSubData> lstItemSubData;
    List<STypeData> lstSTypeData;
    List<CompanyData> lstCompanyData;

    SpTableTypeAdapter spTableTypeAdapter;
    SpItemSubGroupAdapter spItemSubGroupAdapter;
    SpMainMenuAdapter spMainMenuAdapter;
    SpSubMenuAdapter spSubMenuAdapter;
    SpSTypeAdapter spSTypeAdapter;
    SpCompanyAdapter spCompanyAdapter;
    ImageView ivItemImage;

    private Context context=this;
    int selectedMainMenuID,deleteid,editid,editSubMenuPosition,editSubMenuID;
    String confirmMessage,setupModuleName="";
    int BOS_KEYBOARD_COUNT;

    List<ModuleData> lstModuleData;
    ModuleListAdapter moduleListAdapter;
    ArrayList<Integer> lstCheckedModuleID=new ArrayList<>();
    boolean BOS_KEYBOARD_ON;
    private int GALLERY = 1 , SHOP_LOGO=2;
    byte[] saveImage;
    Bitmap saveBitmap;
    private static final int ADD_ITEM_SUB_REQUEST = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        ActionBar actionbar=getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowCustomEnabled(true);
        actionbar.setDisplayShowTitleEnabled(true);

        db=new DBHelper(this);
        setLayoutResource();
        setTitle("");

        btnWaiterModule.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                setupModuleName= btnWaiterModule.getText().toString();
                clearAllLayout(btnWaiterModule);
                clearWaiterControls();
                showWaiterList();
                layoutSetupWaiter.setVisibility(View.VISIBLE);
            }
        });
        btnTableModule.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                setupModuleName= btnTableModule.getText().toString();
                clearAllLayout(btnTableModule);
                clearTableControls();
                bindTableType(spSearchTableType);
                showTableList();
                layoutSetupTable.setVisibility(View.VISIBLE);
            }
        });
        btnTableTypeModule.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                setupModuleName= btnTableTypeModule.getText().toString();
                clearAllLayout(btnTableTypeModule);
                clearTableTypeControls();
                showTableTypeList();
                layoutSetupTableType.setVisibility(View.VISIBLE);
            }
        });
        btnSTypeModule.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                setupModuleName= btnSTypeModule.getText().toString();
                clearAllLayout(btnSTypeModule);
                clearSTypeControls();
                showSTypeList();
                layoutSetupSType.setVisibility(View.VISIBLE);
            }
        });
        btnTasteModule.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                setupModuleName= btnTasteModule.getText().toString();
                clearAllLayout(btnTasteModule);
                clearTasteControls();
                showTasteList();
                layoutSetupTaste.setVisibility(View.VISIBLE);
            }
        });
        btnMainMenuModule.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                setupModuleName= btnMainMenuModule.getText().toString();
                clearAllLayout(btnMainMenuModule);
                clearMainMenuControls();
                showMainMenuList();
                layoutSetupMainMenu.setVisibility(View.VISIBLE);
            }
        });
        btnSubMenuModule.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                setupModuleName= btnSubMenuModule.getText().toString();
                clearAllLayout(btnSubMenuModule);
                clearSubMenuControls();
                bindMainMenu(spSearchMainMenu);
                showSubMenuList();
                layoutSetupSubMenu.setVisibility(View.VISIBLE);
            }
        });
        btnItemModule.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                setupModuleName= btnItemModule.getText().toString();
                clearAllLayout(btnItemModule);
                clearItemControls();
                bindMainMenu(spSearchMainMenuForItem);
                bindSubMenuForItemSearch(spSearchSubMenu);
                showItemList();
                layoutSetupItem.setVisibility(View.VISIBLE);
            }
        });
        btnItemSubGroupModule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupModuleName= btnItemSubGroupModule.getText().toString();
                clearAllLayout(btnItemSubGroupModule);
                showItemSubGroupList();
                layoutSetupItemSubGroup.setVisibility(View.VISIBLE);
            }
        });
        btnItemSubModule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupModuleName= btnItemSubModule.getText().toString();
                clearAllLayout(btnItemSubModule);
                showItemSubList();
                layoutSetupItemSub.setVisibility(View.VISIBLE);
            }
        });
        btnCompanyModule.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                setupModuleName= btnCompanyModule.getText().toString();
                clearAllLayout(btnCompanyModule);
                clearCompanyControls();
                showCompanyList();
                layoutSetupCompany.setVisibility(View.VISIBLE);
            }
        });
        btnSystemSettingModule.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                setupModuleName="";
                clearAllLayout(btnSystemSettingModule);
                lvSetup.setVisibility(View.GONE);
                layoutSetupSystemSetting.setVisibility(View.VISIBLE);
                bindCompany();
                Cursor cur=db.getSystemSetting();
                if(cur.moveToFirst()){
                    etShopName.setText(cur.getString(0));
                    etTax.setText(String.valueOf(cur.getInt(1)));
                    etCharges.setText(String.valueOf(cur.getInt(2)));
                    int companyid=cur.getInt(3);
                    for(int i=0;i<lstCompanyData.size();i++){
                        if(lstCompanyData.get(i).getCompanyID()==companyid){
                            spCompany.setSelection(i);
                            break;
                        }
                    }
                }
            }
        });
        btnVoucherSettingModule.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                setupModuleName="";
                clearAllLayout(btnVoucherSettingModule);
                lvSetup.setVisibility(View.GONE);
                layoutSetupVoucherSetting.setVisibility(View.VISIBLE);
                loadShopLogoFromWaiterOneDB();
                Cursor cur=db.getVoucherSetting();
                if(cur.moveToFirst()){
                    etTitle.setText(cur.getString(0));
                    etDescription.setText(cur.getString(1));
                    etPhone.setText(cur.getString(2));
                    etMessage.setText(cur.getString(3));
                    etAddress.setText(cur.getString(4));
                    etMessage2.setText(cur.getString(5));
                }
            }
        });
        spSearchMainMenuForItem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (lstMainMenuData.size() != 0) {
                    selectedMainMenuID = lstMainMenuData.get(position).getMainMenuID();
                }
                bindSubMenuForItemSearch(spSearchSubMenu);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnNewAddWaiter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                showUserDialog("","",false);
            }
        });
        btnSearchWaiter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
               showWaiterList();
            }
        });
        btnNewAddTableType.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                showTableTypeDialog("",false);
            }
        });
        btnSearchTableType.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
              showTableTypeList();
            }
        });
        btnNewAddSType.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                showSTypeDialog("",false);
            }
        });
        btnSearchSType.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
               showSTypeList();
            }
        });
        btnNewAddTable.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                showTableDialog(0,"",false);
            }
        });
        btnSearchTable.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                showTableList();
            }
        });
        btnNewAddTaste.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                showTasteDialog("",false);
            }
        });
        btnSearchTaste.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
              showTasteList();
            }
        });
        btnNewAddMainMenu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                showMainMenuDialog("",0,false);
            }
        });
        btnSearchMainMenu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
               showMainMenuList();
            }
        });
        btnNewAddSubMenu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                showSubMenuDialog(0,"","",false);
            }
        });
        btnSearchSubMenu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                showSubMenuList();
            }
        });
        btnNewAddItem.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                showItemDialog(0,0,"","",0,0,false,null);
            }
        });
        btnSearchItem.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                showItemList();
            }
        });
        btnNewAddItemSubGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showItemSubGroupDialog("","",1,false);
            }
        });
        btnNewAddItemSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showItemSubDialog(0,"",0,false);
            }
        });
        btnNewAddCompany.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                showCompanyDialog("",false);
            }
        });
        btnSearchCompany.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                showCompanyList();
            }
        });
        btnAddSystemSetting.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                addSystemSetting();
            }
        });
        btnAddVoucherSetting.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                addVoucherSetting();
            }
        });
        btnShopLogo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Shop Logo"), SHOP_LOGO);
            }
        });
        btnCancelLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmMessage="Are you sure to remove logo?";
                showConfirmDialog(0);
            }
        });
    }

    @Override
    public void setTitle(CharSequence title){
        LayoutInflater inflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vi=inflater.inflate(R.layout.actionbar_setup, null);
        ImageButton btnRefresh=(ImageButton)vi.findViewById(R.id.btnRefresh);
        switchBOSKeyboard=(Switch)vi.findViewById(R.id.switchBOSKeyboard);

        ActionBar.LayoutParams params=new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT);
        getSupportActionBar().setCustomView(vi, params);

        switchBOSKeyboard.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    BOS_KEYBOARD_ON=true;
                }else{
                    BOS_KEYBOARD_ON=false;
                }
            }
        });

        btnRefresh.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(setupModuleName.equals(btnWaiterModule.getText().toString())){
                    clearWaiterControls();
                    showWaiterList();
                }else if(setupModuleName.equals(btnTableTypeModule.getText().toString())){
                    clearTableTypeControls();
                    showTableTypeList();
                }
                else if(setupModuleName.equals(btnTableModule.getText().toString())){
                    clearTableControls();
                    showTableList();
                }
                else if(setupModuleName.equals(btnMainMenuModule.getText().toString())){
                    clearMainMenuControls();
                    showMainMenuList();
                }
                else if(setupModuleName.equals(btnSubMenuModule.getText().toString())){
                    clearSubMenuControls();
                    showSubMenuList();
                }
                else if(setupModuleName.equals(btnItemModule.getText().toString())){
                    clearItemControls();
                    showItemList();
                }
                else if(setupModuleName.equals(btnTasteModule.getText().toString())){
                    clearTasteControls();
                    showTasteList();
                }
                else if(setupModuleName.equals(btnSTypeModule.getText().toString())){
                    clearSTypeControls();
                    showSTypeList();
                }
                else if(setupModuleName.equals(btnCompanyModule.getText().toString())){
                    clearCompanyControls();
                    showCompanyList();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onEditButtonClickListener(int position){
        if(setupModuleName.equals(btnWaiterModule.getText().toString())){
            editid=lstWaiterData.get(position).getWaiterid();
            showUserDialog(lstWaiterData.get(position).getWaiterName(),lstWaiterData.get(position).getPassword(),true);
        }else if(setupModuleName.equals(btnTableTypeModule.getText().toString())){
            editid=lstTableTypeData.get(position).getTableTypeID();
            showTableTypeDialog(lstTableTypeData.get(position).getTableTypeName(),true);
        }
        else if(setupModuleName.equals(btnSTypeModule.getText().toString())){
            editid= lstSTypeData.get(position).getsTypeID();
            showSTypeDialog(lstSTypeData.get(position).getsTypeName(),true);
        }
        else if(setupModuleName.equals(btnTableModule.getText().toString())){
            editid=lstTableData.get(position).getTableid();
            int tableTypeid=lstTableData.get(position).getTableTypeID();
            int tableTypePosition=0;
            for(int i=0;i<lstTableTypeData.size();i++){
                if(lstTableTypeData.get(i).getTableTypeID()==tableTypeid){
                    tableTypePosition=i;
                    break;
                }
            }
            showTableDialog(tableTypePosition-1,lstTableData.get(position).getTableName(),true);
        }
        else if(setupModuleName.equals(btnMainMenuModule.getText().toString())){
            editid=lstMainMenuData.get(position).getMainMenuID();
            showMainMenuDialog(lstMainMenuData.get(position).getMainMenuName(),lstMainMenuData.get(position).getCounterid(),true);
        }
        else if(setupModuleName.equals(btnSubMenuModule.getText().toString())){
            editid=lstSubMenuData.get(position).getSubMenuID();
            int mainMenuID=lstSubMenuData.get(position).getMainMenuID();
            int mainMenuPosition=0;
            for(int i=0;i<lstMainMenuData.size();i++){
                if(lstMainMenuData.get(i).getMainMenuID()==mainMenuID){
                    mainMenuPosition=i;
                    break;
                }
            }
            showSubMenuDialog(mainMenuPosition-1,lstSubMenuData.get(position).getSubMenuName(),lstSubMenuData.get(position).getSortCode(),true);
        }
        else if(setupModuleName.equals(btnItemModule.getText().toString())){
            editid=lstItemData.get(position).getId();
            int mainMenuID=lstItemData.get(position).getMainMenuID();
            editSubMenuID=lstItemData.get(position).getSubMenuID();
            int sTypeID=lstItemData.get(position).getsTypeID();
            lstIncludeItemSubGroup=db.getItemSubGroupByItemID(lstItemData.get(position).getItemid());
            showItemDialog(mainMenuID,sTypeID,lstItemData.get(position).getItemid(),lstItemData.get(position).getItemName(),lstItemData.get(position).getPrice(),lstItemData.get(position).getOutOfOrder(),true,lstItemData.get(position).getItemImage());
        }
        else if(setupModuleName.equals(btnItemSubGroupModule.getText().toString())){
            editid=lstItemSubGroupData.get(position).getPkId();
            showItemSubGroupDialog(lstItemSubGroupData.get(position).getSubGroupName(),lstItemSubGroupData.get(position).getSubTitle(),lstItemSubGroupData.get(position).isSingleCheck(),true);
        }
        else if(setupModuleName.equals(btnItemSubModule.getText().toString())){
            editid=lstItemSubData.get(position).getPkId();
            int subGroupId=lstItemSubData.get(position).getSubGroupId();
            int subGroupPosition=0;
            for(int i=0;i<lstItemSubGroupData.size();i++){
                if(lstItemSubGroupData.get(i).getPkId()==subGroupId){
                    subGroupPosition=i;
                    break;
                }
            }
            showItemSubDialog(subGroupPosition,lstItemSubData.get(position).getSubName(),lstItemSubData.get(position).getPrice(),true);
        }
        else if(setupModuleName.equals(btnTasteModule.getText().toString())){
            editid=lstTasteData.get(position).getTasteid();
            showTasteDialog(lstTasteData.get(position).getTasteName(),true);
        }
        else if(setupModuleName.equals(btnCompanyModule.getText().toString())){
            editid=lstCompanyData.get(position).getCompanyID();
            showCompanyDialog(lstCompanyData.get(position).getCompanyName(),true);
        }
    }

    @Override
    public void onDeleteButtonClickListener(int position){
        if(setupModuleName.equals(btnWaiterModule.getText().toString())){
            confirmMessage="Are you sure you want to delete user "+lstWaiterData.get(position).getWaiterName()+"?";
        }else if(setupModuleName.equals(btnTableTypeModule.getText().toString())){
            confirmMessage="Are you sure you want to delete table type "+lstTableTypeData.get(position).getTableTypeName()+"?";
        }
        else if(setupModuleName.equals(btnSTypeModule.getText().toString())){
            confirmMessage="Are you sure you want to delete stype "+ lstSTypeData.get(position).getsTypeName()+"?";
        }
        else if(setupModuleName.equals(btnTableModule.getText().toString())){
            confirmMessage="Are you sure you want to delete table "+lstTableData.get(position).getTableName()+"?";
        }
        else if(setupModuleName.equals(btnMainMenuModule.getText().toString())){
            confirmMessage="Are you sure you want to delete main menu "+lstMainMenuData.get(position).getMainMenuName()+"?";
        }
        else if(setupModuleName.equals(btnSubMenuModule.getText().toString())){
            confirmMessage="Are you sure you want to delete sub menu "+lstSubMenuData.get(position).getSubMenuName()+"?";
        }
        else if(setupModuleName.equals(btnItemModule.getText().toString())){
            confirmMessage="Are you sure you want to delete item "+lstItemData.get(position).getItemName()+"?";
        }
        else if(setupModuleName.equals(btnItemSubGroupModule.getText().toString())){
            confirmMessage="Are you sure you want to delete item sub group "+lstItemSubGroupData.get(position).getSubGroupName()+"?";
        }
        else if(setupModuleName.equals(btnItemSubModule.getText().toString())){
            confirmMessage="Are you sure you want to delete item sub "+lstItemSubData.get(position).getSubName()+"?";
        }
        else if(setupModuleName.equals(btnTasteModule.getText().toString())){
            confirmMessage="Are you sure you want to delete taste "+lstTasteData.get(position).getTasteName()+"?";
        }
        else if(setupModuleName.equals(btnCompanyModule.getText().toString())){
            confirmMessage="Are you sure you want to delete company "+lstCompanyData.get(position).getCompanyName()+"?";
        }
        showConfirmDialog(position);
    }

    @Override
    public void onModuleCheckedListener(int position){
        int checkedModuleID = lstModuleData.get(position).getModuleID();
        if(!lstCheckedModuleID.contains(checkedModuleID)) {
            lstCheckedModuleID.add(lstModuleData.get(position).getModuleID());
        }
    }

    @Override
    public void onModuleUnCheckedListener(int position){
        int removeIndex= lstCheckedModuleID.indexOf(lstModuleData.get(position).getModuleID());
        if(removeIndex!=-1) {
            lstCheckedModuleID.remove(removeIndex);
        }
    }

    /**
     * start setting methods
     */
    private void addSystemSetting(){
        int tax=0,charges=0;
        if(etShopName.getText().toString().length()==0){
            systemSetting.showMessage(SystemSetting.WARNING,"Enter Shop Name!",context,getLayoutInflater());
            etShopName.requestFocus();
            return;
        }
        int position=spCompany.getSelectedItemPosition();
        int companyid=lstCompanyData.get(position).getCompanyID();
        if(etCharges.getText().toString().length()!=0) charges=Integer.parseInt(etCharges.getText().toString());
        if(etTax.getText().toString().length()!=0) tax=Integer.parseInt(etTax.getText().toString());
        if(db.insertSystemSetting(etShopName.getText().toString(),companyid,tax,charges)){
            systemSetting.showMessage(SystemSetting.SUCCESS,"Success!",context,getLayoutInflater());
        }
    }

    private void addVoucherSetting(){
        if(etTitle.getText().toString().length()==0){
            systemSetting.showMessage(SystemSetting.WARNING,"Enter Title!",context,getLayoutInflater());
            etTitle.requestFocus();
            return;
        }

        if(db.insertVoucherSetting(etTitle.getText().toString(),etDescription.getText().toString(),etPhone.getText().toString(),etMessage.getText().toString(),etAddress.getText().toString(),etMessage2.getText().toString())){
            systemSetting.showMessage(SystemSetting.SUCCESS,"Success!",context,getLayoutInflater());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (resultCode == RESULT_OK) {
            if (requestCode == SHOP_LOGO) {
                Uri selectedImageUri = data.getData();
                try {
                    getBitmapFromUri(selectedImageUri);
                } catch (IOException e) {
                }
            } else if (requestCode == GALLERY) {
                if (data != null) {
                    Uri contentURI = data.getData();
                    try {
                        saveBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                        saveBitmap =getResizedBitmap(saveBitmap,300);
                        ivItemImage.setImageBitmap(saveBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }else if(requestCode == ADD_ITEM_SUB_REQUEST){
                lstIncludeItemSubGroup =(List<ItemSubGroupData>) data.getSerializableExtra("LstItemSubGroup");
                if(lstIncludeItemSubGroup.size() != 0 && btnAddItemSub != null) btnAddItemSub.setText("Include "+lstIncludeItemSubGroup.size()+" Item Sub Group");
                else btnAddItemSub.setText("Add Item Sub");
            }
        }
    }

    private void getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        saveShopLogoToWaiterOneDB(image);
        btnShopLogo.setImageBitmap(image);
    }

    private String saveShopLogoToWaiterOneDB(Bitmap bitmapImage){
        File directory = new File(Environment.getExternalStorageDirectory().getPath(), "/WaiterOneDB");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File logoPath=new File(directory,"shoplogo.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(logoPath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    private void loadShopLogoFromWaiterOneDB() {
        try {
            File directory = new File(Environment.getExternalStorageDirectory().getPath(), "/WaiterOneDB");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            File f=new File(directory, "shoplogo.jpg");
            if(f.exists()) {
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                btnShopLogo.setImageBitmap(b);
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    private void removeShopLogoFromWaiterOneDB(){
        try {
            File directory = new File(Environment.getExternalStorageDirectory().getPath(), "/WaiterOneDB");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            File f=new File(directory, "shoplogo.jpg");
            if(f.exists()) {
                if(f.delete()){
                    btnShopLogo.setImageResource(R.mipmap.shoplogo);
                    systemSetting.showMessage(SystemSetting.SUCCESS,"Logo Deleted!",context,getLayoutInflater());
                }
            }else{
                systemSetting.showMessage(SystemSetting.WARNING,"Not Exist Logo!",context,getLayoutInflater());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    /**
     * end setting methods
     */

    /**
     * start listview show methods
     */
    private void showSTypeList(){
        String sTypeName= etSearchSType.getText().toString();
        lstSTypeData =new ArrayList<>();
        Cursor cur=db.getSTypeByFilter(sTypeName);
        while(cur.moveToNext()){
            STypeData data=new STypeData();
            data.setsTypeID(cur.getInt(0));
            data.setsTypeName(cur.getString(1));
            lstSTypeData.add(data);
        }
        stSTypeListAdapter =new StSTypeListAdapter(this, lstSTypeData);
        lvSetup.setAdapter(stSTypeListAdapter);
        stSTypeListAdapter.setOnSetupEditDeleteButtonClickListener(this);
    }

    private void showTableTypeList(){
        String tableTypeName= etSearchTableType.getText().toString();
        lstTableTypeData=new ArrayList<>();
        Cursor cur=db.getTableTypeByFilter(tableTypeName);
        while(cur.moveToNext()){
            TableTypeData data=new TableTypeData();
            data.setTableTypeID(cur.getInt(0));
            data.setTableTypeName(cur.getString(1));
            lstTableTypeData.add(data);
        }
        tableTypeListAdapter=new StTableTypeListAdapter(this,lstTableTypeData);
        lvSetup.setAdapter(tableTypeListAdapter);
        tableTypeListAdapter.setOnSetupEditDeleteButtonClickListener(this);
    }

    private void showTableList(){
        int position= spSearchTableType.getSelectedItemPosition();
        int tableTypeID=lstTableTypeData.get(position).getTableTypeID();
        String tableName= etSearchTable.getText().toString();
        lstTableData=new ArrayList<>();
        Cursor cur=db.getTableByFilter(tableTypeID,tableName);
        while(cur.moveToNext()){
            TableData data=new TableData();
            data.setTableid(cur.getInt(0));
            data.setTableName(cur.getString(1));
            data.setTableTypeID(cur.getInt(2));
            data.setTableTypeName(cur.getString(3));
            lstTableData.add(data);
        }
        tableListAdapter=new StTableListAdapter(this,lstTableData);
        lvSetup.setAdapter(tableListAdapter);
        tableListAdapter.setOnSetupEditDeleteButtonClickListener(this);
    }

    private void showTasteList(){
        String tasteName= etSearchTaste.getText().toString();
        lstTasteData=new ArrayList<>();
        Cursor cur=db.getTasteByFilter(tasteName);
        while(cur.moveToNext()){
            TasteData data=new TasteData();
            data.setTasteid(cur.getInt(0));
            data.setTasteName(cur.getString(1));
            lstTasteData.add(data);
        }
        tasteListAdapter=new StTasteListAdapter(this,lstTasteData);
        lvSetup.setAdapter(tasteListAdapter);
        tasteListAdapter.setOnSetupEditDeleteButtonClickListener(this);
    }

    private void showWaiterList(){
        String waiterName= etSearchWaiterName.getText().toString();
        lstWaiterData=new ArrayList<>();
        Cursor cur=db.getWaiterByFilter(waiterName);
        while(cur.moveToNext()){
            WaiterData data=new WaiterData();
            data.setWaiterid(cur.getInt(0));
            data.setWaiterName(cur.getString(1));
            data.setPassword(cur.getString(2));
            lstWaiterData.add(data);
        }
        waiterListAdapter=new StWaiterListAdapter(this,lstWaiterData);
        lvSetup.setAdapter(waiterListAdapter);
        waiterListAdapter.setOnSetupEditDeleteButtonClickListener(this);
    }

    private void showMainMenuList(){
        String mainMenuName= etSearchMainMenu.getText().toString();
        lstMainMenuData=new ArrayList<>();
        Cursor cur=db.getMainMenuByFilter(mainMenuName);
        while(cur.moveToNext()){
            MainMenuData data=new MainMenuData();
            data.setMainMenuID(cur.getInt(0));
            data.setMainMenuName(cur.getString(1));
            data.setCounterid(cur.getInt(2));
            lstMainMenuData.add(data);
        }
        mainMenuListAdapter=new StMainMenuListAdapter(this,lstMainMenuData);
        lvSetup.setAdapter(mainMenuListAdapter);
        mainMenuListAdapter.setOnSetupEditDeleteButtonClickListener(this);
    }

    private void showSubMenuList(){
        int position= spSearchMainMenu.getSelectedItemPosition();
        int mainMenuID=lstMainMenuData.get(position).getMainMenuID();
        String subMenuName= etSearchSubMenu.getText().toString();
        lstSubMenuData=new ArrayList<>();
        Cursor cur=db.getSubMenuByFilter(mainMenuID,subMenuName);
        while(cur.moveToNext()){
            SubMenuData data=new SubMenuData();
            data.setSubMenuID(cur.getInt(0));
            data.setSubMenuName(cur.getString(1));
            data.setMainMenuID(cur.getInt(2));
            data.setSortCode(cur.getString(3));
            data.setMainMenuName(cur.getString(4));
            lstSubMenuData.add(data);
        }
        subMenuListAdapter=new StSubMenuListAdapter(this,lstSubMenuData);
        lvSetup.setAdapter(subMenuListAdapter);
        subMenuListAdapter.setOnSetupEditDeleteButtonClickListener(this);
    }

    private void showItemList(){
        int mainPosition= spSearchMainMenuForItem.getSelectedItemPosition();
        int mainMenuID=lstMainMenuData.get(mainPosition).getMainMenuID();
        int subPosition= spSearchSubMenu.getSelectedItemPosition();
        int subMenuID=lstSubMenuDataForItemSearch.get(subPosition).getSubMenuID();
        String itemid= etSearchItemID.getText().toString();
        String itemName= etSearchItemName.getText().toString();
        lstItemData=new ArrayList<>();
        Cursor cur=db.getItemByFilter(mainMenuID,subMenuID,itemid,itemName);
        while(cur.moveToNext()){
            ItemData data=new ItemData();
            data.setId(cur.getInt(0));
            data.setItemid(cur.getString(1));
            data.setItemName(cur.getString(2));
            data.setSubMenuID(cur.getInt(3));
            data.setPrice(cur.getDouble(4));
            data.setOutOfOrder(cur.getInt(5));
            data.setSubMenuName(cur.getString(6));
            data.setMainMenuID(cur.getInt(7));
            data.setMainMenuName(cur.getString(8));
            data.setsTypeID(cur.getInt(9));
            data.setItemImage(cur.getBlob(10));
            lstItemData.add(data);
        }
        itemListAdapter=new StItemListAdapter(this,lstItemData);
        lvSetup.setAdapter(itemListAdapter);
        itemListAdapter.setOnSetupEditDeleteButtonClickListener(this);
    }

    private void showItemSubGroupList(){
        lstItemSubGroupData=new ArrayList<>();
        Cursor cur=db.getItemSubGroup();
        while(cur.moveToNext()){
            ItemSubGroupData data=new ItemSubGroupData();
            data.setPkId(cur.getInt(0));
            data.setSubGroupName(cur.getString(1));
            data.setSubTitle(cur.getString(2));
            if(cur.getInt(3)==1){
                data.setSingleCheck(1);
                data.setCheckType(systemSetting.SINGLE_CHECK);
            }else{
                data.setSingleCheck(0);
                data.setCheckType(systemSetting.MULTI_CHECK);
            }
            lstItemSubGroupData.add(data);
        }
        itemSubGroupListAdapter=new StItemSubGroupListAdapter(this,lstItemSubGroupData);
        lvSetup.setAdapter(itemSubGroupListAdapter);
        itemSubGroupListAdapter.setOnSetupEditDeleteButtonClickListener(this);
    }

    private void showItemSubList(){
        lstItemSubData=new ArrayList<>();
        Cursor cur=db.getItemSub();
        while(cur.moveToNext()){
            ItemSubData data=new ItemSubData();
            data.setPkId(cur.getInt(0));
            data.setSubGroupId(cur.getInt(1));
            data.setSubName(cur.getString(2));
            data.setPrice(cur.getInt(3));
            data.setSubGroupName(cur.getString(4));
            lstItemSubData.add(data);
        }
        itemSubListAdapter=new StItemSubListAdapter(this,lstItemSubData);
        lvSetup.setAdapter(itemSubListAdapter);
        itemSubListAdapter.setOnSetupEditDeleteButtonClickListener(this);
    }

    private void showCompanyList(){
        String companyName= etSearchCompany.getText().toString();
        lstCompanyData=new ArrayList<>();
        Cursor cur=db.getCompanyByFilter(companyName);
        while(cur.moveToNext()){
            CompanyData data=new CompanyData();
            data.setCompanyID(cur.getInt(0));
            data.setCompanyName(cur.getString(1));
            lstCompanyData.add(data);
        }
        stCompanyListAdapter =new StCompanyListAdapter(this,lstCompanyData);
        lvSetup.setAdapter(stCompanyListAdapter);
        stCompanyListAdapter.setOnSetupEditDeleteButtonClickListener(this);
    }
    /**
     * end listview show methods
     */

    /**
     * start spinner bind methods
     */
    private void bindTableType(Spinner spTableType){
        lstTableTypeData=new ArrayList<>();
        Cursor cur=db.getTableType();
        while(cur.moveToNext()){
            TableTypeData data=new TableTypeData();
            data.setTableTypeID(cur.getInt(0));
            data.setTableTypeName(cur.getString(1));
            lstTableTypeData.add(data);
        }
        if(spTableType.equals(spSearchTableType)){
            TableTypeData data=new TableTypeData();
            data.setTableTypeID(0);
            data.setTableTypeName("Table Type");
            lstTableTypeData.add(0,data);
            spTableTypeAdapter = new SpTableTypeAdapter(this, lstTableTypeData);
            spTableType.setAdapter(spTableTypeAdapter);
        }else {
            if (lstTableTypeData.size() != 0) {
                spTableTypeAdapter = new SpTableTypeAdapter(this, lstTableTypeData);
                spTableType.setAdapter(spTableTypeAdapter);
            } else {
                TableTypeData data = new TableTypeData();
                data.setTableTypeID(0);
                data.setTableTypeName("No Table Type");
                List<TableTypeData> lst = new ArrayList<>();
                lst.add(data);
                spTableTypeAdapter = new SpTableTypeAdapter(this, lst);
                spTableType.setAdapter(spTableTypeAdapter);
            }
        }
    }

    private void bindSType(Spinner spSType){
        lstSTypeData =new ArrayList<>();
        Cursor cur=db.getSType();
        while(cur.moveToNext()){
            STypeData data=new STypeData();
            data.setsTypeID(cur.getInt(0));
            data.setsTypeName(cur.getString(1));
            lstSTypeData.add(data);
        }
        if(lstSTypeData.size()!=0) {
            spSTypeAdapter = new SpSTypeAdapter(this, lstSTypeData);
            spSType.setAdapter(spSTypeAdapter);
        }
    }

    private void bindItemSubGroup(Spinner spItemSubGroup){
        lstItemSubGroupData =new ArrayList<>();
        Cursor cur=db.getItemSubGroup();
        while(cur.moveToNext()){
            ItemSubGroupData data=new ItemSubGroupData();
            data.setPkId(cur.getInt(0));
            data.setSubGroupName(cur.getString(1));
            lstItemSubGroupData.add(data);
        }
        if(lstItemSubGroupData.size()!=0) {
            spItemSubGroupAdapter = new SpItemSubGroupAdapter(this, lstItemSubGroupData);
            spItemSubGroup.setAdapter(spItemSubGroupAdapter);
        }
    }

    private void bindMainMenu(Spinner spMainMenu){
        lstMainMenuData=new ArrayList<>();
        Cursor cur=db.getMainMenu();
        while(cur.moveToNext()){
            MainMenuData data=new MainMenuData();
            data.setMainMenuID(cur.getInt(0));
            data.setMainMenuName(cur.getString(1));
            data.setCounterid(cur.getInt(2));
            lstMainMenuData.add(data);
        }
        if(spMainMenu.equals(spSearchMainMenu)||spMainMenu.equals(spSearchMainMenuForItem)){
            MainMenuData data = new MainMenuData();
            data.setMainMenuID(0);
            data.setMainMenuName("Main Menu");
            lstMainMenuData.add(0,data);
            spMainMenuAdapter = new SpMainMenuAdapter(this, lstMainMenuData);
            spMainMenu.setAdapter(spMainMenuAdapter);
        }else {
            if (lstMainMenuData.size() != 0) {
                spMainMenuAdapter = new SpMainMenuAdapter(this, lstMainMenuData);
                spMainMenu.setAdapter(spMainMenuAdapter);
            } else {
                MainMenuData data = new MainMenuData();
                data.setMainMenuID(0);
                data.setMainMenuName("No Main Menu");
                List<MainMenuData> lst = new ArrayList<>();
                lst.add(data);
                spMainMenuAdapter = new SpMainMenuAdapter(this, lst);
                spMainMenu.setAdapter(spMainMenuAdapter);
            }
        }
    }

    private void bindSubMenuForItemEntry(Spinner spSubMenu) {
        lstSubMenuData = new ArrayList<>();
        Cursor cur = db.getSubMenuByMainMenu(selectedMainMenuID);
        while (cur.moveToNext()) {
            SubMenuData data = new SubMenuData();
            data.setSubMenuID(cur.getInt(0));
            data.setSubMenuName(cur.getString(1));
            data.setMainMenuID(cur.getInt(2));
            data.setSortCode(cur.getString(3));
            data.setMainMenuName(cur.getString(4));
            lstSubMenuData.add(data);
        }
        if (lstSubMenuData.size() != 0) {
            spSubMenuAdapter = new SpSubMenuAdapter(this, lstSubMenuData);
            spSubMenu.setAdapter(spSubMenuAdapter);
        } else {
            SubMenuData data = new SubMenuData();
            data.setSubMenuID(0);
            data.setSubMenuName("No Sub Menu");
            List<SubMenuData> lst = new ArrayList<>();
            lst.add(data);
            spSubMenuAdapter = new SpSubMenuAdapter(this, lst);
            spSubMenu.setAdapter(spSubMenuAdapter);
        }
    }

    private void bindSubMenuForItemSearch(Spinner spSubMenu){
        lstSubMenuDataForItemSearch=new ArrayList<>();
        Cursor cur;
        if(selectedMainMenuID!=0) cur = db.getSubMenuByMainMenu(selectedMainMenuID);
        else cur=db.getSubMenu();

        while(cur.moveToNext()){
            SubMenuData data=new SubMenuData();
            data.setSubMenuID(cur.getInt(0));
            data.setSubMenuName(cur.getString(1));
            data.setMainMenuID(cur.getInt(2));
            data.setSortCode(cur.getString(3));
            data.setMainMenuName(cur.getString(4));
            lstSubMenuDataForItemSearch.add(data);
        }
        if(spSubMenu.equals(spSearchSubMenu)){
            SubMenuData data = new SubMenuData();
            data.setSubMenuID(0);
            data.setSubMenuName("Sub Menu");
            lstSubMenuDataForItemSearch.add(0,data);
            spSubMenuAdapter = new SpSubMenuAdapter(this, lstSubMenuDataForItemSearch);
            spSubMenu.setAdapter(spSubMenuAdapter);
        }
    }

    private void bindCompany(){
        lstCompanyData=new ArrayList<>();
        CompanyData emptydata = new CompanyData();
        emptydata.setCompanyID(0);
        emptydata.setCompanyName("Choose Company");
        lstCompanyData.add(emptydata);
        Cursor cur=db.getCompany();
        while(cur.moveToNext()){
            CompanyData data=new CompanyData();
            data.setCompanyID(cur.getInt(0));
            data.setCompanyName(cur.getString(1));
            lstCompanyData.add(data);
        }
        spCompanyAdapter = new SpCompanyAdapter(this, lstCompanyData);
        spCompany.setAdapter(spCompanyAdapter);
    }

    /**
     * end spinner bind methods
     */

    /**
     * start common methods
     */
    private void showConfirmDialog(final int position){
        LayoutInflater reg=LayoutInflater.from(context);
        View passwordView=reg.inflate(R.layout.dg_confirm, null);
        android.app.AlertDialog.Builder passwordDialog=new android.app.AlertDialog.Builder(context);
        passwordDialog.setView(passwordView);

        tvConfirmMessage=(TextView)passwordView.findViewById(R.id.tvConfirmMessage);
        final Button btnCancel=(Button)passwordView.findViewById(R.id.btnCancel);
        final Button btnOK=(Button)passwordView.findViewById(R.id.btnOK);

        tvConfirmMessage.setText(confirmMessage);
        passwordDialog.setCancelable(true);
        final android.app.AlertDialog passwordRequireDialog=passwordDialog.create();
        passwordRequireDialog.show();

        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                passwordRequireDialog.dismiss();
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(setupModuleName.equals(btnWaiterModule.getText().toString())){
                    deleteid=lstWaiterData.get(position).getWaiterid();
                    if(db.deleteWaiter(deleteid)){
                        systemSetting.showMessage(SystemSetting.SUCCESS,"Delete Successful!",context,getLayoutInflater());
                        showWaiterList();
                    }
                }else if(setupModuleName.equals(btnTableTypeModule.getText().toString())){
                    deleteid=lstTableTypeData.get(position).getTableTypeID();
                    if(db.deleteTableType(deleteid)){
                        systemSetting.showMessage(SystemSetting.SUCCESS,"Delete Successful!",context,getLayoutInflater());
                        showTableTypeList();
                    }else{
                        systemSetting.showMessage(SystemSetting.INFO,"Cannot delete "+lstTableTypeData.get(position).getTableTypeName()+" because the table type is using in table!",context,getLayoutInflater());
                    }
                }
                else if(setupModuleName.equals(btnSTypeModule.getText().toString())){
                    deleteid= lstSTypeData.get(position).getsTypeID();
                    if(db.deleteSType(deleteid)){
                        systemSetting.showMessage(SystemSetting.SUCCESS,"Delete Successful!",context,getLayoutInflater());
                        showSTypeList();
                    }else{
                        systemSetting.showMessage(SystemSetting.INFO,"Cannot delete "+ lstSTypeData.get(position).getsTypeName()+" because the stype is using in item!",context,getLayoutInflater());
                    }
                }
                else if(setupModuleName.equals(btnTableModule.getText().toString())){
                    deleteid=lstTableData.get(position).getTableid();
                    if(db.deleteTable(deleteid)){
                        systemSetting.showMessage(SystemSetting.SUCCESS,"Delete Successful!",context,getLayoutInflater());
                        showTableList();
                    }
                }
                else if(setupModuleName.equals(btnMainMenuModule.getText().toString())){
                    deleteid=lstMainMenuData.get(position).getMainMenuID();
                    if(db.deleteMainMenu(deleteid)){
                        systemSetting.showMessage(SystemSetting.SUCCESS,"Delete Successful!",context,getLayoutInflater());
                        showMainMenuList();
                    }else{
                        systemSetting.showMessage(SystemSetting.INFO,"Cannot delete "+lstMainMenuData.get(position).getMainMenuName()+" because the main menu is using in sub menu!",context,getLayoutInflater());
                    }
                }
                else if(setupModuleName.equals(btnSubMenuModule.getText().toString())){
                    deleteid=lstSubMenuData.get(position).getSubMenuID();
                    if(db.deleteSubMenu(deleteid)){
                        systemSetting.showMessage(SystemSetting.SUCCESS,"Delete Successful!",context,getLayoutInflater());
                        showSubMenuList();
                    }else{
                        systemSetting.showMessage(SystemSetting.INFO,"Cannot delete "+lstSubMenuData.get(position).getSubMenuName()+" because the sub menu is using in item!",context,getLayoutInflater());
                    }
                }
                else if(setupModuleName.equals(btnItemModule.getText().toString())){
                    deleteid=lstItemData.get(position).getId();
                    String itemId=lstItemData.get(position).getItemid();
                    if(db.deleteItem(deleteid,itemId)){
                        systemSetting.showMessage(SystemSetting.SUCCESS,"Delete Successful!",context,getLayoutInflater());
                        showItemList();
                    }
                }
                else if(setupModuleName.equals(btnItemSubGroupModule.getText().toString())){
                    deleteid=lstItemSubGroupData.get(position).getPkId();
                    if(db.deleteItemSubGroup(deleteid)){
                        systemSetting.showMessage(SystemSetting.SUCCESS,"Delete Successful!",context,getLayoutInflater());
                        showItemSubGroupList();
                    }
                }
                else if(setupModuleName.equals(btnItemSubModule.getText().toString())){
                    deleteid=lstItemSubData.get(position).getPkId();
                    if(db.deleteItemSub(deleteid)){
                        systemSetting.showMessage(SystemSetting.SUCCESS,"Delete Successful!",context,getLayoutInflater());
                        showItemSubList();
                    }
                }
                else if(setupModuleName.equals(btnTasteModule.getText().toString())){
                    deleteid=lstTasteData.get(position).getTasteid();
                    if(db.deleteTaste(deleteid)){
                        systemSetting.showMessage(SystemSetting.SUCCESS,"Delete Successful!",context,getLayoutInflater());
                        showTasteList();
                    }
                }
                else if(setupModuleName.equals(btnCompanyModule.getText().toString())){
                    deleteid=lstCompanyData.get(position).getCompanyID();
                    if(db.deleteCompany(deleteid)){
                        systemSetting.showMessage(SystemSetting.SUCCESS,"Delete Successful!",context,getLayoutInflater());
                        showCompanyList();
                    }
                }
                else{
                    removeShopLogoFromWaiterOneDB();
                }
                passwordRequireDialog.dismiss();
            }
        });
    }

    private void setLayoutResource(){
        layoutSetupWaiter=(LinearLayout) findViewById(R.id.layoutSetupWaiter);
        layoutSetupTable=(LinearLayout)findViewById(R.id.layoutSetupTable);
        layoutSetupTableType=(LinearLayout)findViewById(R.id.layoutSetupTableType);
        layoutSetupTaste=(LinearLayout)findViewById(R.id.layoutSetupTaste);
        layoutSetupMainMenu=(LinearLayout)findViewById(R.id.layoutSetupMainMenu);
        layoutSetupSubMenu=(LinearLayout)findViewById(R.id.layoutSetupSubMenu);
        layoutSetupItem=(LinearLayout)findViewById(R.id.layoutSetupItem);
        layoutSetupSystemSetting=(LinearLayout)findViewById(R.id.layoutSetupSystemSetting);
        layoutSetupVoucherSetting=(LinearLayout)findViewById(R.id.layoutSetupVoucherSetting);
        layoutSetupSType=(LinearLayout)findViewById(R.id.layoutSetupSType);
        layoutSetupCompany=(LinearLayout)findViewById(R.id.layoutSetupCompany);
        layoutSetupItemSubGroup=(LinearLayout)findViewById(R.id.layoutSetupItemSubGroup);
        layoutSetupItemSub=(LinearLayout)findViewById(R.id.layoutSetupItemSub);

        spSearchTableType =(Spinner) findViewById(R.id.spSearchTableType);
        spSearchMainMenu =(Spinner)findViewById(R.id.spSearchMainMenu);
        spSearchSubMenu =(Spinner)findViewById(R.id.spSearchSubMenu);
        spSearchMainMenuForItem =(Spinner)findViewById(R.id.spSearchMainMenuForItem);
        spCompany =(Spinner)findViewById(R.id.spCompany);
        tvCharges=(TextView)findViewById(R.id.tvCharges);
        tvTax=(TextView)findViewById(R.id.tvTax);
        tvShopName=(TextView)findViewById(R.id.tvShopName);
        tvCompany=(TextView)findViewById(R.id.tvCompany);
        lvSetup=(ListView)findViewById(R.id.lvSetup);
        tvHeaderItemID=(TextView)findViewById(R.id.tvHeaderItemID);
        tvHeaderItemName=(TextView)findViewById(R.id.tvHeaderItemName);
        tvHeaderSubMenuName=(TextView)findViewById(R.id.tvHeaderSubMenuName);
        tvHeaderPrice=(TextView)findViewById(R.id.tvHeaderPrice);
        tvHeaderOutOfOrder=(TextView)findViewById(R.id.tvHeaderOutOfOrder);
        tvHeaderSubMenu=(TextView)findViewById(R.id.tvHeaderSubMenu);
        tvHeaderMainMenuName=(TextView)findViewById(R.id.tvHeaderMainMenuName);
        tvHeaderSortCode=(TextView)findViewById(R.id.tvHeaderSortCode);
        tvLogo=(TextView)findViewById(R.id.tvLogo);
        tvTitle=(TextView)findViewById(R.id.tvTitle);
        tvDescription=(TextView)findViewById(R.id.tvDescription);
        tvPhone=(TextView)findViewById(R.id.tvPhone);
        tvAddress=(TextView)findViewById(R.id.tvAddress);
        tvMessage=(TextView)findViewById(R.id.tvMessage);
        tvMessage2=(TextView)findViewById(R.id.tvMessage2);

        etSearchWaiterName =(EditText)findViewById(R.id.etSearchWaiterName);
        etSearchTableType =(EditText)findViewById(R.id.etSearchTableType);
        etSearchTable =(EditText)findViewById(R.id.etSearchTable);
        etSearchTaste =(EditText)findViewById(R.id.etSearchTaste);
        etSearchMainMenu =(EditText)findViewById(R.id.etSearchMainMenu);
        etSearchSubMenu =(EditText)findViewById(R.id.etSearchSubMenu);
        etSearchItemID =(EditText)findViewById(R.id.etSearchItemID);
        etSearchItemName =(EditText)findViewById(R.id.etSearchItemName);
        etShopName=(EditText)findViewById(R.id.etShopName);
        etCharges=(EditText)findViewById(R.id.etCharges);
        etTax=(EditText)findViewById(R.id.etTax);
        etTitle=(EditText)findViewById(R.id.etTitle);
        etDescription=(EditText)findViewById(R.id.etDescription);
        etPhone=(EditText)findViewById(R.id.etPhone);
        etMessage=(EditText)findViewById(R.id.etMessage);
        etMessage2=(EditText)findViewById(R.id.etMessage2);
        etAddress=(EditText)findViewById(R.id.etAddress);
        etSearchSType =(EditText)findViewById(R.id.etSearchSType);
        etSearchCompany =(EditText)findViewById(R.id.etSearchCompany);

        btnWaiterModule =(Button)findViewById(R.id.btnWaiterModule);
        btnTableModule =(Button)findViewById(R.id.btnTableModule);
        btnTableTypeModule =(Button)findViewById(R.id.btnTableTypeModule);
        btnTasteModule =(Button)findViewById(R.id.btnTasteModule);
        btnMainMenuModule =(Button)findViewById(R.id.btnMainMenuModule);
        btnSubMenuModule =(Button)findViewById(R.id.btnSubMenuModule);
        btnItemModule =(Button)findViewById(R.id.btnItemModule);
        btnSTypeModule =(Button)findViewById(R.id.btnSTypeModule);
        btnItemSubGroupModule =(Button)findViewById(R.id.btnItemSubGroupModule);
        btnItemSubModule =(Button)findViewById(R.id.btnItemSubModule);
        btnCompanyModule =(Button)findViewById(R.id.btnCompanyModule);
        btnSystemSettingModule =(Button)findViewById(R.id.btnSystemSettingModule);
        btnVoucherSettingModule =(Button)findViewById(R.id.btnVoucherSettingModule);
        btnNewAddWaiter =(Button)findViewById(R.id.btnNewAddWaiter);
        btnNewAddTable =(Button)findViewById(R.id.btnAddNewTable);
        btnNewAddSType =(Button)findViewById(R.id.btnAddNewSType);
        btnNewAddTableType =(Button)findViewById(R.id.btnAddNewTableType);
        btnNewAddTaste =(Button)findViewById(R.id.btnAddNewTaste);
        btnNewAddMainMenu =(Button)findViewById(R.id.btnAddNewMainMenu);
        btnNewAddSubMenu =(Button)findViewById(R.id.btnAddNewSubMenu);
        btnNewAddItem =(Button)findViewById(R.id.btnAddNewItem);
        btnNewAddItemSubGroup =(Button)findViewById(R.id.btnAddNewItemSubGroup);
        btnNewAddItemSub =(Button)findViewById(R.id.btnAddNewItemSub);
        btnNewAddCompany =(Button)findViewById(R.id.btnAddNewCompany);
        btnAddSystemSetting=(Button)findViewById(R.id.btnAddSystemSetting);
        btnAddVoucherSetting=(Button)findViewById(R.id.btnAddVoucherSetting);
        btnSearchWaiter=(Button)findViewById(R.id.btnSearchWaiter);
        btnSearchTableType=(Button)findViewById(R.id.btnSearchTableType);
        btnSearchTable=(Button)findViewById(R.id.btnSearchTable);
        btnSearchTaste=(Button)findViewById(R.id.btnSearchTaste);
        btnSearchMainMenu=(Button)findViewById(R.id.btnSearchMainMenu);
        btnSearchSubMenu=(Button)findViewById(R.id.btnSearchSubMenu);
        btnSearchItem=(Button)findViewById(R.id.btnSearchItem);
        btnSearchSType=(Button)findViewById(R.id.btnSearchSType);
        btnSearchCompany=(Button)findViewById(R.id.btnSearchCompany);
        btnShopLogo=(ImageButton)findViewById(R.id.btnShopLogo);
        btnCancelLogo=(ImageButton)findViewById(R.id.btnCancelLogo);
    }
    /**
     * end common methods
     */

    /**
     * start clear methods
     */
    private void clearAllLayout(Button btn){
        layoutSetupWaiter.setVisibility(View.GONE);
        layoutSetupTable.setVisibility(View.GONE);
        layoutSetupTableType.setVisibility(View.GONE);
        layoutSetupTaste.setVisibility(View.GONE);
        layoutSetupMainMenu.setVisibility(View.GONE);
        layoutSetupSubMenu.setVisibility(View.GONE);
        layoutSetupItem.setVisibility(View.GONE);
        layoutSetupItemSubGroup.setVisibility(View.GONE);
        layoutSetupItemSub.setVisibility(View.GONE);
        layoutSetupSystemSetting.setVisibility(View.GONE);
        layoutSetupVoucherSetting.setVisibility(View.GONE);
        layoutSetupSType.setVisibility(View.GONE);
        layoutSetupCompany.setVisibility(View.GONE);
        lvSetup.setVisibility(View.VISIBLE);
        btnWaiterModule.setBackground(getResources().getDrawable(R.drawable.bg_gray_5r));
        btnTableTypeModule.setBackground(getResources().getDrawable(R.drawable.bg_gray_5r));
        btnTableModule.setBackground(getResources().getDrawable(R.drawable.bg_gray_5r));
        btnMainMenuModule.setBackground(getResources().getDrawable(R.drawable.bg_gray_5r));
        btnSubMenuModule.setBackground(getResources().getDrawable(R.drawable.bg_gray_5r));
        btnItemModule.setBackground(getResources().getDrawable(R.drawable.bg_gray_5r));
        btnItemSubGroupModule.setBackground(getResources().getDrawable(R.drawable.bg_gray_5r));
        btnItemSubModule.setBackground(getResources().getDrawable(R.drawable.bg_gray_5r));
        btnTasteModule.setBackground(getResources().getDrawable(R.drawable.bg_gray_5r));
        btnSystemSettingModule.setBackground(getResources().getDrawable(R.drawable.bg_gray_5r));
        btnVoucherSettingModule.setBackground(getResources().getDrawable(R.drawable.bg_gray_5r));
        btnSTypeModule.setBackground(getResources().getDrawable(R.drawable.bg_gray_5r));
        btnCompanyModule.setBackground(getResources().getDrawable(R.drawable.bg_gray_5r));
        btn.setBackground(getResources().getDrawable(R.drawable.bg_accent_5r));
    }

    private void clearWaiterControls(){
        etSearchWaiterName.setText("");
    }

    private void clearTableTypeControls(){
        etSearchTableType.setText("");
    }

    private void clearSTypeControls(){
        etSearchSType.setText("");
    }

    private void clearTableControls(){
        etSearchTable.setText("");
        spSearchTableType.setSelection(0);
    }

    private void clearTasteControls(){
        etSearchTaste.setText("");
    }

    private void clearMainMenuControls(){
        etSearchMainMenu.setText("");
    }

    private void clearSubMenuControls(){
        etSearchSubMenu.setText("");
        spSearchMainMenu.setSelection(0);
    }

    private void clearItemControls(){
        etSearchItemID.setText("");
        etSearchItemName.setText("");
        spSearchMainMenuForItem.setSelection(0);
        spSearchSubMenu.setSelection(0);
    }

    private void clearCompanyControls(){
        etSearchCompany.setText("");
    }
    /**
     * end clear methods
     */

    /**
     * start setup dialog methods
     */
    private void showUserDialog(String name,String password,final boolean isEdit){
        LayoutInflater reg=LayoutInflater.from(context);
        View view=reg.inflate(R.layout.dg_st_user, null);
        android.app.AlertDialog.Builder dialog=new android.app.AlertDialog.Builder(context);
        dialog.setView(view);

        final TextView tvLabelAllowRight=(TextView)view.findViewById(R.id.tvLabelAllowRight);
        final EditText etUserName=(EditText)view.findViewById(R.id.etUserName);
        final EditText etPassword=(EditText)view.findViewById(R.id.etPassword);
        final Button btnClose=(Button)view.findViewById(R.id.btnClose);
        final Button btnSave=(Button)view.findViewById(R.id.btnSave);
        final ListView lvModule=(ListView) view.findViewById(R.id.lvModule);

        getAllModule(lvModule);

        dialog.setCancelable(false);
        final android.app.AlertDialog setupDialog=dialog.create();
        setupDialog.show();

        if(isEdit){
            lstCheckedModuleID=new ArrayList<>();
            etUserName.setText(name);
            etPassword.setText(password);
            Cursor cur=db.getModuleByUserID(editid);
            while(cur.moveToNext()){
                lstCheckedModuleID.add(cur.getInt(0));
            }

            for(int i=0;i<lstModuleData.size();i++){
                if(lstCheckedModuleID.contains(lstModuleData.get(i).getModuleID())){
                    lstModuleData.get(i).setSelected(true);
                }
            }
            moduleListAdapter=new ModuleListAdapter(this,lstModuleData);
            lvModule.setAdapter(moduleListAdapter);
            moduleListAdapter.setModuleCheckedListener(this);
        }

        btnClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                showWaiterList();
                lstCheckedModuleID=new ArrayList<>();
                setupDialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(etUserName.getText().toString().length()==0){
                    systemSetting.showMessage(SystemSetting.WARNING,"Enter Name!",context,getLayoutInflater());
                    etUserName.requestFocus();
                    return;
                }
                if(etPassword.getText().toString().length()==0){
                    systemSetting.showMessage(SystemSetting.WARNING,"Enter Password!",context,getLayoutInflater());
                    etPassword.requestFocus();
                    return;
                }
                if (lstCheckedModuleID.size() == 0) {
                    systemSetting.showMessage(SystemSetting.WARNING,"Choose Module!",context,getLayoutInflater());
                    return;
                }

                if(!isEdit) {
                    if (db.insertWaiter(etUserName.getText().toString(), etPassword.getText().toString())) {
                        int waiterid=0;
                        Cursor cur=db.getMaxWaiterID();
                        if(cur.moveToFirst())waiterid=cur.getInt(0);
                        for(int i=0;i<lstCheckedModuleID.size();i++){
                            db.insertUserRight(waiterid,lstCheckedModuleID.get(i));
                        }
                        systemSetting.showMessage(SystemSetting.SUCCESS,"Success!",context,getLayoutInflater());
                        etUserName.setText("");
                        etPassword.setText("");
                        lstCheckedModuleID=new ArrayList<>();
                        getAllModule(lvModule);
                    }
                }else{
                    if (db.updateWaiter(editid,etUserName.getText().toString(), etPassword.getText().toString())) {
                        db.deleteUserRight(editid);
                        for(int i=0;i<lstCheckedModuleID.size();i++){
                            db.insertUserRight(editid,lstCheckedModuleID.get(i));
                        }
                        systemSetting.showMessage(SystemSetting.SUCCESS,"Success!",context,getLayoutInflater());
                        etUserName.setText("");
                        etPassword.setText("");
                        showWaiterList();
                        lstCheckedModuleID=new ArrayList<>();
                        setupDialog.dismiss();
                    }
                }
            }
        });
    }

    /**private void showWaiterDialog(String name,String password,final boolean edit){
        LayoutInflater reg=LayoutInflater.from(context);
        View view=reg.inflate(R.layout.dialog_setup_waiter, null);
        android.app.AlertDialog.Builder dialog=new android.app.AlertDialog.Builder(context);
        dialog.setView(view);

        final EditText etWaiterName=(EditText)view.findViewById(R.id.etWaiterName);
        final EditText etPassword=(EditText)view.findViewById(R.id.etPassword);
        final Button btnClose=(Button)view.findViewById(R.id.btnClose);
        final Button btnSave=(Button)view.findViewById(R.id.btnSave);

        etWaiterName.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        etPassword.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnSave.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/CAMBRIAB.TTF"));
        btnClose.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/CAMBRIAB.TTF"));

        dialog.setCancelable(false);
        final android.app.AlertDialog setupDialog=dialog.create();
        setupDialog.show();

        if(edit){
            etWaiterName.setText(name);
            etPassword.setText(password);
        }

        btnClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                showWaiterList();
                setupDialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(etWaiterName.getText().toString().length()==0){
                    showMessage(warning_message,"Enter Name!");
                    etWaiterName.requestFocus();
                    return;
                }
                if(etPassword.getText().toString().length()==0){
                    showMessage(warning_message,"Enter Password!");
                    etPassword.requestFocus();
                    return;
                }
                if(!edit) {
                    if (db.insertWaiter(etWaiterName.getText().toString(), etPassword.getText().toString())) {
                        showMessage(success_message, "Success!");
                        etWaiterName.setText("");
                        etPassword.setText("");
                    }
                }else{
                    if (db.updateWaiter(editid,etWaiterName.getText().toString(), etPassword.getText().toString())) {
                        showMessage(success_message, "Success!");
                        etWaiterName.setText("");
                        etPassword.setText("");
                        showWaiterList();
                        setupDialog.dismiss();
                    }
                }
            }
        });
    }**/

    private void showTableTypeDialog(String name,final boolean edit){
        LayoutInflater reg=LayoutInflater.from(context);
        View view=reg.inflate(R.layout.dg_st_table_type, null);
        android.app.AlertDialog.Builder dialog=new android.app.AlertDialog.Builder(context);
        dialog.setView(view);

        final EditText etTableTypeName=(EditText)view.findViewById(R.id.etTableTypeName);
        final Button btnClose=(Button)view.findViewById(R.id.btnClose);
        final Button btnSave=(Button)view.findViewById(R.id.btnSave);

        dialog.setCancelable(false);
        final android.app.AlertDialog setupDialog=dialog.create();
        setupDialog.show();

        if(edit){
            etTableTypeName.setText(name);
        }

        btnClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                showTableTypeList();
                setupDialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(etTableTypeName.getText().toString().length()==0){
                    systemSetting.showMessage(SystemSetting.WARNING,"Enter Name!",context,getLayoutInflater());
                    etTableTypeName.requestFocus();
                    return;
                }
                if(!edit) {
                    if (db.insertTableType(etTableTypeName.getText().toString())) {
                        systemSetting.showMessage(SystemSetting.SUCCESS,"Success!",context,getLayoutInflater());
                        etTableTypeName.setText("");
                    }
                }else{
                    if (db.updateTableType(editid,etTableTypeName.getText().toString())) {
                        systemSetting.showMessage(SystemSetting.SUCCESS,"Success!",context,getLayoutInflater());
                        etTableTypeName.setText("");
                        showTableTypeList();
                        setupDialog.dismiss();
                    }
                }
            }
        });
    }

    private void showSTypeDialog(String name,final boolean edit){
        LayoutInflater reg=LayoutInflater.from(context);
        View view=reg.inflate(R.layout.dg_st_stype, null);
        android.app.AlertDialog.Builder dialog=new android.app.AlertDialog.Builder(context);
        dialog.setView(view);

        final EditText etSTypeName=(EditText)view.findViewById(R.id.etSTypeName);
        final Button btnClose=(Button)view.findViewById(R.id.btnClose);
        final Button btnSave=(Button)view.findViewById(R.id.btnSave);

        dialog.setCancelable(false);
        final android.app.AlertDialog setupDialog=dialog.create();
        setupDialog.show();

        if(edit){
            etSTypeName.setText(name);
        }

        btnClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                showSTypeList();
                setupDialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(etSTypeName.getText().toString().length()==0){
                    systemSetting.showMessage(SystemSetting.WARNING,"Enter Name!",context,getLayoutInflater());
                    etSTypeName.requestFocus();
                    return;
                }
                if(!edit) {
                    if (db.insertSType(etSTypeName.getText().toString())) {
                        systemSetting.showMessage(SystemSetting.SUCCESS,"Success!",context,getLayoutInflater());
                        etSTypeName.setText("");
                    }
                }else{
                    if (db.updateSType(editid,etSTypeName.getText().toString())) {
                        systemSetting.showMessage(SystemSetting.SUCCESS,"Success!",context,getLayoutInflater());
                        etSTypeName.setText("");
                        showSTypeList();
                        setupDialog.dismiss();
                    }
                }
            }
        });
    }

    private void showTableDialog(int tableTypePosition,String tableName,final boolean edit){
        LayoutInflater reg=LayoutInflater.from(context);
        View view=reg.inflate(R.layout.dg_st_table, null);
        android.app.AlertDialog.Builder dialog=new android.app.AlertDialog.Builder(context);
        dialog.setView(view);

        final Spinner spTableType=(Spinner) view.findViewById(R.id.spTableType);
        final EditText etTable=(EditText)view.findViewById(R.id.etTable);
        final Button btnClose=(Button)view.findViewById(R.id.btnClose);
        final Button btnSave=(Button)view.findViewById(R.id.btnSave);

        dialog.setCancelable(false);
        final android.app.AlertDialog setupDialog=dialog.create();
        setupDialog.show();

        bindTableType(spTableType);

        if(edit){
            spTableType.setSelection(tableTypePosition);
            etTable.setText(tableName);
        }

        btnClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                TableTypeData data=new TableTypeData();
                data.setTableTypeID(0);
                data.setTableTypeName("Table Type");
                lstTableTypeData.add(0,data);
                showTableList();
                setupDialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(lstTableTypeData.size()==0){
                    systemSetting.showMessage(SystemSetting.WARNING,"Fill Table Type First!",context,getLayoutInflater());
                    return;
                }
                int position= spTableType.getSelectedItemPosition();
                int tableTypeID=lstTableTypeData.get(position).getTableTypeID();
                if(etTable.getText().toString().length()==0){
                    systemSetting.showMessage(SystemSetting.WARNING,"Enter Name!",context,getLayoutInflater());
                    etTable.requestFocus();
                    return;
                }
                if(!edit) {
                    if (db.insertTable(etTable.getText().toString(),tableTypeID)) {
                        systemSetting.showMessage(SystemSetting.SUCCESS,"Success!",context,getLayoutInflater());
                        etTable.setText("");
                    } else {
                        systemSetting.showMessage(SystemSetting.INFO,"Table Name " + etTable.getText().toString() + " has already existed!",context,getLayoutInflater());
                        etTable.requestFocus();
                    }
                }else{
                    if (db.updateTable(editid,etTable.getText().toString(),tableTypeID)) {
                        TableTypeData data=new TableTypeData();
                        data.setTableTypeID(0);
                        data.setTableTypeName("Table Type");
                        lstTableTypeData.add(0,data);
                        systemSetting.showMessage(SystemSetting.SUCCESS,"Success!",context,getLayoutInflater());
                        etTable.setText("");
                        showTableList();
                        setupDialog.dismiss();
                    }else {
                        systemSetting.showMessage(SystemSetting.INFO,"Table Name " + etTable.getText().toString() + " has already existed!",context,getLayoutInflater());
                        etTable.requestFocus();
                    }
                }
            }
        });
    }

    private void showMainMenuDialog(String name,int counterid,final boolean edit){
        LayoutInflater reg=LayoutInflater.from(context);
        View view=reg.inflate(R.layout.dg_st_main_menu, null);
        android.app.AlertDialog.Builder dialog=new android.app.AlertDialog.Builder(context);
        dialog.setView(view);

        final EditText etMainMenu=(EditText)view.findViewById(R.id.etMainMenu);
        final EditText etCounterID=(EditText)view.findViewById(R.id.etCounterID);
        final Button btnClose=(Button)view.findViewById(R.id.btnClose);
        final Button btnSave=(Button)view.findViewById(R.id.btnSave);

        dialog.setCancelable(false);
        final android.app.AlertDialog setupDialog=dialog.create();
        setupDialog.show();

        if(edit){
            etMainMenu.setText(name);
            etCounterID.setText(String.valueOf(counterid));
        }

        btnClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                showMainMenuList();
                setupDialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(etMainMenu.getText().toString().length()==0){
                    systemSetting.showMessage(SystemSetting.WARNING,"Enter Name!",context,getLayoutInflater());
                    etMainMenu.requestFocus();
                    return;
                }
                if(etCounterID.getText().toString().length()==0){
                    systemSetting.showMessage(SystemSetting.WARNING,"Enter Counter ID!",context,getLayoutInflater());
                    etCounterID.requestFocus();
                    return;
                }
                if(!edit) {
                    if (db.insertMainMenu(etMainMenu.getText().toString(), Integer.parseInt(etCounterID.getText().toString()))) {
                        systemSetting.showMessage(SystemSetting.SUCCESS,"Success!",context,getLayoutInflater());
                        etMainMenu.setText("");
                        etCounterID.setText("");
                    }
                }else{
                    if (db.updateMainMenu(editid,etMainMenu.getText().toString(), Integer.parseInt(etCounterID.getText().toString()))) {
                        systemSetting.showMessage(SystemSetting.SUCCESS,"Success!",context,getLayoutInflater());
                        etMainMenu.setText("");
                        etCounterID.setText("");
                        showMainMenuList();
                        setupDialog.dismiss();
                    }
                }
            }
        });
    }

    private void showSubMenuDialog(int mainMenuPosition,String subMenuName,String sortCode,final boolean edit){
        LayoutInflater reg=LayoutInflater.from(context);
        View view=reg.inflate(R.layout.dg_st_sub_menu, null);
        android.app.AlertDialog.Builder dialog=new android.app.AlertDialog.Builder(context);
        dialog.setView(view);

        final Spinner spMainMenu=(Spinner) view.findViewById(R.id.spMainMenu);
        final EditText etSubMenu=(EditText)view.findViewById(R.id.etSubMenu);
        final EditText etSortCode=(EditText)view.findViewById(R.id.etSortCode);
        final Button btnClose=(Button)view.findViewById(R.id.btnClose);
        final Button btnSave=(Button)view.findViewById(R.id.btnSave);

        dialog.setCancelable(false);
        final android.app.AlertDialog setupDialog=dialog.create();
        setupDialog.show();

        bindMainMenu(spMainMenu);

        if(edit){
            spMainMenu.setSelection(mainMenuPosition);
            etSubMenu.setText(subMenuName);
            etSortCode.setText(String.valueOf(sortCode));
        }

        btnClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                MainMenuData data=new MainMenuData();
                data.setMainMenuID(0);
                data.setMainMenuName("Main Menu");
                lstMainMenuData.add(0,data);
                showSubMenuList();
                setupDialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(lstMainMenuData.size()==0){
                    systemSetting.showMessage(SystemSetting.WARNING,"Fill Main Menu First!",context,getLayoutInflater());
                    return;
                }
                int position= spMainMenu.getSelectedItemPosition();
                int mainMenuID=lstMainMenuData.get(position).getMainMenuID();
                String mainMenuName=lstMainMenuData.get(position).getMainMenuName();
                if(etSubMenu.getText().toString().length()==0){
                    systemSetting.showMessage(SystemSetting.WARNING,"Enter Name!",context,getLayoutInflater());
                    etSubMenu.requestFocus();
                    return;
                }
                if(etSortCode.getText().toString().length()==0){
                    systemSetting.showMessage(SystemSetting.WARNING,"Enter Sort Code!",context,getLayoutInflater());
                    etSortCode.requestFocus();
                    return;
                }
                if(!edit) {
                    if (db.insertSubMenu(etSubMenu.getText().toString(),mainMenuID,etSortCode.getText().toString())) {
                        systemSetting.showMessage(SystemSetting.SUCCESS,"Success!",context,getLayoutInflater());
                        etSubMenu.setText("");
                        etSortCode.setText("");
                    } else {
                        systemSetting.showMessage(SystemSetting.INFO,"Sort Code " + etSortCode.getText().toString() + " has already existed in Main Menu " + mainMenuName + "!",context,getLayoutInflater());
                        etSortCode.requestFocus();
                    }
                }else{
                    if (db.updateSubMenu(editid,etSubMenu.getText().toString(),mainMenuID,etSortCode.getText().toString())) {
                        MainMenuData data=new MainMenuData();
                        data.setMainMenuID(0);
                        data.setMainMenuName("Main Menu");
                        lstMainMenuData.add(0,data);
                        systemSetting.showMessage(SystemSetting.SUCCESS,"Success!",context,getLayoutInflater());
                        etSubMenu.setText("");
                        etSortCode.setText("");
                        showSubMenuList();
                        setupDialog.dismiss();
                    }else {
                        systemSetting.showMessage(SystemSetting.INFO,"Sort Code " + etSortCode.getText().toString() + " has already existed in Main Menu " + mainMenuName + "!",context,getLayoutInflater());
                        etSortCode.requestFocus();
                    }
                }
            }
        });
    }

    private void showTasteDialog(String name,final boolean edit){
        LayoutInflater reg=LayoutInflater.from(context);
        View view=reg.inflate(R.layout.dg_st_taste, null);
        android.app.AlertDialog.Builder dialog=new android.app.AlertDialog.Builder(context);
        dialog.setView(view);

        final EditText etTaste=(EditText)view.findViewById(R.id.etTaste);
        final Button btnClose=(Button)view.findViewById(R.id.btnClose);
        final Button btnSave=(Button)view.findViewById(R.id.btnSave);

        dialog.setCancelable(false);
        final android.app.AlertDialog setupDialog=dialog.create();
        setupDialog.show();

        if(edit){
            etTaste.setText(name);
        }

        btnClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                showTasteList();
                setupDialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(etTaste.getText().toString().length()==0){
                    systemSetting.showMessage(SystemSetting.WARNING,"Enter Name!",context,getLayoutInflater());
                    etTaste.requestFocus();
                    return;
                }
                if(!edit) {
                    if (db.insertTaste(etTaste.getText().toString())) {
                        systemSetting.showMessage(SystemSetting.SUCCESS,"Success!",context,getLayoutInflater());
                        etTaste.setText("");
                    }
                }else{
                    if (db.updateTaste(editid,etTaste.getText().toString())) {
                        systemSetting.showMessage(SystemSetting.SUCCESS,"Success!",context,getLayoutInflater());
                        etTaste.setText("");
                        showTasteList();
                        setupDialog.dismiss();
                    }
                }
            }
        });
    }

    private void showCompanyDialog(String name,final boolean edit){
        LayoutInflater reg=LayoutInflater.from(context);
        View view=reg.inflate(R.layout.dg_st_company, null);
        android.app.AlertDialog.Builder dialog=new android.app.AlertDialog.Builder(context);
        dialog.setView(view);

        final EditText etCompany=(EditText)view.findViewById(R.id.etCompany);
        final Button btnClose=(Button)view.findViewById(R.id.btnClose);
        final Button btnSave=(Button)view.findViewById(R.id.btnSave);

        dialog.setCancelable(false);
        final android.app.AlertDialog setupDialog=dialog.create();
        setupDialog.show();

        if(edit){
            etCompany.setText(name);
        }

        btnClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                showCompanyList();
                setupDialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(etCompany.getText().toString().length()==0){
                    systemSetting.showMessage(SystemSetting.WARNING,"Enter Company!",context,getLayoutInflater());
                    etCompany.requestFocus();
                    return;
                }
                if(!edit) {
                    if (db.insertCompany(etCompany.getText().toString())) {
                        systemSetting.showMessage(SystemSetting.SUCCESS,"Success!",context,getLayoutInflater());
                        etCompany.setText("");
                    }
                }else{
                    if (db.updateCompany(editid,etCompany.getText().toString())) {
                        systemSetting.showMessage(SystemSetting.SUCCESS,"Success!",context,getLayoutInflater());
                        etCompany.setText("");
                        showCompanyList();
                        setupDialog.dismiss();
                    }
                }
            }
        });
    }

    private void showItemDialog(int mainMenuID,int sTypeID,String itemid,String name,double price,int outOfOrder,final boolean edit,byte[] editImage){
        LayoutInflater reg=LayoutInflater.from(context);
        View view=reg.inflate(R.layout.dg_st_item, null);
        android.app.AlertDialog.Builder dialog=new android.app.AlertDialog.Builder(context);
        dialog.setView(view);

        final Spinner spMainMenu=(Spinner) view.findViewById(R.id.spMainMenu);
        final Spinner spSubMenu=(Spinner) view.findViewById(R.id.spSubMenu);
        final Spinner spSType=(Spinner) view.findViewById(R.id.spSType);
        final EditText etItemID=(EditText)view.findViewById(R.id.etItemID);
        final EditText etItemName=(EditText)view.findViewById(R.id.etItemName);
        final EditText etPrice=(EditText)view.findViewById(R.id.etPrice);
        final CheckBox chkOutOfOrder=(CheckBox)view.findViewById(R.id.chkOutOfOrder);
        final Button btnClose=(Button)view.findViewById(R.id.btnClose);
        final Button btnSave=(Button)view.findViewById(R.id.btnSave);
        ivItemImage=(ImageView)view.findViewById(R.id.ivItemImage);
        final ImageButton btnImageBrowse=(ImageButton)view.findViewById(R.id.btnImageBrowse);
        final ImageButton btnImageDelete=(ImageButton)view.findViewById(R.id.btnImageDelete);
        final LinearLayout layoutItemImage=(LinearLayout) view.findViewById(R.id.layoutItemImage);
        btnAddItemSub=view.findViewById(R.id.btnAddItemSub);

        if(db.getFeatureResult(FeatureList.fUseItemImage)==1)layoutItemImage.setVisibility(View.VISIBLE);

        dialog.setCancelable(false);
        final android.app.AlertDialog setupDialog=dialog.create();
        setupDialog.show();

        bindMainMenu(spMainMenu);
        bindSType(spSType);
        int mainMenuPosition=0;
        for(int i=0;i<lstMainMenuData.size();i++){
            if(lstMainMenuData.get(i).getMainMenuID()==mainMenuID){
                mainMenuPosition=i;
                break;
            }
        }
        int sTypePosition=0;
        for(int i = 0; i< lstSTypeData.size(); i++){
            if(lstSTypeData.get(i).getsTypeID()==sTypeID){
                sTypePosition=i;
                break;
            }
        }

        spMainMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (lstMainMenuData.size() != 0) {
                    selectedMainMenuID = lstMainMenuData.get(position).getMainMenuID();
                }
                bindSubMenuForItemEntry(spSubMenu);
                for (int i = 0; i < lstSubMenuData.size(); i++) {
                    if (lstSubMenuData.get(i).getSubMenuID() == editSubMenuID) {
                        editSubMenuPosition = i;
                        spSubMenu.setSelection(editSubMenuPosition);
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if(edit){
            spMainMenu.setSelection(mainMenuPosition);
            spSType.setSelection(sTypePosition);
            etItemID.setText(itemid);
            etItemName.setText(name);
            etPrice.setText(String.valueOf(price));
            if(outOfOrder==1)chkOutOfOrder.setChecked(true);
            else chkOutOfOrder.setChecked(false);
            if(editImage != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(editImage, 0, editImage.length);
                if (bitmap != null) ivItemImage.setImageBitmap(bitmap);
                saveBitmap = bitmap;
            }else{
                ivItemImage.setImageBitmap(null);
            }
            if(lstIncludeItemSubGroup.size()!=0)btnAddItemSub.setText("Include "+lstIncludeItemSubGroup.size()+" Item Sub Group");
        }else{
            lstIncludeItemSubGroup=new ArrayList<>();
        }

        btnImageBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePhotoFromGallery();
            }
        });

        ivItemImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                btnImageDelete.setVisibility(View.VISIBLE);
                return false;
            }
        });

        btnImageDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnImageDelete.setVisibility(View.GONE);
                ivItemImage.setImageBitmap(null);
            }
        });

        btnAddItemSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(context,AddItemSubActivity.class);
                i.putExtra("LstIncludeItemSubGroup",(Serializable) lstIncludeItemSubGroup);
                startActivityForResult(i,ADD_ITEM_SUB_REQUEST);
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                MainMenuData main=new MainMenuData();
                main.setMainMenuID(0);
                main.setMainMenuName("Main Menu");
                lstMainMenuData.add(0,main);
                showItemList();
                setupDialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                saveImage=null;
                if(lstMainMenuData.size()==0){
                    systemSetting.showMessage(SystemSetting.WARNING,"Fill Main Menu First!",context,getLayoutInflater());
                    return;
                }
                if(lstSubMenuData.size()==0){
                    systemSetting.showMessage(SystemSetting.ERROR,"Fill Sub Menu First!",context,getLayoutInflater());
                    return;
                }
                if(lstSTypeData.size()==0){
                    systemSetting.showMessage(SystemSetting.WARNING,"Fill Item Type First!",context,getLayoutInflater());
                    return;
                }
                int position= spSubMenu.getSelectedItemPosition();
                int subMenuID=lstSubMenuData.get(position).getSubMenuID();
                int sTypePosition=spSType.getSelectedItemPosition();
                int sTypeID= lstSTypeData.get(sTypePosition).getsTypeID();
                if(etItemID.getText().toString().length()==0){
                    systemSetting.showMessage(SystemSetting.WARNING,"Enter ItemID!",context,getLayoutInflater());
                    etItemID.requestFocus();
                    return;
                }
                if(etItemName.getText().toString().length()==0){
                    systemSetting.showMessage(SystemSetting.WARNING,"Enter Item Name!",context,getLayoutInflater());
                    etItemName.requestFocus();
                    return;
                }
                if(etPrice.getText().toString().length()==0){
                    systemSetting.showMessage(SystemSetting.WARNING,"Enter Price",context,getLayoutInflater());
                    etPrice.requestFocus();
                    return;
                }
                String itemid= etItemID.getText().toString();
                int outOfOrder;
                if(chkOutOfOrder.isChecked())outOfOrder=1;
                else outOfOrder=0;

                //Bitmap b=((BitmapDrawable)ivItemImage.getDrawable()).getBitmap();
                if(saveBitmap != null) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    saveBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    saveImage = stream.toByteArray();
                    saveBitmap=null;
                }

                if(!edit) {
                    if (db.insertItem(itemid,etItemName.getText().toString(),subMenuID,Double.parseDouble(etPrice.getText().toString()),outOfOrder,sTypeID,saveImage)) {
                        for(int i=0;i<lstIncludeItemSubGroup.size();i++) {
                            db.insertItemAndSub(itemid, lstIncludeItemSubGroup.get(i).getPkId(), i + 1);
                        }
                        systemSetting.showMessage(SystemSetting.SUCCESS,"Success!",context,getLayoutInflater());
                        etItemID.setText("");
                        etItemName.setText("");
                        etPrice.setText("");
                        chkOutOfOrder.setChecked(false);
                        ivItemImage.setImageBitmap(null);
                        lstIncludeItemSubGroup=new ArrayList<>();
                        btnAddItemSub.setText(getResources().getString(R.string.add_item_sub));
                    } else {
                        systemSetting.showMessage(SystemSetting.INFO,"ItemID " + itemid + " has already existed!",context,getLayoutInflater());
                        etItemID.requestFocus();
                    }
                }else{
                    if (db.updateItem(editid,itemid,etItemName.getText().toString(),subMenuID,Double.parseDouble(etPrice.getText().toString()),outOfOrder,sTypeID,saveImage)) {
                        db.deleteItemAndSub(itemid);
                        for(int i=0;i<lstIncludeItemSubGroup.size();i++) {
                            db.insertItemAndSub(itemid, lstIncludeItemSubGroup.get(i).getPkId(), i + 1);
                        }
                        MainMenuData main=new MainMenuData();
                        main.setMainMenuID(0);
                        main.setMainMenuName("Main Menu");
                        lstMainMenuData.add(0,main);
                        systemSetting.showMessage(SystemSetting.SUCCESS,"Success!",context,getLayoutInflater());
                        etItemID.setText("");
                        etItemName.setText("");
                        etPrice.setText("");
                        chkOutOfOrder.setChecked(false);
                        ivItemImage.setImageBitmap(null);
                        showItemList();
                        setupDialog.dismiss();
                    }else {
                        systemSetting.showMessage(SystemSetting.INFO,"ItemID " + itemid + " has already existed!",context,getLayoutInflater());
                        etItemID.requestFocus();
                    }
                }
            }
        });
    }

    private void showItemSubGroupDialog(String groupName, String subTitle, int isSingleCheck, final boolean edit){
        LayoutInflater reg=LayoutInflater.from(context);
        View view=reg.inflate(R.layout.dg_st_item_sub_group, null);
        android.app.AlertDialog.Builder dialog=new android.app.AlertDialog.Builder(context);
        dialog.setView(view);

        final EditText etGroupName = view.findViewById(R.id.etGroupName);
        final EditText etSubTitle = view.findViewById(R.id.etSubTitle);
        final RadioButton rdoAllowSingle = view.findViewById(R.id.rdoAllowSingle);
        final RadioButton rdoAllowMulti = view.findViewById(R.id.rdoAllowMulti);
        final Button btnClose = view.findViewById(R.id.btnClose);
        final Button btnSave = view.findViewById(R.id.btnSave);

        dialog.setCancelable(false);
        final android.app.AlertDialog setupDialog=dialog.create();
        setupDialog.show();

        if(edit){
            etGroupName.setText(groupName);
            etSubTitle.setText(subTitle);
            if(isSingleCheck == 1){
                rdoAllowSingle.setChecked(true);
                rdoAllowMulti.setChecked(false);
            }else{
                rdoAllowSingle.setChecked(false);
                rdoAllowMulti.setChecked(true);
            }
        }

        rdoAllowSingle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rdoAllowSingle.isChecked())rdoAllowMulti.setChecked(false);
            }
        });
        rdoAllowMulti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rdoAllowMulti.isChecked())rdoAllowSingle.setChecked(false);
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                showItemSubGroupList();
                setupDialog.dismiss();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                int isSingleCheck=0;
                if(etGroupName.getText().toString().length()==0){
                    systemSetting.showMessage(SystemSetting.WARNING,"Enter Group Name!",context,getLayoutInflater());
                    etGroupName.requestFocus();
                    return;
                }else if(etSubTitle.getText().toString().length()==0){
                    systemSetting.showMessage(SystemSetting.WARNING,"Enter Sub Title!",context,getLayoutInflater());
                    etSubTitle.requestFocus();
                    return;
                }
                if(rdoAllowSingle.isChecked())isSingleCheck=1;
                else isSingleCheck=0;
                if(!edit) {
                    if (db.insertItemSubGroup(etGroupName.getText().toString(),etSubTitle.getText().toString(),isSingleCheck)) {
                        systemSetting.showMessage(SystemSetting.SUCCESS,"Success!",context,getLayoutInflater());
                        etGroupName.setText("");
                        etSubTitle.setText("");
                    }
                }else{
                    if (db.updateItemSubGroup(editid,etGroupName.getText().toString(),etSubTitle.getText().toString(),isSingleCheck)) {
                        systemSetting.showMessage(SystemSetting.SUCCESS,"Success!",context,getLayoutInflater());
                        etGroupName.setText("");
                        etSubTitle.setText("");
                        showItemSubGroupList();
                        setupDialog.dismiss();
                    }
                }
            }
        });
    }

    private void showItemSubDialog(int itemSubGroupPosition,String subName,int price,final boolean edit){
        LayoutInflater reg=LayoutInflater.from(context);
        View view=reg.inflate(R.layout.dg_st_item_sub, null);
        android.app.AlertDialog.Builder dialog=new android.app.AlertDialog.Builder(context);
        dialog.setView(view);

        final Spinner spItemSubGroup= view.findViewById(R.id.spItemSubGroup);
        final EditText etSubName= view.findViewById(R.id.etSubName);
        final EditText etPrice= view.findViewById(R.id.etPrice);
        final Button btnClose= view.findViewById(R.id.btnClose);
        final Button btnSave= view.findViewById(R.id.btnSave);

        dialog.setCancelable(false);
        final android.app.AlertDialog setupDialog=dialog.create();
        setupDialog.show();

        bindItemSubGroup(spItemSubGroup);

        if(edit){
            spItemSubGroup.setSelection(itemSubGroupPosition);
            etSubName.setText(subName);
            etPrice.setText(String.valueOf(price));
        }

        btnClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                showItemSubList();
                setupDialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(lstItemSubGroupData.size()==0){
                    systemSetting.showMessage(SystemSetting.WARNING,"Firstly, fill item sub group!",context,getLayoutInflater());
                    return;
                }
                int position= spItemSubGroup.getSelectedItemPosition();
                int itemSubGroupId=lstItemSubGroupData.get(position).getPkId();
                if(etSubName.getText().toString().length()==0){
                    systemSetting.showMessage(SystemSetting.WARNING,"Enter Sub Name!",context,getLayoutInflater());
                    etSubName.requestFocus();
                    return;
                }else if(etPrice.getText().toString().length()==0){
                    systemSetting.showMessage(SystemSetting.WARNING,"Enter Sub Price!",context,getLayoutInflater());
                    etPrice.requestFocus();
                    return;
                }
                if(!edit) {
                    if (db.insertItemSub(itemSubGroupId,etSubName.getText().toString(),Integer.parseInt(etPrice.getText().toString()))) {
                        systemSetting.showMessage(SystemSetting.SUCCESS,"Success!",context,getLayoutInflater());
                        etSubName.setText("");
                        etPrice.setText("");
                    }
                }else{
                    if (db.updateItemSub(editid,itemSubGroupId,etSubName.getText().toString(),Integer.parseInt(etPrice.getText().toString()))) {
                        systemSetting.showMessage(SystemSetting.SUCCESS,"Success!",context,getLayoutInflater());
                        etSubName.setText("");
                        etPrice.setText("");
                        showItemSubList();
                        setupDialog.dismiss();
                    }
                }
            }
        });
    }

    public void choosePhotoFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    /**private void showBOSKeyboardDialog(final EditText text){
        LayoutInflater li=LayoutInflater.from(context);
        View view=li.inflate(R.layout.dg_bos_keyboard, null);
        AlertDialog.Builder dialog=new AlertDialog.Builder(context);
        dialog.setView(view);

        etText=(EditText)view.findViewById(R.id.etText);
        final ImageButton btnBackspace1=(ImageButton)view.findViewById(R.id.btnBackspace1);
        final ImageButton btnBackspace2=(ImageButton)view.findViewById(R.id.btnBackspace2);
        final ImageButton btnChange1=(ImageButton)view.findViewById(R.id.btnChange1);
        final ImageButton btnChange2=(ImageButton)view.findViewById(R.id.btnChange2);
        final TableLayout layoutBOSKey1=(TableLayout)view.findViewById(R.id.layoutBOSKey1);
        final TableLayout layoutBOSKey2=(TableLayout)view.findViewById(R.id.layoutBOSKey2);
        final Button btnOK=(Button)view.findViewById(R.id.btnOK);
        final Button btnCancel=(Button)view.findViewById(R.id.btnCancel);
        final Button btnKaKyi=(Button)view.findViewById(R.id.btnKaKyi);
        final Button btnKhaKway=(Button)view.findViewById(R.id.btnKhaKway);
        final Button btnGaNgal=(Button)view.findViewById(R.id.btnGaNgal);
        final Button btnGaKyi=(Button)view.findViewById(R.id.btnGaKyi);
        final Button btnNga=(Button)view.findViewById(R.id.btnNga);
        final Button btnSaLone=(Button)view.findViewById(R.id.btnSaLone);
        final Button btnSaLain=(Button)view.findViewById(R.id.btnSaLain);
        final Button btnZaKwal=(Button)view.findViewById(R.id.btnZaKwal);
        final Button btnNya=(Button)view.findViewById(R.id.btnNya);
        final Button btnHtaWinBal=(Button)view.findViewById(R.id.btnHtaWinBal);
        final Button btnNaKyi=(Button)view.findViewById(R.id.btnNaKyi);
        final Button btnTaWinPu=(Button)view.findViewById(R.id.btnTaWinPu);
        final Button btnHtaSinHtoo=(Button)view.findViewById(R.id.btnHtaSinHtoo);
        final Button btnDaDway=(Button)view.findViewById(R.id.btnDaDway);
        final Button btnDaOutChait=(Button)view.findViewById(R.id.btnDaOutChait);
        final Button btnNaNgal=(Button)view.findViewById(R.id.btnNaNgal);
        final Button btnPaSaut=(Button)view.findViewById(R.id.btnPaSaut);
        final Button btnPhaOHtot=(Button)view.findViewById(R.id.btnPhaOHtot);
        final Button btnBaLaChait=(Button)view.findViewById(R.id.btnBaLaChait);
        final Button btnBaGone=(Button)view.findViewById(R.id.btnBaGone);
        final Button btnMa=(Button)view.findViewById(R.id.btnMa);
        final Button btnYaPatLat=(Button)view.findViewById(R.id.btnYaPatLat);
        final Button btnYaKaut=(Button)view.findViewById(R.id.btnYaKaut);
        final Button btnLa=(Button)view.findViewById(R.id.btnLa);
        final Button btnWa=(Button)view.findViewById(R.id.btnWa);
        final Button btnTha=(Button)view.findViewById(R.id.btnTha);
        final Button btnHa=(Button)view.findViewById(R.id.btnHa);
        final Button btnLaKyi=(Button)view.findViewById(R.id.btnLaKyi);
        final Button btnAa=(Button)view.findViewById(R.id.btnAa);
        final Button btnU=(Button)view.findViewById(R.id.btnU);
        final Button btnA=(Button)view.findViewById(R.id.btnA);
        final Button btnNaTo=(Button)view.findViewById(R.id.btnNaTo);
        final Button btnEee=(Button)view.findViewById(R.id.btnEee);
        final Button btnThaWayHtoe=(Button)view.findViewById(R.id.btnThaWayHtoe);
        final Button btnYaPin=(Button)view.findViewById(R.id.btnYaPin);
        final Button btnLoneKyiTin=(Button)view.findViewById(R.id.btnLoneKyiTin);
        final Button btnShaeHtoe=(Button)view.findViewById(R.id.btnShaeHtoe);
        final Button btnYayChaShal=(Button)view.findViewById(R.id.btnYayChaShal);
        final Button btnOutKaMyint=(Button)view.findViewById(R.id.btnOutKaMyint);
        final Button btnYaYitThay=(Button)view.findViewById(R.id.btnYaYitThay);
        final Button btnTaChaungNginTo=(Button)view.findViewById(R.id.btnTaChaungNginTo);
        final Button btnNaChaungNginTo=(Button)view.findViewById(R.id.btnNaChaungNginTo);
        final Button btnYayChaTo=(Button)view.findViewById(R.id.btnYayChaTo);
        final Button btnWitSanPaut=(Button)view.findViewById(R.id.btnWitSanPaut);
        final Button btnYaPinHaHtoe=(Button)view.findViewById(R.id.btnYaPinHaHtoe);
        final Button btnYaPinWaSwalHaHtoe=(Button)view.findViewById(R.id.btnYaPinWaSwalHaHtoe);
        final Button btnYaPinWaSwal=(Button)view.findViewById(R.id.btnYaPinWaSwal);
        final Button btnWaSwalHaHtoe=(Button)view.findViewById(R.id.btnWaSwalHaHtoe);
        final Button btnYwae=(Button)view.findViewById(R.id.btnYwae);
        final Button btnHaHtoeChaungKhaing=(Button)view.findViewById(R.id.btnHaHtoeChaungKhaing);
        final Button btnHaHtoe=(Button)view.findViewById(R.id.btnHaHtoe);
        final Button btnLoneKyiTinSanKhat=(Button)view.findViewById(R.id.btnLoneKyiTinSanKhat);
        final Button btnNgaThat=(Button)view.findViewById(R.id.btnNgaThat);
        final Button btnWaSwal=(Button)view.findViewById(R.id.btnWaSwal);
        final Button btnThayThayTin=(Button)view.findViewById(R.id.btnThayThayTin);
        final Button btnNautPyit=(Button)view.findViewById(R.id.btnNautPyit);
        final Button btnTaChaungNginShal=(Button)view.findViewById(R.id.btnTaChaungNginShal);
        final Button btnNaChaungNginShal=(Button)view.findViewById(R.id.btnNaChaungNginShal);
        final Button btnYaYitKyiPyat=(Button)view.findViewById(R.id.btnYaYitKyiPyat);
        final Button btnYaYitKyi=(Button)view.findViewById(R.id.btnYaYitKyi);
        final Button btnYaYitThayPyat=(Button)view.findViewById(R.id.btnYaYitThayPyat);
        final Button btnYayChaShalHtoe=(Button)view.findViewById(R.id.btnYayChaShalHtoe);
        //final Button btnZaMyintZwe=(Button)view.findViewById(R.id.btnZaMyintZwe);
        //final Button btnEeeKyi=(Button)view.findViewById(R.id.btnEeeKyi);
        //final Button btnThreeTha=(Button)view.findViewById(R.id.btnThreeTha);
        //final Button btnNhait=(Button)view.findViewById(R.id.btnNhait);
        //final Button btnTaTaLinChait=(Button)view.findViewById(R.id.btnTaTaLinChait);
        //final Button btnDaYinKaut=(Button)view.findViewById(R.id.btnDaYinKaut);
        //final Button btnDaYinMhot=(Button)view.findViewById(R.id.btnDaYinMhot);

        etText.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnOK.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/CAMBRIAB.TTF"));
        btnCancel.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/CAMBRIAB.TTF"));
        btnKaKyi.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnKhaKway.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnGaNgal.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnGaKyi.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnNga.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnSaLone.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnSaLain.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnZaKwal.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnNya.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnHtaWinBal.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnNaKyi.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnTaWinPu.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnHtaSinHtoo.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnDaDway.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnDaOutChait.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnNaNgal.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnPaSaut.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnPhaOHtot.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnBaLaChait.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnBaGone.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnMa.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnYaPatLat.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnYaKaut.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnLa.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnWa.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnTha.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnHa.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnLaKyi.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnAa.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnU.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnA.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnNaTo.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnEee.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnThaWayHtoe.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnYaPin.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnLoneKyiTin.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnShaeHtoe.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnYayChaShal.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnOutKaMyint.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnYaYitThay.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnTaChaungNginTo.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnNaChaungNginTo.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnYayChaTo.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnWitSanPaut.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnYaPinHaHtoe.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnYaPinWaSwalHaHtoe.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnYaPinWaSwal.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnWaSwalHaHtoe.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnYwae.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnHaHtoeChaungKhaing.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnHaHtoe.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnLoneKyiTinSanKhat.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnNgaThat.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnWaSwal.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnThayThayTin.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnNautPyit.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnTaChaungNginShal.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnNaChaungNginShal.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnYaYitKyiPyat.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnYaYitKyi.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnYaYitThayPyat.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        btnYayChaShalHtoe.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        // btnEeeKyi.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        //btnThreeTha.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        //btnNhait.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        //btnDaYinKaut.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        //btnDaYinMhot.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        //btnZaMyintZwe.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        //btnTaTaLinChait.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));

        dialog.setCancelable(false);
        final AlertDialog alertDialog=dialog.create();
        alertDialog.show();
        BOS_KEYBOARD_COUNT=1;

        btnBackspace1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(etText.getText().toString().length()!=0){
                    String searchkey=etText.getText().toString();
                    searchkey=searchkey.substring(0,searchkey.length()-1);
                    etText.setText(searchkey);
                }
            }
        });
        btnBackspace2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(etText.getText().toString().length()!=0){
                    String searchkey=etText.getText().toString();
                    searchkey=searchkey.substring(0,searchkey.length()-1);
                    etText.setText(searchkey);
                }
            }
        });
        btnChange1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
               layoutBOSKey1.setVisibility(View.GONE);
                layoutBOSKey2.setVisibility(View.VISIBLE);
            }
        });
        btnChange2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                layoutBOSKey1.setVisibility(View.VISIBLE);
                layoutBOSKey2.setVisibility(View.GONE);
            }
        });
        btnOK.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick( View arg0) {
                if(etText.getText().toString().length()!=0){
                    text.setText(etText.getText().toString());
                }
                alertDialog.dismiss();
                BOS_KEYBOARD_COUNT=0;
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick( View arg0) {
                alertDialog.dismiss();
                BOS_KEYBOARD_COUNT=0;
            }
        });
        btnKaKyi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnKaKyi,"¡");
            }
        });
        btnKhaKway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnKhaKway,"¢");
            }
        });
        btnGaNgal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnGaNgal,"£");
            }
        });
        btnGaKyi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnGaKyi,"¤");
            }
        });
        btnNga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnNga,"¥");
            }
        });
        btnSaLone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnSaLone,"¦");
            }
        });
        btnSaLain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnSaLain,"§");
            }
        });
        btnZaKwal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnZaKwal,"¨");
            }
        });
        btnNya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnNya,"«");
            }
        });
        btnHtaWinBal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnHtaWinBal,"\u00AD");
            }
        });
        btnNaKyi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnNaKyi,"°");
            }
        });
        btnTaWinPu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnTaWinPu,"±");
            }
        });
        btnHtaSinHtoo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnHtaSinHtoo,"²");
            }
        });
        btnDaDway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnDaDway,"³");
            }
        });
        btnDaOutChait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnDaOutChait,"´");
            }
        });
        btnNaNgal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnNaNgal,"Ì");
            }
        });
        btnPaSaut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnPaSaut,"¶");
            }
        });
        btnPhaOHtot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnPhaOHtot,"µ");
            }
        });
        btnBaLaChait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnBaLaChait,"¸");
            }
        });
        btnBaGone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnBaGone,"¹");
            }
        });
        btnMa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnMa,"º");
            }
        });
        btnYaPatLat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnYaPatLat,"»");
            }
        });
        btnYaKaut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnYaKaut,"¼");
            }
        });
        btnLa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnLa,"½");
            }
        });
        btnWa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnWa,"¾");
            }
        });
        btnTha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnTha,"¿");
            }
        });
        btnHa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnHa,"À");
            }
        });
        btnLaKyi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnLaKyi,"Á");
            }
        });
        btnAa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnAa,"Â");
            }
        });
        btnU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnU,"Ü");
            }
        });
        btnA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnA,"Þ");
            }
        });
        btnNaTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnNaTo,"ƒ");
            }
        });
        btnThaWayHtoe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnThaWayHtoe,"è");
            }
        });
        btnYaPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnYaPin,"ì");
            }
        });
        btnLoneKyiTin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnLoneKyiTin,"â");
            }
        });
        btnShaeHtoe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnShaeHtoe,"ý");
            }
        });
        btnYayChaShal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnYayChaShal,"á");
            }
        });
        btnOutKaMyint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnOutKaMyint,"ú");
            }
        });
        btnYaYitThay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnYaYitThay,"í");
            }
        });
        btnTaChaungNginTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnTaChaungNginTo,"ä");
            }
        });
        btnNaChaungNginTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnNaChaungNginTo,"æ");
            }
        });
        btnYayChaTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnYayChaTo,"à");
            }
        });
        btnWitSanPaut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnWitSanPaut,"ü");
            }
        });
        btnYaPinHaHtoe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnYaPinHaHtoe,"ˆ");
            }
        });
        btnYaPinWaSwalHaHtoe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnYaPinWaSwalHaHtoe,"");
            }
        });
        btnYaPinWaSwal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnYaPinWaSwal,"");
            }
        });
        btnWaSwalHaHtoe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnWaSwalHaHtoe,"");
            }
        });
        btnYwae.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnYwae,"™");
            }
        });
        btnEee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnEee,"˜");
            }
        });
        btnHaHtoeChaungKhaing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnHaHtoeChaungKhaing,"‘");
            }
        });
        btnHaHtoe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnHaHtoe,"û");
            }
        });
        btnLoneKyiTinSanKhat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnLoneKyiTinSanKhat,"ã");
            }
        });
        btnNgaThat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnNgaThat,"“");
            }
        });
        btnWaSwal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnWaSwal,"");
            }
        });
        btnThayThayTin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnThayThayTin,"ê");
            }
        });
        btnNautPyit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnNautPyit,"é");
            }
        });
        btnTaChaungNginShal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnTaChaungNginShal,"å");
            }
        });
        btnNaChaungNginShal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnNaChaungNginShal,"ç");
            }
        });
        btnYaYitKyiPyat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnYaYitKyiPyat,"");
            }
        });
        btnYaYitKyi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnYaYitKyi,"");
            }
        });
        btnYaYitThayPyat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnYaYitThayPyat,"");
            }
        });
        btnYayChaShalHtoe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addBOSKey(btnYayChaShalHtoe,"ï");
            }
        });
    }

    private void setBOSSearchText(EditText text,View v){
        v.setFocusable(true);
        v.setFocusableInTouchMode(true);
        if(BOS_KEYBOARD_ON){
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            if(BOS_KEYBOARD_COUNT==0) showBOSKeyboardDialog(text);
        }
    }

    private void addBOSKey(Button button,String key){
        if(etText.getText().toString().length()!=0){
            String searchkey=etText.getText().toString();
            String newkey=searchkey+key;
            etText.setText(newkey);
        }else{
            etText.setText(key);
        }
    }**/

    /**
     * end setup dialog methods
     */

    private void getAllModule(ListView lvModule){
        lstModuleData=new ArrayList<>();
        Cursor cur=db.getAllModule();
        while(cur.moveToNext()){
            ModuleData data=new ModuleData();
            data.setModuleID(cur.getInt(0));
            data.setModuleName(cur.getString(1));
            lstModuleData.add(data);
        }
        moduleListAdapter=new ModuleListAdapter(this,lstModuleData);
        lvModule.setAdapter(moduleListAdapter);
        moduleListAdapter.setModuleCheckedListener(this);
    }
}
