package adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bosictsolution.waiterone.R;

import java.util.List;

import data.ItemData;
import listener.ItemImageButtonClickListener;

public class ItemImageListAdapter extends BaseAdapter {

    private Context context;
    List<ItemData> lstItemData;
    Bitmap bitmap;
    ItemImageButtonClickListener itemImageButtonClickListener;

    public ItemImageListAdapter(Context context,List<ItemData> lstItemData){
        this.context=context;
        this.lstItemData=lstItemData;
    }

    public void setOnOrderClickListener(ItemImageButtonClickListener itemImageButtonClickListener){
        this.itemImageButtonClickListener = itemImageButtonClickListener;
    }

    public int getCount(){
        return lstItemData.size();
    }

    public Object getItem(int position){
        return position;
    }

    public long getItemId(int position){
        return position;
    }

    static class ViewHolder {
        TextView tvItemName,tvPrice;
        ImageView ivItemImage;
        Button btnOrder;
    }

    public View getView(final int position, View convertView, ViewGroup parent){
        View vi;
        final ViewHolder holder;
        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vi=inflater.inflate(R.layout.list_item_image, null);
            holder=new ViewHolder();

            holder.ivItemImage=(ImageView)vi.findViewById(R.id.ivItemImage);
            holder.tvItemName=(TextView)vi.findViewById(R.id.tvItemName);
            holder.tvPrice=(TextView)vi.findViewById(R.id.tvPrice);
            holder.btnOrder=(Button)vi.findViewById(R.id.btnOrder);

            vi.setTag(holder);
        }else{
            vi=convertView;
            holder=(ViewHolder) vi.getTag();
        }

        if(lstItemData.get(position).getItemImage() != null){
            bitmap = BitmapFactory.decodeByteArray(lstItemData.get(position).getItemImage(), 0, lstItemData.get(position).getItemImage().length);
            if (bitmap != null) holder.ivItemImage.setImageBitmap(bitmap);
        }else holder.ivItemImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_default_image));

        holder.tvItemName.setText(lstItemData.get(position).getItemName());
        holder.tvPrice.setText(String.valueOf(lstItemData.get(position).getPrice()));

        holder.btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemImageButtonClickListener !=null){
                    itemImageButtonClickListener.onOrderClickListener(position);
                }
            }
        });

        return vi;
    }
}
