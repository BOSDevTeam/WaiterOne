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

import data.CompanyData;
import listener.SetupEditDeleteButtonClickListener;

/**
 * Created by User on 6/21/2017.
 */
public class StCompanyListAdapter extends BaseAdapter {

    private Context context;
    SetupEditDeleteButtonClickListener setupEditDeleteButtonClickListener;
    List<CompanyData> lstCompanyData;

    public StCompanyListAdapter(Context context, List<CompanyData> lstCompanyData){
        this.context=context;
        this.lstCompanyData =lstCompanyData;
    }

    public void setOnSetupEditDeleteButtonClickListener(SetupEditDeleteButtonClickListener setupEditDeleteButtonClickListener){
        this.setupEditDeleteButtonClickListener =setupEditDeleteButtonClickListener;
    }

    @Override
    public int getCount(){
        return lstCompanyData.size();
    }

    @Override
    public String getItem(int position){
        return lstCompanyData.get(position).getCompanyName();
    }

    @Override
    public long getItemId(int position){
        return 0;
    }

    static class ViewHolder {
        TextView tvCompanyName;
        ImageButton btnEdit,btnDelete;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        View row;
        final ViewHolder holder;
        if(convertView==null){
            LayoutInflater layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row=layoutInflater.inflate(R.layout.list_st_company, parent,false);
            holder=new ViewHolder();
            holder.tvCompanyName =(TextView) row.findViewById(R.id.tvCompany);
            holder.btnEdit=(ImageButton) row.findViewById(R.id.btnEdit);
            holder.btnDelete=(ImageButton) row.findViewById(R.id.btnDelete);

            row.setTag(holder);
        }
        else{
            row=convertView;
            holder=(ViewHolder) row.getTag();
        }

        holder.tvCompanyName.setText(lstCompanyData.get(position).getCompanyName());

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
