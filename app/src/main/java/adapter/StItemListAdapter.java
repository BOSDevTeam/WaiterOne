package adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bosictsolution.waiterone.R;

import java.util.List;

import common.DBHelper;
import common.FeatureList;
import data.ItemData;
import listener.SetupEditDeleteButtonClickListener;

/**
 * Created by NweYiAung on 01-03-2017.
 */
public class StItemListAdapter extends BaseAdapter {

    private Context context;
    SetupEditDeleteButtonClickListener setupEditDeleteButtonClickListener;
    List<ItemData> lstItemData;
    Bitmap bitmap;
    DBHelper db;

    public StItemListAdapter(Context context, List<ItemData> lstItemData){
        this.context=context;
        this.lstItemData =lstItemData;
        db=new DBHelper(context);
    }

    public void setOnSetupEditDeleteButtonClickListener(SetupEditDeleteButtonClickListener setupEditDeleteButtonClickListener){
        this.setupEditDeleteButtonClickListener =setupEditDeleteButtonClickListener;
    }

    @Override
    public int getCount(){
        return lstItemData.size();
    }

    @Override
    public String getItem(int position){
        return lstItemData.get(position).getItemName();
    }

    @Override
    public long getItemId(int position){
        return 0;
    }

    static class ViewHolder {
        TextView tvItemName, tvItemID,tvSubMenuName,tvPrice,tvOutOfOrder;
        ImageButton btnEdit,btnDelete;
        ImageView ivItemImage;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        View row;
        final ViewHolder holder;
        if(convertView==null){
            LayoutInflater layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row=layoutInflater.inflate(R.layout.list_st_item, parent,false);
            holder=new ViewHolder();
            holder.tvItemID =(TextView) row.findViewById(R.id.tvItemID);
            holder.tvItemName =(TextView) row.findViewById(R.id.tvItemName);
            holder.tvSubMenuName =(TextView) row.findViewById(R.id.tvSubMenuName);
            holder.tvPrice =(TextView) row.findViewById(R.id.tvPrice);
            holder.tvOutOfOrder=(TextView)row.findViewById(R.id.tvOutOfOrder);
            holder.btnEdit=(ImageButton) row.findViewById(R.id.btnEdit);
            holder.btnDelete=(ImageButton) row.findViewById(R.id.btnDelete);
            holder.ivItemImage=(ImageView) row.findViewById(R.id.ivItemImage);

            if(db.getFeatureResult(FeatureList.fUseItemImage)==1)holder.ivItemImage.setVisibility(View.VISIBLE);

            row.setTag(holder);
        }
        else{
            row=convertView;
            holder=(ViewHolder) row.getTag();
        }

        holder.tvItemID.setText(lstItemData.get(position).getItemid());
        holder.tvItemName.setText(lstItemData.get(position).getItemName());
        holder.tvSubMenuName.setText(lstItemData.get(position).getSubMenuName());
        holder.tvPrice.setText(String.valueOf(lstItemData.get(position).getPrice()));
        if(lstItemData.get(position).getOutOfOrder()==1)holder.tvOutOfOrder.setText("1");
        else holder.tvOutOfOrder.setText("0");

        if(lstItemData.get(position).getItemImage() != null){
            bitmap = BitmapFactory.decodeByteArray(lstItemData.get(position).getItemImage(), 0, lstItemData.get(position).getItemImage().length);
            if (bitmap != null) holder.ivItemImage.setImageBitmap(bitmap);
        }else holder.ivItemImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_default_image));

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
