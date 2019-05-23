package com.cube9.gmarket.Delivery.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.cube9.gmarket.Delivery.ModelClass.CartProductPojo;
import com.cube9.gmarket.R;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

public class DeliveryProductAdapter extends BaseAdapter {
    Context context;
    List<CartProductPojo> cartProductPojoList;
    private final String[] quantityValues = new String[] { "1", "2", "3", "4",
            "5" };
    public DeliveryProductAdapter(Context context, List<CartProductPojo> cartProductPojoList) {
        this.context = context;
        this.cartProductPojoList = cartProductPojoList;
    }

    @Override
    public int getCount() {
        return cartProductPojoList.size();
    }

    @Override
    public CartProductPojo getItem(int position) {
        return cartProductPojoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return cartProductPojoList.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        // If holder not exist then locate all view from UI file.
        if (convertView == null) {
            // inflate UI from XML file
            convertView = inflater.inflate(R.layout.order_items, parent, false);
            // get all UI view
            holder = new ViewHolder(convertView);
            // set tag for holder
            convertView.setTag(holder);
        } else {
            // if holder created, get tag from view
            holder = (ViewHolder) convertView.getTag();
        }
        final CartProductPojo items=cartProductPojoList.get(position);
        holder.tv_product_name.setText(items.getProduct_name());
       /* Glide.with(context)
                .load(items.getProduct_image())
                .apply(new RequestOptions())
                .into(holder.product_image);*/


     /*   Glide.with(context).load(items.getProduct_image())
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.product_image);*/
        Picasso.with(context).load(items.getProduct_image()).networkPolicy(NetworkPolicy.NO_CACHE).into(holder.product_image);

        holder.tv_price.setText(items.getProduct_price());
        holder.tv_qty.setText("Qty: "+items.getSelected_quantity());
        if (!items.getProduct_price().equals("null"))
        {
            String Product_price = items.getProduct_price().replaceAll(",","");
            holder.tv_price.setText(Product_price+" FCFA");
          //  holder.tv_price.setText("FCFA "+new DecimalFormat("##.##").format(Double.parseDouble(Product_price)));

        }
        if ( !items.getProduct_special_price().equals("null"))
        {
            String Product_special_price = items.getProduct_special_price().replaceAll(",","");
            holder.tv_discounted_pice.setText(Product_special_price+" FCFA");
          //  holder.tv_discounted_pice.setText("FCFA "+new DecimalFormat("##.##").format(Double.parseDouble(Product_special_price)));
            holder.tv_price.setPaintFlags( holder.tv_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        if (items.getProduct_special_price().equals("null"))
        {
            holder.tv_discounted_pice.setVisibility(View.GONE);
        }
        if (items.getProduct_discount().equals("null"))
        {
            holder.tv_discount.setVisibility(View.GONE);
        }

        ArrayAdapter<String> quantityAdapter = new ArrayAdapter<String>(
                context, android.R.layout.simple_spinner_item,
                quantityValues);
        quantityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.sp_qty.setAdapter(quantityAdapter);
        holder.sp_qty
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> arg0,
                                               View arg1, int arg2, long arg3) {
                        cartProductPojoList.get(position).setSelected_quantity(
                                arg0.getSelectedItem().toString());
                        holder.tv_qty.setText("QTY "+cartProductPojoList.get(position).getSelected_quantity());
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {

                    }
                });
        return convertView;
    }
    private class ViewHolder {

        private ImageView product_image;
        TextView tv_product_name,tv_price,tv_product_group,tv_seller,tv_qty,tv_discounted_pice,tv_discount;
        Spinner sp_qty;

        public ViewHolder(View v) {
            product_image = (ImageView) v.findViewById(R.id.product_image);
            tv_product_name = (TextView) v.findViewById(R.id.tv_product_name);
            tv_price = (TextView) v.findViewById(R.id.tv_price);
            tv_discounted_pice = (TextView) v.findViewById(R.id.tv_discounted_pice);
            tv_discount = (TextView) v.findViewById(R.id.tv_discount);
            tv_product_group = (TextView) v.findViewById(R.id.tv_product_group);
            tv_qty = (TextView) v.findViewById(R.id.tv_qty);
            tv_seller = (TextView) v.findViewById(R.id.tv_seller);
            sp_qty = (Spinner) v.findViewById(R.id.sp_qty);
        }
    }
}
