package com.cube9.gmarket.Home.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.cube9.gmarket.HelperClass.SharedPrefManager;
import com.cube9.gmarket.Home.ModelClass.BestSellerPojo;
import com.cube9.gmarket.Home.ModelClass.MostViewedMenPojo;
import com.cube9.gmarket.Login.Activity.LoginDetailsActivity;
import com.cube9.gmarket.Products.Activity.ProductDetailsNewActivity;
import com.cube9.gmarket.R;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;

import java.util.List;

public class MostViewedMenAdapter extends RecyclerView.Adapter<MostViewedMenAdapter.MyViewHolder> {

    List<MostViewedMenPojo> mostViewedMenPojoList;
    Context context;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_product_image;
        TextView tv_product_name,tv_discounted_pice,tv_original_price,tv_discount,tv_rating,tv_no_cost_emi;
        SparkButton spark_button;
        LinearLayout ll_product;
        RatingBar ratingBar;
        public MyViewHolder(View view) {
            super(view);
            iv_product_image = (ImageView) view.findViewById(R.id.iv_product_image);
            tv_product_name = (TextView) view.findViewById(R.id.tv_product_name);
            tv_discounted_pice = (TextView) view.findViewById(R.id.tv_discounted_pice);
            tv_original_price = (TextView) view.findViewById(R.id.tv_original_price);
            tv_discount = (TextView) view.findViewById(R.id.tv_discount);
            tv_rating = (TextView) view.findViewById(R.id.tv_rating);
            tv_no_cost_emi = (TextView) view.findViewById(R.id.tv_no_cost_emi);
            spark_button = (SparkButton) view.findViewById(R.id.spark_button);
            ll_product = (LinearLayout) view.findViewById(R.id.ll_product);
            ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
        }
    }


    public MostViewedMenAdapter(List<MostViewedMenPojo> mostViewedMenPojoList, Context context) {
        this.mostViewedMenPojoList = mostViewedMenPojoList;
        this.context=context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_featured_products_list, parent, false);
        itemView.getLayoutParams().width = (int) (getScreenWidth() /2.0);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final MostViewedMenPojo items=mostViewedMenPojoList.get(position);

        holder.tv_product_name.setText(items.getProduct_name());

        if (items.getRating().equals("") || items.getRating().equals("null")|| items.getRating().equals(null))
        {
            mostViewedMenPojoList.get(position).setRating("0");
        }
        holder.ratingBar.setRating(Float.parseFloat(items.getRating()));
        holder.tv_original_price.setText(items.getProduct_price()+" FCFA");
        //  holder.tv_discount.setText(items.getProduct_discount());
        holder.tv_discounted_pice.setText(items.getProduct_price());
       /* if (featuredProductPojoList.get(position).getWishlist_flag().equals("1"))
        {
            holder.spark_button.setChecked(true);
        }
        else
        {
            holder.spark_button.setChecked(false);
        }*/

        if (items.getProduct_special_price().equals("null"))
        {
            holder.tv_discounted_pice.setVisibility(View.GONE);
        }
        if (items.getProduct_discount().equals("null"))
        {
            holder.tv_discount.setVisibility(View.GONE);
        }
        if (!items.getProduct_price().equals("null"))
        {

            holder.tv_original_price.setText(items.getProduct_price()+" FCFA");
            //  holder.tv_original_price.setText("FCFA "+new DecimalFormat("##.##").format(Double.parseDouble(items.getProduct_price())));

        }
        if ( !items.getProduct_special_price().equals("null"))
        {
            holder.tv_discounted_pice.setText(items.getProduct_special_price()+" FCFA");
            //  holder.tv_discounted_pice.setText("FCFA "+new DecimalFormat("##.##").format(Double.parseDouble(items.getProduct_special_price())));
            holder.tv_original_price.setPaintFlags( holder.tv_original_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        }
        //set drawable to imageview
        /*Glide.with(context)
                .load(items.getProduct_image())
                .apply(new RequestOptions())
                .into(holder.iv_product_image);*/

       /* Glide.with(context).load(items.getProduct_image())
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.iv_product_image);*/

        Picasso.with(context).load(items.getProduct_image()).networkPolicy(NetworkPolicy.NO_CACHE).into(holder.iv_product_image);

        holder.spark_button.setEventListener(new SparkEventListener(){
            @Override
            public void onEvent(ImageView button, boolean buttonState) {

                if (!new SharedPrefManager(context).IsLogin())
                {
                    Intent i = new Intent(context, LoginDetailsActivity.class);
                    i.putExtra("intent_from","wishlist");
                    i.putExtra("product_id",items.getProduct_id());
                    ((Activity) context).startActivityForResult(i, 1);

                }
                else
                {
                    if (buttonState) {
                        //  AddToWishList(items.getProduct_id(),items,position,holder.spark_button);
                    } else {
                        // Button is inactive
                        //  RemoveFromWishList(items.getProduct_id(),items,position,holder.spark_button);
                    }
                }

            }

            @Override
            public void onEventAnimationEnd(ImageView button, boolean buttonState) {

            }

            @Override
            public void onEventAnimationStart(ImageView button, boolean buttonState) {

            }
        });
        holder.ll_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(context, ProductDetailsNewActivity.class);
                i.putExtra("product_id",items.getProduct_id());
                i.putExtra("product_name",items.getProduct_name());
                i.putExtra("category_id",items.getCategory_id());
                context.startActivity(i);

            }
        });



    }

    @Override
    public int getItemCount() {
        return mostViewedMenPojoList.size();
    }


    public int getScreenWidth() {

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        return size.x;
    }
}
