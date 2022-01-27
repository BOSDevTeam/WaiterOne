package adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bosictsolution.waiterone.R;

import java.util.List;

import common.DBHelper;
import common.FeatureList;
import data.TransactionData;
import listener.OrderButtonClickListener;

/**
 * Created by NweYiAung on 14-02-2017.
 */
public class OrderListAdapter extends BaseAdapter {

    DBHelper db;
    private Context context;
    OrderButtonClickListener orderButtonClickListener;
    List<TransactionData> lstTransactionData;
    float floatQty;
    Bitmap bitmap;

    public OrderListAdapter(Context context,List<TransactionData> lstTransactionData){
        db=new DBHelper(context);
        this.context=context;
        this.lstTransactionData=lstTransactionData;
    }

    public void setOnOrderButtonClickListener(OrderButtonClickListener orderButtonClickListener){
        this.orderButtonClickListener=orderButtonClickListener;
    }

    @Override
    public int getCount(){
        return lstTransactionData.size();
    }

    @Override
    public String getItem(int position){
        return lstTransactionData.get(position).getItemName();
    }

    @Override
    public long getItemId(int position){
        return 0;
    }

    static class ViewHolder {
        TextView tvItemName,tvTaste,tvTime,tvItemSub;
        EditText etQuantity;
        ImageButton imgbtnPlus,imgbtnMinus,imgbtnCancel,imgbtnCalculator,imgbtnTaste;
        View row;
        Button btnPrice;
        ImageView ivItemImage;
    }

    @Override
    public View getView(final int position,View convertView,ViewGroup parent){
        View row;
        final ViewHolder holder;
        if(convertView==null){
            LayoutInflater layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row=layoutInflater.inflate(R.layout.list_order, parent,false);
            holder=new ViewHolder();
            holder.tvItemName=(TextView) row.findViewById(R.id.tvItemName);
            holder.tvItemSub=(TextView) row.findViewById(R.id.tvItemSub);
            holder.tvTaste=(TextView) row.findViewById(R.id.tvTaste);
            holder.tvTime=(TextView) row.findViewById(R.id.tvTime);
            holder.etQuantity=(EditText) row.findViewById(R.id.etQuantity);
            holder.imgbtnPlus=(ImageButton) row.findViewById(R.id.imgbtnPlus);
            holder.imgbtnMinus=(ImageButton) row.findViewById(R.id.imgbtnMinus);
            holder.imgbtnCalculator=(ImageButton) row.findViewById(R.id.imgbtnCalculator);
            holder.imgbtnCancel=(ImageButton) row.findViewById(R.id.imgbtnCancel);
            holder.imgbtnTaste=(ImageButton) row.findViewById(R.id.imgbtnTaste);
            holder.btnPrice=(Button)row.findViewById(R.id.btnPrice);
            holder.ivItemImage=(ImageView) row.findViewById(R.id.ivItemImage);

            if(db.getFeatureResult(FeatureList.fUseItemImage)==1)holder.ivItemImage.setVisibility(View.VISIBLE);

            row.setTag(holder);
        }
        else{
            row=convertView;
            holder=(ViewHolder) row.getTag();
        }

        holder.tvItemName.setText(lstTransactionData.get(position).getItemName());

        if(lstTransactionData.get(position).getAllItemSub().length()!=0) {
            holder.tvItemSub.setVisibility(View.VISIBLE);
            holder.tvItemSub.setText(lstTransactionData.get(position).getAllItemSub());
        }
        else holder.tvItemSub.setVisibility(View.GONE);

        holder.tvTaste.setText(lstTransactionData.get(position).getTaste());

        if(db.getFeatureResult(FeatureList.fOrderTime)==1) {
            holder.tvTime.setVisibility(View.VISIBLE);
            holder.tvTime.setText(lstTransactionData.get(position).getOrderTime());
        }else{
            holder.tvTime.setVisibility(View.GONE);
        }
        holder.btnPrice.setText(String.valueOf(lstTransactionData.get(position).getSalePrice()));
        floatQty = Float.parseFloat(lstTransactionData.get(position).getStringQty());
        if(floatQty==Math.round(floatQty)){
            holder.etQuantity.setText(String.valueOf(lstTransactionData.get(position).getIntegerQty()));
        }else{
            holder.etQuantity.setText(String.valueOf(lstTransactionData.get(position).getFloatQty()));
        }

        if(lstTransactionData.get(position).getItemImage() != null){
            bitmap = BitmapFactory.decodeByteArray(lstTransactionData.get(position).getItemImage(), 0, lstTransactionData.get(position).getItemImage().length);
            if (bitmap != null) holder.ivItemImage.setImageBitmap(bitmap);
        }else holder.ivItemImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_default_image));

        holder.imgbtnPlus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(orderButtonClickListener !=null){
                    orderButtonClickListener.onPlusButtonClickListener(position, holder.etQuantity);
                }
            }
        });

        holder.imgbtnMinus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(orderButtonClickListener !=null){
                    orderButtonClickListener.onMinusButtonClickListener(position, holder.etQuantity);
                }
            }
        });

        holder.imgbtnCalculator.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(orderButtonClickListener !=null){
                    orderButtonClickListener.onCalculatorButtonClickListener(position,holder.etQuantity);
                }
            }
        });

        holder.imgbtnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(orderButtonClickListener !=null){
                    orderButtonClickListener.onCancelButtonClickListener(position,holder.row);
                }
            }
        });

        holder.imgbtnTaste.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(orderButtonClickListener !=null){
                    orderButtonClickListener.onTasteButtonClickListener(position, holder.tvTaste);
                }
            }
        });

        holder.btnPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(orderButtonClickListener !=null){
                    orderButtonClickListener.onPriceButtonClickListener(position, holder.btnPrice);
                }
            }
        });

        return row;
    }
}
