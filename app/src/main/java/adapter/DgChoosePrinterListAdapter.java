package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bosictsolution.waiterone.R;

import java.util.List;

import data.PrinterData;
import listener.PrinterChoiceListener;

/**
 * Created by NweYiAung on 27-12-2016.
 */
public class DgChoosePrinterListAdapter extends BaseAdapter {

    private Context context;
    private List<PrinterData> lstPrinterData;
    PrinterChoiceListener printerChoiceListener;
    static int selectedOldPosition=-1;
    static CompoundButton selectedOldButton;

    public DgChoosePrinterListAdapter(Context context, List<PrinterData> lstPrinterData){
        this.context=context;
        this.lstPrinterData =lstPrinterData;
    }

    public void setCustomChoiceListener(PrinterChoiceListener printerChoiceListener){
        this.printerChoiceListener=printerChoiceListener;
    }

    public int getCount(){
        return  lstPrinterData.size();
    }

    public Object getItem(int position){
        return position;
    }

    public long getItemId(int position){
        return position;
    }

    static class ViewHolder {
       //protected RadioGroup rdoGroupPrinter;
        protected RadioButton rdoPrinter;
    }

    public View getView(final int position, View convertView, ViewGroup parent){
        final ViewHolder viewHolder;
        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.list_dg_choose_printer, null);
            viewHolder = new ViewHolder();
            viewHolder.rdoPrinter=(RadioButton)convertView.findViewById(R.id.rdoPrinter);
            //viewHolder.rdoGroupPrinter = (RadioGroup) convertView.findViewById(R.id.rdoGroupPrinter);

            /**viewHolder.rdoGroupPrinter.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    //int getPosition = (Integer) group.getTag();  // Here we get the position that we have set for the checkbox using setTag.
                    //lstPrinterData.get(getPosition).setSelected(buttonView.isChecked()); // Set the value of checkbox to maintain its state.
                    //RadioButton spec1=(RadioButton)v.findViewById(group.getCheckedRadioButtonId());
                    //if (spec1.isChecked())
                    //{
                        //spec1.setChecked(false);
                    //}

                    for(int i=0;i<group.getChildCount();i++){
                        int id=
                    }
                    if(checkedRadioId!=0){
                        RadioButton rBtn=(RadioButton)v.findViewById(checkedRadioId);
                        rBtn.setChecked(false);
                    }
                    checkedRadioId=checkedId;
                    if(printerChoiceListener!=null){
                        //if(buttonView.isChecked()){
                            printerChoiceListener.onPrinterChoiceListener(position);
                        //}else{
                            //buttonView.setChecked(false);
                            //printerChoiceListener.onPrinterUnChoiceListener(getPosition);
                        //}
                    }
                }
            });**/
            viewHolder.rdoPrinter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int getPosition = (Integer) buttonView.getTag();  // Here we get the position that we have set for the checkbox using setTag.
                    lstPrinterData.get(getPosition).setSelected(buttonView.isChecked());// Set the value of checkbox to maintain its state.
                    if(selectedOldPosition != -1)selectedOldButton.setChecked(false);
                    selectedOldPosition=getPosition;
                    selectedOldButton=buttonView;
                    if(printerChoiceListener!=null){
                        if(buttonView.isChecked()){
                            printerChoiceListener.onPrinterChoiceListener(getPosition);
                        }else{
                            buttonView.setChecked(false);
                            printerChoiceListener.onPrinterUnChoiceListener(getPosition);
                        }
                    }
                }
            });
            convertView.setTag(viewHolder);
            convertView.setTag(R.id.rdoPrinter, viewHolder.rdoPrinter);
            //convertView.setTag(R.id.rdoGroupPrinter, viewHolder.rdoGroupPrinter);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.rdoPrinter.setTag(position); // This line is important.
        viewHolder.rdoPrinter.setText(lstPrinterData.get(position).getPrinterName());
        viewHolder.rdoPrinter.setChecked(lstPrinterData.get(position).isSelected());
        return convertView;
    }
}
