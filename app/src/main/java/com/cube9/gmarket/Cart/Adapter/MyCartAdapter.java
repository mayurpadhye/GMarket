package com.cube9.gmarket.Cart.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cube9.gmarket.Cart.Activity.MyCartActivity;
import com.cube9.gmarket.Delivery.ModelClass.CartProductPojo;
import com.cube9.gmarket.HelperClass.CustomUtils;
import com.cube9.gmarket.HelperClass.GMarketApplication;
import com.cube9.gmarket.HelperClass.SharedPrefManager;
import com.cube9.gmarket.R;
import com.cube9.gmarket.WebUrls.WebUrls;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.MyViewHolder> {
    private List<CartProductPojo> cartProductPojoList;
       Context context;
       List<Integer> spinerQty;
       ProgressBar p_bar;
       TextView tv_final_total;
       RelativeLayout rl_no_items,rl_continue;
       TextView tv_title;

    int iCurrentSelection;
    private final String[] quantityValues = new String[] { "1", "2", "3", "4",
            "5","more" };
    public MyCartAdapter(List<CartProductPojo> cartProductPojoList, Context context,ProgressBar p_bar, TextView tv_final_total,RelativeLayout rl_continue,RelativeLayout rl_no_items,TextView tv_title) {
        this.cartProductPojoList = cartProductPojoList;
        this.context = context;
        this.p_bar=p_bar;
        this.tv_final_total=tv_final_total;
        spinerQty=new ArrayList<Integer>();
        this.rl_continue=rl_continue;
        this.rl_no_items=rl_no_items;
        this.tv_title=tv_title;
    }//Constructor

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_my_cart, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        final CartProductPojo item=cartProductPojoList.get(position);
        holder.tv_product_name.setText(item.getProduct_name());

        if (item.getIs_in_stock().equals("1"))
        {

            holder.tv_stock_out.setVisibility(View.GONE);
        }
        else
        {
            holder.tv_stock_out.setVisibility(View.VISIBLE);
        }

        holder.btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoveFromCart(item.getProduct_id(),position);
            }
        });

        holder.btn_move_to_wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToWishlist(item.getProduct_id(),position);
            }
        });
        if (!item.getProduct_price().equals("null"))
        {

            holder.tv_original_price.setText(item.getProduct_price()+" FCFA");
         //   holder.tv_original_price.setText("FCFA "+new DecimalFormat("##.##").format(Double.parseDouble(item.getProduct_price())));

        }
        if ( !item.getProduct_special_price().equals("null"))
        {
            holder.tv_discounted_pice.setText(item.getProduct_special_price()+" FCFA");
            //holder.tv_discounted_pice.setText("FCFA "+new DecimalFormat("##.##").format(Double.parseDouble(item.getProduct_special_price())));
            holder.tv_original_price.setPaintFlags( holder.tv_original_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        holder.tv_discount.setText(item.getProduct_discount()+"% OFF ");
     //   Glide.with(context).load(item.getProduct_image()).into(holder.iv_product_image);


   /*     Glide.with(context).load(item.getProduct_image())
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.iv_product_image);*/
        Picasso.with(context).load(item.getProduct_image()).networkPolicy(NetworkPolicy.NO_CACHE).into(holder.iv_product_image);
        if (item.getProduct_special_price().equals("null"))
        {
            holder.tv_discounted_pice.setVisibility(View.GONE);
        }
        if (item.getProduct_discount().equals("null"))
        {
            holder.tv_discount.setVisibility(View.GONE);
        }
        ArrayAdapter<String> quantityAdapter = new ArrayAdapter<String>(
                context, android.R.layout.simple_spinner_item,
                quantityValues);

        quantityAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        holder.sp_qty.setAdapter(quantityAdapter);
        holder.sp_qty.setSelection(findSelection(item));
      //  holder.tv_qty.setText("QTY "+holder.sp_qty.getSelectedItem());
        holder.tv_qty.setText("QTY "+item.getSelected_quantity());
        final int[] iCurrentSelection = {holder.sp_qty.getSelectedItemPosition()};
        holder.sp_qty
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0,
                                               View arg1, int arg2, long arg3) {
                            // Your code here
                            if(iCurrentSelection[0] == arg2){
                              return; //do nothing
                            }
                            else
                            {
                                if (holder.sp_qty.getSelectedItem().equals("more"))
                                {
                                    openMoreQuantityDialog(item,position, holder.tv_qty,arg0,"more");
                                }
                                else
                                {
                                    if (quantityValues[arg2].equals("more"))
                                    {
                                      //  Toast.makeText(context, "dsd", Toast.LENGTH_SHORT).show();

                                    }
                                    else
                                    {
                                        UpdateCart(item.getProduct_id(),position,quantityValues[arg2],holder.tv_qty,item,arg0,"single");
                                    }

                                }


                            }
                        iCurrentSelection[0] = arg2;

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        if (holder.sp_qty.getSelectedItem().equals("more"))
                        {
                            openMoreQuantityDialog(item,position, holder.tv_qty,arg0,"more");
                        }
                    }

                });

    }
    private int findSelection(CartProductPojo item) {
        for (int i = 0; i < quantityValues.length; i++) {
            if (!quantityValues[i].equals("more"))
            {
                if (Integer.valueOf(quantityValues[i]).intValue() == Integer.parseInt(item.getSelected_quantity())) {
                    return i;
                }
            }
            else
                return  5;

        }

        return -1;
    }
    @Override
    public int getItemCount() {
        return cartProductPojoList.size();
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
        public TextView tv_product_name, tv_discounted_pice, tv_original_price,tv_discount,tv_qty,tv_stock_out;
        ImageView iv_product_image;
        Button btn_move_to_wishlist,btn_remove;
        LinearLayout ll_move_to_wishlist;
        Spinner sp_qty;
        public MyViewHolder(View view) {
            super(view);
            iv_product_image = (ImageView) view.findViewById(R.id.iv_product_image);
            tv_product_name = (TextView) view.findViewById(R.id.tv_product_name);
            tv_stock_out = (TextView) view.findViewById(R.id.tv_stock_out);
            tv_discounted_pice = (TextView) view.findViewById(R.id.tv_discounted_pice);
            tv_original_price = (TextView) view.findViewById(R.id.tv_original_price);
            tv_discount = (TextView) view.findViewById(R.id.tv_discount);
            tv_qty = (TextView) view.findViewById(R.id.tv_qty);
            btn_remove = (Button) view.findViewById(R.id.btn_remove);
            btn_move_to_wishlist = (Button) view.findViewById(R.id.btn_move_to_wishlist);
            sp_qty = (Spinner) view.findViewById(R.id.sp_qty);
            ll_move_to_wishlist = (LinearLayout) view.findViewById(R.id.ll_move_to_wishlist);
        }

    }
    public void openMoreQuantityDialog(final CartProductPojo item, final int position, final TextView tv_qty, final AdapterView<?> arg0, final String more_qty)
    {
        final Dialog dialog=new Dialog(context);
        dialog.setContentView(R.layout.dialog_add_more_qty);
        final EditText et_quantity=(EditText)dialog.findViewById(R.id.et_quantity);
        TextView tv_cancel=(TextView)dialog.findViewById(R.id.tv_cancel);
        TextView tv_save=(TextView)dialog.findViewById(R.id.tv_save);
        dialog.show();
        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_quantity.getText().toString().trim().length()==0)
                {
                    et_quantity.setError(context.getResources().getString(R.string.enter_qty_error));
                    return;
                }
                else
                {

                    UpdateCart(item.getProduct_id(),position,et_quantity.getText().toString(),tv_qty,item,arg0,more_qty);
                    dialog.dismiss();
                }
            }
        });

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
       // return Integer.parseInt(et_quantity.getText().toString());
    }

    public void RemoveFromCart(final String product_id, final int position)
    {
        p_bar.setVisibility(View.VISIBLE);
        if(context instanceof MyCartActivity){
            ((MyCartActivity)context).setActivityInteraction();
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.REMOVE_FROM_CART,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        p_bar.setVisibility(View.GONE);
                        if(context instanceof MyCartActivity){
                            ((MyCartActivity)context).enableActivityInteraction();
                        }
                        try {

                            JSONObject Jsonobj = new JSONObject(response);
                            Log.i("ressssss",""+product_id+" "+new SharedPrefManager(context).GetCustomerId()+" "+new SharedPrefManager(context).getLanguage());
                            String status=Jsonobj.getString("status");

                            if (status.equals("1"))
                            {
                               // int adapterPosition=context.getAdapterPosition();
                                String message=Jsonobj.getString("message");
                                CustomUtils.showToast(context.getResources().getString(R.string.cart_item_deleted),context);
                               String total_amount=Jsonobj.getString("total_amount");
                               String my_cart_count=Jsonobj.getString("my_cart_count");
                               new SharedPrefManager(context).setCartCount(my_cart_count);
                                tv_title.setText(context.getResources().getString(R.string.my_cart)+" ("+my_cart_count+")");

                              //  tv_final_total.setText("FCFA "+total_amount);
                                tv_final_total.setText( total_amount+" FCFA");
                              //  tv_final_total.setText("FCFA " + new DecimalFormat("##.##").format(Double.parseDouble(total_amount)));

                                JSONArray products=Jsonobj.getJSONArray("products");
                                for (int i=0;i<products.length();i++)
                                {
                                    JSONObject j1=products.getJSONObject(i);
                                    String product_id=j1.getString("product_id");
                                    String product_name=j1.getString("product_name");
                                    String product_price=j1.getString("product_price");
                                    String product_special_price=j1.getString("product_special_price");
                                    String selected_quantity=j1.getString("selected_quantity");
                                    String product_image=j1.getString("product_image");
                                    String product_discount=j1.getString("product_discount");
                                    String quantity_available=j1.getString("quantity_available");
                                }

                                if (products.length()==0)
                                {
                                    rl_continue.setVisibility(View.GONE);
                                    rl_no_items.setVisibility(View.VISIBLE);
                                }
                                else
                                {
                                    rl_continue.setVisibility(View.VISIBLE);
                                    rl_no_items.setVisibility(View.GONE);
                                }
                                cartProductPojoList.remove(position);
                                notifyDataSetChanged();
                            }
                            else
                            {
                                CustomUtils.showToast(context.getResources().getString(R.string.unable_to_remove),context);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(context instanceof MyCartActivity){
                            ((MyCartActivity)context).enableActivityInteraction();
                        }
                        p_bar.setVisibility(View.GONE);
                      //  Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("product_id",product_id);
                params.put("language",new SharedPrefManager(context).getLanguage());
                params.put("user_id",new SharedPrefManager(context).GetCustomerId());


                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                9000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GMarketApplication.getInstance(context).addToRequestQueue(stringRequest);
    }//RemoveFromCart


    public void moveToWishlist(final String product_id, final int position)
    {
        p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.MOVE_TO_WISHLIST,
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
                                new SharedPrefManager(context).setCartCount(my_cart_count);
                                tv_title.setText(context.getResources().getString(R.string.my_cart)+" ("+my_cart_count+")");
                                String total_amount=Jsonobj.getString("total_amount");
                              //   tv_final_total.setText("FCFA "+total_amount);
                              //  tv_final_total.setText("FCFA " + new DecimalFormat("##.##").format(Double.parseDouble(total_amount)));
                                tv_final_total.setText( total_amount+" FCFA");
                                CustomUtils.showToast(context.getResources().getString(R.string.moved_to_wishlist),context);

                                JSONArray products=Jsonobj.getJSONArray("products");
                                if (products.length()==0)
                                {
                                    rl_continue.setVisibility(View.GONE);
                                    rl_no_items.setVisibility(View.VISIBLE);
                                }
                                else
                                {
                                    rl_continue.setVisibility(View.VISIBLE);
                                    rl_no_items.setVisibility(View.GONE);
                                }
                                cartProductPojoList.remove(position);
                                notifyDataSetChanged();



                            }
                            else
                            {
                                CustomUtils.showToast(context.getResources().getString(R.string.moved_to_wishlist_error),context);

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
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                9000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GMarketApplication.getInstance(context).addToRequestQueue(stringRequest);
    }//moveToWishList

    public void UpdateCart(final String product_id, final int position, final String qty, final TextView tv_qty, final CartProductPojo item, final AdapterView<?> arg0, final String update_from)
    {
        p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.UPDATE_CART_ITEM,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        p_bar.setVisibility(View.GONE);
                        try {

                            JSONObject Jsonobj = new JSONObject(response);
                            String status=Jsonobj.getString("status");
                            String message=Jsonobj.getString("message");
                            if (status.equals("1"))
                            {

                                String total_amount=Jsonobj.getString("total_amount");
                                CustomUtils.showToast(context.getResources().getString(R.string.cart_updated),context);
                              //  tv_final_total.setText("FCFA "+total_amount);
                                String final_total = total_amount.replaceAll(",","");
                                tv_final_total.setText( final_total.trim()+" FCFA");
                               // tv_final_total.setText("FCFA " + new DecimalFormat("##.##").format(Double.parseDouble(final_total.trim())));
                                JSONArray products=Jsonobj.getJSONArray("products");
                                if (update_from.equals("single"))
                                {
                                    item.setSelected_quantity(
                                            arg0.getSelectedItem().toString()
                                    );
                                    tv_qty.setText("QTY "+item.getSelected_quantity());
                                }
                                else
                                {
                                    item.setSelected_quantity(qty);
                                    tv_qty.setText("QTY "+item.getSelected_quantity());
                                }


                                for (int i=0;i<products.length();i++)
                                {
                                    JSONObject j1=products.getJSONObject(i);
                                    String product_id=j1.getString("product_id");
                                    String product_name=j1.getString("product_name");
                                    String product_price=j1.getString("product_price");
                                    String product_special_price=j1.getString("product_special_price");
                                    String selected_quantity=j1.getString("selected_quantity");
                                    String product_image=j1.getString("product_image");
                                    String product_discount=j1.getString("product_discount");
                                    String quantity_available=j1.getString("quantity_available");
                                  //  cartProductPojoList.get(position).setSelected_quantity(selected_quantity);
                                   // tv_qty.setText("QTY "+item.getSelected_quantity());
                                }


                            }
                            else
                            {
                                CustomUtils.showToast(message,context);
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
                params.put("product_id",product_id);
                params.put("user_id",new SharedPrefManager(context).GetCustomerId());
                params.put("qty",qty);


                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                9000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GMarketApplication.getInstance(context).addToRequestQueue(stringRequest);
    }
}
