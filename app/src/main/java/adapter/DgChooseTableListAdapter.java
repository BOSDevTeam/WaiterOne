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

import data.TableData;
import listener.CheckedListener;

/**
 * Created by NweYiAung on 30-12-2016.
 */
public class DgChooseTableListAdapter extends BaseAdapter {

    private Context context;
    private List<TableData> lstTableData;
    CheckedListener checkedListener;

    public DgChooseTableListAdapter(Context context, List<TableData> lstTableData){
        this.context=context;
        this.lstTableData =lstTableData;
    }

    public void setCustomCheckedListener(CheckedListener checkedListener){
        this.checkedListener=checkedListener;
    }

    public int getCount(){
        return  lstTableData.size();
    }

    public Object getItem(int position){
        return position;
    }

    public long getItemId(int position){
        return position;
    }

    static class ViewHolder {
        protected TextView tvTableName;
        protected CheckBox chkTable;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflator.inflate(R.layout.list_dg_choose_table, null);
            viewHolder = new ViewHolder();
            viewHolder.tvTableName = (TextView) convertView.findViewById(R.id.tvTableName);
            viewHolder.chkTable = (CheckBox) convertView.findViewById(R.id.chkTable);
            viewHolder.chkTable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int getPosition = (Integer) buttonView.getTag();  // Here we get the position that we have set for the checkbox using setTag.
                    lstTableData.get(getPosition).setSelected(buttonView.isChecked()); // Set the value of checkbox to maintain its state.
                    if(checkedListener!=null){
                        if(buttonView.isChecked()){
                            checkedListener.onTableCheckedListener(getPosition);
                        }else{
                            checkedListener.onTableUnCheckedListener(getPosition);
                        }
                    }
                }
            });
            convertView.setTag(viewHolder);
            convertView.setTag(R.id.tvTableName, viewHolder.tvTableName);
            convertView.setTag(R.id.chkTable, viewHolder.chkTable);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.chkTable.setTag(position); // This line is important.
        viewHolder.tvTableName.setText(lstTableData.get(position).getTableName());
        viewHolder.chkTable.setChecked(lstTableData.get(position).isSelected());

        return convertView;
    }
}

