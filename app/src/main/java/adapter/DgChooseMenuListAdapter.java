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
 * Created by NweYiAung on 05-01-2017.
 */
public class DgChooseMenuListAdapter extends BaseAdapter {

    private Context context;
    private List<ItemData> lstMenuData;
    CheckedListener checkedListener;

    public DgChooseMenuListAdapter(Context context, List<ItemData> lstMenuData){
        this.context=context;
        this.lstMenuData =lstMenuData;
    }

    public void setCustomCheckedListener(CheckedListener checkedListener){
        this.checkedListener=checkedListener;
    }

    public int getCount(){
        return  lstMenuData.size();
    }

    public Object getItem(int position){
        return position;
    }

    public long getItemId(int position){
        return position;
    }

    static class ViewHolder {
        protected TextView tvSubMenuName;
        protected CheckBox chkSubMenu;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflator.inflate(R.layout.list_dg_choose_menu, null);
            viewHolder = new ViewHolder();
            viewHolder.tvSubMenuName = (TextView) convertView.findViewById(R.id.tvSubMenuName);
            viewHolder.chkSubMenu = (CheckBox) convertView.findViewById(R.id.chkSubMenu);
            viewHolder.chkSubMenu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int getPosition = (Integer) buttonView.getTag();  // Here we get the position that we have set for the checkbox using setTag.
                    lstMenuData.get(getPosition).setSelected(buttonView.isChecked()); // Set the value of checkbox to maintain its state.
                    if(checkedListener!=null){
                        if(buttonView.isChecked()){
                            checkedListener.onSubMenuCheckedListener(getPosition);
                        }else{
                            checkedListener.onSubMenuUnCheckedListener(getPosition);
                        }
                    }
                }
            });
            convertView.setTag(viewHolder);
            convertView.setTag(R.id.tvSubMenuName, viewHolder.tvSubMenuName);
            convertView.setTag(R.id.chkSubMenu, viewHolder.chkSubMenu);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.chkSubMenu.setTag(position); // This line is important.
        viewHolder.tvSubMenuName.setText(lstMenuData.get(position).getSubMenuName());
        viewHolder.chkSubMenu.setChecked(lstMenuData.get(position).isSelected());

        return convertView;
    }
}
