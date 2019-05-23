package com.cube9.gmarket.Category.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cube9.gmarket.Category.Activity.CategoryDetailsActivity;
import com.cube9.gmarket.Category.ModelClass.SubCategoryListPojo;
import com.cube9.gmarket.Products.Activity.ProductListActivity;
import com.cube9.gmarket.R;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.MyViewHolder> {
    private Context context;
    List<SubCategoryListPojo> categoryListPojoList;
    public SubCategoryAdapter(Context context, List<SubCategoryListPojo> categoryListPojoList) {
        this.context = context;
        this.categoryListPojoList=categoryListPojoList;

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView iv_cat_image;
        TextView tv_cat_name;
        CardView rl_main;

        public MyViewHolder(View v) {
            super(v);
            iv_cat_image = (ImageView) v.findViewById(R.id.iv_cat_image);
            tv_cat_name = (TextView) v.findViewById(R.id.tv_cat_name);
            rl_main = (CardView) v.findViewById(R.id.rl_main);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_categories, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final SubCategoryListPojo items=categoryListPojoList.get(position);
        holder.tv_cat_name.setText(items.getCategoryName());

        //set drawable to imageview
       /* Glide.with(context).load(items.getCategoryImage())
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.iv_cat_image);*/

        Picasso.with(context).load(items.getCategoryImage()).placeholder(context.getResources().getDrawable(R.drawable.ic_g_market_logo)).networkPolicy(NetworkPolicy.NO_CACHE).into(holder.iv_cat_image);

        holder.rl_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (items.getSub_cat_length()>0)
                {
                    Intent i=new Intent(context,CategoryDetailsActivity.class);
                    i.putExtra("sub_cat_array",items.getSub_cats().toString());
                    i.putExtra("cat_name",items.getCategoryName());
                    context.startActivity(i);
                }
                else
                {
                    Intent i=new Intent(context,ProductListActivity.class);
                    i.putExtra("cat_id",items.getCategoryId());
                    i.putExtra("cat_name",items.getCategoryName());
                    context.startActivity(i);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryListPojoList.size();
    }
}
