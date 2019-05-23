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
import com.cube9.gmarket.Home.Fragments.NewHomeFragment;
import com.cube9.gmarket.Home.ModelClass.AllProductsModel;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllProductsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private List<AllProductsModel> allProductsModels;
    private Context context;

    private boolean isLoadingAdded = false;

    public AllProductsAdapter(Context context) {
        this.context = context;
        allProductsModels = new ArrayList<>();
    }

    public List<AllProductsModel> getMovies() {
        return allProductsModels;
    }

    public void setMovies(List<AllProductsModel> movies) {
        this.allProductsModels = movies;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingVH(v2);
                break;
        }
        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.row_latest_products, parent, false);
        viewHolder = new ViewHolder(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        final AllProductsModel items = allProductsModels.get(position);

        switch (getItemViewType(position)) {
            case ITEM:
                ViewHolder viewHolder = (ViewHolder) holder;

                viewHolder.tv_product_name.setText(items.getProduct_name());
                viewHolder.tv_product_name.setText(items.getProduct_name());
                viewHolder.tv_original_price.setText(items.getProduct_price() +" FCFA");
                viewHolder.tv_discount.setText(items.getProduct_discount());
                viewHolder.tv_discounted_pice.setText(items.getProduct_special_price()+" FCFA");
                if (items.getProduct_special_price().equals("null"))
                {
                    viewHolder.tv_discounted_pice.setVisibility(View.GONE);
                }
                if (items.getProduct_discount().equals("null"))
                {
                    viewHolder.tv_discount.setVisibility(View.GONE);
                }
                if (!items.getProduct_price().equals("null"))
                {

                    //holder.tv_original_price.setText("FCFA "+new DecimalFormat("##.##").format(Double.parseDouble(items.getProduct_price())));
                    viewHolder.tv_original_price.setText(items.getProduct_price()+" FCFA");
                }
                if ( !items.getProduct_special_price().equals("null"))
                {
                    // holder.tv_discounted_pice.setText("FCFA "+new DecimalFormat("##.##").format(Double.parseDouble(items.getProduct_special_price())));
                    viewHolder.tv_discounted_pice.setText(items.getProduct_special_price()+" FCFA");
                    viewHolder.tv_original_price.setPaintFlags( viewHolder.tv_original_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

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

                Picasso.with(context).load(items.getProduct_image()).networkPolicy(NetworkPolicy.NO_CACHE).into(viewHolder.iv_product_image);


                viewHolder.ll_product.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i=new Intent(context, ProductDetailsNewActivity.class);
                        i.putExtra("product_id",items.getProduct_id());
                        i.putExtra("product_name",items.getProduct_name());
                        i.putExtra("category_id","");
                        context.startActivity(i);

                    }
                });


                break;
            case LOADING:
//                Do nothing
                break;
        }

    }

    @Override
    public int getItemCount() {
        return allProductsModels == null ? 0 : allProductsModels.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == allProductsModels.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    /*
   Helpers
   _________________________________________________________________________________________________
    */

    public void add(AllProductsModel mc) {
        allProductsModels.add(mc);
        notifyItemInserted(allProductsModels.size() - 1);
    }

    public void addAll(List<AllProductsModel> mcList) {
        for (AllProductsModel mc : mcList) {
            add(mc);
        }
    }

    public void remove(AllProductsModel city) {
        int position = allProductsModels.indexOf(city);
        if (position > -1) {
            allProductsModels.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


   /* public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new AllProductsModel());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = allProductsModels.size() - 1;
        AllProductsModel item = getItem(position);

        if (item != null) {
            allProductsModels.remove(position);
            notifyItemRemoved(position);
        }
    }*/

    public AllProductsModel getItem(int position) {
        return allProductsModels.get(position);
    }


   /*
   View Holders
   _________________________________________________________________________________________________
    */

    /**
     * Main list's content ViewHolder
     */
    protected class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_product_image;
        Button btn_add_to_cart;
        TextView tv_product_name,tv_discounted_pice,tv_original_price,tv_discount,tv_rating,tv_no_cost_emi;
        SparkButton spark_button;
        LinearLayout ll_product;
        RatingBar ratingBar;

        public ViewHolder(View view) {
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


    protected class LoadingVH extends RecyclerView.ViewHolder {

        public LoadingVH(View itemView) {
            super(itemView);
        }
    }

}