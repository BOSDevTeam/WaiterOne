package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.bosictsolution.waiterone.R;

import java.util.List;

import data.ItemData;
import listener.CheckedListener;

/**
 * Created by NweYiAung on 26-01-2017.
 */
public class DgChooseItemListAdapter extends BaseAdapter {

    private Context context;
    private List<ItemData> lstItemData;
    CheckedListener checkedListener;
    /*public static int checkAll;
    public static boolean startItemBindState;*/

    public DgChooseItemListAdapter(Context context, List<ItemData> lstItemData){
        this.context=context;
        this.lstItemData =lstItemData;
    }

    public void setCustomCheckedListener(CheckedListener checkedListener){
        this.checkedListener=checkedListener;
    }

    public int getCount(){
        return  lstItemData.size();
    }

    public Object getItem(int position){
        return position;
    }

    public long getItemId(int position){
        return position;
    }

    static class ViewHolder {
        protected TextView tvItemName;
        protected CheckBox chkItem;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflator.inflate(R.layout.list_dg_choose_item, null);
            viewHolder = new ViewHolder();
            viewHolder.tvItemName = (TextView) convertView.findViewById(R.id.tvItemName);
            viewHolder.chkItem = (CheckBox) convertView.findViewById(R.id.chkItem);
            viewHolder.chkItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int getPosition = (Integer) buttonView.getTag();  // Here we get the position that we have set for the checkbox using setTag.
                    lstItemData.get(getPosition).setSelected(buttonView.isChecked()); // Set the value of checkbox to maintain its state.
                    if (checkedListener != null) {
                        if (buttonView.isChecked()) {
                            checkedListener.onItemCheckedListener(getPosition);
                        } else {
                            checkedListener.onItemUnCheckedListener(getPosition);
                        }
                    }
                }
            });
            convertView.setTag(viewHolder);
            convertView.setTag(R.id.tvItemName, viewHolder.tvItemName);
            convertView.setTag(R.id.chkItem, viewHolder.chkItem);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.chkItem.setTag(position); // This line is important.
        viewHolder.tvItemName.setText(lstItemData.get(position).getItemName());
        viewHolder.chkItem.setChecked(lstItemData.get(position).isSelected());

        /*if(checkAll==1){
            viewHolder.chkItem.setChecked(true);
        }
        else if(checkAll==0) {
            viewHolder.chkItem.setChecked(false);
        }

        viewHolder.tvItemName.setText(lstModuleData.get(position).getItemName());
        if(checkAll==2) {
            viewHolder.chkItem.setChecked(lstModuleData.get(position).isSelected());
        }

        if(startItemBindState) {
            if (SaleActivity.lstCheckedItemID.contains(lstModuleData.get(position).itemID)) {
                viewHolder.chkItem.setChecked(true);
            } else {
                viewHolder.chkItem.setChecked(false);
            }
        }*/
        return convertView;
    }
}
