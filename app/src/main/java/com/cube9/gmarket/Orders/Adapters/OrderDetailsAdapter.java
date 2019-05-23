package com.cube9.gmarket.Orders.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cube9.gmarket.HelperClass.GMarketApplication;
import com.cube9.gmarket.HelperClass.SharedPrefManager;
import com.cube9.gmarket.Orders.ModelClass.OrderDetailPojo;
import com.cube9.gmarket.R;
import com.cube9.gmarket.WebUrls.WebUrls;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderDetailsAdapter extends   RecyclerView.Adapter<OrderDetailsAdapter.MyViewHolder>  {
private List<OrderDetailPojo> orderDetailPojoList;
        Context context;
    float rating = 0;
        public OrderDetailsAdapter(List<OrderDetailPojo> orderDetailPojoList, Context context) {
        this.orderDetailPojoList = orderDetailPojoList;
        this.context = context;

}
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_order_details, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final OrderDetailPojo items=orderDetailPojoList.get(position);

        holder.tv_product_name.setText(items.getName());
        if (!items.getRating().equals("null")) {
            if (Float.parseFloat(items.getRating()) > 0) {
                holder.ratingBar.setRating(Float.parseFloat(items.getRating()));
                holder.ratingBar.setVisibility(View.VISIBLE);
                holder.btn_rate.setVisibility(View.GONE);
            } else {
                holder.ratingBar.setVisibility(View.GONE);
                holder.btn_rate.setVisibility(View.VISIBLE);
            }
        }

       holder.tv_price.setText(context.getResources().getString(R.string.currency)+" "+items.getPrice());
        holder.btn_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddCustomerRating(items.getProduct_id(),position);

            }
        });
        Picasso.with(context).load(items.getImage()).networkPolicy(NetworkPolicy.NO_CACHE).into(holder.iv_product_image);
    }

    @Override
    public int getItemCount() {
        return orderDetailPojoList.size();
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
        public TextView tv_product_name,tv_price,tv_status;
        ImageView iv_product_image;
        RatingBar ratingBar;
        Button btn_rate;
        public MyViewHolder(View view) {
            super(view);

            tv_product_name = (TextView) view.findViewById(R.id.tv_product_name);
            tv_status = (TextView) view.findViewById(R.id.tv_status);
            tv_price = (TextView) view.findViewById(R.id.tv_price);
            iv_product_image = (ImageView) view.findViewById(R.id.iv_product_image);
            btn_rate=(Button)view.findViewById(R.id.btn_rate);
            ratingBar=(RatingBar) view.findViewById(R.id.ratingBar);
        }

    }



    public void AddCustomerRating(final String product_id, final int position) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_add_rating);
        Button  btn_submit = (Button) dialog.findViewById(R.id.btn_submit);
        final EditText  et_comment = (EditText) dialog.findViewById(R.id.et_comment);
        final EditText    et_title = (EditText) dialog.findViewById(R.id.et_title);
        final EditText et_name = (EditText) dialog.findViewById(R.id.et_name);
        final ProgressBar progress = (ProgressBar) dialog.findViewById(R.id.progress);
        TextInputLayout text_inputname = (TextInputLayout) dialog.findViewById(R.id.text_inputname);
        TextInputLayout   text_inputtitle = (TextInputLayout) dialog.findViewById(R.id.text_inputtitle);
        TextInputLayout  text_inputcomment = (TextInputLayout) dialog.findViewById(R.id.text_inputcomment);
        RatingBar  rate_bar = (RatingBar) dialog.findViewById(R.id.rate_bar);
        rate_bar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                if (b) {
                    rating = v;
                }
            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_name.getText().toString().length() > 0 || et_title.getText().toString().length() > 0 || et_comment.getText().toString().length() > 0) {
                  //  dialog.dismiss();
                    if (rating == 0) {
                        Toast.makeText(context, ""+context.getResources().getString(R.string.please_rate_the_product), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    progress.setVisibility(View.VISIBLE);
                    SubmitRating(et_name.getText().toString(), et_title.getText().toString(), et_comment.getText().toString(), rating,product_id,position,dialog,progress);
                } else {
                    Toast.makeText(context, ""+context.getResources().getString(R.string.please_fill_all_details), Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }//AddCustomerRatingClose
    public void SubmitRating(final String name, final String title, final String comment, final float rating, final String product_id, final int position, final Dialog dialog, final ProgressBar progress) {

       // p_bar.setVisibility(View.VISIBLE);
        final int i=(int)rating;
        //Toast.makeText(context, ""+i, Toast.LENGTH_SHORT).show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.ADD_RATING,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                  //      p_bar.setVisibility(View.GONE);
                        progress.setVisibility(View.GONE);
                        try {


                            Log.i("productttt",""+response.toString());
                            JSONObject Jsonobj = new JSONObject(response);

                            String status=Jsonobj.getString("status");

                            if (status.equals("1"))
                            {
                                dialog.dismiss();
                               // orderDetailPojoList.get(position).setRating(String.valueOf(i));
                                notifyDataSetChanged();
                                Toast.makeText(context, ""+Jsonobj.getString("message"), Toast.LENGTH_SHORT).show();

                            }
                            else
                            {
                                dialog.dismiss();
                                notifyDataSetChanged();
                                Toast.makeText(context, ""+Jsonobj.getString("message"), Toast.LENGTH_SHORT).show();

                            }


                        } catch (JSONException e) {
                            dialog.dismiss();
                            e.printStackTrace();
                            Log.i("jsonException",e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        progress.setVisibility(View.GONE);
                       // p_bar.setVisibility(View.GONE);
                        // Toast.makeText(ProductListActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("language",new SharedPrefManager(context).getLanguage());
                params.put("user_id",new SharedPrefManager(context).GetCustomerId());
                params.put("product_id",product_id);
                params.put("rating",""+i);
                params.put("title", title);
                params.put("comment", comment);
                params.put("name", name);


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
