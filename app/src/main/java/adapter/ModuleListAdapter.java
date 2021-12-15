package adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.bosictsolution.waiterone.R;

import java.util.List;

import data.ModuleData;
import listener.ModuleCheckedListener;

/**
 * Created by User on 6/5/2017.
 */
public class ModuleListAdapter extends BaseAdapter {

    private Context context;
    List<ModuleData> lstModuleData;
    ModuleCheckedListener checkedListener;

    public ModuleListAdapter(Context context,List<ModuleData> lstModuleData){
        this.context=context;
        this.lstModuleData =lstModuleData;
    }

    public void setModuleCheckedListener(ModuleCheckedListener checkedListener){
        this.checkedListener=checkedListener;
    }

    public int getCount(){
        return lstModuleData.size();
    }

    public Object getItem(int position){
        return position;
    }

    public long getItemId(int position){
        return position;
    }

    static class ViewHolder {
        protected CheckBox chkModule;
    }

    public View getView(final int position, View convertView, ViewGroup parent){
        final ViewHolder viewHolder;
        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.list_module, null);
            viewHolder = new ViewHolder();
            viewHolder.chkModule=(CheckBox)convertView.findViewById(R.id.chkModule);

            viewHolder.chkModule.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int getPosition = (Integer) buttonView.getTag();  // Here we get the position that we have set for the checkbox using setTag.
                    lstModuleData.get(getPosition).setSelected(buttonView.isChecked()); // Set the value of checkbox to maintain its state.
                    if(checkedListener!=null){
                        if(buttonView.isChecked()){
                            checkedListener.onModuleCheckedListener(getPosition);
                        }else{
                            checkedListener.onModuleUnCheckedListener(getPosition);
                        }
                    }
                }
            });
            convertView.setTag(viewHolder);
            convertView.setTag(R.id.chkModule, viewHolder.chkModule);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.chkModule.setTag(position); // This line is important.
        viewHolder.chkModule.setText(lstModuleData.get(position).getModuleName());
        viewHolder.chkModule.setChecked(lstModuleData.get(position).isSelected());

        return convertView;
    }
}
