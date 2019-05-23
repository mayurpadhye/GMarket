package com.cube9.gmarket.Home.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.cube9.gmarket.Home.ModelClass.BestSellerPojo;
import com.cube9.gmarket.Products.Activity.ProductDetailsNewActivity;
import com.cube9.gmarket.R;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BestSellerAdapter extends RecyclerView.Adapter<BestSellerAdapter.MyViewHolder>{

    private List<BestSellerPojo> bestSellerPojoList;
    Context context;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, tv_product_name, tv_shop_now;
        ImageView iv_product_image;
        public MyViewHolder(View view) {
            super(view);
            iv_product_image = (ImageView) view.findViewById(R.id.iv_product_image);
            tv_product_name = (TextView) view.findViewById(R.id.tv_product_name);
            tv_shop_now = (TextView) view.findViewById(R.id.tv_shop_now);
        }
    }


    public BestSellerAdapter(List<BestSellerPojo> bestSellerPojoList,Context context) {
        this.bestSellerPojoList = bestSellerPojoList;
        this.context=context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_best_seller, parent, false);
        itemView.getLayoutParams().width = (int) (getScreenWidth() /3.5);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final BestSellerPojo items=bestSellerPojoList.get(position);
        holder.tv_product_name.setText(items.getProduct_name());
        //Glide.with(context).load(items.getProduct_image()).into(holder.iv_product_image);

       /* Glide.with(context).load(items.getProduct_image())
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.iv_product_image);*/
        Picasso.with(context).load(items.getProduct_image()).networkPolicy(NetworkPolicy.NO_CACHE).into(holder.iv_product_image);
        holder.iv_product_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(context, ProductDetailsNewActivity.class);
                i.putExtra("product_id",items.getProduct_id());
                i.putExtra("category_id",items.getCategory_id());
                context.startActivity(i);
            }
        });
        holder.tv_shop_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(context, ProductDetailsNewActivity.class);
                i.putExtra("product_id",items.getProduct_id());
                i.putExtra("category_id",items.getCategory_id());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bestSellerPojoList.size();
    }


    public int getScreenWidth() {

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        return size.x;
    }
}
