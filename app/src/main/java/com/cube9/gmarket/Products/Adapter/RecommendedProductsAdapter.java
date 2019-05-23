package com.cube9.gmarket.Products.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.NonNull;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cube9.gmarket.HelperClass.CustomUtils;
import com.cube9.gmarket.HelperClass.GMarketApplication;
import com.cube9.gmarket.HelperClass.SharedPrefManager;
import com.cube9.gmarket.Login.Activity.LoginDetailsActivity;
import com.cube9.gmarket.Products.Activity.ProductDetailsNewActivity;
import com.cube9.gmarket.Products.ModelClasses.RecommendedPojo;
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

public class RecommendedProductsAdapter extends RecyclerView.Adapter<RecommendedProductsAdapter.MyViewHolder> {
    private List<RecommendedPojo> recommendedPojoList;
    Context context;
    public RecommendedProductsAdapter(List<RecommendedPojo> recommendedPojoList, Context context) {
        this.recommendedPojoList = recommendedPojoList;
        this.context = context;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_latest_products, parent, false);
        itemView.getLayoutParams().width = (int) (getScreenWidth() /2);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final RecommendedPojo items=recommendedPojoList.get(position);
        holder.tv_product_name.setText(items.getProduct_name());
        // holder.tv_original_price.setText(items.getProduct_original_price());
        holder.tv_discount.setText(items.getProduct_discount());
        holder.tv_discounted_pice.setText(items.getProduct_price());
        holder.spark_button.setVisibility(View.GONE);
        if (recommendedPojoList.get(position).getWishlist_flag().equals("1"))
        {
            holder.spark_button.setChecked(true);
        }
        else
        {
            holder.spark_button.setChecked(false);
        }

        if (items.getProduct_special_price().equals("null"))
        {
            holder.tv_discounted_pice.setVisibility(View.INVISIBLE);
        }
        if (items.getProduct_discount().equals("null"))
        {
            holder.tv_discount.setVisibility(View.GONE);
        }
        if (!items.getProduct_price().equals("null"))
        {

           // holder.tv_original_price.setText("FCFA "+new DecimalFormat("##.##").format(Double.parseDouble(items.getProduct_price())));
            holder.tv_original_price.setText(items.getProduct_price()+" FCFA");

        }
        if ( !items.getProduct_special_price().equals("null"))
        {
           // holder.tv_discounted_pice.setText("FCFA "+new DecimalFormat("##.##").format(Double.parseDouble(items.getProduct_special_price())));
            holder.tv_discounted_pice.setText(items.getProduct_special_price()+" FCFA");
            holder.tv_original_price.setPaintFlags( holder.tv_original_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        }
if (items.getRating().equals("")||items.getRating().equals("null")||items.getRating().equals(null))
{
    holder.ratingBar.setRating(0);
}
else
{
    holder.ratingBar.setRating((float) Double.parseDouble(items.getRating()));
}
        holder.btn_add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(context instanceof ProductDetailsNewActivity){
                    ((ProductDetailsNewActivity)context).LoadRecommendedProduct(items.getProduct_id(),items.getProduct_name());
                }

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
        holder.ll_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(context instanceof ProductDetailsNewActivity){
                    ((ProductDetailsNewActivity)context).LoadRecommendedProduct(items.getProduct_id(),items.getProduct_name());
                }

            }
        });
    }


    public void AddToWishList(final String product_id, final RecommendedPojo items, final int position, final SparkButton sparkButton)
    {
    //    p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.ADD_TO_WISHLIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    //    p_bar.setVisibility(View.GONE);
                        try {

                            JSONObject Jsonobj = new JSONObject(response);
                            String status=Jsonobj.getString("status");

                            if (status.equals("1"))
                            {
                                CustomUtils.showToast(Jsonobj.getString("message"),context);
                                //items.setWishlist_flag("1");
                                recommendedPojoList.get(position).setWishlist_flag("1");
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
                       // p_bar.setVisibility(View.GONE);
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

    public void RemoveFromWishList(final String product_id, final RecommendedPojo items, final int position, final SparkButton sparkButton)
    {
       // p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.REMOVE_FROM_WISHLIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    //    p_bar.setVisibility(View.GONE);
                        try {

                            JSONObject Jsonobj = new JSONObject(response);
                            String status=Jsonobj.getString("status");

                            if (status.equals("1"))
                            {
                                CustomUtils.showToast(context.getResources().getString(R.string.remove_from_wishlist),context);
                                recommendedPojoList.get(position).setWishlist_flag("0");
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
                       // p_bar.setVisibility(View.GONE);
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

    @Override
    public int getItemCount() {
        return recommendedPojoList.size();
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
    public int getScreenWidth() {

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        return size.x;
    }
}
