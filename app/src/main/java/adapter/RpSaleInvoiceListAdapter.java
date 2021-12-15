package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bosictsolution.waiterone.R;

import java.util.List;

import data.RpSaleInvoiceData;

/**
 * Created by NweYiAung on 20-01-2017.
 */
public class RpSaleInvoiceListAdapter extends BaseAdapter {

    private Context context;
    private List<RpSaleInvoiceData> lstRpSaleInvoiceData;

    public RpSaleInvoiceListAdapter(Context context, List<RpSaleInvoiceData> lstRpSaleInvoiceData){
        this.context=context;
        this.lstRpSaleInvoiceData = lstRpSaleInvoiceData;
    }

    public int getCount(){
        return lstRpSaleInvoiceData.size();
    }

    public Object getItem(int position){
        return position;
    }

    public long getItemId(int position){
        return position;
    }

    static class ReportSaleInvoiceHolder {
        TextView tvListItemName, tvListQty,tvListPrice, tvListAmount,tvListSubTotal,tvListTax,tvListCharges,tvListDiscount,tvListGrandTotal,tvListVoucher,tvListWaiter,tvListTable,tvListDate;
    }

    public View getView(final int position, View convertView, ViewGroup parent){
        View vi=convertView;
        ReportSaleInvoiceHolder holder;
        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vi=inflater.inflate(R.layout.list_rp_sale_invoice, null);

            holder=new ReportSaleInvoiceHolder();
            holder.tvListItemName=(TextView)vi.findViewById(R.id.tvListItemName);
            holder.tvListQty =(TextView)vi.findViewById(R.id.tvListQty);
            holder.tvListPrice =(TextView)vi.findViewById(R.id.tvListPrice);
            holder.tvListAmount =(TextView)vi.findViewById(R.id.tvListAmount);
            holder.tvListSubTotal=(TextView)vi.findViewById(R.id.tvListSubTotal);
            holder.tvListTax=(TextView)vi.findViewById(R.id.tvListTax);
            holder.tvListCharges=(TextView)vi.findViewById(R.id.tvListCharges);
            holder.tvListDiscount=(TextView)vi.findViewById(R.id.tvListDiscount);
            holder.tvListGrandTotal=(TextView)vi.findViewById(R.id.tvListGrandTotal);
            holder.tvListVoucher=(TextView)vi.findViewById(R.id.tvListVoucher);
            holder.tvListWaiter=(TextView)vi.findViewById(R.id.tvListWaiter);
            holder.tvListTable=(TextView)vi.findViewById(R.id.tvListTable);
            holder.tvListDate=(TextView)vi.findViewById(R.id.tvListDate);

            vi.setTag(holder);
        }else{
            holder=(ReportSaleInvoiceHolder)vi.getTag();
        }

        if(lstRpSaleInvoiceData.get(position).getSubtotal()==1) holder.tvListSubTotal.setVisibility(View.GONE);
        else {
            holder.tvListSubTotal.setVisibility(View.VISIBLE);
            holder.tvListSubTotal.setText("Sub Total: "+String.valueOf(lstRpSaleInvoiceData.get(position).getSubtotal()));
        }
        if(lstRpSaleInvoiceData.get(position).getTax()==1) holder.tvListTax.setVisibility(View.GONE);
        else {
            holder.tvListTax.setVisibility(View.VISIBLE);
            holder.tvListTax.setText("Tax: "+String.valueOf(lstRpSaleInvoiceData.get(position).getTax()));
        }
        if(lstRpSaleInvoiceData.get(position).getCharges()==1) holder.tvListCharges.setVisibility(View.GONE);
        else {
            holder.tvListCharges.setVisibility(View.VISIBLE);
            holder.tvListCharges.setText("Charges: "+String.valueOf(lstRpSaleInvoiceData.get(position).getCharges()));
        }
        if(lstRpSaleInvoiceData.get(position).getDiscount()==1) holder.tvListDiscount.setVisibility(View.GONE);
        else {
            holder.tvListDiscount.setVisibility(View.VISIBLE);
            holder.tvListDiscount.setText("Discount: "+String.valueOf(lstRpSaleInvoiceData.get(position).getDiscount()));
        }
        if(lstRpSaleInvoiceData.get(position).getGrandtotal()==1) holder.tvListGrandTotal.setVisibility(View.GONE);
        else {
            holder.tvListGrandTotal.setVisibility(View.VISIBLE);
            holder.tvListGrandTotal.setText("Grand Total: "+String.valueOf(lstRpSaleInvoiceData.get(position).getGrandtotal()));
        }

        if(lstRpSaleInvoiceData.get(position).getVoucher().equals("")) holder.tvListVoucher.setVisibility(View.GONE);
        else {
            holder.tvListVoucher.setVisibility(View.VISIBLE);
            holder.tvListVoucher.setText("Voucher: "+ lstRpSaleInvoiceData.get(position).getVoucher());
        }
        if(lstRpSaleInvoiceData.get(position).getDate().equals("")) holder.tvListDate.setVisibility(View.GONE);
        else {
            holder.tvListDate.setVisibility(View.VISIBLE);
            holder.tvListDate.setText(lstRpSaleInvoiceData.get(position).getDate());
        }
        if(lstRpSaleInvoiceData.get(position).getWaiter().equals("")) holder.tvListWaiter.setVisibility(View.GONE);
        else {
            holder.tvListWaiter.setVisibility(View.VISIBLE);
            holder.tvListWaiter.setText(lstRpSaleInvoiceData.get(position).getWaiter());
        }
        if(lstRpSaleInvoiceData.get(position).getTable().equals("")) holder.tvListTable.setVisibility(View.GONE);
        else {
            holder.tvListTable.setVisibility(View.VISIBLE);
            holder.tvListTable.setText(lstRpSaleInvoiceData.get(position).getTable());
        }

        holder.tvListItemName.setText(lstRpSaleInvoiceData.get(position).getItemName());
        holder.tvListQty.setText(String.valueOf(lstRpSaleInvoiceData.get(position).getQty()));
        holder.tvListPrice.setText(String.valueOf(lstRpSaleInvoiceData.get(position).getPrice()));
        holder.tvListAmount.setText(String.valueOf(lstRpSaleInvoiceData.get(position).getAmount()));

        return vi;
    }
}
