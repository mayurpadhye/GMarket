package com.cube9.gmarket.Products.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cube9.gmarket.Category.ModelClass.CatProductsPojo;
import com.cube9.gmarket.HelperClass.CustomUtils;
import com.cube9.gmarket.HelperClass.GMarketApplication;
import com.cube9.gmarket.HelperClass.SharedPrefManager;
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

import static com.cube9.gmarket.Products.Activity.ProductListActivity.isViewWithCatalog;

public class ProductListAdapter  extends RecyclerView.Adapter<ProductListAdapter.MyViewHolder>  {
    List<CatProductsPojo> catProductsPojoList;
    Context context;
    ProgressBar p_bar;

    public ProductListAdapter(List<CatProductsPojo> catProductsPojoList, Context context, ProgressBar p_bar) {
        this.catProductsPojoList = catProductsPojoList;
        this.context = context;
        this.p_bar = p_bar;
    }

    @NonNull
    @Override
    public ProductListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(isViewWithCatalog ? R.layout.row_product_list_linear : R.layout.row_product_list, null);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProductListAdapter.MyViewHolder holder, final int position) {
        final CatProductsPojo items=catProductsPojoList.get(position);
        holder.tv_product_name.setText(items.getProduct_name());
        holder.tv_original_price.setText(items.getProduct_original_price());
        holder.tv_discount.setText(items.getProduct_discount());
        holder.tv_discounted_pice.setText(items.getProduct_price());
        if (catProductsPojoList.get(position).getWishlist_flag().equals("1"))
        {
            holder.spark_button.setChecked(true);
        }
        else
        {
            holder.spark_button.setChecked(false);
        }

        if (items.getProduct_price().equals("null"))
        {
            holder.tv_discounted_pice.setVisibility(View.GONE);
        }
        if (items.getProduct_discount().equals("null"))
        {
            holder.tv_discount.setVisibility(View.GONE);
        }
        if (!items.getProduct_original_price().equals("null"))
        {

         //   holder.tv_original_price.setText("FCFA "+new DecimalFormat("##.##").format(Double.parseDouble(items.getProduct_original_price())));
            holder.tv_original_price.setText(items.getProduct_original_price()+" FCFA");

        }
        if ( !items.getProduct_price().equals("null"))
        {
         //   holder.tv_discounted_pice.setText("FCFA "+new DecimalFormat("##.##").format(Double.parseDouble(items.getProduct_price())));
            holder.tv_discounted_pice.setText(items.getProduct_price()+" FCFA");
            holder.tv_original_price.setPaintFlags( holder.tv_original_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        if (items.getIs_in_stock().equals("1"))
        {
            holder.tv_in_stock.setText(context.getResources().getString(R.string.in_stock));

            holder.tv_in_stock.setTextColor(context.getResources().getColor(R.color.green));
        }
        else
        {

            holder.tv_in_stock.setText(context.getResources().getString(R.string.stock_out));
            holder.tv_in_stock.setTextColor(context.getResources().getColor(R.color.red));
        }
        //set drawable to imageview
      /*  Glide.with(context)
                .load(items.getProduct_image())
                .apply(new RequestOptions())
                .into(holder.iv_product_image);*/

       /* Glide.with(context).load(items.getProduct_image())
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.iv_product_image);*/

        Picasso.with(context).load(items.getProduct_image()).placeholder(context.getResources().getDrawable(R.drawable.ic_g_market_logo)).networkPolicy(NetworkPolicy.NO_CACHE).into(holder.iv_product_image);
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
                        AddToWishList(items.getProduct_id(),items,position,holder.spark_button);
                    } else {
                        // Button is inactive
                        RemoveFromWishList(items.getProduct_id(),items,position,holder.spark_button);
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
        holder.rl_products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(context, ProductDetailsNewActivity.class);
                i.putExtra("product_id",items.getProduct_id());
                i.putExtra("product_name",items.getProduct_name());
                i.putExtra("category_id",items.getCat_id());
                context.startActivity(i);

            }
        });

    }

    @Override
    public int getItemCount() {
        return catProductsPojoList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_product_image;
        TextView tv_product_name,tv_discounted_pice,tv_original_price,tv_discount,tv_rating,tv_in_stock;
        SparkButton spark_button;
        RelativeLayout rl_products;
        public MyViewHolder(View v) {
            super(v);

            iv_product_image = (ImageView) v.findViewById(R.id.iv_product_image);
            tv_product_name = (TextView) v.findViewById(R.id.tv_product_name);
            tv_discounted_pice = (TextView) v.findViewById(R.id.tv_discounted_pice);
            tv_original_price = (TextView) v.findViewById(R.id.tv_original_price);
            tv_discount = (TextView) v.findViewById(R.id.tv_discount);
            tv_rating = (TextView) v.findViewById(R.id.tv_rating);
            tv_in_stock = (TextView) v.findViewById(R.id.tv_in_stock);
            spark_button = (SparkButton) v.findViewById(R.id.spark_button);
            rl_products = (RelativeLayout) v.findViewById(R.id.rl_products);

        }

    }
    public void AddToWishList(final String product_id, final CatProductsPojo items, final int position, final SparkButton sparkButton)
    {
        p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.ADD_TO_WISHLIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        p_bar.setVisibility(View.GONE);
                        try {

                            JSONObject Jsonobj = new JSONObject(response);
                            String status=Jsonobj.getString("status");

                            if (status.equals("1"))
                            {
                                CustomUtils.showToast(Jsonobj.getString("message"),context);
                                //items.setWishlist_flag("1");
                                catProductsPojoList.get(position).setWishlist_flag("1");
                                notifyDataSetChanged();
                                sparkButton.setChecked(true);

                            }
                            else
                            {
                                CustomUtils.showToast(Jsonobj.getString("message"),context);
                                sparkButton.setChecked(false);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        p_bar.setVisibility(View.GONE);
                      //  Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("language",new SharedPrefManager(context).getLanguage());
                params.put("product_id",product_id);
                params.put("user_id",new SharedPrefManager(context).GetCustomerId());


                return params;
            }
        };

        GMarketApplication.getInstance(context).addToRequestQueue(stringRequest);
    }//AddToWishListClose

    public void RemoveFromWishList(final String product_id, final CatProductsPojo items, final int position, final SparkButton sparkButton)
    {
        p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.REMOVE_FROM_WISHLIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        p_bar.setVisibility(View.GONE);
                        try {

                            JSONObject Jsonobj = new JSONObject(response);
                            String status=Jsonobj.getString("status");

                            if (status.equals("1"))
                            {
                                CustomUtils.showToast(context.getResources().getString(R.string.remove_from_wishlist),context);
                                catProductsPojoList.get(position).setWishlist_flag("0");
                                notifyDataSetChanged();
                                sparkButton.setChecked(false);

                            }
                            else
                            {
                                CustomUtils.showToast(context.getResources().getString(R.string.remove_from_wishlist_error),context);
                                sparkButton.setChecked(true);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        p_bar.setVisibility(View.GONE);
                       // Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("language",new SharedPrefManager(context).getLanguage());
                params.put("product_id",product_id);
                params.put("user_id",new SharedPrefManager(context).GetCustomerId());


                return params;
            }
        };

        GMarketApplication.getInstance(context).addToRequestQueue(stringRequest);
    }//AddToWishListClose
}
