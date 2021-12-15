package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bosictsolution.waiterone.R;

import java.util.List;

import data.STypePrinterData;
import listener.SetupEditDeleteButtonClickListener;

/**
 * Created by User on 6/16/2017.
 */
public class STypePrinterListAdapter extends BaseAdapter {

    private Context context;
    SetupEditDeleteButtonClickListener setupEditDeleteButtonClickListener;
    List<STypePrinterData> lstSTypePrinterData;

    public STypePrinterListAdapter(Context context, List<STypePrinterData> lstSTypePrinterData){
        this.context=context;
        this.lstSTypePrinterData = lstSTypePrinterData;
    }

    public void setOnSetupEditDeleteButtonClickListener(SetupEditDeleteButtonClickListener setupEditDeleteButtonClickListener){
        this.setupEditDeleteButtonClickListener =setupEditDeleteButtonClickListener;
    }

    @Override
    public int getCount(){
        return lstSTypePrinterData.size();
    }

    @Override
    public String getItem(int position){
        return lstSTypePrinterData.get(position).getPrinterName();
    }

    @Override
    public long getItemId(int position){
        return 0;
    }

    static class ViewHolder {
        TextView tvSTypeName,tvPrinterIP;
        ImageButton btnEdit,btnDelete;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        View row;
        final ViewHolder holder;
        if(convertView==null){
            LayoutInflater layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row=layoutInflater.inflate(R.layout.list_stype_printer_setting, parent,false);
            holder=new ViewHolder();
            holder.tvSTypeName =(TextView) row.findViewById(R.id.tvSTypeName);
            holder.tvPrinterIP =(TextView) row.findViewById(R.id.tvPrinterIP);
            holder.btnEdit=(ImageButton) row.findViewById(R.id.btnEdit);
            holder.btnDelete=(ImageButton) row.findViewById(R.id.btnDelete);

            row.setTag(holder);
        }
        else{
            row=convertView;
            holder=(ViewHolder) row.getTag();
        }

        holder.tvSTypeName.setText(lstSTypePrinterData.get(position).getsTypeName());
        holder.tvPrinterIP.setText(lstSTypePrinterData.get(position).getPrinterName());

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(setupEditDeleteButtonClickListener !=null){
                    setupEditDeleteButtonClickListener.onEditButtonClickListener(position);
                }
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(setupEditDeleteButtonClickListener !=null){
                    setupEditDeleteButtonClickListener.onDeleteButtonClickListener(position);
                }
            }
        });

        return row;
    }
}
