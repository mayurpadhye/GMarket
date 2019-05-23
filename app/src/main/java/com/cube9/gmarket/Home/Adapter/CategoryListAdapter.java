package com.cube9.gmarket.Home.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cube9.gmarket.Home.ModelClass.CategoryListPojo;
import com.cube9.gmarket.Products.Activity.ProductListActivity;
import com.cube9.gmarket.R;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CategoryListAdapter extends BaseAdapter {
    private Context context;
   List<CategoryListPojo> categoryListPojoList;

    public CategoryListAdapter(Context context, List<CategoryListPojo> categoryListPojoList) {
        this.context = context;
        this.categoryListPojoList=categoryListPojoList;

    }

    @Override
    public int getCount() {
        return categoryListPojoList.size();
    }

    @Override
    public CategoryListPojo getItem(int position) {
        return categoryListPojoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("CheckResult")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        // If holder not exist then locate all view from UI file.
        if (convertView == null) {
            // inflate UI from XML file
            convertView = inflater.inflate(R.layout.row_categories, parent, false);
            // get all UI view
            holder = new ViewHolder(convertView);
            // set tag for holder
            convertView.setTag(holder);
        } else {
            // if holder created, get tag from view
            holder = (ViewHolder) convertView.getTag();
        }
        final CategoryListPojo items=categoryListPojoList.get(position);
        holder.tv_cat_name.setText(items.getCategoryName());

        //set drawable to imageview
       /* Glide.with(context).load(items.getCategoryImage())
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.iv_cat_image);*/

        Picasso.with(context).load(items.getCategoryImage()).networkPolicy(NetworkPolicy.NO_CACHE).into(holder.iv_cat_image);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent i=new Intent(context,ProductListActivity.class);
               i.putExtra("cat_id",items.getCategoryId());
               i.putExtra("cat_name",items.getCategoryName());
               context.startActivity(i);
            }
        });

        return convertView;
    }

    private class ViewHolder {

        private ImageView iv_cat_image;
        TextView tv_cat_name;

        public ViewHolder(View v) {
            iv_cat_image = (ImageView) v.findViewById(R.id.iv_cat_image);
            tv_cat_name = (TextView) v.findViewById(R.id.tv_cat_name);
        }
    }
}
