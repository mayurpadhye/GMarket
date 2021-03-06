package com.cube9.gmarket.Home.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cube9.gmarket.HelperClass.CustomUtils;
import com.cube9.gmarket.HelperClass.GMarketApplication;
import com.cube9.gmarket.HelperClass.SharedPrefManager;
import com.cube9.gmarket.Home.Fragments.HomeFragmentNew;
import com.cube9.gmarket.Home.Fragments.NewHomeFragment;
import com.cube9.gmarket.Home.ModelClass.AllTopOffers;
import com.cube9.gmarket.Home.ModelClass.LatestProductMenPojo;
import com.cube9.gmarket.Login.Activity.LoginDetailsActivity;
import com.cube9.gmarket.Products.Activity.ProductDetailsNewActivity;
import com.cube9.gmarket.R;
import com.cube9.gmarket.WebUrls.WebUrls;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllTopOfferAdapter  extends RecyclerView.Adapter<AllTopOfferAdapter.MyViewHolder> {

    private List<AllTopOffers> allTopOffersList;
    NewHomeFragment homeFragmentNew;
    Context context;
    ProgressBar p_bar;

    public AllTopOfferAdapter(List<AllTopOffers> allTopOffersList, Context context, ProgressBar p_bar, NewHomeFragment homeFragmentNew) {
        this.allTopOffersList = allTopOffersList;
        this.context = context;
        this.p_bar = p_bar;
        this.homeFragmentNew=homeFragmentNew;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_latest_products, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final AllTopOffers items=allTopOffersList.get(position);
        holder.tv_product_name.setText(items.getProduct_name());
        // holder.tv_original_price.setText(items.getProduct_original_price());
        holder.tv_discount.setText(items.getProduct_discount());
        holder.tv_discounted_pice.setText(items.getProduct_price());
        if (allTopOffersList.get(position).getWishlist_flag().equals("1"))
        {
            holder.spark_button.setChecked(true);
        }
        else
        {
            holder.spark_button.setChecked(false);
        }

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

            //holder.tv_original_price.setText("FCFA "+new DecimalFormat("##.##").format(Double.parseDouble(items.getProduct_price())));
            holder.tv_original_price.setText(items.getProduct_price()+" FCFA");
        }
        if ( !items.getProduct_special_price().equals("null"))
        {
            // holder.tv_discounted_pice.setText("FCFA "+new DecimalFormat("##.##").format(Double.parseDouble(items.getProduct_special_price())));
            holder.tv_discounted_pice.setText(items.getProduct_special_price()+" FCFA");
            holder.tv_original_price.setPaintFlags( holder.tv_original_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        }
        if (items.getRating().equals(""))
        {
            allTopOffersList.get(position).setRating("0");
        }
        if (!items.getRating().equals("null"))
            holder.ratingBar.setRating(Float.parseFloat(items.getRating()));
        holder.btn_add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(context, ProductDetailsNewActivity.class);
                i.putExtra("product_id",items.getProduct_id());
                i.putExtra("product_name",items.getProduct_name());

                context.startActivity(i);

            }});
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

        Picasso.with(context).load(items.getProduct_image()).noFade().resize(150, 150).placeholder(context.getResources().getDrawable(R.drawable.ic_g_market_logo)).into(holder.iv_product_image);


        holder.ll_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(context, ProductDetailsNewActivity.class);
                i.putExtra("product_id",items.getProduct_id());
                i.putExtra("product_name",items.getProduct_name());
                i.putExtra("category_id","1");

                context.startActivity(i);

            }
        });
    }



    @Override
    public int getItemCount() {
        return allTopOffersList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_product_image;
        Button btn_add_to_cart;
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
            btn_add_to_cart=(Button)view.findViewById(R.id.btn_add_to_cart);
            ratingBar=(RatingBar) view.findViewById(R.id.ratingBar);
        }
    }
}
