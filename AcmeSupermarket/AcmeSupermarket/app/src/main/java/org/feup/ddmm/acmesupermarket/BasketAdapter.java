package org.feup.ddmm.acmesupermarket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class BasketAdapter extends ArrayAdapter<Product> {

    private LayoutInflater mInflater;
    private ArrayList<Product> products;
    private int mViewResourceId;

    public BasketAdapter(Context context, int textViewResourceId, ArrayList<Product> products) {
        super(context, textViewResourceId, products);
        this.products = products;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewResourceId = textViewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(mViewResourceId, null);

        Product product = products.get(position);

        if (product != null) {
            TextView name = (TextView) convertView.findViewById(R.id.product_name);
            TextView price = (TextView) convertView.findViewById(R.id.product_price);
            TextView quantity = (TextView) convertView.findViewById(R.id.product_quantity);

            if (name != null)
                name.setText(product.getName());
            if (price != null)
                price.setText(product.getTotalPrice() + "€");
            if (quantity != null)
                quantity.setText(Integer.toString(product.getQuantity() + 1));

        }

        return convertView;
    }

}
