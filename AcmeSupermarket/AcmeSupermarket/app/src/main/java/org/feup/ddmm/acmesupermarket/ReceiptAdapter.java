package org.feup.ddmm.acmesupermarket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ReceiptAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private ArrayList<Receipt> receipts; // header titles
    private ExpandableListView exp;

    public ReceiptAdapter(Context context, ArrayList<Receipt> listDataHeader, ExpandableListView exp){
        this.mContext = context;
        this.receipts = listDataHeader;
        this.exp = exp;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.receipts.get(groupPosition).getProducts().get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final String productName = this.receipts.get(groupPosition).getProducts().get(childPosition).getName();
        final String productPrice = this.receipts.get(groupPosition).getProducts().get(childPosition).getPrice() + "€";

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.receipt_view, null);
        }

        if (childPosition == this.receipts.get(groupPosition).getProducts().size() - 1)
            exp.setDividerHeight(30);
         else
            exp.setDividerHeight(0);

        TextView productNameView = (TextView) convertView.findViewById(R.id.product_name_receipt);
        TextView productPriceView = (TextView) convertView.findViewById(R.id.product_price_receipt);

        productNameView.setText(productName);
        productPriceView.setText(productPrice);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.receipts.get(groupPosition).getProducts().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.receipts.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.receipts.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String purchaseDate = formatter.format(this.receipts.get(groupPosition).getPurchaseDate());
        String totalPrice = this.receipts.get(groupPosition).getTotalPrice() + "€";

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.expandable_receipt_view, null);
        }

        if (isExpanded)
            exp.setDividerHeight(0);
        else
            exp.setDividerHeight(30);

        TextView purchaseDateView = (TextView) convertView.findViewById(R.id.purchase_date);
        purchaseDateView.setText(purchaseDate);

        TextView totalPriceView = (TextView) convertView.findViewById(R.id.total_price_receipts);
        totalPriceView.setText(totalPrice);

        /*TextView clickForMoreTextView = (TextView) convertView.findViewById(R.id.click_for_more);

        exp.setOnGroupClickListener((parent1, v, groupPosition1, id) ->  {
            System.out.println(groupPosition1);
            if (groupPosition == groupPosition1)
                clickForMoreTextView.setVisibility((clickForMoreTextView.getVisibility() == v.VISIBLE) ? v.GONE : v.VISIBLE);
            return false;
        });*/

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
