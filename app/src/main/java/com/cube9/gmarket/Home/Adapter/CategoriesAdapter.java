package com.cube9.gmarket.Home.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.cube9.gmarket.Category.Activity.CategoryDetailsActivity;
import com.cube9.gmarket.Category.Activity.SubCategoryDetailsActivity;
import com.cube9.gmarket.Category.Fragment.SubCategoryFragment;
import com.cube9.gmarket.Home.Activities.HomeActivity;
import com.cube9.gmarket.Home.Fragments.NewHomeFragment;
import com.cube9.gmarket.Home.ModelClass.CategoryListPojo;
import com.cube9.gmarket.Products.Activity.ProductListActivity;
import com.cube9.gmarket.R;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.MyViewHolder> {
    private Context context;
    List<CategoryListPojo> categoryListPojoList;
    NewHomeFragment newHomeFragment;
    public CategoriesAdapter(Context context, List<CategoryListPojo> categoryListPojoList,NewHomeFragment newHomeFragment) {
        this.context = context;
        this.categoryListPojoList=categoryListPojoList;
        this.newHomeFragment=newHomeFragment;

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
    public CategoriesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_categories, parent, false);


        return new CategoriesAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final CategoryListPojo items=categoryListPojoList.get(position);
        holder.tv_cat_name.setText(items.getCategoryName());

        //set drawable to imageview
       /* Glide.with(context).load(items.getCategoryImage())
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.iv_cat_image);*/

        Picasso.with(context).load(items.getCategoryImage()).noFade().placeholder(context.getResources().getDrawable(R.drawable.ic_g_market_logo)).networkPolicy(NetworkPolicy.NO_CACHE).into(holder.iv_cat_image);

        holder.rl_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (items.getSub_cats().equals("1"))
                {
                    Intent i = new Intent(context, SubCategoryDetailsActivity.class);
                    i.putExtra("cat_id", items.getCategoryId());
                    i.putExtra("cat_name", items.getCategoryName());
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
    public int getScreenWidth() {

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        return size.x;
    }
}
