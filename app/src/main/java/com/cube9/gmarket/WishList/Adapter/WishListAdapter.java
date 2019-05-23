package com.cube9.gmarket.WishList.Adapter;

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
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cube9.gmarket.HelperClass.CustomUtils;
import com.cube9.gmarket.HelperClass.GMarketApplication;
import com.cube9.gmarket.HelperClass.SharedPrefManager;
import com.cube9.gmarket.Products.Activity.VarientProductActivity;
import com.cube9.gmarket.R;
import com.cube9.gmarket.WebUrls.WebUrls;
import com.cube9.gmarket.WishList.ModelClass.WishListPojo;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WishListAdapter extends RecyclerView.Adapter<WishListAdapter.MyViewHolder> {
    private List<WishListPojo> wishListPojoList;
    Context context;
    ProgressBar p_bar;
    TextView tv_cart_count;
    LinearLayout ll_empty_wishlist;
TextView tv_title;
    public WishListAdapter(List<WishListPojo> wishListPojoList, Context context, ProgressBar p_bar, TextView tv_cart_count, LinearLayout ll_empty_wishlist, TextView tv_title) {
        this.wishListPojoList = wishListPojoList;
        this.context = context;
        this.p_bar = p_bar;
        this.ll_empty_wishlist=ll_empty_wishlist;
        this.tv_cart_count=tv_cart_count;
        this.tv_title=tv_title;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_wishlist, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final WishListPojo items=wishListPojoList.get(position);
        holder.tv_product_name.setText(items.getProduct_name());
        holder.tv_discounted_pice.setText(" "+items.getProduct_special_price());
     //   Glide.with(context).load(items.getProduct_image()).into(holder.iv_product_image);


       /* Glide.with(context).load(items.getProduct_image())
                .thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.iv_product_image);*/

        Picasso.with(context).load(items.getProduct_image()).networkPolicy(NetworkPolicy.NO_CACHE).into(holder.iv_product_image);
        if (items.getProduct_price().equals("null"))
        {
            holder.tv_original_price.setVisibility(View.GONE);
        }
        else
        {
            holder.tv_original_price.setVisibility(View.VISIBLE);
            holder.tv_original_price.setText(items.getProduct_price()+" FCFA");
           // holder.tv_original_price.setText("FCFA "+new DecimalFormat("##.##").format(Double.parseDouble(items.getProduct_price())));
        }
        if (items.getProduct_special_price().equals("null"))
        {
            holder.tv_discounted_pice.setVisibility(View.GONE);
        }
        else
        {
            holder.tv_discounted_pice.setVisibility(View.VISIBLE);
            holder.tv_discounted_pice.setText(items.getProduct_special_price()+" FCFA");
          //  holder.tv_discounted_pice.setText("FCFA "+new DecimalFormat("##.##").format(Double.parseDouble(items.getProduct_special_price())));
            holder.tv_original_price.setPaintFlags( holder.tv_original_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        if (items.getProduct_discount().equals("null"))
        {
            holder.tv_discount.setVisibility(View.GONE);
        }
        else
        {
            holder.tv_discount.setVisibility(View.VISIBLE);
        }

        if (items.getIs_in_stock().equals("1"))
        {
            holder.btn_addto_cart.setVisibility(View.VISIBLE);
            holder.btn_out_of_stock.setVisibility(View.GONE);
        }
        else
        {
            holder.btn_addto_cart.setVisibility(View.GONE);
            holder.btn_out_of_stock.setVisibility(View.VISIBLE);
        }
        holder.btn_addto_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (items.isConfigurale())
                {
                    Intent i=new Intent(context,VarientProductActivity.class);
                    i.putExtra("product_id",items.getProduct_id());
                    i.putExtra("using_from","cart");
                    context.startActivity(i);
                }
                else
                   moveToCart(items.getProduct_id(),position);
            }
        });

        holder.btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoveFromWishList(items.getProduct_id(),position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return wishListPojoList.size();
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
        public TextView tv_product_name, tv_discounted_pice, tv_original_price,tv_discount,tv_rating;
        Button btn_remove,btn_addto_cart,btn_out_of_stock;
        ImageView iv_product_image;

        public MyViewHolder(View view) {
            super(view);
            iv_product_image = (ImageView) view.findViewById(R.id.iv_product_image);
            tv_product_name = (TextView) view.findViewById(R.id.tv_product_name);
            tv_discounted_pice = (TextView) view.findViewById(R.id.tv_discounted_pice);
            tv_original_price = (TextView) view.findViewById(R.id.tv_original_price);
            tv_discount = (TextView) view.findViewById(R.id.tv_discount);
            tv_rating = (TextView) view.findViewById(R.id.tv_rating);
            btn_remove = (Button) view.findViewById(R.id.btn_remove);
            btn_addto_cart = (Button) view.findViewById(R.id.btn_addto_cart);
            btn_out_of_stock = (Button) view.findViewById(R.id.btn_out_of_stock);

        }

    }
    public void RemoveFromWishList(final String product_id, final int position)
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
                               wishListPojoList.remove(position);
                               notifyDataSetChanged();
                               if (wishListPojoList.size()>0)
                               {
                                   ll_empty_wishlist.setVisibility(View.GONE);
                               }
                               else
                               {
                                   ll_empty_wishlist.setVisibility(View.VISIBLE);
                               }
                                tv_title.setText(context.getResources().getString(R.string.my_wishlist)+" "+"("+wishListPojoList.size()+")");
                            }
                            else
                            {
                                CustomUtils.showToast(context.getResources().getString(R.string.remove_from_wishlist_error),context);

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
    }

    public void moveToCart(final String product_id, final int position)
    {
        p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.MOVE_TO_CART,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        p_bar.setVisibility(View.GONE);
                        try {

                            JSONObject Jsonobj = new JSONObject(response);
                            String status=Jsonobj.getString("status");

                            if (status.equals("1"))
                            {
                                String my_cart_count=Jsonobj.getString("my_cart_count");
                                tv_cart_count.setText(my_cart_count);
                                new SharedPrefManager(context).setCartCount(my_cart_count);
                                CustomUtils.showToast(context.getResources().getString(R.string.moved_to_cart),context);
                                wishListPojoList.remove(position);

                                tv_title.setText(context.getResources().getString(R.string.my_wishlist)+" "+"("+wishListPojoList.size()+")");
                                notifyDataSetChanged();

                            }
                            else
                            {
                                CustomUtils.showToast(context.getResources().getString(R.string.moved_to_cart_error),context);

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
    }
}
