package com.cube9.gmarket.Home.Adapter;


import android.app.Activity;
import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.cube9.gmarket.HelperClass.SharedPrefManager;
import com.cube9.gmarket.Home.ModelClass.AllProductsModel;
import com.cube9.gmarket.Home.ModelClass.productinfo;
import com.cube9.gmarket.Login.Activity.LoginDetailsActivity;
import com.cube9.gmarket.Products.Activity.ProductDetailsNewActivity;
import com.cube9.gmarket.R;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;

public class ItemAdapter extends PagedListAdapter<productinfo, ItemAdapter.ItemViewHolder> {

private Context mCtx;

     RecyclerViewClickListener itemListener;

        public ItemAdapter(Context mCtx,RecyclerViewClickListener itemListener) {
        super(DIFF_CALLBACK);
        this.mCtx = mCtx;
            this.itemListener = itemListener;
        }

@NonNull
@Override
public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.row_latest_products, parent, false);
        return new ItemViewHolder(view);
        }

@Override
public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
    final  productinfo item;   item = getItem(position);

        if (item != null) {
        holder.tv_product_name.setText(item.product_name);

            holder.tv_discount.setText(item.product_discount);
            holder.tv_discounted_pice.setText(item.product_price);


            if (item.product_special_price.equals("null"))
            {
                holder.tv_discounted_pice.setVisibility(View.GONE);
            }
            if (item.product_discount.equals("null"))
            {
                holder.tv_discount.setVisibility(View.GONE);
            }
            if (!item.product_price.equals("null"))
            {

                //holder.tv_original_price.setText("FCFA "+new DecimalFormat("##.##").format(Double.parseDouble(items.getProduct_price())));
                holder.tv_original_price.setText(item.product_price+" FCFA");
            }
            if ( !item.product_special_price.equals("null"))
            {
                // holder.tv_discounted_pice.setText("FCFA "+new DecimalFormat("##.##").format(Double.parseDouble(items.getProduct_special_price())));
                holder.tv_discounted_pice.setText(item.product_special_price+" FCFA");
                holder.tv_original_price.setPaintFlags( holder.tv_original_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            }



            //set drawable to imageview
            if (!item.product_image.isEmpty()) {
                Picasso.with(mCtx)
                        .load(item.product_image)
                        .noFade()
                        .resize(150, 150)
                        .placeholder(mCtx.getResources().getDrawable(R.drawable.ic_g_market_logo))
                        .into(holder.iv_product_image);
            }



           holder.ll_product.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(mCtx, ProductDetailsNewActivity.class);
                    i.putExtra("product_id",""+item.product_id);
                    i.putExtra("product_name",item.product_name);
                    i.putExtra("category_id","");
                    mCtx.startActivity(i);

                }
            });



        }else{
        Toast.makeText(mCtx, "Item is null", Toast.LENGTH_LONG).show();
        }
        }

private static DiffUtil.ItemCallback<productinfo> DIFF_CALLBACK =
        new DiffUtil.ItemCallback<productinfo>() {
@Override
public boolean areItemsTheSame(productinfo oldItem, productinfo newItem) {
        return oldItem.product_id == newItem.product_id;
        }

@Override
public boolean areContentsTheSame(productinfo oldItem, productinfo newItem) {
        return oldItem.equals(newItem);
        }
        };
    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }
class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    ImageView iv_product_image;
    Button btn_add_to_cart;
    TextView tv_product_name,tv_discounted_pice,tv_original_price,tv_discount,tv_rating,tv_no_cost_emi;
    SparkButton spark_button;
    LinearLayout ll_product;
    RatingBar ratingBar;

    public ItemViewHolder(View view) {
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
   //     ll_product.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
       // itemListener.recyclerViewListClicked(v, this.getPosition());

    }
}
}
