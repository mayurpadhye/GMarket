package com.cube9.gmarket.Category.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cube9.gmarket.Category.ModelClass.CatProductsPojo;
import com.cube9.gmarket.Products.Activity.ProductDetailsNewActivity;
import com.cube9.gmarket.R;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CategoryProductsAdapter extends BaseAdapter {
    List<CatProductsPojo> catProductsPojoList;
    Context context;

    public CategoryProductsAdapter(List<CatProductsPojo> catProductsPojoList, Context context) {
        this.catProductsPojoList = catProductsPojoList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return catProductsPojoList.size();
    }

    @Override
    public CatProductsPojo getItem(int position) {
        return catProductsPojoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        // If holder not exist then locate all view from UI file.
        if (convertView == null) {
            // inflate UI from XML file
            convertView = inflater.inflate(R.layout.row_cat_products, parent, false);
            // get all UI view
            holder = new ViewHolder(convertView);
            // set tag for holder
            convertView.setTag(holder);
        } else {
            // if holder created, get tag from view
            holder = (ViewHolder) convertView.getTag();
        }
        final CatProductsPojo items=catProductsPojoList.get(position);
        holder.tv_product_name.setText(items.getProduct_name());
        holder.tv_original_price.setText(items.getProduct_original_price());
        holder.tv_discount.setText(items.getProduct_discount());
        holder.tv_discounted_price.setText(items.getProduct_price());
        //set drawable to imageview
       /* Glide.with(context)
                .load(items.getProduct_image())
                .apply(new RequestOptions())
                .into(holder.iv_product);*/

      /*  Glide.with(context).load(items.getProduct_image())
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.iv_product);*/
        Picasso.with(context).load(items.getProduct_image()).networkPolicy(NetworkPolicy.NO_CACHE).into(holder.iv_product);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(context, ProductDetailsNewActivity.class);
                i.putExtra("product_id",items.getProduct_id());
                i.putExtra("product_name",items.getProduct_name());
                i.putExtra("category_id",items.getCat_id());
                Toast.makeText(context, ""+items.getCat_id(), Toast.LENGTH_SHORT).show();
                context.startActivity(i);

            }
        });

        return convertView;
    }
    private class ViewHolder {

        private ImageView iv_product;
        TextView tv_product_name,tv_discounted_price,tv_original_price,tv_discount;

        public ViewHolder(View v) {
            iv_product = (ImageView) v.findViewById(R.id.iv_product);
            tv_product_name = (TextView) v.findViewById(R.id.tv_product_name);
            tv_discounted_price = (TextView) v.findViewById(R.id.tv_discounted_price);
            tv_original_price = (TextView) v.findViewById(R.id.tv_original_price);
            tv_discount = (TextView) v.findViewById(R.id.tv_discount);
        }
    }
}
